package org.simmi.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cog {
	public static Map<Character,String>	charcog = new HashMap<Character,String>();
	public static Map<String,Character>	cogchar = new HashMap<String,Character>();
	public static Map<String,Set<Character>> coggroups = new HashMap<String,Set<Character>>();
	static {
		coggroups.put( "CELLULAR PROCESSES AND SIGNALING", new HashSet<Character>( Arrays.asList( new Character[] {'D','M','N','O','T','U','V','W','Y','Z'} ) ) );
		coggroups.put( "INFORMATION STORAGE AND PROCESSING", new HashSet<Character>( Arrays.asList( new Character[] {'A','B','J','K','L'} ) ) );
		coggroups.put( "METABOLISM", new HashSet<Character>( Arrays.asList( new Character[] {'C','E','F','G','H','I','P','Q'} ) ) );
		coggroups.put( "POORLY CHARACTERIZED", new HashSet<Character>( Arrays.asList( new Character[] {'R','S'} ) ) );
		
		/*
		CELLULAR PROCESSES AND SIGNALING
		[D] Cell cycle control, cell division, chromosome partitioning
		[M] Cell wall/membrane/envelope biogenesis
		[N] Cell motility
		[O] Post-translational modification, protein turnover, and chaperones
		[T] Signal transduction mechanisms
		[U] Intracellular trafficking, secretion, and vesicular transport
		[V] Defense mechanisms
		[W] Extracellular structures
		[Y] Nuclear structure
		[Z] Cytoskeleton
		INFORMATION STORAGE AND PROCESSING
		[A] RNA processing and modification
		[B] Chromatin structure and dynamics
		[J] Translation, ribosomal structure and biogenesis
		[K] Transcription
		[L] Replication, recombination and repair
		METABOLISM
		[C] Energy production and conversion
		[E] Amino acid transport and metabolism
		[F] Nucleotide transport and metabolism
		[G] Carbohydrate transport and metabolism
		[H] Coenzyme transport and metabolism
		[I] Lipid transport and metabolism
		[P] Inorganic ion transport and metabolism
		[Q] Secondary metabolites biosynthesis, transport, and catabolism
		POORLY CHARACTERIZED
		[R] General function prediction only
		[S] Function unknown*/
		charcog.put('D', "Cell cycle control, cell division, chromosome partitioning" );
		charcog.put('M', "Cell wall/membrane/envelope biogenesis" );
		charcog.put('N', "Cell motility" );
		charcog.put('O', "Post-translational modification, protein turnover, and chaperones" );
		charcog.put('T', "Signal transduction mechanisms" );
		charcog.put('U', "Intracellular trafficking, secretion, and vesicular transport" );
		charcog.put('V', "Defense mechanisms" );
		charcog.put('W', "Extracellular structures" );
		charcog.put('Y', "Nuclear structure" );
		charcog.put('Z', "Cytoskeleton" );
		//INFORMATION STORAGE AND PROCESSING
		charcog.put('A', "RNA processing and modification" );
		charcog.put('B', "Chromatin structure and dynamics" );
		charcog.put('J', "Translation, ribosomal structure and biogenesis" );
		charcog.put('K', "Transcription" );
		charcog.put('L', "Replication, recombination and repair" );
		//METABOLISM
		charcog.put('C', "Energy production and conversion" );
		charcog.put('E', "Amino acid transport and metabolism" );
		charcog.put('F', "Nucleotide transport and metabolism" );
		charcog.put('G', "Carbohydrate transport and metabolism" );
		charcog.put('H', "Coenzyme transport and metabolism" );
		charcog.put('I', "Lipid transport and metabolism" );
		charcog.put('P', "Inorganic ion transport and metabolism" );
		charcog.put('Q', "Secondary metabolites biosynthesis, transport, and catabolism" );
		//POORLY CHARACTERIZED
		charcog.put('R', "General function prediction only" );
		charcog.put('S', "Function unknown" );
		
		for( Character c : charcog.keySet() ) {
			cogchar.put( charcog.get(c), c );
		}
		
		cogchar.put( "Cell envelope biogenesis, outer membrane", 'M' );
		cogchar.put( "Cell wall", 'M' );
		cogchar.put( "Cell motility and secretion", 'N' );	
		cogchar.put( "Cell division and chromosome partitioning", 'D' );
		cogchar.put( "DNA replication, recombination and repair", 'L' );
		cogchar.put( "DNA replication, recombination, and repair", 'L' );
		cogchar.put( "Replication, recombination, and repair", 'L' );
		cogchar.put( "Posttranslational modification, protein turnover, chaperones", 'O' );
		cogchar.put( "Lipid metabolism", 'I' );
		cogchar.put( "Intracellular trafficking and secretion", 'U' );
		cogchar.put( "Coenzyme metabolism", 'H' );
		cogchar.put( "Secondary metabolites biosynthesis, transport and catabolism", 'Q' );
		//cogchar.put( "acetolactate synthase, pyruvate dehydrogenase (cytochrome), glyoxylate carboligase, phosphonopyruvate decarboxylase", 'Æ' );
	}
	
	public Cog( String id, Character symbol, String name, String annotation ) {
		this.id = id;
		this.symbol = symbol;
		this.name = name;
		if( annotation != null ) {
			int i = annotation.indexOf(',');
			if( i != -1 && i <= 5 ) {
				this.genesymbol = annotation.substring(0,i);
				this.annotation = annotation.substring(i+1);
			} else this.annotation = annotation;
		}
	}
	
	public String	id;
	public Character symbol;
	public String	annotation;
	public String	genesymbol;
	public String	name;
}
