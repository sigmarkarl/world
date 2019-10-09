package org.simmi.javafasta.unsigned;

public class SW {
	public float default_sw_alignment() {
		float[][] m = new float[4][4];
		m[0][0] = 1;
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = 0;
		m[1][0] = 0;
		m[1][1] = 1;
		m[1][2] = 0;
		m[1][3] = 0;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = 1;
		m[2][3] = 0;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;
		return sw_alignment( m, -11.0f, -1.0f, null, null, null, null, null, null, null, null );
	}
	
	final double SMALL = 1e-10;
	public float sw_alignment(
			float[][] m,		/* Scoring matrix, d matrix from lecture */
			float fg,			/* Penalty for first gap */
			float ng,			/* Penalty for each of subsequent gaps */
			float[][] S,		/* match scores, D matrix from lecture */
			int[] firsti,		/* Offset in query sequence */
			int[] firstj,		/* Offset in database sequence */
			char[] qal,			/* Query alignment */
			char[] dal, 		/* DB algnemnt */
			int[] alen,			/* alignemnt length */
			char[] qseq,		/* Query sequence */
			char[] dseq			/* Database sequence */
			)
		{
			int l1 = qseq.length;
			int l2 = dseq.length;
			
			int acur = 0;
			
			float[][] P;		/* P matrix */	
			float[][] Q;		/* Q matrix */
			int     i, j;
			float   temp1, temp2, temp;
			float   sij, pij, qij;
			float[]  Si, Sp; 	/* pointers to rows in S matrix */
			float[]  Pi; 		/* pointers to row in P matrix */
			float[]  Qi, Qp; 	/*  pointers to row in Q matrix */
			float[]  Mi; 		/*  pointers to row in m matrix */
			int[][]	eij;
			int	e;
			int[]	Ei;
			float	score;
			int	keep_going;
			int	best;
			int	k;

			score = 0;
			firsti[0] = -1;
			firstj[0] = -1;

			eij = new int[l1+1][l2+1]; //imatrix(0, l1, 0, l2);
			P = new float[l1+1][l2+1]; //fmatrix(0, l1, 0, l2);
			Q = new float[l1+1][l2+1]; //fmatrix(0, l1, 0, l2);
			S[l1][l2] = 0.0f;
			P[l1][l2] = 0.0f;
			Q[l1][l2] = 0.0f;

			for (j = l2 - 1; j >= 0; j--) {
				sij = S[l1][j + 1];
				S[l1][j] = sij;
				P[l1][j] = sij;  /* Here one can penalize ends and set P[l1][j] = sij - fg */
				Q[l1][j] = sij;  /* Here one can penalize ends and set Q[l1][j] = sij - fg */
			}

			for (j = l1 - 1; j >= 0; j--) {
				sij = S[j+1][l2];
				S[j][l2] = sij;
				P[j][l2] = sij;  /* Here one can penalize ends and set P[j][l2] = sij - fg */
				Q[j][l2] = sij;	 /* Here one can penalize ends and set Q[j][l2] = sij - fg */
			}

			/* Loop over Query sequence */
			for (i = l1 - 1; i >= 0; i--) {

				/* indirect array access to speed up code */
				/* Mi, Si, Pi etc points to row i in the corresponding matrix */

				Mi = m[i];
				Si = S[i];
				Pi = P[i];
				Qi = Q[i];

				Sp = S[i + 1]; /* pointer to the i+1 row in S */
				Qp = Q[i + 1];

				Ei = eij[i];

				/* eij is the backtrack direction matrix
					eij = 0 stop back tracking
					eij = 1 match
					eij = 2 gap-opening database
					eij = 3 gap-extension database
					eij = 4 gap-opening query
					eij = 5 gap-extension query
				*/

				for (j = l2 - 1; j >= 0; j--) {

		/* Fill in the missing code XXXXX */

					/* Try match state */

					sij = Sp[j + 1] + Mi[j]; /* sij = S[i+1][j+1] + m[i][j] */

					/* Try Gap in Database sequence (insertion in query sequence) */

					temp1 = Qp[j] - ng; /* Gap extension temp1 = Q[i+1][j] - ng */
		            temp2 = Sp[j] - fg; /* Gap opening   temp2 = S[i+1][j] - fg */

					if ( temp1 > temp2 ) { /* extension best */
						qij = temp1;
						e = 3;
					} else { /* gap opening best */
						qij = temp2; 
						e = 2;
					}

					/* Select if match or gap scores best */

					if ( qij > sij ) 
						sij = qij; /* Gap is best */
					else 
						e = 1; /* Match best */

					/* Try Gap in query sequence (insertion in database sequence) */

					temp1 = Pi[j+1] - ng; /* Gap extension */
					temp2 = Si[j+1] - fg; /* Gap opening */

					if ( temp1 > temp2 ) { /* extension best */

						pij = temp1;

						if ( temp1 > sij ) { /* extension best */
							sij = temp1;
							e = 5;
						}

					} else { /* gap opening best */

						pij = temp2;

						if ( temp2 > sij ) { /* gap opening best */
							sij = temp2;
							e = 4;
		                }

					}

					if (sij > score) {
						score = sij;
						firsti[0] = i;
						firstj[0] = j;
					}

					if ( sij <= 0 ) {
						sij = 0.0f;
						e = 0;
					}

					Si[j] = sij; /* S[i][j] = sij */
					Qi[j] = qij; /* Q[i][j] = qij */
					Pi[j] = pij; /* P[i][j] = pij */
					Ei[j] = e;   /* eij[i][j] = e */
				}
			}

			/* This is some code you might use to debug the program *

			if ( p_show != 0 ) {
		                System.out.printf( "# S-matrix\n" );

		                for ( j=0; j<l2; j++ )
		                	System.out.printf( "%4c ", dseq[j] );
		                System.out.printf( "\n" );

		                for ( i=0; i<=l1; i++ ) {
		                		System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
		                        for ( j=0; j<=l2; j++ )
		                        	System.out.printf( " %5.2f", S[i][j] );
		                        System.out.printf( "\n" );
		                }

		                System.out.printf( "# Q-matrix\n" );

		                for ( j=0; j<l2; j++ )
		                	System.out.printf( "%4c ", dseq[j] );
		                System.out.printf( "\n" );

		                for ( i=0; i<=l1; i++ ) {
		                	System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
		                        for ( j=0; j<=l2; j++ )
		                        	System.out.printf( " %5.2f", Q[i][j] );
		                        System.out.printf( "\n" );
		                }
		               
		                System.out.printf( "# P-matrix\n" );
		                
		                for ( j=0; j<l2; j++ )
		                	System.out.printf( "%4c ", dseq[j] );
		                System.out.printf( "\n" );
		                
		                for ( i=0; i<=l1; i++ ) {
		                		System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
		                        for ( j=0; j<=l2; j++ )
		                        	System.out.printf( " %5.2f", P[i][j] );
		                        System.out.printf( "\n" );
		                }
		                
		                System.out.printf( "# Eij-matrix\n" );
		                
		                for ( j=0; j<l2; j++ )
		                	System.out.printf( "%4c ", dseq[j] );
		                System.out.printf( "\n" );
		                
		                for ( i=0; i<=l1; i++ ) {
		                		System.out.printf( "%c", ( i<l1 ? qseq[i] : ' ' ));
		                        for ( j=0; j<=l2; j++ )
		                        	System.out.printf( " %2i", eij[i][j] );
		                        System.out.printf( "\n" );
		                }
			}*/

			//fmatrix_free(P, 0, l1, 0, l2);
			//fmatrix_free(Q, 0, l1, 0, l2);

			/* Do back tracking */

			if (firsti[0] < 0 || firstj[0] < 0 ) 
				System.out.printf( "No alignment found. Exit\n" );

			alen[acur] = 0;

			i = firsti[0];
			j = firstj[0];
			qal[alen[acur]] = qseq[i];
			dal[alen[acur]] = dseq[j];
			i++;
			j++;

			alen[acur]++;

			keep_going = 1;

			while( (i < l1 ) && (j < l2 ) && keep_going != 0 ) {

				if ( eij[i][j] == 0 ) {
					keep_going = 0;
				} else if ( eij[i][j] == 1 ) { /* Match */
					qal[alen[acur]] = qseq[i];
					dal[alen[acur]] = dseq[j];
					i++;
					j++;
					alen[acur]++;
				} else if ( eij[i][j] == 4 ) { /* gap opening in Query */
					qal[alen[acur]] = '-';
					dal[alen[acur]] = dseq[j];
					j++;
					alen[acur]++;
				} else if (  eij[i][j] == 5 ) { /* gap extension in Query */
					best = j+2;
					Si = S[i];
					temp = Si[best] - fg - ( best-j-1) * ng - Si[j];
					while ( temp*temp > SMALL ) {
						best++;
						temp = Si[best] - fg - ( best-j-1) * ng - Si[j];
					}

					for ( k=j; k<best; k++ ) {
						qal[alen[acur]] = '-';
						dal[alen[acur]] = dseq[j];
						j++;
						alen[acur]++;
					}
				} else if ( eij[i][j] == 2 ) { /* gap opening in Database */
					qal[alen[acur]] = qseq[i];
					dal[alen[acur]] = '-';
					i++;
					alen[acur]++;
				} else if (  eij[i][j] == 3 ) { /* gap extension in Database */

					/* Write code for gap extension in database inspired by code for gap extension 
					in query 25 linies up */

					best = i+2;
					//Si = S[i];
					temp = S[best][j] - fg - (best-i-1) * ng - S[i][j];

					while ( temp*temp > SMALL ) {
						best++;
						//if( best < S.length && j < S[best].length )
						temp = S[best][j] - fg - (best-i-1) * ng - S[i][j];
					}
					                              
					for ( k=i; k<best; k++ ) {
						qal[alen[acur]] = qseq[i];
						dal[alen[acur]] = '-';
						i++;
						alen[acur]++;
					}
				}
			}

		/* NOW YOU ARE DONE */

			qal[alen[acur]] = 0;
			dal[alen[acur]] = 0;

			//imatrix_free( eij, 0, l1, 0, l2);

			return( score );
		}
}
