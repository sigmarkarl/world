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

bool isSorted( char* buffer, long long* ibuffer, long long length ) {
	bool ret = true;
	for( int i = 0; i < length; i++ ) {
		if( strcmpsim( &buffer[ibuffer[i]], &buffer[ibuffer[i+1]] ) != 0 ) {
			return false;
		}
	}
	return true;
}

char* buffer;
int simcomp( const void* b1, const void* b2 ) {
	return strcmpsim( &buffer[*(long long*)b1], &buffer[*(long long*)b2] );
}

int main(int argc, char **argv) {
	/*FILE* f = fopen("/vg454flx/nr/nr.fasta","r");

	FILE* nf = fopen("/vg454flx/nr/nr.names.txt", "w");
	FILE* df = fopen("/vg454flx/nr/nr.data.txt", "w");

	std::vector<long long>	nameidx;
	std::vector<long long>	dataidx;

	printf("ready\n");
	bool inname = false;
	long long nametotal = 0;
	long long datatotal = 0;
	char* buffer = new char[1000000];
	int r = fread( buffer, 1, 1000000, f );
	while( r > 0 ) {
		int lastwrite = 0;
		int i = 0;
		for( ; i < r; i++ ) {
			if( buffer[i] == '>' ) {
				inname = true;
				lastwrite = i+1;
				if( dataidx.size() > 0 ) fputc( 0, df );
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

	FILE* ni = fopen("/vg454flx/nr/nr.nameidx", "wb");
	for( unsigned int i = 0; i < nameidx.size(); i++ ) {
		fwrite( &nameidx[i], 8, 1, ni );
	}
	fclose( ni );
	FILE* di = fopen("/vg454flx/nr/nr.dataidx", "wb");
	for( unsigned int i = 0; i < dataidx.size(); i++ ) {
		fwrite( &dataidx[i], 8, 1, di );
	}
	fclose( di );*/

	FILE* df = fopen("/vg454flx/nr/nr.data.txt", "r");
	fseek( df, 0, SEEK_END );
	long long size = ftello64( df );
	fseek( df, 0, SEEK_SET );

	//free( buffer );
	buffer = new char[size+1];
	fread( buffer, 1, size, df );
	fclose( df );
	buffer[size] = 0;

	/*FILE* idf = fopen("/vg454flx/nr/nr.idx", "w");
	for( long long ll = 0; ll < size; ll++ ) {
		fwrite( &ll, 8, 1, idf );
	}
	fclose( idf );*/

	long long bufsiz = 3000000000L;
	long long bufhalf = bufsiz/2;

	printf( "reading idx\n" );
	long long* idxbuf = new long long[ bufsiz ];
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
	fclose( idf );

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
