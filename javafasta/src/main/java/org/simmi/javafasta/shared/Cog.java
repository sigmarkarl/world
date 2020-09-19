package org.simmi.javafasta.shared;

import java.awt.*;
import java.util.*;

public class Cog {
	public static Map<Character,String>	charcog = new HashMap<>();
	public static Map<String,Character>	cogchar = new HashMap<>();
	public static Map<String,Set<Character>> coggroups = new HashMap<>();
	public static Map<Character,Color> charcogcol = new HashMap<>();
	static {
		coggroups.put( "CELLULAR PROCESSES AND SIGNALING", new HashSet<>( Arrays.asList( new Character[] {'D','M','N','O','T','U','V','W','Y','Z'} ) ) );
		coggroups.put( "INFORMATION STORAGE AND PROCESSING", new HashSet<>( Arrays.asList( new Character[] {'A','B','J','K','L'} ) ) );
		coggroups.put( "METABOLISM", new HashSet<>( Arrays.asList( new Character[] {'C','E','F','G','H','I','P','Q'} ) ) );
		coggroups.put( "POORLY CHARACTERIZED", new HashSet<>( Arrays.asList( new Character[] {'R','S'} ) ) );
		
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

		/*Random r = new Random();
		for( Character c : charcog.keySet() ) {
			cogchar.put( charcog.get(c), c );
			charcogcol.put( c, new Color(r.nextFloat(),r.nextFloat(),r.nextFloat()) );
		}*/

		charcogcol.put('D', Color.decode("#FCFCDC"));
		charcogcol.put('M', Color.decode("#ECFCAC"));
		charcogcol.put('N', Color.decode("#DCFCAC"));
		charcogcol.put('O', Color.decode("#DCFCAC"));
		charcogcol.put('T', Color.decode("#FCFCAC"));
		charcogcol.put('U', Color.decode("#ACFCAC"));
		charcogcol.put('V', Color.decode("#FCFCBC"));
		charcogcol.put('W', Color.decode("#BCFCAC"));
		charcogcol.put('Y', Color.decode("#FCFCCC"));
		charcogcol.put('Z', Color.decode("#CCFCAC"));
		charcogcol.put('A', Color.decode("#FCDCFC"));
		charcogcol.put('B', Color.decode("#FCDCCC"));
		charcogcol.put('J', Color.decode("#FCCCFC"));
		charcogcol.put('K', Color.decode("#FCDCEC"));
		charcogcol.put('L', Color.decode("#FCDCDC"));
		charcogcol.put('C', Color.decode("#BCFCFC"));
		charcogcol.put('E', Color.decode("#DCFCFC"));
		charcogcol.put('G', Color.decode("#CCFCFC"));
		charcogcol.put('H', Color.decode("#DCDCFC"));
		charcogcol.put('I', Color.decode("#DCCCFC"));
		charcogcol.put('P', Color.decode("#CCCCFC"));
		charcogcol.put('Q', Color.decode("#BCCCFC"));
		charcogcol.put('R', Color.decode("#E0E0E0"));
		charcogcol.put('S', Color.decode("#CCCCCC"));
		
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
		cogchar.put( "acyl-carrier-protein", 'I' );
		cogchar.put( "acyl-carrier protein", 'I' );
		cogchar.put( "acyl carrier protein", 'I' );
		cogchar.put( "3-hydroxymyristoyl", 'M' );
		cogchar.put( "Signal transduction", 'T');
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