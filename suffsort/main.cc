/*
 * main.cc
 *
 *  Created on: Feb 13, 2013
 *      Author: root
 */

#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <cstring>
#include <vector>
#include <pthread.h>

using namespace std;

const char* buff;
static int comp( const void *i1, const void *i2 ) {
	return strcmp( &buff[*((int*)i1)], &buff[*((int*)i2)] );
}

void *isSrt(void *threadid) {
   int tid;
   tid = (long)threadid;
   printf("Hello World! It's me, thread #%d!\n", tid);
   pthread_exit(NULL);
}

#define NUM_THREADS 4
pthread_t threads[NUM_THREADS];
bool isSortedPar( char* buffer, long long* ibuffer, long long length ) {
	int id = fork();

	bool ret = true;
	if( id == 0 ) {
		for( int i = 0; i < length/2; i++ ) {
			if( strcmp( &buffer[ibuffer[i]], &buffer[ibuffer[i+1]] ) > 0 ) {
				ret = false;
				break;
			}
		}
		exit( 0 );
	} else {
		for( int i = length/2+1; i < length-1; i++ ) {
			if( strcmp( &buffer[ibuffer[i]], &buffer[ibuffer[i+1]] ) > 0 ) {
				ret = false;
				break;
			}
		}
		wait();
	}

	return ret;
}

inline int strcmpsim( char* b1, char* b2 ) {
	int i = 0;
	while( b1[i] == b2[i] ) {
		if( b1[i] == 0 ) return 0;
		i++;
	}
	return b1[i] - b2[i];
}

char* buffer;
long long buffersize;
inline int strcmpsimdbg( char* b1, char* b2, long long off ) {
	int i = 0;
	printf("erm1 %lld %lld\n", (long long)buffer, off+i);
	while( b1[i] == b2[i] ) {
		printf("erm2 %lld\n", off+i);
		if( b1[i] == 0 ) return 0;
		i++;
		//if( off+i >= buffersize ) {
			printf("erm2 %lld\n", off+i);
			//exit(0);
		//}
	}
	return b1[i] - b2[i];
}

bool isSorted( char* buffer, long long* ibuffer, long long length ) {
	bool ret = true;
	for( int i = 0; i < length; i++ ) {
		if( strcmpsim( &buffer[ibuffer[i]], &buffer[ibuffer[i+1]] ) != 0 ) {
			return false;
		}
	}
	return true;
}

int simcomp( const void* b1, const void* b2 ) {
	return strcmpsim( &buffer[*(long long*)b1], &buffer[*(long long*)b2] );
}

int simcompdbg( const void* b1, const void* b2 ) {
	long long idx1 = *(long long*)b1;
	long long idx2 = *(long long*)b2;

	//if( idx1 >= buffersize || idx2 >= buffersize ) {
		printf( "erm %lld %lld %lld\n", idx1, idx2, buffersize );
	//	exit( 0 );
	//}
	return strcmpsimdbg( &buffer[idx1], &buffer[idx2], idx1 > idx2 ? idx1 : idx2 );
}

void first() {
	FILE* df = fopen("/vg454flx/silva111.data.txt", "r");
	fseek( df, 0, SEEK_END );
	long long size = ftello64( df );
	fseek( df, 0, SEEK_SET );

	//free( buffer );
	buffer = new char[size+1];
	fread( buffer, 1, size, df );
	fclose( df );
	buffer[size] = 0;

	FILE* idf = fopen("/vg454flx/silva111.idx", "rb");
	long long* bb = new long long[20000000];
	fread( bb, 8, 20000000, idf );
	fclose( idf );
	for( long long ll = 19999800; ll < 20000000; ll++ ) {
		long long val = bb[ll];
		char c = buffer[val+50];
		buffer[val+50] = 0;
		printf( "%s\t%lld\n", &buffer[val], val );
		buffer[val+50] = c;
	}
}

