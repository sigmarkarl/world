package org.simmi.javafasta.unsigned;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import breeze.linalg.DenseMatrix;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.simmi.ann.ANIResult;
import org.simmi.ann.ANIScore;
import org.simmi.javafasta.shared.*;
import org.simmi.treedraw.shared.TreeUtil;

import flobb.ChatServer;
/*import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;*/
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JavaFasta extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel	parentApplet = JavaFasta.this;
	Container	currentCnt;
	Serifier	serifier = null;
	
	//ClipboardService clipboardService;
	boolean grabFocus;
	
	JSplitPane	mainsplit;
	JSplitPane	splitpane;
	JSplitPane	overviewsplit;
	
	boolean		collapseView = false;
	boolean		edited = false;
	
	Path		currentPath = null;
	public static String		user;
	
	public void setCurrentPath( Path cp ) {
		currentPath = cp;
	}
	
	public Path getCurrentPath() {
		return currentPath;
	}

	public static Map<String,Integer> getBlosumMap() {
		return getBlosumMap( true );
	}
	
	public static Map<String,Integer> getBlosumMap( boolean includeDash ) {
		Map<String,Integer> blosumap = new ConcurrentHashMap<>();
		InputStream is = JavaFasta.class.getResourceAsStream("/BLOSUM62");
		if( is != null ) {
			InputStreamReader ir = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(ir);
			String[] abet = null;
			//int i = 0;
			try {
				String line = br.readLine();
				while (line != null) {
					if (line.charAt(0) != '#') {
						String[] split = line.trim().split("[ ]+");
						char chr = line.charAt(0);
						if (chr == ' ') {
							abet = split;
							if (includeDash) abet[abet.length - 1] = "-";
						} else {
							if (includeDash && chr == '*') chr = '-';
							int k = 0;
							for (String a : abet) {
								blosumap.put(chr + a, Integer.parseInt(split[++k]));
							}
						}
					}
					line = br.readLine();
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		blosumap.remove("--");
		return blosumap;
	}

	public void copyData(Component source) throws IOException {
		//JTextArea textarea = (JTextArea) source;
		//String s = textarea.getText();
		boolean fasta = true;
		
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter( baos );
		
		List<Sequence> selseqs = new ArrayList<>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			selseqs.add( serifier.lseq.get(i) );
		}
		if( fasta ) serifier.writeFasta(selseqs, osw, getSelectedRect());
		else {
			for( Sequence seq : selseqs ) {
				int val = 0;
		   		int end = seq.length();
		   		 
		   		if( val <= end ) osw.write( seq.getName() + "\t" + seq.getSequence() + "\n" );
		   		/*while( val < end ) {
		   			 osw.write( seq.sb.substring(val, Math.min( end, val+70 )) + "\n" );
		   			 val += 70;
		   		}*/
			}
		}
		osw.close();
		baos.close();

		String s = baos.toString();
		if (s == null || s.trim().length() == 0) {
			JOptionPane.showMessageDialog(source, "There is no data selected!");
		} else {
			StringSelection selection = new StringSelection(s);
			//if( clipboardService != null ) clipboardService.setContents(selection);
			//else
		 	Toolkit.getDefaultToolkit().getSystemClipboard().setContents( selection, null );
		}

		if (grabFocus) {
			source.requestFocus();
		}
	}

	class CopyAction extends AbstractAction {
		public CopyAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				copyData((Component) e.getSource());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void setParentApplet( JPanel applet ) {
		parentApplet = applet;
	}
		
	public JavaFasta() {
		this.serifier = new Serifier();
	}
	
	public JavaFasta( Serifier serifier ) { this.serifier = serifier; }
	
	public List<Sequence> getEditedSequences() {
		List<Sequence>	es = new ArrayList<>();
		for( Sequence s : serifier.lseq ) {
			if( s.isEdited() ) es.add( s );
		}
		
		return es;
	}
	
	public boolean isEdited() {
		if( edited ) return true;
		for( Sequence s : serifier.lseq ) {
			if( s.isEdited() ) return true;
		}
		return false;
	}
	
	public Serifier getSerifier() {
		return serifier;
	}
	
	public void setSerifier( Serifier s ) {
		this.serifier = s;
	}
	
	/*public void checkMaxMin() {
		serifier.checkMaxMin();
		if( lmin < getMin() || lmax > getMax() ) {	
			table.tableChanged( new TableModelEvent(table.getModel()) );
			c.repaint();
		}
	}*/
	
	ChatServer cs;
	public JavaFasta( JPanel parentApplet, Serifier serifier, ChatServer cs ) {
		this( serifier );
		if( parentApplet != null ) this.parentApplet = parentApplet;
		this.cs = cs;
	}
	
	public JavaFasta( JPanel parentApplet, Serifier serifier ) {
		this( parentApplet, serifier, null );
	}
	
	public void selectAll() {
		if( table != null ) table.selectAll();
	}
	
	public void setStatus() {
		int r = table.getSelectedRow();
		if( r != -1 ) {
			int tr = table.convertRowIndexToModel( r );
			
			if( tr >= 0 && tr < serifier.lseq.size() ) {
				Sequence s = serifier.lseq.get(tr);
				
				int start = 0;
				int stop = -1;
				
				int rs = s.getRealStart();
				while( rs < c.selectedRect.x ) {
					if( s.getCharAt( rs ) != '-' ) start++;
					rs++;
				}
				stop = start;
				while( rs < c.selectedRect.x+c.selectedRect.width ) {
					if( s.getCharAt( rs ) != '-' ) stop++;
					rs++;
				}
				
				if( stop >= start ) status.setText( start + "-" + stop );
			}
		}
	}

	public class Ruler extends JComponent {
		int 	x;
		double	cw;
		
		public Ruler( double lcw ) {
			super();
			
			this.cw = lcw;
			final JPopupMenu	popup = new JPopupMenu();
			popup.add( new AbstractAction("Start here") {
				@Override
				public void actionPerformed(ActionEvent e) {	
					int xval = (int)(x/cw)+serifier.getMin();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = serifier.lseq.get(i);
						s.setStart( xval );
					}
					
					serifier.checkMaxMin();		
					c.repaint();
					overview.reval();
					overview.repaint();
					updateView();
				}
			});
			popup.add( new AbstractAction("End here") {
				@Override
				public void actionPerformed(ActionEvent e) {
					int xval = (int)(x/cw)+serifier.getMin();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = serifier.lseq.get(i);
						s.setEnd( xval );
					}
					
					serifier.checkMaxMin();		
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
						Sequence s = serifier.lseq.get(i);
						if( s.getStart() < min ) min = s.getStart();
					}
					
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = serifier.lseq.get(i);
						s.setStart( xval+(s.getStart()-min) );
					}
					
					serifier.checkMaxMin();		
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
						Sequence s = serifier.lseq.get(i);
						if( s.getEnd() > max ) max = s.getEnd();
					}
					
					for( int r : rr ) {
						int i = table.convertRowIndexToModel( r );
						Sequence s = serifier.lseq.get(i);
						s.setEnd( xval-(max-s.getEnd()) );
					}
					
					serifier.checkMaxMin();		
					c.repaint();
					overview.reval();
					overview.repaint();
					updateView();
				}
			});
			popup.addSeparator();
			popup.add( new AbstractAction("Delete") {
				@Override
				public void actionPerformed(ActionEvent e) {
					for( Sequence s : serifier.lseq ) {
						int begin = c.selectedRect.x-s.getStart();
						int stop = begin+c.selectedRect.width;
						
						int start = Math.max(begin, 0);
						int end = Math.min(stop, s.length());
						if( end > start ) {
							s.getStringBuilder().delete( start, end );
						}
						s.setStart( s.getStart()-Math.max( 0, Math.min( c.selectedRect.width, -begin ) ) );
						
					}
					//max -= c.selectedRect.width;
					c.repaint();
				}
			});
			popup.add( new AbstractAction("Sort") {
				@Override
				public void actionPerformed(ActionEvent e) {
					//DefaultRowSorter<TableModel, String>	erm = 
					RowSorter<? extends TableModel>		rs = table.getRowSorter();
					TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)rs;
					
					//DefaultRowSorter<>	erm = (DefaultRowSorter<>)rs;
					//List<SortKey>	lsortkey = new ArrayList<SortKey>();
					//lsortkey.add( new SortKey() );
					//rs.setSortKeys(keys);
				}
			});
			
			this.setComponentPopupMenu( popup );
			
			this.addMouseListener( new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {
					p = e.getPoint();
					
					if( e.isShiftDown() ) {
						//c.selectedRect.x = (int)(p.x/c.cw);
						//c.selectedRect.y = 0;
						int oldpx = (int)(c.selectedRect.x*c.cw);
						c.selectedRect.width = (int)((p.x-oldpx)/c.cw)+1;
						//c.selectedRect.height = serifier.lseq.size();
						c.repaint();
					} else {
						c.selectedRect.x = (int)(p.x/c.cw);
						c.selectedRect.y = 0;
						c.selectedRect.width = 1;
						c.selectedRect.height = serifier.lseq.size();
						c.repaint();
					}
				}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
			this.addMouseMotionListener( new MouseMotionListener() {
				@Override
				public void mouseMoved(MouseEvent e) {
					x = e.getX();
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					Point np = e.getPoint();
					
					if( e.isShiftDown() ) {
						c.selectedRect.x = (int)(p.x/c.cw);
						c.selectedRect.y = 0;
						c.selectedRect.width = (int)((np.x-p.x)/c.cw)+1;
						c.selectedRect.height = serifier.lseq.size();
						
						setStatus();
						c.repaint();
					}
				}
			});
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			int h = this.getHeight();
			Rectangle r = g2.getClipBounds();
			
			int smin = serifier != null ? serifier.getMin() : 0;
			//double l = Math.log10( cw );
			//int dval = Math.min( 10, )
			for( int x = (int)(r.x/cw); x < (int)((r.x+r.width)/cw)+1; x++ ) {
				int xx = (int)(x*cw);
				int xm = x+smin;
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
				if( serifier.getMax() != serifier.getMin() && i < serifier.lseq.size() ) {
					Sequence s = serifier.lseq.get(i);
					int x = ((s.getRealStart()-serifier.getMin())*bi.getWidth())/serifier.getDiff();
					int y = (r*bi.getHeight())/serifier.lseq.size();
					bg.fillRect( x, y, Math.max(1, (int)( ((long)s.getRealLength()*(long)bi.getWidth())/(long)serifier.getDiff() )), Math.max(1, (bi.getHeight())/serifier.lseq.size()) );
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
	
	Set<Integer> filterset = new HashSet<Integer>();
	final RowFilter rowfilter = new RowFilter() {
		@Override
		public boolean include(Entry entry) {
			return filterset.isEmpty() || filterset.contains(entry.getIdentifier());
		}
	};
	
	public class FastaView extends JComponent implements KeyListener {
		Ruler			ruler;
		JTable			table;
		int				rh;
		double			cw;
		double			ch;
		boolean			basecolors = false;
		boolean			aacolors = false;
		Map<Character,Color>	ccol = new HashMap<Character,Color>();
		Rectangle		selectedRect = new Rectangle();
		Color			selectColor = new Color( 150,150,200,200 );
		
		public FastaView( int rh, Ruler ruler, JTable table ) {
			this.rh = rh;
			this.ruler = ruler;
			this.table = table;
			
			this.addKeyListener( this );
			
			cw = ruler.cw;
			ch = rh;
			
			this.setToolTipText(" ");
			
			ccol.put('A', Color.red);
			ccol.put('a', Color.red);
			ccol.put('G', Color.green);
			ccol.put('g', Color.green);
			ccol.put('T', Color.blue);
			ccol.put('t', Color.blue);
			ccol.put('U', Color.cyan);
			ccol.put('u', Color.cyan);
			ccol.put('C', Color.yellow);
			ccol.put('c', Color.yellow);
		}
		
		Annotation searchann = new Annotation( null, "search", null, serifier != null ? serifier.mann : null );
		public String getToolTipText( MouseEvent e ) {
			Point p = e.getPoint();
			
			int w = (int)(p.x/cw);
			int h = p.y/rh;
			
			if( h >= 0 && h < table.getRowCount() ) {
				int i = table.convertRowIndexToModel( h );
				Sequence seq = serifier.lseq.get( i );
				
				if( seq.getAnnotations() != null && w+serifier.getMin() >= seq.getStart() && w+serifier.getMin() <= seq.getEnd() ) { 
					searchann.start = (w+serifier.getMin()) - seq.getStart();
					int ai = Collections.binarySearch( seq.getAnnotations(), searchann );
					
					int ip = Math.abs(ai)-1;
					
					if( ip > 0 && ip <= seq.getAnnotations().size() ) {
						Annotation a = seq.getAnnotations().get( ip-1 );
						if( a.getCoordEnd() > w+serifier.getMin() ) {
							if( a.getGene() != null ) {
								GeneGroup gg = a.getGene().getGeneGroup();
								if( gg != null ) {
									return gg.getName();
								}
								return a.getGene().getName();
							}
							return a.getName();
						}
					}
				}
			}
			
			return null;
		}
		
		int prevx = -1;
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			if( serifier != null ) {
				Rectangle r = g2.getClipBounds();
				
				int i = 0;
				Rectangle vr = this.getVisibleRect();
				
				if( collapseView && prevx != vr.x ) {
					filterset.clear();
					
					/*Sequence prev = null;
					Sequence next = null;
					for( Sequence s : serifier.lgseq ) {
						if( (s.getStart()-serifier.getMin())*cw < vr.x+vr.width && (s.getEnd()-serifier.getMin())*cw > vr.x ) {
							filterset.add( s.index );
							List<Sequence> lgseq = serifier.gseq.get(s.name);
							if( lgseq != null ) for( Sequence ss : lgseq ) {
								if( (ss.getStart()-serifier.getMin())*cw < vr.x+vr.width && (ss.getEnd()-serifier.getMin())*cw > vr.x ) {
									filterset.add( ss.index );
								}
							}
						}
					}*/
					
					for( Sequence s : serifier.lseq ) {
						if( (s.getStart()-serifier.getMin())*cw < vr.x+vr.width && (s.getEnd()-serifier.getMin())*cw > vr.x ) {
							filterset.add( i );
						}
						i++;
					}
					
					
					
					DefaultRowSorter<TableModel, Integer> rs = (DefaultRowSorter<TableModel,Integer>)table.getRowSorter();
					rs.setRowFilter( null );
					rs.setRowFilter( rowfilter );
					updateCoords();
					
					prevx = vr.x;
				}
				
				int filtersize = filterset.size();
				if( filtersize == 0 ) filtersize = serifier.lseq.size();
				
				int xmin = (int)(r.x/cw);
				int xmax = Math.min( (int)((r.x+r.width)/cw)+1, serifier.getDiff() );
				for( int y = r.y/rh; y < Math.min( (r.y+r.height)/rh+1, filtersize ); y++ ) {
					if( y >= 0 && y < table.getRowCount() ) {
						i = table.convertRowIndexToModel( y );
						Sequence seq = serifier.lseq.get( i );
						//System.err.println( seq.getName() );
						if( seq.getAnnotations() != null ) {
							for( Annotation a : seq.getAnnotations() ) {
								g.setColor( (Color)a.color );
								for( int x = Math.max(a.getCoordStart()-serifier.getMin(), xmin); x < Math.min(a.getCoordEnd()-serifier.getMin(), xmax); x++ ) {
									g.fillRect((int)(x*cw), y*rh, (int)cw, rh);
									if( a.ori == -1 ) {
										g.setColor( Color.black );
										g.drawLine((int)(x*cw)+3, y*rh, (int)(x*cw), y*rh+3);
										g.setColor( (Color)a.color );
									}
								}
								//if( a.start > )
							}
						}
						
						if( cw > 5.0 ) {
							g.setColor( Color.black );
							if( basecolors ) {
								for( int x = Math.max(seq.getStart()-serifier.getMin(), xmin); x < Math.min(seq.getEnd()-serifier.getMin(), xmax); x++ ) {
									char ct = seq.getCharAt(x+serifier.getMin());
									
									Color col = ccol.get(ct);
									int startx = (int)(x*cw);
									int starty = (int)(y*rh);
									if( col != null ) g.setColor( col );
									else g.setColor( Color.white );
									g.fillRect( startx, starty, (int)cw, (int)rh );
									g.setColor( Color.black );
									g.drawString( Character.toString( ct ), startx, starty+rh-2);
								}
							} else if( aacolors ) {
								for( int x = Math.max(seq.getStart()-serifier.getMin(), xmin); x < Math.min(seq.getEnd()-serifier.getMin(), xmax); x++ ) {
									char ct = seq.getCharAt(x+serifier.getMin());
									
									Color col = Sequence.aacolor.get(ct);
									int startx = (int)(x*cw);
									int starty = (int)(y*rh);
									if( col != null ) g.setColor( col );
									else g.setColor( Color.white );
									g.fillRect( startx, starty, (int)cw, (int)rh );
									g.setColor( Color.black );
									g.drawString( Character.toString( ct ), startx, starty+rh-2);
								}
							} else {
								/*if( seq.getName().contains("SA") ) {
									System.err.println( "SA01 " + seq.sb.substring(0,10) );
									for( int k = 0; k < 10; k++ ) System.err.print( seq.getCharAt(serifier.getMin()+k) );
									System.err.println();
									for( int k = 0; k < 10; k++ ) System.err.print( seq.sb.getCharAt(serifier.getMin()+k-seq.offset) );
									System.err.println( " " + serifier.getMin() + "  " + seq.offset );
								}*/
								for( int x = Math.max(seq.getStart()-serifier.getMin(), xmin); x < Math.min(seq.getEnd()-serifier.getMin(), xmax); x++ ) {
									char ct = seq.getCharAt(x+serifier.getMin());
									g.drawString( Character.toString( ct ), (int)(x*cw), y*rh+rh-2);
								}
							}
						}
					}
				}
			}
			
			g.setColor( selectColor );
			g.fillRect( (int)(selectedRect.x*cw), selectedRect.y*rh, (int)(selectedRect.width*cw), selectedRect.height*rh );
		}
		
		public void updateCoords() {
			int w = (int)(serifier.getDiff()*cw);
			int h = (filterset.isEmpty() ? serifier.lseq.size() : filterset.size())*rh;
			
			this.setPreferredSize( new Dimension(w,h) );
			this.setSize(w, h);
			
			ruler.setPreferredSize( new Dimension(w, 20) );
			ruler.setSize(w, 20);
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();
			char keychar = e.getKeyChar();
			if( this.selectedRect != null && this.selectedRect.width > 0 ) {
				List<Sequence>	seq = new ArrayList<Sequence>();
				for( int i = Math.max(0, this.selectedRect.y); i < Math.min(table.getRowCount(),this.selectedRect.y+this.selectedRect.height); i++ ) {
					int r = table.convertRowIndexToModel(i);
					if( r != -1 ) {
						seq.add( serifier.lseq.get(r) );
					}
				}
				if( keycode == KeyEvent.VK_LEFT ) {
					for( Sequence s : seq ) {
						int i = this.selectedRect.x-s.getStart()-1;
						if( i >= 0 && i < s.getSequence().length() ) {
							s.getSequence().deleteCharAt( i );
							s.getSequence().insert( i+this.selectedRect.width, '-');
							s.edited = true;
						}
					}
					selectedRect.x--;
					c.repaint();
				} else if( keycode == KeyEvent.VK_RIGHT ) {
					for( Sequence s : seq ) {
						int i = this.selectedRect.x-s.getStart();
						if( i >= 0 && i < s.getSequence().length() ) {
							s.getSequence().insert( i, '-' );
							s.getSequence().deleteCharAt( i+this.selectedRect.width+1 );
							s.edited = true;
						}
					}
					selectedRect.x++;
					c.repaint();
				} else if( keycode == KeyEvent.VK_DELETE ) {
					for( Sequence s : seq ) {
						for( int i = Math.max(0, this.selectedRect.x-s.getStart()); i < Math.min(s.getSequence().length(),this.selectedRect.x+this.selectedRect.width-s.getStart()); i++ ) {
							s.getSequence().setCharAt(i, '-');
							s.edited = true;
							//s.sb.insert( i+this.selectedRect.width, '-');
						}
					}
					c.repaint();
					//selectedRect.x--;
				} else if( keycode == KeyEvent.VK_BACK_SPACE ) {
					int start = this.selectedRect.x;
					int end = this.selectedRect.x+this.selectedRect.width;
					for( Sequence s : seq ) {
						Set<Annotation> remset = new HashSet<Annotation>();
						if( s.getAnnotations() != null ) for( Annotation a : s.getAnnotations() ) {
							if( a.start < start ) {
								if( a.stop > start ) {
									a.stop = Math.max(start, a.stop-(end-start));
								}
							} else if( a.start < end ) {
								if( a.stop <= end ) {
									remset.add(a);
								} else {
									a.start = start;
									a.stop -= (end-start);
								}
							} else {
								a.start -= (end-start);
								a.stop -= (end-start);
							}
						}
						s.getAnnotations().removeAll( remset );
					}
					
					for( Sequence s : seq ) {
						start = Math.max(0, this.selectedRect.x-s.getStart() );
						end = Math.min( s.getSequence().length(), this.selectedRect.x+this.selectedRect.width-s.getStart() );
						
						if( end > start ) {
							s.getSequence().delete(start, end);
							s.edited = true;
						}
						/*Set<Annotation> remset = new HashSet<Annotation>();
						if( s.getAnnotations() != null ) for( Annotation a : s.getAnnotations() ) {
							if( a.start < start ) {
								if( a.stop > start ) {
									a.stop = Math.max(start, a.stop-(end-start));
								}
							} else if( a.start < end ) {
								if( a.stop <= end ) {
									remset.add(a);
								} else {
									a.start = start;
									a.stop -= (end-start);
								}
							} else {
								a.start -= (end-start);
								a.stop -= (end-start);
							}
						}
						s.getAnnotations().removeAll( remset );*/
					}
					//selectedRect.width;
					c.repaint();
				} else if( keycode == KeyEvent.VK_SPACE ) {
					int k = -1;
					int o = 1;
					while( o < 10 ) {
						char c = '-';
						for( Sequence s : seq ) {
							int i = this.selectedRect.x+serifier.min-s.getStart()+o;
							if( i >= 0 && i < s.getSequence().length() ) {
								c = s.getSequence().charAt(i);
								if( c != '-' ) break;
							}
						}
						if( c == '-' ) {
							k = o;
							break;
						}
						o++;
					}
					
					for( Sequence s : seq ) {
						int i = this.selectedRect.x+serifier.min-s.getStart();
						if( i >= 0 && i < s.getSequence().length() ) {
							if( k != -1 ) s.getSequence().delete(i+k, i+k+1);
							s.getSequence().insert( i, '-' );
							s.edited = true;
						}
					}
					selectedRect.x++;
					c.repaint();
				} else if( (keychar >= 'a' && keychar <= 'z') || (keychar >= 'A' && keychar <= 'Z') ) {
					for( Sequence s : seq ) {
						//System.err.println( s.getName() + "  " + s.getEnd() + "  " + serifier.min );
						for( int k = this.selectedRect.x; k < this.selectedRect.x+this.selectedRect.width; k++ ) {
							int i = k+serifier.min-s.getStart();
							if( i >= 0 && i < s.getSequence().length() ) {
								s.getSequence().replace(i, i+1, Character.toString(keychar) );
								s.edited = true;
							}
						}
					}
					c.repaint();
				} else if( keycode == KeyEvent.VK_ESCAPE ) {
					filterset.clear();
					c.updateCoords();
					table.tableChanged( new TableModelEvent(table.getModel()) );
				}
			}
		
			if( keycode == KeyEvent.VK_PLUS || keychar == '+' ) {
				cw *= 1.25;
				ch *= 1.25;
				rh = (int)ch;
				table.setRowHeight( rh );
				this.setFont( this.getFont().deriveFont((float)ch*0.6f) );
				table.setFont( table.getFont().deriveFont((float)ch*0.6f) );
				ruler.setFont( ruler.getFont().deriveFont((float)ch*0.6f) );
				
				FastaView.this.ruler.cw = cw;
				updateCoords();
			} else if( keycode == KeyEvent.VK_MINUS || keychar == '-' ) {
				cw *= 0.8;
				ch *= 0.8;
				rh = (int)ch;
				table.setRowHeight( rh );
				this.setFont( this.getFont().deriveFont((float)ch*0.6f) );
				table.setFont( table.getFont().deriveFont((float)ch*0.6f) );
				ruler.setFont( ruler.getFont().deriveFont((float)ch*0.6f) );
				
				FastaView.this.ruler.cw = cw;
				updateCoords();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	};
	
	public void importBlastReader( BufferedReader br ) throws IOException {
		String line = br.readLine();
		Sequence s = null;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				serifier.checkMaxMin();
				s = new Sequence( line.substring(7), null );
				/*for( int i = 0; i < serifier.max; i++ ) {
					s.sb.append('-');
				}*/
				
				serifier.lseq.add( s );
				serifier.mseq.put( s.getName(), s );
				s.setStart(0);
			} else if( line.startsWith("Query ") ) {
				int first = line.indexOf(' ');
				while( line.charAt(++first) == ' ' );
				while( line.charAt(++first) != ' ' );
				
				int last = line.lastIndexOf(' ');
				String qseq = line.substring(first, last).trim();
				String[] qsplit = line.split("[ ]+");
				line = br.readLine();
				line = br.readLine();
				
				first = line.indexOf(' ');
				while( line.charAt(++first) == ' ' );
				while( line.charAt(++first) != ' ' );
				last = line.lastIndexOf(' ');
				String sseq = line.substring(first, last).trim();
				String[] ssplit = line.split("[ ]+");
				
				int qstart = Integer.parseInt(qsplit[1]);
				int qstop = Integer.parseInt( qsplit[ qsplit.length-1 ] );
				int sstart = Integer.parseInt(ssplit[1]);
				int sstop = Integer.parseInt( ssplit[ ssplit.length-1 ] );
				
				if( qseq.length() != sseq.length() ) {
					System.err.println();
				}
				if( sstop > sstart ) {
					int k = 0;
					for( int i = 0; i < sseq.length(); i++ ) {
						if( sseq.charAt(i) != '-' ) {
							//if( k+sstart-1 >= s.sb.length() ) break;
							while( s.getSequence().length() < k+sstart ) {
								s.getSequence().append('-');
							}
							char c = qseq.charAt(i);
							s.getSequence().setCharAt(k+sstart-1, c);
							k++;
						}
					}
				} else {
					int k = 0;
					for( int i = 0; i < sseq.length(); i++ ) {
						if( sseq.charAt(i) != '-' ) {
							//if( k+sstop-1 >= s.sb.length() ) break;
							while( s.getSequence().length() < sstart-k ) {
								s.getSequence().append('-');
							}
							char c = qseq.charAt(i);
							s.getSequence().setCharAt(sstart-k-1, Sequence.rc.get(c) );
							k++;
						}
					}
				}
			}
			line = br.readLine();
		}
	}
	
	public void importAceReader( BufferedReader br ) throws IOException {
		String line = br.readLine();
		String consensus = null;
		Sequence cseq = null;
		int lastStart = -1;
		int lastEnd = 0;
		Sequence s = null;
		
		List<Sequence> ctgs = new ArrayList<Sequence>();
		//int k = 0;
		while( line != null ) {
			if( line.startsWith("CO")) {
				//if( k > 1 ) break;
				consensus = line.split("[ ]+")[1];
				
				lastStart = lastEnd;
				cseq = new Sequence( consensus, null );
				cseq.setId( consensus );
				cseq.setStart( lastStart );
				
				//serifier.lseq.add( cseq );
				ctgs.add( cseq );
				serifier.mseq.put( cseq.getName(), cseq );
				serifier.lgseq.add( cseq );
				
				Annotation ann = new Annotation( cseq, cseq.getName(), null, 0, cseq.length(), cseq.getRevComp(), serifier.mann );
				serifier.addAnnotation(ann);
				
				//k++;
			} else if( line.startsWith("BQ") ) {
				lastEnd = cseq.getEnd();
				cseq = null;
			} else if( line.startsWith("AF") ) {
				String[] split = line.split("[ ]+");
				Sequence seq = new Sequence( split[1], null );
				seq.consensus = cseq;
				seq.setId( consensus );
				seq.setStart( Integer.parseInt( split[3] )-1+lastStart );
				serifier.lseq.add( seq );
				serifier.mseq.put(seq.getName(), seq);
				List<Sequence> lseq;
				if( serifier.gseq.containsKey( consensus ) ) {
					lseq = serifier.gseq.get( consensus );
				} else {
					lseq = new ArrayList<Sequence>();
					serifier.gseq.put( consensus, lseq );
				}
				lseq.add( seq );
			} else if( line.startsWith("RD") ) {
				String[] split = line.split("[ ]+");
				String name = split[1];
				if( name.contains("GX9TPFH03FQ93B") ) {
					System.err.println();
				}
				s = serifier.mseq.get( name );
			}  else if( line.startsWith("QA") ) {
				s = null;
			} else if( s != null ) {
				s.append( line );
			} else if( cseq != null ) {
				cseq.append( line );
			}
			
			line = br.readLine();
		}
		br.close();
		
		for( Sequence seq : ctgs ) {
			serifier.lseq.add( 0, seq );
		}
	}
	
	public void importPsiReader( List<String> lines ) throws IOException {
		Map<String,Sequence>	seqm = new HashMap<String,Sequence>();
		for( String line : lines ) {
			String[] split = line.split("[ ]+");
			
			if( split.length > 1 ) {
				Sequence seq;
				String name = split[0];
				if( seqm.containsKey( name ) ) {
					seq = seqm.get(name);
				} else {
					seq = new Sequence( name, serifier.mseq );
					serifier.lseq.add( seq );
					seqm.put( name, seq );
				}
				seq.append( split[1] );
			}
		}
		
		c.updateCoords();
	}
	
	public void importGbkReader( String name, BufferedReader br ) throws IOException {
		Map<String,List<Sequence>> seqlist = serifier.readGBK( name, br );
		
		if( seqlist != null && seqlist.size() > 0 ) {
			Sequence seq = seqlist.values().iterator().next().get(0);
			serifier.lgseq.add( seq );
			seq.setId( "paste" );
			
			for( List<Sequence> ss : seqlist.values() ) {
				for( Sequence s : ss ) {
					//s.consensus = seq;
					s.setId("paste");
					serifier.lseq.add(s);
					serifier.mseq.put(s.getName(), s);

					if (s != null) {
						if (s.getEnd() > serifier.getMax()) serifier.setMax(s.getEnd());
					}
				/*if( s != null ) {
					if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
				}*/
				}
				serifier.gseq.put( "paste", ss );
			}
		}
	}
	
	public void importReader( BufferedReader br ) throws IOException {
		importReader( null, br );
	}
	
	public void importReader( String fname, BufferedReader br ) throws IOException {
		List<Sequence> seqlist = serifier.readSequences( br );
		br.close();
		
		if( seqlist != null && seqlist.size() > 0 ) {
			Sequence seq = seqlist.get(0);
			serifier.lgseq.add( seq );
			seq.setId( "paste" );
			
			for( Sequence s : seqlist ) {
				if( fname != null ) s.setName( fname+"-"+s.getName() );
				//s.consensus = seq;
				s.setId( "paste" );
				serifier.lseq.add( s );
				serifier.mseq.put( s.getName(), s );
				
				if( s != null ) {
					if( s.getEnd() > serifier.getMax() ) serifier.setMax( s.getEnd() );
				}
				/*if( s != null ) {
					if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
				}*/
			}
			
			serifier.gseq.put( "paste", seqlist );
		}
	}
	
	public void importFile( String name, InputStream is ) throws IOException {
		if( name.endsWith(".ab1") || name.endsWith(".abi") ) addAbiSequence( name, is );
		else if( name.endsWith(".blastout") ) {
			BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
			importBlastReader( br );
		} else if( name.endsWith(".ace") ) {
			BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
			importAceReader( br );
		} else {
			BufferedReader	br = new BufferedReader( new InputStreamReader(is) );
			importReader( br );
    	}
	}
	
	String pos = "";
	public void importFile( String name, Path path ) throws IOException {
		if( name.endsWith(".gor") ) {
			Stream<String> lstream = Files.newBufferedReader(path).lines();
			lstream.skip(1).forEach( line -> {
				String[] split = line.split("\t");
				String pn = split[split.length-1];
				if( !serifier.mseq.containsKey(pn) ) {
					Sequence seq = new Sequence(pn, serifier.mseq);
					serifier.addSequence(seq);
				}
			});
			lstream = Files.newBufferedReader(path).lines();
			lstream.skip(1).forEach( line -> {
				String[] split = line.split("\t");
				if( !pos.equals(split[1]) ) {
					pos = split[1];
					char ref = split[2].charAt(0);
					for( Sequence seq : serifier.mseq.values() ) {
						seq.append(ref);
					}
				}
				String pn = split[split.length-1];
				if( serifier.mseq.containsKey(pn) ) {
					Sequence seq = serifier.mseq.get(pn);
					seq.setCharAt( seq.length()-1, split[3].charAt(0) );
				}
			});
		} else if( name.endsWith(".psi") ) {
			List<String> lines = Files.readAllLines(path);
			importPsiReader( lines );
		} else if( name.endsWith(".gbff") || name.endsWith(".gbk") || name.endsWith(".gb") ) {
			BufferedReader br = Files.newBufferedReader(path);
			importGbkReader( name, br );
		} else if( name.endsWith(".bam") ) {
			/*SamReaderFactory.setDefaultValidationStringency( ValidationStringency.SILENT );
			SamReaderFactory srf = SamReaderFactory.makeDefault();
			final SamReader reader = srf.open( path.toFile() );
			
			boolean bb = JOptionPane.showConfirmDialog( currentCnt, "Include header?", "Read header", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION;
			
			SAMFileHeader sh = reader.getFileHeader();
			SAMSequenceDictionary ssd = sh.getSequenceDictionary();
			for( SAMSequenceRecord samseq : ssd.getSequences() ) {
				//String ass = samseq.getAssembly();
				
				String chr = samseq.getSequenceName();
				System.err.println( "cc " + chr );
				final SAMRecordIterator iterator = reader.queryAlignmentStart(chr, 1);
				try {
			        while (iterator.hasNext()) {
			            final SAMRecord record = iterator.next();
			            System.err.println("about to insert " + record + " " + chr);
			            String refname = record.getReferenceName();
			            Sequence consensus = serifier.mseq.get( refname );
			            Sequence seq = new Sequence( record.getReadName(), serifier.mseq );
			            seq.consensus = consensus;
			            seq.append(record.getReadString());
			            //seq.setStart(record.getAlignmentStart());
						serifier.lseq.add( seq );
						List<Sequence> glst = serifier.gseq.get( refname );
						if( glst != null ) glst.add( seq );
			            //String str = record.getSAMString();
			        }
				} catch( Exception e ) {
					e.printStackTrace();
				} finally {
					try { iterator.close(); } catch( Exception e ) {};
				}
				
				if( bb ) {
					Sequence seq = new Sequence( samseq.getSequenceName(), serifier.mseq );
					seq.setLength( samseq.getSequenceLength() );
					//seq.append(ass);
					serifier.lgseq.add( seq );
					serifier.lseq.add( seq );
					serifier.gseq.put( samseq.getSequenceName(), new ArrayList<Sequence>() );
				}
			}*/
		} else importFile( name, Files.newInputStream(path) );
	}
	
	public Map<String,String> openRenameFile() throws IOException {
		Map<String,String>	or = new HashMap<String,String>();
		/*FileOpenService fos; 
	    try { 
	        fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService"); 
	    } catch (UnavailableServiceException e) {
	        fos = null; 
	    }*/
	    
	    /*if (fos != null) {
	        FileContents[] fcs = fos.openMultiFileDialog(null, null);
            for( FileContents fc : fcs ) {
            	String name = fc.getName();
            	or.putAll( importRenameFile( fc.getInputStream() ) );
            }
	    } else {*/
		    	JFileChooser	jfc = new JFileChooser();
		    	if( jfc.showOpenDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
		    		File f = jfc.getSelectedFile();
		    		or.putAll( importRenameFile( new FileInputStream(f) ) );
		    	}
	    //}
	    
	    return or;
	}
	
	public void openFiles() throws IOException {
		/*FileOpenService fos; 
	    try { 
	        fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService"); 
	    } catch (UnavailableServiceException e) {
	        fos = null; 
	    }*/
	    
	    /*if (fos != null) {
	        FileContents[] fcs = fos.openMultiFileDialog(null, null);
            for( FileContents fc : fcs ) {
            	String name = fc.getName();
            	importFile( name, fc.getInputStream() );
            }
	    } else {*/
		    	JFileChooser	jfc = new JFileChooser();
		    	if( jfc.showOpenDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
		    		File f = jfc.getSelectedFile();
		    		importFile( f.getName(), f.toPath() );
		    	}
	    //}   
	    serifier.checkMaxMin();
	    updateView();
	}
	
	public Map<String,String> importRenameFile( InputStream is ) throws IOException {
		Map<String,String> or = new HashMap<>();
	
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		String line = br.readLine();
		while( line != null ) {
			String[] split = line.trim().split("[\t ]+");
			if( split.length > 1 ) {
				or.put(split[0], split[1]);
			}
			line = br.readLine();
		}
		br.close();
		
		return or;
	}
	
	public void exportPhylip() throws IOException {
		 //FileSaveService fss = null;
         //FileContents fileContents = null;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         OutputStreamWriter	osw = new OutputStreamWriter( baos );
    	 
	    	 osw.write( Sequence.getPhylip( serifier.lseq, false ) );
	    	 osw.close();
	    	 baos.close();
	
	    	 /*try {
	    		 fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
	    	 } catch( UnavailableServiceException e ) {
	    		 fss = null;
	    	 }*/
    	 
         /*if (fss != null) {
        	 	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
             fileContents = fss.saveFileDialog(null, null, bais, "export.phy");
             bais.close();
             OutputStream os = fileContents.getOutputStream(true);
             os.write( baos.toByteArray() );
             os.close();
         } else {*/
        	 JFileChooser jfc = new JFileChooser();
        	 if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
        		 File f = jfc.getSelectedFile();
        		 FileOutputStream fos = new FileOutputStream( f );
        		 fos.write( baos.toByteArray() );
        		 fos.close();
        		 
        		 Desktop.getDesktop().browse( f.toURI() );
        	 }
         //}
	}
	
	public void exportFasta() throws IOException {
		exportFasta( table, serifier.lseq );
	}
	
	/*public Rectangle getSelectedRect() {
		return c != null ? c.selectedRect : null;
	}*/
	
	public void exportFasta( JTable table, List<Sequence> lseq ) throws IOException {
		 //FileSaveService fss = null;
         //FileContents fileContents = null;
         //ByteArrayOutputStream baos = new ByteArrayOutputStream();
         //OutputStreamWriter	osw = new OutputStreamWriter( baos );
    	 
         List<Sequence> seqlist = new ArrayList<>();
	    	 int[] rr = table.getSelectedRows();
	    	 for( int r : rr ) {
	    		 int i = table.convertRowIndexToModel( r );
	    		 Sequence seq = lseq.get(i);
	    		 seqlist.add( seq );
	    	 }
	    	 //serifier.writeFasta( seqlist, osw, getSelectedRect() );
	    	 //osw.close();
	    	 //baos.close();
	    	 
	    	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	 OutputStreamWriter	osw = new OutputStreamWriter( baos );
	    	 serifier.writeFasta( seqlist, osw, getSelectedRect() );
	    	 osw.close();
	    	 baos.close();
	    	 
	    	 String str = baos.toString();
	    	 //netscape.javascript.JSObject obj = netscape.javascript.JSObject.getWindow( parentApplet );
	    	 //obj.call("blobstuff", new Object[] {str, "text/plain"});

        	 JFileChooser jfc = new JFileChooser();
        	 if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
        		 File f = jfc.getSelectedFile();
        		 //FileOutputStream fos = new FileOutputStream( f );
        		 //fos.write( baos.toByteArray() );
        		 
        		 FileWriter fw = new FileWriter( f );
        		 serifier.writeFasta( seqlist, fw, getSelectedRect() );
        		 fw.close();
        		 
        		 //fos.close();
        		 
        		 Desktop.getDesktop().browse( f.toURI() );
        	 }
         //}

         /*if (fileContents != null) {
             try {
            	 OutputStream os = fileContents.getOutputStream( true );
            	 OutputStreamWriter	osw = new OutputStreamWriter( os );
            	 
            	 int[] rr = table.getSelectedRows();
            	 for( int r : rr ) {
            		 int i = table.convertRowIndexToModel( r );
            		 Sequence seq = serifier.lseq.get(i);
            		 osw.write( ">" + seq.name + "\n" );
            		 int val = 0;
            		 while( val < seq.length() ) {
            			 osw.write( seq.sb.substring(val, Math.min( seq.length(), val+70 )) + "\n" );
            			 val += 70;
            		 }
            	 }
             } catch (IOException exc) {
            	 exc.printStackTrace();
             }
         }*/
	}
	
	public void exportManyFasta( JTable table, List<Sequence> lseq ) throws IOException {
		 JFileChooser jfc = new JFileChooser();
       	 jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
       	 File dir = null;
       	 if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
       		 dir = jfc.getSelectedFile();
       	 }
       	 
       	 Map<String,FileWriter>	filemap = new HashMap<String,FileWriter>();
   	 
	   	 int[] rr = table.getSelectedRows();
	   	 for( int r : rr ) {
	   		 int i = table.convertRowIndexToModel( r );
	   		 Sequence seq = lseq.get(i);
	   		 int val = 0;
	   		 int end = seq.length();
	   		 
	   		 if( c.selectedRect.width > 0 ) {
	   			 val = Math.max( val, c.selectedRect.x-seq.getStart() );
	   			 end = Math.min( end, c.selectedRect.x+c.selectedRect.width-seq.getStart() );
	   		 }
	   		 
	   		 String name = seq.getName();
	   		 int ui = name.indexOf('_');
	   		 if( ui == -1 ) ui = name.length();
	   		 String filename = name.substring(0,ui);
	   		 
	   		 FileWriter fw;
	   		 if( filemap.containsKey(filename) ) fw = filemap.get( filename );
	   		 else {
	   			 fw = new FileWriter( new File( dir, filename ) );
	   			 filemap.put( filename, fw );
	   		 }
	   		 
	   		 if( val < end ) fw.write( ">" + seq.getName() + "\n" );
	   		 while( val < end ) {
	   			 fw.write( seq.getSequence().substring(val, Math.min( end, val+70 )) + "\n" );
	   			 val += 70;
	   		 }
	   	 }
	   	 
	   	 for( String filename : filemap.keySet() ) {
	   		 filemap.get(filename).close();
	   	 }

        /*if (fileContents != null) {
            try {
           	 OutputStream os = fileContents.getOutputStream( true );
           	 OutputStreamWriter	osw = new OutputStreamWriter( os );
           	 
           	 int[] rr = table.getSelectedRows();
           	 for( int r : rr ) {
           		 int i = table.convertRowIndexToModel( r );
           		 Sequence seq = serifier.lseq.get(i);
           		 osw.write( ">" + seq.name + "\n" );
           		 int val = 0;
           		 while( val < seq.length() ) {
           			 osw.write( seq.sb.substring(val, Math.min( seq.length(), val+70 )) + "\n" );
           			 val += 70;
           		 }
           	 }
            } catch (IOException exc) {
           	 exc.printStackTrace();
            }
        }*/
	}
	
	public void exportAnnotationFasta( JTable table, List<Annotation> tlann ) throws IOException {
		//FileSaveService fss = null;
        //FileContents fileContents = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter	osw = new OutputStreamWriter( baos );
   	 
	   	int[] rr = table.getSelectedRows();
	   	for( int r : rr ) {
	   		int i = table.convertRowIndexToModel( r );
	   		Annotation ann = tlann.get(i);
	   		osw.write( ">" + ann.getName() + "\n" );
	   		int val = ann.start;
	   		while( val < ann.stop ) {
	   			osw.write( ann.getSeq().getSequence().substring( val, Math.min( ann.stop, val+70 )) + "\n" );
	   			val += 70;
	   		}
	   	}
	   	osw.close();
	   	baos.close();
	
	   	/*try {
	   		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
	   	} catch( UnavailableServiceException e ) {
	   		fss = null;
	   	}*/
	   	 
	    /*if (fss != null) {
	   	 		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		        fileContents = fss.saveFileDialog(null, null, bais, "exportannotation.fasta");
		        bais.close();
		        OutputStream os = fileContents.getOutputStream(true);
		        os.write( baos.toByteArray() );
		        os.close();
	    } else {*/
		   	 JFileChooser jfc = new JFileChooser();
		   	 if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
		   		 File f = jfc.getSelectedFile();
		   		 FileOutputStream fos = new FileOutputStream( f );
		   		 fos.write( baos.toByteArray() );
		   		 fos.close();
		   		 
		   		 Desktop.getDesktop().browse( f.toURI() );
		   	 }
	    //}

        /*if (fileContents != null) {
            try {
           	 OutputStream os = fileContents.getOutputStream( true );
           	 OutputStreamWriter	osw = new OutputStreamWriter( os );
           	 
           	 int[] rr = table.getSelectedRows();
           	 for( int r : rr ) {
           		 int i = table.convertRowIndexToModel( r );
           		 Sequence seq = serifier.lseq.get(i);
           		 osw.write( ">" + seq.name + "\n" );
           		 int val = 0;
           		 while( val < seq.length() ) {
           			 osw.write( seq.sb.substring(val, Math.min( seq.length(), val+70 )) + "\n" );
           			 val += 70;
           		 }
           	 }
            } catch (IOException exc) {
           	 exc.printStackTrace();
            }
        }*/
	}
	
	//Map<String,Sequence>	mseq;
	//ArrayList<Sequence>		lseq;
	//ArrayList<Annotation>	lann;
	//Map<String,Annotation>	mann;
	JTable			table;
	public FastaView		c;
	Overview		overview;
	//int				max = 0;
	//int				min = 0;
	
	JTable			atable;
	
	public List<Sequence> getSequences() {
		return serifier.lseq;
	}
	
	public int getNumberOfSequences() {
		return serifier.lseq.size();
	}
	
	public byte[] getByteArray( int len ) {
		return new byte[ len ];
	}
	
	public void addAbiSequence( String name, InputStream is ) {
		Ab1Reader abi = new Ab1Reader( is );
		Sequence s = new Sequence( name, serifier.mseq );
		s.append( abi.getSequence() );
		serifier.lseq.add( s );
		
		if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
	}
	
	public void addAbiSequence( String name, Path path ) throws IOException {
		addAbiSequence( name, Files.newInputStream(path) );
	}
	
	public void addAbiSequence( String name, byte[] bts, int len ) {
		//byte[] ba = bts.getBytes();
		ByteBuffer bb = ByteBuffer.wrap( bts );
		try {
			Ab1Reader abi = new Ab1Reader( bb );
			Sequence s = new Sequence( name, serifier.mseq );
			s.append( abi.getSequence() );
			serifier.lseq.add( s );
			
			if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void updateView() {
		if( table != null ) {
			/*SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					splitpane.setDividerLocation(0.7);
					overviewsplit.setDividerLocation(0.7);
					mainsplit.setDividerLocation(0.7);
				}
			});*/
			
			c.updateCoords();
			table.tableChanged( new TableModelEvent( table.getModel() ) );
			atable.tableChanged( new TableModelEvent( atable.getModel() ) );
		}
	}
	
	public void delete() {
		edited = true;
		
		Set<Sequence>	delset = new HashSet<>();
		Set<Annotation>	adelset = new HashSet<>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			Sequence seq = serifier.lseq.get(i);
			
			delset.add( seq );
			if( seq.getAnnotations() != null ) adelset.addAll( seq.getAnnotations() );
		}
		serifier.lseq.removeAll( delset );
		serifier.lann.removeAll( adelset );
		
		updateIndexes();
		serifier.checkMaxMin();
		updateView();
	}
	
	public void init() {
		initGui( this );
	}
	
	public void cleargetCharAt( int x, Sequence s ) {
		if( x < s.getStart() ) s.setStart( s.getStart()-1 );
		else {
			int val = x-s.getStart();
			if( val < s.length() ) {
				s.getSequence().setCharAt( val, '-' );
			}
		}
	}
	
	public void cleargetCharAt( int x, int y ) {
		int r = table.convertRowIndexToModel(y);
		Sequence s = serifier.lseq.get(r);
		cleargetCharAt( x, s );
	}
	
	public void deletegetCharAt( int x, Sequence s ) {
		if( x < s.getStart() ) s.setStart( s.getStart()-1 );
		else {
			int val = x-s.getStart();
			if( val < s.length() ) {
				s.getSequence().deleteCharAt( val );
			}
		}
	}
	
	public void deletegetCharAt( int x, int y ) {
		int r = table.convertRowIndexToModel(y);
		Sequence s = serifier.lseq.get(r);
		deletegetCharAt( x, s );
	}
	
	public char getgetCharAt( int x, Sequence s ) {
		char c = ' ';
		if( x >= s.getStart() && x < s.getEnd() ) {
			int val = x-s.getStart();
			if( val >= 0 && val < s.length() ) c = s.getSequence().charAt( val );
		}
		return c;
	}
	
	public char getCharAt( int x, int y ) {
		int r = table.convertRowIndexToModel(y);
		Sequence s = serifier.lseq.get(r);
		return getgetCharAt( x, s );
	}
	
	public void clearConservedSites( Map<String,Collection<Sequence>> specmap ) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for( String spec : specmap.keySet() ) {
			Collection<Sequence> seqset = specmap.get( spec );
			for( Sequence seq : seqset ) {
				min = Math.min( min, seq.getStart() );
				max = Math.max( max, seq.getEnd() );
			}
		}
		
		int i = 0;
		while( i < max-min ) {
			boolean rem = true;
			for( String spec : specmap.keySet() ) {
				Collection<Sequence> seqset = specmap.get( spec );
				char c = 0;
				for( Sequence seq : seqset ) {
					int r = table.convertRowIndexToView( seq.index );
					char c2 = getCharAt(i, r);
					/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
						rem = false;
						break;
					}*/
					//if( c2 != '.' && c2 != '-' ) {
						if( c != 0 && c2 != c ) {
							rem = false;
							break;
						}
					
						c = c2;
					//}
				}
				if( !rem ) break;
			}
			
			if( rem ) {
				for( String spec : specmap.keySet() ) {
					Collection<Sequence> seqset = specmap.get( spec );
					for( Sequence seq : seqset ) {
						int r = table.convertRowIndexToView( seq.index );
						cleargetCharAt(i, r);
					}
				}
			}
			i++;
		}
		
		for( String spec : specmap.keySet() ) {
			Collection<Sequence> seqset = specmap.get( spec );
			for( Sequence seq : seqset ) {
				seq.checkLengths();
			}
		}
		
		c.repaint();
	}
	
	public void clearSites( Collection<Sequence> seqset, boolean variant ) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for( Sequence seq : seqset ) {
			min = Math.min( min, seq.getStart() );
			max = Math.max( max, seq.getEnd() );
		}
		
		int i = 0;
		while( i < max-min ) {
			boolean rem = true;
			char c = 0;
			for( Sequence seq : seqset ) {
				int r = table.convertRowIndexToView( seq.index );
				char c2 = getCharAt(i, r);
				/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
					rem = false;
					break;
				}*/
				//if( c2 != '.' && c2 != '-' ) {
					if( c != 0 && c2 != c ) {
						rem = false;
						break;
					}
				
					c = c2;
				//}
			}
			if( !rem ^ variant ) {
				for( Sequence seq : seqset ) {
					int r = table.convertRowIndexToView( seq.index );
					cleargetCharAt(i, r);
				}
			}
			i++;
		}
		
		for( Sequence seq : seqset ) {
			seq.checkLengths();
		}
		
		c.repaint();
	}
	
	public void clearSitesWithGaps( Collection<Sequence> seqset ) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for( Sequence seq : seqset ) {
			min = Math.min( min, seq.getStart() );
			max = Math.max( max, seq.getEnd() );
		}
		
		int i = 0;
		while( i < max-min ) {
			boolean rem = true;
			for( Sequence seq : seqset ) {
				//int r = table.convertRowIndexToView( seq.index );
				char c = getgetCharAt(i, seq);
				/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
					rem = false;
					break;
				}*/
				if( c == '.' || c == '-' ) {
					//if( c != 0 && c2 != c ) {
					rem = false;
					break;
					//}
				}
			}
			if( !rem ) {
				for( Sequence seq : seqset ) {
					cleargetCharAt(i, seq);
				}
			}
			i++;
		}
		
		for( Sequence seq : seqset ) {
			seq.checkLengths();
		}
		
		if( c != null ) c.repaint();
	}
	
	public void updateIndexes() {
		for( int i = 0; i < serifier.lseq.size(); i++ ) {
			serifier.lseq.get(i).index = i;
		}
	}
	
	public void clearSitesWithGapsNonSelected( Collection<Sequence> seqset, Collection<Sequence> nonselected ) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for( Sequence seq : seqset ) {
			min = Math.min( min, seq.getStart() );
			max = Math.max( max, seq.getEnd() );
		}
		
		int i = 0;
		while( i < max-min ) {
			boolean rem = true;
			for( Sequence seq : nonselected ) {
				int r = table.convertRowIndexToView( seq.index );
				char c = getCharAt(i, r);
				/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
					rem = false;
					break;
				}*/
				if( c != '.' && c != '-' && c != ' ' ) {
					//if( c != 0 && c2 != c ) {
					rem = false;
					break;
					//}
				}
			}
			if( rem ) {
				for( Sequence seq : seqset ) {
					int r = table.convertRowIndexToView( seq.index );
					cleargetCharAt(i, r);
				}
			}
			i++;
		}
		
		for( Sequence seq : seqset ) {
			seq.checkLengths();
		}
		
		c.repaint();
	}
	
	public StringBuilder getFastaWoGaps() {
		int start = Integer.MIN_VALUE;
		int end = Integer.MAX_VALUE;
		
		for( Sequence seq : serifier.lseq ) {
			if( seq.getRealStart() > start ) start = seq.getRealStart();
			if( seq.getRealStop() < end ) end = seq.getRealStop();
		}
		
		List<Integer>	idxs = new ArrayList<Integer>();
		for( int x = start; x < end; x++ ) {
			boolean skip = false;
			for( Sequence seq : serifier.lseq ) {
				char c = seq.getCharAt( x );
				if( c != '-' && c != '.' && c == ' ' ) {
					skip = true;
					break;
				}
			}
			
			if( !skip ) {
				idxs.add( x );
			}
		}
		
		StringBuilder ret = new StringBuilder();
		for( Sequence seq : serifier.lseq ) {
			ret.append( ">"+seq.getName()+"\n" );
			int k = 0;
			for( int i : idxs ) {
				ret.append( seq.getCharAt(i) );
				k++;
				if( k % 70 == 0 ) ret.append("\n");
			}
			if( k % 70 != 0 ) ret.append("\n");
		}
		return ret;
	}
	
	public double[] get2StatePCAMatrix( int[] rr ) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for( int r : rr ) {
			int k = table.convertRowIndexToModel( r );
			Sequence seq = serifier.lseq.get( k );
			
			min = Math.min( min, seq.getStart() );
			max = Math.max( max, seq.getEnd() );
		}
		
		List<Integer>	indx = new ArrayList<>();
		for( int i = min; i < max; i++ ) {
			Set<Character>	charset = new HashSet<>();
			for( int r : rr ) {
				char c2 = getCharAt(i, r);
				//if( c2 == '.' || c2 == '-' || c2 == ' ' || c2 == 'N' || c2 == 'n' ) {
				if(c2 != 'a' && c2 != 'c' && c2 != 'g' && c2 != 't') {
					//charset.clear();
					//break;
				} else {
					charset.add( c2 );
					
					/*if( charset.size() == 2 ) {
						int k = table.convertRowIndexToModel( r );
						System.err.println( i + "  " + c2 + "  " + serifier.lseq.get( k ).getName() );
					}*/
				}
			}
			if( charset.size() == 2 ) {
				indx.add( i );
			}
		}
		
		double[] X = new double[ indx.size()*rr.length ];
		int ki = 0;
		for( int i : indx ) {
			char c = 0;
			int kr = 0;
			for( int r : rr ) {
				char c2 = getCharAt(i, r);
				//if( c2 == '.' || c2 == '-' || c2 == ' ' || c2 == 'N' || c2 == 'n' ) {
				if(c2 != 'a' && c2 != 'c' && c2 != 'g' && c2 != 't') {
					X[kr * indx.size() + ki] = 0.0;
				} else {
					if (c == 0) c = c2;
				
				/*int k = table.convertRowIndexToModel( r );
				String acc = serifier.lseq.get( k ).getName();
				if( i == 192 && acc.contains("1940") ) {
					System.err.println(  i + "  " + c + "  " + c2 + "  " + serifier.lseq.get( k ).getName() );
				}*/

					X[kr * indx.size() + ki] = c2 == c ? 1.0 : -1.0;
				}
				kr++;
			}
			ki++;
		}
		
		return X;
	}
	
	public StringBuilder printDistanceMatrix( double[] dd, List<String> names ) {
		StringBuilder	text = new StringBuilder();
		text.append("\t"+names.size()+"\n");
		
		int k = 0;
		for( String name : names ) {
			text.append( name );
			for( int i = 0; i < names.size(); i++ ) {
				text.append( "\t"+dd[k*names.size()+i] );
			}
			text.append("\n");
			k++;
		}
		/*for( double d : dd ) {
			text.append( d );
		}*/
		
		return text;
	}
	
	public StringBuilder distanceMatrix( boolean excludeGaps, Map<String,Integer> blosumap, List<String> names ) {
		double[] dd = distanceMatrixNumeric( false, null, blosumap );
		return printDistanceMatrix( dd, names );
	}
	
	public StringBuilder distanceMatrix( boolean excludeGaps ) {
		JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
		JCheckBox	boots = new JCheckBox("Bootstrap");
		JOptionPane.showMessageDialog( parentApplet, new Object[] {jukes, boots} );
		boolean cantor = jukes.isSelected();
		boolean bootstrap = boots.isSelected();
		
		int[] rr = table.getSelectedRows();
		StringBuilder	text = new StringBuilder();
		text.append("\t").append(rr.length).append("\n");
		
		if( rr.length > 0 ) {
			if( excludeGaps ) {
				int start = Integer.MIN_VALUE;
				int end = Integer.MAX_VALUE;
				
				for( int i = 0; i < rr.length; i++ ) {
					int r = rr[i];
					Sequence seq = serifier.lseq.get( table.convertRowIndexToModel(r) );
					if( seq.getRealStart() > start ) start = seq.getRealStart();
					if( seq.getRealStop() < end ) end = seq.getRealStop();
				}
				
				List<Integer>	idxs = new ArrayList<>();
				for( int x = start; x < end; x++ ) {
					int i;
					for( i = 0; i < rr.length; i++ ) {
						int r = rr[i];
						Sequence seq = serifier.lseq.get( table.convertRowIndexToModel(r) );
						char c = seq.getCharAt( x );
						if( c != '-' && c != '.' && c == ' ' ) break;
					}
					
					if( i == rr.length ) {
						idxs.add( x );
					}
				}
				
				for( int i = 0; i < rr.length; i++ ) {
					int r = rr[i];
					text.append( ((String)table.getValueAt(r, 0)).replace(' ','_') );
					for( int y = 0; y < rr.length; y++ ) {
						if( i == y ) text.append("\t0.0");
						else {
							Sequence seq1 = serifier.lseq.get( table.convertRowIndexToModel(rr[i]) );
							Sequence seq2 = serifier.lseq.get( table.convertRowIndexToModel(rr[y]) );
							int count = 0;
							int mism = 0;
							
							for( int k : idxs ) {
								char c1 = seq1.getCharAt( k-seq1.getStart() );
								char c2 = seq2.getCharAt( k-seq2.getStart() );
								
								if( c1 != c2 ) mism++;
								count++;
							}
							double d = count == 0 ? 0.0 : ((double)mism/(double)count);
							if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
							text.append("\t"+d);
						}
					}
					text.append("\n");
				}
			} else {
				for( int i = 0; i < rr.length; i++ ) {
					int r = rr[i];
					text.append( ((String)table.getValueAt(r, 0)).replace(' ','_') );
					for( int y = 0; y < rr.length; y++ ) {
						if( i == y ) text.append("\t0.0");
						else {
							Sequence seq1 = serifier.lseq.get( table.convertRowIndexToModel(rr[i]) );
							Sequence seq2 = serifier.lseq.get( table.convertRowIndexToModel(rr[y]) );
							int count = 0;
							int mism = 0;
							
							int start = Math.max( seq1.getStart(), seq2.getStart() );
							int end = Math.min( seq1.getEnd(), seq2.getEnd() );
							
							for( int k = start; k < end; k++ ) {
								char c1 = seq1.getCharAt( k-seq1.getStart() );
								char c2 = seq2.getCharAt( k-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' &&  c2 != '.' && c2 != '-' && c2 != ' ' ) {
									if( c1 != c2 ) mism++;
									count++;
								}
							}
							double d = count == 0 ? 0.0 : ((double)mism/(double)count);
							if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
							text.append("\t"+d);
						}
					}
					text.append("\n");
				}
			}
		}
		
		return text;
	}
	
	public StringBuilder blosumDistanceMatrix( boolean excludeGaps, Map<String,Integer> blosumap ) {
		//JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
		JCheckBox	boots = new JCheckBox("Bootstrap");
		JOptionPane.showMessageDialog( parentApplet, new Object[] {boots} );
		//boolean cantor = jukes.isSelected();
		boolean bootstrap = boots.isSelected();
		
		int[] rr = table.getSelectedRows();
		StringBuilder	text = new StringBuilder();
		text.append("\t"+rr.length+"\n");
		
		if( rr.length > 0 ) {
			if( excludeGaps ) {
				int start = Integer.MIN_VALUE;
				int end = Integer.MAX_VALUE;
				
				for( int i = 0; i < rr.length; i++ ) {
					int r = rr[i];
					Sequence seq = serifier.lseq.get( table.convertRowIndexToModel(r) );
					if( seq.getRealStart() > start ) start = seq.getRealStart();
					if( seq.getRealStop() < end ) end = seq.getRealStop();
				}
				
				List<Integer>	idxs = new ArrayList<Integer>();
				for( int x = start; x < end; x++ ) {
					int i;
					for( i = 0; i < rr.length; i++ ) {
						int r = rr[i];
						Sequence seq = serifier.lseq.get( table.convertRowIndexToModel(r) );
						char c = seq.getCharAt( x );
						if( c != '-' && c != '.' && c == ' ' ) break;
					}
					
					if( i == rr.length ) {
						idxs.add( x );
					}
				}
				
				for( int i = 0; i < rr.length; i++ ) {
					int r = rr[i];
					text.append( ((String)table.getValueAt(r, 0)).replace(' ','_') );
					for( int y = 0; y < rr.length; y++ ) {
						if( i == y ) text.append("\t0.0");
						else {
							Sequence seq1 = serifier.lseq.get( table.convertRowIndexToModel(rr[i]) );
							Sequence seq2 = serifier.lseq.get( table.convertRowIndexToModel(rr[y]) );
							int count = 0;
							int mism = 0;
							
							for( int k : idxs ) {
								char c1 = seq1.getCharAt( k-seq1.getStart() );
								char c2 = seq2.getCharAt( k-seq2.getStart() );
								
								if( c1 != c2 ) mism++;
								count++;
							}
							double d = count == 0 ? 0.0 : ((double)mism/(double)count);
							//if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
							text.append("\t"+d);
						}
					}
					text.append("\n");
				}
			} else {
				for( int i = 0; i < rr.length; i++ ) {
					int r = rr[i];
					text.append( ((String)table.getValueAt(r, 0)).replace(' ','_') );
					for( int y = 0; y < rr.length; y++ ) {
						if( i == y ) text.append("\t0.0");
						else {
							Sequence seq1 = serifier.lseq.get( table.convertRowIndexToModel(rr[i]) );
							Sequence seq2 = serifier.lseq.get( table.convertRowIndexToModel(rr[y]) );
							//int count = 0;
							//int mism = 0;
							
							int start = Math.max( seq1.getStart(), seq2.getStart() );
							int stop = Math.min( seq1.getEnd(), seq2.getEnd() );
							
							int mest = 0;
							int tmest = 0;
							for( int k = start; k < stop; k++ ) {
					        	char lc = seq1.getCharAt(k);
					        	char c = Character.toUpperCase( lc );
					        	//if( )
					        	String comb = c+""+c;
					        	if( blosumap.containsKey(comb) ) tmest += blosumap.get(comb);
					        }
					        
					        for( int k = start; k < stop; k++ ) {
					        	char lc = seq1.getCharAt( k );
					        	char c = Character.toUpperCase( lc );
					        	char lc2 = seq2.getCharAt( k );
					        	char c2 = Character.toUpperCase( lc2 );
					        	
					        	String comb = c+""+c2;
					        	if( blosumap.containsKey(comb) ) mest += blosumap.get(comb);
					        }
					        
					        double tani = (double)(tmest-mest)/(double)tmest;
					        /*if( tani > (double)score/(double)tscore ) {
					        	score = mest;
					        	tscore = tmest;
					        }*/
					        
							/*for( int k = start; k < end; k++ ) {
								char c1 = seq1.getCharAt( k-seq1.getStart() );
								char c2 = seq2.getCharAt( k-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' &&  c2 != '.' && c2 != '-' && c2 != ' ' ) {
									if( c1 != c2 ) mism++;
									count++;
								}
							}*/
							//if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
							text.append("\t"+tani);
						}
					}
					text.append("\n");
				}
			}
		}
		
		return text;
	}
	
	public List<String> getNames() {
		List<String>	ret = new ArrayList<String>();
		
		for( Sequence seqname : serifier.lseq ) {
			ret.add( seqname.getName() ); //.replace(' ', '_') );
		}
		
		return ret;
	}
	
	public StringBuilder getFasta() {
		return getFasta( this.getSequences() );
	}
	
	public StringBuilder getFasta( List<Sequence> lseq ) {
		StringBuilder out = new StringBuilder();
		
   	 	for( Sequence seq : lseq ) {
   		 int val = 0;
   		 int end = seq.length();
   		 
   		 if( c.selectedRect.width > 0 ) {
   			 val = Math.max( val, c.selectedRect.x-seq.getStart() );
   			 end = Math.min( end, c.selectedRect.x+c.selectedRect.width-seq.getStart() );
   		 }
   		 
   		 if( val < end ) out.append( ">" + seq.getName() + "\n" );
   		 while( val < end ) {
   			 out.append( seq.getSequence().substring(val, Math.min( end, val+70 )) + "\n" );
   			 val += 70;
   		 }
   	 	}
   		 
   		return out;
	}
	
	public String getPhylip( boolean numeric ) {
		return Sequence.getPhylip( this.getSequences(), numeric );
	}
	
	public double[] distanceMatrixNumeric( boolean excludeGaps, double[] ent, Map<String,Integer> blosum ) {
		JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
		JCheckBox	boots = new JCheckBox("Bootstrap");
		
		Object[] val = blosum != null ? new Object[] { boots } : new Object[] { jukes, boots };
		JOptionPane.showMessageDialog( parentApplet, val );
		boolean cantor = jukes.isSelected();
		//boolean bootstrap = boots.isSelected();
		
		List<Integer>	idxs = null;
		if( excludeGaps ) {
			int start = Integer.MIN_VALUE;
			int end = Integer.MAX_VALUE;
			
			for( Sequence seq : serifier.lseq ) {
				if( seq.getRealStart() > start ) start = seq.getRealStart();
				if( seq.getRealStop() < end ) end = seq.getRealStop();
			}
			
			idxs = new ArrayList<Integer>();
			for( int x = start; x < end; x++ ) {
				int i;
				boolean skip = false;
				for( Sequence seq : serifier.lseq ) {
					char c = seq.getCharAt( x );
					if( c != '-' && c != '.' && c == ' ' ) {
						skip = true;
						break;
					}
				}
				
				if( !skip ) {
					idxs.add( x );
				}
			}
		}
		
		double[] dd = new double[ serifier.lseq.size()*serifier.lseq.size() ];
		Sequence.distanceMatrixNumeric( serifier.lseq, dd, idxs, boots.isSelected(), cantor, ent, blosum );
		
		int i = 0;
		for( double d : dd ) {
			System.err.print( "  " + d );
			i++;
			if( i % serifier.lseq.size() == 0 ) System.err.println();
		}
		
		return dd;
	}
	
	public void initDataStructures() {
		/*lseq = new ArrayList<Sequence>() {
			private static final long serialVersionUID = 1L;

			public boolean add( Sequence seq ) {
				seq.index = serifier.lseq.size();
				return super.add( seq );
			}
		};
		Sequence.mseq = new HashMap<String,Sequence>();
		Sequence.lann = new ArrayList<Annotation>();
		Sequence.mann = new HashMap<String,Annotation>();*/
		
		Sequence.runbl = new Sequence.RunInt() {
			@Override
			public void run( Sequence seq ) {
				if( seq.getStart() < serifier.getMin() ) serifier.setMin( seq.getStart() );
				if( seq.getEnd() > serifier.getMax() ) serifier.setMax( seq.getEnd() );
			}
		};
	}
	
	public TransferHandler dragRows( final JTable table ) {
		TransferHandler th = null;
		try {
			final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<Sequence>	selseq = new ArrayList<>(rr.length);
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( serifier.lgseq.get(i) );
						}
						return selseq;
					} else {
						String ret = "";//makeCopyString();
						for( int r = 0; r < table.getRowCount(); r++ ) {
							Object o = table.getValueAt(r, 0);
							if( o != null ) {
								ret += o.toString();
							} else {
								ret += "";
							}
							for( int c = 1; c < table.getColumnCount(); c++ ) {
								o = table.getValueAt(r, c);
								if( o != null ) {
									ret += "\t"+o.toString();
								} else {
									ret += "\t";
								}
							}
							ret += "\n";
						}
						//return arg0.getReaderForText( this );
						return new ByteArrayInputStream( ret.getBytes( charset ) );
					}
					//return ret;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { df, ndf };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					if( arg0.equals(df) || arg0.equals(ndf) ) {
						return true;
					}
					return false;
				}
			};
			
			th = new TransferHandler() {
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
						
						DataFlavor[] dfs = support.getDataFlavors();
						if( support.isDataFlavorSupported( ndf ) ) {					
							Object obj = support.getTransferable().getTransferData( ndf );
							ArrayList<Sequence>	seqs = (ArrayList<Sequence>)obj;
							
							ArrayList<Sequence> newlist = new ArrayList<>(serifier.lgseq.size());
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( serifier.lgseq.get(i) );
							}
							serifier.lgseq.clear();
							serifier.lgseq = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							serifier.lgseq.removeAll( seqs );
							for( Sequence s : seqs ) {
								serifier.lgseq.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							c.repaint();
							
							return true;
						} else if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
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
									Sequence s = new Sequence( f.getName(), serifier.mseq );
									s.append( abi.getSequence() );
									serifier.lgseq.add( s );
									
									if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
									
									bb.clear();
								} else if( fname.endsWith(".blastout") ) {
									FileReader fr = new FileReader( f );
									BufferedReader br = new BufferedReader( fr );
									String line = br.readLine();
									
									Map<String,Integer>	hitmap = new HashMap<>();
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
											if( serifier.mseq.containsKey( query ) ) {
												Sequence seq = serifier.mseq.get( query );
												int ind = serifier.lgseq.indexOf( seq );
												if( ind >= k ) {
													tseq = seq;
													serifier.lgseq.remove( seq );
													serifier.lgseq.add(k++, seq);
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
														if( serifier.mseq.containsKey( val ) ) {
															seq = serifier.mseq.get( val );
															ind = serifier.lgseq.indexOf( seq );
															if( ind >= k ) {
																serifier.lgseq.remove( seq );
																serifier.lgseq.add(k, seq);
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
														seq.setRevComp( 2 );
														int sval = qstart-(seq.length()-sstart+1);
														seq.setStart( sval );
													} else {
														seq.setStart( qstart-sstart );
													}
													k++;
												} else if( tseq != null ) {
													if( sstart > sstop ) {
														if( seq.getRevComp() == 2 ) {
															int sval = (seq.length()-sstart+1)-qstart  +  seq.getStart();
															tseq.setStart( sval );
														} else {
															tseq.setRevComp( 2 );
															int sval = sstart-qstart  +  seq.getStart();
															tseq.setStart( sval );
														}
													} else {
														if( seq.getRevComp() == 2 ) {
															tseq.setRevComp( 2 );
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
										if( s.length() > max ) max = s.length();
									}*/
								}
							}
							
							updateView();
							
							return true;
						} else if( support.isDataFlavorSupported( df ) ) {							
							Object obj = support.getTransferable().getTransferData( df );
							InputStream is = (InputStream)obj;
							
							System.err.println( charset );
							importReader( new BufferedReader(new InputStreamReader(is, charset)) );
							
							updateView();
							
							return true;
						}  else if( support.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {							
							Object obj = support.getTransferable().getTransferData( DataFlavor.stringFlavor );
							String str = (String)obj;
							importReader( new BufferedReader( new StringReader(str) ) );
							
							updateView();
							
							return true;
						}
					} catch (UnsupportedFlavorException | IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			};
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return th;
	}
	
	class Ab1FileVisitor extends SimpleFileVisitor<Path> {
	
	};
	
	public void importFile( File f ) throws IOException {
		String fname = f.getName();
		if( fname.endsWith(".ab1") ) {
			int flen = (int)f.length();
			ByteBuffer bb = ByteBuffer.allocate( flen );
			FileInputStream fis = new FileInputStream( f );
			fis.read( bb.array() );
			fis.close();
			Ab1Reader abi = new Ab1Reader( bb );
			Sequence s = new Sequence( f.getName(), serifier.mseq );
			s.append( abi.getSequence() );
			serifier.lseq.add( s );
			
			if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
			
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
					if( serifier.mseq.containsKey( query ) ) {
						Sequence seq = serifier.mseq.get( query );
						int ind = serifier.lseq.indexOf( seq );
						if( ind >= k ) {
							tseq = seq;
							serifier.lseq.remove( seq );
							serifier.lseq.add(k++, seq);
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
								if( serifier.mseq.containsKey( val ) ) {
									seq = serifier.mseq.get( val );
									ind = serifier.lseq.indexOf( seq );
									if( ind >= k ) {
										serifier.lseq.remove( seq );
										serifier.lseq.add(k, seq);
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
								seq.setRevComp( 2 );
								int sval = qstart-(seq.length()-sstart+1);
								seq.setStart( sval );
							} else {
								seq.setStart( qstart-sstart );
							}
							k++;
						} else if( tseq != null ) {
							if( sstart > sstop ) {
								if( seq.getRevComp() == 2 ) {
									int sval = (seq.length()-sstart+1)-qstart  +  seq.getStart();
									tseq.setStart( sval );
								} else {
									tseq.setRevComp( 2 );
									int sval = sstart-qstart  +  seq.getStart();
									tseq.setStart( sval );
								}
							} else {
								if( seq.getRevComp() == 2 ) {
									tseq.setRevComp( 2 );
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
			
			serifier.checkMaxMin();
			updateView();
		} else {
			BufferedReader	br = new BufferedReader( new FileReader( f ) );
			importReader( f.getName(), br );
		}
	}
	
	static class RepeatNum implements Comparable<RepeatNum> {
		public RepeatNum( String r, int t ) {
			this.repeat = r;
			this.total = t;
		}
		
		String 	repeat;
		int		total;
		
		@Override
		public int compareTo(RepeatNum o) {
			return o.total-total;
		}
	}
	
	public void drawPhys( Graphics2D g2, List<Sequence> seqs, int fasti, int maxseqlen, int offset, int w, int h) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//g2.setFont( g2.getFont().deriveFont(24.0f) );
		g2.setColor( Color.white );
		g2.fillRect(0, 0, w, h);
		g2.setColor( Color.darkGray );
		int y = 0;
		
		g2.setFont( g2.getFont().deriveFont(18.0f) );
		for( Sequence seq : seqs ) {
			String spec = seq.getName(); //Sequence.nameFix(seq.getName(), true);
			
			g2.setColor( Color.darkGray );
			String nspec = spec.substring(0,Math.min(30, spec.length()));
			int strw = g2.getFontMetrics().stringWidth(nspec);
			g2.drawString(nspec, 350-strw, y*fasti+30);
			
			for( int i = 0; i < maxseqlen; i++ ) {
				char c = seq.charAt(i);
				if( c != '-' && c != ' ' && c != 'N' ) {
					Annotation inanno = null;
					if( seq.annset != null ) for( Annotation a : seq.annset ) {
						if( i >= a.start && i <= a.stop ) {
							inanno = a;
							//break;
						}
						
						if( i == a.start ) {
							break;
						}
					}
					
					Map<String,Integer> countmap = new HashMap<String,Integer>();
					for( Sequence sseq : seqs ) {
						if( sseq.annset != null ) for( Annotation a : sseq.annset ) {
							if( i >= a.start && i <= a.stop ) {
								String agroup = a.getGroup();
								if( countmap.containsKey(agroup) ) {
									countmap.put(agroup, countmap.get(agroup)+1);
								} else {
									countmap.put(agroup, 1);
								}
								break;
							}
						}
					}
					
					boolean large = false;
					
					int drawi = offset+(i*2400)/maxseqlen;
					if( inanno != null && inanno.color != null && inanno.color instanceof Color ) {
						g2.setColor( (Color)inanno.color );
						//if( inanno.ori == 1 ) g2.drawLine(drawi, y*fasti+5, drawi+1, y*fasti+15);
						//else g2.drawLine(drawi+1, y*fasti+5, drawi, y*fasti+15);
						Integer count = countmap.get( inanno.getGroup() );
						large = count != null && count > seqs.size()/2;
						if( large ) {
							g2.drawLine(drawi, y*fasti+8, drawi, y*fasti+32);
						} else {
							g2.drawLine(drawi, y*fasti+14, drawi, y*fasti+26);
						}
						//g2.drawLine(drawi, y*fasti+5, drawi, y*fasti+15);
					} else {
						g2.setColor( Color.darkGray );
						g2.drawLine(drawi, y*fasti+18, drawi, y*fasti+22);
					}
					
					/*if( inanno != null && inanno.name.contains("Cas4") ) {
						System.err.println();
					}
					
					if( inanno != null && inanno.name.contains("Cas5") ) {
						System.err.println();
					}*/
					
					if( inanno != null && i == inanno.start && inanno.type == null ) { //&& (seq == serifier.lseq.get(serifier.lseq.size()-1) || !large) ) {
						g2.setColor( Color.darkGray );
						
						/*if( inanno.name.contains("Cas5") ) {
							System.err.println();
						}
						
						if( inanno.name.contains("Cas4") ) {
							System.err.println();
						}*/
						
						int val = inanno.getName().length();
						int bil = (inanno.stop*2400)/maxseqlen - (inanno.start*2400)/maxseqlen;
						String str = inanno.getName().substring(0,Math.min(val, inanno.getName().length()));
						strw = g2.getFontMetrics().stringWidth(str);
						while( strw > bil ) {
							val--;
							str = inanno.getName().substring(0,Math.min(val, inanno.getName().length()));
							strw = g2.getFontMetrics().stringWidth(str);
						}
						//if( str.contains("hypo") ) {
						//	g2.drawString("hyp", drawi, y*fasti+50);
						//} else 
						g2.drawString(str, drawi, y*fasti+50);
					}
				}
			}
			y++;
		}
	}

	public static BufferedImage showRelation(List<String> specset, ANIResult aniResult, boolean inverted ) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		int mstrw = 0;
		for (String spec : specset) {
			String spc = spec;//nameFix( spec );
			int tstrw = g2.getFontMetrics().stringWidth(spc);
			if (tstrw > mstrw)
				mstrw = tstrw;
		}

		int sss = mstrw + 72 * specset.size() + 10 + 72;
		bi = new BufferedImage(sss, sss, BufferedImage.TYPE_INT_RGB);
		g2 = (Graphics2D) bi.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.white);
		g2.fillRect(0, 0, sss, sss);

		double[] matrix = aniResult.corrarr;
		int[] cmatrix = aniResult.countarr;
		boolean hasCount = Arrays.stream(cmatrix).anyMatch(p -> p > 1);

		int where = 0;
		for (String spec1 : specset) {
			int wherex = 0;
			int w = where; //specset.indexOf( spec1 );
			//String spc1 = nameFix( spec1 );
			int strw = g2.getFontMetrics().stringWidth(spec1);

			g2.setColor(Color.black);
			g2.drawString(spec1, mstrw - strw, mstrw + 47 + where * 72);
			g2.rotate(Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			g2.drawString(spec1, mstrw + 47 + where * 72, mstrw - strw);
			g2.rotate(-Math.PI / 2.0, mstrw + 47 + where * 72, mstrw - strw);
			//String spc1 = nameFix( spec1 );
			for (String spec2 : specset) {
				if( where != wherex ) {
					int wx = specset.indexOf( spec2 );//corrInd.indexOf( spec2 );
					double ani = inverted ? 1.0-matrix[ w*specset.size()+wx ] : matrix[ w*specset.size()+wx ];
					int count = cmatrix[ w*specset.size()+wx ];

					//float cval = Math.min( 0.9f, Math.max( 0.0f,4.2f - (float)(4.2*ani) ) );
					float cval = Math.min( 0.9f, Math.max( 0.0f, 1.2f - (float)(1.2*ani) ) );
					System.err.println( cval + "  " + ani );
					Color color = new Color( cval, cval, cval );
					g2.setColor( color );
					g2.fillRoundRect(mstrw + 10 + wherex * 72, mstrw + 10 + where * 72, 64, 64, 16, 16);

					g2.setColor( Color.white );
					String str = String.format("%.1f%s", (float) (ani * 100.0), "%");
					int nstrw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 + 15);

					if (hasCount) {
						str = String.format("%d", count);
						nstrw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, mstrw + 42 + wherex * 72 - nstrw / 2, mstrw + 47 + where * 72 + 2);
					}
				}
				wherex++;
			}
			where++;
		}

		return bi;
	}

	public static void seqIdent(Sequence seq1, Sequence seq2, boolean all, Map<String, Integer> blosumap, ANIScore aniScore) {
		int mest = 0;
		int tmest = 0;

		int start = -1;
		int stop = -1;
		for (int i = 0; i < seq1.length(); i++) {
			int startcheck = 0;
			if (seq1.getCharAt(i) != '-') {
				startcheck |= 1;
			}
			if (seq2.getCharAt(i) != '-') {
				startcheck |= 2;
			}

			if (startcheck == 3) {
				start = i;
				break;
			}
		}

		for (int i = seq1.length() - 1; i >= 0; i--) {
			int stopcheck = 0;
			if (seq1.getCharAt(i) != '-') {
				stopcheck |= 1;
			}
			if (seq2.getCharAt(i) != '-') {
				stopcheck |= 2;
			}

			if (stopcheck == 3) {
				stop = i + 1;
				break;
			}
		}
		//count += stop-start;

		if(all) {
			for (int i = 0; i < seq1.length(); i++) {
				char lc = seq1.getCharAt(i);
				char lc2 = seq2.getCharAt(i);
				//char c = Character.toUpperCase(lc);
				//char c2 = Character.toUpperCase(lc2);
				if (lc != '-' /*|| lc2 != '-'*/) {
					tmest++;
												/*String comb = c + "" + c;
												if (blosumap.containsKey(comb)) tmest += blosumap.get(comb);*/
				}
			}

			for (int i = 0; i < seq1.length(); i++) {
				char lc = seq1.getCharAt(i);
				char c = Character.toUpperCase(lc);
				char lc2 = seq2.getCharAt(i);
				char c2 = Character.toUpperCase(lc2);

				if (lc != '-' /*&& lc2 != '-'*/) {
					if (c == c2) mest++;
												/*String comb = c + "" + c2;
												if (blosumap.containsKey(comb)) mest += blosumap.get(comb);*/
				}
			}
		} else {
			for (int i = start; i < stop; i++) {
				char lc = seq1.getCharAt(i);
				char c = Character.toUpperCase(lc);
				String comb = c + "" + c;
				if (blosumap.containsKey(comb)) tmest += blosumap.get(comb);
			}

			for (int i = start; i < stop; i++) {
				char lc = seq1.getCharAt(i);
				char c = Character.toUpperCase(lc);
				char lc2 = seq2.getCharAt(i);
				char c2 = Character.toUpperCase(lc2);
				String comb = c + "" + c2;
				if (blosumap.containsKey(comb)) mest += blosumap.get(comb);
			}
		}

		double tani = (double) mest / (double) tmest;
		if (tani > (double) aniScore.score / (double) aniScore.tscore) {
			aniScore.score = mest;
			aniScore.tscore = tmest;
		}
		//ret = (double)score/(double)tscore; //int cval = tscore == 0 ? 0 : Math.min( 192, 512-score*512/tscore );
		//return ret;
	}

	public static ANIResult corr(List<String> speclist, Collection<GeneGroup> agg, boolean diff, boolean all) {
		Map<String, Integer> blosumap = getBlosumMap();
		Collection<GeneGroup> allgg = Collections.synchronizedCollection(agg);

		ANIResult aniResult = new ANIResult(speclist.size());
		IntStream.range(0,speclist.size()).parallel().forEach(where -> {
			String spec1 = speclist.get(where);
			int wherex = 0;

			//String spc1 = geneset.nameFix(spec1);
			for (String spec2 : speclist) {
				System.err.println(spec1 + " vs " + spec2);
				//boolean both = spec1.contains("15-6") && spec2.contains("GBK1");
				if (where != wherex) {
					int totalscore = 0;
					int totaltscore = 1;
					int count = 0;
					for (GeneGroup gg : allgg) {
						Set<String> species = gg.getSpecies();
						if ( species != null && species.contains(spec1) && species.contains(spec2)) {
							Teginfo ti1 = gg.species.get(spec1);
							Teginfo ti2 = gg.species.get(spec2);
							//if( ti1.tset.size() == 1 && ti2.tset.size() == 1 ) {
							//double bval = 0.0;

							var aniScore = new ANIScore();
							for (Annotation tv1 : ti1.tset) {
								//int maxscore;
								//int maxtscore;
								for (Annotation tv2 : ti2.tset) {
									Sequence seq1 = tv1.getAlignedSequence();
									Sequence seq2 = tv2.getAlignedSequence();
									if (seq1 != null && seq2 != null) {
										seqIdent(seq1, seq2, all, blosumap, aniScore);
									}
									//if( where == 0 ) d1.add( gg.getCommonName() );
									//else d2.add( gg.getCommonName() );
								}
							}
							if(aniScore.score>0) count++;
							totalscore += aniScore.score;
							totaltscore += aniScore.tscore;

										/*if( bval > 0 ) {
											ani += bval;
											count++;
										}*/
							//}
						}
					}
					double ani = (diff ? (double) (totaltscore - totalscore) : totalscore) / (double) totaltscore;
					aniResult.corrarr[where * speclist.size() + wherex] = ani;
					aniResult.countarr[where * speclist.size() + wherex] = count;
				}
				wherex++;
			}
		});
		System.err.println("done");
		return aniResult;
	}

	public static void showAniMatrix(List<String> specList, ANIResult aniResult) {
		final BufferedImage bi = showRelation( specList, aniResult, false );
		JFrame f = new JFrame("TNI matrix");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setSize(500, 500);

		var ff = new File("/Users/sigmar/simmi.png");
		try {
			ImageIO.write(bi, "PNG", ff);
			Desktop.getDesktop().open(ff);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JComponent comp2 = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), this);
			}
		};
		Dimension dim = new Dimension(bi.getWidth(),bi.getHeight());
		comp2.setPreferredSize(dim);
		comp2.setSize( dim );
		JScrollPane scroll = new JScrollPane(comp2);
		f.add(scroll);

		f.setVisible( true );
	}
	
	static JFrame		fxframe = new JFrame();
	static JFXPanel		fxp = new JFXPanel();
	static Scene 		scene;
	int[]				currentRowSelection;
	Point				p;
	public void initGui( final Container cnt ) {
		currentCnt = cnt;
		
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu view = new JMenu("View");
		JMenu anno = new JMenu("Annotation");
		JMenu name = new JMenu("Name");
		JMenu group = new JMenu("Groups");
		JMenu phylogeny = new JMenu("Phylogeny");
		
		AbstractAction reorderGroups = new AbstractAction( "Reorder groups" ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				TableModel tm = new TableModel() {
					@Override
					public int getRowCount() {
						return serifier.lgseq.size();
					}

					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public String getColumnName(int columnIndex) {
						return "Group";
					}

					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return String.class;
					}

					@Override
					public boolean isCellEditable(int rowIndex, int columnIndex) {
						return false;
					}

					@Override
					public Object getValueAt(int rowIndex, int columnIndex) {
						return serifier.lgseq.get( rowIndex );
					}

					@Override
					public void setValueAt(Object aValue, int rowIndex,int columnIndex) {}

					@Override
					public void addTableModelListener(TableModelListener l) {}

					@Override
					public void removeTableModelListener(TableModelListener l) {}
				};
				JTable gtable = new JTable();
				gtable.setDragEnabled( true );
				TransferHandler th = dragRows( gtable );
				gtable.setTransferHandler( th );
				gtable.setModel( tm );
				JScrollPane	gscroll = new JScrollPane( gtable );
				JOptionPane.showMessageDialog(null, gscroll);
				
				int o = 0;
				for( int r = 0; r < gtable.getRowCount(); r++ ) {
					Sequence gseq = (Sequence)gtable.getValueAt(r, 0);
					List<Sequence> lgseq = serifier.gseq.get( gseq.getName() );
					for( Sequence seq : lgseq ) {
						seq.setStart( seq.getStart() + (o-gseq.getStart()) );
					}
					gseq.setStart( o );
					o += gseq.length();
				}
			}
		};
		group.add( reorderGroups );
		//Window window = SwingUtilities.windowForComponent(cnt);
		initDataStructures();
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		table.setDragEnabled( true );
		
		Action action = new CopyAction("Copy", null, "Copy data", KeyEvent.VK_CONTROL + KeyEvent.VK_C);
		table.getActionMap().put("copy", action);
		
		/*try {
			if (clipboardService == null)
				clipboardService = (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
			grabFocus = true;
		} catch (Exception ee) {
			ee.printStackTrace();
			System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
		}*/
		
		final Ruler ruler = new Ruler( 10.0 );
		c = new FastaView( table.getRowHeight(), ruler, table );
		c.addMouseListener( new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				p = e.getPoint();
				c.requestFocus();
				if( e.isShiftDown() ) {
					if( c.selectedRect.width > 0 ) {
						//c.selectedRect.x = (int)(p.x/c.cw);
						//c.selectedRect.y = (int)(p.y/c.rh);
						double npx = c.cw*c.selectedRect.x;
						double npy = c.rh*c.selectedRect.y;
						c.selectedRect.width = (int)((p.x-npx)/c.cw)+1;
						c.selectedRect.height = (int)((p.y-npy)/c.rh)+1;
					} else {
						c.selectedRect.x = (int)(p.x/c.cw);
						c.selectedRect.y = p.y/c.rh;
						c.selectedRect.width = 1;
						c.selectedRect.height = 1;
					}
				} else {
					c.selectedRect.width = 0;
					c.selectedRect.height = 0;
				}
				c.repaint();
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
				
				if( e.isShiftDown() ) {
					c.selectedRect.x = (int)(p.x/c.cw);
					c.selectedRect.y = p.y/c.rh;
					c.selectedRect.width = (int)((np.x-p.x)/c.cw)+1;
					c.selectedRect.height = ((np.y-p.y)/c.rh) +1;
					c.repaint();
				} else {
					Rectangle r = c.getVisibleRect();
					if( p != null ) {
						r.translate(p.x-np.x, p.y-np.y);
						c.scrollRectToVisible( r );
					}
				}
				
				//p = np;
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});
		
		//final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return serifier != null ? serifier.lseq.size() : 0;
			}

			@Override
			public int getColumnCount() {
				return 9;
			}

			@Override
			public String getColumnName(int columnIndex) {
					if( columnIndex == 0 ) return "Name";
				else if( columnIndex == 1 ) return "Group";
				
				else if( columnIndex == 2 ) return "Length";
				else if( columnIndex == 3 ) return "#Anno";
				else if( columnIndex == 4 ) return "Unaligned length";
				else if( columnIndex == 5 ) return "Start";
				else if( columnIndex == 6 ) return "RevComp";
				else if( columnIndex == 7 ) return "GC%";
				else if( columnIndex == 8 ) return "Sort";
				return null;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return String.class;
				else if( columnIndex == 1 ) return String.class;
				else if( columnIndex == 2 ) return Integer.class;
				else if( columnIndex == 3 ) return Integer.class;
				else if( columnIndex == 4 ) return Integer.class;
				else if( columnIndex == 5 ) return Integer.class;
				else if( columnIndex == 6 ) return Integer.class;
				else if( columnIndex == 7 ) return Float.class;
				else if( columnIndex == 8 ) return String.class;
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 0 || columnIndex == 1 || columnIndex == 5;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if( rowIndex < serifier.lseq.size() ) {
					Sequence seq = serifier.lseq.get( rowIndex );
					if( columnIndex == 0 ) return seq.getName();
					else if( columnIndex == 1 ) return seq.getGroup();
					else if( columnIndex == 2 ) return seq.getAlignedLength();
					else if( columnIndex == 3 ) return seq.getAnnotations() != null ? seq.getAnnotations().size() : 0;
					else if( columnIndex == 4 ) return seq.getUnalignedLength();
					else if( columnIndex == 5 ) return seq.getRealStart();
					else if( columnIndex == 6 ) return seq.getRevComp();
					else if( columnIndex == 7 ) return seq.getGCP();
					else if( columnIndex == 8 ) {
						int begin = c.selectedRect.x-seq.getStart();
						int stop = begin+c.selectedRect.width;
						
						int start = Math.max(begin, 0);
						int end = Math.min(stop, seq.length());
						if( end > start ) {
							if( begin < 0 ) {
								String val = String.format( "%"+c.selectedRect.width+"s", seq.getSubstring( start, end, 1 ) );
								return val;
							}
							return seq.getSubstring( start, end, 1 );
						}
						
						return "";
					}
				}
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				if( columnIndex == 0 ) {
					Sequence seq = serifier.lseq.get( rowIndex );
					seq.setName( aValue.toString() );
				} else if( columnIndex == 1 ) {
					Sequence seq = serifier.lseq.get( rowIndex );
					seq.setGroup( aValue.toString() );
				} else if( columnIndex == 5 ) {
					Sequence seq = serifier.lseq.get( rowIndex );
					seq.setStart( (Integer)aValue );
				}
			}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		
		table.getRowSorter().addRowSorterListener(e -> {
            c.repaint();
            overview.reval();
            overview.repaint();
        });
		
		splitpane = new JSplitPane();
		splitpane.setDividerLocation(0.7);
		splitpane.setBackground( Color.white );
		
		JScrollPane	fastascroll = new JScrollPane( c );
		fastascroll.setBackground( Color.white );
		fastascroll.getViewport().setBackground( Color.white );
		fastascroll.setRowHeaderView( table );
		fastascroll.setColumnHeaderView( ruler );
		
		fastascroll.getHorizontalScrollBar().setUnitIncrement( (int)c.cw );
		
		JScrollPane	tablescroll = new JScrollPane();
		tablescroll.setViewport( fastascroll.getRowHeader() );
		tablescroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		tablescroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		tablescroll.setBackground( Color.white );
		tablescroll.getViewport().setBackground( Color.white );
		
		try {
			final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws IOException {
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<Sequence>	selseq = new ArrayList<>(rr.length);
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( serifier.lseq.get(i) );
						}
						return selseq;
					} else {
						StringBuilder ret = new StringBuilder();//makeCopyString();
						for( int r = 0; r < table.getRowCount(); r++ ) {
							Object o = table.getValueAt(r, 0);
							if( o != null ) {
								ret.append(o);
							} else {
							}
							for( int c = 1; c < table.getColumnCount(); c++ ) {
								o = table.getValueAt(r, c);
								if( o != null ) {
									ret.append("\t").append(o);
								} else {
									ret.append("\t");
								}
							}
							ret.append("\n");
						}
						//return arg0.getReaderForText( this );
						return new ByteArrayInputStream( ret.toString().getBytes( charset ) );
					}
					//return ret;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { df, ndf };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					if( arg0.equals(df) || arg0.equals(ndf) ) {
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
						
						DataFlavor[] dfs = support.getDataFlavors();
						if( support.isDataFlavorSupported( ndf ) ) {					
							Object obj = support.getTransferable().getTransferData( ndf );
							ArrayList<Sequence>	seqs = (ArrayList<Sequence>)obj;
							
							ArrayList<Sequence> newlist = new ArrayList<>(serifier.lseq.size());
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( serifier.lseq.get(i) );
							}
							serifier.lseq.clear();
							serifier.lseq = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							serifier.lseq.removeAll( seqs );
							for( Sequence s : seqs ) {
								serifier.lseq.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							c.repaint();
							
							return true;
						} else if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
							Object obj = support.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
							//InputStream is = (InputStream)obj;
							List<File>	lfile = (List<File>)obj;
							
							for( File f : lfile ) {
								if( f.isDirectory() ) {
									Files.walkFileTree( f.toPath(), new SimpleFileVisitor<Path>() {
										public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
									        if (attr.isSymbolicLink()) {
									            System.out.format("Symbolic link: %s ", file);
									        } else if (attr.isRegularFile() && file.getFileName().toString().endsWith(".ab1")) {
												System.err.println( file.getFileName() + "  " + file.toFile().length() );
												try {
													importFile( file.toFile() );
												} catch (IOException e) {
													e.printStackTrace();
												}
									        } else {
									            System.out.format("Other: %s ", file);
									        }
									       
									        return FileVisitResult.CONTINUE;
										}
									});
								} else {
									importFile( f );
								}
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
										if( s.length() > max ) max = s.length();
									}*/
							}
							
							serifier.checkMaxMin();
							updateView();
								
							return true;
						} else if( support.isDataFlavorSupported( df ) ) {							
							Object obj = support.getTransferable().getTransferData( df );
							InputStream is = (InputStream)obj;
							
							System.err.println( charset );
							importReader( new BufferedReader(new InputStreamReader(is, charset)) );
							
							serifier.checkMaxMin();
							updateView();
							
							return true;
						}  else if( support.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {							
							Object obj = support.getTransferable().getTransferData( DataFlavor.stringFlavor );
							String str = (String)obj;
							importReader( new BufferedReader( new StringReader(str) ) );
							
							serifier.checkMaxMin();
							updateView();
							
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
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		JTextField	textfield = new JTextField();
		JComponent tablecomp = new JComponent() { private static final long serialVersionUID = 1L; };
		tablecomp.setLayout( new BorderLayout() );
		tablecomp.add( tablescroll );
		tablecomp.add( textfield, BorderLayout.SOUTH );
		
		splitpane.setLeftComponent( tablecomp );
		splitpane.setRightComponent( fastascroll );
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("View 2-state PCA matrix") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				double[] X = get2StatePCAMatrix( rr );
				int isize = X.length/rr.length;
				
				StringBuilder sb = new StringBuilder();
				for( int i = 0; i < X.length; i++ ) {
					sb.append( X[i] );
					if( i % isize == isize-1 ) sb.append("\n");
					else sb.append("\t");
				}
				
				StringBuilder col = new StringBuilder();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					String name = seq.getName();
					int i = name.indexOf("[#");
					if( i == -1 ) {
						col.append( "0.0\t0.0\t0.0\n" );
					} else {
						col.append( Integer.parseInt(name.substring(i+2, i+4), 16)+"\t" );
						col.append( Integer.parseInt(name.substring(i+4, i+6), 16)+"\t" );
						col.append( Integer.parseInt(name.substring(i+6, i+8), 16)+"\n" );
					}
				}
				
				//Map<String,StringBuilder>	sbmap = new HashMap<String,StringBuilder>();
				List<Sequence> ls = new ArrayList<Sequence>();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					String name = seq.getName();
					StringBuilder sub = new StringBuilder();
					for( int i = 0; i < isize; i++ ) {
						if( X[r*isize+i] == 1.0 ) sub.append("1");
						else sub.append("0");
					}
					
					Sequence subseq = new Sequence( name, name, sub, null );
					ls.add( subseq );
					
					//sbmap.put(name, sub);
					/*fas.append( ">"+name );
					for( int i = 0; i < isize; i++ ) {
						if( i % 70 == 0 ) fas.append("\n");
						if( X[r*isize+i] == 1.0 ) fas.append("1");
						else fas.append("0");
					}
					fas.append("\n");*/
				}
				String restext = Sequence.getPhylip( ls, false );				
				
				try {
					FileWriter fw = new FileWriter("/Users/sigmar/ok.txt");
					fw.write( sb.toString() );
					fw.close();
					
					fw = new FileWriter("/Users/sigmar/col.txt");
					fw.write( col.toString() );
					fw.close();
					
					fw = new FileWriter("/Users/sigmar/2state.fasta");
					fw.write( restext );
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("PCA for 2-state sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				double[] X = get2StatePCAMatrix( rr );
				int isize = X.length/rr.length;
				
				double[] u = new double[rr.length];
				double uu = 0.0;
				for( int i = 0; i < u.length; i++ ) {
					u[i] = X[i*isize];
					uu += u[i]*u[i];
				}
				double[] uold = new double[rr.length];
				double d = 0.0;
				
				int count = 0;
				do {
					double[] v = new double[isize];
					double vv = 0.0;
					for( int i = 0; i < v.length; i++ ) {
						double Xv = 0.0;
						for( int k = 0; k < rr.length; k++ ) {
							Xv += X[k*isize+i];
						}
						v[i] = Xv/uu;
						vv = v[i]*v[i];
					}
					double vlen = Math.sqrt( vv );
					for( int i = 0; i < v.length; i++ ) {
						v[i] /= vlen;
					}
					
					for( int i = 0; i < u.length; i++ ) {
						uold[i] = u[i];
					}
					
					uu = 0.0;
					for( int i = 0; i < u.length; i++ ) {
						u[i] = 0.0;
						for( int k = 0; k < v.length; k++ ) {
							u[i] += X[i*v.length+k]*v[k];
						}
						u[i] /= vv;
						
						uu += u[i]*u[i];
					}
					
					d = 0.0;
					for( int i = 0; i < u.length; i++ ) {
						double ud = uold[i] - u[i];
						d += ud*ud;
					}
					d = Math.sqrt(d);
					 
					System.err.println( d );
					count++;
			 	} while( d > 1.0e-8 );
				
				System.err.println( "success after "+count+" iteration" );
			}
		});
		popup.add( new AbstractAction("Spark PCA") {
			@Override
			public void actionPerformed(ActionEvent e) {
				SparkSession sparkSession = SparkSession.builder().master("local[*]").getOrCreate();

				try {
					Map<String,String> name2country = Files.lines(Paths.get("/Users/sigmar/Downloads/sequences.csv")).map(s -> s.split(",")).collect(Collectors.toUnmodifiableMap(s -> s[0], s -> s.length >= 2 ? Arrays.stream(s).skip(1).map(String::trim).collect(Collectors.joining("_")) : "Unknown"));
					int[] rr = table.getSelectedRows();
					/*rr = Arrays.stream(rr).filter(i -> {
						int k = table.convertRowIndexToModel( i );
						Sequence seq = serifier.lseq.get( k );
						String name = seq.getName().trim();
						String country = name2country.get(name);
						return !country.contains("USA: WA");
					}).toArray();*/
					double[] X = get2StatePCAMatrix( rr );
					int isize = X.length/rr.length;

					List<String> names = new ArrayList<>();
					List<Vector> data = new ArrayList<>();
					int m = 0;
					for(int i : rr) {
						double[] xx = null;
						try {
							xx = Arrays.copyOfRange(X,m*isize,m*isize+isize);
						} catch(Exception iie) {
							iie.printStackTrace();
						}
						Vector vec = Vectors.dense(xx);
						data.add(vec);

						int k = table.convertRowIndexToModel( i );
						Sequence seq = serifier.lseq.get( k );
						names.add(seq.getName().trim());

						m++;
					}

					Encoder<Vector> enc = Encoders.javaSerialization(Vector.class);
					Dataset<Vector> rows = sparkSession.createDataset(data, enc);//sparkContext().parallelize(data);

					// Create a RowMatrix from JavaRDD<Vector>.
					RowMatrix mat = new RowMatrix(rows.rdd());

					// Compute the top 4 principal components.
					// Principal components are stored in a local dense matrix.
					Matrix pc = mat.computePrincipalComponents(4);

					// Project the rows to the linear space spanned by the top 4 principal components.
					RowMatrix projected = mat.multiply(pc);

					DenseMatrix dm = projected.toBreeze();

					Path p = Paths.get("/Users/sigmar/pcares.txt");
					try(BufferedWriter fw = Files.newBufferedWriter(p)) {
						for(int i = 0; i < names.size(); i++) {
							String name = names.get(i);
							String country = name2country.get(name);
							fw.write(country.startsWith("\"") ? country.substring(1,country.length()-1) : country);
							fw.write('\t');
							fw.write(dm.apply(i, 0).toString());
							fw.write('\t');
							fw.write(dm.apply(i, 1).toString());
							fw.write('\t');
							fw.write(dm.apply(i, 2).toString());
							fw.write('\n');
						}
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					System.err.println(dm.toString());
					System.err.println();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				/*Arrays.asList(
						Vectors.sparse(5, new int[] {1, 3}, new double[] {1.0, 7.0}),
						Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
						Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0)
				);*/

				/*Platform.runLater(() -> {
					WebView webView = new WebView();
					webView.getEngine().load("http://www.mbl.is");
					final Stage dialog = new Stage();
					Scene dialogScene = new Scene(webView, 800, 600);
					dialog.setScene(dialogScene);
					dialog.show();
				});*/



				/*double[] u = new double[rr.length];
				double uu = 0.0;
				for( int i = 0; i < u.length; i++ ) {
					u[i] = X[i*isize];
					uu += u[i]*u[i];
				}
				double[] uold = new double[rr.length];
				double d = 0.0;

				int count = 0;
				do {
					double[] v = new double[isize];
					double vv = 0.0;
					for( int i = 0; i < v.length; i++ ) {
						double Xv = 0.0;
						for( int k = 0; k < rr.length; k++ ) {
							Xv += X[k*isize+i];
						}
						v[i] = Xv/uu;
						vv = v[i]*v[i];
					}
					double vlen = Math.sqrt( vv );
					for( int i = 0; i < v.length; i++ ) {
						v[i] /= vlen;
					}

					for( int i = 0; i < u.length; i++ ) {
						uold[i] = u[i];
					}

					uu = 0.0;
					for( int i = 0; i < u.length; i++ ) {
						u[i] = 0.0;
						for( int k = 0; k < v.length; k++ ) {
							u[i] += X[i*v.length+k]*v[k];
						}
						u[i] /= vv;

						uu += u[i]*u[i];
					}

					d = 0.0;
					for( int i = 0; i < u.length; i++ ) {
						double ud = uold[i] - u[i];
						d += ud*ud;
					}
					d = Math.sqrt(d);

					System.err.println( d );
					count++;
				} while( d > 1.0e-8 );

				System.err.println( "success after "+count+" iteration" );*/
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Select right paired-end") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				for( Sequence tseq : serifier.lseq ) {
					if( tseq.getName().contains("right") ) {
						int r = table.convertRowIndexToView(i);
						if( r >= 0 && r < table.getRowCount() ) table.addRowSelectionInterval(r, r);
					}
					i++;
				}
			}
		});
		popup.add( new AbstractAction("Select left paired-end") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				for( Sequence tseq : serifier.lseq ) {
					if( tseq.getName().contains("left") ) {
						int r = table.convertRowIndexToView(i);
						if( r >= 0 && r < table.getRowCount() ) table.addRowSelectionInterval(r, r);
					}
					i++;
				}
			}
		});
		popup.add( new AbstractAction("Go to paired-end") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel(r);
				Sequence seq = serifier.lseq.get(i);
				int k = seq.getName().indexOf('_');
				
				String searchstr = null;
				if( seq.getName().contains("left") ) {
					searchstr = seq.getName().substring(0, k+1)+"right";
				} else if( seq.getName().contains("right") ) {
					searchstr = seq.getName().substring(0, k+1)+"left";
				}
				
				if( searchstr != null ) {
					i = 0;
					for( Sequence tseq : serifier.lseq ) {
						if( tseq.getName().contains(searchstr) ) {
							r = table.convertRowIndexToView(i);
							if( r >= 0 && r < table.getRowCount() ) table.addRowSelectionInterval(r, r);
							
							Rectangle rc = c.getVisibleRect();
							rc.x = (int)(tseq.getStart()*c.cw);
							c.scrollRectToVisible(rc);
						}
						i++;
					}
				}
				//int u = seq.getName().indexOf('.',k+1);
				//String sname = seq.getName().substr
			}
			
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Sub sort") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] rr = table.getSelectedRows();
				Collections.sort( serifier.lseq.subList(rr[0], rr[rr.length-1]), new Comparator<Sequence>() {
					@Override
					public int compare(Sequence seq1, Sequence seq2) {
						String seq1str = "";
						String seq2str = "";
						
						int begin = c.selectedRect.x-seq1.getStart();
						int stop = begin+c.selectedRect.width;
						
						int start = Math.max(begin, 0);
						int end = Math.min(stop, seq1.length());
						if( end > start ) {
							if( begin < 0 ) {
								String val = String.format( "%"+c.selectedRect.width+"s", seq1.getSubstring( start, end, 1 ) );
								seq1str = val;
							} else seq1str = seq1.getSubstring( start, end, 1 );
						}
						
						begin = c.selectedRect.x-seq2.getStart();
						stop = begin+c.selectedRect.width;
						
						start = Math.max(begin, 0);
						end = Math.min(stop, seq2.length());
						if( end > start ) {
							if( begin < 0 ) {
								String val = String.format( "%"+c.selectedRect.width+"s", seq2.getSubstring( start, end, 1 ) );
								seq2str = val;
							} else seq2str = seq2.getSubstring( start, end, 1 );
						}
						
						return seq1str.compareTo( seq2str );
					}
				});
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});
		popup.add( new AbstractAction("Align MAFFT") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextField host = new JTextField("localhost");
				JOptionPane.showMessageDialog(null, host);
				String userhome = System.getProperty("user.home");
				String username = System.getProperty("user.name");
				String hostname = host.getText();
				
				 final List<Sequence> seqlist = new ArrayList<Sequence>();
		    	 int[] rr = table.getSelectedRows();
		    	 for( int r : rr ) {
		    		 int i = table.convertRowIndexToModel( r );
		    		 Sequence seq = serifier.lseq.get(i);
		    		 seqlist.add( seq );
		    	 }
		    	 final Path tmpdir = Paths.get( userhome );
		    	 try {
					//BufferedWriter fw = Files.newBufferedWriter( tmpdir.resolve( "tmp.fasta" ) ); //new FileWriter( new File( tmpdir, "tmp.fasta" ) );
					//serifier.writeFasta( seqlist, fw, null );
			    	//fw.close();
			    	String filename = "mafftfile.fasta";
		    		Path mafftp = tmpdir.resolve(filename);
		    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    		OutputStreamWriter osw = new OutputStreamWriter(baos);
		    		serifier.writeFasta( seqlist, osw, getSelectedRect() );
		    		osw.close();
		    		Files.write(mafftp, baos.toByteArray());
		    		
		    		//"--localpair"
			    	ProcessBuilder pb;
			    	if( hostname.contains("localhost") ) pb = new ProcessBuilder("/opt/homebrew/bin/mafft","--thread",Integer.toString(Runtime.getRuntime().availableProcessors()),filename); //, "-in", "tmp.fasta", "-out", "tmpout.fasta");
			    	else {
			    		ProcessBuilder pbt = new ProcessBuilder("scp", "-q", filename, username+"@"+hostname+":~/"+filename);
			    		pbt.directory( tmpdir.toFile() );
						Process pc = pbt.start();
						pc.waitFor();
						
			    		pb = new ProcessBuilder("ssh",username+"@"+hostname,"mafft","--thread",Integer.toString(Runtime.getRuntime().availableProcessors()),filename);
			    	}
			    	//ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/mafft","--thread","32","--localpair",mafftp.getFileName().toString()); //, "-in", "tmp.fasta", "-out", "tmpout.fasta");
			    	
			    	pb.directory( tmpdir.toFile() );
			    	//pb.redirectErrorStream(true);
			    	final Process p = pb.start();
			    	
			    	Thread t = new Thread(() -> {
						InputStream is = p.getErrorStream();
						try {
							ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
							int r = is.read();
							while( r != -1 ) {
								baos1.write(r);
								r = is.read();
							}
							is.close();
							baos1.close();

							System.err.println( "error " + baos1);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
			    	t.start();
			    	
			    	/*t = new Thread() {
			    		public void run() {
			    			try {
			    				OutputStream os = p.getOutputStream();
			    				OutputStreamWriter osw = new OutputStreamWriter( os );
			    				serifier.writeFasta( seqlist, osw, getSelectedRect() );
			    				osw.close();
				    			os.close();
				    			//serifier.lseq.removeAll( seqlist );
			    				
			    				/*for( Sequence seq : seqlist ) {
			    					if( seq.annset != null ) for( Annotation a : seq.annset ) {
			    						for( Sequence nseq : serifier.lseq ) {
			    							if( nseq.getName().equals(seq.getName()) ) {
			    								nseq.addAnnotation(a);
			    							}
			    						}
			    					}
			    				}
			    				
			    				for( Sequence seq : serifier.lseq ) {
			    					if( seq.annset != null ) {
			    						for( Annotation a : seq.annset ) {
			    							int cnt = 0;
			    							
			    							int newstart = 0;
			    							int newstop = 0;
			    							for( int i = 0; i < seq.length(); i++ ) {
			    								char c = seq.charAt(i);
			    								if( c != '-' ) {
			    									if( cnt == a.start ) newstart = i;
			    									else if( cnt == a.stop ) {
			    										newstop = i;
			    										break;
			    									}
			    									cnt++;
			    								}
			    							}
			    							a.start = newstart;
			    							a.stop = newstop;
			    						}
			    					} else {
			    						System.err.println("empt");
			    					}
			    				}*/
				    			
			    				/*ByteArrayOutputStream	baos = new ByteArrayOutputStream();
				    			int r = is.read();
						    	while( r != -1 ) {
						    		baos.write( r );
						    		r = is.read();
						    	}
						    	//System.out.println( baos.toString() );*/
						    	/*BufferedReader br = Files.newBufferedReader( tmpdir.resolve("tmpout.fasta") ); //new FileReader( new File( tmpdir, "tmpout.fasta" ) );
						    	//BufferedReader	br = new BufferedReader( fr );
						    	importReader( br );
						    	br.close();
						    	//fr.close();*
				    		} catch (IOException e) {
								e.printStackTrace();
							}
			    		}
			    	};
			    	t.start();*/
			    	
			    	InputStream is = p.getInputStream();
    				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
    				
    				Rectangle selr = getSelectedRect();
    				if( selr != null && selr.width > 0 ) {
	    				int start = selr.x;
	    				int end = start+selr.width;
	    				
	    				List<Sequence> seql = serifier.readSequences( br );
	    				for( Sequence fs : seqlist ) {
	    					for( Sequence ns : seql ) {
	    						if( fs.getName().equals(ns.getName()) ) {
	    							fs.replaceSelected( ns, start, end );
	    						}
	    					}
	    				}
	    				
	    				int len = seql.get(0).length();
	    				for( Sequence seq : seqlist ) {
	    					if( seq.annset != null ) {
	    						for( Annotation a : seq.annset ) {
	    							if( a.stop > start && a.start < end ) {
		    							int cnt = start;
		    							
		    							if( seq.getName().contains("teng") && a.getName().contains("Cas6") ) {
		    								System.err.println();
		    							}
		    							System.err.println(  seq.getName() + " bleh2 " + a.getName() );
		    							
		    							int bil = a.stop-a.start;
		    							int newstart = a.start;
		    							int newstop = a.stop;
		    							for( int i = start; i < start+len; i++ ) {
		    								char c = seq.charAt(i);
		    								if( c != '-' ) {
		    									if( cnt == a.start ) newstart = i;
		    									else if( cnt == a.stop ) {
		    										newstop = i;
		    										break;
		    									}
		    									cnt++;
		    								}
		    							}
		    							a.start = newstart;
		    							a.stop = Math.max(newstop,a.start+bil);
	    							} else if( a.start > end ) {
	    								a.start += len-(end-start);
	    								a.stop += len-(end-start);
	    							}
	    						}
	    					} else {
	    						System.err.println("empt");
	    					}
	    				}
    				} else {
    					serifier.lseq.removeAll( seqlist );
    					importReader( br );
    					
    					for( Sequence seq : seqlist ) {
	    					if( seq.annset != null ) for( Annotation a : seq.annset ) {
	    						for( Sequence nseq : serifier.lseq ) {
	    							if( nseq.getName().equals(seq.getName()) ) {
	    								nseq.addAnnotation(a);
	    							}
	    						}
	    					}
	    				}
    					
    					for( Sequence seq : seqlist ) {
	    					if( seq.annset != null ) {
	    						for( Annotation a : seq.annset ) {
	    							int cnt = 0;
	    							
	    							int newstart = 0;
	    							int newstop = 0;
	    							for( int i = 0; i < seq.length(); i++ ) {
	    								char c = seq.charAt(i);
	    								if( c != '-' ) {
	    									if( cnt == a.start ) newstart = i;
	    									else if( cnt == a.stop ) {
	    										newstop = i;
	    										break;
	    									}
	    									cnt++;
	    								}
	    							}
	    							a.start = newstart;
	    							a.stop = newstop;
	    						}
	    					} else {
	    						System.err.println("empt");
	    					}
	    				}
    				}
    				
    				br.close();
    				is.close();
    				
    				updateView();
		    	 } catch (IOException e) {
					e.printStackTrace();
				 } catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		popup.add( new AbstractAction("Align") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 final List<Sequence> seqlist = new ArrayList<Sequence>();
		    	 int[] rr = table.getSelectedRows();
		    	 for( int r : rr ) {
		    		 int i = table.convertRowIndexToModel( r );
		    		 Sequence seq = serifier.lseq.get(i);
		    		 seqlist.add( seq );
		    	 }
		    	 final Path tmpdir = Paths.get( System.getProperty("user.home") );
		    	 try {
					//BufferedWriter fw = Files.newBufferedWriter( tmpdir.resolve( "tmp.fasta" ) ); //new FileWriter( new File( tmpdir, "tmp.fasta" ) );
					//serifier.writeFasta( seqlist, fw, null );
			    	//fw.close();
			    	
			    	ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/muscle"); //, "-in", "tmp.fasta", "-out", "tmpout.fasta");
			    	pb.directory( tmpdir.toFile() );
			    	//pb.redirectErrorStream(true);
			    	final Process p = pb.start();
			    	
			    	Thread t = new Thread() {
			    		public void run() {
			    			InputStream is = p.getErrorStream();
			    			try {
								while( is.read() != -1 ) ;
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
			    		}
			    	};
			    	t.start();
			    	
			    	t = new Thread() {
			    		public void run() {
			    			try {
			    				OutputStream os = p.getOutputStream();
			    				OutputStreamWriter osw = new OutputStreamWriter( os );
			    				serifier.writeFasta( seqlist, osw, getSelectedRect() );
			    				osw.close();
				    			os.close();
				    			
			    				InputStream is = p.getInputStream();
			    				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
			    				
			    				Rectangle selr = getSelectedRect();
			    				if( selr != null && selr.width > 0 ) {
				    				int start = selr.x;
				    				int end = start+selr.width;
				    				
				    				List<Sequence> seqlist = serifier.readSequences( br );
				    				for( Sequence fs : serifier.lseq ) {
				    					for( Sequence ns : seqlist ) {
				    						if( fs.getName().equals(ns.getName()) ) {
				    							fs.replaceSelected( ns, start, end );
				    							
				    						}
				    					}
				    				}
			    				} else {
			    					serifier.lseq.removeAll( seqlist );
			    					importReader( br );
			    				}
			    				
			    				br.close();
			    				is.close();
			    				
			    				/*for( Sequence seq : seqlist ) {
			    					if( seq.annset != null ) for( Annotation a : seq.annset ) {
			    						for( Sequence nseq : serifier.lseq ) {
			    							if( nseq.getName().equals(seq.getName()) ) {
			    								nseq.addAnnotation(a);
			    							}
			    						}
			    					}
			    				}*/
			    				
			    				/*for( Sequence seq : serifier.lseq ) {
			    					if( seq.annset != null ) {
			    						for( Annotation a : seq.annset ) {
			    							int cnt = 0;
			    							
			    							int newstart = 0;
			    							int newstop = 0;
			    							for( int i = 0; i < seq.length(); i++ ) {
			    								char c = seq.charAt(i);
			    								if( c != '-' ) {
			    									if( cnt == a.start ) newstart = i;
			    									else if( cnt == a.stop ) {
			    										newstop = i;
			    										break;
			    									}
			    									cnt++;
			    								}
			    							}
			    							a.start = newstart;
			    							a.stop = newstop;
			    						}
			    					} else {
			    						System.err.println("empt");
			    					}
			    				}*/
				    			
			    				/*ByteArrayOutputStream	baos = new ByteArrayOutputStream();
				    			int r = is.read();
						    	while( r != -1 ) {
						    		baos.write( r );
						    		r = is.read();
						    	}
						    	//System.out.println( baos.toString() );*/
						    	
						    	
						    	 
						    	
						    	/*BufferedReader br = Files.newBufferedReader( tmpdir.resolve("tmpout.fasta") ); //new FileReader( new File( tmpdir, "tmpout.fasta" ) );
						    	//BufferedReader	br = new BufferedReader( fr );
						    	importReader( br );
						    	br.close();
						    	//fr.close();*/
						    	 
						    	//table.tableChanged( new TableModelEvent( table.getModel() ) );
			    				updateView();
				    		} catch (IOException e) {
								e.printStackTrace();
							}
			    		}
			    	};
			    	t.start();
		    	 } catch (IOException e) {
					e.printStackTrace();
				 }
			}
		});
		popup.add( new AbstractAction("Align refine") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<Sequence> seqlist = new ArrayList<Sequence>();
		    	 int[] rr = table.getSelectedRows();
		    	 for( int r : rr ) {
		    		 int i = table.convertRowIndexToModel( r );
		    		 Sequence seq = serifier.lseq.get(i);
		    		 seqlist.add( seq );
		    	 }
		    	 File tmpdir = new File("/tmp");
		    	 try {
					FileWriter fw = new FileWriter( new File( tmpdir, "tmp.fasta" ) );
					serifier.writeFasta( seqlist, fw, getSelectedRect() );
			    	fw.close();
			    	
			    	ProcessBuilder pb = new ProcessBuilder("muscle", "-in", "tmp.fasta", "-out", "tmpout.fasta");
			    	pb.directory( tmpdir );
			    	Process p = pb.start();
			    	InputStream os = p.getInputStream();
			    	while( os.read() != -1 ) ;
			    	
			    	// serifier.lseq.removeAll( seqlist );
			    	 
			    	 FileReader fr = new FileReader( new File( tmpdir, "tmpout.fasta" ) );
			    	 BufferedReader	br = new BufferedReader( fr );
			    	 List<Sequence> seqs = serifier.readSequences( br );
			    	 br.close();
			    	 fr.close();
			    	 
			    	 for( Sequence s : seqs ) {
			    		 Sequence ps = serifier.mseq.get( s.getName() );
			    		 ps.getSequence().replace( c.selectedRect.x, c.selectedRect.x+c.selectedRect.width, s.getSequence().toString() );
			    	 }
			    	 
			    	 table.tableChanged( new TableModelEvent( table.getModel() ) );
		    	 } catch (IOException e) {
					e.printStackTrace();
				 }
			}
		});
		//popup.addSeparator();
		
		edit.add( new AbstractAction("Merge same names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				//String name = "";
				Map<String,Set<Sequence>>	remseqmap = new HashMap<String,Set<Sequence>>();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					if( i >= 0 && i < serifier.lseq.size() ) {
						Sequence seq = serifier.lseq.get(i);
						
						System.err.println("same name se " + seq.getStart() + " " + seq.getEnd() + "  " + seq.offset );
						
						String name = seq.getName();
						Set<Sequence> remseq;
						if( remseqmap.containsKey( name ) ) {
							remseq = remseqmap.get( name );
						} else {
							remseq = new HashSet<Sequence>();
							remseqmap.put(name, remseq);
						}
						remseq.add( seq );
						/*if( name.length() == 0 ) name = seq.getName();
						else {
							int k = 0;
							char c = name.getCharAt(k);
							while( c == seq.getName().getCharAt(k) ) {
								k++;
								if( k == name.length() || k == seq.getName().length() ) break;
								c = name.getCharAt(k);
							}
							name = name.substring(0,k);
						}
						remseq.add( seq );*/
					}
				}
				
				System.err.println( "lseq size " + serifier.lseq.size() );
				for( String name : remseqmap.keySet() ) {
					Set<Sequence> remseq = remseqmap.get( name );
					Sequence newseq = new Sequence(name, serifier.mseq);
					int start = Integer.MAX_VALUE;
					int end = 0;
					for( Sequence s : remseq ) {
						if( s.getStart() < start ) start = s.getStart();
						if( s.getEnd() > end ) end = s.getEnd();
					}
					
					newseq.setStart( start );
					Map<Character,Integer>	charset = new TreeMap<Character,Integer>();
					Map<Character,Integer>	allcharset = new TreeMap<Character,Integer>();
					
					System.err.println( "now " + name + " " + start + " " + end );
					for( int i = start; i < end; i++ ) {
					//for( int i = 408; i < 410; i++ ) {
						for( Sequence s : remseq ) {
							char chr = getgetCharAt(i, s);
							char cc = Character.toUpperCase(chr);
							//if( c != '-' && c != ' ' ) 
							int k = 0;
							if( charset.containsKey(cc) ) {
								k = charset.get(cc);
							}
							charset.put( cc, k+1 );
						}
						
						for( Sequence s : serifier.lseq ) {
							char c = getgetCharAt(i, s);
							char cc = Character.toUpperCase(c);
							//if( c != '-' && c != ' ' ) 
							int k = 0;
							if( allcharset.containsKey(cc) ) {
								k = allcharset.get(cc);
							}
							allcharset.put( cc, k+1 );
						}
						
						if( charset.size() == 0 ) newseq.getSequence().append('-');
						else if( charset.size() == 1 ) newseq.getSequence().append( charset.keySet().iterator().next() );
						else if( charset.size() > 1 ) {
							char val = '-';
							int max = 0;
							for( char c : charset.keySet() ) {
								if( c != '-' && c != ' ' ) {
									int m = allcharset.get(c);
									if( m > max ) {
										max = m;
										val = c;
									}
								}
							}
							if( val == ' ' ) newseq.getSequence().append('-');
							else newseq.getSequence().append( val );
							
							/*	newseq.sb.append( charset.iterator().next() );
							} else if( charset.size() == 3 ) {
								newseq.sb.append( charset.iterator().next() );
							} else newseq.sb.append( 'N' );*/
						}
						
						charset.clear();
						allcharset.clear();
					}
					
					serifier.lseq.removeAll( remseq );
					serifier.lseq.add( newseq );
				}
				
				serifier.checkMaxMin();
				c.updateCoords();
				table.tableChanged( new TableModelEvent(table.getModel()) );
				c.repaint();
				overview.reval();
				overview.repaint();
			}
		});
		edit.add( new AbstractAction("Merge selected") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				String name = "";
				Set<Sequence>	remseq = new HashSet<>();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					if( name.length() == 0 ) name = seq.getName();
					else {
						int k = 0;
						char c = name.charAt(k);
						while( c == seq.getName().charAt(k) ) {
							k++;
							if( k == name.length() || k == seq.getName().length() ) break;
							c = name.charAt(k);
						}
						name = name.substring(0,k);
					}
					remseq.add( seq );
				}
				if( name.length() == 0 ) name = "newseq";
				Sequence newseq = new Sequence(name, serifier.mseq);
				int start = Integer.MAX_VALUE;
				int end = 0;
				for( Sequence s : remseq ) {
					if( s.getStart() < start ) start = s.getStart();
					if( s.getEnd() > end ) end = s.getEnd();
				}
				
				newseq.setStart( start );
				Map<Character,Integer>	charset = new TreeMap<>();
				for( int i = start; i < end; i++ ) {
					for( Sequence s : remseq ) {
						char c = s.getCharAt(i);
						char cc = Character.toUpperCase(c);
						//if( c != '-' && c != ' ' ) 
						int k = 0;
						if( charset.containsKey(cc) ) {
							k = charset.get(cc);
						}
						charset.put( cc, k+1 );
					}
					
					if( charset.size() == 0 ) newseq.getSequence().append('-');
					else if( charset.size() == 1 ) newseq.getSequence().append( charset.keySet().iterator().next() );
					else if( charset.size() > 1 ) {
						char val = '-';
						int max = 0;
						for( char c : charset.keySet() ) {
							if( c != '-' && c != ' ' ) {
								int m = charset.get(c);
								if( m > max ) {
									max = m;
									val = c;
								}
							}
						}
						newseq.getSequence().append( val );
						
						/*	newseq.sb.append( charset.iterator().next() );
						} else if( charset.size() == 3 ) {
							newseq.sb.append( charset.iterator().next() );
						} else newseq.sb.append( 'N' );*/
					}
					
					charset.clear();
				}
				
				serifier.lseq.removeAll( remseq );
				serifier.lseq.add( newseq );
				
				table.tableChanged( new TableModelEvent(table.getModel()) );
				c.repaint();
				overview.reval();
				overview.repaint();
			}
		});
		edit.add( new AbstractAction("Concat/Join") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				String gname = null;
				Set<Sequence>	remseq = new LinkedHashSet<Sequence>();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					if( gname == null ) gname = seq.getName();
					if( seq.getGroup() != null ) gname = seq.getGroup();
					remseq.add( seq );
				}
				
				Sequence newseq = new Sequence(gname, serifier.mseq);
				for( Sequence s : remseq ) {
					/*if( s.getAnnotations() != null ) for( Annotation a : s.getAnnotations() ) {
						a.start += newseq.length();
						a.stop += newseq.length();
						newseq.addAnnotation(a);
					}*/
					newseq.append( s );
				}
				newseq.setStart( 0 );
				
				//serifier.lseq.removeAll( remseq );
				serifier.lseq.add( newseq );
				
				updateView();
				overview.reval();
				overview.repaint();
			}
		});
		edit.add( new AbstractAction("Split NNN's") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				Set<Sequence>	remseq = new HashSet<>();
				Set<Sequence>	addseq = new HashSet<>();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					StringBuilder sb = seq.getSequence();
					String[] split = sb.toString().split("NNN");
					if (split.length>1) {
						remseq.add(seq);
						for (int k = 0; k < split.length; k++) {
							Sequence newseq = new Sequence(seq.getName()+"_"+k, serifier.mseq);
							newseq.setSequenceString(split[k]);
							addseq.add(newseq);
						}
					}
				}

				serifier.lseq.removeAll( remseq );
				serifier.lseq.addAll( addseq );

				table.tableChanged( new TableModelEvent(table.getModel()) );
				c.repaint();
				overview.reval();
				overview.repaint();
			}
		});
		edit.addSeparator();
		edit.add( new AbstractAction("Collapse alignment to left") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle r = getSelectedRect();
				for( Sequence seq : serifier.lseq ) {
					int i = r.x;
					int k = i;
					while( i >= 0 ) {
						char c = seq.getCharAt(i);
						if( c != '-' && c != ' ' ) {
							seq.setCharAt(k, c);
							k--;
						}
						i--;
					}
					while( k >= 0 ) {
						seq.setCharAt(k, '-');
						k--;
					}
				}
			}
		});
		edit.add( new AbstractAction("Collapse alignment to right") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle r = getSelectedRect();
				for( Sequence seq : serifier.lseq ) {
					int i = r.x;
					int k = i;
					while( i < seq.length() ) {
						char c = seq.getCharAt(i);
						if( c != '-' && c != ' ' ) {
							seq.setCharAt(k, c);
							k++;
						}
						i++;
					}
					while( k < seq.length() ) {
						seq.setCharAt(k, '-');
						k++;
					}
				}
			}
		});
		edit.add( new AbstractAction("Fill append") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int max = 0;
				for( Sequence seq : serifier.lseq ) {
					if( seq.length() > max ) max = seq.length();
				}
				
				for( Sequence seq : serifier.lseq ) {
					for( int i = seq.length(); i < max; i++ ) {
						seq.getSequence().append('-');
					}
				}
			}
		});
		edit.add( new AbstractAction("Append selected") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int max = 0;
				Sequence total = new Sequence("total", serifier.mseq);
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					if( r == rr[0] ) total.setName(seq.getName());
					total.append( seq );
				}
				updateView();
				serifier.addSequence( total );
			}
		});
		edit.add( new AbstractAction("Reverse complement group") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					if( serifier.gseq.containsKey(seq.getName()) ) {
						List<Sequence> lseq = serifier.gseq.get(seq.getName());
						for( Sequence subseq : lseq ) {
							subseq.reverseComplement();
							if( subseq != seq ) {
								subseq.setStart( seq.getStart() + (seq.getEnd()-subseq.getEnd()) );
							}
						}
						seq.reverseComplement();
						
						c.repaint();
					}
				}
			}
		});
		edit.add( new AbstractAction("Reverse complement") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					System.err.println( seq.getName() );
					/*StringBuilder	sb = seq.sb;
					for( int i = 0; i < seq.length()/2; i++ ) {
						char c = sb.getCharAt(i);
						sb.setgetCharAt( i, sb.getCharAt(seq.length()-1-i) );
						sb.setgetCharAt( seq.length()-1-i, c );
					}*/
					
					Rectangle rc = getSelectedRect();
					if( rc == null || rc.width == 0 ) {
						seq.reverseComplement();
						/*if( seq.getRevComp() == 1 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-8) );
							seq.setRevComp( 0 );
						} else if( seq.getRevComp() == 3 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-18)+"_compliment" );
							seq.setRevComp( 2 );
						} else if( seq.getRevComp() == 2 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-11)+"_reversecompliment" );
							seq.setRevComp( 3 );
						} else {
							seq.setName( seq.getName()+"_reverse" );
							seq.setRevComp( 1 );
						}*/
					} else {
						seq.reverseComplement( rc.x, rc.x+rc.width );
					}
				}
				c.repaint();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});	
		edit.add( new AbstractAction("Reverse") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					System.err.println( seq.getName() );
					/*StringBuilder	sb = seq.sb;
					for( int i = 0; i < seq.length()/2; i++ ) {
						char c = sb.getCharAt(i);
						sb.setgetCharAt( i, sb.getCharAt(seq.length()-1-i) );
						sb.setgetCharAt( seq.length()-1-i, c );
					}*/
					
					Rectangle rc = getSelectedRect();
					if( rc == null || rc.width == 0 ) {
						seq.reverse();
						if( seq.getRevComp() == 1 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-8) );
							seq.setRevComp( 0 );
						} else if( seq.getRevComp() == 3 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-18)+"_compliment" );
							seq.setRevComp( 2 );
						} else if( seq.getRevComp() == 2 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-11)+"_reversecompliment" );
							seq.setRevComp( 3 );
						} else {
							seq.setName( seq.getName()+"_reverse" );
							seq.setRevComp( 1 );
						}
					} else {
						seq.reverse( rc.x, rc.x+rc.width );
					}
				}
				c.repaint();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});
		edit.add( new AbstractAction("Complement") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					/*StringBuilder	sb = seq.sb;
					for( int i = 0; i < seq.length(); i++ ) {
						char c = sb.getCharAt(i);
						sb.setgetCharAt( i, complimentMap.get(c) );
					}*/
					
					Rectangle rc = getSelectedRect();
					if( rc == null || rc.width == 0 ) {
						seq.complement();
						if( seq.getRevComp() == 1 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-8)+"_reversecompliment" );
							seq.setRevComp( 3 );
						} else if( seq.getRevComp() == 3 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-18)+"_reverse" );
							seq.setRevComp( 1 );
						} else if( seq.getRevComp() == 2 ) {
							seq.setName( seq.getName().substring(0, seq.getName().length()-11) );
							seq.setRevComp( 0 );
						} else {
							seq.setName( seq.getName()+"_compliment" );
							seq.setRevComp( 2 );
						}
					} else {
						seq.complement(rc.x, rc.x+rc.width);
					}
				}
				c.repaint();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});
		edit.add( new AbstractAction("Uppercase") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seq.upperCase();
				}
				c.repaint();
			}
		});

		edit.add( new AbstractAction("UT Replacement") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					
					/*StringBuilder	sb = seq.sb;
					
					int i1 = sb.indexOf("T");
					int i2 = sb.indexOf("U");
					
					if( i1 == -1 ) i1 = sb.length();
					if( i2 == -1 ) i2 = sb.length();
					
					while( i1 < sb.length() || i2 < sb.length() ) {
						while( i1 < i2 ) {
							sb.setgetCharAt(i1, 'U');
							i1 = sb.indexOf("T", i1+1);
							if( i1 == -1 ) i1 = sb.length();
						}
						
						while( i2 < i1 ) {
							sb.setgetCharAt(i2, 'T');
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
							sb.setgetCharAt(i1, 'u');
							i1 = sb.indexOf("t", i1+1);
							if( i1 == -1 ) i1 = sb.length();
						}
						
						while( i2 < i1 ) {
							sb.setgetCharAt(i2, 't');
							i2 = sb.indexOf("u", i2+1);
							if( i2 == -1 ) i2 = sb.length();
						}
					}*/
					seq.utReplace();
				}
				c.repaint();
			}
		});
		edit.add( new AbstractAction(".* - Replacement") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					StringBuilder	sb = seq.getSequence();
					for( int i = 0; i < sb.length(); i++ ) {
						if( sb.charAt(i) == '.' || sb.charAt(i) == '*' ) sb.setCharAt(i, '-');
					}
					
					/*int i1 = sb.indexOf("T");
					int i2 = sb.indexOf("U");
					
					if( i1 == -1 ) i1 = sb.length();
					if( i2 == -1 ) i2 = sb.length();
					
					while( i1 < sb.length() || i2 < sb.length() ) {
						while( i1 < i2 ) {
							sb.setgetCharAt(i1, 'U');
							i1 = sb.indexOf("T", i1+1);
							if( i1 == -1 ) i1 = sb.length();
						}
						
						while( i2 < i1 ) {
							sb.setgetCharAt(i2, 'T');
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
							sb.setgetCharAt(i1, 'u');
							i1 = sb.indexOf("t", i1+1);
							if( i1 == -1 ) i1 = sb.length();
						}
						
						while( i2 < i1 ) {
							sb.setgetCharAt(i2, 't');
							i2 = sb.indexOf("u", i2+1);
							if( i2 == -1 ) i2 = sb.length();
						}
					}*/
				}
				c.repaint();
			}
		});
		edit.add( new AbstractAction("Remove all N's") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Sequence>	seqlist = new ArrayList<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqlist.add( seq );
				}
				serifier.removeAllNs( seqlist );
				updateView();
				
				c.repaint();
			}
		});
		edit.add( new AbstractAction("Remove gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Sequence>	seqlist = new ArrayList<>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqlist.add( seq );
				}
				serifier.removeGaps( seqlist );
				updateView();
				
				c.repaint();
			}
		});
		edit.add( new AbstractAction("Remove all gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Sequence>	seqlist = new ArrayList<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqlist.add( seq );
				}
				serifier.removeAllGaps( seqlist );
				updateView();
				
				c.repaint();
			}
		});
		edit.add( new AbstractAction("Discard high evo-rate sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Sequence>	seqlist = new ArrayList<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqlist.add( seq );
				}
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( JavaFasta.this ) == JFileChooser.APPROVE_OPTION ) {
					try {
						FileReader fr = new FileReader( fc.getSelectedFile() );
						BufferedReader br = new BufferedReader( fr );
						String line = br.readLine();
						line = br.readLine();
						int i = 0;
						while( line != null ) {
							String[] split = line.split("\t");
							int ix = Integer.parseInt( split[0].substring(1,split[0].length()-1) );
							int is = Integer.parseInt( split[2] );
							double dis = Double.parseDouble( split[3] );
							
							if( dis >= -20.0 ) {
								for( Sequence seq : seqlist ) {
									seq.getSequence().setCharAt( i, seq.getSequence().charAt(ix-1) );
								}
								i++;
							}
							
							line = br.readLine();
						}
						for( Sequence seq : seqlist ) {
							seq.getSequence().delete( i, seq.getSequence().length() );
							seq.checkLengths();
						}
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				//discardEvo( seqlist );
				
				c.repaint();
			}
		});
		edit.add( new AbstractAction("Clear sites with gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Sequence> seqset = new HashSet<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqset.add( seq );
				}
				clearSitesWithGaps( seqset );
				
			}
		});
		edit.add( new AbstractAction("Clear gaps from selected") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Sequence> seqset = new HashSet<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqset.add( seq );
				}
				Collection<Sequence> nonselected = new ArrayList<Sequence>( serifier.lseq );
				nonselected.removeAll( seqset );
				clearSitesWithGapsNonSelected( seqset, nonselected );
				
			}
		});
		edit.add( new AbstractAction("Clear conserved sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Sequence> seqset = new HashSet<Sequence>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqset.add( seq );
				}
				clearSites( seqset, false );
			}
		});
		edit.add( new AbstractAction("Clear variant sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Sequence> seqset = new HashSet<>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seqset.add( seq );
				}
				clearSites( seqset, true );
			}
		});
		edit.add( new AbstractAction("Retain variant sites allow error") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int min = Integer.MAX_VALUE;
				int max = Integer.MIN_VALUE;
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					//StringBuilder	sb = seq.sb;
					//sb.
					min = Math.min( min, seq.getStart() );
					max = Math.max( max, seq.getEnd() );
				}

				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seq.setStart( seq.getStart()-min );
				}

				int i = 0;
				while( i < max-min ) {
					boolean rem = true;
					char c = 0;
					int count = 0;
					for( int r : rr ) {
						char c2 = getCharAt(i, r);
						/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
							rem = false;
							break;
						}*/
						if( c2 != '.' && c2 != '-' && c2 != 'N' && c2 != 'n' ) {
							if( c != 0 && c2 != c ) {
								if(count++==2) {
									rem = false;
									break;
								}
							}

							c = c2;
						}
					}
					if( rem ) {
						for( int r : rr ) {
							deletegetCharAt(i, r);
						}
						max--;
					} else i++;
				}

				c.repaint();
			}
		});
		edit.add( new AbstractAction("Retain variant sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int min = Integer.MAX_VALUE;
				int max = Integer.MIN_VALUE;
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					//StringBuilder	sb = seq.sb;
					//sb.
					min = Math.min( min, seq.getStart() );
					max = Math.max( max, seq.getEnd() );
				}
				
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seq.setStart( seq.getStart()-min );
				}
				
				int i = 0;
				while( i < max-min ) {
					boolean rem = true;
					char c = 0;
					for( int r : rr ) {
						char c2 = getCharAt(i, r);
						/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
							rem = false;
							break;
						}*/
						if( c2 != '.' && c2 != '-' && c2 != 'N' && c2 != 'n' ) {
							if( c != 0 && c2 != c ) {
								rem = false;
								break;
							}
						
							c = c2;
						}
					}
					if( rem ) {
						for( int r : rr ) {
							deletegetCharAt(i, r);
						}
						max--;
					} else i++;
				}
				
				c.repaint();
			}
		});
		
		view.add( new AbstractAction("Physical mapping") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 400);
				
				int[] rr = table.getSelectedRows();
				final List<Sequence> seqs;
				if( rr == null || rr.length == 0 ) {
					seqs = serifier.lseq;
				} else {
					seqs = new ArrayList<Sequence>();
					for( int r : rr ) {
						int i = table.convertRowIndexToModel(r);
						seqs.add( serifier.lseq.get(i) );
					}
				}
				
				int maxseql = 0;
				for( Sequence seq : seqs ) {
					maxseql = Math.max( seq.length(), maxseql );
				}
				final int maxseqlen = Math.max(1, maxseql);
				final int fasti = 50;
				final int offset = 360;
				
				int w = 3200; //serifier.lseq.get(0).length();
				int h = fasti*seqs.size()+10;
				
				BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = bimg.createGraphics();
				drawPhys( g2, seqs, fasti, maxseqlen, offset, w, h );
				
				JComponent c = new JComponent() {
					public void paintComponent( Graphics g ) {
						Graphics2D g2 = (Graphics2D)g;
						g2.drawImage(bimg, 0, 0, w/2, h/2, this);
					}
				};
				Dimension dim = new Dimension(w/2, h/2);
				c.setPreferredSize(dim);
				c.setSize( dim );
				JScrollPane pane = new JScrollPane( c );
				frame.add( pane );
				
				JPopupMenu popup = new JPopupMenu();
				popup.add( new AbstractAction("Save img") {

					@Override
					public void actionPerformed(ActionEvent e) {
						int w = 3000;
						int h = (fasti*seqs.size()+10);
						BufferedImage	bimg = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
						Graphics2D g2 = bimg.createGraphics();
						drawPhys( g2, seqs, fasti, maxseqlen, offset, w, h );
						g2.dispose();
						try {
							ImageIO.write(bimg, "png", new File("/Users/sigmar/mynd.png"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				c.setComponentPopupMenu( popup );
				
				frame.setVisible(true);
			}
		});
		view.add( new AbstractAction("Mummer") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField host = new JTextField("localhost");
				JOptionPane.showMessageDialog(null, host);
				String userhome = System.getProperty("user.home");
				String username = System.getProperty("user.name");
				String hostname = host.getText();
				
				Map<String,List<Sequence>> mseq = new HashMap<>();
				
				for( Sequence seq : serifier.lseq ) {
					String spec = seq.getSpec();
					List<Sequence> lseq;
					if( mseq.containsKey(spec) ) {
						lseq = mseq.get(spec);
					} else {
						lseq = new ArrayList<>();
						mseq.put(spec, lseq);
					}
					lseq.add( seq );
				}
				
				String home = System.getProperty("user.home");
				Path tmpdir = Paths.get(home);
				for( String spec : mseq.keySet() ) {
					List<Sequence> lseq = mseq.get(spec);
					
					String filename = "in."+spec+".fasta";
					String pname = home+"/"+filename;
					Path p = Paths.get(pname);
					
					try {
						BufferedWriter bw = Files.newBufferedWriter(p);
						serifier.writeFasta(lseq, bw, null);
						bw.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					/*try {
						Files.write( p, fasta.toString().getBytes() );
					} catch (IOException e2) {
						e2.printStackTrace();
					}*/
					
					List<String> commandsList;
					if( hostname.equals("localhost") ) commandsList = new ArrayList<>(Arrays.asList("/Users/sigmarkarl/mummer-4.0.0rc1/mummer", "-maxmatch", "-n", "-l", "15", pname, pname));
					else {
			    		ProcessBuilder pbt = new ProcessBuilder("scp", "-q", filename, username+"@"+hostname+":~/"+filename);
			    		pbt.directory( tmpdir.toFile() );
						try {
							Process pc = pbt.start();
							pc.waitFor();
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
						}
						
						commandsList = new ArrayList<>(Arrays.asList("ssh", username + "@" + hostname, "mummer", "-maxmatch", "-n", "-l", "25", filename, filename));
			    	}
					
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					//Path pout = Paths.get("/Users/sigmar/pout.mummer");
					List cmds = new ArrayList<>();
					cmds.add( new Object[] {null, baos, null} );
					cmds.add( commandsList );
					
					Runnable run = () -> {
						String res = baos.toString();

						String name1 = null;
						boolean rev = false;
						String[] lines = res.split("\n");
						for( String line : lines ) {
							if( line.startsWith(">") ) {
								name1 = line.substring(1).trim();
								rev = line.contains("Reverse");
							} else {
								String trim = line.trim();
								String[] split = trim.split("[\t ]+");

								System.err.println( "serifier min "+serifier.min );

								if( split.length == 4 ) {
									if( !split[1].equals(split[2]) ) {
										String seqname = split[0];
										int start = Integer.parseInt(split[1])+serifier.min;
										int len = Integer.parseInt(split[3]);

										Sequence seq = serifier.mseq.get(seqname);
										Annotation ann = new Annotation( seq, start, start+len, rev ? -1 : 1, seq == null ? seqname : seq.getSubstring(start, start+len, 1) );
										ann.type = "mummer";
										ann.color = rev ? Color.pink : Color.cyan;
										serifier.lann.add( ann );
									}
								} else if( split.length == 3 ) {
									if( !split[0].equals(split[1]) ) {
										String seqname = name1;
										int start = Integer.parseInt(split[0])+serifier.min;
										int len = Integer.parseInt(split[2]);

										Sequence seq = serifier.mseq.get(seqname);
										if( seq == null ) {
											System.err.println();
										}
										Annotation ann = new Annotation( seq, start, start+len, rev ? -1 : 1, seq == null ? seqname : seq.getSubstring(start, start+len, 1) );
										ann.type = "mummer";
										ann.color = rev ? Color.pink : Color.cyan;
										serifier.lann.add( ann );
									}
								}
							}
						}

						atable.tableChanged( new TableModelEvent( atable.getModel() ) );
					};
					NativeRun nrun = new NativeRun( run );
					Object[] cont = new Object[] {null, null, null};
					try {
						nrun.runProcessBuilder("Mummer", cmds, cont, false, run, false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		view.add( new AbstractAction("Blast") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField host = new JTextField("localhost");
				JOptionPane.showMessageDialog(null, host);
				String userhome = System.getProperty("user.home");
				String username = System.getProperty("user.name");
				String hostname = host.getText();
				
				/*Map<String,List<Sequence>> mseq = new HashMap<String,List<Sequence>>();
				
				for( Sequence seq : serifier.lseq ) {
					String spec = seq.getSpec();
					List<Sequence> lseq;
					if( mseq.containsKey(spec) ) {
						lseq = mseq.get(spec);
					} else {
						lseq = new ArrayList<Sequence>();
						mseq.put(spec, lseq);
					}
					lseq.add( seq );
				}*/
				
				//Path tmpdir = Paths.get(userhome);
				//for( String spec : mseq.keySet() ) {
					//List<Sequence> lseq = mseq.get(spec);
					
					/*String filename = "in."+spec+".fasta";
					String pname = userhome+"/"+filename;
					Path p = Paths.get(pname);
					
					try {
						BufferedWriter bw = Files.newBufferedWriter(p);
						serifier.writeFasta(lseq, bw, null);
						bw.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}*/
					
				List<String> commandsList;
				if( hostname.equals("localhost") ) commandsList = new ArrayList<>(Arrays.asList(new String[]{"blastp", "-num_threads", "4"}));
				else {
					commandsList = new ArrayList<>(Arrays.asList(new String[]{"ssh", username + "@" + hostname, "blastp", "-db", "/data/nr", "-num_threads", "32"}));
		    	}
				
				StringWriter sw = new StringWriter();
				try {
					serifier.writeFasta(serifier.lseq, sw, getSelectedRect());
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//Path pout = Paths.get("/Users/sigmar/pout.mummer");
				List<Object> cmds = new ArrayList<Object>();
				cmds.add( new Object[] {sw.toString().getBytes(), baos, null} );
				cmds.add( commandsList );
				
				Runnable run = () -> {
                    String res = baos.toString();

                    JTextArea ta = new JTextArea( res );
                    JScrollPane	sp = new JScrollPane( ta );
                    ta.setFont( new Font("monospaced", Font.PLAIN, 12) );
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
                    frame.setSize(800, 600);
                    frame.add( sp );
                    frame.setVisible(true);
                };
				NativeRun nrun = new NativeRun( run );
				Object[] cont = new Object[] {null, null, null};
				try {
					nrun.runProcessBuilder("Blast", cmds, cont, false, run, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		view.add( new AbstractAction("GC count") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel(r);
				Sequence s = serifier.lseq.get(i);
				int c = s.getGCCount();
				JOptionPane.showMessageDialog(null, ""+c);
			}
		});
		view.addSeparator();
		view.add( new AbstractAction("Reverse complement") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel( r );
					Sequence seq = serifier.lseq.get( k );
					seq.setRevComp( seq.getRevComp() == -1 ? 1 : -1 );
				}
				c.repaint();
			}
		});
		view.add( new AbstractAction("Clear annotation within genes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int row : rr ) {
					int m = table.convertRowIndexToModel( row );
					Sequence sequence = serifier.lseq.get( m );
					
					Set<Annotation> clearann = new HashSet<Annotation>();
					for( Annotation ann : sequence.getAnnotations() ) {
						if( ann.type == null || !ann.type.contains("repeat") ) {
							for( Annotation subann : sequence.getAnnotations() ) {
								if( subann.type != null && subann.type.contains("repeat") && (subann.stop > ann.start && subann.start < ann.stop) ) clearann.add( subann );
							}
						}
					}
					sequence.getAnnotations().removeAll( clearann );
					serifier.lann.removeAll( clearann );
				}
				atable.tableChanged( new TableModelEvent(atable.getModel()) );
			}
		});
		view.addSeparator();
		view.add( new AbstractAction("Remove start positions") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int len = 0;
				for( Sequence seq : serifier.lseq ) {
					seq.setStart(0);
					len = Math.max(len, seq.length());
				}
				serifier.setMin(0);
				serifier.setMax( len );
				c.repaint();
			}
		});
		view.addSeparator();
		view.add( new AbstractAction("Find repeats") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int LEN = 2;
				int repNum = 2;
				
				List<Repeat> _repeats = new ArrayList<Repeat>();
				//for( Sequence sequence : serifier.lseq ) {
				int[] rr = table.getSelectedRows();
				for( int row : rr ) {
					int m = table.convertRowIndexToModel( row );
					Sequence sequence = serifier.lseq.get( m );
					for( int k = 0; k < sequence.length()-LEN*4; k++ ) {
						for( int i = 2; i < 10; i++ ) {
							boolean yes = true;
							/*for( Repeat r : _repeats ) {
								if( r.length != i && k >= r.start && k <= r.stop ) {
									yes = false;
									break;
								}
							}*/
							
							//if( yes ) {
								for( int l = k; l < k+i; l++ ) {
									for( int r = 1; r < repNum; r++ ) {
										if( sequence.getCharAt(l) != sequence.getCharAt(l+i*r) ) {
											yes = false;
											break;
										}
									}
									if( !yes ) break;
									/*if( sequence.getCharAt(l) != sequence.getCharAt(l+i) || sequence.getCharAt(l) != sequence.getCharAt(l+i*2) || sequence.getCharAt(l) != sequence.getCharAt(l+i*3) ) {
										yes = false;
										break;
									}*/
								}
								
								if( yes ) {
									int u = LEN;
									while( yes ) {
										for( int l = k; l < k+i; l++ ) {
											int val = l+u*i;
											if( val >= sequence.length() || sequence.getCharAt(l) != sequence.getCharAt(val) ) {
												yes = false;
												break;
											}
										}
										if( yes ) u++;
									}
									
									int start = k;
									int stop = k+i*u;
									
									if( stop - start > 8 ) {
										Repeat r = new Repeat();
										r.setName( sequence.getName()+"_repeat_"+i );
										
										r.setSeq(sequence);
										r.start = start;
										
										r.length = i;
										r.color = Color.blue;
										
										r.type = "repeat_"+i+"_"+u;
										r.stop = stop;
											
										sequence.addAnnotation( r );
										_repeats.add( r );
									}
									
									k = stop;
								}
							//}
						}
					}
				}
				
				serifier.lann.addAll( _repeats );
				atable.tableChanged( new TableModelEvent( atable.getModel()) );
			}
		});
		view.add( new AbstractAction("Find in consensus") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				String leit = getSelectedSequence();
				StringBuilder rc = new StringBuilder();
				for( int i = 0; i < leit.length(); i++ ) {
					rc.append( Sequence.rc.get(leit.charAt(leit.length()-i-1)) );
				}
				String rcleit = rc.toString();
				if( leit != null ) {
					int i = 0;
					for( Sequence s : serifier.lseq ) {
						if( s.getSequence().indexOf(leit) != -1 || s.getSequence().indexOf(rcleit) != -1 ) {
							//int i = serifier.lseq.indexOf(s);
							filterset.add( i );
						}
						i++;
					}
					//c.setSize(width, height);
					//c.prevx = 0;
					DefaultRowSorter<TableModel, Integer> rs = (DefaultRowSorter<TableModel,Integer>)table.getRowSorter();
					//rs.setRowFilter( null );
					//table.tableChanged( new TableModelEvent(table.getModel()) );
					rs.setRowFilter(null);
					rs.setRowFilter( rowfilter );
					c.updateCoords();
					
					
					//table.tableChanged( new TableModelEvent(table.getModel()) );
				}
			}
		});
		view.add( new AbstractAction("Align to selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				Rectangle rect = getSelectedRect();
				String leit = getSelectedSequence();
				if( leit != null && leit.length() > 3 ) {
					StringBuilder rc = new StringBuilder();
					for( int i = 0; i < leit.length(); i++ ) {
						rc.append( Sequence.rc.get(leit.charAt(leit.length()-i-1)) );
					}
					String rcleit = rc.toString();
					if( leit != null ) {
						//int[] rr = table.getSelectedRows();
						//for( int r : rr ) {
							//int i = table.convertRowIndexToModel(r);
						//	Sequence s = serifier.lseq.get(i);
						for( Sequence s : serifier.lgseq ) {
							boolean stopped = false;
							List<Sequence> lseq = serifier.gseq.get( s.getId() );
							for( Sequence seq : lseq ) {
								int ind = seq.getSequence().indexOf(leit);
								int rind = seq.getSequence().indexOf(rcleit);
								
								if( !stopped ) {
									if( ind != -1 ) {
										int val = rect.x+serifier.getMin()-(seq.getStart()+ind);
										
										for( Sequence sseq : lseq ) {
											if( sseq != s ) {
												sseq.setStart( sseq.getStart()+val );
											}
										}
										s.setStart( s.getStart()+val );
										
										stopped = true;
									} else if( rind != -1 ) {
										System.err.println( "reverse" );
										
										for( Sequence subseq : lseq ) {
											subseq.reverseComplement();
											if( subseq != s ) {
												subseq.setStart( s.getStart() + (s.getEnd()-subseq.getEnd()) );
											}
										}
										s.reverseComplement();
										
										ind = seq.getSequence().indexOf(leit);
										int val = rect.x+serifier.getMin()-(seq.getStart()+ind);
										
										for( Sequence sseq : lseq ) {
											if( sseq != s ) {
												sseq.setStart( sseq.getStart()+val );
											}
										}
										s.setStart( s.getStart()+val );
										
										stopped = true;
									}
								}
								
								if( ind != -1 || rind != -1 ) {
									int u = seq.getSequence().indexOf(leit);
									if( u != -1 ) filterset.add( seq.index );
								}
							}
						}
						//c.setSize(width, height);
						//c.prevx = 0;
						DefaultRowSorter<TableModel, Integer> rs = (DefaultRowSorter<TableModel,Integer>)table.getRowSorter();
						//rs.setRowFilter( null );
						//table.tableChanged( new TableModelEvent(table.getModel()) );
						rs.setRowFilter(null);
						rs.setRowFilter( rowfilter );
						c.updateCoords();
						
						//table.tableChanged( new TableModelEvent(table.getModel()) );
					}
				}
			}
		});
		view.addSeparator();
		JCheckBoxMenuItem cbmi2 = new JCheckBoxMenuItem( new AbstractAction("Collapse view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				collapseView = !collapseView;
				
				if( !collapseView ) {
					//filterset.clear();
					//c.updateCoords();
					//table.tableChanged( new TableModelEvent( table.getModel() ) );
				}
			}
		});
		view.add( cbmi2 );
		
		anno.add( new AbstractAction("Annotate selected") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle selr = getSelectedRect();
				for( int y = selr.y; y < selr.y+selr.height; y++ ) {
					int i = table.convertColumnIndexToModel(y);
					Sequence seq = serifier.lseq.get(i);
					
					Annotation a = new Annotation(seq,"unknown",Color.gray,selr.x,selr.x+selr.width,1,null);
					serifier.lann.add( a );
					Collections.sort( seq.getAnnotations() );
				}
				atable.tableChanged( new TableModelEvent( atable.getModel() ) );
			}
		});
		
		//popup.addSeparator();
		file.add( new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					openFiles();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		file.add( new AbstractAction("Open directory") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser	jfc = new JFileChooser();
				jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		    	if( jfc.showOpenDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
		    		File f = jfc.getSelectedFile();
		    		
		    		Path startingDir = Paths.get( f.toURI() );
		            String pattern = "*.ab1";

		            Finder finder = new Finder(pattern);
		            try {
						Files.walkFileTree(startingDir, finder);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		            finder.done();
		    	}
			}
		});
		file.add( new AbstractAction("AA version") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame	frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 600);
				JavaFasta	jf = new JavaFasta();
				jf.initGui( frame );
				
				List<Sequence> seqs = JavaFasta.this.getSequences();
				for( Sequence s : seqs ) {
					Sequence seq = new Sequence(s.getName(), jf.serifier.mseq);
					for( int i = 0; i < s.length(); i+=3 ) {
						String subs = s.getSubstring(i, i+3, 1);
						Character c = Sequence.amimap.get( subs );
						seq.append( c == null ? '-' : c );
					}
					jf.serifier.addSequence(seq);
				}
				jf.updateView();
				
				frame.setVisible( true );
			}
		});
		file.add( new AbstractAction("Flanking sequences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame	frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 600);
				JavaFasta	jf = new JavaFasta();
				jf.initGui( frame );
				
				Map<Sequence,List<Annotation>> mann = new HashMap<>();
				int[] rr = atable.getSelectedRows();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					Annotation ann = serifier.lann.get(i);
					if( ann.getSeq() == null ) {
						System.err.println();
					}
					
					List<Annotation> alist;
					if( mann.containsKey(ann.getSeq()) ) {
						alist = mann.get( ann.getSeq() );
					} else {
						alist = new ArrayList<>();
						mann.put(ann.getSeq(), alist);
					}
					alist.add( ann );
				}
				
				for( Sequence seq : mann.keySet() ) {
					List<Annotation> alist = mann.get(seq);
					Collections.sort( alist );
					List<Annotation> spacerlist = new ArrayList<>();
					for( int i = 0; i < alist.size()-1; i++ ) {
						Annotation a = alist.get(i);
						Annotation na = alist.get(i+1);
						
						String type = "";
						if( a.getName().contains("-") ) type = "-"+a.getName().substring(0,4);
						else if( na.getName().contains("-") ) type = "-"+na.getName().substring(0,4);
						
						String newname = a.getSeq().getName()+"-CRISPR-"+a.start+type;
						Sequence nseq = new Sequence( newname, serifier.mseq );
						
						if( a.stop+1 > na.start-1 ) {
							System.err.println();
						}
						
						String subs = a.getSeq().getSubstring(a.stop+1, na.start-1, 1);
						if( na.start - a.stop < 50 && na.start - a.stop > 19 ) {
							Annotation newa = new Annotation(seq,newname,null,a.stop+1,na.start-1,0,serifier.mann);
							newa.type = "spacer";
							if( type.length() > 0 ) newa.type += type;
							spacerlist.add( newa );
							serifier.lann.add( newa );
						}
						
						nseq.append( subs );
						jf.serifier.addSequence(nseq);
					}
					//alist.addAll( spacerlist );
					//Collections.sort( alist );
				}
				jf.updateView();
				
				frame.setVisible( true );
			}
		});
		file.add( new AbstractAction("Export") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exportFasta( table, serifier.lseq );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		file.add( new AbstractAction("Export flanking") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<Sequence> lseq = new ArrayList<Sequence>();
					for( String cons : serifier.gseq.keySet() ) {
						Sequence 		parseq = serifier.mseq.get(cons);
						List<Sequence> 	sseq = serifier.gseq.get(cons);
						
						for( Sequence s : sseq ) {
							int diff = parseq.getStart() - s.getStart();
							if( diff >= 50 ) {
								Sequence nseq = new Sequence( parseq.getName()+"_left", null );
								nseq.append( s.getSubstring(0, diff, 1) );
								lseq.add( nseq );
							}
							
							diff = s.getEnd() - parseq.getEnd();
							if( diff >= 50 ) {
								Sequence nseq = new Sequence( parseq.getName()+"_right", null );
								nseq.append( s.getSubstring(s.length()-diff-1, s.length(), 1) );
								lseq.add( nseq );
							}
						}
					}
					JFileChooser jfc = new JFileChooser();
					if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
						File f = jfc.getSelectedFile();
	        		 //FileOutputStream fos = new FileOutputStream( f );
	        		 //fos.write( baos.toByteArray() );
	        		 
	        		 	FileWriter fw = new FileWriter( f );
	        		 	serifier.writeFasta( lseq, fw, null );
	        		 	fw.close();
					}
				//exportFlanking( table, lseq );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		file.add( new AbstractAction("Export phylip") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exportPhylip();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		file.add( new AbstractAction("Export many") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exportManyFasta( table, serifier.lseq );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		file.add( new AbstractAction("Delete") {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		if( !(cnt instanceof JFrame ) ){
			popup.add( file );
			popup.add( edit );
			popup.addSeparator();
		}
		final JCheckBoxMenuItem		cbmi = new JCheckBoxMenuItem();
		cbmi.setAction( new AbstractAction("Base colors") {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.basecolors = cbmi.getState();
				c.repaint();
			}
		});
		popup.add( cbmi );
		final JCheckBoxMenuItem		cbmi3 = new JCheckBoxMenuItem();
		cbmi3.setAction( new AbstractAction("AA colors") {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.aacolors = cbmi3.getState();
				c.repaint();
			}
		});
		popup.add( cbmi3 );
		phylogeny.add( new AbstractAction("Sequence identity matrix") {
						   @Override
						   public void actionPerformed(ActionEvent e) {
							   var diff = false;
							   var speclist = serifier.lseq.stream().map(FastaSequence::getName).toList();
							   var aniResult = new ANIResult(speclist.size());
							   Map<String, Integer> blosumap = getBlosumMap();
							   int where = 0;
							   for(Sequence seq1 : serifier.lseq) {
								   int wherex = 0;
								   for(Sequence seq2 : serifier.lseq) {
									   var aniScore = new ANIScore();
									   seqIdent(seq1, seq2, true, blosumap, aniScore);

									   double ani = (diff ? (double) (aniScore.tscore - aniScore.score) : aniScore.score) / (double) aniScore.tscore;
									   aniResult.corrarr[where * speclist.size() + wherex] = ani;
									   aniResult.countarr[where * speclist.size() + wherex] = 1;

									   wherex++;
								   }
								   where++;
							   }
							   showAniMatrix(speclist, aniResult);
						   }
					   });
		phylogeny.add( new AbstractAction("Dis-mat exclude gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = distanceMatrix( true );
				
				File save = null;
				try {
					JFileChooser	fc = new JFileChooser();
					if( fc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
						save = fc.getSelectedFile();
					}
				} catch( Exception e1 ) {
					
				}
				
				if( save != null ) {
					try {
						FileWriter fw = new FileWriter( save );
						fw.write( sb.toString() );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JTextArea		text = new JTextArea( sb.toString() );
					JScrollPane	sp = new JScrollPane( text );
					JFrame	fr = new JFrame("Distance matrix");
					fr.add( sp );
					fr.setSize(800, 600);
					fr.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					fr.setVisible( true );
				}
			}
		});
		phylogeny.add( new AbstractAction("Distance matrix") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = distanceMatrix( false );
				
				File save = null;
				try {
					JFileChooser	fc = new JFileChooser();
					if( fc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
						save = fc.getSelectedFile();
					}
				} catch( Exception e1 ) {
					
				}
				
				if( save != null ) {
					try(FileWriter fw = new FileWriter( save )) {
						fw.write( sb.toString() );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JTextArea		text = new JTextArea( sb.toString() );
					JScrollPane	sp = new JScrollPane( text );
					JFrame	fr = new JFrame("Distance matrix");
					fr.add( sp );
					fr.setSize(800, 600);
					fr.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					fr.setVisible( true );
				}
			}
		});
		phylogeny.add( new AbstractAction("Distance matrix blosum") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Integer> blosumap = JavaFasta.getBlosumMap();
				
				List<String> names = new ArrayList<>();
				for( Sequence seq : serifier.lseq ) {
					names.add( seq.getName() );
				}
				
				StringBuilder sb = distanceMatrix( false, blosumap, names );
				
				System.err.println( sb.toString().split("\n").length );
				
				File save = null;
				try {
					JFileChooser	fc = new JFileChooser();
					if( fc.showOpenDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
						save = fc.getSelectedFile();
					}
				} catch( Exception e1 ) {
					
				}
				
				if( save != null ) {
					try(FileWriter fw = new FileWriter( save )) {
						fw.write( sb.toString() );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JTextArea		text = new JTextArea( sb.toString() );
					JScrollPane	sp = new JScrollPane( text );
					JFrame	fr = new JFrame("Distance matrix");
					fr.add( sp );
					fr.setSize(800, 600);
					fr.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					fr.setVisible( true );
				}
			}
		});
		phylogeny.add( new AbstractAction("Draw tree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, Integer> blosumap = getBlosumMap();
				double[] dd = distanceMatrixNumeric( false, null, blosumap );
				
				System.err.println("about to call showTree");
				List<String> corrInd = new ArrayList<String>();
				for( Sequence seq : serifier.lseq ) {
					corrInd.add( seq.getName() );
				}
				
				StringBuilder sb = printDistanceMatrix( dd, corrInd );
				Path p = Paths.get( "/Users/sigmar/dist.txt" );
				try {
					Files.write( p, sb.toString().getBytes() );
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				TreeUtil tu = new TreeUtil();
				TreeUtil.Node n = tu.neighborJoin(dd, corrInd, null, false, false);
				String tree = n.toString();
				
				/*boolean failed = false;
				try {
					JSObject jso = JSObject.getWindow( parentApplet );
					jso.call("showTree", new Object[] {sb.toString()} );
				} catch( NoSuchMethodError | Exception e1 ) {
					failed = true;
				}*/
				
				//if( failed ) {
				//String 				tree = serifier.getFastTree();
				if( cs.getConnections().size() > 0 ) {
		    		cs.sendToAll( tree );
		    	} else if( Desktop.isDesktopSupported() ) {
		    		cs.message = tree;
		    		//String uristr = "http://webconnectron.appspot.com/Treedraw.html?tree="+URLEncoder.encode( tree, "UTF-8" );
		    		String uristr = "http://webconnectron.appspot.com/Treedraw.html?ws=127.0.0.1:8887";
					try {
						Desktop.getDesktop().browse( new URI(uristr) );
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
		    	}
				//}
				
				/*String urlstr = Base64.encodeBase64URLSafeString( sb.toString().getBytes() );
				try {
					URI treeuri = new URI( "http://webconnectron.appspot.com/Treedraw.html?dist="+urlstr );
					JavaFasta.this.getAppletContext().showDocument(treeuri.toURL(), "_blank");
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				try {
					URI treeuri = new URI( "http://webconnectron.appspot.com/Treedraw.html" );
					JavaFasta.this.getAppletContext().showDocument(treeuri.toURL(), "_blank");
				} catch (URISyntaxException | MalformedURLException e1) {
					e1.printStackTrace();
				}*/
			}
		});
		phylogeny.add( new AbstractAction("Draw ML tree") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 List<Sequence> seqlist = new ArrayList<>();
		    	 int[] rr = table.getSelectedRows();
		    	 for( int r : rr ) {
		    		 int i = table.convertRowIndexToModel( r );
		    		 Sequence seq = serifier.lseq.get(i);
		    		 seqlist.add( seq );
		    	 }
		    	 
		    	 /*List<Sequence> oldseq = serifier.lseq;
		    	 serifier.lseq = seqlist;
		    	 String tree = serifier.getFastTree();
		    	 serifier.lseq = oldseq;*/
		    	 
		    	 //System.err.println( tree );
			     /*pb = new ProcessBuilder("google-chrome", "http://127.0.0.1:8888/Treedraw.html"); //"http://webconnectron.appspot.com/Treedraw.html");
		    	p = pb.start();
		    	OutputStream os = p.getOutputStream();
		    	os.write( tree.getBytes() );
		    	os.close();*/
		    	 

				String 				tree = serifier.getFastTree( seqlist, user, false );
				System.err.println( tree );
				if( cs != null && cs.getConnections() != null && cs.getConnections().size() > 0 ) {
					cs.sendToAll( tree );
				} else if( Desktop.isDesktopSupported() && cs != null ) {
					cs.message = tree;
					//String uristr = "http://webconnectron.appspot.com/Treedraw.html?tree="+URLEncoder.encode( tree, "UTF-8" );
					String uristr = "http://webconnectron.appspot.com/Treedraw.html?ws=127.0.0.1:"+cs.getPort();
					try {
						Desktop.getDesktop().browse( new URI(uristr) );
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				} else {
					Platform.runLater( new Runnable() {
						public void run() {
							WebView webview = new WebView();
							WebEngine webengine = webview.getEngine();
							webengine.setUserAgent("AppleWebKit/537.44");
							webengine.load("http://webconnectron.appspot.com/Treedraw.html");

							webengine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
								@Override
								public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
									webview.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
								}
							});

							Stage stage = new Stage();
							stage.setScene( new Scene(webview) );
							stage.show();
							//region.getStyleClass()
						}
					});
				}
			}
		});
		phylogeny.add( new AbstractAction("Draw tree excluding gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder	sb = distanceMatrix( true );
			}
		});
		phylogeny.add( new AbstractAction("Draw distance matrix") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = distanceMatrix( true );
			}
		});
		
		name.add( new AbstractAction("Retain") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				if( jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
					Path p = jfc.getSelectedFile().toPath();
					try {
						List<String> lines = Files.readAllLines(p);
						Set<String> lset = new HashSet<String>( lines );
						
						Set<Sequence> remset = new HashSet<Sequence>();
						int[] rr = table.getSelectedRows();
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							Sequence seq = serifier.lseq.get(i);
							
							if( !lset.contains(seq.getName()) ) remset.add( seq );
						}
						serifier.lseq.removeAll( remset );
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					c.updateCoords();
					table.tableChanged( new TableModelEvent( table.getModel() ) );
				}
				
				/*JTextField tf = new JTextField();
				JOptionPane.showMessageDialog(cnt, tf);
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel( r );
				Sequence s = serifier.lseq.get( i );
				int k = s.sb.indexOf( tf.getText() );
				
				Rectangle rect = c.getVisibleRect();
				rect.x = k*10;
				c.scrollRectToVisible( rect );*/
			}
		});
		name.add( new AbstractAction("Goto") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextField tf = new JTextField();
				JOptionPane.showMessageDialog(cnt, tf);
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel( r );
				Sequence s = serifier.lseq.get( i );
				int k = s.getSequence().indexOf( tf.getText() );
				
				Rectangle rect = c.getVisibleRect();
				rect.x = (int)((k+s.offset)*c.cw);
				c.scrollRectToVisible( rect );
			}
		});
		name.add( new AbstractAction("Goto sequence") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextField tf = new JTextField();
				JOptionPane.showMessageDialog(cnt, tf);
				//int r = table.getSelectedRow();
				//int i = table.convertRowIndexToModel( r );
				int i = 0;
				for( Sequence s : serifier.lseq ) {
					if( s.getName().contains(tf.getText()) ) {
						int r = table.convertRowIndexToView(i);
						table.setRowSelectionInterval(r, r);
						
						Rectangle rect = c.getVisibleRect();
						rect.y = r*table.getRowHeight();
						rect.x = (-serifier.getMin()+s.getStart())*10;
						c.scrollRectToVisible( rect );
						break;
					}
					i++;
				}
			}
		});
		
		name.add( new AbstractAction("Find duplicates") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r = 0; r < rr.length; r++ ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					String seqstr = seq.getSequence().toString();
					
					for( int k = r+1; k < rr.length; k++ ) {
						i = table.convertRowIndexToModel(k);
						Sequence seq2 = serifier.lseq.get(i);
						
						if( seqstr.compareTo( seq2.getSequence().toString() ) == 0 ) {
							table.removeRowSelectionInterval(0, table.getRowCount()-1);
							table.setRowSelectionInterval(r, r);
							table.addRowSelectionInterval(k, k);
							break;
						}
					}
					
					if( table.getSelectedRowCount() == 2 ) break;
				}
			}
		});
		name.add( new AbstractAction("Rename duplicates") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int i = 0; i < rr.length; i++ ) {
					Sequence seq = serifier.lseq.get( rr[i] );
					String seqstr = seq.getName();
					
					int count = 0;
					for( int n = i+1; n < rr.length; n++ ) {
						Sequence seq2 = serifier.lseq.get(n);
						String seqstr2 = seq2.getName();
						
						if( seqstr.compareTo( seqstr2 ) == 0 ) {
							int curi = seqstr2.indexOf('[');
							if( curi == -1 ) {
								seqstr2 += "_"+(++count);
							} else {
								seqstr2 = seqstr2.substring(0,curi)+"_"+(++count)+seqstr2.substring(curi, seqstr2.length());
							}
							seq2.setName( seqstr2 );
							//removee.add( seq2 );
							//removei.add( n );
						}
					}
				}
					//sortseqs.removeAll( removei );
				//serifier.lseq.removeAll( removee );
				//updateIndexes();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
			}
		});
		name.add( new AbstractAction("Remove name duplicates") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Collection<Integer> sortseqs = new java.util.TreeSet<Integer>();
				int[] rr = table.getSelectedRows();
				for( int r = 0; r < rr.length; r++ ) {
					int i = table.convertRowIndexToModel(r);
					sortseqs.add( i );
				}
				
				Collection<Sequence> removee = new HashSet<Sequence>();
				while( sortseqs.size() > 0 ) {
					Collection<Integer> removei = new HashSet<Integer>();
					for( int i : sortseqs ) {
						Sequence seq = serifier.lseq.get(i);
						String seqstr = seq.getName();
						
						for( int n : sortseqs ) {
							if( n > i ) {
								Sequence seq2 = serifier.lseq.get(n);
								String seqstr2 = seq2.getName();
								
								if( seqstr.compareTo( seqstr2 ) == 0 ) {
									removee.add( seq2 );
									removei.add( n );
								}
							}
						}
						
						removei.add(i);
						break;
					}
					sortseqs.removeAll( removei );
				}
				serifier.lseq.removeAll( removee );
				updateIndexes();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				c.updateCoords();
			}
		});
		name.add( new AbstractAction("Remove sequence duplicates") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Collection<Integer> sortseqs = new java.util.TreeSet<Integer>();
				int[] rr = table.getSelectedRows();
				for( int r = 0; r < rr.length; r++ ) {
					int i = table.convertRowIndexToModel(r);
					sortseqs.add( i );
				}
				
				Collection<Sequence> removee = new HashSet<Sequence>();
				while( sortseqs.size() > 0 ) {
					Collection<Integer> removei = new HashSet<Integer>();
					for( int i : sortseqs ) {
						Sequence seq = serifier.lseq.get(i);
						String seqstr = seq.getSequence().toString();
						
						for( int n : sortseqs ) {
							if( n > i ) {
								Sequence seq2 = serifier.lseq.get(n);
								String seqstr2 = seq2.getSequence().toString();
								
								if( seqstr.compareTo( seqstr2 ) == 0 ) {
									removee.add( seq2 );
									removei.add( n );
								}
							}
						}
						
						removei.add(i);
						break;
					}
					sortseqs.removeAll( removei );
				}
				serifier.lseq.removeAll( removee );
				updateIndexes();
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				c.updateCoords();
			}
		});
		name.add( new AbstractAction("RenameSpec") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] colors = {"#ff0000", "#00ff00", "#0000ff", "#ffff00", "#ff00ff", "#00ffff", "#ff8800", "#ff0088", "#88ff00", "#00ff88", "#8800ff", "#0088ff"};
				Map<String,String> ss = new HashMap<String,String>();
				for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					
					int i = name.indexOf('_');
					if( i != -1 ) {
						//int k = name.indexOf('_', i+1);
						//if( k != -1 ) {
						
						int li = name.lastIndexOf(';');
						int k = name.indexOf("_");
						if( k == -1 ) k = li;
						String key = name.substring(0, k);
						String color;
						if( ss.containsKey( key ) ) {
							color = ss.get( key );
						} else {
							color = colors[ss.size()];
							ss.put( key, color );
						}
						
						if( li != -1 ) {
							String end = name.substring(li+1).replace(';', ' ').trim();
							seq.setName( name.substring(0,li)+"["+color+"]$;"+end );
							//String end = name.substring(ln+1, name.length()).replace(';', ' ').trim();
							//seq.setName( name.substring(0,u)+"["+color+"];"+end );
						} else {
							//String end = name.substring(li+1).replace(';', ' ').trim();
							//seq.setName( name.substring(0,li)+"["+color+"]{1.0 1.0 2.0};"+end );
						}
						
						/*int li = name.lastIndexOf(';');
						int ln = name.lastIndexOf(';', li-1);
						if( ln != -1 ) {
							String end = name.substring(ln+1, name.length()).replace(';', ' ').trim();
							seq.setName( name.substring(0,u)+"["+color+"];"+end );
						} else {
							String end = name.substring(u+1).replace(';', ' ').trim();
							seq.setName( name.substring(0,u)+"["+color+"];"+end );
						}*/
					}
				}
				updateView();
			}
		});
		name.add( new AbstractAction("RenameAppend") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Map<String,String> or = openRenameFile();
					for( String seqval : or.keySet() ) {
						String rename = or.get(seqval);
						
						for( Sequence seq : serifier.lseq ) {
							if( seq.getName().equals(seqval) ) {
								seq.setName( seq.getName() + "_" + rename );
							}
						}
					}
					updateView();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		name.add( new AbstractAction("RenameAppend from sequence") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Map<String,String> or = openRenameFile();
					for( String seqval : or.keySet() ) {
						String rename = or.get(seqval);
						
						for( Sequence seq : serifier.lseq ) {
							if( seq.getStringBuilder().indexOf(seqval) != -1 ) {
								seq.setName( seq.getName() + "_" + rename );
							}
						}
					}
					updateView();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		name.add( new AbstractAction("Non-unique names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,List<Sequence>> un = new HashMap<String,List<Sequence>>();
				for( Sequence seq : serifier.lseq ) {
					List<Sequence> ul;
					if( un.containsKey( seq.getName() ) ) {
						ul = un.get( seq.getName() );
					} else {
						ul = new ArrayList<Sequence>();
						un.put( seq.getName(), ul );
					}
					ul.add( seq );
				}
				
				for( String seqname : un.keySet() ) {
					List<Sequence> ul = un.get( seqname );
					for( int i = 1; i < ul.size(); i++ ) {
						Sequence seq = ul.get(i);
						seq.setName( seq.getName()+"_"+i );
					}
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Select cultured level") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					int end = seq.getName().length();
					int last = seq.getName().lastIndexOf(';');
					String prefix = seq.getName().substring(0,last);
					String current = seq.getName().substring(last+1,end);
					while( current.contains("ncultur") ) {
						end = prefix.length();
						last = prefix.lastIndexOf(';');
						current = prefix.substring(last+1,end);
						prefix = prefix.substring(0,last);
					}
					seq.setName( current );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Strip names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					seq.setName( seq.getName().split("[\t _]+")[0] );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Strip front level") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					seq.setName( seq.getName().substring(seq.getName().indexOf('_')+1) );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Strip level") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int i = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(i);
					
					String name = seq.getName();
					int d = name.lastIndexOf('-');
					if( d == -1 ) d = name.length();
					int u = name.lastIndexOf('_');
					if( u == -1 ) u = name.length();
					seq.setName( seq.getName().substring(0,Math.min( d,u )) );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Underscore spaces") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					//int i = name.indexOf(' ');
					//name = name.substring(0,i+1) + name.substring(i+1).replace(' ', '_') + ";";
					seq.setName( name.replace(' ', '_') );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Underscore paranthesis") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					//int i = name.indexOf(' ');
					//name = name.substring(0,i+1) + name.substring(i+1).replace(' ', '_') + ";";
					seq.setName( name.replace('(', '_').replace(')', '_') );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Underscore brackets") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					//int i = name.indexOf(' ');
					//name = name.substring(0,i+1) + name.substring(i+1).replace(' ', '_') + ";";
					seq.setName( name.replace('[', '_').replace(']', '_') );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Underscore comma") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					//int i = name.indexOf(' ');
					//name = name.substring(0,i+1) + name.substring(i+1).replace(' ', '_') + ";";
					seq.setName( name.replace(',', '_') );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Underscore :") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					//int i = name.indexOf(' ');
					//name = name.substring(0,i+1) + name.substring(i+1).replace(' ', '_') + ";";
					seq.setName( name.replace(':', '_') );
				}
				updateView();
			}
		});
		name.add( new AbstractAction("Reverse names") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int k = table.convertRowIndexToModel(r);
					Sequence seq = serifier.lseq.get(k);
				//for( Sequence seq : serifier.lseq ) {
					String name = seq.getName();
					String[] split = name.split("_");
					String newname = "";
					for( int i = split.length-1; i >= 1; i-- ) {
						newname += split[i] + "_";
					}
					newname += split[0];
					//int i = name.indexOf(' ');
					//name = name.substring(0,i+1) + name.substring(i+1).replace(' ', '_') + ";";
					seq.setName( newname );
				}
				updateView();
			}
		});
		
		popup.add( new AbstractAction("Shannon blocks filering") {
			public void actionPerformed( ActionEvent e ) {
				final JCheckBox cb = new JCheckBox("Only use selected for evaluation");
				cb.setSelected( true );
				final JLabel	ws = new JLabel("Winodw size:");
				final JSpinner 	sp = new JSpinner( new SpinnerNumberModel(10, 2, 100, 1) );
				final JLabel	lb = new JLabel("Percentage:");
				final JSpinner 	pc = new JSpinner( new SpinnerNumberModel(0.9, 0.0, 1.0, 0.01) );
				Object[] message = new Object[] { cb, ws, sp, lb, pc };
				JOptionPane.showMessageDialog(parentApplet, message, "Shannon blocks filter", JOptionPane.DEFAULT_OPTION );
				int winsize = (Integer)sp.getValue();
				double per = (Double)pc.getValue();
				
				int min;
				int max;
				int total = table.getRowCount();
				Set<Sequence> seqset = null;
				if( cb.isSelected() ){
					min = Integer.MAX_VALUE;
					max = Integer.MIN_VALUE;
					
					seqset = new HashSet<Sequence>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						int k = table.convertRowIndexToModel( r );
						Sequence seq = serifier.lseq.get( k );
						
						min = Math.min( min, seq.getStart() );
						max = Math.max( max, seq.getEnd() );
						
						seqset.add( seq );
					}
					total = seqset.size();
				} else {
					min = serifier.getMin();
					max = serifier.getMax();
				}
				
				double[] d = new double[max-min];
				Map<Character,Integer>	shanmap = new HashMap<Character,Integer>();
				for( int x = min; x < max; x++ ) {
					shanmap.clear();
					
					if( seqset != null ) {
						for( Sequence seq : seqset ) {
							char c = getCharAt( x, table.convertRowIndexToView( seq.index ) );
							int val = 0;
							if( shanmap.containsKey(c) ) val = shanmap.get(c);
							shanmap.put( c, val+1 );
						}
					} else {
						for( int y = 0; y < total; y++ ) {
							char c = getCharAt(x, y);
							int val = 0;
							if( shanmap.containsKey(c) ) val = shanmap.get(c);
							shanmap.put( c, val+1 );
						}
					}
					
					double res = 0.0;
					for( char c : shanmap.keySet() ) {
						int val = shanmap.get(c);
						double p = (double)val/(double)total;
						res -= p*Math.log(p)/Math.log(2.0);
					}
					d[x-min] = res;
				}
				
				double[] old = d;
				double sum = 0.0;
				for( int k = 0; k < Math.min( old.length, winsize ); k++ ) {
					sum += old[k];
				}
				d = new double[ old.length-winsize ];
				for( int i = 0; i < d.length; i++ ) {
					d[i] = sum/(double)winsize;
					sum += -d[i]+old[i+winsize];
				}
				
				int i = 0;
				while( i < max-min ) {
					boolean rem = true;
					for( Sequence seq : seqset ) {
						int r = table.convertRowIndexToView( seq.index );
						char c = getCharAt(i, r);
						/*if( c2 != '.' && c2 != '-' && c2 != ' ' ) {
							rem = false;
							break;
						}*/
						if( c == '.' || c == '-' ) {
							//if( c != 0 && c2 != c ) {
							rem = false;
							break;
							//}
						}
					}
					if( !rem ) {
						for( Sequence seq : seqset ) {
							int r = table.convertRowIndexToView( seq.index );
							cleargetCharAt(i, r);
						}
					}
					i++;
				}
				
				for( Sequence seq : seqset ) {
					seq.checkLengths();
				}
				
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Draw shannon") {
			public void actionPerformed( ActionEvent e ) {
				String command = "command";
				System.err.println("about to call showShannon");
				double[] d = new double[ serifier.getDiff() ];
				Map<Character,Integer>	shanmap = new HashMap<Character,Integer>(); 
				for( int x = serifier.getMin(); x < serifier.getMax(); x++ ) {
					shanmap.clear();
					int total = table.getRowCount();
					for( int y = 0; y < total; y++ ) {
						char c = getCharAt(x, y);
						int val = 0;
						if( shanmap.containsKey(c) ) val = shanmap.get(c);
						shanmap.put( c, val+1 );
					}
					double res = 0.0;
					for( char c : shanmap.keySet() ) {
						int val = shanmap.get(c);
						double p = (double)val/(double)total;
						res -= p*Math.log(p)/Math.log(2.0);
					}
					d[x-serifier.getMin()] = res;
				}
				
				final JCheckBox	cb = new JCheckBox("Filter blocks");
				final JSpinner 	sp = new JSpinner( new SpinnerNumberModel(10, 2, 100, 1) );
				sp.setEnabled( false );
				/*okokcb.addChangeListener( new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						sp.setEnabled( cb.isSelected() );
					}
				});*/
				Object[] message = new Object[] { cb, sp };
				JOptionPane.showMessageDialog(parentApplet, message);
				if( cb.isSelected() ) {
					int val = (Integer)sp.getValue();
					double[] old = d;
					double sum = 0.0;
					for( int k = 0; k < val; k++ ) {
						sum += old[k];
					}
					d = new double[ old.length-val ];
					for( int i = 0; i < d.length; i++ ) {
						d[i] = sum/(double)val;
						sum += -d[i]+d[i+val];
					}
				}
			}
		});
		popup.add( new AbstractAction("Dot plot") {
			public void actionPerformed( ActionEvent e ) {
				if( table.getSelectedRowCount() > 0 ) {
					int[] rr = table.getSelectedRows();
					final Sequence[]	seqs = new Sequence[2];
					for( int r : rr ) {
						int cr = table.convertRowIndexToModel(r);
						if( seqs[0] == null ) seqs[0] = serifier.lseq.get(cr);
						else {
							seqs[1] = serifier.lseq.get(cr);
							break;
						}
					}
					if( seqs[1] == null ) seqs[1] = seqs[0];
					
					final int x;
					final int y;
					
					final int offset;
					Rectangle rc = getSelectedRect();
					if( rc != null && rc.width > 0 ) {
						offset = rc.x;
						
						x = rc.width;
						y = rc.width;
					} else {
						offset = 0;
						
						x = seqs[0].length();
						y = seqs[1].length();
					}
					
					final JDialog	dialog = new JDialog();
					dialog.setSize(300, 200);
					dialog.setDefaultCloseOperation( dialog.DISPOSE_ON_CLOSE );
					
					JComponent	panel = new JComponent() {};
					Border brd = new EmptyBorder(10, 10, 10, 10);
					panel.setBorder( brd );
					GridLayout grid = new GridLayout( 5, 2 );
					grid.setHgap(5);
					grid.setVgap(5);
					panel.setLayout( grid );
					
					JLabel		windlab = new JLabel("Window");
					final JSpinner	windspin = new JSpinner( new SpinnerNumberModel(10, 1, 1000, 1) );
					JLabel		errlab = new JLabel("Errors");
					final JSpinner	errspin = new JSpinner( new SpinnerNumberModel(2, 0, 1000, 1) );
					JLabel		wlab = new JLabel("Width");
					final JSpinner	wspin = new JSpinner( new SpinnerNumberModel(x-10, 1, x-10, 1) );
					JLabel		hlab = new JLabel("Height");
					final JSpinner	hspin = new JSpinner( new SpinnerNumberModel(y-10, 1, y-10, 1) );
					JButton		ok = new JButton( new AbstractAction("Ok") {
						@Override
						public void actionPerformed(ActionEvent e) {
							dialog.dispose();
							
							JFrame	frame = new JFrame();
							frame.setSize(800, 600);
							frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
							
							Sequence first = seqs[0];
							Sequence second = seqs[1];
							int w = (Integer)windspin.getValue();
							int err = (Integer)errspin.getValue();
							
							final int ix = (Integer)wspin.getValue();
							final int iy = (Integer)hspin.getValue();
							final BufferedImage	bi = new BufferedImage( ix, iy, BufferedImage.TYPE_INT_ARGB );
							Graphics2D g2 = bi.createGraphics();
							g2.setColor( Color.white );
							g2.fillRect(0, 0, ix,  iy);
							
							int rx = x-w;
							int ry = y-w;
							if( err == -1 ) {
								String secstr = second.getSubstring(offset, offset+y, 1);
								for( int i = 0; i < rx; i++ ) {
									int ind = i+offset;
									String check = first.getSubstring(ind, ind+w, 1);//first.sb.substring(ind, ind+w);
									String rcheck = first.getSubstring(ind, ind+w, -1);
									
									int x = (ix*i)/rx;
									int k = secstr.indexOf( check );
									while( k != -1 ) {
										int y = (iy*k)/ry;
										/*if( !(x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight()) ) {
											System.err.println();
										}*/
										bi.setRGB(x, y, 0xFF000000);
										
										k = secstr.indexOf( check, k+1 );
									}
									int r = secstr.indexOf( rcheck );
									while( r != -1 ) {
										int y = (iy*r)/ry;
										//if( x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight() ) 
										bi.setRGB(x, y, 0xFFFF0000);
										
										r = secstr.indexOf( rcheck, r+1 );
									}
								}
							} else {
								Annotation rann = null;
								
								for( int i = 0; i < rx; i++ ) {
									Annotation fann = null;
									System.err.println( i + " of " + rx );
									for( int k = 0; k < ry; k++ ) {
										if( i != k ) {
											int count = 0;
											int rcount = 0;
											for( int v = 0; v < w; v++ ) {
												char c = first.getCharAt(i+v+offset);
												char sc = second.getCharAt(k-v+w-1+offset);
												if( c == second.getCharAt(k+v+offset) ) count++;
												
												Character rC = Sequence.rc.get( sc );
												
												/*if( rC == null ) {
													System.err.println();
												}*/
												
												char rc = rC;
												if( c == rc ) rcount++;
											}
											
											if( w - count <= err ) {
												if( /*rann == null &&*/ fann == null ) {
													fann = new Annotation( first,i,i+w,1, second.getSubstring(i, i+w, 1) );
												}
												
												int x = (ix*i)/rx;
												int y = (iy*k)/ry;
												if( x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight() ) {
													bi.setRGB(x, y, 0xFF000000);
													//bi.setRGB(y, x, 0xFF000000);
												}
											} /*else {
												if( fann != null && fann.stop - fann.start > 15 ) {
													fann.seq = first;
													serifier.lann.add( fann );
												}
												fann = null;
											}*/
											
											if( w - rcount <= err ) {
												if( /*rann == null &&*/ fann == null ) {
													fann = new Annotation( first,i,i+w,1, first.getSubstring(i, i+w, -1) );
												}
												
												int x = (ix*i)/rx;
												int y = (iy*k)/ry;
												if( x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight() ) {
													bi.setRGB(x, y, 0xFFFF0000);
													//bi.setRGB(y, x, 0xFFFF0000);
												}
											} /*else {
												if( rann != null && rann.stop - rann.start > 15 ) {
													rann.seq = first;
													serifier.lann.add( rann );
												}
												rann = null;
											}*/
										}
									}
									
									if( fann != null ) {
										if( rann != null && i-rann.start == rann.stop-rann.start-w+1 ) {
											rann.stop++;
										} else {
											serifier.lann.add( fann );
											first.addAnnotation( fann );
											fann.color = Color.cyan;
											rann = fann;
										}
									} else rann = null;
								}
							}
							
							List<Annotation> lann = new ArrayList<>();
							int[] rr = atable.getSelectedRows();
							//g2.setColor( Color.green );
							for( int r : rr ) {
								int m = atable.convertRowIndexToModel(r);
								Annotation a = serifier.lann.get(m);
								lann.add( a );
							}
							g2.setColor( Color.green );
							Collections.sort( lann );
							for( Annotation a : lann ) {
								int start = iy*(a.start-offset)/ry;
								int stop = iy*(a.stop-offset)/ry;
								
								if( start < ix && stop > 0 ) {
									if( g2.getColor() == Color.green ) g2.setColor( Color.darkGray );
									else g2.setColor( Color.green );
									g2.drawLine(start, start, stop, stop);
								}
							}
							
							//g2.setColor( Color.magenta );
							//g2.fillRect(10, 10, 90, 90);
							
							g2.dispose();
							
							/*for( int i = 20; i < 90; i++) {
								for( int k = 10; k < 100; k++) {
									bi.setRGB(i, k, 0xFFaaaa00);
								}
							}*/
							
							atable.tableChanged( new TableModelEvent( atable.getModel() ) );
							
							JComponent	comp = new JComponent() {
								public void paintComponent( Graphics g ) {
									super.paintComponent(g);
									
									g.drawImage( bi, 0, 0, this );
								}
							};
							comp.setPreferredSize( new Dimension(ix, iy) );
							JScrollPane	scrollpane = new JScrollPane();
							scrollpane.setViewportView( comp );
							frame.add( scrollpane );
							
							comp.addMouseListener( new MouseListener() {
								int startx;
								int starty;
								
								@Override
								public void mouseReleased(MouseEvent e) {
									int x = e.getX();
									int y = e.getY();
									
									int minx = Math.min(startx, x);
									//int miny = Math.min(starty, y);
									
									int maxx = Math.max(startx, x);
									//int maxy = Math.max(starty, y);
									
									int start = minx; //Math.min( minx, miny );
									int stop = maxx; //Math.max( maxx, maxy );
									
									double pstart = (double)(start*serifier.getDiff())/(double)ix;
									double pstop = (double)(stop*serifier.getDiff())/(double)ix;
									
									c.selectedRect.y = 0;
									c.selectedRect.height = 1;
									c.selectedRect.x = (int)pstart;
									c.selectedRect.width = (int)pstop - c.selectedRect.x;
									
									atable.clearSelection();
									int i = 0;
									for( Annotation a : serifier.lann ) {
										if( a.start < c.selectedRect.x+c.selectedRect.width && a.stop > c.selectedRect.x ) {
											int r = atable.convertRowIndexToView(i);
											atable.addRowSelectionInterval(r, r);
										}
										i++;
									}
									
									c.repaint();
								}
								
								@Override
								public void mousePressed(MouseEvent e) {
									startx = e.getX();
									starty = e.getY();
								}
								
								@Override
								public void mouseExited(MouseEvent e) {}
								
								@Override
								public void mouseEntered(MouseEvent e) {}
								
								@Override
								public void mouseClicked(MouseEvent e) {}
							});
							
							JPopupMenu	popup = new JPopupMenu();
							popup.add( new AbstractAction("Save image") {
								@Override
								public void actionPerformed(ActionEvent e) {
									ByteArrayOutputStream	baos = new ByteArrayOutputStream();
									try {
										ImageIO.write(bi, "png",  baos);
										
										/*FileSaveService fss; 
										try {
											fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
									    	} catch( UnavailableServiceException e2 ) {
									    		fss = null;
									    	}*/
									    	 
								         /*if (fss != null) {
								        	 	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
								             FileContents fileContents = fss.saveFileDialog(null, null, bais, "export.png");
								             bais.close();
								             OutputStream os = fileContents.getOutputStream(true);
								             os.write( baos.toByteArray() );
								             os.close();
								         } else {*/
								        	 JFileChooser jfc = new JFileChooser();
								        	 if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
								        		 File f = jfc.getSelectedFile();
								        		 FileOutputStream fos = new FileOutputStream( f );
								        		 fos.write( baos.toByteArray() );
								        		 fos.close();
								        		 
								        		 Desktop.getDesktop().browse( f.toURI() );
								        	 }
								         //}
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							});
							comp.setComponentPopupMenu( popup );
							
							frame.setVisible( true );
						}
					});
					dialog.add( panel );
					panel.add( windlab );
					panel.add( windspin );
					panel.add( errlab );
					panel.add( errspin );
					panel.add( wlab );
					panel.add( wspin );
					panel.add( hlab );
					panel.add( hspin );
					panel.add( ok );
					
					dialog.setVisible( true );
				}
			}
		});
		
		popup.add( new AbstractAction("GC plot") {
			public void actionPerformed( ActionEvent e ) {
				final JDialog	dialog = new JDialog();
				dialog.setSize(300, 200);
				dialog.setDefaultCloseOperation( dialog.DISPOSE_ON_CLOSE );
				
				JComponent	panel = new JComponent() {};
				Border brd = new EmptyBorder(10, 10, 10, 10);
				panel.setBorder( brd );
				GridLayout grid = new GridLayout( 3, 2 );
				grid.setHgap(5);
				grid.setVgap(5);
				panel.setLayout( grid );
				
				JLabel		windlab = new JLabel("Window");
				final JSpinner	windspin = new JSpinner( new SpinnerNumberModel(10, 1, 1000, 1) );
				JLabel		errlab = new JLabel("Points");
				final JSpinner	errspin = new JSpinner( new SpinnerNumberModel(2, 0, 1000, 1) );
				/*JLabel		wlab = new JLabel("Width");
				final JSpinner	wspin = new JSpinner( new SpinnerNumberModel(x-10, 1, x-10, 1) );
				JLabel		hlab = new JLabel("Height");
				final JSpinner	hspin = new JSpinner( new SpinnerNumberModel(y-10, 1, y-10, 1) );*/
				JButton		ok = new JButton( new AbstractAction("Ok") {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
						
						//JFrame	frame = new JFrame();
						fxframe.setSize(800, 600);
						fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
						
						if( fxp != null ) {
							fxp = new JFXPanel();
							fxframe.setLayout( new BorderLayout() );
							fxframe.add( fxp, BorderLayout.CENTER );
						}
						
						int w = (Integer)windspin.getValue();
						int p = (Integer)errspin.getValue();
						
						int ir = 0;
						int r = table.getSelectedRow();
						if( r != -1 ) r = table.convertRowIndexToModel( r );
						if( r != -1 ) ir = r;
						
						Sequence s = serifier.lseq.get(ir);
						
						final XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
						double[] d = new double[ p ];
						//int start = w/2;
						for( int i = 0; i < p; i++ ) {
							int u = i*(s.length()-w)/p;
							
							int tot = 0;
							int gctot = 0;
							for( int k = u; k < u+w; k++ ) {
								char c = s.charAt(k);
								if( c == 'c' || c == 'C' || c == 'g' || c == 'G' ) {
									gctot++;
								}
								tot++;
							}
							d[i] = (double)gctot/(double)tot;
							
							XYChart.Data<Number,Number> dd = new XYChart.Data<Number,Number>( i, d[i] );
				        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
				        	series.getData().add( dd );
						}
				       
						final NumberAxis xAxis = new NumberAxis();
					    final NumberAxis yAxis = new NumberAxis();
						final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
						//Scene scene = createBarChartScene( names, data, xTitle, yTitle, start, stop, step, title );
						
						Platform.runLater(new Runnable() {
			                 @Override
			                 public void run() {
			                	 if( scene == null ) {
			                		 scene  = new Scene(lineChart,800,600);
			                		 //fxframe.setScene( scene );
			                		 fxp.setScene(scene);
			                	 } else {
			                		 
			                		 scene.setRoot( lineChart );
			                	 }
								 lineChart.getData().add(series);
								 
								 //lineChart.re
			                 }
			            });
				        
						/*Sequence first = seqs[0];
						Sequence second = seqs[1];
						int w = (Integer)windspin.getValue();
						int err = (Integer)errspin.getValue();
						
						int ix = (Integer)wspin.getValue();
						int iy = (Integer)hspin.getValue();
						final BufferedImage	bi = new BufferedImage( ix, iy, BufferedImage.TYPE_INT_ARGB );
						Graphics2D g2 = bi.createGraphics();
						g2.setColor( Color.white );
						g2.fillRect(0, 0, ix,  iy);
						
						int rx = x-w;
						int ry = y-w;
						if( err == 0 ) {
							String secstr = second.getSubstring(offset, offset+y, 1);
							for( int i = 0; i < rx; i++ ) {
								int ind = i+offset;
								String check = first.getSubstring(ind, ind+w, 1);//first.sb.substring(ind, ind+w);
								String rcheck = first.getSubstring(ind, ind+w, -1);
								
								int x = (ix*i)/rx;
								int k = secstr.indexOf( check );
								while( k != -1 ) {
									int y = (iy*k)/ry;
									/*if( !(x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight()) ) {
										System.err.println();
									}*
									bi.setRGB(x, y, 0xFF000000);
									
									k = secstr.indexOf( check, k+1 );
								}
								int r = secstr.indexOf( rcheck );
								while( r != -1 ) {
									int y = (iy*r)/ry;
									//if( x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight() ) 
									bi.setRGB(x, y, 0xFFFF0000);
									
									r = secstr.indexOf( rcheck, r+1 );
								}
							}
						} else {
							for( int i = 0; i < rx; i++ ) {
								System.err.println( i + " of " + rx );
								for( int k = 0; k < ry; k++ ) {
									int count = 0;
									int rcount = 0;
									for( int v = 0; v < w; v++ ) {
										char c = first.getCharAt(i+v+offset);
										char sc = second.getCharAt(k-v+w-1+offset);
										if( c == second.getCharAt(k+v+offset) ) count++;
										
										Character rC = Sequence.rc.get( sc );
										
										char rc = rC;
										if( c == rc ) rcount++;
									}
									if( w - count <= err ) {
										int x = (ix*i)/rx;
										int y = (iy*k)/ry;
										if( x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight() ) bi.setRGB(x, y, 0xFF000000);
									}
									
									if( w - rcount <= err ) {
										int x = (ix*i)/rx;
										int y = (iy*k)/ry;
										if( x >= 0 && x < bi.getWidth() && y >= 0 && y < bi.getHeight() ) bi.setRGB(x, y, 0xFFFF0000);
									}
								}
							}
						}
						
						int[] rr = atable.getSelectedRows();
						g2.setColor( Color.green );
						for( int r : rr ) {
							int m = atable.convertRowIndexToModel(r);
							Annotation a = serifier.lann.get(m);
							int start = iy*(a.start-offset)/ry;
							int stop = iy*(a.stop-offset)/ry;
							
							if( start < ix && stop > 0 ) {
								g2.drawLine(start, start, stop, stop);
							}
						}
						g2.dispose();
						
						JComponent	comp = new JComponent() {
							public void paintComponent( Graphics g ) {
								super.paintComponent(g);
								
								g.drawImage( bi, 0, 0, this );
							}
						};
						comp.setPreferredSize( new Dimension(ix, iy) );
						JScrollPane	scrollpane = new JScrollPane();
						scrollpane.setViewportView( comp );
						frame.add( scrollpane );
						
						comp.addMouseListener( new MouseListener() {
							int startx;
							int starty;
							
							@Override
							public void mouseReleased(MouseEvent e) {
								int x = e.getX();
								int y = e.getY();
								
								int minx = Math.min(startx, x);
								int miny = Math.min(starty, y);
								
								int maxx = Math.max(startx, x);
								int maxy = Math.max(starty, y);
								
								int start = Math.min( minx, miny );
								int stop = Math.max( maxx, maxy );
								
								double pstart = (double)(start*serifier.getDiff())/(double)ix;
								double pstop = (double)(stop*serifier.getDiff())/(double)ix;
								
								c.selectedRect.x = (int)pstart;
								c.selectedRect.width = (int)pstop - c.selectedRect.x;
								
								atable.clearSelection();
								int i = 0;
								for( Annotation a : serifier.lann ) {
									if( a.start < c.selectedRect.x+c.selectedRect.width && a.stop > c.selectedRect.x ) {
										int r = atable.convertRowIndexToView(i);
										atable.addRowSelectionInterval(r, r);
									}
									i++;
								}
								
								c.repaint();
							}
							
							@Override
							public void mousePressed(MouseEvent e) {
								startx = e.getX();
								starty = e.getY();
							}
							
							@Override
							public void mouseExited(MouseEvent e) {}
							
							@Override
							public void mouseEntered(MouseEvent e) {}
							
							@Override
							public void mouseClicked(MouseEvent e) {}
						});
						
						JPopupMenu	popup = new JPopupMenu();
						popup.add( new AbstractAction("Save image") {
							@Override
							public void actionPerformed(ActionEvent e) {
								ByteArrayOutputStream	baos = new ByteArrayOutputStream();
								try {
									ImageIO.write(bi, "png",  baos);
									
									FileSaveService fss; 
									try {
										fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
							    	} catch( UnavailableServiceException e2 ) {
							    		fss = null;
							    	}
							    	 
							         if (fss != null) {
							        	 ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
							             FileContents fileContents = fss.saveFileDialog(null, null, bais, "export.png");
							             bais.close();
							             OutputStream os = fileContents.getOutputStream(true);
							             os.write( baos.toByteArray() );
							             os.close();
							         } else {
							        	 JFileChooser jfc = new JFileChooser();
							        	 if( jfc.showSaveDialog( parentApplet ) == JFileChooser.APPROVE_OPTION ) {
							        		 File f = jfc.getSelectedFile();
							        		 FileOutputStream fos = new FileOutputStream( f );
							        		 fos.write( baos.toByteArray() );
							        		 fos.close();
							        		 
							        		 Desktop.getDesktop().browse( f.toURI() );
							        	 }
							         }
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						});
						comp.setComponentPopupMenu( popup );*/
						
						fxframe.setVisible( true );
					}
				});
				dialog.add( panel );
				panel.add( windlab );
				panel.add( windspin );
				panel.add( errlab );
				panel.add( errspin );
				panel.add( ok );
				
				dialog.setVisible( true );
			}
		});
		table.setComponentPopupMenu( popup );
		tablescroll.setComponentPopupMenu( popup );
		
		/*fastascroll.getRowHeader().addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				table.getVisibleRect();
				Rectangle r = c.getVisibleRect();
				r.y = table.getY();
				c.scrollRectToVisible( r );
			}
		});*/
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				table.getVisibleRect();
				Rectangle r = c.getVisibleRect();
				r.y = -table.getY();
				//System.err.println( r );
				c.scrollRectToVisible( r );
				setStatus();
			}
		});
		table.addMouseListener( new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if( e.getClickCount() == 2 ) {
					int r = table.getSelectedRow();
					int i = table.convertRowIndexToModel( r );
					Sequence s = serifier.lseq.get( i );
					
					Rectangle rect = c.getVisibleRect();
					if( rect.x == (int)((s.getStart()-serifier.getMin())*c.cw) ) {
						rect.x = (int)((s.getEnd()-serifier.getMin())*c.cw)-rect.width;
					} else {
						rect.x = (int)((s.getStart()-serifier.getMin())*c.cw);
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
				int keycode = e.getKeyCode();
				char keychar = e.getKeyChar();
				if( keycode == KeyEvent.VK_DELETE ) {
					delete();
				} else if( keycode == KeyEvent.VK_ESCAPE ) {
					filterset.clear();
					c.updateCoords();
					table.tableChanged( new TableModelEvent(table.getModel()) );
				} else if( keycode == KeyEvent.VK_LEFT ) {
					if( e.isAltDown() ) { //e.getModifiers() & KeyEvent.VK_CONTROL ) {
						int r = table.getSelectedRow();
						int i = table.convertRowIndexToModel(r);
						Sequence seq = serifier.lseq.get(i);
						seq.setStart( seq.getStart()-1 );
						List<Sequence> lseq = serifier.gseq.get(seq.getName());
						if( lseq != null ) {
							for( Sequence nseq : lseq ) {
								nseq.setStart( nseq.getStart()-1 );
							}
						}// else seq.setStart( seq.getStart()-1 );
						c.updateCoords();
						c.repaint();
					}
				} else if( keycode == KeyEvent.VK_RIGHT ) {
					if( e.isAltDown() ) { //e.getModifiers() & KeyEvent.VK_CONTROL ) {
						int r = table.getSelectedRow();
						int i = table.convertRowIndexToModel(r);
						Sequence seq = serifier.lseq.get(i);
						seq.setStart( seq.getStart()+1 );
						List<Sequence> lseq = serifier.gseq.get(seq.getName());
						if( lseq != null ) {
							for( Sequence nseq : lseq ) {
								nseq.setStart( nseq.getStart()+1 );
							}
						}// else seq.setStart( seq.getStart()+1 );
						c.updateCoords();
						c.repaint();
					}
				}
			}
		});
		
		overview = new Overview();
		overviewsplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		overviewsplit.setDividerLocation(0.7);
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
				Annotation a = serifier.lann.get(i);
				
				if( a.desc != null ) return a.desc.toString();
				return a.getName();
			}
		};
		atable.setDragEnabled( true );
		atable.setToolTipText( "" );
		atable.setAutoCreateRowSorter( true );
		
		JScrollPane	ascroll = new JScrollPane( atable );
		JComponent	acomp = new JComponent() {};
		acomp.setLayout( new BorderLayout() );
		acomp.add( ascroll );
		acomp.add( asearch, BorderLayout.SOUTH );
		mainsplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		mainsplit.setDividerLocation(0.7);
		mainsplit.setLeftComponent( overviewsplit );
		mainsplit.setRightComponent( acomp );
		
		JPopupMenu	apopup = new JPopupMenu();
		atable.setComponentPopupMenu( apopup );
		apopup.add( new AbstractAction("Open as sequence") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame	frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setSize(800, 600);
				JavaFasta	jf = new JavaFasta();
				jf.initGui( frame );
				
				int[] rr = atable.getSelectedRows();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					Annotation ann = serifier.lann.get(i);
					Sequence aseq = ann.createSequence();
					jf.serifier.addSequence(aseq);
					jf.serifier.mseq.put(aseq.getName(), aseq);
				}
			
				jf.updateView();
				
				frame.setVisible( true );
			}
		});
		apopup.addSeparator();
		apopup.add( new AbstractAction("Delete") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Annotation> remannset = new HashSet<Annotation>();
				int[] rr = atable.getSelectedRows();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					
					if( i >= 0 && i < serifier.lann.size() ) {
						Annotation a = serifier.lann.get(i);
						remannset.add( a );
					}
					
					// right?
					//a.seq.removeAnnotation( a );
				}
				serifier.lann.removeAll( remannset );
				atable.tableChanged( new TableModelEvent(atable.getModel() ) );
			}
		});
		apopup.add( new AbstractAction("Delete from sequence") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Annotation> remannset = new HashSet<Annotation>();
				int[] rr = atable.getSelectedRows();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					
					Annotation a = serifier.lann.get(i);
					remannset.add( a );
					
					a.getSeq().removeAnnotation( a );
				}
				serifier.lann.removeAll( remannset );
				atable.tableChanged( new TableModelEvent(atable.getModel() ) );
			}
		});
		
		apopup.add( new AbstractAction("Align offset") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int max = 0;
				int[] rr = atable.getSelectedRows();
				Map<Sequence,Annotation> seqs = new HashMap<>();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					Annotation a = serifier.lann.get(i);
					seqs.put( a.getSeq(), a );
					max = Math.max(max, a.start);
				}
				
				for( Sequence seq : seqs.keySet() ) {
					Annotation sela = seqs.get(seq);
					seq.setStart(max-sela.start);
				}
			}
		});
		apopup.add( new AbstractAction("Rotate to here") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = atable.getSelectedRows();
				Map<Sequence,Annotation> seqs = new HashMap<>();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					Annotation a = serifier.lann.get(i);
					a.getContig().shift(a.start);
				}
			}
		});
		apopup.add( new AbstractAction("Align selected start") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int max = 0;
				int[] rr = atable.getSelectedRows();
				Map<Sequence,Annotation> seqs = new HashMap<>();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					Annotation a = serifier.lann.get(i);
					seqs.put( a.getSeq(), a );
					max = Math.max(max, a.start);
				}
				
				for( Sequence seq : seqs.keySet() ) {
					Annotation sela = seqs.get(seq);
					List<Annotation> lann = seq.getAnnotations();
					if( lann != null ) {
						int hit = -1;
						for( Annotation a : lann ) {
							if( a == sela ) {
								hit = max-a.start;
								while( hit > 0 ) {
									seq.getSequence().insert(0, '-');
									//seq.sb.append('-')
									hit--;
								}
								hit = max-a.start;
								
								break;
							}
						}
						
						for( Annotation a : lann ) {
							if( hit != -1 ) {
								a.start += hit;
								a.stop += hit;
							}
						}
					}
				}
			}
		});
		apopup.add( new AbstractAction("Align selected end") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int max = 0;
				int[] rr = atable.getSelectedRows();
				Map<Sequence,Annotation> seqs = new HashMap<>();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					Annotation a = serifier.lann.get(i);
					seqs.put( a.getSeq(), a );
					max = Math.max(max, a.stop);
				}
				
				for( Sequence seq : seqs.keySet() ) {
					Annotation sela = seqs.get(seq);
					List<Annotation> lann = seq.getAnnotations();
					if( lann != null ) {
						int hit = -1;
						System.err.println( "mmu start" );
						for( Annotation a : lann ) {
							if( hit != -1 ) { 
								System.err.println( "mmu " + a.start );
								
								a.start += hit;
								a.stop += hit;
							} else System.err.println( "mmux " + a.start );
							
							if( a == sela ) {
								hit = max-a.stop;
								while( hit > 0 ) {
									seq.getSequence().insert(a.stop, '-');
									//seq.sb.append('-')
									hit--;
								}
								hit = max-a.stop;
							}
						}
					}
				}
			}
		});
		apopup.addSeparator();
		apopup.add( new AbstractAction("Select mummer") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				for( Annotation ann : serifier.lann ) {
					if( ann != null && ann.type != null && ann.type.contains("mummer") ) {
						int r = atable.convertRowIndexToView(i);
						atable.addRowSelectionInterval(r, r);
					}
					i++;
				}
			}
		});
		apopup.add( new AbstractAction("Inject annotation") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = atable.getSelectedRows();
				if( rr != null && rr.length > 0 ) {
					Set<Sequence> sset = new HashSet<>();
					for( int r : rr ) {
						int i = atable.convertRowIndexToModel(r);
						Annotation ann = serifier.lann.get(i);
						if( ann.getSeq() != null ) {
							ann.getSeq().addAnnotation( ann );
							sset.add( ann.getSeq() );
						}
					}
					
					for( Sequence seq : sset ) {
						seq.sortLocs();
					}
				}
			}
		});
		apopup.add( new AbstractAction("CRISPR types") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Integer> hcell = new HashMap<>();
				hcell.put("Csh2", 1);
				hcell.put("Csm2", 2);
				hcell.put("Cse2", 3);
				hcell.put("Csd2", 4);
				hcell.put("Cmr3", 5);
				
				for( Sequence seq : serifier.lseq ) {
					List<Annotation> lann = seq.getAnnotations();
					if( lann != null ) {
						String found = null;
						int fidx = -1;
						
						for( int k = 0; k < lann.size(); k++ ) {
							Annotation a = lann.get(k);
							
							for( String type : hcell.keySet() ) {
								if( a.getName() != null && a.getName().contains(type) ) {
									found = type;
									fidx = k;
								}
							}
							
							String type = a.getType();
							if( found != null && type != null && type.contains("mummer") ) {
								if( !a.getName().contains("-") ) {
									a.setName( found+"-"+a.getName() );
								}
							}
							
							if( k > fidx+100 ) {
								found = null;
							}
						}
						
						found = null;
						fidx = -1;
						
						for( int k = lann.size()-1; k >= 0; k-- ) {
							Annotation a = lann.get(k);
							
							for( String type : hcell.keySet() ) {
								if( a.getName() != null && a.getName().contains(type) ) {
									found = type;
									fidx = k;
								}
							}
							
							String type = a.getType();
							if( found != null && type != null && type.contains("mummer") ) {
								if( !a.getName().contains("-") ) {
									a.setName( found+"-"+a.getName() );
								}
							}
							
							if( k < fidx-100 ) {
								found = null;
							}
						}
					}
				}
			}
		});
		apopup.add(new AbstractAction("Retain CRISPR contigs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Sequence> contigs = new HashSet<>();
				int[] rr = atable.getSelectedRows();
				if( rr != null && rr.length > 0 ) {
					for (int r : rr) {
						int i = atable.convertRowIndexToModel(r);
						Annotation ann = serifier.lann.get(i);
						contigs.add( ann.getContig() );
					}
				}
				serifier.lseq.retainAll(contigs);
				serifier.lgseq.retainAll(contigs);
				Set<String> ss = new HashSet<>();
				for( String s : serifier.mseq.keySet() ) {
					Sequence seq = serifier.mseq.get(s);
					if( contigs.contains(seq) ) ss.add(s);
				}
				serifier.mseq.keySet().retainAll(ss);

				Set<Annotation> allset = new HashSet<>();
				for( Annotation a : serifier.lann ) {
					if( contigs.contains(a.getContig()) ) allset.add(a);
				}
				serifier.lann.retainAll( allset );
				atable.tableChanged( new TableModelEvent(atable.getModel() ) );
				table.tableChanged( new TableModelEvent(atable.getModel() ) );
			}
		});
		apopup.add( new AbstractAction("Retain CRISPR") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Annotation> allset = new HashSet<>();
				
				Map<Sequence, List<Annotation>> mann = new HashMap<>();
				int[] rr = atable.getSelectedRows();
				if( rr != null && rr.length > 0 ) {
					for( int r : rr ) {
						int i = atable.convertRowIndexToModel(r);
						Annotation ann = serifier.lann.get(i);
					//for( Annotation ann : serifier.lann ) {
						List<Annotation> lann;
						if( mann.containsKey(ann.getSeq()) ) {
							lann = mann.get(ann.getSeq());
						} else {
							lann = new ArrayList<>();
							mann.put( ann.getSeq(), lann );
						}
						lann.add( ann );
						allset.add( ann );
						
						//mann.put( ann.seq, ann );
					}
				} else {
					for( Annotation ann : serifier.lann ) {
						if( ann == null ) {
							System.err.println();
						}
						if( ann != null && ann.type != null && ann.type.contains("mummer") ) {
							List<Annotation> lann;
							if( mann.containsKey(ann.getSeq()) ) {
								lann = mann.get(ann.getSeq());
							} else {
								lann = new ArrayList<>();
								mann.put( ann.getSeq(), lann );
							}
							lann.add( ann );
							allset.add( ann );
						}
						
						//mann.put( ann.seq, ann );
					}
				}
				System.err.println( "reps " + allset.size() );
				
				Set<Annotation> remannset = new HashSet<>();
				for( Sequence seq : mann.keySet() ) {
					List<Annotation> lann = mann.get( seq );
					Collections.sort(lann);
					
					Set<Annotation> remo = new HashSet<>();
					Annotation first = lann.get(0);
					for( int i = 1; i < lann.size(); i++ ) {
						Annotation second = lann.get(i);
						
						if( second.start < first.stop ) {
							first.stop = Math.max(first.stop, second.stop);
							remo.add( second );
						} else first = second;
					}
					lann.removeAll( remo );
					
					for( int i = 0; i < lann.size()-1; i++ ) {
						first = lann.get(i);
						Annotation second = lann.get(i+1);
						
						if( first.stop-first.start < 50 && second.stop-second.start < 50 && second.start-first.stop > 15 && second.start-first.stop < 50 ) {
							boolean similar = false;
							String minna = first.getName().length() < second.getName().length() ? first.getName() : second.getName();
							String meira = minna == first.getName() ? second.getName() : first.getName();
							
							minna = minna.toUpperCase();
							meira = meira.toUpperCase();
							
							for( int u = 0; u < meira.length()-20; u++ ) {
								int count = 0;
								for( int m = 0; m < Math.min(meira.length()-u,minna.length()); m++ ) {
									if( minna.charAt(m) == meira.charAt(m+u) ) count++;
								}
								
								if( count >= 20 ) {
									similar = true;
									break;
								}
							}
							
							if( !similar ) for( int u = 0; u < minna.length()-20; u++ ) {
								int count = 0;
								for( int m = 0; m < Math.min(minna.length()-u,meira.length()); m++ ) {
									if( minna.charAt(m+u) == meira.charAt(m) ) count++;
								}
								
								if( count >= 20 ) {
									similar = true;
									break;
								}
							}
							
							if( similar ) {
								remannset.add( first );
								remannset.add( second );
								//i++;
							}
						}
					}
				}
				
				System.err.println( "remann "+remannset.size() );
				allset.removeAll( remannset );
				/*int[] rr = atable.getSelectedRows();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel(r);
					
					Annotation a = serifier.lann.get(i);
					remannset.add( a );
					
					a.seq.removeAnnotation( a );
				}*/
				
				serifier.lann.removeAll( allset );
				atable.tableChanged( new TableModelEvent(atable.getModel() ) );
			}
		});
		apopup.add( new AbstractAction("Remove repeats within prophage") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( Sequence seq : serifier.lseq ) {
					List<Annotation> lann = seq.getAnnotations();
					if( lann != null ) {
						Set<Annotation>	allrem = new HashSet<>();
						Set<Annotation> remann = new HashSet<>();
						int phagemummer = 0;
						for( Annotation ann : lann ) {
							if( phagemummer > 0 && ann != null && ann.type != null && ann.type.contains("mummer") ) {
								phagemummer = 2;
								remann.add( ann );
							} else if( ann != null && ann.designation != null && ann.designation.contains("phage") ) {
								if( phagemummer == 2 ) {
									allrem.addAll( remann );
								}
								remann.clear();
								phagemummer = 1;
							} else {
								remann.clear();
								phagemummer = 0;
							}
						}
						if( allrem.size() > 0 ) {
							lann.removeAll( allrem );
							serifier.lann.removeAll( allrem );
						}
						atable.tableChanged( new TableModelEvent( atable.getModel() ) );
					}
				}
			}
		});
		apopup.add( new AbstractAction("CRISPR report") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Map<String,Map<String,Map<String,List<Sequence>>>> whatf = new HashMap<String,Map<String,Map<String,List<Sequence>>>>();
				Map<String, Map<String,List<Annotation>>>	tann = new LinkedHashMap<>();
				Map<String, List<Annotation>> 			mann = new TreeMap<>();
				for( Annotation ann : serifier.lann ) {
					if( ann != null && ann.type != null && ann.type.contains("mummer") ) {
						ann.setName( ann.getName().toUpperCase() );

						if(ann.getSeq()!=null) {
							List<Annotation> lann;
							if (mann.containsKey(ann.getSeq().getName())) {
								lann = mann.get(ann.getSeq().getName());
							} else {
								lann = new ArrayList<>();
								System.err.println();
								mann.put(ann.getSeq().getName(), lann);
							}
							lann.add(ann);
						} else {
							System.err.println();
						}
						//allset.add( ann );
						//mann.put( ann.seq, ann );
					}
				}
				
				int maxcount = 14;
				Set<String> specset = new TreeSet<>();
				for( String seq : mann.keySet() ) {
					List<Annotation> lann = mann.get( seq );
					Collections.sort(lann);
					
					String spec = Sequence.getSpec( seq );
					//spec = Sequence.nameFix(spec, true);
					//System.err.println( "truff " + seq + " " + lann.size() );
					
					specset.add( spec );
					
					for( int i = 0; i < lann.size(); i++ ) {
						Annotation ann = lann.get(i);
						String annname = ann.getName();
						int idx = annname.indexOf('-');
						if( idx != -1 ) annname = annname.substring(idx+1).toUpperCase();
						
						int storecount = 0;
						String similar = null;
						boolean rev = false;
						
						int maxconsecount = 0;
						for( String rep : tann.keySet() ) {
							//String minna = ann.name.length() < rep.length() ? ann.name : rep;
							//String meira = minna == ann.name ? rep : ann.name;
							
							//minna = minna.toUpperCase();
							//meira = meira.toUpperCase();
							
							for( int u = 0; u < rep.length()-maxcount; u++ ) {
								int count = 0;
								int consecount = 0;
								int storeconsecount = 0;
								int size = Math.min(rep.length()-u,annname.length());
								for( int m = 0; m < size; m++ ) {
									if( annname.charAt(m) == rep.charAt(m+u) ) {
										count++;
										consecount++;
									} else {
										if( consecount > storeconsecount ) storeconsecount = consecount;
										consecount = 0;
									}
								}
								if( consecount > storeconsecount ) storeconsecount = consecount;
								
								if( count >= maxcount && storeconsecount > maxconsecount ) {
									storecount = count;
									maxconsecount = storeconsecount;
									similar = rep.toUpperCase();
								}
							}
							
							for( int u = 0; u < annname.length()-maxcount; u++ ) {
								int count = 0;
								int consecount = 0;
								int storeconsecount = 0;
								int size = Math.min(annname.length()-u,rep.length());
								for( int m = 0; m < size; m++ ) {
									if( annname.charAt(m+u) == rep.charAt(m) ) {
										count++;
										consecount++;
									} else {
										if( consecount > storeconsecount ) storeconsecount = consecount;
										consecount = 0;
									}
								}
								if( consecount > storeconsecount ) storeconsecount = consecount;
								
								if( count >= maxcount && storeconsecount > maxconsecount ) {
									storecount = count;
									maxconsecount = storeconsecount;
									similar = rep.toUpperCase();
								}
							}
							
							for( int u = 0; u < rep.length()-maxcount; u++ ) {
								int count = 0;
								int consecount = 0;
								int storeconsecount = 0;
								int size = Math.min(rep.length()-u,annname.length());
								for( int m = 0; m < size; m++ ) {
									if( annname.charAt(m) == Sequence.rc.get( rep.charAt(rep.length()-1-(m+u)) ) ) {
										count++;
										consecount++;
									} else {
										if( consecount > storeconsecount ) storeconsecount = consecount;
										consecount = 0;
									}
								}
								if( consecount > storeconsecount ) storeconsecount = consecount;
								
								if( count >= maxcount && storeconsecount > maxconsecount ) {
									rev = true;
									storecount = count;
									maxconsecount = storeconsecount;
									similar = rep.toUpperCase();
								}
							}
							
							for( int u = 0; u < annname.length()-maxcount; u++ ) {
								int count = 0;
								int consecount = 0;
								int storeconsecount = 0;
								int size = Math.min(annname.length()-u,rep.length());
								for( int m = 0; m < size; m++ ) {
									if( annname.charAt(m+u) == Sequence.rc.get( rep.charAt(rep.length()-1-m) ) ) {
										count++;
										consecount++;
									} else {
										if( consecount > storeconsecount ) storeconsecount = consecount;
										consecount = 0;
									}
								}
								if( consecount > storeconsecount ) storeconsecount = consecount;
								
								if( count >= maxcount && storeconsecount > maxconsecount ) {
									rev = true;
									storecount = count;
									maxconsecount = storeconsecount;
									similar = rep.toUpperCase();
								}
							}
							
							/*if( similar != null ) {
								break;
							}*/
						}
						
						List<Annotation> lan;
						if( similar != null && tann.containsKey(similar) ) {
							Map<String,List<Annotation>> lanm = tann.get( similar );
							String aspec = ann.getSeq().getSpec();
							if( lanm.containsKey(spec) ) {
								lan = lanm.get( spec );
							} else {
								lan = new ArrayList<>();
								lanm.put( spec, lan );
							}
							
							/*if( ann.seq.name.contains("2137") ) {
								System.err.println( "setnext: " + annname + " similar to " + similar + " rev " + rev + " storecount " + storecount );
							}*/
							
							//System.err.println( "blubbbbbi " + ann.seq.name );
						} else {
							Map<String,List<Annotation>> lanm = new HashMap<>();
							lan = new ArrayList<>();
							lanm.put( ann.getSeq().getSpec(), lan );
							tann.put( annname, lanm );
							
							//if( ann.seq.name.contains("2137") ) System.err.println( "reference: " + annname );
						}
						lan.add( ann );
					}
				}
				
				Map<String,CellStyle>	repeatColor = new HashMap<>();
				
				List<RepeatNum>	lrn = new ArrayList<>();
				Map<String,String>	commonRepeatMap = new HashMap<>();
				for( String rep : tann.keySet() ) {
					Map<String,List<Annotation>>	lanm = tann.get(rep);
					
					int sum = 0;
					for( String spec : lanm.keySet() ) {
						List<Annotation> slann = lanm.get(spec);
						for( Annotation a : slann ) commonRepeatMap.put(a.getName(), rep);
						
						sum += slann.size();
					}
					
					lrn.add( new RepeatNum(rep,sum) );
				}
				
				Workbook wb = new XSSFWorkbook();
				Sheet sh = wb.createSheet("CRISPR");
				
				CellStyle csPlas = wb.createCellStyle();
				csPlas.setFillForegroundColor( IndexedColors.RED.index );
				csPlas.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				repeatColor.put("plas", csPlas);
				
				CellStyle csNb = wb.createCellStyle();
				csNb.setFillForegroundColor( IndexedColors.GREEN.index );
				csNb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				repeatColor.put("np", csNb);
				
				
				CellStyle csRed = wb.createCellStyle();
				csRed.setFillForegroundColor( IndexedColors.RED.index );
				csRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				CellStyle csGreen = wb.createCellStyle();
				csGreen.setFillForegroundColor( IndexedColors.GREEN.index );
				csGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				CellStyle csBlue = wb.createCellStyle();
				csBlue.setFillForegroundColor( IndexedColors.BLUE.index );
				csBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				CellStyle csCyan = wb.createCellStyle();
				csCyan.setFillForegroundColor( IndexedColors.PLUM.index );
				csCyan.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				CellStyle csMagenta = wb.createCellStyle();
				csMagenta.setFillForegroundColor( IndexedColors.AQUA.index );
				csMagenta.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				CellStyle csYellow = wb.createCellStyle();
				csYellow.setFillForegroundColor( IndexedColors.YELLOW.index );
				csYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				int i = 0;
				CellStyle[] colors = new CellStyle[] { csRed, csGreen, csBlue, csCyan, csMagenta, csYellow };
				Collections.sort( lrn );
				Map<String,Map<String,List<Annotation>>>	tann2 = new LinkedHashMap<String,Map<String,List<Annotation>>>();
				for( RepeatNum rn : lrn ) {
					if( i < 6 ) repeatColor.put(rn.repeat, colors[i++]);
					tann2.put(rn.repeat, tann.get(rn.repeat));
				}
				tann.clear();
				tann = tann2;
				
				
				
				int total = 0;
				for( String rep : tann.keySet() ) {
					Map<String,List<Annotation>> lanm = tann.get( rep );
					for( String str : lanm.keySet() ) {
						List<Annotation> lann = lanm.get(str);
						total += lann.size();
					}
				}
				
				Map<String,Row> hrow = new HashMap<>();
				Map<String,Integer> hcell = new HashMap<>();
				hcell.put("Csh2", 1);
				hcell.put("Csm2", 2);
				hcell.put("Cse2", 3);
				hcell.put("Csd2", 4);
				hcell.put("Cmr3", 5);
				
				Map<String,Map<String,Integer>> typeRepeat = new HashMap<>();
				
				//hcell.put();
				
				Map<String,Integer> hcell2 = new HashMap<>();
				//hcell.put("Csh2", 1);
				hcell2.put("III-A", 2);
				hcell2.put("I-E", 3);
				//hcell.put("Csd2", 4);
				hcell2.put("III-B", 5);
				
				i = 0;
				List<Row> nullrow = new ArrayList<>();
				Row rw = sh.createRow(i++);
				nullrow.add( rw );
				for( String crispr : hcell.keySet() ) {
					int c = hcell.get(crispr);
					rw.createCell(c).setCellValue(crispr);
				}
				rw.createCell(hcell.size()+1).setCellValue("Cas6");
				
				rw = sh.createRow(i++);
				nullrow.add( rw );
				for( String crispr : hcell2.keySet() ) {
					int c = hcell2.get(crispr);
					rw.createCell(c).setCellValue(crispr);
				}
				
				int k = 0;
				for( String spec : specset ) {
					rw = sh.createRow(i++);
					nullrow.add( rw );
					hrow.put(spec,rw);
					
					String nspec = Sequence.nameFix(spec, true);
					rw.createCell(k).setCellValue(nspec);
				}
				
				i++;
				//i = 0;
				k = 1;
				rw = sh.createRow(i);
				rw = sh.createRow(i);
				rw.createCell(k++).setCellValue("sum");
				System.err.print("\tsum");
				for( String spec : specset ) {
					String nspec = Sequence.nameFix(spec, true);
					rw.createCell(k++).setCellValue(nspec);
					System.err.print( "\t" + spec );
				}
				System.err.println();
				
				i++;
				Map<String,Integer> msi = new HashMap<String,Integer>();
				for( String rep : tann.keySet() ) {
					Map<String,List<Annotation>> lanm = tann.get( rep );
					int sum = 0;
					for( String s : lanm.keySet() ) {
						sum += lanm.get(s).size();
					}
					
					k = 0;
					rw = sh.createRow(i++);
					
					Cell cell = rw.createCell(k++);
					cell.setCellValue(rep);
					CellStyle cs = repeatColor.get(rep);
					if( cs != null ) cell.setCellStyle( cs );
					
					rw.createCell(k++).setCellValue(sum);
					
					System.err.print( rep+"\t"+sum );
					for( String spec : specset ) {
						int count = 0;
						if( lanm.containsKey(spec) ) {
							count = lanm.get(spec).size();
							if( msi.containsKey(spec) ) {
								msi.put(spec, msi.get(spec)+count);
							} else {
								msi.put(spec, count);
							}
						}
						rw.createCell(k++).setCellValue(count);
						System.err.print( "\t" + count );
					}
					System.err.println();
				}
				
				rw = sh.createRow(i++);
				k = 1;
				rw.createCell(k++).setCellValue(total);;
				for( String spec : specset ) {
					if( msi.containsKey(spec) ) {
						int count = msi.get(spec);
						rw.createCell(k++).setCellValue(count);
						System.err.print( "\t"+count );
					} else {
						rw.createCell(k++).setCellValue(0);
						System.err.print( "\t0" );
					}
				}
				Map<String,CellStyle>	csMap = new HashMap<>();
				
				i++;
				List<Row> lrow = new ArrayList<>();
				Row hd = sh.createRow(i++);
				k = 2;
				for( String spec : specset ) {
					int u = 0;
					String nspec = Sequence.nameFix(spec, true);
					hd.createCell(k).setCellValue(nspec);
					for( Sequence seq : serifier.lseq ) {
						if( seq.getName().contains(spec) && mann.containsKey(seq.getName()) ) {
							List<Annotation> repeats = mann.get(seq.getName());
							
							if( u < lrow.size() ) rw = lrow.get(u);
							else {
								rw = sh.createRow(i+u);
								lrow.add(rw);
							}
							u++;
							
							rw.createCell(k).setCellValue(seq.getName());
							
							List<Annotation> olann = seq.getAnnotations();
							if( olann != null ) {
								String type = null;
								int mcount = 0;
								String lastrep = null;
								int count = 0;
								
								List<Annotation> lann = new ArrayList<>( olann );
								lann.addAll( repeats );
								Collections.sort( lann );
								
								Map<String,Integer> lastrepeats = new HashMap<>();
								
								int onlycas = -1;
								String name = null;
								String lastname = null;
								for( Annotation ann : lann ) {
									name = ann.getName();
									if( ann instanceof Tegeval ) {
										Tegeval tv = (Tegeval)ann;
										GeneGroup gg = tv.getGene().getGeneGroup();
										if( gg != null ) name = gg.getName();
									}
									int m = name.indexOf('(');
									if( m == -1 ) m = name.length();
									name = name.substring(0,m);
									
									if( ann.type != null && ann.type.contains("mummer") ) {
										if( onlycas > 2 ) {
											Row r = hrow.get(spec);
											int c = hcell.size()+1;
											
											Contig ct = (Contig)seq;
											boolean plas = ct.isPlasmid();
											String plastr = plas ? "plas" : "np";
											
											Cell cell = r.createCell(c);
											cell.setCellValue( plastr );
											
											CellStyle cs = repeatColor.get(plastr);
											if( cs != null ) {
												cell.setCellStyle(cs);
											}
										}
										onlycas = 0;
										String rep = commonRepeatMap.get(ann.getName()); //null;
										
										if( lastrepeats.containsKey(rep) ) {
											lastrepeats.put( rep, lastrepeats.get(rep)+1 );
										} else {
											lastrepeats.put(rep, 1);
										}
										/*for( String rp : tann.keySet() ) {
											Map<String,List<Annotation>> mmp = tann.get(rp);
											List<Annotation> lll = mmp.get( ann.seq.getSpec() );
											if( lll != null ) {
												for( Annotation a : lll ) {
													if( a.equals(ann) ) {
														rep = rp;
														break;
													}
													//if( a.name.equals(anObject))
												}
											}
										}
										System.err.println( rep );*/
										
										if( mcount > 0 && lastrep != null && !lastrep.equals(rep) ) {
											if( u < lrow.size() ) rw = lrow.get(u);
											else {
												rw = sh.createRow(i+u);
												lrow.add(rw);
											}
											u++;
											Cell cell = rw.createCell(k);
											cell.setCellValue( mcount+"-"+lastrep );
											CellStyle cs = repeatColor.get(lastrep);
											if( cs != null ) cell.setCellStyle( cs );
											
											mcount = 0;
										}
										mcount++;
										lastrep = rep;
										
										if( count > 0 ) {
											if( u < lrow.size() ) rw = lrow.get(u);
											else {
												rw = sh.createRow(i+u);
												lrow.add(rw);
											}
											u++;
											rw.createCell(k).setCellValue( count == 1 ? lastname : "count "+count );
											
											count = 0;
										}
									} else if( name.contains("CRISPR") || name.contains("Csh") || name.contains("Cse") || name.contains("Cmr") || name.contains("Crm") || name.contains("Cas") || name.contains("Csd") || name.contains("Csm") ) {
										if( count > 0 ) {
											if( u < lrow.size() ) rw = lrow.get(u);
											else {
												rw = sh.createRow(i+u);
												lrow.add(rw);
											}
											u++;
											rw.createCell(k).setCellValue( count == 1 ? lastname : "count "+count );
											
											count = 0;
										} else if( mcount > 0 ) {
											if( u < lrow.size() ) rw = lrow.get(u);
											else {
												rw = sh.createRow(i+u);
												lrow.add(rw);
											}
											u++;
											Cell cell = rw.createCell(k);
											cell.setCellValue( mcount+"-"+lastrep );
											CellStyle cs = repeatColor.get(lastrep);
											if( cs != null ) cell.setCellStyle( cs );
											
											mcount = 0;
										}
										
										if( u < lrow.size() ) rw = lrow.get(u);
										else {
											rw = sh.createRow(i+u);
											lrow.add(rw);
										}
										u++;
										
										//int v = name.indexOf('(');
										//if( v == -1 ) v = name.length();
										String newname = name; //.substring(0,v);
										/*newname = newname.replace("CRISPR-associated", "");
										newname = newname.replace("CRISPR", "");
										newname = newname.replace("protein", "");
										newname = newname.replace("family", "");
										newname = newname.replace("helicase", "");
										newname = newname.replace(",", "");*/
										newname = newname.trim();
										System.err.println( "blehname   " + newname );
										
										String yes = null;
										for( String h : hcell.keySet() ) {
											if( newname.contains(h) ) {
												yes = h;
												break;
											}
										}
										if( yes != null ) { //hcell.containsKey(newname) ) {
											Row r = hrow.get(spec);
											int c = hcell.get(yes);
											
											Contig ct = (Contig)seq;
											boolean plas = ct.isPlasmid();
											String plastr = plas ? "plas" : "np";
											
											Cell cell = r.createCell(c);
											cell.setCellValue( plastr );
											
											CellStyle cs = repeatColor.get(plastr);
											if( cs != null ) {
												cell.setCellStyle(cs);
											}
										}
										
										Cell cell = rw.createCell(k);
										cell.setCellValue( newname.length() == 0 ? name : newname );
										
										if( newname.contains("Cse") ) {
											String crispr = "Cse";
											
											onlycas = -1;
											CellStyle cs;
											if( csMap.containsKey("Cse") ) {
												cs = csMap.get("Cse");
											} else {
												cs = wb.createCellStyle();
												cs.setFillForegroundColor( IndexedColors.DARK_YELLOW.index );
												cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
												csMap.put("Cse", cs);
											}
											cell.setCellStyle( cs );
											
											if( typeRepeat.containsKey(crispr) ) {
												Map<String,Integer> mm = typeRepeat.get(crispr);
												for( String typ : lastrepeats.keySet() ) {
													if( mm.containsKey(typ) ) {
														mm.put(typ, lastrepeats.get(typ)+mm.get(typ));
													} else {
														mm.put(typ, lastrepeats.get(typ));
													}
												}
											} else {
												typeRepeat.put( crispr, new HashMap<String,Integer>(lastrepeats) );
											}
											lastrepeats.clear();
										} else if( newname.contains("Cas") ) {
											String crispr = "Cas";
											
											if( onlycas != -1 ) onlycas++;
											CellStyle cs;
											if( csMap.containsKey("Cas") ) {
												cs = csMap.get("Cas");
											} else {
												cs = wb.createCellStyle();
												cs.setFillForegroundColor( IndexedColors.DARK_RED.index );
												cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
												csMap.put("Cas", cs);
											}
											cell.setCellStyle( cs );
											
											if( typeRepeat.containsKey(crispr) ) {
												Map<String,Integer> mm = typeRepeat.get(crispr);
												for( String typ : lastrepeats.keySet() ) {
													if( mm.containsKey(typ) ) {
														mm.put(typ, lastrepeats.get(typ)+mm.get(typ));
													} else {
														mm.put(typ, lastrepeats.get(typ));
													}
												}
											} else {
												typeRepeat.put( crispr, new HashMap<String,Integer>(lastrepeats) );
											}
											//lastrepeats.clear();
										} else if( newname.contains("Csm") ) {
											String crispr = "Csm";
											onlycas = -1;
											CellStyle cs;
											if( csMap.containsKey("Csm") ) {
												cs = csMap.get("Csm");
											} else {
												cs = wb.createCellStyle();
												cs.setFillForegroundColor( IndexedColors.DARK_BLUE.index );
												cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
												csMap.put("Csm", cs);
											}
											cell.setCellStyle( cs );
											
											if( typeRepeat.containsKey(crispr) ) {
												Map<String,Integer> mm = typeRepeat.get(crispr);
												for( String typ : lastrepeats.keySet() ) {
													if( mm.containsKey(typ) ) {
														mm.put(typ, lastrepeats.get(typ)+mm.get(typ));
													} else {
														mm.put(typ, lastrepeats.get(typ));
													}
												}
											} else {
												typeRepeat.put( crispr, new HashMap<String,Integer>(lastrepeats) );
											}
											lastrepeats.clear();
										} else if( newname.contains("Cmr") || newname.contains("Crm") ) {
											String crispr = "Cmr";
											onlycas = -1;
											CellStyle cs;
											if( csMap.containsKey("Cmr") ) {
												cs = csMap.get("Cmr");
											} else {
												cs = wb.createCellStyle();
												cs.setFillForegroundColor( IndexedColors.DARK_GREEN.index );
												cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
												csMap.put("Cmr", cs);
											}
											cell.setCellStyle( cs );
											
											if( typeRepeat.containsKey(crispr) ) {
												Map<String,Integer> mm = typeRepeat.get(crispr);
												for( String typ : lastrepeats.keySet() ) {
													if( mm.containsKey(typ) ) {
														mm.put(typ, lastrepeats.get(typ)+mm.get(typ));
													} else {
														mm.put(typ, lastrepeats.get(typ));
													}
												}
											} else {
												typeRepeat.put( crispr, new HashMap<String,Integer>(lastrepeats) );
											}
											lastrepeats.clear();
										} else if( newname.contains("Csd") ) {
											String crispr = "Csd";
											onlycas = -1;
											
											CellStyle cs;
											if( csMap.containsKey("Csd") ) {
												cs = csMap.get("Csd");
											} else {
												cs = wb.createCellStyle();
												cs.setFillForegroundColor( IndexedColors.DARK_TEAL.index );
												cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
												csMap.put("Csd", cs);
											}
											cell.setCellStyle( cs );
											
											if( typeRepeat.containsKey(crispr) ) {
												Map<String,Integer> mm = typeRepeat.get(crispr);
												for( String typ : lastrepeats.keySet() ) {
													if( mm.containsKey(typ) ) {
														mm.put(typ, lastrepeats.get(typ)+mm.get(typ));
													} else {
														mm.put(typ, lastrepeats.get(typ));
													}
												}
											} else {
												typeRepeat.put( crispr, new HashMap<String,Integer>(lastrepeats) );
											}
											lastrepeats.clear();
										} else if( newname.contains("Csh") ) {
											String crispr = "Csh";
											onlycas = -1;
											CellStyle cs;
											if( csMap.containsKey("Csh") ) {
												cs = csMap.get("Csh");
											} else {
												cs = wb.createCellStyle();
												cs.setFillForegroundColor( IndexedColors.GOLD.index );
												cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
												csMap.put("Csh", cs);
											}
											cell.setCellStyle( cs );
											
											if( typeRepeat.containsKey(crispr) ) {
												Map<String,Integer> mm = typeRepeat.get(crispr);
												for( String typ : lastrepeats.keySet() ) {
													if( mm.containsKey(typ) ) {
														mm.put(typ, lastrepeats.get(typ)+mm.get(typ));
													} else {
														mm.put(typ, lastrepeats.get(typ));
													}
												}
											} else {
												typeRepeat.put( crispr, new HashMap<String,Integer>(lastrepeats) );
											}
											lastrepeats.clear();
										}
									} else {
										if( count > 10 ) {
											onlycas = -1;
											lastrepeats.clear();
										}
										if( mcount > 0 ) {
											if( u < lrow.size() ) rw = lrow.get(u);
											else {
												rw = sh.createRow(i+u);
												lrow.add(rw);
											}
											u++;
											Cell cell = rw.createCell(k);
											cell.setCellValue( mcount+"-"+lastrep );
											CellStyle cs = repeatColor.get(lastrep);
											if( cs != null ) cell.setCellStyle( cs );
											
											mcount = 0;
										}
										count++;
									}
									lastname = name;
								}
								
								if( count > 0 ) {
									if( u < lrow.size() ) rw = lrow.get(u);
									else {
										rw = sh.createRow(i+u);
										lrow.add(rw);
									}
									u++;
									rw.createCell(k).setCellValue( count == 1 ? lastname : "count "+count );
								} else if( mcount > 0 ) {
									if( u < lrow.size() ) rw = lrow.get(u);
									else {
										rw = sh.createRow(i+u);
										lrow.add(rw);
									}
									u++;
									Cell cell = rw.createCell(k);
									cell.setCellValue( mcount+"-"+lastrep );
									CellStyle cs = repeatColor.get(lastrep);
									if( cs != null ) cell.setCellStyle( cs );
								}
							}
							u++;
						}
					}
					k++;
					
					//break;
				}
				
				int j = 10;
				for( String crispr : typeRepeat.keySet() ) {
					int l = 0;
					nullrow.get(l).createCell(j).setCellValue(crispr);
					
					Map<String,Integer> msti = typeRepeat.get(crispr);
					for( String rep : msti.keySet() ) {
						int count = msti.get(rep);
						l++;
						if( l < nullrow.size() ) {
							Cell cell = nullrow.get(l).createCell(j);
							cell.setCellValue(count + "-" + rep);
							CellStyle cs = repeatColor.get(rep);
							if (cs != null) cell.setCellStyle(cs);
						}
					}
					
					j++;
				}
				
				
				try {
					String userhome = System.getProperty("user.home");
					Path p = userhome != null && userhome.length() > 0 ? Paths.get(userhome).resolve("crispr.xlsx") : Paths.get("crispr.xlsx");
					wb.write( Files.newOutputStream(p) );
					Desktop.getDesktop().open(p.toFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				k = 0;
				for( String rep : tann.keySet() ) {
					System.err.println(">"+k);
					System.err.println( rep );
					k++;
				}
			}
		});
		apopup.addSeparator();
		apopup.add( new AbstractAction("Export Annotation") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exportAnnotationFasta( atable, serifier.lann );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		apopup.add( new AbstractAction("Clear sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = atable.getSelectedRows();
				for( int r : rr ) {
					int i = atable.convertRowIndexToModel( r );
					Annotation a = serifier.lann.get( i );
					if( a.getSeq() == null ) {
						for( Sequence seq : serifier.lseq ) {
							int start = Math.max( seq.getRealStart(), a.getCoordStart() );
							int stop = Math.min( seq.getRealStop(), a.getCoordEnd() );
							for( int k = start; k < stop; k++ ) {
								seq.clearCharAt(k);
							}
						}
					}
				}
			}
		});
		apopup.add( new AbstractAction("Retain sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = atable.getSelectedRows();
				for( Sequence seq : serifier.lseq ) {
					for( int i = seq.getRealStart(); i < seq.getRealStop(); i++ ) {
						boolean contained = false;
						for( int r : rr ) {
							int k = atable.convertRowIndexToModel( r );
							Annotation a = serifier.lann.get( k );
							if( i >= a.getCoordStart() && i <= a.getCoordEnd() ) {
								contained = true;
								break;
							}
						}
						if( !contained ) {
							seq.clearCharAt(i);
						}
					}
				}
			}
		});
		
		atable.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return serifier != null ? serifier.lann.size() : 0;
			}

			@Override
			public int getColumnCount() {
				return 8;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Name";
				else if( columnIndex == 1 ) return "Contig";
				else if( columnIndex == 2 ) return "Type";
				else if( columnIndex == 3 ) return "Designation";
				else if( columnIndex == 4 ) return "Group";
				else if( columnIndex == 5 ) return "Start";
				else if( columnIndex == 6 ) return "Stop";
				else if( columnIndex == 7 ) return "Length";
				else return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex > 4 ) return Integer.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Annotation ann = serifier.lann.get( rowIndex );
				if( ann != null ) {
					if( columnIndex == 0 ) {
						if( ann instanceof Tegeval ) {
							Tegeval tv = (Tegeval)ann;
							GeneGroup gg = tv.getGene().getGeneGroup();
							if( gg != null ) return gg.getName();
						}
						return ann.getName();
					}
					else if( columnIndex == 1 ) return ann.getSeq() != null ? ann.getSeq().getName() : "";
					else if( columnIndex == 2 ) return ann.type+"_"+ann.ori;
					else if( columnIndex == 3 ) return ann.designation;
					else if( columnIndex == 4 ) return ann.getGroup();
					else if( columnIndex == 5 ) return ann.start;
					else if( columnIndex == 6 ) return ann.stop;
					else if( columnIndex == 7 ) return ann.stop-ann.start;
				}
				return "";
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
						Annotation a = serifier.lann.get( i );
						
						i = serifier.lseq.indexOf( a.getSeq() );
						int m = 0;
						if( i != -1 ) {
							m = table.convertRowIndexToView( i );
							if( m >= 0 && m < table.getRowCount() ) {
								System.err.println( m );
								table.setRowSelectionInterval(m, m);
							}
						}
						
						Rectangle cellrect = table.getCellRect(m, 0, true);
						Rectangle rect = c.getVisibleRect();
						if( rect.x == (int)((a.getCoordStart()-serifier.getMin())*c.cw) ) {
							rect.x = (int)((a.getCoordEnd()-serifier.getMin())*c.cw-rect.width);
						} else {
							rect.x = (int)((a.getCoordStart()-serifier.getMin())*c.cw);
						}
						rect.y = cellrect.y;
						
						c.scrollRectToVisible( rect );
						
						if( i == -1 ) {
							c.selectedRect.x = a.getCoordStart()-serifier.getMin();
							c.selectedRect.width = a.getLength();
							c.selectedRect.y = 0;
							c.selectedRect.height = table.getRowCount();
							c.repaint();
						}
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
					Set<Annotation>	delset = new HashSet<>();
					int[] rr = atable.getSelectedRows();
					for( int r : rr ) {
						int i = atable.convertRowIndexToModel(r);
						Annotation ann = serifier.lann.get(i);
						if( ann.getSeq() != null ) ann.getSeq().removeAnnotation( ann );
						delset.add( ann );
					}
					serifier.lann.removeAll( delset );
					
					updateView();
				}
			}
		});
		
		if (cnt instanceof JFrame) {
			JFrame frame = (JFrame)cnt;
			frame.setResizable(true);
			
			JMenuBar mb = new JMenuBar();
			mb.add( file );
			mb.add( edit );
			mb.add( view );
			mb.add( anno );
			mb.add( name );
			mb.add( group );
			mb.add( phylogeny );
			frame.setJMenuBar( mb );
		}
		
		mainsplit.setBackground( Color.white );
		ascroll.getViewport().setBackground( Color.white );
		
		cnt.setLayout( new BorderLayout() );
		cnt.add( mainsplit );
		status.setPreferredSize( new Dimension(100, 20) );
		cnt.add( status, BorderLayout.SOUTH );
	}
	
	JLabel	status = new JLabel();
	public void setAnnotationTableTransferhandler( JScrollPane atablescroll ) {
		try {
			final DataFlavor df = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws IOException {
					if( arg0.equals( df ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<Annotation>	selann = new ArrayList<>( rr.length );
						for( int r : rr ) {
							int i = atable.convertRowIndexToModel(r);
							selann.add( serifier.lann.get(i) );
						}
						return selann;
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
					currentRowSelection = atable.getSelectedRows();
					
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
									Sequence s = new Sequence( f.getName(), serifier.mseq );
									s.append( abi.getSequence() );
									serifier.lseq.add( s );
									
									if( s.length() > serifier.getMax() ) serifier.setMax( s.length() );
									
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
												for( String seqname : serifier.mseq.keySet() ) {
													int bil = seqname.indexOf(' ');
													if( query.contains( seqname.substring(0, bil) ) ) {
														seq = serifier.mseq.get(seqname);
														break;
													}
												}
												
												if( seq != null ) {
													Annotation a = new Annotation( seq, name, Color.red, serifier.mann );
													a.desc = a.desc == null ? new StringBuilder( query ) : a.desc.append( query );
													String[]	mylla = query.split("#");
													if( mylla[3].trim().equals("-1") ) a.color = Color.green;
													int start = Integer.parseInt( mylla[1].trim() );
													int stop = Integer.parseInt( mylla[2].trim() );
													a.start = start;
													a.stop = stop;
													serifier.lann.add( a );
													serifier.mann.put( name, a );
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
									for( Sequence seq : serifier.lseq ) {
										if( seq.getAnnotations() != null ) Collections.sort( seq.getAnnotations() );
									}
								} else {
									Annotation a = null;
									BufferedReader	br = new BufferedReader( new FileReader( f ) );
									String line = br.readLine();
									while( line != null ) {
										if( line.startsWith(">") ) {
											String name = line.substring(1);
											
											Sequence theseq = null;
											for( String seqname : serifier.mseq.keySet() ) {
												if( name.contains( seqname.split(" ")[0] ) ) {
													theseq = serifier.mseq.get( seqname );
													break;
												}
											}
											
											if(  theseq != null ) {
												a = new Annotation( theseq, name, Color.red, serifier.mann );
												String[]	mylla = name.split("#");
												if( mylla[3].trim().equals("-1") ) a.color = Color.green;
												int start = Integer.parseInt( mylla[1].trim() );
												int stop = Integer.parseInt( mylla[2].trim() );
												a.start = start;
												a.stop = stop;
												serifier.lann.add( a );
												serifier.mann.put( name, a );
											}
										} else if( a != null ) {
											a.appendDesc( line );
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
							ArrayList<Annotation>	lann = (ArrayList<Annotation>)obj;
							/*ArrayList<Sequence> newlist = new ArrayList<Sequence>( serifier.lseq.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( serifier.lseq.get(i) );
							}
							serifier.lseq.clear();
							serifier.lseq = newlist;*/
							
							Point p = support.getDropLocation().getDropPoint();
							int k = atable.rowAtPoint( p );
							
							serifier.lann.removeAll( lann );
							
							//for( Sequence seq : serifier)
							/*for( Annotation ann : serifier.lann ) {
								Sequence seq = ann.getContig();
								List<Sequence>	lseq = serifier.gseq.get( seq.getName() );
							}*/
							
							for( Annotation a : lann ) {
								serifier.lann.add(k++, a);
							}
							
							int current = 0;
							//Sequence prev;
							for( Annotation ann : serifier.lann ) {
								Sequence seq = ann.getContig();
								int val = seq.getStart();
								seq.setStart( current );
								List<Sequence>	lseq = serifier.gseq.get( seq.getName() );
								for( Sequence subseq : lseq ) {
									if( subseq != seq ) subseq.setStart( subseq.getStart() + (current-val) );
								}
								
								current = seq.getEnd();
							}
							
							/*TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)atable.getRowSorter();
							trs.setSortKeys( null );*/
							
							atable.tableChanged( new TableModelEvent(atable.getModel()) );
							c.repaint();
							
							return true;
						}
					} catch (UnsupportedFlavorException | IOException e) {
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
	
	public static void main(String[] args) {
		JFrame	frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize(800, 600);
		JavaFasta	jf = new JavaFasta();
		jf.initGui( frame );
		
		frame.setVisible( true );
	}
	
	public Rectangle getSelectedRect() {
		return c == null ? null : c.selectedRect;
	}
	
	public String getSelectedSequence() {
		String str = null;;
		Rectangle r = getSelectedRect();
		int i = table.convertRowIndexToModel( r.y );
		if( i >= 0 && i < serifier.lseq.size() ) {
			Sequence s = serifier.lseq.get(i);
			int start = Math.max(s.getStart(), r.x+serifier.getMin()) - s.getStart();
			int end = Math.min(s.getEnd(), r.x+r.width+serifier.getMin()) - s.getStart();
			if( end > start ) str = s.getSequence().substring(start, end);
		}
		return str;
	}
	
	public class Finder extends SimpleFileVisitor<Path> {
	    private final PathMatcher matcher;
	    private int numMatches = 0;
	
	    public Finder(String pattern) {
	        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	    }
	
	    // Compares the glob pattern against
	    // the file or directory name.
	    void find(Path file) {
	        Path name = file.getFileName();
	        if (name != null && matcher.matches(name)) {
	        	try {
					JavaFasta.this.addAbiSequence( name.toString(), file );
				} catch (IOException e) {
					e.printStackTrace();
				}
	            numMatches++;
	            System.out.println(file);
	        }
	    }
	
	    // Prints the total number of
	    // matches to standard out.
	    void done() {
	        System.out.println("Matched: " + numMatches);
	    }
	
	    // Invoke the pattern matching
	    // method on each file.
	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
	        find(file);
	        return FileVisitResult.CONTINUE;
	    }
	
	    // Invoke the pattern matching
	    // method on each directory.
	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
	        find(dir);
	        return FileVisitResult.CONTINUE;
	    }
	
	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc) {
	        System.err.println(exc);
	        return FileVisitResult.CONTINUE;
	    }
	}
	
	class Repeat extends Annotation {
		public int length;
	};
}
