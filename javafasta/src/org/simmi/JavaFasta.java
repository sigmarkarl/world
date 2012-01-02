package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import netscape.javascript.JSObject;

public class JavaFasta extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class Ruler extends JComponent {
		int 	x;
		double	cw;
		
		public Ruler() {
			super();
			
			final JPopupMenu	popup = new JPopupMenu();
			popup.add( new AbstractAction("Start here") {
				@Override
				public void actionPerformed(ActionEvent e) {					
					int xval = (int)(x/cw)+min;
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = lseq.get(i);
						s.setStart( xval );
					}
					
					checkMaxMin();		
					c.repaint();
					overview.reval();
					overview.repaint();
					updateView();
				}
			});
			popup.add( new AbstractAction("End here") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int xval = (int)(x/cw)+min;
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = lseq.get(i);
						s.setEnd( xval );
					}
					
					checkMaxMin();		
					c.repaint();
					overview.reval();
					overview.repaint();
					updateView();
				}
			});
			popup.addSeparator();
			popup.add( new AbstractAction("Move here") {
				@Override
				public void actionPerformed(ActionEvent e) {					
					int xval = (int)(x/cw);
					int[] rr = table.getSelectedRows();
					
					int min = Integer.MAX_VALUE;
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = lseq.get(i);
						if( s.getStart() < min ) min = s.getStart();
					}
					
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = lseq.get(i);
						s.setStart( xval+(s.getStart()-min) );
					}
					
					checkMaxMin();		
					c.repaint();
					overview.reval();
					overview.repaint();
					updateView();
				}
			});
			popup.add( new AbstractAction("Move end here") {
				@Override
				public void actionPerformed(ActionEvent e) {					
					int xval = (int)(x/cw);
					int[] rr = table.getSelectedRows();
					
					int max = 0;
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = lseq.get(i);
						if( s.getEnd() > max ) max = s.getEnd();
					}
					
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = lseq.get(i);
						s.setEnd( xval-(max-s.getEnd()) );
					}
					
					checkMaxMin();		
					c.repaint();
					overview.reval();
					overview.repaint();
					updateView();
				}
			});
			
			this.setComponentPopupMenu( popup );
			
			this.addMouseMotionListener( new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					x = e.getX();
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {}
			});
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			int h = this.getHeight();
			Rectangle r = g2.getClipBounds();
			
			//double l = Math.log10( cw );
			//int dval = Math.min( 10, )
			for( int x = (int)(r.x/cw); x < (int)((r.x+r.width)/cw)+1; x++ ) {
				int xx = (int)(x*cw);
				int xm = x+min;
				if( xm % 10 == 0 ) {
					g.drawLine(xx+4, h-6, xx+4, h);
					g.drawString( xm+"", xx, h-6);
				} else if( xm % 5 == 0 ) {
					g.drawLine(xx+4, h-6, xx+4, h);
				} else {
					g.drawLine(xx+4, h-4, xx+4, h);
				}
			}
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	};
	
	public class Overview extends JComponent {
		BufferedImage	bi;
		
		public Overview() {
			super();
		}
		
		public void reval() {			
			Graphics bg = bi.getGraphics();
			bg.setColor( Color.white );
			bg.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			bg.setColor( Color.green );
			for( int r = 0; r < table.getRowCount(); r++ ) {
				int i = table.convertRowIndexToModel(r);
				Sequence s = lseq.get(i);
				
				if( max != min ) {
					int x = ((s.getStart()-min)*bi.getWidth())/(max-min);
					int y = (r*bi.getHeight())/lseq.size();
					bg.fillRect( x, y, Math.max(1, (int)( ((long)s.getLength()*(long)bi.getWidth())/(long)(max-min) )), Math.max(1, (bi.getHeight())/lseq.size()) );
				}
			}
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			if( bi == null || bi.getWidth() != this.getWidth() || bi.getHeight() != this.getHeight() ) {
				bi = new BufferedImage( this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB );
				reval();
			}
			
			g.drawImage(bi, 0, 0, this);
		}
	};
	
	public class FastaView extends JComponent {
		Ruler			ruler;
		JTable			table;
		int				rh;
		double			cw = 10.0;
		
		public FastaView( int rh, Ruler ruler, JTable table ) {
			this.rh = rh;
			this.ruler = ruler;
			this.table = table;
			
			this.setToolTipText(" ");
			
			this.addKeyListener( new KeyAdapter() {
				public void keyPressed( KeyEvent k ) {
					int keycode = k.getKeyCode();
					if( keycode == KeyEvent.VK_PLUS ) {
						cw *= 1.25;
					} else if( keycode == KeyEvent.VK_MINUS ) {
						cw *= 0.8;
					}
					FastaView.this.ruler.cw = cw;
					
					updateCoords();
				}
			});
		}
		
		Annotation searchann = new Annotation( null, "search", null );
		public String getToolTipText( MouseEvent e ) {
			Point p = e.getPoint();
			
			int w = (int)(p.x/cw);
			int h = p.y/rh;
			
			if( h >= 0 && h < table.getRowCount() ) {
				int i = table.convertRowIndexToModel( h );
				Sequence seq = lseq.get( i );
				
				if( seq.annset != null && w+min >= seq.getStart() && w+min <= seq.getEnd() ) { 
					searchann.start = (w+min) - seq.getStart();
					int ai = Collections.binarySearch( seq.annset, searchann );
					
					int ip = Math.abs(ai)-1;
					
					if( ip > 0 && ip <= seq.annset.size() ) {
						Annotation a = seq.annset.get( ip-1 );
						if( a.getCoordEnd() > w+min ) return a.name;
					}
				}
			}
			
			return null;
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			Rectangle r = g2.getClipBounds();
			
			int xmin = (int)(r.x/cw);
			int xmax = Math.min( (int)((r.x+r.width)/cw)+1, max-min );
			for( int y = r.y/rh; y < Math.min( (r.y+r.height)/rh+1, lseq.size() ); y++ ) {
				int i = table.convertRowIndexToModel( y );
				Sequence seq = lseq.get( i );
				
				if( seq.annset != null ) {
					for( Annotation a : seq.annset ) {
						g.setColor( a.color );
						for( int x = Math.max(a.getCoordStart()-min, xmin); x < Math.min(a.getCoordEnd()-min, xmax); x++ ) {
							g.fillRect((int)(x*cw), y*rh, (int)cw, rh);
							if( a.ori == -1 ) {
								g.setColor( Color.black );
								g.drawLine((int)(x*cw)+3, y*rh, (int)(x*cw), y*rh+3);
								g.setColor( a.color );
							}
						}
						//if( a.start > )
					}
				}
				
				if( cw > 5.0 ) {
					g.setColor( Color.black );
					for( int x = Math.max(seq.getStart()-min, xmin); x < Math.min(seq.getEnd()-min, xmax); x++ ) {
						g.drawString( Character.toString( seq.charAt(x+min) ), (int)(x*cw), y*rh+rh-2);
					}
				}
			}
		}
		
		public void updateCoords() {
			int w = (int)((max-min)*cw);
			int h = lseq.size()*rh;
			
			this.setPreferredSize( new Dimension(w,h) );
			this.setSize(w, h);
			
			ruler.setPreferredSize( new Dimension(w, 20) );
			ruler.setSize(w, 20);
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	};
	
	public class Annotation implements Comparable<Annotation> {
		Sequence	seq;
		String	name;
		StringBuilder	desc;
		String	type;
		String	group;
		int		start;
		int		stop;
		int		ori;
		Color	color;
		
		public Annotation( Sequence seq, String name, Color color ) {
			this.name = name;
			this.color = color;
			this.seq = seq;
			
			if( seq != null ) {
				seq.addAnnotation( this );
			}
			mann.put( name, this );
		}
		
		public int getLength() {
			return stop-start;
		}
		
		public int getStart() {
			return start;
		}
		
		public int getEnd() {
			return stop;
		}
		
		public void setStart( int start ) {
			this.start = start;
		}
		
		public void setStop( int stop ) {
			this.stop = stop;
		}
		
		public void setOri( int ori ) {
			this.ori = ori;
		}
		
		public void setGroup( String group ) {
			this.group = group;
		}
		
		public void setType( String type ) {
			this.type = type;
		}
		
		public int getCoordStart() {
			return seq.getStart()+start;
		}
		
		public int getCoordEnd() {
			return seq.getStart()+stop;
		}
		
		public void append( String astr ) {
			if( desc == null ) desc = new StringBuilder( astr );
			else desc.append( astr );
		}

		@Override
		public int compareTo(Annotation o) {
			return start - o.start;
		}
	};
	
	public class Sequence implements Comparable<Sequence> {
		String 			name;
		StringBuilder 	sb;
		int				start = 0;
		int				revcomp = 0;
		int				gcp = -1;
		List<Annotation>	annset;
		
		public Sequence( String name ) {
			this.name = name;
			sb = new StringBuilder();
			mseq.put( name, this );
		}
		
		public Sequence( String name, StringBuilder sb ) {
			this.name = name;
			this.sb = sb;
			mseq.put( name, this );
		}
		
		public void addAnnotation( Annotation a ) {
			if( annset == null ) {
				annset = new ArrayList<Annotation>();
			}
			annset.add( a );
		}
		
		public String getName() {
			return name;
		}
		
		public boolean equals( Object obj ) {			
			/*boolean ret = name.equals( obj.toString() ); //super.equals( obj );
			System.err.println( "erm " + this.toString() + " " + obj.toString() + "  " + ret );
			return ret;*/			
			return super.equals( obj );
		}
		
		public String toString() {
			return name;
		}
		
		public void append( String str ) {
			sb.append( str );
		}
		
		public char charAt( int i ) {
			int ind = i-start;
			if( ind >= 0 && ind < sb.length() ) {
				return sb.charAt( ind );
			}
			
			return '-';
		}
		
		public int getLength() {
			return sb.length();
		}
		
		public void boundsCheck() {
			if( start < min ) min = start;
			if( start+sb.length() > max ) max = start+sb.length();
		}
		
		public void setStart( int start ) {
			this.start = start;
			
			boundsCheck();
		}
		
		public void setEnd( int end ) {
			this.start = end-sb.length();
			
			boundsCheck();
		}
		
		public int getStart() {
			return start;
		}
		
		public int getEnd() {
			return start+sb.length();
		}
		
		public int getRevComp() {
			return revcomp;
		}
		
		public int getGCP() {
			if( gcp == -1 && sb.length() > 0 ) {
				gcp = 0;
				for( int i = 0; i < sb.length(); i++ ) {
					char c = sb.charAt(i);
					if( c == 'G' || c == 'g' || c == 'C' || c == 'c' ) gcp++;
				}
				gcp = 100*gcp/sb.length();
			}
			return gcp;
		}

		@Override
		public int compareTo(Sequence o) {
			return start - o.start;
		}
	}
	
	public void addSequence( Sequence seq ) {
		lseq.add( seq );
		if( seq.getLength() > max ) max = seq.getLength();
	}
	
	public void addAnnotation( Annotation ann ) {
		lann.add( ann );
	}
	
	public void importReader( BufferedReader br ) throws IOException {
		Sequence s = null;
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( s != null ) {
					if( s.getEnd() > max ) max = s.getEnd();
				}
				s = new Sequence( line.substring(1) );
				lseq.add( s );
			} else if( s != null ) {
				int start = 0;
				int i = line.indexOf(' ');
				while( i != -1 ) {
					String substr = line.substring(start, i);
					s.append( substr );
					start = i+1;
					i = line.indexOf(' ', start);
				}
				s.append( line.substring(start, line.length()) );
			}
			line = br.readLine();
		}
		br.close();
		
		if( s != null ) {
			if( s.getLength() > max ) max = s.getLength();
		}
	}
	
	public void importFile( String name, InputStream is ) throws IOException {
		if( name.endsWith(".ab1") || name.endsWith(".abi") ) addAbiSequence( name, is );
    	else {
			BufferedReader	br = new BufferedReader( new InputStreamReader( is ) );
			importReader( br );
    	}
	}
	
	public void openFiles() throws IOException {
		FileOpenService fos; 

	    try { 
	        fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService"); 
	    } catch (UnavailableServiceException e) {
	        fos = null; 
	    }
	    
	    if (fos != null) {
	        FileContents[] fcs = fos.openMultiFileDialog(null, null);
            for( FileContents fc : fcs ) {
            	String name = fc.getName();
            	importFile( name, fc.getInputStream() );
            }
	    } else {
	    	JFileChooser	jfc = new JFileChooser();
	    	if( jfc.showOpenDialog( JavaFasta.this ) == JFileChooser.APPROVE_OPTION ) {
	    		File f = jfc.getSelectedFile();
	    		importFile( f.getName(), new FileInputStream(f) );
	    	}
	    }
	    updateView();
	}
	
	public void exportFasta( JTable table, List<Sequence> lseq ) throws IOException, UnavailableServiceException {
		 FileSaveService fss = null;
         FileContents fileContents = null;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         OutputStreamWriter	osw = new OutputStreamWriter( baos );
    	 
    	 int[] rr = table.getSelectedRows();
    	 for( int r : rr ) {
    		 int i = table.convertRowIndexToModel( r );
    		 Sequence seq = lseq.get(i);
    		 osw.write( ">" + seq.name + "\n" );
    		 int val = 0;
    		 while( val < seq.getLength() ) {
    			 osw.write( seq.sb.substring(val, Math.min( seq.getLength(), val+70 )) + "\n" );
    			 val += 70;
    		 }
    	 }
    	 osw.close();
    	 baos.close();

    	 try {
    		 fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
    	 } catch( UnavailableServiceException e ) {
    		 fss = null;
    	 }
    	 
         if (fss != null) {
        	 ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
             fileContents = fss.saveFileDialog(null, null, bais, "export.fasta");
             bais.close();
             OutputStream os = fileContents.getOutputStream(true);
             os.write( baos.toByteArray() );
             os.close();
         } else {
        	 JFileChooser jfc = new JFileChooser();
        	 if( jfc.showSaveDialog( JavaFasta.this ) == JFileChooser.APPROVE_OPTION ) {
        		 File f = jfc.getSelectedFile();
        		 FileOutputStream fos = new FileOutputStream( f );
        		 fos.write( baos.toByteArray() );
        		 fos.close();
        		 
        		 Desktop.getDesktop().browse( f.toURI() );
        	 }
         }

         /*if (fileContents != null) {
             try {
            	 OutputStream os = fileContents.getOutputStream( true );
            	 OutputStreamWriter	osw = new OutputStreamWriter( os );
            	 
            	 int[] rr = table.getSelectedRows();
            	 for( int r : rr ) {
            		 int i = table.convertRowIndexToModel( r );
            		 Sequence seq = lseq.get(i);
            		 osw.write( ">" + seq.name + "\n" );
            		 int val = 0;
            		 while( val < seq.getLength() ) {
            			 osw.write( seq.sb.substring(val, Math.min( seq.getLength(), val+70 )) + "\n" );
            			 val += 70;
            		 }
            	 }
             } catch (IOException exc) {
            	 exc.printStackTrace();
             }
         }*/
	}
	
	public void exportAnnotationFasta( JTable table, List<Annotation> lann ) throws IOException, UnavailableServiceException {
		FileSaveService fss = null;
        FileContents fileContents = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter	osw = new OutputStreamWriter( baos );
   	 
	   	int[] rr = table.getSelectedRows();
	   	for( int r : rr ) {
	   		int i = table.convertRowIndexToModel( r );
	   		Annotation ann = lann.get(i);
	   		osw.write( ">" + ann.name + "\n" );
	   		int val = ann.start;
	   		while( val < ann.stop ) {
	   			osw.write( ann.seq.sb.substring( val, Math.min( ann.stop, val+70 )) + "\n" );
	   			val += 70;
	   		}
	   	}
	   	osw.close();
	   	baos.close();
	
	   	try {
	   		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
	   	} catch( UnavailableServiceException e ) {
	   		fss = null;
	   	}
	   	 
	    if (fss != null) {
	   	 		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		        fileContents = fss.saveFileDialog(null, null, bais, "exportannotation.fasta");
		        bais.close();
		        OutputStream os = fileContents.getOutputStream(true);
		        os.write( baos.toByteArray() );
		        os.close();
	    } else {
		   	 JFileChooser jfc = new JFileChooser();
		   	 if( jfc.showSaveDialog( JavaFasta.this ) == JFileChooser.APPROVE_OPTION ) {
		   		 File f = jfc.getSelectedFile();
		   		 FileOutputStream fos = new FileOutputStream( f );
		   		 fos.write( baos.toByteArray() );
		   		 fos.close();
		   		 
		   		 Desktop.getDesktop().browse( f.toURI() );
		   	 }
	    }

        /*if (fileContents != null) {
            try {
           	 OutputStream os = fileContents.getOutputStream( true );
           	 OutputStreamWriter	osw = new OutputStreamWriter( os );
           	 
           	 int[] rr = table.getSelectedRows();
           	 for( int r : rr ) {
           		 int i = table.convertRowIndexToModel( r );
           		 Sequence seq = lseq.get(i);
           		 osw.write( ">" + seq.name + "\n" );
           		 int val = 0;
           		 while( val < seq.getLength() ) {
           			 osw.write( seq.sb.substring(val, Math.min( seq.getLength(), val+70 )) + "\n" );
           			 val += 70;
           		 }
           	 }
            } catch (IOException exc) {
           	 exc.printStackTrace();
            }
        }*/
	}
	
	public void console( String str ) {
		JSObject jso = JSObject.getWindow(this);
		JSObject console = (JSObject)jso.getMember("console");
		console.call("log", new Object[] {str});
	}
	
	Map<String,Sequence>	mseq;
	ArrayList<Sequence>		lseq;
	ArrayList<Annotation>	lann;
	Map<String,Annotation>	mann;
	JTable			table;
	FastaView		c;
	Overview		overview;
	int				max = 0;
	int				min = 0;
	
	JTable			atable;
	
	public byte[] getByteArray( int len ) {
		return new byte[ len ];
	}
	
	public void addAbiSequence( String name, InputStream is ) {
		Ab1Reader abi = new Ab1Reader( is );
		Sequence s = new Sequence( name );
		s.append( abi.getSequence() );
		lseq.add( s );
		
		if( s.getLength() > max ) max = s.getLength();
	}
	
	public void addAbiSequence( String name, byte[] bts, int len ) {
		//byte[] ba = bts.getBytes();
		console( "in "+bts.length );
		ByteBuffer bb = ByteBuffer.wrap( bts );
		try {
			Ab1Reader abi = new Ab1Reader( bb );
			Sequence s = new Sequence( name );
			s.append( abi.getSequence() );
			lseq.add( s );
			
			if( s.getLength() > max ) max = s.getLength();
		} catch( Exception e ) {
			console( e.getMessage() );
		}
	}
	
	public void updateView() {
		table.tableChanged( new TableModelEvent( table.getModel() ) );
		atable.tableChanged( new TableModelEvent( atable.getModel() ) );
		c.updateCoords();
	}
	
	public void init() {
		initGui( this );
	}
	
	int[]	currentRowSelection;
	Point	p;
	public void initGui( Container cnt ) {
		final String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		try {
			UIManager.setLookAndFeel(lof);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			frame.setResizable(true);
		}

		lseq = new ArrayList<Sequence>();
		mseq = new HashMap<String,Sequence>();
		lann = new ArrayList<Annotation>();
		mann = new HashMap<String,Annotation>();
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		
		table.setDragEnabled( true );
		
		final Ruler ruler = new Ruler();
		c = new FastaView( table.getRowHeight(), ruler, table );
		
		c.addMouseListener( new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				p = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		c.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point np = e.getPoint();
				
				Rectangle r = c.getVisibleRect();
				r.translate(p.x-np.x, p.y-np.y);
				c.scrollRectToVisible( r );
				
				p = np;
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
		});
		
		//final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return lseq.size();
			}

			@Override
			public int getColumnCount() {
				return 5;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Name";
				else if( columnIndex == 1 ) return "Length";
				else if( columnIndex == 2 ) return "Start";
				else if( columnIndex == 3 ) return "RevComp";
				else if( columnIndex == 4 ) return "GC%";
				return null;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return String.class;
				else if( columnIndex == 1 ) return Integer.class;
				else if( columnIndex == 2 ) return Integer.class;
				else if( columnIndex == 3 ) return Integer.class;
				else if( columnIndex == 4 ) return Integer.class;
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Sequence seq = lseq.get( rowIndex );
				if( columnIndex == 0 ) return seq.getName();
				else if( columnIndex == 1 ) return seq.getLength();
				else if( columnIndex == 2 ) return seq.getStart();
				else if( columnIndex == 3 ) return seq.getRevComp();
				else if( columnIndex == 4 ) return seq.getGCP();
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
		});
		
		table.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				c.repaint();
				overview.reval();
				overview.repaint();
			}
		});
		
		JSplitPane	splitpane = new JSplitPane();
		splitpane.setBackground( Color.white );
		
		JScrollPane	fastascroll = new JScrollPane( c );
		fastascroll.setBackground( Color.white );
		fastascroll.getViewport().setBackground( Color.white );
		fastascroll.setRowHeaderView( table );
		fastascroll.setColumnHeaderView( ruler );
		
		JScrollPane	tablescroll = new JScrollPane();
		tablescroll.setViewport( fastascroll.getRowHeader() );
		tablescroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		tablescroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		tablescroll.setBackground( Color.white );
		tablescroll.getViewport().setBackground( Color.white );
		
		try {
			final DataFlavor df = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( df ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<Sequence>	selseq = new ArrayList<Sequence>( rr.length );
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( lseq.get(i) );
						}
						return selseq;
					} else {
						String ret = "";//makeCopyString();
						//return arg0.getReaderForText( this );
						return new ByteArrayInputStream( ret.getBytes( charset ) );
					}
					//return ret;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { df };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					if( arg0.equals(df) ) {
						return true;
					}
					return false;
				}
			};
			
			TransferHandler th = new TransferHandler() {
				private static final long serialVersionUID = 1L;
				
				public int getSourceActions(JComponent c) {
					return TransferHandler.COPY_OR_MOVE;
				}

				public boolean canImport(TransferHandler.TransferSupport support) {					
					return true;
				}

				protected Transferable createTransferable(JComponent c) {
					currentRowSelection = table.getSelectedRows();
					
					return transferable;
				}

				public boolean importData(TransferHandler.TransferSupport support) {
					try {
						System.err.println( table.getSelectedRows().length );
						
						if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
							Object obj = support.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
							//InputStream is = (InputStream)obj;
							List<File>	lfile = (List<File>)obj;
							
							for( File f : lfile ) {
								String fname = f.getName();
								if( fname.endsWith(".ab1") ) {
									int flen = (int)f.length();
									ByteBuffer bb = ByteBuffer.allocate( flen );
									FileInputStream fis = new FileInputStream( f );
									fis.read( bb.array() );
									Ab1Reader abi = new Ab1Reader( bb );
									Sequence s = new Sequence( f.getName() );
									s.append( abi.getSequence() );
									lseq.add( s );
									
									if( s.getLength() > max ) max = s.getLength();
									
									bb.clear();
								} else if( fname.endsWith(".blastout") ) {
									FileReader fr = new FileReader( f );
									BufferedReader br = new BufferedReader( fr );
									String line = br.readLine();
									
									Map<String,Integer>	hitmap = new HashMap<String,Integer>();
									while( line != null ) {
										if( line.startsWith(">") ) {
											System.err.println( line );
											String val = line.substring(2);
											
											System.err.println( line );
											
											line = br.readLine();
											//boolean erm = true;
											//while( erm && !line.startsWith(">") && !line.startsWith("Query=") ) {
											while( line != null && !line.startsWith(">") && !line.startsWith("Query=") ) {
												String trim = line.trim();
												if( trim.startsWith("Score") ) {
													String end = trim.substring(trim.length()-3);
													if( end.equals("0.0") ) {
														if( hitmap.containsKey( val ) ) {
															hitmap.put( val, hitmap.get(val)+1 );
														} else {
															hitmap.put( val, 1 );
														}
													}
												}
												
												line = br.readLine();
											}
										} else {
											line = br.readLine();
										}
									}
									br.close();
									fr = new FileReader( f );
									br = new BufferedReader( fr );
									
									line = br.readLine();
									String query;
									Sequence tseq = null;
									int k = 0;
									while( line != null ) {
										if( line.startsWith( "Query=" ) ) {
											query = line.substring(7);
											if( mseq.containsKey( query ) ) {
												Sequence seq = mseq.get( query );
												int ind = lseq.indexOf( seq );
												if( ind >= k ) {
													tseq = seq;
													lseq.remove( seq );
													lseq.add(k++, seq);
												}
											}
										} else if( line.startsWith(">") ) {
											int qstart = -1;
											int qstop = 0;
											int sstart = -1;
											int sstop = 0;
											
											String val = line.substring(2);//line.split("[\t ]+")[1];
											line = br.readLine();
											Sequence seq = null;
											int ind = -1;
											while( !line.startsWith(">") && !line.startsWith("Query=") ) {
												String trim = line.trim();
												if( trim.startsWith("Score") ) {
													String end = trim.substring(trim.length()-3);
													if( end.equals("0.0") && hitmap.get(val) == 1 ) {
														if( mseq.containsKey( val ) ) {
															seq = mseq.get( val );
															ind = lseq.indexOf( seq );
															if( ind >= k ) {
																lseq.remove( seq );
																lseq.add(k, seq);
															}/* else {											
																seq = null;
															}*/
														}
													} else {
														break;
													}
												} else if( trim.startsWith("Query") ) {
													int li = trim.lastIndexOf(' ');
													int qtmp = Integer.parseInt( trim.substring( li+1 ) );
													qstop = qtmp;
													if( qstart == -1 ) {
														qtmp = Integer.parseInt( trim.split("[ ]+")[1] );
														qstart = qtmp;
													}
												} else if( trim.startsWith("Sbjct") ) {
													int li = trim.lastIndexOf(' ');
													int stmp = Integer.parseInt( trim.substring( li+1 ) );
													sstop = stmp;
													if( sstart == -1 ) {
														stmp = Integer.parseInt( trim.split("[ ]+")[1] );
														sstart = stmp;
													}
												}
												line = br.readLine();
											}
											
											if( ind != -1 ) {
												if( ind >= k ) {
													if( sstart > sstop ) {
														seq.revcomp = 2;
														int sval = qstart-(seq.getLength()-sstart+1);
														seq.setStart( sval );
													} else {
														seq.setStart( qstart-sstart );
													}
													k++;
												} else if( tseq != null ) {
													if( sstart > sstop ) {
														if( seq.revcomp == 2 ) {
															int sval = (seq.getLength()-sstart+1)-qstart  +  seq.getStart();
															tseq.setStart( sval );
														} else {
															tseq.revcomp = 2;
															int sval = sstart-qstart  +  seq.getStart();
															tseq.setStart( sval );
														}
													} else {
														if( seq.revcomp == 2 ) {
															tseq.revcomp = 2;
															int sval = sstart-qstart  +  seq.getStart();
															tseq.setStart( sval );
														} else {
															tseq.setStart( sstart-qstart  +  seq.getStart() );
														}
													}
												}
											}
											
											continue;
										}
										
										line = br.readLine();
									}
									br.close();
									
									updateView();
								} else {
									BufferedReader	br = new BufferedReader( new FileReader( f ) );
									importReader( br );
									/*String line = br.readLine();
									while( line != null ) {
										if( line.startsWith(">") ) {
											if( s != null ) {
												if( s.getEnd() > max ) max = s.getEnd();
											}
											s = new Sequence( line.substring(1) );
											lseq.add( s );
										} else if( s != null ) {
											int start = 0;
											int i = line.indexOf(' ');
											while( i != -1 ) {
												String substr = line.substring(0, i);
												s.append( substr );
												start = i+1;
												i = line.indexOf(' ', start);
											}
											s.append( line.substring(start, i) );
										}
										line = br.readLine();
									}
									br.close();
									
									if( s != null ) {
										if( s.getLength() > max ) max = s.getLength();
									}*/
								}
							}
							
							updateView();
							
							return true;
						} else if( support.isDataFlavorSupported( df ) ) {							
							Object obj = support.getTransferable().getTransferData( df );
							ArrayList<Sequence>	seqs = (ArrayList<Sequence>)obj;
							
							ArrayList<Sequence> newlist = new ArrayList<Sequence>( lseq.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( lseq.get(i) );
							}
							lseq.clear();
							lseq = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							lseq.removeAll( seqs );
							for( Sequence s : seqs ) {
								lseq.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							c.repaint();
							
							return true;
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			};
			tablescroll.setTransferHandler( th );
			table.setTransferHandler( th );
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		
		JTextField	textfield = new JTextField();
		JComponent tablecomp = new JComponent() { private static final long serialVersionUID = 1L; };
		tablecomp.setLayout( new BorderLayout() );
		tablecomp.add( tablescroll );
		tablecomp.add( textfield, BorderLayout.SOUTH );
		
		splitpane.setLeftComponent( tablecomp );
		splitpane.setRightComponent( fastascroll );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Join") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				Set<Sequence>	remseq = new HashSet<Sequence>();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = lseq.get(i);
					remseq.add( seq );
				}
				
				Sequence newseq = new Sequence("newseq");
				int start = Integer.MAX_VALUE;
				int end = 0;
				for( Sequence s : remseq ) {
					if( s.getStart() < start ) start = s.getStart();
					if( s.getEnd() > end ) end = s.getEnd();
				}
				
				newseq.setStart( start );
				Set<Character>	charset = new TreeSet<Character>();
				for( int i = start; i < end; i++ ) {
					for( Sequence s : remseq ) {
						char c = s.charAt(i);
						if( c != '-' ) charset.add( Character.toUpperCase(c) );
					}
					
					if( charset.size() == 0 ) newseq.sb.append('-');
					else if( charset.size() == 1 ) newseq.sb.append( charset.iterator().next() );
					else if( charset.size() == 2 ) {
						if( charset.contains('A') || charset.contains('a') ) {
							
						}
						newseq.sb.append( charset.iterator().next() );
					} else if( charset.size() == 3 ) {
						newseq.sb.append( charset.iterator().next() );
					} else newseq.sb.append( 'N' );
					
					
					charset.clear();
				}
				
				lseq.removeAll( remseq );
				lseq.add( newseq );
				
				table.tableChanged( new TableModelEvent(table.getModel()) );
				c.repaint();
				overview.reval();
				overview.repaint();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Reverse") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = lseq.get( k );
					StringBuilder	sb = seq.sb;
					for( int i = 0; i < seq.getLength()/2; i++ ) {
						char c = sb.charAt(i);
						sb.setCharAt( i, sb.charAt(seq.getLength()-1-i) );
						sb.setCharAt( seq.getLength()-1-i, c );
					}
					if( seq.revcomp == 1 ) {
						seq.name = seq.name.substring(0, seq.name.length()-8);
						seq.revcomp = 0;
					} else if( seq.revcomp == 3 ) {
						seq.name = seq.name.substring(0, seq.name.length()-18)+"_compliment";
						seq.revcomp = 2;
					} else if( seq.revcomp == 2 ) {
						seq.name = seq.name.substring(0, seq.name.length()-11)+"_reversecompliment";
						seq.revcomp = 3;
					} else {
						seq.name = seq.name+"_reverse";
						seq.revcomp = 1;
					}
				}
				c.repaint();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});
		
		final Map<Character,Character>	complimentMap = new HashMap<Character,Character>();
		complimentMap.put( 'A', 'T' );
		complimentMap.put( 'T', 'A' );
		complimentMap.put( 'G', 'C' );
		complimentMap.put( 'C', 'G' );
		complimentMap.put( 'a', 't' );
		complimentMap.put( 't', 'a' );
		complimentMap.put( 'g', 'c' );
		complimentMap.put( 'c', 'g' );
		
		popup.add( new AbstractAction("Compliment") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = lseq.get( k );
					StringBuilder	sb = seq.sb;
					for( int i = 0; i < seq.getLength(); i++ ) {
						char c = sb.charAt(i);
						sb.setCharAt( i, complimentMap.get(c) );
					}
					if( seq.revcomp == 1 ) {
						seq.name = seq.name.substring(0, seq.name.length()-8)+"_reversecompliment";
						seq.revcomp = 3;
					} else if( seq.revcomp == 3 ) {
						seq.name = seq.name.substring(0, seq.name.length()-18)+"_reverse";
						seq.revcomp = 1;
					} else if( seq.revcomp == 2 ) {
						seq.name = seq.name.substring(0, seq.name.length()-11);
						seq.revcomp = 0;
					} else {
						seq.name = seq.name+"_compliment";
						seq.revcomp = 2;
					}
				}
				c.repaint();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});
		popup.add( new AbstractAction("UT Replacement") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = lseq.get( k );
					StringBuilder	sb = seq.sb;
					
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
				c.repaint();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					openFiles();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Export") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exportFasta( table, lseq );
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (UnavailableServiceException e1) {
					e1.printStackTrace();
				}
			}
		});
		table.setComponentPopupMenu( popup );
		tablescroll.setComponentPopupMenu( popup );
		
		table.addMouseListener( new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if( e.getClickCount() == 2 ) {
					int r = table.getSelectedRow();
					int i = table.convertRowIndexToModel( r );
					Sequence s = lseq.get( i );
					
					Rectangle rect = c.getVisibleRect();
					if( rect.x == (int)((s.getStart()-min)*c.cw) ) {
						rect.x = (int)((s.getEnd()-min)*c.cw)-rect.width;
					} else {
						rect.x = (int)((s.getStart()-min)*c.cw);
					}
					c.scrollRectToVisible( rect );
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		table.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					Set<Sequence>	delset = new HashSet<Sequence>();
					Set<Annotation>	adelset = new HashSet<Annotation>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel(r);
						Sequence seq = lseq.get(i);
						
						delset.add( seq );
						if( seq.annset != null ) adelset.addAll( seq.annset );
					}
					lseq.removeAll( delset );
					lann.removeAll( adelset );
					
					checkMaxMin();
					updateView();
				}
			}
		});
		
		overview = new Overview();
		JSplitPane	overviewsplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		overviewsplit.setTopComponent( splitpane );
		overviewsplit.setBottomComponent( overview );
		
		JTextField	asearch = new JTextField();
		atable = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String getToolTipText( MouseEvent me ) {
				//super.getToolTipText( me );
				Point p = me.getPoint();
				int y = p.y/atable.getRowHeight();
				
				int i = atable.convertRowIndexToModel( y );
				Annotation a = lann.get(i);
				
				if( a.desc != null ) return a.desc.toString();
				return a.name;
			}
		};
		atable.setToolTipText( "" );
		atable.setAutoCreateRowSorter( true );
		JScrollPane	ascroll = new JScrollPane( atable );
		JComponent	acomp = new JComponent() {};
		acomp.setLayout( new BorderLayout() );
		acomp.add( ascroll );
		acomp.add( asearch, BorderLayout.SOUTH );
		JSplitPane mainsplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		mainsplit.setLeftComponent( overviewsplit );
		mainsplit.setRightComponent( acomp );
		
		JPopupMenu	apopup = new JPopupMenu();
		atable.setComponentPopupMenu( apopup );
		apopup.add( new AbstractAction("Export Annotation") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exportAnnotationFasta( atable, lann );
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (UnavailableServiceException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		atable.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return lann.size();
			}

			@Override
			public int getColumnCount() {
				return 6;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Name";
				else if( columnIndex == 1 ) return "Contig";
				else if( columnIndex == 2 ) return "Type";
				else if( columnIndex == 3 ) return "Group";
				else if( columnIndex == 4 ) return "Start";
				else if( columnIndex == 5 ) return "Stop";
				else return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex > 3 ) return Integer.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Annotation ann = lann.get( rowIndex );
				if( columnIndex == 0 ) return ann.name+"_"+ann.group;
				else if( columnIndex == 1 ) return ann.name;
				else if( columnIndex == 2 ) return ann.type+"_"+ann.ori;
				else if( columnIndex == 3 ) return ann.group;
				else if( columnIndex == 4 ) return ann.start;
				else if( columnIndex == 5 ) return ann.stop;
				else return "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		setAnnotationTableTransferhandler( ascroll );
		
		atable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				if( e.getClickCount() == 2 ) {
					int r = atable.getSelectedRow();
					int i = atable.convertRowIndexToModel( r );
					if( i == -1 && r < atable.getRowCount() ) {
						i = r;
					}
					
					if( i != -1 ) {
						Annotation a = lann.get( i );
						
						i = lseq.indexOf( a.seq );
						int m = table.convertRowIndexToView( i );
						table.setRowSelectionInterval(m, m);
						
						Rectangle cellrect = table.getCellRect(m, 0, true);
						Rectangle rect = c.getVisibleRect();
						if( rect.x == (int)((a.getCoordStart()-min)*c.cw) ) {
							rect.x = (int)((a.getCoordEnd()-min)*c.cw-rect.width);
						} else {
							rect.x = (int)((a.getCoordStart()-min)*c.cw);
						}
						rect.y = cellrect.y;
						
						c.scrollRectToVisible( rect );
					}
				}
			}
		});
		
		atable.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					Set<Annotation>	delset = new HashSet<Annotation>();
					int[] rr = atable.getSelectedRows();
					for( int r : rr ) {
						int i = atable.convertRowIndexToModel(r);
						delset.add( lann.get(i) );
					}
					delset.removeAll( delset );
					
					updateView();
				}
			}
		});
		
		mainsplit.setBackground( Color.white );
		ascroll.getViewport().setBackground( Color.white );
		
		cnt.add( mainsplit );
	}
	
	public void setAnnotationTableTransferhandler( JScrollPane atablescroll ) {
		try {
			final DataFlavor df = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( df ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<Sequence>	selseq = new ArrayList<Sequence>( rr.length );
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( lseq.get(i) );
						}
						return selseq;
					} else {
						String ret = "";//makeCopyString();
						//return arg0.getReaderForText( this );
						return new ByteArrayInputStream( ret.getBytes( charset ) );
					}
					//return ret;
				}
	
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { df };
				}
	
				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					if( arg0.equals(df) ) {
						return true;
					}
					return false;
				}
			};
			
			TransferHandler th = new TransferHandler() {
				private static final long serialVersionUID = 1L;
				
				public int getSourceActions(JComponent c) {
					return TransferHandler.COPY_OR_MOVE;
				}
	
				public boolean canImport(TransferHandler.TransferSupport support) {					
					return true;
				}
	
				protected Transferable createTransferable(JComponent c) {
					currentRowSelection = table.getSelectedRows();
					
					return transferable;
				}
	
				public boolean importData(TransferHandler.TransferSupport support) {
					try {						
						if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
							Object obj = support.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
							//InputStream is = (InputStream)obj;
							List<File>	lfile = (List<File>)obj;
							
							for( File f : lfile ) {
								String fname = f.getName();
								if( fname.endsWith(".ab1") ) {
									int flen = (int)f.length();
									ByteBuffer bb = ByteBuffer.allocate( flen );
									FileInputStream fis = new FileInputStream( f );
									fis.read( bb.array() );
									Ab1Reader abi = new Ab1Reader( bb );
									Sequence s = new Sequence( f.getName() );
									s.append( abi.getSequence() );
									lseq.add( s );
									
									if( s.getLength() > max ) max = s.getLength();
									
									bb.clear();
								} else if( fname.endsWith(".blastout") ) {
									FileReader fr = new FileReader( f );
									BufferedReader br = new BufferedReader( fr );									
									String line = br.readLine();
									String query = null;
									String name = null;
									double	eval;
									Sequence tseq = null;
									int k = 0;
									while( line != null ) {
										if( line.startsWith( "Query=" ) ) {
											if( query != null ) {
												Sequence seq = null;
												for( String seqname : mseq.keySet() ) {
													int bil = seqname.indexOf(' ');
													if( query.contains( seqname.substring(0, bil) ) ) {
														seq = mseq.get(seqname);
														break;
													}
												}
												
												if( seq != null ) {
													Annotation a = new Annotation( seq, name, Color.red );
													a.desc = a.desc == null ? new StringBuilder( query ) : a.desc.append( query );
													String[]	mylla = query.split("#");
													if( mylla[3].trim().equals("-1") ) a.color = Color.green;
													int start = Integer.parseInt( mylla[1].trim() );
													int stop = Integer.parseInt( mylla[2].trim() );
													a.start = start;
													a.stop = stop;
													lann.add( a );
													mann.put( name, a );
												}
											}
											query = line.substring(7);
										} else if( line.startsWith(">") ) {
											name = line;
										} else {
											String[] str = line.split("[ ]+");
											eval = -1.0;
											try {
												eval = Double.parseDouble( str[str.length-1] );
											} catch( Exception e ) {
												e.printStackTrace();
											}
										}
										
										line = br.readLine();
									}
									br.close();
									
									atable.tableChanged( new TableModelEvent( atable.getModel() ) );
									for( Sequence seq : lseq ) {
										if( seq.annset != null ) Collections.sort( seq.annset );
									}
								} else {
									Annotation a = null;
									BufferedReader	br = new BufferedReader( new FileReader( f ) );
									String line = br.readLine();
									while( line != null ) {
										if( line.startsWith(">") ) {
											String name = line.substring(1);
											
											Sequence theseq = null;
											for( String seqname : mseq.keySet() ) {
												if( name.contains( seqname.split(" ")[0] ) ) {
													theseq = mseq.get( seqname );
													break;
												}
											}
											
											if(  theseq != null ) {
												a = new Annotation( theseq, name, Color.red );
												String[]	mylla = name.split("#");
												if( mylla[3].trim().equals("-1") ) a.color = Color.green;
												int start = Integer.parseInt( mylla[1].trim() );
												int stop = Integer.parseInt( mylla[2].trim() );
												a.start = start;
												a.stop = stop;
												lann.add( a );
												mann.put( name, a );
											}
										} else if( a != null ) {
											a.append( line );
										}
										line = br.readLine();
									}
									br.close();
								}
							}
							
							atable.tableChanged( new TableModelEvent( atable.getModel() ) );
							//c.updateCoords();
							
							return true;
						} else if( support.isDataFlavorSupported( df ) ) {						
							Object obj = support.getTransferable().getTransferData( df );
							ArrayList<Sequence>	seqs = (ArrayList<Sequence>)obj;
							ArrayList<Sequence> newlist = new ArrayList<Sequence>( lseq.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( lseq.get(i) );
							}
							lseq.clear();
							lseq = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							lseq.removeAll( seqs );
							for( Sequence s : seqs ) {
								lseq.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							c.repaint();
							
							return true;
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			};
			atablescroll.setTransferHandler( th );
			atable.setTransferHandler( th );
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
	}
	
	public void checkMaxMin() {
		int lmin = Integer.MAX_VALUE;
		int lmax = 0;
		
		for( Sequence s : lseq ) {
			if( s.getEnd() > lmax ) lmax = s.getEnd();
			if( s.getStart() < lmin ) lmin = s.getStart();
		}
		
		if( lmin < min ) min = lmin;
		if( lmax > max ) max = lmax;
		
		if( lmin < min || lmax > max ) {	
			table.tableChanged( new TableModelEvent(table.getModel()) );
			c.repaint();
		}
	}
	
	public static void main(String[] args) {
		JFrame	frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JavaFasta	jf = new JavaFasta();
		jf.initGui( frame );
		
		frame.setVisible( true );
	}
}
