package org.simmi.javafasta.shared;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sequence extends FastaSequence implements Comparable<Sequence> {
	/*public static int						max = 0;
	public static int						min = 0;s
	
	public static ArrayList<Sequence>		lseq = new ArrayList<Sequence>() {
		private static final long serialVersionUID = 1L;

		public boolean add( Sequence seq ) {
			seq.index = Sequence.lseq.size();
			return super.add( Oseq );
		}
	};
	public static Map<String,Sequence>		mseq = new HashMap<String,Sequence>();
	public static ArrayList<Annotation>	lann = new ArrayList<Annotation>();
	public static Map<String,Annotation>	mann = new HashMap<String,Annotation>();*/
	
	public static Map<Character, Color> aacolor = new HashMap<>();
	static Map<Character, Character> 	sidechainpolarity = new HashMap<>();
	static Map<Character, Integer> 		sidechaincharge = new HashMap<>();
	static Map<Character, Double> 		hydropathyindex = new HashMap<>();
	static Map<Character, Double> 		aamass = new HashMap<>();
	static Map<Character, Double> 		isoelectricpoint = new HashMap<>();

	public static Color[] colorCodes = new Color[9];
	private boolean			plasmid;
	// abundance
	// aliphatic - aromatic
	// size
	// sortcoeff

	public static List<Erm> uff = new ArrayList<>();
	public static List<Erm> uff2 = new ArrayList<>();
	public static List<Erm> uff3 = new ArrayList<>();
	public static List<Erm> mass = new ArrayList<>();
	public static List<Erm> isoel = new ArrayList<>();
	
	public static Map<String,Character>				amimap = new HashMap<>();
	public static Map<String,String>				revcom = new HashMap<>();
	public static Map<Character,Character>	rc = new HashMap<>();
	static {
		amimap.put("TTT",'F');
		amimap.put("TTC",'F');
		amimap.put("TTA",'L');
		amimap.put("TTG",'L');
		amimap.put("TCT",'S');
		amimap.put("TCC",'S');
		amimap.put("TCA",'S');
		amimap.put("TCG",'S');
		amimap.put("TAT",'Y');
		amimap.put("TAC",'Y');
		amimap.put("TAA",'X');
		amimap.put("TAG",'X');
		amimap.put("TGT",'C');
		amimap.put("TGC",'C');
		amimap.put("TGA",'X');
		amimap.put("TGG",'W');
		amimap.put("CTT",'L');
		amimap.put("CTC",'L');
		amimap.put("CTA",'L');
		amimap.put("CTG",'L');
		amimap.put("CCT",'P');
		amimap.put("CCC",'P');
		amimap.put("CCA",'P');
		amimap.put("CCG",'P');
		amimap.put("CAT",'H');
		amimap.put("CAC",'H');
		amimap.put("CAA",'Q');
		amimap.put("CAG",'Q');
		amimap.put("CGT",'R');
		amimap.put("CGC",'R');
		amimap.put("CGA",'R');
		amimap.put("CGG",'R');
		amimap.put("ATT",'I');
		amimap.put("ATC",'I');
		amimap.put("ATA",'I');
		amimap.put("ATG",'M');
		amimap.put("ACT",'T');
		amimap.put("ACC",'T');
		amimap.put("ACA",'T');
		amimap.put("ACG",'T');
		amimap.put("AAT",'N');
		amimap.put("AAC",'N');
		amimap.put("AAA",'K');
		amimap.put("AAG",'K');
		amimap.put("AGT",'S');
		amimap.put("AGC",'S');
		amimap.put("AGA",'R');
		amimap.put("AGG",'R');
		amimap.put("GTT",'V');
		amimap.put("GTC",'V');
		amimap.put("GTA",'V');
		amimap.put("GTG",'V');
		amimap.put("GCT",'A');
		amimap.put("GCC",'A');
		amimap.put("GCA",'A');
		amimap.put("GCG",'A');
		amimap.put("GAT",'D');
		amimap.put("GAC",'D');
		amimap.put("GAA",'E');
		amimap.put("GAG",'E');
		amimap.put("GGT",'G');
		amimap.put("GGC",'G');
		amimap.put("GGA",'G');
		amimap.put("GGG",'G');
		
		revcom.put("TTT","AAA");
		revcom.put("TTC","GAA");
		revcom.put("TTA","TAA");
		revcom.put("TTG","CAA");
		revcom.put("TCT","AGA");
		revcom.put("TCC","GGA");
		revcom.put("TCA","TGA");
		revcom.put("TCG","CGA");
		revcom.put("TAT","ATA");
		revcom.put("TAC","GTA");
		revcom.put("TAA","TTA");
		revcom.put("TAG","CTA");
		revcom.put("TGT","ACA");
		revcom.put("TGC","GCA");
		revcom.put("TGA","TCA");
		revcom.put("TGG","CCA");
		revcom.put("CTT","AAG");
		revcom.put("CTC","GAG");
		revcom.put("CTA","TAG");
		revcom.put("CTG","CAG");
		revcom.put("CCT","AGG");
		revcom.put("CCC","GGG");
		revcom.put("CCA","TGG");
		revcom.put("CCG","CGG");
		revcom.put("CAT","ATG");
		revcom.put("CAC","GTG");
		revcom.put("CAA","TTG");
		revcom.put("CAG","CTG");
		revcom.put("CGT","ACG");
		revcom.put("CGC","GCG");
		revcom.put("CGA","TCG");
		revcom.put("CGG","CCG");
		revcom.put("ATT","AAT");
		revcom.put("ATC","GAT");
		revcom.put("ATA","TAT");
		revcom.put("ATG","CAT");
		revcom.put("ACT","AGT");
		revcom.put("ACC","GGT");
		revcom.put("ACA","TGT");
		revcom.put("ACG","CGT");
		revcom.put("AAT","ATT");
		revcom.put("AAC","GTT");
		revcom.put("AAA","TTT");
		revcom.put("AAG","CTT");
		revcom.put("AGT","ACT");
		revcom.put("AGC","GCT");
		revcom.put("AGA","TCT");
		revcom.put("AGG","CCT");
		revcom.put("GTT","AAC");
		revcom.put("GTC","GAC");
		revcom.put("GTA","TAC");
		revcom.put("GTG","CAC");
		revcom.put("GCT","AGC");
		revcom.put("GCC","GGC");
		revcom.put("GCA","TGC");
		revcom.put("GCG","CGC");
		revcom.put("GAT","ATC");
		revcom.put("GAC","GTC");
		revcom.put("GAA","TTC");
		revcom.put("GAG","CTC");
		revcom.put("GGT","ACC");
		revcom.put("GGC","GCC");
		revcom.put("GGA","TCC");
		revcom.put("GGG","CCC");
		
		rc.put('A', 'T');
		rc.put('C', 'G');
		rc.put('G', 'C');
		rc.put('T', 'A');
		rc.put('N', 'N');
		rc.put('X', 'X');
		rc.put('a', 't');
		rc.put('c', 'g');
		rc.put('g', 'c');
		rc.put('t', 'a');
		rc.put('n', 'n');
		rc.put('x', 'x');
		rc.put('-', '-');
		rc.put(' ', ' ');
		
		aacolor.put('A', new Color(0.0f,0.0f,1.0f));
		aacolor.put('R', new Color(0.0f,1.0f,1.0f));
		aacolor.put('N', new Color(0.0f,1.0f,0.0f));
		aacolor.put('D', new Color(1.0f,1.0f,0.0f));
		aacolor.put('C', new Color(1.0f,0.0f,0.0f));
		aacolor.put('E', new Color(1.0f,0.0f,1.0f));
		aacolor.put('Q', new Color(0.5f,0.5f,0.1f));
		aacolor.put('G', new Color(0.5f,1.0f,1.0f));
		aacolor.put('H', new Color(0.5f,1.0f,0.5f));
		aacolor.put('I', new Color(1.0f,1.0f,0.5f));
		aacolor.put('L', new Color(1.0f,0.5f,0.5f));
		aacolor.put('K', new Color(1.0f,0.5f,1.0f));
		aacolor.put('M', new Color(0.0f,1.0f,0.5f));
		aacolor.put('F', new Color(0.0f,0.5f,1.0f));
		aacolor.put('P', new Color(1.0f,0.5f,0.0f));
		aacolor.put('S', new Color(0.5f,1.0f,0.0f));
		aacolor.put('T', new Color(1.0f,0.0f,0.5f));
		aacolor.put('W', new Color(0.5f,0.0f,1.0f));
		aacolor.put('Y', new Color(0.8f,0.0f,0.8f));
		aacolor.put('V', new Color(0.8f,0.8f,0.0f));
		
		sidechainpolarity.put('A', 'n');
		sidechainpolarity.put('R', 'p');
		sidechainpolarity.put('N', 'p');
		sidechainpolarity.put('D', 'p');
		sidechainpolarity.put('C', 'n');
		sidechainpolarity.put('E', 'p');
		sidechainpolarity.put('Q', 'p');
		sidechainpolarity.put('G', 'n');
		sidechainpolarity.put('H', 'p');
		sidechainpolarity.put('I', 'n');
		sidechainpolarity.put('L', 'n');
		sidechainpolarity.put('K', 'p');
		sidechainpolarity.put('M', 'n');
		sidechainpolarity.put('F', 'n');
		sidechainpolarity.put('P', 'n');
		sidechainpolarity.put('S', 'p');
		sidechainpolarity.put('T', 'p');
		sidechainpolarity.put('W', 'n');
		sidechainpolarity.put('Y', 'p');
		sidechainpolarity.put('V', 'n');

		sidechaincharge.put('A', 0);
		sidechaincharge.put('R', 1);
		sidechaincharge.put('N', 0);
		sidechaincharge.put('D', -1);
		sidechaincharge.put('C', 0);
		sidechaincharge.put('E', -1);
		sidechaincharge.put('Q', 0);
		sidechaincharge.put('G', 0);
		sidechaincharge.put('H', 0);
		sidechaincharge.put('I', 0);
		sidechaincharge.put('L', 0);
		sidechaincharge.put('K', 1);
		sidechaincharge.put('M', 0);
		sidechaincharge.put('F', 0);
		sidechaincharge.put('P', 0);
		sidechaincharge.put('S', 0);
		sidechaincharge.put('T', 0);
		sidechaincharge.put('W', 0);
		sidechaincharge.put('Y', 0);
		sidechaincharge.put('V', 0);

		hydropathyindex.put('A', 1.8);
		hydropathyindex.put('R', -4.5);
		hydropathyindex.put('N', -3.5);
		hydropathyindex.put('D', -3.5);
		hydropathyindex.put('C', 2.5);
		hydropathyindex.put('E', -3.5);
		hydropathyindex.put('Q', -3.5);
		hydropathyindex.put('G', -0.4);
		hydropathyindex.put('H', -3.2);
		hydropathyindex.put('I', 4.5);
		hydropathyindex.put('L', 3.8);
		hydropathyindex.put('K', -3.9);
		hydropathyindex.put('M', 1.9);
		hydropathyindex.put('F', 2.8);
		hydropathyindex.put('P', -1.6);
		hydropathyindex.put('S', -0.8);
		hydropathyindex.put('T', -0.7);
		hydropathyindex.put('W', -0.9);
		hydropathyindex.put('Y', -1.3);
		hydropathyindex.put('V', 4.2);

		aamass.put('A', 89.09404);
		aamass.put('C', 121.15404);
		aamass.put('D', 133.10384);
		aamass.put('E', 147.13074);
		aamass.put('F', 165.19184);
		aamass.put('G', 75.06714);
		aamass.put('H', 155.15634);
		aamass.put('I', 131.17464);
		aamass.put('K', 146.18934);
		aamass.put('L', 131.17464);
		aamass.put('M', 149.20784);
		aamass.put('N', 132.11904);
		aamass.put('O', 100.0);
		aamass.put('P', 115.13194);
		aamass.put('Q', 146.14594);
		aamass.put('R', 174.20274);
		aamass.put('S', 105.09344);
		aamass.put('T', 119.12034);
		aamass.put('U', 168.053);
		aamass.put('V', 117.14784);
		aamass.put('W', 204.22844);
		aamass.put('Y', 181.19124);

		isoelectricpoint.put('A', 6.01);
		isoelectricpoint.put('C', 5.05);
		isoelectricpoint.put('D', 2.85);
		isoelectricpoint.put('E', 3.15);
		isoelectricpoint.put('F', 5.49);
		isoelectricpoint.put('G', 6.06);
		isoelectricpoint.put('H', 7.6);
		isoelectricpoint.put('I', 6.05);
		isoelectricpoint.put('K', 9.6);
		isoelectricpoint.put('L', 6.01);
		isoelectricpoint.put('M', 5.74);
		isoelectricpoint.put('N', 5.41);
		isoelectricpoint.put('O', 21.0);
		isoelectricpoint.put('P', 6.3);
		isoelectricpoint.put('Q', 5.65);
		isoelectricpoint.put('R', 10.76);
		isoelectricpoint.put('S', 5.68);
		isoelectricpoint.put('T', 5.6);
		isoelectricpoint.put('U', 20.0);
		isoelectricpoint.put('V', 6.0);
		isoelectricpoint.put('W', 5.89);
		isoelectricpoint.put('Y', 5.64);

		for (char c : hydropathyindex.keySet()) {
			double d = hydropathyindex.get(c);
			uff.add(new Erm(d, c));
		}
		Collections.sort(uff);

		for (char c : sidechainpolarity.keySet()) {
			double d = sidechainpolarity.get(c);
			uff2.add(new Erm(d, c));
		}
		Collections.sort(uff2);

		for (char c : sidechaincharge.keySet()) {
			double d = sidechaincharge.get(c);
			uff3.add(new Erm(d, c));
		}
		Collections.sort(uff3);

		for (char c : aamass.keySet()) {
			double d = aamass.get(c);
			mass.add(new Erm(d, c));
		}
		Collections.sort(mass);

		for (char c : isoelectricpoint.keySet()) {
			double d = isoelectricpoint.get(c);
			isoel.add(new Erm(d, c));
		}
		Collections.sort(isoel);
	}

	public void shift(int i) {
		if (i < 0) {
			i = sb.length()+i;
		}
		var nsb = new StringBuilder(sb.capacity());
		nsb.append(sb.substring(i));
		nsb.append(sb.substring(0, i));
		sb = nsb;
	}

	public StringBuilder getSequence() {
		return sb;
	}

	public void setSequence(StringBuilder sb) {
		this.sb = sb;
	}
	
	public int getNumberOfSubContigs() {
		int count = 0;
		int i = sb.indexOf("NNN");
		if( i != -1 ) count++;
		
		while( i != -1 ) {
			int k = sb.indexOf("NNN", i+5);
			if( k > i+100 ) {
				count++;
			}
			i = k;
		}
		return count;
	}
	
	public void deleteAfter( Annotation cur ) {
		int i = annset.indexOf( cur );
		if( i != -1 && i < annset.size() && annset.get(i+1).getGene() == null ) {
			annset.remove( i+1 );
		}
	}

	public int deleteAllAfter( Annotation cur ) {
		int k = 0;
		int i = annset.indexOf( cur );
		while( i != -1 && i < annset.size()-1 && annset.get(i+1).getGene() == null ) {
			annset.remove( i+1 );
			k++;
		}
		return k;
	}
	
	public void deleteBefore( Annotation cur ) {
		int i = annset.indexOf( cur );
		if( i > 0 && annset.get(i-1).getGene() == null )
			annset.remove( i-1 );
	}

	public int deleteAllBefore( Annotation cur ) {
		int k = 0;
		int i = annset.indexOf( cur );
		while( i > 0 && annset.get(i-1).getGene() == null ) {
			annset.remove(--i);
			++k;
		}
		return k;
	}

	public boolean delete(Annotation ann) {
		return annset.remove(ann);
	}
	
	public static void writeFasta( OutputStream os, List<Sequence> lseq ) throws IOException {
		writeFasta(os, lseq, false);
	}
	
	public static void writeFasta( OutputStream os, List<? extends Sequence> lseq, boolean italic ) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		writeFasta( osw, lseq, italic, false );
		osw.close();
	}
	
	public static void writeFasta( Writer osw, List<? extends Sequence> lseq, boolean italic, boolean group ) throws IOException {
		for( Sequence seq : lseq ) {
			seq.writeSequence(osw, italic, group);
		}
	}

	public static void writeIdFasta( Writer osw, List<? extends Sequence> lseq ) throws IOException {
		for( Sequence seq : lseq ) {
			seq.writeIdSequence(osw);
		}
	}
	
	public static void writeFasta( Writer osw, List<? extends Sequence> lseq, int start, int stop, boolean italic ) throws IOException {
		for( Sequence seq : lseq ) {
			seq.writeSequence(osw,start,stop,italic);
		}
	}

	public void writeSequence( Writer fw, int gap, int start, int stop, boolean italic ) throws IOException {
		int val = Math.max( 0, start-getStart() );
		int end = Math.min( length(), stop-getStart() );

		if( end > val ) {
			fw.write(">"+getName()+"\n");
			for( int k = 0; k < sb.length(); k+=gap ) {
				int m = Math.min(sb.length(), k+gap);
				String substr = sb.substring(k, m);
				//(seq.sb.length() == k+70 ? "")
				fw.write( substr+"\n" );
			}
		}
	}

	public void writeSequence( Writer fw, int start, int stop, boolean italic ) throws IOException {
		writeSequence( fw, 70, start, stop, italic );
	}
	
	public void injectAfter( Annotation cur, Annotation tv ) {
		int i = annset.indexOf( cur );
		if( i != -1 ) {
			addAnnotation(i+1, tv);
		}
	}
	
	public void injectBefore( Annotation cur, Annotation tv ) {
		int i = annset.indexOf( cur );
		if( i != -1 ) {
			addAnnotation( i, tv );
		}
	}
	
	public void initIndexBuffers() {
		ib = IntBuffer.allocate( sb.length() );
		int count = 0;
		for( int i = 0; i < sb.length(); i++ ) {
			ib.put( i, count );
			char c = sb.charAt(i);
			if( c != '.' && c != '-' && c != ' ' ) {
				count++;
			}
		}
		rib = IntBuffer.allocate( count );
		count = 0;
		for( int i = 0; i < sb.length(); i++ ) {
			char c = sb.charAt(i);
			if( c != '.' && c != '-' && c != ' ' ) {
				rib.put( count, i );
				count++;
			}
		}
	}

	static class Locus {
		String locus_tag;
		String locus_decformat;
		String locus;
		String locus_tag_decformat;
		String source;
		String organism;

		Locus( String lt, String dec, String lcs, String ldec, String src, String org ) {
			locus_tag = lt;
			locus_tag_decformat = dec;
			locus = lcs;
			locus_decformat= ldec;
			source = src;
			organism = org;
		}
	}

	class Reference {
		public Reference(String authors, String title, String journal) {
			this.authors = authors;
			this.title = title;
			this.journal = journal;
		}

		public String toString() {
			return name;
		}

		public void export(Appendable sb) throws IOException {
			sb.append("   AUTHORS   "+authors+"\n");
			sb.append("   TITLE     "+title+"\n");
			sb.append("   JOURNAL   "+journal+"\n");
		}

		String name;
		String authors;
		String title;
		String journal;
	}
	
	public Sequence				consensus;
	public String				definition;
	public String				version;
	public String				keywords;
	public String 				country;
	Set<Reference> 				refset;
	String						organism;
	public IntBuffer			ib = null;
	public IntBuffer			rib = null;
	public int					offset = 0;
	private int					revcomp = 0;
	private int					gcat = 0;
	float						gcp = -1;
	int							alignedlength = -1;
	int							unalignedlength = -1;
	int							substart = -1;
	int							substop = 0;
	public List<Annotation>		annset;
	public int					index = -1;
	public boolean				edited = false;
	public boolean				selected = false;
	
	public Sequence			next;
	public Sequence			prev;
	public List<Sequence>	partof;
	
	public double 			loc;
	
	public int getAnnotationCount() {
		if( annset != null ) {
			return annset.size();
		}
		return 0;
	}

	public void clear() {
		sb.setLength(0);
	}
	
	public static List<Sequence> readFasta( Path p, Map<String,Sequence> mseq ) throws IOException {
		BufferedReader br = Files.newBufferedReader(p);
		List<Sequence> ret = readFasta(br, mseq);
		br.close();
		
		return ret;
	}
	
	public static List<Sequence> readFasta( BufferedReader br, Map<String,Sequence> mseq ) throws IOException {
		return readFasta(br, mseq, false);
	}
	
	public static List<Sequence> readFasta( BufferedReader br, Map<String,Sequence> mseq, boolean shrt ) throws IOException {
		List<Sequence> lseq = new ArrayList<>();
		
		Sequence seq = null;
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				String name = line.substring(1);
				if( shrt ) name = name.substring(0,name.indexOf(' '));
				seq = new Sequence(name, mseq);
				lseq.add( seq );
			} else seq.append(line);
			line = br.readLine();
		}
		
		return lseq;
	}
	
	public static int specCheck( String str ) {
		int i = str.indexOf("uid");
		if( i == -1 ) {
			i = str.indexOf("NMX");
		}
		if( i == -1 ) {
			i = str.indexOf("RAST");
		}
		if( i == -1 ) {
			i = str.indexOf("33923_K677DRAFT");
		}
		if( i == -1 ) {
			i = str.indexOf("H328");
		}
		if( i == -1 ) {
			i = str.indexOf("Tb_T");
		}
		return i;
	}
	
	public void sortLocs() {
		if( annset != null ) {
			Collections.sort(annset);
			int i = 0;
			//Tegeval prev = null;
			for( Annotation tv : annset ) {
				tv.setNum( i++ );
				//if( prev != null ) tv.setPrevious( prev );
				//prev = tv;
			}
		}
	}
	
	public static int parseSpec( String lname ) {
		int i = lname.indexOf("contig");
		if( i == -1 ) {
			i = lname.indexOf("Contig");
		}
		if( i == -1 ) {
			i = lname.indexOf("CONTIG");
		}
		if( i == -1 ) {
			i = lname.indexOf("NODE_");
		}
		if( i == -1 ) {
			i = lname.indexOf("scaffold");
		}
		if( i == -1 ) {
			i = lname.indexOf("chromosome");
		}
		if( i == -1 ) {
			i = lname.indexOf("plasmid");
		}
		if( i == -1 ) {
			i = lname.indexOf("RAST");
			if( i >= 0 ) i += 5;
		}
		if( i == -1 && lname.length() > 5 && (lname.startsWith("J") || lname.startsWith("A") || lname.startsWith("L") || lname.startsWith("B")) && lname.charAt(4) == '0' ) i = 5;
		if( i == -1 ) {
			i = lname.indexOf("uid");
			if( i != -1 ) {
				i = lname.indexOf('_', i)+1;
			}
		}

		if( i == -1 ) {
			i = lname.lastIndexOf('_');
			i = lname.lastIndexOf('_', i-1)+1;
		}
		
		/*int i = lname.indexOf("contig");
		if( i == -1 ) {
			i = lname.indexOf("scaffold");
		}
		if( i == -1 ) {
			i = lname.indexOf("RAST")+5;
		}
		if( i == -1 && lname.length() > 5 && lname.startsWith("J") && lname.charAt(4) == '0' ) i = 5;*/
		
		return i;
	}

	public static String nameFix( String selspec ) {
		return nameFix(selspec, true);
	}

	public static String nameFix( String selspec, boolean isthermus ) {
		String ret = selspec;
		if( isthermus ) {
			if( selspec.contains("Rhodothermus_") ) {
				selspec = selspec.replace("Rhodothermus_", "R.");
				int i = selspec.indexOf("_uid");
				if( i != -1 ) {
					ret = selspec.substring(0,i);
				} else {
					i = selspec.indexOf('_');
					if( i != -1 ) {
						i = selspec.indexOf('_', i+1);
						if( i != -1 ) {
							i = selspec.indexOf('_', i+1);
							if( i != -1 ) {
								i = selspec.lastIndexOf('_', i+1);
								if( i != -1 ) ret = selspec.substring(0, i);
							}
						}
					}
				}
			} else if( selspec.contains("Marinithermus_") ) {
				ret = "Marinithermus_hydrothermalis_T1";
			} else if( selspec.contains("hermus") ) {	
				int i = selspec.indexOf("_uid");
				if( i != -1 ) {
					if( selspec.contains("ATCC_700962") ) {
						ret = "Thermus_igniterrae_RF-4";
					} else if( selspec.contains("DSM_12092") ) {
						ret = "Thermus_sps-17";
					} else {
						ret = selspec.substring(0,i);
					}
				} else if(selspec.contains("DSM")) {
					if(selspec.contains("oshimai")) {
						ret = "Thermus_oshimai_SPS-17";
					}/* else if(selspec.contains("")) {
					
					}*/ else {
						int k = selspec.indexOf("DSM");
						k = selspec.indexOf('_', k+4);
						if( k == -1 ) k = selspec.length();
						ret = selspec.substring(0,k);
					}
				} else if(selspec.contains("ATCC")) {
					if(selspec.contains("igniterrae")) {
						ret = "Thermus_igniterrae_RF-4";
					}
				} else {
					if( selspec.equals("Thermus_4884") ) ret = "Thermus_sp._HR13";
					else if( selspec.equals("Thermus_2121") ) ret = "Thermus_scotoductus_MAT_2121";
					
					i = selspec.indexOf('_');
					if( i != -1 ) {
						i = selspec.indexOf('_', i+1);
						if( i != -1 ) {
							i = selspec.indexOf('_', i+1);
							if( i != -1 ) {
								i = selspec.lastIndexOf('_', i+1);
								if( i != -1 ) ret = selspec.substring(0, i);
							}
						}
					}
				}
			} /*else if( (selspec.charAt(0) == 'J' || selspec.charAt(0) == 'A' || selspec.charAt(0) == 'L' || selspec.charAt(0) == 'B') && (selspec.length() == 4 || selspec.charAt(4) == '0') ) {
				if( selspec.startsWith("JQNC") ) ret = "Thermus_caliditerrae_YIM_77777";
				if( selspec.startsWith("JQMV") ) ret = "Thermus_amyloliquefaciens_YIM_77409";
				if( selspec.startsWith("JQLK") ) ret = "Thermus_tengchongensis_YIM_77401";
				if( selspec.startsWith("JQLJ") ) ret = "Thermus_scotoductus_KI2";
				
				if( selspec.startsWith("JPSL") ) ret = "Thermus_filiformis_Wai33_A1";
				if( selspec.startsWith("JTJB") ) ret = "Thermus_sp_2.9";
				
				if( selspec.startsWith("AUIW") ) ret = "Thermus_antranikianii_HN3-7";
				if( selspec.startsWith("ATXJ") ) ret = "Thermus_islandicus_PRI_3838";
				if( selspec.startsWith("ATNI") ) ret = "Thermus_scotoductus_NMX2_A1";
				if( selspec.startsWith("ARLD") ) ret = "Thermus_scotoductus_SE-1";
				if( selspec.startsWith("AQOS") ) ret = "Thermus_thermophilus_AT-62";
				if( selspec.startsWith("ARDI") ) ret = "Thermus_oshimai_DSM_12092";
				if( selspec.startsWith("ABVK") ) ret = "Thermus_aquaticus_Y51MC23";
				if( selspec.startsWith("AQWU") ) ret = "Thermus_igniterrae_ATCC_700962";
				if( selspec.startsWith("AIJQ") ) ret = "Thermus_parvatiensis";
				
				if( selspec.startsWith("LHCI") ) ret = "Thermus_aquaticus_YT1";
				if( selspec.startsWith("LJJR") ) ret = "Thermus_scotoductus_K1";
				
				if( selspec.startsWith("BBBL") ) ret = "Thermus_kawarayensis_JCM_12314";
				if( selspec.startsWith("BBBN") ) ret = "Thermus_sp_JCM_17653";
			}*/ else if( selspec.contains("GenBank") || selspec.contains("MAT") ) {
				
			} else {
				if( selspec.contains("islandicus") ) ret = "Thermus_islandicus_MAT_3838";
				if( selspec.contains("eggertsoni") ) ret = "Thermus_eggertsoniae_MAT_2789";
				else if( selspec.contains("filiformis") ) ret = "Thermus_filiformis_MAT_947";
				else if( selspec.contains("kawarayensis") ) ret = "Thermus_kawarayensis_KW11";
				else if( selspec.contains("arciformis") ) ret = "Thermus_arciformis_TH92";
				else if( selspec.contains("brockianus1003") ) ret = "Thermus_brockianus_YS38";
				else if( selspec.contains("scotoductus252") ) ret = "Thermus_scotoductus_MAT_252";
				else if( selspec.contains("scotoductus4063") ) ret = "Thermus_scotoductus_SA-01";
				else if( selspec.contains("aquaticus4884") ) ret = "Thermus_sp._HR13";
				
				else {
					int i = 0;
					while( i < selspec.length() && (selspec.charAt(i) < '0' || selspec.charAt(i) > '9') ) {
						i++;
					}
					if( i != selspec.length() ) selspec = selspec.substring(0,i)+"_MAT"+selspec.substring(i);
					else {
						System.err.println("doni "+selspec);
					}
					
					Matcher m = Pattern.compile("\\d").matcher(selspec); 
					int firstDigitLocation = m.find() ? m.start() : 0;
					if( firstDigitLocation == 0 ) ret = "Thermus_" + selspec;
					else ret = "Thermus_" + selspec.substring(0,firstDigitLocation) + "_" + selspec.substring(firstDigitLocation);
				}
			}
			return ret.replace("Thermus_", "T.");
		}
		return ret;
	}
	
	public static String getSpec( String name ) {
		String spec = "";
		int i = specCheck( name );
		
		if( i == -1 ) {
			i = parseSpec( name );
			if( i == -1 ) {
				i = name.lastIndexOf('_')+1;
			}
			if( i <= 0 ) {
				spec = null;
			} else spec = name.substring(0, i-1);
		} else {
			i = name.indexOf("_", i+1);
			spec = name.substring(0, i);
		}
		return spec;
	}
	
	public Annotation getNext( Annotation from ) {
		Annotation ret = null;
		
		int i = annset != null ? annset.indexOf( from ) : -1;
		if( i != -1 ) {
			if( isReverse() ) {
				if( i > 0 ) {
					ret = annset.get( i-1 );
				} else {
					ret = getPrevContig().getFirst();
				}
			} else {
				if( i < annset.size()-1 ) {
					ret = annset.get( i+1 );
				} else {
				 	ret = getNextContig().getFirst();
				}
			}
		}
		
		return ret;
	}
	
	public Annotation getPrev( Annotation from ) {
		Annotation ret = null;

		int i = annset != null ? annset.indexOf( from ) : -1;
		if( i != -1 ) {
			if( isReverse() ) {
				if( i < annset.size()-1  ) {
					ret = annset.get( i+1 );
				} else {
					ret = getNextContig().getLast();
				}
			} else {
				if( i > 0 ) {
					return annset.get( i-1 );
				} else {
					ret = getPrevContig().getLast();
				}
			}
		}
		//System.err.println( from.getGene().getSpecies() + "  " + from.getGene() );
		return ret;
	}
	
	public Annotation getEndAnnotation() {
		if( annset != null ) return annset.get( annset.size()-1 );
		return null;
	}
	
	public Annotation getStartAnnotation() {
		if( annset != null ) return annset.get( 0 );
		return null;
	}
	
	public Annotation getFirst() {
		return isReverse() ? getEndAnnotation() : getStartAnnotation();
	}
	
	public Annotation getLast() {
		return isReverse() ? getStartAnnotation() : getEndAnnotation();
	}
	
	public Annotation getIndex( int i ) {
		Annotation first = getFirst();
	
		int k = 0;
		while( first != null && k < i ) {
			first = first.getNext();
			k++;
		}
		
		return first;
	}
	
	public String getSpec() {
		return group;
	}
	
	public boolean isReverse() {
		return revcomp == -1;
	}

	public int getGC() {
		return gcat;
	}

	public void setGC(int gc) {
		gcat = gc;
	}
	
	public void setReverse( boolean rev ) {
		revcomp = rev ? -1 : 1;
	}
	
	static final Random r = new Random();
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected( boolean sel ) {
		this.selected = sel;
	}
	
	public void removeGaps() {
		
	}
	
	public static String getPhylip( List<Sequence> lseq, boolean numeric ) {
		StringBuilder out = new StringBuilder();
		
		for( Sequence seq : lseq ) {
			System.err.println( seq.getName() );
		}
		
		String erm = ""+lseq.size();
		String seqlen = "";
		for( int i = 0; i < 6-erm.length(); i++ ) {
			seqlen += " ";
		}
		seqlen += erm;
		int alen = lseq.get(0).length();
		seqlen += "   "+alen;
		
		out.append( seqlen+"\n" );
		
		Map<String,Integer> seqset = new HashMap<String,Integer>();
		
		int u = 0;
		int count = 0;
		for( int k = 0; k < alen; k+=50 ) {
			int seqi = 0;
			for( Sequence seq : lseq ) {
				if( u == 0 ) {
					if( !numeric ) {
						String seqname = seq.getName();
						int m = Math.min( seqname.length(), 10 );
						
						String subname = seqname.substring(0, m);
						
						int scount = 1;
						String newname;
						if( seqset.containsKey( subname ) ) {
							scount = seqset.get( subname )+1;
							//if( seqname.length() > 10 ) {
							//	subname = seqname.substring( seqname.length()-10, seqname.length() );
							//} else {
							String cstr = ""+scount;
								m = Math.min( seqname.length(), 10-cstr.length() );
								newname = seqname.substring(0,m)+cstr;
							//}
						} else newname = subname;
						seqset.put( subname, scount );
						
						out.append( newname );
						while( m < 10 ) {
							out.append(' ');
							m++;
						}
					} else {
						String sind = Integer.toString( seqi++ );
						
						int m = 0;
						while( m < 10-sind.length() ) {
							out.append('0');
							m++;
						}
						out.append( sind );
					}
				} else out.append("          ");
				
				for( int l = k; l < Math.min(k+50, alen); l++ ) {
					if( l % 10 == 0 ) {
						out.append(" ");
					}
					out.append( seq.getCharAt(l + seq.getStart()) );
				}
				out.append("\n");
			}
			out.append("\n");
			
			u++;
		}
		
		return out.toString();
	}
	
	public static double[] entropy( List<Sequence> lseq ) {
		int total = lseq.get(0).length();
		double[] ret = new double[total];
		Map<Character,Integer>	shanmap = new HashMap<Character,Integer>();
		for( int x = 0; x < total; x++ ) {
			shanmap.clear();
			
			int count = 0;
			for( Sequence seq : lseq ) {
				char c = seq.getCharAt( x );
				if( c != '.' && c != '-' && c != ' ' && c != '\n' ) {
					int val = 0;
					if( shanmap.containsKey(c) ) val = shanmap.get(c);
					shanmap.put( c, val+1 );
					count++;
				}
			}
			
			double res = 0.0;
			for( char c : shanmap.keySet() ) {
				int val = shanmap.get(c);
				double p = (double)val/(double)count;
				res -= p*Math.log(p);
			}
			ret[x] = res/Math.log(2.0);
		}
		return ret;
	}
	
	public static void distanceMatrixNumeric(List<Sequence> lseq, double[] dmat, List<Integer> idxs, boolean bootstrap, boolean cantor, double[] ent, Map<String,Integer> blosum ) {
		int len = lseq.size();
		//double[] dmat = new double[ len*len ];
		for( int x = 0; x < len; x++ ) {
			dmat[x*len+x] = 0.0;
		}
		if( idxs != null && idxs.size() > 0 ) {
			int count = idxs.size();
			for( int x = 0; x < len-1; x++ ) {
				for( int y = x+1; y < len; y++ ) {
					Sequence seq1 = lseq.get(x);
					Sequence seq2 = lseq.get(y);
					
					//if( seq1 == seq2 ) dmat[i] = 0.0;
					//else {
						double mism = 0;
						
						if( ent != null ) {
							if( bootstrap ) {
								for( int k : idxs ) {
									int ir = r.nextInt( idxs.size() );
									int u = idxs.get(ir);
									char c1 = seq1.getCharAt( u-seq1.getStart() );
									char c2 = seq2.getCharAt( u-seq2.getStart() );
									
									if( c1 != c2 ) mism += 1.0/ent[u];
									//count++;
								}
							} else {
								for( int k : idxs ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != c2 ) mism += 1.0/ent[k];
								}
							}
						} else {
							if( bootstrap ) {
								for( int k : idxs ) {
									int ir = r.nextInt( idxs.size() );
									char c1 = seq1.getCharAt( idxs.get(ir)-seq1.getStart() );
									char c2 = seq2.getCharAt( idxs.get(ir)-seq2.getStart() );
									
									if( c1 != c2 ) mism++;
								}
							} else {
								for( int k : idxs ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != c2 ) mism++;
								}
							}
						}
						double d = mism/(double)count;
						if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
						dmat[x*len+y] = d;
						dmat[y*len+x] = d;
					//}
					//i++;
				}
			}
		} else {
			for( int x = 0; x < len-1; x++ ) {
				for( int y = x+1; y < len; y++ ) {
					Sequence seq1 = lseq.get(x);
					Sequence seq2 = lseq.get(y);
						
					int count = 0;
					double mism = 0;
					
					int start = 0;//Math.max( seq1.getRealStart(), seq2.getRealStart() );
					int end = seq1.length();//Math.min( seq1.getRealStop(), seq2.getRealStop() );
					
					if( ent != null ) {
						/*if( start < 0 || end >= ent.length ) {
							System.err.println( "mu " + ent.length );
							System.err.println( start + "  " + end );
						}*/
						
						if( bootstrap ) {
							for( int k = start; k < end; k++ ) {
								int ir = start + r.nextInt( end-start );
								char c1 = seq1.getCharAt( ir-seq1.getStart() );
								char c2 = seq2.getCharAt( ir-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism += 1.0/ent[ir];
									count++;
								}
							}
						} else {
							if( blosum != null ) {
								/////-----
								for( int k = start; k < end; k++ ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
										if( c1 != c2 ) {
											mism += 1.0/ent[k];
											/*if( ent[k] == 0.0 ) {
												System.err.println("ok");
											}*/
										}
										count++;
									}
								}
							} else {
								for( int k = start; k < end; k++ ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
										if( c1 != c2 ) {
											mism += 1.0/ent[k];
											/*if( ent[k] == 0.0 ) {
												System.err.println("ok");
											}*/
										}
										count++;
									}
								}
							}
						}
					} else {
						if( bootstrap ) {
							for( int k = start; k < end; k++ ) {
								int ir = start + r.nextInt( end-start );
								char c1 = seq1.getCharAt( ir-seq1.getStart() );
								char c2 = seq2.getCharAt( ir-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism++;
									count++;
								}
							}
						} else {
							if( blosum != null ) {
								for( int k = start; k < end; k++ ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
										String str = c1+""+c2;
										String fstr = c1+""+c1;
										mism += blosum.get( str );
										count += blosum.get( fstr );
									}
								}
								if( count == 0 ) {
									System.err.println();
								}
							} else {
								for( int k = start; k < end; k++ ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
										if( c1 != c2 ) mism++;
										count++;
									}
								}
							}
						}
					}
					double d = count == 0 ? 0.0 : mism /(double)count;
					if( blosum != null ) d = 1.0-d;
					if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
					
					dmat[x*len+y] = d;
					dmat[y*len+x] = d;
				}
			}
		}
		
		//return dmat;
	}
	
	public void reverseComplement() {
		for( int i = 0; i < length()/2; i++ ) {
			char c = sb.charAt(i);
			char cc = rc.getOrDefault(c, c);
			
			char nc = sb.charAt(length()-1-i);
			char ncc = rc.getOrDefault(nc, nc);
			
			sb.setCharAt( i, ncc );
			sb.setCharAt( length()-1-i, cc );
		}
		
		if( annset != null ) {
			int i;
			for( i = 0; i < annset.size()/2; i++ ) {
				Annotation a = annset.get(i);
				
				a.start = length()-a.stop-1;
				a.stop = length()-a.start-1;
				a.ori *= -1;
				a = annset.set(annset.size()-i-1, a);
				
				a.start = length()-a.stop-1;
				a.stop = length()-a.start-1;
				a.ori *= -1;
				annset.set(i, a);
			}
			if( annset.size()%2 == 1 ) {
				Annotation a = annset.get(annset.size()/2+1);
				a.start = length()-a.stop-1;
				a.stop = length()-a.start-1;
				a.ori *= -1;
			}
		}
	}
	
	public void reverseComplement( int start, int end ) {
		for( int i = start; i < start+(end-start)/2; i++ ) {
			char c = sb.charAt(i);
			char cc = rc.getOrDefault(c, c);
			
			char nc = sb.charAt(length()-1-i);
			char ncc = rc.getOrDefault(nc, nc);
			
			sb.setCharAt( i, ncc );
			sb.setCharAt( length()-1-i, cc );
		}
	}
	
	public void reverse( int start, int end ) {
		//System.err.println( sb.substring(start,end) + " " + getName() );
		for( int i = start; i < start+(end-start)/2; i++ ) {
			char c = sb.charAt(i);
			int ri = end-1-i+start;
			sb.setCharAt( i, sb.charAt(ri) );
			sb.setCharAt( ri, c );
		}
	}
	
	public void reverse() {
		for( int i = 0; i < length()/2; i++ ) {
			char c = sb.charAt(i);
			sb.setCharAt( i, sb.charAt(length()-1-i) );
			sb.setCharAt( length()-1-i, c );
		}
	}
	
	public void complement( int start, int end ) {
		for( int i = start; i < end; i++ ) {
			char c = sb.charAt(i);
			char cc = rc.getOrDefault(c, c);
			/*if( c == rc ) {
				System.err.println();
			}*/
			sb.setCharAt( i, cc );
		}
	}
		
	public void complement() {
		for( int i = 0; i < length(); i++ ) {
			char c = sb.charAt(i);
			char cc = rc.getOrDefault(c, c);
			/*if( c == rc ) {
				System.err.println();
			}*/
			sb.setCharAt( i, cc );
		}
	}
	
	public void upperCase() {
		for( int i = 0; i < sb.length(); i++ ) {
			sb.setCharAt( i, Character.toUpperCase(sb.charAt(i)) );
		}
	}
	
	public void caseSwap() {
		
	}
	
	public void replaceSelected( Sequence seq, int start, int end ) {
		this.sb.replace(start, end, seq.sb.toString());
	}
	
	public void utReplace() {
		int i1 = sb.indexOf("T");
		int i2 = sb.indexOf("U");
		
		if( i1 == -1 ) i1 = sb.length();
		if( i2 == -1 ) i2 = sb.length();
		
		while( i1 < sb.length() || i2 < sb.length() ) {
			while( i1 < i2 ) {
				sb.setCharAt(i1, 'U');
				i1 = sb.indexOf("T", i1+1);
				if( i1 == -1 ) i1 = sb.length();
			}
			
			while( i2 < i1 ) {
				sb.setCharAt(i2, 'T');
				i2 = sb.indexOf("U", i2+1);
				if( i2 == -1 ) i2 = sb.length();
			}
		}
		
		i1 = sb.indexOf("t");
		i2 = sb.indexOf("u");
		
		if( i1 == -1 ) i1 = sb.length();
		if( i2 == -1 ) i2 = sb.length();
		
		while( i1 < sb.length() || i2 < sb.length() ) {
			while( i1 < i2 ) {
				sb.setCharAt(i1, 'u');
				i1 = sb.indexOf("t", i1+1);
				if( i1 == -1 ) i1 = sb.length();
			}
			
			while( i2 < i1 ) {
				sb.setCharAt(i2, 't');
				i2 = sb.indexOf("u", i2+1);
				if( i2 == -1 ) i2 = sb.length();
			}
		}
	}
	
	public boolean isEdited() {
		return edited;
	}
	
	public void setSequenceString( StringBuilder sb ) {
		this.sb = sb;
	}
	
	public Sequence() {
		super();
		sb = new StringBuilder();
	}
	
	public Sequence( String id, String name, Map<String,Sequence> mseq ) {
		this( name, mseq );
		this.id = id;
	}
	
	public Sequence( String name, Map<String,Sequence> mseq ) {
		this();
		setName( name );
		if( mseq != null ) mseq.put( name, this );
	}
	
	public Sequence( String id, String name, StringBuilder sb, Map<String,Sequence> mseq ) {
		this( name, sb, mseq );
		this.id = id;
	}
	
	public Sequence( String name, StringBuilder sb, Map<String,Sequence> mseq ) {
		setName( name );
		this.sb = sb;
		this.id = name;
		if( mseq != null ) {
			//int millind = name.indexOf('#');
			//if( millind == -1 ) millind = name.length();
			//String val = name.substring( 0, millind ).trim();
			mseq.put( name, this );
		}
	}
	
	public Sequence( Sequence seq, boolean rev ) {
		this();
		sb.append( seq.sb );
		if( rev ) {
			this.reverseComplement();
		}
	}
	
	public Annotation getAnnotation( int i ) {
		return annset.get(i);
	}
	
	public List<Annotation> getAnnotations() {
		return annset;
	}
	
	public void removeAnnotation( Annotation a ) {
		if( annset != null ) {
			annset.remove( a );
		}
	}
	
	public boolean addAnnotation( int i, Annotation a ) {
		if( annset == null ) {
			annset = new ArrayList<>();
		} else for( Annotation an : annset ) {
			if( an.start == a.start && a.start > 0 ) return true;
		}
		/*if( annset.contains(a) ) {
			if( a.start == 0 ) annset.add( a );
		} else*/ 
 		annset.add( i, a );
		
		return false;
	}
	
	public boolean addAnnotation( Annotation a ) {
		if( annset == null ) {
			annset = new ArrayList<>();
		} else for( Annotation an : annset ) {
			if( an.start == a.start && a.start > 0 ) return true;
		}
		/*if( annset.contains(a) ) {
			if( a.start == 0 ) annset.add( a );
		} else*/ 
		annset.add( a );
		
		return false;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public boolean equals( Object obj ) {			
		/*boolean ret = name.equals( obj.toString() ); //super.equals( obj );
		System.err.println( "erm " + this.toString() + " " + obj.toString() + "  " + ret );
		return ret;*/			
		return super.equals( obj );
	}
	
	public StringBuilder getStringBuilder() {
		return sb;
	}
	
	public String toString() {
		return name;
	}

	public void toGenbank( Appendable genbank, boolean translations ) throws IOException {
		genbank.append("LOCUS        ").append(name).append("\n");
		genbank.append("DEFINITION   ").append(definition).append("\n");
		genbank.append("ACCESSION    ").append(id).append("\n");
		genbank.append("VERSION      ").append(version).append("\n");
		genbank.append("KEYWORDS     ").append(keywords).append("\n");
		genbank.append("SOURCE       ").append(group).append("\n");
		genbank.append("ORGANISM     ").append(organism).append("\n");
		for( Reference ref : refset ) {
			genbank.append("REFERENCE   ").append(String.valueOf(ref)).append("\n");
			ref.export(genbank);
		}
		genbank.append("FEATURES     Location/Qualifiers");
		int k = 0;
		if( isReverse() ) {
			for (int i = annset.size()-1; i>=0; i--) {
				Annotation a = annset.get(i);
				a.export(genbank, translations, Optional.empty(), ++k, getName());
			}
		} else {
			for (Annotation a : annset) {
				a.export(genbank, translations, Optional.empty(), ++k, getName());
			}
		}
	}
	
	public void append( Sequence seq ) {
		if( seq.getAnnotations() != null ) for( Annotation a : seq.getAnnotations() ) {
			this.addAnnotation( new Annotation( a, this.length() ) );
		}
		sb.append( seq.sb );
	}
	
	public void append( CharSequence cs ) {
		sb.append( cs );
	}
	
	public void append( String str ) {
		sb.append( str );
	}
	
	public void append( char c ) {
		sb.append( c );
	}
	
	public void append( Character c ) {
		sb.append( c );
	}
	
	public void appendSubseq( Sequence subs, int start, int end ) {
		//getSubstring(-3000, subs.getLength()+3000);
	}
	
	public void deleteCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < sb.length() ) {
			sb.deleteCharAt(ind);
			edited = true;
		}
	}
	
	public void delete( int dstart, int dstop ) {
		int ind = dstart-offset;
		int end = dstop-offset;
		if( ind >= 0 && end <= sb.length() ) {
			sb.delete( ind, end );
			edited = true;
		}
	}
	
	public char charAt( int i ) {
		return revcomp == -1 ? revCompCharAt( i ) : getCharAt( i );
	}
	
	public void clearCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt(ind, '-');
			edited = true;
		}
	}
	
	public void setCharAt( int i, char c ) {
		int ind = i-offset;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt( ind, c );
		}
	}
	
	public char getCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < length() ) {
			return sb.charAt( ind );
		}
		
		return ' ';
	}
	
	public char revCompCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < length() ) {
			char c = sb.charAt( length()-ind-1 );
			if( rc.containsKey(c) ) {
				return rc.get( c );
			} else {
				System.err.println( c );
			}
		}
		
		return ' ';
	}
	
	public void checkLengths() {
		//int start = -1;
		//int stop = 0;
		int count = 0;
		for( int i = 0; i < sb.length(); i++ ) {
			char c = sb.charAt(i);
			if( c != '.' && c != '-' && c != ' ' && c != '*' ) {
				if( substart == -1 ) substart = i;
				substop = i;
				count++;
			}
		}
		alignedlength = count;
		unalignedlength = substop-substart;
	}
	
	public int length() {
		return sb.length();
	}
	
	public void setLength( int len ) {
		sb.setLength( len );
	}
	
	public boolean isChromosome() {
		return this.length() > 1500000;
	}
	
	public boolean isPlasmid() {
		return plasmid;
	}

	public void setPlasmid(boolean plasmid) {
		this.plasmid = plasmid;
	}
	
	public boolean isNucleotide() {
		for( int i = 0; i < sb.length(); i++ ) {
			char c = sb.charAt(i);
			if( !rc.containsKey(c) ) {
				return false;
			}
		}
		return true;
	}
	
	public int getAlignedLength() {
		if( alignedlength == -1 ) {
			checkLengths();
		}
		return alignedlength;
	}
	
	public int getUnalignedLength() {
		if( unalignedlength == -1 ) {
			checkLengths();
		}
		return unalignedlength;
	}
	
	public int getRealStart() {
		return getStart() + substart;
	}
	
	public int getRealStop() {
		return getStart() + substop;
	}
	
	public int getRealLength() {
		return substop - substart;
	}
	
	/*public void boundsCheck() {
		if( start < min ) min = start;
		if( start+sb.length() > max ) max = start+sb.length();
	}*/
	
	public interface RunInt {
		void run(Sequence s);
	};
	
	public static RunInt runbl = null;
	public void setStart( int start ) {
		this.offset = start;
		
		if( runbl != null ) runbl.run( this ); //boundsCheck();
	}
	
	public void setEnd( int end ) {
		this.offset = end-sb.length();
		
		if( runbl != null ) runbl.run( this );
		//boundsCheck();
	}
	
	public int getStart() {
		return offset;
	}
	
	public int getEnd() {
		return offset+sb.length();
	}
	
	public int getRevComp() {
		return revcomp;
	}
	
	public void setRevComp( int rc ) {
		this.revcomp = rc;
	}
	
	public int getGCCount() {
		int ret = 0;
		if( sb.length() > 0 ) {
			for( int i = 0; i < sb.length(); i++ ) {
				char c = sb.charAt(i);
				if( c == 'G' || c == 'g' || c == 'C' || c == 'c' ) {
					ret++;
				}
			}
		}
		return ret;
	}
	
	public float getGCP() {
		if( gcp == -1 && sb.length() > 0 ) {
			gcp = 0;
			int count = 0;
			for( int i = 0; i < sb.length(); i++ ) {
				char c = sb.charAt(i);
				if( c == 'G' || c == 'g' || c == 'C' || c == 'c' ) {
					gcp++;
					count++;
				} else if( c == 'T' || c == 't' || c == 'A' || c == 'a' ) {
					count++;
				}
			}
			gcp = count > 0 ? 100.0f*gcp/count : 0;
		}
		return gcp;
	}
	
	public void setConnection( Sequence contig, boolean rev, boolean forw ) {
		if( forw ) setForwardConnection( contig, rev );
		else setBackwardConnection( contig, rev );
	}
	
	public void setForwardConnection( Sequence contig, boolean rev ) {
		this.next = contig;
		if( rev ) {
			contig.next = this;
			
			/*if( this.getEnd() != null ) this.getEnd().next = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getEnd();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}*/
		} else {
			contig.prev = this;
			
			/*if( this.getEnd() != null ) this.getEnd().next = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getEnd();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}*/
		}
	}
	
	public void setBackwardConnection( Sequence contig, boolean rev ) {
		this.prev = contig;
		if( rev ) {
			contig.next = this;
			
			/*this.getStart().prev = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getStart();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}*/
		} else {
			contig.prev = this;
			
			/*this.getStart().prev = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getStart();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}*/
		}
	}
	
	public Sequence getNextContig() {
		//if (next==null) {
			var i = partof.indexOf(this);
			next = this.isReverse() ? partof.get((i+partof.size()-1)%partof.size()) : partof.get((i+1)%partof.size());
		//}
		return next;
	}
	
	public Sequence getPrevContig() {
		//if (prev==null) {
			var i = partof.indexOf(this);
			prev = this.isReverse() ? partof.get((i+1)%partof.size()) : partof.get((i+partof.size()-1)%partof.size());
		//}
		return prev;
	}

	@Override
	public int compareTo(Sequence o) {
		return offset - o.offset;
	}
	
	public boolean contains( Annotation at ) {
		return annset.contains(at);
	}
	
	public int indexOf( Annotation at ) {
		return annset.indexOf( at );
	}
	
	public Sequence getProteinSequence( int start, int stop, int ori ) {
		Sequence ret = new Sequence();
		ret.consensus = this;
		
		//if( stop > sb.length() ) {
		//if( stop != end ) {
		//	System.err.println();
		//}
		
		if( ori == -1 ) {
			int begin = stop - 3*((stop-start)/3) - 1;
			
			//String aaa = sb.substring(start-1, start+2);
			//String aa = amimap.get( aaa );
			
			//String aaa = sb.substring(stop-2, stop+1);
			//String aa = amimap.get( revcom.get(aaa) );
			
			//System.err.println( aa );
			for( int i = stop-3; i > begin; i-=3 ) {
				String aaa = sb.substring(i, i+3).toUpperCase();
				if(aaa.contains("N") || aaa.contains("X")) {
					ret.append( "X" );
				} else {
					Character aa = amimap.get( revcom.get(aaa) );
					if( aa != null ) ret.append( i != stop-3 ? aa : (aa.equals('V') || aa.equals('L') ? 'M' : aa) );
					//else break;
				}
			}
		} else {
			int end = start + 3*((stop-start)/3) - 1;
			for( int i = start == 0 ? start+2 : start-1; i < end; i+=3 ) {
				if( i < 0 || i+3 >= sb.length() ) {
					System.err.println(  );
				}
				String aaa = sb.substring(i, i+3).toUpperCase();
				if( aaa.contains("N") || aaa.contains("X")) {
					ret.append( "X" );
				} else {
					Character aa = amimap.get( aaa );
					if( aa != null ) ret.append( i != start-1 ? aa : (aa.equals('V') || aa.equals('L') ? 'M' : aa) );
					//else break;
				}
			}
		}
		
		/*if( ret.length() == 0 ) {
			System.err.println(" ");
		}*/
		
		return ret;
	}
	
	public int getSubstringOffset( int start, int end, int ori ) {
		if( ori == -1 ) {
			return Math.min(0,end-sb.length());
		} else return Math.min(0,start);
	}
	
	public String getPaddedSubstring( int ostart, int oend, int ori ) {
		//if( start < sb.length() && end <= sb.length() ) {
		if( ori == -1 ) {
			StringBuilder subsb = new StringBuilder();
			int start = Math.max(0, ostart);
			int end = Math.min(sb.length(), oend);
			for( int i = end-1; i >= start; i-- ) {
				char c = sb.charAt(i);
				char cc = rc.getOrDefault(c, c);
				subsb.append( cc );
			}
			while( oend > Math.max(sb.length(),ostart) ) {
				subsb.insert(0, '-');
				oend--;
			}
			return subsb.toString();
		} else {
			StringBuilder sb2 = new StringBuilder();
			while( ostart < Math.min(0,oend) ) {
				sb2.append('-');
				ostart++;
			}
			sb2.append( sb.substring(ostart, Math.min(oend,sb.length())) );
			return sb2.toString();
			//if( start < sb.length() ) return sb.substring( Math.max(0,start), Math.min(sb.length(),end) );
		}
	}
	
	public String getSubstring( int start, int end, int ori ) {
		//if( start < sb.length() && end <= sb.length() ) {
		if( ori == -1 ) {
			StringBuilder subsb = new StringBuilder();
			start = Math.max(0, start);
			end = Math.min(sb.length(), end);
			for( int i = end-1; i >= start; i-- ) {
				char c = sb.charAt(i);
				char cc = rc.getOrDefault(c, c);
				subsb.append( cc );
			}
			return subsb.toString();
		} else if( start < sb.length() ) {
			int u = Math.max(0,start);
			int e = Math.min(sb.length(),end);
			if( e > u ) return sb.substring( u, e );
		}
		//}
		return "";
	}
}