int main(int argc, char **argv) {
	first();
	return 0;

	/*FILE* f = fopen("/vg454flx/nr/nr.fasta","r");

	FILE* nf = fopen("/vg454flx/nr/nr.names.txt", "w");
	FILE* df = fopen("/vg454flx/nr/nr.data.txt", "w");*/

	FILE* f = fopen("/u0/qiime_software/silva111.fasta","r");

	FILE* nf = fopen("/vg454flx/silva111.names.txt", "w");
	FILE* df = fopen("/vg454flx/silva111.data.txt", "w");

	std::vector<long long>	nameidx;
	std::vector<long long>	dataidx;

	printf("ready\n");
	bool inname = false;
	long long nametotal = 0;
	long long datatotal = 0;
	buffer = new char[1000000];
	int r = fread( buffer, 1, 1000000, f );
	while( r > 0 ) {
		int lastwrite = 0;
		int i = 0;
		for( ; i < r; i++ ) {
			if( buffer[i] == '>' ) {
				inname = true;
				lastwrite = i+1;
				if( dataidx.size() > 0 ) fputc( '\0', df );
				dataidx.push_back( datatotal );
				nameidx.push_back( nametotal );
			} else if( buffer[i] == '\n' ) {
				int w = i-lastwrite;
				if( w > 0 ) {
					if( inname ) {
						fwrite( &buffer[lastwrite], 1, w+1, nf );
						nametotal += w+1;
					} else {
						fwrite( &buffer[lastwrite], 1, w, df );
						datatotal += w;
					}
				}
				inname = false;
				lastwrite = i+1;
			}
		}
		int w = i-lastwrite;
		if( w > 0 ) {
			if( inname ) {
				fwrite( &buffer[lastwrite], 1, w, df );
			} else {
				fwrite( &buffer[lastwrite], 1, w, df );
			}
		}
		r = fread( buffer, 1, 1000000, f );
	}
	fputc( 0, df );
	fclose( f );
	fclose( nf );
	fclose( df );
	free( buffer );

	FILE* ni = fopen("/vg454flx/silva111.nameidx", "wb");
	for( unsigned int i = 0; i < nameidx.size(); i++ ) {
		fwrite( &nameidx[i], 8, 1, ni );
	}
	fclose( ni );
	FILE* di = fopen("/vg454flx/silva111.dataidx", "wb");
	for( unsigned int i = 0; i < dataidx.size(); i++ ) {
		fwrite( &dataidx[i], 8, 1, di );
	}
	fclose( di );

	df = fopen("/vg454flx/silva111.data.txt", "r");
	fseek( df, 0, SEEK_END );
	long long size = ftello64( df );
	fseek( df, 0, SEEK_SET );

	//free( buffer );
	buffer = new char[size+1];
	fread( buffer, 1, size, df );
	fclose( df );
	buffer[size] = 0;

	buffersize = size;

	//FILE* idf = fopen("/vg454flx/SSURef_111_tax_silva_trunc.idx", "w");
	long long* idxbuf = new long long[ size+1 ];
	for( long long ll = 0; ll < size+1; ll++ ) {
		//fwrite( &ll, 8, 1, idf );
		idxbuf[ll] = ll;
	}
	//fclose( idf );
	printf("erme %lld\n", size);
	qsort( idxbuf, size, sizeof(long long), simcomp );
	printf("done\n");

	FILE* idf = fopen("/vg454flx/silva111.idx", "wb");
	//for( long long ll = 0; ll < size+1; ll++ ) {
	fwrite( idxbuf, 8, size+1, idf );
	//}
	fclose( idf );
	/*long long bufsiz = 3000000000L;
	long long bufhalf = bufsiz/2;

	printf( "reading idx\n" );
	long long* idxbuf = new long long[ bufsiz ];
	//FILE* idf = fopen("/vg454flx/nr/nr.idx", "rw");
	FILE* idf = fopen("/vg454flx/nr/nr.idx", "rw");

	for( int k = 0; k < 5; k++ ) {
		fseeko64( idf, 0L, SEEK_SET );
		for( int i = 0; i < 5-k; i++ ) {
			fread( idxbuf, sizeof(long long), bufsiz, idf );
			printf( "sorting\n" );
			qsort( idxbuf, bufsiz, sizeof(double), simcomp );
			printf( "stage1\n" );
			fseeko64( idf, 0L, SEEK_SET );
			fwrite( idxbuf, sizeof(long long), bufhalf, idf );
			for( long long ll = 0; ll < bufhalf; ll++ ) {
				idxbuf[ll] = idxbuf[ll+bufhalf];
			}
			fseeko64( idf, sizeof(long long)*bufhalf, SEEK_CUR );
			fread( idxbuf, sizeof(long long), bufhalf, idf );
			qsort( idxbuf, bufsiz, sizeof(double), simcomp );
			fseeko64( idf, sizeof(long long)*bufhalf, SEEK_SET );
			fwrite( idxbuf, sizeof(long long), bufhalf, idf );
			for( long long ll = 0; ll < bufhalf; ll++ ) {
				idxbuf[ll] = idxbuf[ll+bufhalf];
			}
		}
	}
	fclose( idf );*/

	/*buff = buffer;
	size = (10000000 < size ? 10000000 : size);

	printf( "fill gig\n" );
	int* ibuffer = new int[ size ];
	#pragma omp parallel for
	for( int i = 0; i < size; i++ ) {
		ibuffer[i] = i;
	}

	printf( "sorted %s\n", isSorted( buffer, ibuffer, size ) ? "true" : "false" );
	int id = fork();
	if( id == 0 ) {
		qsort( ibuffer, size/2, sizeof(int), comp );
		exit( 0 );
	} else {
		qsort( &ibuffer[size/2], size/2, sizeof(int), comp );
		wait();
	}
	qsort( &ibuffer[size/4], size/2, sizeof(int), comp );
	id = fork();
	if( id == 0 ) {
		qsort( ibuffer, size/2, sizeof(int), comp );
		exit( 0 );
	} else {
		qsort( &ibuffer[size/2], size/2, sizeof(int), comp );
		wait();
	}
	qsort( &ibuffer[size/4], size/2, sizeof(int), comp );
	printf( "sorted %s\n", isSorted( buffer, ibuffer, size ) ? "true" : "false" );

	for( int i = 0; i < (1000 < size ? 1000 : size); i++ ) {
		putchar( buffer[ibuffer[i]] );
	}
	/*std::ifstream	filestream("/u0/qiime_software/silva111.fasta");
	std::stringstream	strstream;
	strstream << filestream.rdbuf();

	std::string mystr = strstream.str();
	std::cout << mystr.substr(0, 1000);
	std::cout << std::endl;*/

	return 0;
}
