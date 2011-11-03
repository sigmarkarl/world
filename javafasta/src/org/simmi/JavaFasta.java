package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
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

import netscape.javascript.JSObject;

public class JavaFasta extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class Ruler extends JComponent {
		public Ruler() {
			super();
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			int h = this.getHeight();
			Rectangle r = g2.getClipBounds();
			
			for( int x = r.x/10; x < (r.x+r.width)/10+1; x++ ) {
				int xx = x*10;
				if( x % 10 == 0 ) {
					g.drawLine(xx+4, h-6, xx+4, h);
					g.drawString( x+"", xx, h-6);
				} else if( x % 5 == 0 ) {
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
	
	public class FastaView extends JComponent {
		Ruler			ruler;
		JTable			table;
		List<Sequence>	lseq;
		int				max = 0;
		int				rh;
		
		public FastaView( List<Sequence> lseq, int rh, Ruler ruler, JTable table ) {
			this.lseq = lseq;
			this.rh = rh;
			this.ruler = ruler;
			this.table = table;
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			Rectangle r = g2.getClipBounds();
			
			int xmin = r.x/10;
			int xmax = Math.min( (r.x+r.width)/10+1, max );
			for( int y = r.y/rh; y < Math.min( (r.y+r.height)/rh+1, lseq.size() ); y++ ) {
				int i = table.convertRowIndexToModel( y );
				Sequence seq = lseq.get( i );
				for( int x = Math.max(seq.start, xmin); x < Math.min(seq.getEnd(), xmax); x++ ) {
					g.drawString(seq.charAt(x), x*10, y*rh+rh-2);
				}
			}
		}
		
		public int getMax() {
			return max;
		}
		
		public void updateCoords( int max ) {
			this.max = Math.max( this.max, max );
			
			int w = max*10;
			int h = lseq.size()*16;
			
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
	
	public class Sequence {
		String 			name;
		StringBuilder 	sb = new StringBuilder();
		int				start = 0;
		
		public Sequence( String name ) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void append( String str ) {
			sb.append( str );
		}
		
		public String charAt( int i ) {
			return Character.toString( sb.charAt( i-start ) );
		}
		
		public int getLength() {
			return sb.length();
		}
		
		public int getStart() {
			return start;
		}
		
		public int getEnd() {
			return start+sb.length();
		}
	}
	
	public void openFiles() {
		FileOpenService fos; 

	    try { 
	        fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService"); 
	    } catch (UnavailableServiceException e) {
	        fos = null; 
	    }
	    
	    if (fos != null) {
	        try {
	            FileContents[] fcs = fos.openMultiFileDialog(null, null);
	            for( FileContents fc : fcs ) {
	            	String name = fc.getName();
	            	if( name.endsWith(".ab1") || name.endsWith(".abi") ) addAbiSequence( name, fc.getInputStream() );
	            	else {
	            		Sequence s = null;
						BufferedReader	br = new BufferedReader( new InputStreamReader( fc.getInputStream() ) );
						String line = br.readLine();
						while( line != null ) {
							if( line.startsWith(">") ) {
								if( s != null ) {
									if( s.getEnd() > max ) max = s.getEnd();
								}
								s = new Sequence( line.substring(1) );
								lseq.add( s );
							} else if( s != null ) {
								s.append( line );
							}
							line = br.readLine();
						}
						br.close();
						
						if( s != null ) {
							if( s.getLength() > max ) max = s.getLength();
						}
	            	}
	            }
	        } catch (Exception e) { 
	        	console( e.getMessage() );
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

    	 fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
         if (fss != null) {
        	 ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
             fileContents = fss.saveFileDialog(null, null, bais, "export.fasta");
             bais.close();
             OutputStream os = fileContents.getOutputStream(true);
             os.write( baos.toByteArray() );
             os.close();
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
	
	List<Sequence>	lseq;
	JTable			table;
	FastaView		c;
	int				max = 0;
	
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
		c.updateCoords( max );
	}
	
	public void init() {
		initGui( this );
	}
	
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
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		final Ruler ruler = new Ruler();
		c = new FastaView( lseq, table.getRowHeight(), ruler, table );
		
		final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
		final String charset = df.getParameter("charset");
		final Transferable transferable = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
				String ret = "";//makeCopyString();
				//return arg0.getReaderForText( this );
				return new ByteArrayInputStream( ret.getBytes( charset ) );
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
				return transferable;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				try {
					DataFlavor[] dfs = support.getDataFlavors();
					for( DataFlavor df : dfs ) {
						System.err.println( df );
					}
					
					if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
						Object obj = support.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
						//InputStream is = (InputStream)obj;
						List<File>	lfile = (List<File>)obj;
						
						max = c.getMax();
						for( File f : lfile ) {
							if( f.getName().endsWith(".ab1") ) {
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
							} else {
								Sequence s = null;
								BufferedReader	br = new BufferedReader( new FileReader( f ) );
								String line = br.readLine();
								while( line != null ) {
									if( line.startsWith(">") ) {
										if( s != null ) {
											if( s.getEnd() > max ) max = s.getEnd();
										}
										s = new Sequence( line.substring(1) );
										lseq.add( s );
									} else if( s != null ) {
										s.append( line );
									}
									line = br.readLine();
								}
								br.close();
								
								if( s != null ) {
									if( s.getLength() > max ) max = s.getLength();
								}
							}
						}
						
						table.tableChanged( new TableModelEvent( table.getModel() ) );
						c.updateCoords( max );
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return lseq.size();
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Name";
				else if( columnIndex == 1 ) return "Length";
				return null;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return String.class;
				else if( columnIndex == 1 ) return Integer.class;
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
		tablescroll.setTransferHandler( th );
		
		JTextField	textfield = new JTextField();
		JComponent tablecomp = new JComponent() { private static final long serialVersionUID = 1L; };
		tablecomp.setLayout( new BorderLayout() );
		tablecomp.add( tablescroll );
		tablecomp.add( textfield, BorderLayout.SOUTH );
		
		splitpane.setLeftComponent( tablecomp );
		splitpane.setRightComponent( fastascroll );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFiles();
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
		
		table.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					Set<Sequence>	delset = new HashSet<Sequence>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel(r);
						delset.add( lseq.get(i) );
					}
					lseq.removeAll( delset );
					
					max = checkMax();
					table.tableChanged( new TableModelEvent( table.getModel() ) );
				}
			}
		});
		
		cnt.add( splitpane );
	}
	
	public int checkMax() {
		int max = 0;
		
		for( Sequence s : lseq ) {
			if( s.getEnd() > max ) max = s.getEnd();
		}
		
		return max;
	}
	
	public static void main(String[] args) {
		JFrame	frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JavaFasta	jf = new JavaFasta();
		jf.initGui( frame );
		
		frame.setVisible( true );
	}
}
