package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.jnlp.ClipboardService;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultRowSorter;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import netscape.javascript.JSObject;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.simmi.shared.Annotation;
import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;
import org.simmi.shared.TreeUtil.NodeSet;
import org.simmi.unsigned.JavaFasta;

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

public class DataTable extends JApplet implements ClipboardOwner {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static void updateLof() {
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
	}
	
	Map<String,Sequence>	seqcache = new HashMap<String,Sequence>();
	String[] specs = {"antranikianii","aquaticus","arciformis","brockianus","eggertsoni","filiformis","igniterrae","islandicus","kawarayensis","oshimai","scotoductus","thermophilus","yunnanensis","rehai","composti","unknownchile"};
	Map<String,String>	specColors = new HashMap<String,String>();
	Map<String,String> namesMap = new HashMap<String,String>();
	//tu.softReplaceNames( n, namesMap );*/
	
	public void updateTable( String tabmap ) {
		try {
			JSONObject jsono = new JSONObject( tabmap );
			Iterator<String> keys = jsono.keys();
			while( keys.hasNext() ) {
				String key = keys.next();
				if( tablemap.containsKey(key) ) {
					Object[] strs = tablemap.get( key );
					JSONObject jo = jsono.getJSONObject(key);
					strs[6] = jo.getString("country");
					String vb = (String)jo.getString("valid");
					if( vb != null ) strs[20] = Boolean.parseBoolean( vb );
				}
			}
			table.tableChanged( new TableModelEvent(table.getModel()) );
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String makeCopyString() {
		StringBuilder sb = new StringBuilder();
            
        int[] rr = table.getSelectedRows();
        int[] cc = table.getSelectedColumns();
        
        for( int ii : rr ) {
            for( int jj = 0; jj < table.getColumnCount(); jj++ ) {
            	Object val = table.getValueAt(ii,jj);
                //if( val != null && val instanceof Float ) sb.append( "\t"+Float.toString( (Float)val ) );
                //else sb.append( "\t" );
            	if( jj == 0 ) sb.append( val.toString() );
            	else {
            		if( val == null ) sb.append( "\t" );
            		else sb.append( "\t"+val.toString() );
            	}
            }
            sb.append( "\n" );
        }
        return sb.toString();
	}
	
	public void copyData(Component source) {
        TableModel model = table.getModel();
 
        String s = makeCopyString();
        if (s==null || s.trim().length()==0) {
            JOptionPane.showMessageDialog(this, "There is no data selected!");
        } else {
        	if( clipboardService != null ) {
        		StringSelection selection = new StringSelection(s);
            	clipboardService.setContents( selection );
        	} else {
        		StringSelection stringSelection = new StringSelection( s );
        		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	    clipboard.setContents( stringSelection, this );
        	}
        }
        
        if (grabFocus) {
            source.requestFocus();
        }
    }
	
	public void replaceTreeText( String tree ) {
		int seqi = 0;
		for( Sequence seq : currentserifier.lseq ) {
			String nm = "";
			String sind = Integer.toString( seqi++ );
			int m = 0;
			while( m < 10-sind.length() ) {
				nm += "0";
				m++;
			}
			nm += sind;
			tree = tree.replace( nm, seq.getName() );
		}
		JSObject win = JSObject.getWindow( DataTable.this );
		win.call( "showTree", new Object[] { tree } );
	}
	
    class CopyAction extends AbstractAction {
        public CopyAction(String text) {
            super(text);
            //putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
        }
 
        public void actionPerformed(ActionEvent e) {
            copyData((Component)e.getSource());
        }
    }

    JTable						table;
    Map<String,Object[]>		tablemap;
    private ClipboardService 	clipboardService;
    private boolean			grabFocus = false;
    JCheckBoxMenuItem 			cbmi = new JCheckBoxMenuItem("Collapse tree");
    final Map<String,String>	nameaccmap = new HashMap<String,String>();
    final List<Object[]>		rowList = new ArrayList<Object[]>();
    
    public static String[] csvSplit( String line ) {
    	List<String> splitlist = new ArrayList<String>();
		
		int first = 0;
		int last = line.indexOf('"');
		while( last != -1 ) {
			if( last > first ) {
				String sub = line.substring(first, last-1);
    		//if( sub.length() > 0 ) {
    			int uno = 0;
    			int duo = sub.indexOf(',');
    			while( duo != -1 ) {
    				splitlist.add( sub.substring(uno, duo) );
    				uno = duo+1;
    				duo = sub.indexOf(',', uno);
    			}
    			splitlist.add( sub.substring(uno) );
    		}
    		first = last+1;
    		last = line.indexOf('"', first);
    		
    		if( last != -1 ) {
    			String sub = line.substring(first, last);
    			splitlist.add( sub );
    		
    			first = last+2;
    			last = line.indexOf('"', first);
    		}
		}
		if( first != -1 && first < line.length() ) {
    		String sub = line.substring(first);
    		if( sub.length() > 0 ) {
    			int uno = 0;
    			int duo = sub.indexOf(',');
    			while( duo != -1 ) {
    				splitlist.add( sub.substring(uno, duo) );
    				uno = duo+1;
    				duo = sub.indexOf(',', uno);
    			}
    			splitlist.add( sub.substring(uno) );
    		}
		} else System.err.println("first is not");
		return splitlist.toArray( new String[0] );
    }
    
    public void loadData( String data ) {
    	String[] lines = data.split("\n");
    	List<String> splitlist = new ArrayList<String>();
    	
    	try {
	    	for( int i = 1; i < lines.length; i++ ) {
	    		 //lines[i].split(",");
	    		String[] split = csvSplit( lines[i] );
	    		if( split.length > 8 ) {
		    		nameaccmap.put(split[0], split[1]);
					Object[] strs = new Object[ 23 ];
					
					int k = 0;
					for( k = 0; k < split.length; k++ ) {
						/*if( k < 3 ) {
							strs[k] = split[(k+2)%3];
						} else */
						if( k == 4 || k == 5 ) {
							String istr = split[k];
							if( istr != null && istr.length() > 0 ) {
								try {
									strs[k] = Integer.parseInt( istr );
								} catch( Exception e ) {
									e.printStackTrace();
								}
							} else {
								strs[k] = null;
							}
						} else if( k == 18 || k == 19 ) {
							String dstr = split[k];
							if( dstr != null && dstr.length() > 0 ) {
								try {
									strs[k] = Double.parseDouble( dstr );
								} catch( Exception e ) {
									e.printStackTrace();
								}
							} else {
								strs[k] = null;
							}
						} else if( k == 21 ) {
							strs[k] = (split[k] != null && (split[k].equalsIgnoreCase("true") || split[k].equalsIgnoreCase("false")) ? Boolean.parseBoolean( split[k] ) : true);
						} else {
							strs[k] = split[k];
						}
					}
					
					if( k == 8 ) strs[k++] = "";
					if( k == 9 ) strs[k++] = "";
					if( k == 10 ) strs[k++] = "";
					if( k == 11 ) strs[k++] = "";
					if( k == 12 ) strs[k++] = "";
					if( k == 13 ) strs[k++] = "";
					if( k == 14 ) strs[k++] = "";
					if( k == 15 ) strs[k++] = "";
					if( k == 16 ) strs[k++] = "";
					if( k == 17 ) strs[k++] = "";
					if( k == 18 ) strs[k++] = null;
					if( k == 19 ) strs[k++] = null;
					if( k == 20 ) strs[k++] = "";
					//if( k == 21 ) strs[k++] = "";
					strs[k] = true;
					
					//Arrays.copyOfRange(split, 1, split.length );
					rowList.add( strs );
					tablemap.put((String)strs[1], strs);
	    		} else {
	    			System.err.println("ermimeri " + split.length );
	    		}
	    	}
	    	table.tableChanged( new TableModelEvent( table.getModel() ) );
    	} catch( Exception e ) {
    		e.printStackTrace();
    	}
    }
    
    private static GoogleService service;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query";
	private static final String GEOCODE_SERVICE_URL = "http://maps.googleapis.com/maps/api/geocode";
	private static final String oldtableid = "1QbELXQViIAszNyg_2NHOO9XcnN_kvaG1TLedqDc";
	private static final String old2tableid = "1dmyUhlXVEoWHrT-rfAaAHl3vl3lCUvQy3nkuNUw";
	private static final String tableid = "140P0Wj3l6bciT0ihiGjvP97M_CClVhvPHBkFOek";
	
	public String getThermusFusion() throws IOException {
		String baseurl = "https://www.googleapis.com/fusiontables/v1/query";
		String query = URLEncoder.encode( "select * from "+tableid, "UTF-8" );
		URL url = new URL( baseurl + "?sql="+query + "&alt=csv&key=AIzaSyCxBoPVCLiktcFM6WGAa1C6TQOhLk7MZII" );
		
		//System.setProperty(GoogleGDataRequest.DISABLE_COOKIE_HANDLER_PROPERTY, "true");
		/*if( service == null ) {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			/*try {
				service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}*
		}
		
		if( service != null ) {
			try {
				String ret = run("select * from "+tableid, true);
				return ret;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}*/
		
		return getResultsText( url.openStream() );
	}
	
	public static String run(String query, boolean isUsingEncId) throws IOException, ServiceException {
		   String lowercaseQuery = query.toLowerCase();
		   String encodedQuery = URLEncoder.encode(query, "UTF-8");
		  
		   GDataRequest request;
		   // If the query is a select, describe, or show query, run a GET request.
		   if (lowercaseQuery.startsWith("select") ||
		       lowercaseQuery.startsWith("describe") ||
		       lowercaseQuery.startsWith("show")) {
		     URL url = new URL(SERVICE_URL + "?sql=" + encodedQuery + "&encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.QUERY, url,
		         ContentType.TEXT_PLAIN);
		   } else {
		     // Otherwise, run a POST request.
		     URL url = new URL(SERVICE_URL + "?encid=" + isUsingEncId);
		     request = service.getRequestFactory().getRequest(RequestType.INSERT, url,
		         new ContentType("application/x-www-form-urlencoded"));
		     OutputStreamWriter writer = new OutputStreamWriter(request.getRequestStream());
		     writer.append("sql=" + encodedQuery);
		     writer.flush();
		   }

		   request.execute();

		   return getResultsText(request);
	}
	
	private static String getResultsText( InputStream is ) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader( is );
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		StringBuilder sb = new StringBuilder();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			
			line = bufferedStreamReader.readLine();
		}
		
		return sb.toString();
	}
	
	private static String getResultsText(GDataRequest request) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(request.getResponseStream());
		BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
		
		StringBuilder sb = new StringBuilder();
		String line = bufferedStreamReader.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			
			line = bufferedStreamReader.readLine();
		}
		
		return sb.toString();
	}
	
	public static void updateFilter(JTable table, RowFilter filter) {
		DefaultRowSorter<TableModel, Integer> rowsorter = (DefaultRowSorter<TableModel, Integer>)table.getRowSorter();
		rowsorter.setRowFilter(filter);
	}
	
	public void conservedSpecies( JavaFasta jf, boolean variant ) {
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		JCheckBox accession = new JCheckBox("Accession");
		Object[] params = new Object[] {country, source, accession};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				include.add( (String)val[1] );
			}
		}
		
		List<Sequence> contset = new ArrayList<Sequence>();
		Sequence	seq = null;
		int nseq = 0;
		
		Map<String,Collection<Sequence>>	specMap = new HashMap<String,Collection<Sequence>>();
		InputStream is = DataTable.this.getClass().getResourceAsStream("thermus16Ssilva115.fasta"); //"/thermaceae_16S_aligned.fasta");
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			String inc = null;
			String line = br.readLine();
			while( line != null ) {
				/*if( line.startsWith(">") ) {
					int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);
					if( include.contains(name) ) {
						Object[] obj = tablemap.get(acc);
						
						inc = true;
						String fname = ">";
						if( accession.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						if( country.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[11];
							else fname += "_"+obj[11];
						} 
						if( source.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						
						if( fname.length() > 1 ) {
							sb.append(">"+fname+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( line.length() > 100 ) {
						for( int i = 0; i < line.length(); i+= 70 ) {
							sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
						}
					} else sb.append( line+"\n" );
				}*/
				
				if( line.startsWith(">") ) {
					if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}
					
					inc = null;
					for( String str : include ) {
						if( line.contains( str ) ) {
							inc = str;
							break;
						}
					}
					
					if( inc != null ) {
						Object[] obj = tablemap.get(inc);
						
						String fname = "";
						String spec = (String)obj[3];
						int iv = spec.indexOf('_');
						if( iv == -1 ) {
							iv = spec.indexOf("16S");
						}
						if( iv != -1 ) spec = spec.substring(0, iv).trim();
						if( fname.length() == 0 ) fname += spec;
						else fname += "_"+spec;
						
						if( country.isSelected() ) {
							String cntr = (String)obj[6];
							int idx = cntr.indexOf('(');
							if( idx > 0 ) {
								int idx2 = cntr.indexOf(')', idx+1);
								if( idx2 == -1 ) idx2 = cntr.length()-1;
								cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
							}
							if( fname.length() == 0 ) fname += cntr;
							else fname += "_"+cntr;
						} 
						if( source.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[7];
							else fname += "_"+obj[7];
						}
						if( accession.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						
						String cont;
						if( fname.length() > 1 ) {
							cont = fname;
						} else cont = line.substring(1);
					//if( rr.length == 1 ) cont = line.replace( ">", "" );
					//else cont = line.replace( ">", seqs.getName()+"_" );
						seq = new Sequence( inc, cont, jf.getSerifier().mseq );
						
						Collection<Sequence> specset;
						if( specMap.containsKey( spec ) ) {
							specset = specMap.get( spec );
						} else {
							specset = new HashSet<Sequence>();
							specMap.put( spec, specset );
						}
						specset.add( seq );
					//dna.append( line.replace( ">", ">"+seqs.getName()+"_" )+"\n" );
						nseq++;
					}
				} else if( inc != null ) {
					seq.append( line.replace(" ", "") );
				}
				line = br.readLine();
			}
			br.close();
			if( inc != null && seq != null ) {
				//Sequence seq = jf.new Sequence(cont, dna);
				contset.add(seq);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (Sequence contig : contset) {
			jf.getSerifier().addSequence(contig);
			if (contig.getAnnotations() != null)
				Collections.sort(contig.getAnnotations());
		}
		
		if( variant ) {
			jf.clearConservedSites( specMap );
		} else {
			for( String spec : specMap.keySet() ) {
				Collection<Sequence> specset = specMap.get(spec);
				jf.clearSites( specset, false );
			}
			jf.clearSitesWithGaps( contset );
		}
		
		jf.updateView();
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned ) {
		loadAligned( jf, aligned, null );
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned, Object[] extra ) {
		nameSelection( extra );
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				String cacheval = (String)val[1];
				if( seqcache.containsKey( cacheval ) ) {
					Sequence seq = seqcache.get( cacheval );
					
					Object[] obj = tablemap.get( seq.id );
					if( obj != null ) {
						String fname = getFastaName( names, metas, obj );
						String cont = (Integer)obj[4] >= 900 ? fname : "*"+fname;
						cont = cont.replace(": ", "-").replace(':', '-').replace(",", "");
						seq.setName( cont );
					}
					
					jf.getSerifier().addSequence( seq );
				} else include.add( cacheval );
			}
		}
		if( include.size() > 0 ) loadAligned( jf, aligned, include, names, metas );
		else if( runnable != null ) {
			runnable.run();
			runnable = null;
		}
	}
	
	class NameSel {
		String 		name;
		Boolean		selected;
		
		public NameSel( String name ) {
			this.name = name;
		}
		
		public boolean isSelected() {
			return selected != null && selected;
		}
	}
	
	public int selectionOfEach() {
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		JSpinner spin = new JSpinner( new SpinnerNumberModel(5, 1, 10, 1) );
		JTable	table = nameSelectionComponent( names );
		
		comp.add( table );
		comp.add( spin, BorderLayout.NORTH );
		
		JOptionPane.showMessageDialog( DataTable.this, comp, "Select names and number", JOptionPane.PLAIN_MESSAGE );
		
		return (Integer)spin.getValue();
	}
	
	List<NameSel>		names = new ArrayList<NameSel>();
	List<NameSel>		metas = new ArrayList<NameSel>();
	int[] 				currentRowSelection;
	public void nameSelection( Object[] extra ) {
		JTable table = nameSelectionComponent( names );
		JTable mtable = nameSelectionComponent( metas );
		if( extra == null ) extra = new Object[] {table, mtable};
		else {
			Object[] oldextra = extra;
			extra = new Object[ extra.length+2 ];
			extra[0] = table;
			extra[1] = mtable;
			for( int i = 0; i < oldextra.length; i++ ) {
				extra[i+2] = oldextra[ i ];
			}
		}
		JOptionPane.showMessageDialog( DataTable.this, extra, "Select names", JOptionPane.PLAIN_MESSAGE );
	}
	
	public JTable nameSelectionComponent( final List<NameSel> names ) {
		final JTable table = new JTable();
		table.setDragEnabled( true );
		String[] nlist = {"Species", "Pubmed", "Country", "Source", "Accession", "Color", "Country color"};
		names.clear();
		for( String name : nlist ) {
			names.add( new NameSel( name ) );
		}
		
		try {
			final DataFlavor ndf = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType );
			final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
			final String charset = df.getParameter("charset");
			final Transferable transferable = new Transferable() {
				@Override
				public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {					
					if( arg0.equals( ndf ) ) {
						int[] rr = currentRowSelection; //table.getSelectedRows();
						List<NameSel>	selseq = new ArrayList<NameSel>( rr.length );
						for( int r : rr ) {
							int i = table.convertRowIndexToModel(r);
							selseq.add( names.get(i) );
						}
						return selseq;
					}
					return null;
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
						if( support.isDataFlavorSupported( ndf ) ) {						
							Object obj = support.getTransferable().getTransferData( ndf );
							ArrayList<NameSel>	seqs = (ArrayList<NameSel>)obj;
							
							ArrayList<NameSel> newlist = new ArrayList<NameSel>( names.size() );
							for( int r = 0; r < table.getRowCount(); r++ ) {
								int i = table.convertRowIndexToModel(r);
								newlist.add( names.get(i) );
							}
							names.clear();
							if( names == DataTable.this.names ) DataTable.this.names = newlist;
							else DataTable.this.metas = newlist;
							
							Point p = support.getDropLocation().getDropPoint();
							int k = table.rowAtPoint( p );
							
							names.removeAll( seqs );
							for( NameSel s : seqs ) {
								names.add(k++, s);
							}
							
							TableRowSorter<TableModel>	trs = (TableRowSorter<TableModel>)table.getRowSorter();
							trs.setSortKeys( null );
							
							table.tableChanged( new TableModelEvent(table.getModel()) );
							
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
			table.setTransferHandler( th );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		table.setModel( new TableModel() {
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				NameSel ns = names.get( rowIndex );
				ns.selected = (Boolean)aValue;
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 0 ) return true;
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				NameSel ns = names.get(rowIndex);
				if( columnIndex == 0 ) return ns.selected;
				return ns.name;
			}
			
			@Override
			public int getRowCount() {
				return names.size();
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return Boolean.class;
				return String.class;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {}
		});
		return table;
	}
	
	public void loadAligned( JavaFasta jf, boolean aligned, Set<String> iset, List<NameSel> namesel, List<NameSel> metasel ) {
		/*JCheckBox species = new JCheckBox("Species");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source =s new JCheckBox("Source");
		JCheckBox accession = new JCheckBox("Accession");
		Object[] params = new Object[] {species, country, source, accession};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);*/
		
		boolean fail = false;
		try {
			JSObject win = JSObject.getWindow( DataTable.this );
			StringBuilder include = new StringBuilder();
			for( String is : iset ) {
				if( include.length() == 0 ) include.append( is );
				else include.append( ","+is );
			}
			this.ns = namesel;
			this.ms = metasel;
			String includes = include.toString();
			win.call( "fetchSeq", new Object[] { includes } );
		} catch( Exception e ) {
			fail = true;
		}
		
		if( fail ) {
			try {
				JSONObject jsono = new JSONObject();
				for( String is : iset ) {
					jsono.put(is, (Object)"");
				}
				loadSequences( jsono.toString(), aligned, namesel, metasel );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	List<NameSel> ns;
	List<NameSel> ms;
	public void loadSequences( String jsonstr ) throws JSONException {
		loadSequences( jsonstr, true, ns, ms );
		ns = null;
		ms = null;
	}
	
	public void console( String message ) {
		try {
			JSObject win = JSObject.getWindow( DataTable.this );
			JSObject con = (JSObject)win.getMember("console");
			con.call("log", new Object[] {message} );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void loadSequences( String jsonstr, boolean aligned, List<NameSel> namesel, List<NameSel> metasel ) throws JSONException {
		//List<NameSel> namesel = nameSelection( extra );
		
		List<Sequence> contset = new ArrayList<Sequence>();
		Sequence	seq = null;
		//int nseq = 0;
		
		JSONObject jsono = new JSONObject( jsonstr );
		Set<String> include = new HashSet<String>();
		Iterator it = jsono.keys();
		while( it.hasNext() ) {
			String n = it.next().toString();
			Object o = jsono.get( n );
			if( o == null || o.toString().length() <= 1 || o.toString().equalsIgnoreCase("null") ) {
				include.add( n );
			} else {
				Object[] obj = tablemap.get(n);
				String fname = getFastaName( namesel, metasel, obj );
				
				String cont = (Integer)obj[4] >= 900 ? fname : "*"+fname;
				cont = cont.replace(": ", "-").replace(':', '-').replace(",", "");
				
				contset.add( new Sequence( n, cont, new StringBuilder(o.toString()), currentserifier.mseq ) );
			}
		}
		
		InputStream is = DataTable.this.getClass().getResourceAsStream("/thermaceae_16S_aligned.fasta");
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			String inc = null;
			String line = br.readLine();
			while( line != null ) {
				/*if( line.startsWith(">") ) {
					int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);
					if( include.contains(name) ) {
						Object[] obj = tablemap.get(acc);
						
						inc = true;
						String fname = ">";
						if( accession.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[1];
							else fname += "_"+obj[1];
						} 
						if( country.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[11];
							else fname += "_"+obj[11];
						} 
						if( source.isSelected() ) {
							if( fname.length() == 1 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						
						if( fname.length() > 1 ) {
							sb.append(">"+fname+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( line.length() > 100 ) {
						for( int i = 0; i < line.length(); i+= 70 ) {
							sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
						}
					} else sb.append( line+"\n" );
				}*/
				
				if( line.startsWith(">") ) {
					if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}
					
					inc = null;
					
					//Iterator it = jsono.keys();
					//while( it.hasNext() ) {
					for( String str : include ) {
						//String str = it.next().toString();
						if( line.contains( str ) ) {
							inc = str;
							break;
						}
					}
					
					if( inc != null ) {
						Object[] obj = tablemap.get(inc);
						String fname = getFastaName( namesel, metasel, obj );
						/*String fname = "";
						for( NameSel ns : namesel ) {
							if( ns.isSelected() ) {
								if( ns.name.equals("Species") ) {
									String spec = (String)obj[2];
									spec = spec.replace("Thermus ", "T.");
									
									int iv = spec.indexOf('_');
									int iv2 = spec.indexOf(' ');
									if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
									else iv = Math.min(iv, iv2);
									if( iv == -1 ) {
										iv = spec.indexOf("16S");
									}
									if( iv != -1 ) spec = spec.substring(0, iv).trim();
									if( fname.length() == 0 ) fname += spec;
									else fname += "_"+spec;
								} else if( ns.name.equals("country") ) {
									String cntr = (String)obj[11];
									int idx = cntr.indexOf('(');
									if( idx > 0 ) {
										int idx2 = cntr.indexOf(')', idx+1);
										if( idx2 == -1 ) idx2 = cntr.length()-1;
										cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
									}
									if( fname.length() == 0 ) fname += cntr;
									else fname += "_"+cntr;
								} else if( ns.name.equals("Source") ) {
									if( fname.length() == 0 ) fname += obj[12];
									else fname += "_"+obj[12];
								} else if( ns.name.equals("Accession") ) {
									String acc = (String)obj[1];
									acc = acc.replace("_", "");
									if( fname.length() == 0 ) fname += acc;
									else fname += "_"+acc;
								} else if( ns.name.equals("Pubmed") ) {
									String pubmed = (String)obj[6];
									if( fname.length() == 0 ) fname += pubmed;
									else fname += "_"+pubmed;
								}
							}
						}*/
						
						String cont;
						if( fname.length() > 1 ) {
							cont = (Integer)obj[4] >= 900 ? fname : "*"+fname;
						} else cont = line.substring(1);
						cont = cont.replace(": ", "-").replace(':', '-').replace(",", "");
						//if( rr.length == 1 ) cont = line.replace( ">", "" );
						//else cont = line.replace( ">", seqs.getName()+"_" );
						seq = new Sequence( inc, cont, currentserifier.mseq );
						//dna.append( line.replace( ">", ">"+seqs.getName()+"_" )+"\n" );
						//nseq++;
					}
				} else if( inc != null ) {
					String lrp = line.replace(" ", "");
					if( !aligned ) lrp = lrp.replace("-", "");
					seq.append( lrp );
				}
				line = br.readLine();
			}
			br.close();
			if( inc != null && seq != null ) {
				//Sequence seq = jf.new Sequence(cont, dna);
				contset.add(seq);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (Sequence contig : contset) {
			contig.checkLengths();
			currentserifier.addSequence(contig);
			if (contig.getAnnotations() != null)
				Collections.sort(contig.getAnnotations());
			
			seqcache.put( contig.getId(), contig );
		}
		
		if( runnable != null ) {
			runnable.run();
			runnable = null;
		}
		//currentjavafasta.updateView();
	}
	
	Runnable runnable = null;
	public void viewAligned( JavaFasta jf, boolean aligned ) {
		loadAligned( jf, aligned );
		Sequence cons = jf.getSerifier().getConsensus();
		jf.getSerifier().addAnnotation( new Annotation(null,"V1 - 16S rRNA",Color.blue,140,226,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V2 - 16S rRNA",Color.blue,276,438,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V3 - 16S rRNA",Color.blue,646,742,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V4 - 16S rRNA",Color.blue,865,1024,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V5 - 16S rRNA",Color.blue,1217,1309,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V6 - 16S rRNA",Color.blue,1469,1595,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V7 - 16S rRNA",Color.blue,1708,1804,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V8 - 16S rRNA",Color.blue,1894,1956,1, jf.getSerifier().mann ) );
		jf.getSerifier().addAnnotation( new Annotation(null,"V9 - 16S rRNA",Color.blue,2149,2209,1, jf.getSerifier().mann ) );
	}
	
	public void addSave( JFrame frame, final JavaFasta jf ) {
		frame.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {
				List<Sequence> lseq = jf.getEditedSequences();
				if( lseq.size() > 0 && JOptionPane.showConfirmDialog(DataTable.this, "Save", "Do you want to save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
					JSObject jso = JSObject.getWindow( DataTable.this );
					Map<String,String>	map = new HashMap<String,String>();
					for( Sequence s : lseq ) {
						map.put(s.getId(), s.getStringBuilder().toString());
					}
					JSONObject jsono = new JSONObject( map );
					String savestr = jsono.toString();
					jso.call("saveSeq2", new Object[] {savestr} );
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	public String getFastaName( List<NameSel> namesel, List<NameSel> metasel, Object[] obj ) {
		String name = getConstructedName( namesel, obj );
		String meta = getConstructedName( metasel, obj );
		return meta == null || meta.length() == 0 ? name : name + ";" + meta;
	}
	
	Map<String,String> 	ccol;
	Random				rand;
	public String getConstructedName( List<NameSel> namesel, Object[] obj ) {
		String fname = "";
		for( NameSel ns : namesel ) {
			if( ns.isSelected() ) {
				if( ns.name.equals("Species") ) {
					String spec = (String)obj[3];
					int		id = (Integer)obj[5];
					if( id >= 97 ) {
						spec = spec.replace("Thermus ", "T.");
						
						if( spec.contains("eggert") || spec.contains("yunnan") || spec.contains("rehai") || spec.contains("malas") || spec.contains("chile") ) {
							spec = '"'+spec+'"';
						}
					} else spec = '"'+"T.unkown"+'"';
					
					int iv = spec.indexOf('_');
					int iv2 = spec.indexOf(' ');
					if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
					else iv = Math.min(iv, iv2);
					if( iv == -1 ) {
						iv = spec.indexOf("16S");
					}
					if( iv != -1 ) spec = spec.substring(0, iv).trim();
					if( fname.length() == 0 ) fname += spec;
					else fname += "_"+spec;
				} else if( ns.name.equals("Country") ) {
					String cntr = (String)obj[6];
					for( String key : namesMap.keySet() ) {
						if( cntr != null && cntr.contains(key) ) {
							cntr = namesMap.get( key );
							break;
						}
					}
					
					cntr = countryShort( cntr );
					
					cntr = cntr.replace('_', ' ');
					int idx = cntr.indexOf('(');
					if( idx > 0 ) {
						int idx2 = cntr.indexOf(')', idx+1);
						if( idx2 == -1 ) idx2 = cntr.length()-1;
						cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
					}
					if( fname.length() == 0 ) fname += cntr;
					else fname += " "+cntr;
				} else if( ns.name.equals("Source") ) {
					if( fname.length() == 0 ) fname += obj[7];
					else fname += "_"+obj[7];
				} else if( ns.name.equals("Accession") ) {
					String acc = (String)obj[1];
					//acc = acc.replace("_", "");
					if( fname.length() == 0 ) fname += acc;
					else fname += "_"+acc;
				} else if( ns.name.equals("Pubmed") ) {
					String pubmed = (String)obj[9];
					if( fname.length() == 0 ) fname += pubmed;
					else fname += "_"+pubmed;
				} else if( ns.name.equals("Color") ) {
					String spec = (String)obj[3];
					for( String ss : specs ) {
						if( spec.contains( ss ) ) {
							fname += "[#"+specColors.get(ss)+"]";
							break;
						}
					}
					/*String col = (String)obj[18];
					//col = col.replace("_", "");
					//if( fname.length() == 0 ) fname += col;
					//else 
					if( colmap.containsKey(col) ) {
						fname += "["+colmap.get(col)+"]";	
					}*/
				} else if( ns.name.equals("Country color") ) {
					String country = (String)obj[6];
					
					for( String key : namesMap.keySet() ) {
						if( country != null && country.contains(key) ) {
							country = namesMap.get( key );
							break;
						}
					}
					
					country = countryShort( country );
					
					if( country != null && country.length() > 0 ) {
						String color;
						if( ccol == null ) ccol = new HashMap<String,String>();
						if( rand == null ) rand = new Random();
						if( ccol.containsKey(country) ) {
							color = ccol.get(country);
						} else {
							color = "[#"+Integer.toString(128+rand.nextInt(128), 16)+""+Integer.toString(128+rand.nextInt(128), 16)+""+Integer.toString(128+rand.nextInt(128), 16)+"]{1.0 2.0 1.0}";
							ccol.put(country, color);
						}
						
						fname += color;
					}
					
					/*String col = (String)obj[18];
					//col = col.replace("_", "");
					//if( fname.length() == 0 ) fname += col;
					//else 
					if( colmap.containsKey(col) ) {
						fname += "["+colmap.get(col)+"]";	
					}*/
				}
			}
		}
		return fname;
	}
	
	public String extractPhy( String filename ) {
		/*JCheckBox species = new JCheckBox("Species");
		JCheckBox accession = new JCheckBox("Acc");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		Object[] params = new Object[] {species, accession, country, source};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);*/
		
		nameSelection( null );
		
		int start = 0;
		int stop = -1;
		if( currentjavafasta != null && currentjavafasta.getSelectedRect() != null ) {
			Rectangle selrect = currentjavafasta.getSelectedRect();
			if( selrect.width > 0 ) {
				start = selrect.x;
				stop = selrect.x+selrect.width;
			}
		}
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				String acc = (String)val[1];
				//System.err.println( acc );
				include.add( acc );
			}
		}
		
		System.err.println( "about to" );
		StringBuilder sb = new StringBuilder();
		InputStream is = DataTable.this.getClass().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			int istart = 0;
			boolean inc = false;
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					istart = 0;
					/*int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);*/
					
					/*if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}*/
					
					String incstr = null;
					for( String str : include ) {
						if( line.contains( str ) ) {
							incstr = str;
							break;
						}
					}
					
					if( incstr != null ) {
						Object[] obj = tablemap.get(incstr);
						
						inc = true;
						/*String fname = "";
						if( species.isSelected() ) {
							Integer ident = (Integer)obj[4];
							String spec = "T.unkown";
							if( ident >= 97 ) {
								spec = (String)obj[2];
								spec = spec.replace("Thermus ", "T.");
								
								int iv = spec.indexOf('_');
								int iv2 = spec.indexOf(' ');
								if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
								else iv = Math.min(iv, iv2);
								if( iv == -1 ) {
									iv = spec.indexOf("16S");
								}
								if( iv != -1 ) spec = spec.substring(0, iv).trim();
							}
							
							if( fname.length() == 0 ) fname += spec;
							else fname += "_"+spec;
						} 
						if( country.isSelected() ) {
							String cntr = (String)obj[11];
							int idx = cntr.indexOf('(');
							if( idx > 0 ) {
								int idx2 = cntr.indexOf(')', idx+1);
								if( idx2 == -1 ) idx2 = cntr.length()-1;
								cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
							}
							if( fname.length() == 0 ) fname += cntr;
							else fname += "_"+cntr;
						} 
						if( source.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						if( accession.isSelected() ) {
							String acc = (String)obj[1];
							acc = acc.replace("_", "");
							if( fname.length() == 0 ) fname += acc;
							else fname += "_"+acc;
						}*/
						String fname = getFastaName( names, metas, obj );
						
						if( fname.length() > 1 ) {
							String startf = (Integer)obj[3] >= 900 ? ">" : ">*";
							sb.append(startf+fname.replace(": ", "-").replace(':', '-').replace(",", "")+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( stop > 0 ) {
						for( int i = start; i < Math.min( stop, istart+line.length() ); i++ ) {
							sb.append( line.charAt(i) );
							if( (i-start)%70 == 69 ) sb.append( '\n' );
						}
						istart += line.length();
					} else {
						if( line.length() > 100 ) {
							for( int i = 0; i < line.length(); i+= 70 ) {
								sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
							}
						} else sb.append( line+"\n" );
					}
				}
				line = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.err.println( "after" );
		return sb.toString();
	}
	
	public StringBuilder extractFasta( String filename ) {
		
		/*JCheckBox species = new JCheckBox("Species");
		JCheckBox accession = new JCheckBox("Acc");
		JCheckBox country = new JCheckBox("Country");
		JCheckBox source = new JCheckBox("Source");
		Object[] params = new Object[] {species, accession, country, source};
		JOptionPane.showMessageDialog(DataTable.this, params, "Select fasta names", JOptionPane.PLAIN_MESSAGE);*/
		
		nameSelection( null );
		
		int start = 0;
		int stop = -1;
		if( currentjavafasta != null && currentjavafasta.getSelectedRect() != null ) {
			Rectangle selrect = currentjavafasta.getSelectedRect();
			if( selrect != null && selrect.width > 0 ) {
				start = selrect.x;
				stop = selrect.x+selrect.width;
			}
		}
		
		Set<String>	include = new HashSet<String>();
		int[] rr = table.getSelectedRows();
		for( int r : rr ) {
			int i = table.convertRowIndexToModel(r);
			if( i != -1 ) {
				Object[] val = rowList.get(i);
				String acc = (String)val[1];
				//System.err.println( acc );
				include.add( acc );
			}
		}
		
		System.err.println( "about to" );
		StringBuilder sb = new StringBuilder();
		InputStream is = DataTable.this.getClass().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		try {
			int istart = 0;
			boolean inc = false;
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					istart = 0;
					/*int v = line.indexOf(' ');
					if( v == -1 ) v = line.length();
					String name = line.substring(1, v).trim();
					String acc = nameaccmap.get(name);*/
					
					/*if( inc != null && seq != null ) {
						//Sequence seq = jf.new Sequence(cont, dna);
						contset.add(seq);
					}*/
					
					String incstr = null;
					for( String str : include ) {
						if( line.contains( str ) ) {
							incstr = str;
							break;
						}
					}
					
					if( incstr != null ) {
						Object[] obj = tablemap.get(incstr);
						
						inc = true;
						/*String fname = "";
						if( species.isSelected() ) {
							Integer ident = (Integer)obj[4];
							String spec = "T.unkown";
							if( ident >= 97 ) {
								spec = (String)obj[2];
								spec = spec.replace("Thermus ", "T.");
								
								int iv = spec.indexOf('_');
								int iv2 = spec.indexOf(' ');
								if( iv == -1 || iv2 == -1 ) iv = Math.max(iv, iv2);
								else iv = Math.min(iv, iv2);
								if( iv == -1 ) {
									iv = spec.indexOf("16S");
								}
								if( iv != -1 ) spec = spec.substring(0, iv).trim();
							}
							
							if( fname.length() == 0 ) fname += spec;
							else fname += "_"+spec;
						} 
						if( country.isSelected() ) {
							String cntr = (String)obj[11];
							int idx = cntr.indexOf('(');
							if( idx > 0 ) {
								int idx2 = cntr.indexOf(')', idx+1);
								if( idx2 == -1 ) idx2 = cntr.length()-1;
								cntr = cntr.substring(0, idx) + cntr.substring(idx2+1);
							}
							if( fname.length() == 0 ) fname += cntr;
							else fname += "_"+cntr;
						} 
						if( source.isSelected() ) {
							if( fname.length() == 0 ) fname += obj[12];
							else fname += "_"+obj[12];
						}
						if( accession.isSelected() ) {
							String acc = (String)obj[1];
							acc = acc.replace("_", "");
							if( fname.length() == 0 ) fname += acc;
							else fname += "_"+acc;
						}*/
						String fname = getFastaName( names, metas, obj );
						
						if( fname.length() > 1 ) {
							String startf = (Integer)obj[4] >= 900 ? ">" : ">*";
							sb.append(startf+fname.replace(": ", "-").replace(':', '-').replace(",", "")+"\n");
						} else sb.append( line+"\n" );
					} else inc = false;
				} else if( inc ) {
					if( stop > 0 ) {
						for( int i = start; i < Math.min( stop, istart+line.length() ); i++ ) {
							sb.append( line.charAt(i) );
							if( (i-start)%70 == 69 ) sb.append( '\n' );
						}
						istart += line.length();
					} else {
						if( line.length() > 100 ) {
							for( int i = 0; i < line.length(); i+= 70 ) {
								sb.append( line.substring(i, Math.min(i+70, line.length()))+"\n" );
							}
						} else sb.append( line+"\n" );
					}
				}
				line = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//String fst = sb.toString();
		return sb;
	}
	
	public void runSql( String sql ) {
		if( sql != null ) {
			sql = sql.replace("table", tableid);
			try {
				if( sql.startsWith("update") && sql.contains(" in ") ) {
					int start = sql.indexOf('(')+1;
					int stop = sql.indexOf(')', start);
					
					String innerstr = sql.substring(start, stop).replace(" ", "").replace(",", "','");
					innerstr = "('"+innerstr+"')";
					int sw = sql.indexOf("where");
					String innersql = "select rowid from "+tableid+" "+sql.substring(sw,start-1)+innerstr; //sql.substring(start, stop);
					//System.err.println("about to run "+innersql);
					String result = run( innersql, true );
					String[] split = result.split("\n");
					
					System.err.println( sql );
					
					String subsql = sql.substring(0, sw);
					for( int i = 1; i < split.length; i++ ) {
						String rowid = split[i];
						String runsql = subsql+"where rowid = '"+rowid+"'";
						System.err.println("about to run "+runsql);
						run( runsql, true );
					}
				} else {
					run( sql, true );
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	JMenu	selectionMenu = new JMenu("Saved selections");
	//Map<String,String>	selectionMap = new HashMap<String,String>();
	public void appendSelection( final String key, final String value ) {
		selectionMenu.add( new AbstractAction( key ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] split = value.split(",");
				Set<String>	selset = new HashSet<String>( Arrays.asList(split) );
				
				for( int i = 0; i < table.getRowCount(); i++ ) {
					if( selset.contains( table.getValueAt(i, 1) ) ) {
						table.addRowSelectionInterval(i, i);
					}
				}
			}
		});
	}
	
	public String countryFuck( String country ) {
		if( country.contains("Zealand") ) {
			country = "New Zealand";
		} else if( country.contains("Hawaii") ) {
			country = "Hawaii";
		} else if( country.contains("Murrieta") ) {
			country = "USA:California";
		} else if( !country.startsWith("USA") ) {
			int i = country.indexOf(':');
			if( i == -1 ) i = country.length();
			country = country.substring(0, i);
			i = country.indexOf(' ');
			if( i == -1 ) i = country.length();
			country = country.substring(0, i);
		} else {
			if( country.contains("Gulf") ) {
				country = "USA:Gulf of Mexico";
			} else if( country.contains("Mexico") ) {
				country = "USA:New Mexico";
			} else if( country.contains("Grass") ) {
				country = "USA:Nevada";
			} else if( country.contains("Alvord") || country.contains("OR") ) {
				country = "USA:Oregon";
			} else if( country.contains("California") || country.contains("CA") ) {
				country = "USA:California";
			} else if( country.contains("Yellowstone") ) {
				country = "USA:Yellowstone";
			} else {
				int i = country.indexOf(' ');
				if( i == -1 ) i = country.length();
				country = country.substring(0, i);
			}
		}
		return country;
	}
	
	public String countryShort( String country ) {
		 if( country.contains("Azores") ) {
				country = "Azores";
		} else if( country.contains("Greece") || country.contains("Turkey") || country.contains("Italy")  || country.contains("Bulgaria") || country.contains("Hungary") || country.contains("Portugal") || country.contains("Switz") ) {
			country = "Europe";
		} else if( country.contains("Hawai") ) {
			country = "Hawaii";
		} else if( country.contains("USA") || country.contains("Yellowstone") ) {
			country = "North-America";
		} /*else if() {
			
		}*/
		return country;
	}
	
	public void getSpecLoc( List<Object[]> rowList, String[] specs, Map<String,Map<String,Long>> specLoc, Map<String,Map<String,Long>> locSpec, Map<String,String> geoLoc, boolean reverse, boolean erm, boolean shorter ) {
		for( Object[] row : rowList ) {
			String country = reverse ? (String)row[21] : (String)row[6];
			if( erm ) {
				int i = country.indexOf(',');
				if( i == -1 ) i = country.length();
				country = country.substring(0, i);
				country = countryFuck( country );
				if( shorter ) country = countryShort( country );
			}
			if( country != null && country.length() > 0 ) {
				String species = (String)row[3];
				String geocode = reverse ? (String)row[6] : (String)row[21];
				geoLoc.put( country, geocode );
				
				String thespec = null;
				for( String spec : specs ) {
					if( species.contains(spec) ) {
						thespec = "T."+spec;
						break;
					}
				}
				
				if( thespec != null ) {
					int len = (Integer)row[4];
					int id = (Integer)row[5];
					long idlen = (((long)len)<<16)+id;
					
					Map<String,Long> cmap;
					if( specLoc.containsKey( thespec ) ) {
						cmap = specLoc.get( thespec );
					} else {
						cmap = new TreeMap<String,Long>();
						specLoc.put( thespec, cmap );
					}
					
					if( cmap.containsKey(country) ) {
						long oldidlencount = cmap.get(country);
						
						int oldidlen = (int)(oldidlencount&0xFFFFFFFF);
						int oldcount = (int)(oldidlencount>>32);
						
						int oldid = (int)(oldidlen&0xFFFF);
						int oldlen = (int)(oldidlen>>16);
						
						if( id > oldid || (id == oldid && len > oldlen) ) {
							cmap.put( country, idlen+((long)(oldcount+1)<<32) );
						} else {
							cmap.put( country, oldidlen+((long)(oldcount+1)<<32) );
						}
					} else {
						cmap.put( country, idlen+(1L<<32) );
					}
					
					Map<String,Long> smap;
					if( locSpec.containsKey( country ) ) {
						smap = locSpec.get( country );
					} else {
						smap = new TreeMap<String,Long>();
						locSpec.put( country, smap );
					}
					if( smap.containsKey(thespec) ) {
						long oldidlencount = smap.get( thespec );
						
						int oldidlen = (int)(oldidlencount&0xFFFFFFFF);
						int oldcount = (int)(oldidlencount>>32);
						
						int oldid = (int)(oldidlen&0xFFFF);
						int oldlen = (int)(oldidlen>>16);
						
						/*long oldidlen = smap.get(country);
						int oldid = (int)(oldidlen&0xFFFF);
						int oldlen = (int)(oldidlen>>32);*/
						
						if( id > oldid || (id == oldid && len > oldlen) ) {
							smap.put( thespec, idlen+((long)(oldcount+1)<<32) );
						} else {
							smap.put( thespec, oldidlen+((long)(oldcount+1)<<32) );
						}
					} else {
						smap.put( thespec, idlen+(1L<<32)  );
					}
				}
			}
		}
	}
	
	public void insertGeocodes() throws IOException, AuthenticationException {
		service = new GoogleService("fusiontables", "fusiontables.ApiExample");
		service.setUserCredentials("sigmarkarl@gmail.com", "mul", ClientLoginAccountType.GOOGLE);
		
		FileReader fr = new FileReader( "/home/sigmar/Downloads/Thermus_16S_aligned.csv" );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		line = br.readLine();
		
		Map<String,Set<String>>		accsetMap = new HashMap<String,Set<String>>();
		Map<String,String>			accountryMap = new HashMap<String,String>();
		Map<String,String>			accspecMap = new HashMap<String,String>();
		//int count = 0;
		while( line != null ) {
			String[] split = csvSplit( line ); //line.split(",");
			String acc = split[1];
			String spec = split[3];
			String country = split[6];
			Set<String>	accset;
			if( accsetMap.containsKey( country ) ) {
				accset = accsetMap.get( country );
			} else {
				accset = new HashSet<String>();
				accsetMap.put( country, accset );
			}
			accset.add( acc );
			
			accountryMap.put( acc, country );
			accspecMap.put( acc, spec );
			
			line = br.readLine();
		}
		br.close();
		
		Set<String> finished = new HashSet<String>();
		for( Object[] row : rowList ) {
			String acc = (String)row[1];
			if( !finished.contains( acc ) ) {
				System.err.println("try "+acc);
				
				String coord = fetchCoord( acc );
				String country = accountryMap.get( acc );
				Set<String> accset = accsetMap.get( country );
				finished.addAll( accset );
				String accsetStr = accset.toString();
				String accsetStr2 = accsetStr.substring(1, accsetStr.length()-1);
				//String sql = "update table set geocode = '"+coord+"' where acc in ("+accsetStr2+")";
				String sql = "update table set geocode = '"+coord+"' where acc in ("+accsetStr2+")";
				runSql( sql );
				
				System.err.println("done");
			}
		}
	}
	
	public static void assignSupportValues( Node n, Map<Set<String>,NodeSet> nmap, boolean copybootstrap ) {
		if( !n.isLeaf() ) {
			for( Node cn : n.getNodes() ) {
				assignSupportValues( cn, nmap, copybootstrap );
			}
			Set<String> leavenames = n.getLeaveNames();
			NodeSet ns = nmap.get( leavenames );
			if( !copybootstrap ) {
				n.setName( Math.round( (double)(ns.getCount()) / (double)1000.0 ) / 10.0 + "%" );
			} else {
				n.setName( Double.toString( Math.round( (ns.getAverageBootstrap()*100.0) )/100.0 ) );
			}
		}
	}
	
	public static Node majoRuleConsensus( TreeUtil tu, Map<Set<String>,NodeSet> nmap, Node guideTree, boolean copybootstrap ) {
		List<NodeSet>	nslist = new ArrayList<NodeSet>();
		System.err.println( nmap.size() );
		for( Set<String> nodeset : nmap.keySet() ) {
			NodeSet count = nmap.get( nodeset );
			nslist.add( count );
		}
		
		//Map<Set<String>,NodeSet>	guideMap = new HashMap<Set<String>,NodeSet>();
		//guideTree.nodeCalcMap( guideMap );
		
		Node root;
		if( guideTree != null ) {
			root = guideTree;
			assignSupportValues( root, nmap, copybootstrap );
		} else {
			Collections.sort( nslist );
			int c = 0;
			for( NodeSet nodeset : nslist ) {
				System.err.println( nodeset.getCount() + "  " + nodeset.getNodes() + "  " + nodeset.getAverageHeight() + "  " + nodeset.getAverageBootstrap() );
				c++;
				if( c > 20 ) break;
			}
			
			//Map<Set<String>, Node>	nodemap = new HashMap<Set<String>, Node>();
			Map<String, Node>		leafmap = new HashMap<String, Node>();
			NodeSet	allnodes = nslist.get(0);
			int total = allnodes.getCount();
			root = tu.new Node();
			for( String nname : allnodes.getNodes() ) {
				Node n = tu.new Node( nname, false );
				root.addNode(n, 1.0);
				//n.seth( 1.0 );
				leafmap.put( nname, n );
			}
			
			for( int i = 1; i < Math.min( nslist.size(), 100 ); i++ ) {
				NodeSet	allsubnodes = nslist.get(i);
				Node subroot = tu.new Node();
				if( !copybootstrap ) {
					subroot.setName( Math.round( (double)(allsubnodes.getCount()*1000) / (double)total ) / 10.0 + "%" );
				} else {
					subroot.setName( Double.toString( Math.round( (allsubnodes.getAverageBootstrap()*100.0) )/100.0 ) );
				}
				
				Node vn = tu.getValidNode( allsubnodes.getNodes(), root );
				if( tu.isValidSet( allsubnodes.getNodes(), vn ) ) {
					while( allsubnodes.getNodes().size() > 0 ) {
						for( String nname : allsubnodes.getNodes() ) {
							Node leaf = leafmap.get( nname );
							Node newparent = leaf.getParent();
							Node current = leaf;
							while( newparent.countLeaves() <= allsubnodes.getNodes().size() ) {
								current = newparent;
								newparent = current.getParent();
							}
							
							if( allsubnodes.getNodes().containsAll( current.getLeaveNames() ) ) {
								Node parent = current.getParent();
								parent.removeNode( current );
								
								double h = allsubnodes.getAverageHeight();
								//double b = allsubnodes.getAverageBootstrap();
								double lh = allsubnodes.getAverageLeaveHeight(nname);
								
								/*subroot.addNode( current, h );
								if( lh != -1.0 ) parent.addNode( subroot, lh );
								else parent.addNode( subroot, 1.0 );*/
								
								parent.addNode( subroot, h );
								
								if( current.isLeaf() && lh != -1.0 ) {
									System.err.println( "printing "+current.getName() + "  " + lh );
									subroot.addNode( current, lh );
								} else subroot.addNode( current, current.geth() );
							
								removeNames( allsubnodes.getNodes(), current );
							} else allsubnodes.getNodes().clear();
							
							break;
						}
					}
				}
			}
		}
		
		return root;
	}
	
	public static void removeNames( Set<String> set, Node node ) {
		List<Node> subnodes = node.getNodes();
		if( subnodes != null ) for( Node n : subnodes ) {
			removeNames(set, n);
		}
		set.remove( node.getName() );
	}
	
	public void start() {
		super.start();
		System.err.println( "starting..." );
		//load();
	}
	
	boolean done = false;
	public boolean load() {
		boolean succ = true;
		if( !done ) {
			try {
				//System.err.println( "bleh3erm" );
				Object obj = JSObject.class;
				obj = null;
				//System.err.println( "bleh4" );
				JSObject erm = (JSObject)obj;
				//System.err.println( "ble2h"+this );
				JSObject win = JSObject.getWindow(this);
				//System.err.println( "about to run loadData" );
				win.call("loadData", new Object[] {});
				win.call("reqSavedSel", new Object[] {});
				//System.err.println( "done loadData" );
				//win.call("loadMeta", new Object[] {});
			} catch( NoSuchMethodError | Exception e ) {
				succ = false;
				e.printStackTrace();
			}
			done = true;
		}
		
		return succ;
	}
	
	byte[] current = null;
	public byte[] blobFetch() {
		return current;
	}
	
	public void init() {
		initGUI( this );
	}
    
	Map<String,String>	colmap = new HashMap<String,String>();
	JavaFasta	currentjavafasta;
	Serifier	currentserifier;
	public void initGUI( Container cont ) {
		updateLof();
		
		specColors.put("antranikianii", "000088");
		specColors.put("aquaticus", "FFFF00");
		specColors.put("arciformis", "888800");
		specColors.put("brockianus", "00FF00");
		specColors.put("igniterrae", "008800");
		specColors.put("eggertsoni", "88FF88");
		specColors.put("filiformis", "00FFFF");
		specColors.put("islandicus", "FF8800");
		specColors.put("kawarayensis", "88FF00");
		specColors.put("oshimai", "FF00FF");
		specColors.put("scotoductus", "0000FF");
		specColors.put("thermophilus", "FF0000");
		specColors.put("yunnanensis", "8888FF");
		specColors.put("rehai", "8888FF");
		specColors.put("composti", "888844");
		specColors.put("unknownchile", "008888");
		
		colmap.put("small_red", "#FF0000");
		colmap.put("small_green", "#00FF00");
		colmap.put("small_blue", "#0000FF");
		colmap.put("small_cyan", "#00FFFF");
		colmap.put("small_yellow", "#FFFF00");
		colmap.put("small_magenta", "#FF00FF");
		colmap.put("small_white", "#FFFFFF");
		colmap.put("small_grey", "#999999");
		colmap.put("small_brown", "#555511");
		colmap.put("small_orange", "#999944");
		colmap.put("small_purple", "#AA22AA");
		colmap.put("small_black", "#000000");
		
		namesMap.put("Chile", "Chile");
		namesMap.put("Yellowstone", "Yellowstone");
		namesMap.put("Iceland", "Iceland");
		namesMap.put("Australia", "Australia");
		namesMap.put("Oregon", "USA-Oregon");
		namesMap.put("Nevada", "USA-Nevada");
		namesMap.put("Washington", "USA-Washington");
		namesMap.put("California", "USA-California");
		namesMap.put("Guinea", "Papua New Guinea");
		namesMap.put("OR", "USA-Oregon");
		namesMap.put("Fiji", "Fiji");
		namesMap.put("Italy", "Italy");
		namesMap.put("India", "India");
		namesMap.put("New Mexico", "USA-New Mexico");
		namesMap.put("China", "China");
		namesMap.put("Japan", "Japan");
		namesMap.put("Bulgaria", "Bulgaria");
		namesMap.put("Taiwan", "Taiwan");
		namesMap.put("New Zealand", "New Zealand");
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		//table.setColumnSelectionAllowed( true );
		JScrollPane	scrollpane = new JScrollPane( table );
		tablemap = new HashMap<String,Object[]>();
		
		/*InputStream is = this.getClass().getResourceAsStream( "/therm3.txt" );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		try {
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\t");
				
				nameaccmap.put(split[0], split[1]);
				Object[] strs = new Object[ 14 ];
				int i;
				for( i = 0; i < split.length; i++ ) {
					if( i == 3 || i == 4 ) strs[i] = Integer.parseInt( split[i] );
					else strs[i] = split[i];
				}
				if( i == 8 ) strs[i++] = "";
				if( i == 9 ) strs[i++] = "";
				if( i == 10 ) strs[i++] = "";
				if( i == 11 ) strs[i++] = "";
				if( i == 12 ) strs[i++] = "";
				//if( i == 13 ) strs[i++] = "";
				strs[i] = true;
				//Arrays.copyOfRange(split, 1, split.length );
				rowList.add( strs );
				tablemap.put((String)strs[1t], strs);
				
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		table.setModel( new TableModel() {
			@Override
			public int getRowCount() {
				return rowList.size();
			}

			@Override
			public int getColumnCount() {
				return 22;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "name";
				else if( columnIndex == 1 ) return "acc";
				else if( columnIndex == 2 ) return "fullname";
				else if( columnIndex == 3 ) return "species";
				else if( columnIndex == 4 ) return "len";
				else if( columnIndex == 5 ) return "ident";
				else if( columnIndex == 6 ) return "country";
				else if( columnIndex == 7 ) return "source";
				else if( columnIndex == 8 ) return "doi";
				else if( columnIndex == 9 ) return "pubmed";
				else if( columnIndex == 10 ) return "author";
				else if( columnIndex == 11 ) return "journal";
				else if( columnIndex == 12 ) return "sub_auth";
				else if( columnIndex == 13 ) return "sub_date";
				else if( columnIndex == 14 ) return "lat_lon";
				else if( columnIndex == 15 ) return "date";
				else if( columnIndex == 16 ) return "title";
				//else if( columnIndex == 17 ) return "arb";
				else if( columnIndex == 17 ) return "color";
				else if( columnIndex == 18 ) return "temp";
				else if( columnIndex == 19 ) return "pH";
				else if( columnIndex == 20 ) return "geocode";
				else if( columnIndex == 21 ) return "valid";
				//else if( columnIndex == 13 ) return "color";
				
				return "";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 4 || columnIndex == 5 ) return Integer.class;
				else if( columnIndex == 18 || columnIndex == 19 ) return Double.class;
				else if( columnIndex == 21 ) return Boolean.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 6 || columnIndex == 18 ) return true;
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = "";
				
				Object[] val = rowList.get(rowIndex);
				if( columnIndex < val.length ) {
					//if( columnIndex == 2 || columnIndex ==3 ) return Integer.parseInt( val[columnIndex] );
					ret = val[columnIndex];
				}
				//if( ret instanceof Integer ) System.err.println( columnIndex );
				return ret;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				//int r = table.convertRowIndexToModel( rowIndex );
				Object[] row = rowList.get(rowIndex);
				row[columnIndex] = aValue;
				
				try {
					JSObject jso = JSObject.getWindow( DataTable.this );
					jso.call( "saveMeta", new Object[] {row[1], row[11], row[13]} );
				} catch( Exception e ) {
					
				}
			}

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public void removeTableModelListener(TableModelListener l) {}
		});
		
		table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		table.getColumnModel().getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		//final DataFlavor df = DataFlavor.getTextPlainUnicodeFlavor();
		final DataFlavor df;
		DataFlavor dflocal = null;
		try {
			dflocal = new DataFlavor("text/plain;charset=utf-8");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} finally {
			df = dflocal;
		}
		//final DataFlavor textfl = DataFlavor.pl
		//String mime = df.getMimeType();
		final String charset = df.getParameter("charset");
		//int start = mime.indexOf("harset=")+7;
		//int stop = mime.indexOf(';', start);
		//if( stop == -1 ) stop = mime.length();
		//final String type = mime.substring(start, stop);
		
		final Transferable transferable = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus.ntree");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/RAxML_10993.ntree");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/testskrimsli.phb");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/lmin900idmin95phyml.tree");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus16S_hq.phb");
				//InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus16S_hq.phy_phyml_tree.txt");
				InputStream is = DataTable.this.getClass().getResourceAsStream("/thermus16S_selected.phb");
				
				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while( line != null ) {
					sb.append( line );
					line = br.readLine();
				}
				br.close();
				
				Map<String,String>	colormap = new HashMap<String,String>();
				
				String[] ss = new String[] {"unknown", "kawarayensis", "scotoductus", "thermophilus", "eggertsoni", "islandicus", "igniterrae", "brockianus", "aquaticus", "oshimai", "filiformis", "antranikianii"};
				Set<String> collapset = new HashSet<String>( Arrays.asList( ss ) );
				
				String[] cc = new String[] {"USA", "Yellowstone", "Hawaii", "Tibet", "Taiwan", "Italy", "Bulgaria", "Hungary", "Iceland", "Portugal", "China", "Japan", "Australia", "New Zealand", "Chile", "Antarctica", "Puerto Rico", "Greece", "Switzerland", "Russia", "India", "Indonesia"};
				Set<String> countryset = new HashSet<String>( Arrays.asList( cc ) );
				
				Random rnd = new Random();
				for( String c : cc ) {
					//String cstr = "rgb( "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+" )";
					String cstr = Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 );
					colormap.put( c, cstr );
				}
				for( String s : ss ) {
					//colormap.put( s, "rgb( "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+" )" );
					String cstr = Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 );
					colormap.put( s, cstr );
					for( String c : cc ) {
						//colormap.put( s+"-"+c, "rgb( "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+", "+(int)(180+rnd.nextFloat()*75)+" )" );
						cstr = Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 )+Integer.toString( (int)(150+rnd.nextFloat()*75), 16 );
						colormap.put( s+"-"+c, cstr );
					}
				}
				
				Map<String,Map<String,String>> mapmap = new HashMap<String,Map<String,String>>();
				Set<String>	include = new HashSet<String>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					String name = (String)table.getValueAt(r, 0);
					name = name.substring(0, Math.min( name.length(), 10 ));
					String acc = (String)table.getValueAt(r, 1);
					include.add( name );
					include.add( acc );
					
					Map<String,String>	map = new HashMap<String,String>();
					String nm = (String)table.getValueAt(r, 2);
					int id = (Integer)table.getValueAt(r, 4);
					
					if( id >= 97 ) {
						if( nm.contains("t.eggertsoni") ) nm = "Thermus eggertsoni";
						else if( nm.contains("t.islandicus") ) nm = "Thermus islandicus";
						else if( nm.contains("t.kawarayensis") ) nm = "Thermus kawarayensis";
						else if( nm.contains("t.brock") ) nm = "Thermus brockianus";
						else {
							int ix = nm.indexOf(' ');
							if( ix > 0 ) {
								nm = nm.substring(0, nm.indexOf(' ', ix+1) );
							}
						}
					} else {
						nm = "Thermus unknown";
					}
					
					map.put("name", nm);
					String country = (String)table.getValueAt(r, 11);
					//String acc = (String)table.getValueAt(r, 1);
					if( country != null && country.length() > 0 ) {
						map.put( "country", country );
					}
					map.put( "acc", acc );
					map.put("id", Integer.toString(id));
					//mapmap.put(acc, map);
					mapmap.put(name, map);
				}
				
				TreeUtil tu = new TreeUtil();
				tu.init( sb.toString(), false, include, mapmap, cbmi.isSelected(), collapset, colormap, true  );
				//return arg0.getReaderForText( this );
				String str = tu.getNode().toString();
				return new ByteArrayInputStream( str.getBytes( charset ) );
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
				return false;
			}

			protected Transferable createTransferable(JComponent c) {
				return transferable;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				/*try {
					Object obj = support.getTransferable().getTransferData( df );
					InputStream is = (InputStream)obj;
					
					byte[] bb = new byte[2048];
					int r = is.read(bb);
					
					//importFromText( new String(bb,0,r) );
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				return false;
			}
		};
		table.setTransferHandler( th );
		table.setDragEnabled( true );
		
		try {
	    	clipboardService = (ClipboardService)ServiceManager.lookup("javax.jnlp.ClipboardService");
            //table.getActionMap().put( "copy", action );
            grabFocus = true;
	    } catch (Exception e) { 
	    	e.printStackTrace();
	    	System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
	    }
		/*table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				if( me.getClickCount() == 2 ) {
					int r = table.getSelectedRow();
					int i = table.convertRowIndexToModel(r);
					if( i != -1 ) {
						String[] str = rowList.get( i );
						String doi = str[4];
						try {
							URL url = new URL( "http://dx.doi.org/"+doi );
							DataTable.this.getAppletContext().showDocument( url );
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});*/
		
		System.err.println("ermf");
		
		final Set<Integer>	filterset = new HashSet<Integer>();
		final RowFilter 	filter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				return filterset.isEmpty() || filterset.contains(entry.getIdentifier());
			}
		};
		updateFilter(table, filter);
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Export KML") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Map<String,Long>>	specLoc = new TreeMap<String,Map<String,Long>>();
				Map<String,Map<String,Long>>	locSpec = new TreeMap<String,Map<String,Long>>();
				Map<String,String>				geoLoc = new HashMap<String,String>();
				
				List<Object[]>	selectedRowList = new ArrayList<Object[]>();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					selectedRowList.add( rowList.get( table.convertRowIndexToModel(r) ) );
				}
				getSpecLoc( selectedRowList, specs, specLoc, locSpec, geoLoc, true, false, false );
				
				try {
					FileWriter fw = new FileWriter("/home/sigmar/kml.kml");
					fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					fw.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
					fw.write("<Document>");
					
					for( String coord : locSpec.keySet() ) {
						String country = geoLoc.get( coord );
						
						Map<String,Long>	specMap = locSpec.get( coord );
						String specsstr = null;
						
						String unos = null;
						String colors = null;
						String size = "40x40";
						for( String spec : specMap.keySet() ) {
							long idlencount = specMap.get(spec);
							
							int idlen = (int)(idlencount&0xFFFFFFFF);
							int count = (int)(idlencount>>32);
							
							int id = (int)(idlen&0xFFFF);
							int len = (int)(idlen>>16);
							
							if( specsstr == null ) specsstr = spec + " ("+id+","+len+","+count+")";
							else specsstr += "," + spec + " ("+id+","+len+","+count+")";
							
							if( len > 900 && id > 97 ) size = "50x50";
							
							if( unos == null ) {
								if( len > 900 && id > 97 ) unos = "2";
								else unos = "1";
								colors = specColors.get( spec.substring(2) );
							} else {
								if( len > 900 && id > 97 ) unos += ",2";
								else unos += ",1";
								colors += ","+specColors.get( spec.substring(2) );
							}
						}
						
						fw.write("<Placemark>\n");
						fw.write("<name>"+specsstr+"</name>\n");
						fw.write("<description>"+country+"</description>\n");
						fw.write("<Style>");
						fw.write("<IconStyle>");
						fw.write("<scale>1.0</scale>");
						fw.write("<Icon>");
						fw.write("<href>http://chart.apis.google.com/chart?cht=p&amp;chd=t:"+unos+"&amp;chs="+size+"&amp;chf=bg,s,ffffff00&amp;chco="+colors+"</href>");
						fw.write("</Icon>");
						fw.write("</IconStyle>");
						fw.write("</Style>");
						fw.write("<Point>\n");
						fw.write("<coordinates>"+coord+"</coordinates>\n");
						fw.write("</Point>\n");
						fw.write("</Placemark>\n");
					}
					
					fw.write("</Document>\n");
					fw.write("</kml>\n");
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				/*try {
					insertGeocodes();
				} catch (IOException | AuthenticationException e1) {
					e1.printStackTrace();
				}*/
			}
		});
		popup.add( new AbstractAction("Export all KML") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Map<String,Long>>	specLoc = new TreeMap<String,Map<String,Long>>();
				Map<String,Map<String,Long>>	locSpec = new TreeMap<String,Map<String,Long>>();
				Map<String,String>				geoLoc = new HashMap<String,String>();
				getSpecLoc( rowList, specs, specLoc, locSpec, geoLoc, true, false, false );
				
				try {
					FileWriter fw = new FileWriter("/home/sigmar/kml.kml");
					fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					fw.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
					fw.write("<Document>");
					
					for( String coord : locSpec.keySet() ) {
						String country = geoLoc.get( coord );
						
						Map<String,Long>	specMap = locSpec.get( coord );
						String specsstr = null;
						
						String unos = null;
						String colors = null;
						String size = "40x40";
						for( String spec : specMap.keySet() ) {
							long idlencount = specMap.get(spec);
							
							int idlen = (int)(idlencount&0xFFFFFFFF);
							int count = (int)(idlencount>>32);
							
							int id = (int)(idlen&0xFFFF);
							int len = (int)(idlen>>16);
							
							if( specsstr == null ) specsstr = spec + " ("+id+","+len+","+count+")";
							else specsstr += "," + spec + " ("+id+","+len+","+count+")";
							
							if( len > 900 && id > 97 ) size = "50x50";
							
							if( unos == null ) {
								if( len > 900 && id > 97 ) unos = "2";
								else unos = "1";
								colors = specColors.get( spec.substring(2) );
							} else {
								if( len > 900 && id > 97 ) unos += ",2";
								else unos += ",1";
								colors += ","+specColors.get( spec.substring(2) );
							}
						}
						
						fw.write("<Placemark>\n");
						fw.write("<name>"+specsstr+"</name>\n");
						fw.write("<description>"+country+"</description>\n");
						fw.write("<Style>");
						fw.write("<IconStyle>");
						fw.write("<scale>1.0</scale>");
						fw.write("<Icon>");
						fw.write("<href>http://chart.apis.google.com/chart?cht=p&amp;chd=t:"+unos+"&amp;chs="+size+"&amp;chf=bg,s,ffffff00&amp;chco="+colors+"</href>");
						fw.write("</Icon>");
						fw.write("</IconStyle>");
						fw.write("</Style>");
						fw.write("<Point>\n");
						fw.write("<coordinates>"+coord+"</coordinates>\n");
						fw.write("</Point>\n");
						fw.write("</Placemark>\n");
					}
					
					fw.write("</Document>\n");
					fw.write("</kml>\n");
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				/*try {
					insertGeocodes();
				} catch (IOException | AuthenticationException e1) {
					e1.printStackTrace();
				}*/
			}
		});
		popup.add( new AbstractAction("Export biogeography report") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] specs = {"antranikianii","aquaticus","arciformis","brockianus","eggertsoni","filiformis","igniterrae","islandicus","kawarayensis","oshimai","scotoductus","thermophilus","yunnanensis","rehai","composti","unknownchile"};
				
				Map<String,Map<String,Long>>	specLoc = new TreeMap<String,Map<String,Long>>();
				Map<String,Map<String,Long>>	locSpec = new TreeMap<String,Map<String,Long>>();
				
				Map<String,Map<String,Long>>	specSimpLoc = new TreeMap<String,Map<String,Long>>();
				Map<String,Map<String,Long>>	locSimpSpec = new TreeMap<String,Map<String,Long>>();
				
				Map<String,String>				geoLoc = new HashMap<String,String>();
				getSpecLoc( rowList, specs, specLoc, locSpec, geoLoc, false, false, false );
				getSpecLoc( rowList, specs, specSimpLoc, locSimpSpec, geoLoc, false, true, false );
				
				Workbook wb = new XSSFWorkbook();
				Sheet lSheet = wb.createSheet("Locations");
				Sheet sSheet = wb.createSheet("Species");
				Sheet bSheet = wb.createSheet("Boolean");
				Sheet bsSheet = wb.createSheet("BoolSimple");
				
				Row r = lSheet.createRow(0);
				r.createCell(0).setCellValue("Loction");
				r.createCell(1).setCellValue("Species");
				r.createCell(2).setCellValue("Identity");
				r.createCell(3).setCellValue("Length");
				
				int val = 1;
				for( String cnt : locSpec.keySet() ) {
					Map<String,Long> smap = locSpec.get(cnt);
					int subval = 0;
					for( String spec : smap.keySet() ) {
						long idlencount = smap.get(spec);
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						r = lSheet.createRow(val+subval);
						if( subval == 0 ) r.createCell(0).setCellValue( cnt );
						if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
							r.createCell(1).setCellValue( "*"+spec+" ("+count+")" );
						} else {
							r.createCell(1).setCellValue( spec+" ("+count+")" );
						}
						r.createCell(2).setCellValue( id );
						r.createCell(3).setCellValue( len );
						
						subval++;
					}
					val += subval;
				}
				
				r = sSheet.createRow(0);
				r.createCell(0).setCellValue("Species");
				r.createCell(1).setCellValue("Location");
				r.createCell(2).setCellValue("Identity");
				r.createCell(3).setCellValue("Length");
				
				val = 1;
				for( String spec : specLoc.keySet() ) {
					Map<String,Long> cmap = specLoc.get(spec);
					int subval = 0;
					for( String cnt : cmap.keySet() ) {
						long idlencount = cmap.get( cnt);
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						/*long idlen = cmap.get(cnt);
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>32);*/
						
						r = sSheet.createRow(val+subval);
						if( subval == 0 ) r.createCell(0).setCellValue( spec );
						if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
							r.createCell(1).setCellValue( "*"+cnt+" ("+count+")" );
						} else {
							r.createCell(1).setCellValue( cnt+" ("+count+")" );
						}
						r.createCell(2).setCellValue( id );
						r.createCell(3).setCellValue( len );
						
						subval++;
					}
					val += subval;
				}
				
				Map<String,Integer>	specIndex = new HashMap<String,Integer>();
				r = bSheet.createRow(0);
				val = 0;
				for( String spec : specLoc.keySet() ) {
					specIndex.put( spec, val );
					r.createCell(++val).setCellValue( spec );
				}
				
				val = 1;
				for( String cnt : locSpec.keySet() ) {
					Map<String,Long> smap = locSpec.get(cnt);
					
					r = bSheet.createRow(val);
					r.createCell(0).setCellValue(cnt);
					for( String spec : smap.keySet() ) {
						long idlencount = smap.get( spec );
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						/*long idlen = smap.get(spec);
						int id = (int)(idlen&0xFF);
						int len = (int)(idlen>>32);*/
						
						int idx = -1;
						if( specIndex.containsKey(spec) ) idx = specIndex.get(spec);
						if( idx != -1 ) {
							if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
								r.createCell(idx+1).setCellValue( "*" + id + "/" + len+" ("+count+")" );
							} else {
								r.createCell(idx+1).setCellValue( id + "/" + len+" ("+count+")" );
							}
						}
					}
					val++;
				}
				
				Map<String,Integer>	specSimpIndex = new HashMap<String,Integer>();
				r = bsSheet.createRow(0);
				val = 0;
				for( String spec : specSimpLoc.keySet() ) {
					specSimpIndex.put( spec, val );
					r.createCell(++val).setCellValue( spec );
				}
				
				val = 1;
				for( String cnt : locSimpSpec.keySet() ) {
					Map<String,Long> smap = locSimpSpec.get(cnt);
					
					r = bsSheet.createRow(val);
					r.createCell(0).setCellValue(cnt);
					for( String spec : smap.keySet() ) {
						long idlencount = smap.get( spec );
						
						int idlen = (int)(idlencount&0xFFFFFFFF);
						int count = (int)(idlencount>>32);
						
						int id = (int)(idlen&0xFFFF);
						int len = (int)(idlen>>16);
						
						/*long idlen = smap.get(spec);
						int id = (int)(idlen&0xFF);
						int len = (int)(idlen>>32);*/
						
						int idx = -1;
						if( specSimpIndex.containsKey(spec) ) idx = specSimpIndex.get(spec);
						if( idx != -1 ) {
							if( id < 97 || len < 900 || (id == 97 &&  len < 900) ) {
								r.createCell(idx+1).setCellValue( "*" + id + "/" + len+" ("+count+")" );
							} else {
								r.createCell(idx+1).setCellValue( id + "/" + len+" ("+count+")" );
							}
						}
					}
					val++;
				}
				
	        	try {
	        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
					wb.write( baos );
					baos.close();
					
					byte[] bb = baos.toByteArray();
					//Files.write( Paths.get( new File("/u0/tmp.xlsx").toURI() ), bb );
					
					//String str = baos.toString();
					String str = Base64.getEncoder().encodeToString(bb);
					current = bb;
		        	JSObject obj = JSObject.getWindow( DataTable.this );
		        	obj.call("blobfetch", new Object[] {str,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        	 
				/*FileSaveService fss = null;
		        FileContents fileContents = null;
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        OutputStreamWriter	osw = new OutputStreamWriter( baos );
		    	
		    	try {
		    		fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
		    	} catch( UnavailableServiceException e1 ) {
		    		fss = null;
		    	}
		    	 
		    	try {
			        if (fss != null) {
			        	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
			            fileContents = fss.saveFileDialog(null, null, bais, "export.xlsx");
			            bais.close();
			            OutputStream os = fileContents.getOutputStream(true);
			            //os.write( baos.toByteArray() );
			            wb.write( os );
			            os.close();
			        } else {
			        	JFileChooser jfc = new JFileChooser();
			        	if( jfc.showSaveDialog( DataTable.this ) == JFileChooser.APPROVE_OPTION ) {
			        		File f = jfc.getSelectedFile();
			        		FileOutputStream fos = new FileOutputStream( f );
			        		wb.write( fos );
			        		//fos.write( baos.toByteArray() );
			        		fos.close();
			        		 
			        		Desktop.getDesktop().browse( f.toURI() );
			        	}
			        }
		    	} catch( IOException ioe ) {
		    		ioe.printStackTrace();
		    	}*/
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("SQL") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
				dialog.setSize(800, 600);
				dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				final JTextArea	textarea = new JTextArea();
				
				JButton	exc = new JButton( new AbstractAction("Execute") {
					@Override
					public void actionPerformed(ActionEvent e) {
						String sql = textarea.getSelectedText();
						if( sql == null ) sql = textarea.getText();
						runSql( sql );
					}
				});
				JButton	cls = new JButton( new AbstractAction("Close") {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				
				JComponent comp = new JComponent() {};
				comp.setLayout( new FlowLayout() );
				comp.add( exc );
				comp.add( cls );
				
				dialog.setLayout( new BorderLayout() );
				JScrollPane	scrollpane = new JScrollPane( textarea );
				dialog.add( scrollpane );
				dialog.add( comp, BorderLayout.SOUTH );
				
				dialog.setVisible( true );
			}
		});
		popup.add(new AbstractAction("Crop to selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterset.clear();
				int[] rr = table.getSelectedRows();
				for (int r : rr) {
					int mr = table.convertRowIndexToModel(r);
					filterset.add(mr);
				}
				updateFilter(table, filter);
			}
		});
		popup.add( new AbstractAction("Save selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selname = JOptionPane.showInputDialog("Name of selection?");
				StringBuilder sb = new StringBuilder();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					Object o = table.getValueAt(r, 1);
					if( r == rr[0] ) sb.append( (String)o );
					else sb.append( ","+(String)o );
				}
				
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("saveSel", new Object[] {selname, sb.toString()} );
			}
		});
		popup.add( new AbstractAction("Propogate selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					Object o = table.getValueAt(r, 1);
					String val = ((String)o);//.replace("_", "");
					if( r == rr[0] ) sb.append( val );
					else sb.append( ","+val );
				}
				
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("propSel", new Object[] {sb.toString()} );
			}
		});
		popup.add( selectionMenu );
		Action action = new CopyAction( "Copy" );
		popup.add( action );
		popup.addSeparator();
		
		JMenu	njmenu = new JMenu( "NJTree" );
		popup.add( njmenu );
		njmenu.add( new AbstractAction("NJTree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//final JCheckBox	colors = new JCheckBox("Species colors");
				final JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
				final JCheckBox	boots = new JCheckBox("Bootstrap");
				final JCheckBox	majo = new JCheckBox("Majority-rule consensus from bootstrap replicates");
				final JCheckBox	entropy = new JCheckBox("Entropy weighting");
				final JCheckBox	exgaps = new JCheckBox("Exclude gaps");
				Object[] extraObjs = new Object[] {jukes, boots, majo, exgaps, entropy};
				//JOptionPane.showMessageDialog( DataTable.this, extraObjs );
				
				runnable = new Runnable() {
					public void run() {
						//boolean color = colors.isSelected();
						boolean cantor = jukes.isSelected();
						boolean bootstrap = boots.isSelected();
						boolean majorule = majo.isSelected();
						boolean entr = entropy.isSelected();
						boolean exg = exgaps.isSelected();
						
						double[] ent = null;
						if( entr ) {
							ent = Sequence.entropy( currentserifier.lseq );
						}
						
						List<Integer>	idxs = null;
						if( exg ) {
							int start = Integer.MIN_VALUE;
							int end = Integer.MAX_VALUE;
							
							for( Sequence seq : currentserifier.lseq ) {
								if( seq.getRealStart() > start ) start = seq.getRealStart();
								if( seq.getRealStop() < end ) end = seq.getRealStop();
							}
							
							idxs = new ArrayList<Integer>();
							for( int x = start; x < end; x++ ) {
								boolean skip = false;
								for( Sequence seq : currentserifier.lseq ) {
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
						
						String tree = "";
						List<String>	corrInd = currentjavafasta.getNames();
						double[] corr = new double[ currentserifier.lseq.size()*currentserifier.lseq.size() ];
						Sequence.distanceMatrixNumeric( currentserifier.lseq, corr, null, false, cantor, ent, null );
						TreeUtil	tu = new TreeUtil();
						
						for( String str : corrInd ) {
							System.err.println( str );
						}
						
						Node n = tu.neighborJoin(corr, corrInd, null, false, true);
						if( bootstrap ) {
							if( majorule ) {
								Map<Set<String>,NodeSet> nmap = new HashMap<Set<String>,NodeSet>();
								for( int i = 0; i < 1000; i++ ) {
									Sequence.distanceMatrixNumeric( currentserifier.lseq, corr, idxs, true, cantor, ent, null );
									Node nn = tu.neighborJoin(corr, corrInd, null, false, false);
									
									int val = nn.getLeaveNames().size();
									if( val == 16 ) {
										int nval = nn.getLeaveNames().size();
										System.err.println( nval );
									}
									
									if( nn.getLeavesCount() != 17 ) {
										System.err.println("bah " + nn.getLeavesCount());
									}
									
									//String[] sobj = {"mt.ruber", "mt.silvanus", "o.profundus", "m.hydrothermalis"};
									//Node newnode = tu.getParent( n, new HashSet<String>( Arrays.asList( sobj ) ) );
									//tu.rerootRecur( n, newnode );
									
									tu.setLoc( 0 );
									nn.nodeCalcMap( nmap );
									
									//tu.arrange( nn, comp );
									//tu.compareTrees( tree, n, nn );
									
									//String btree = nn.toStringWoLengths();
									//System.err.println( btree );
								}
								
								n = majoRuleConsensus( tu, nmap, null, false );
							} else {
								Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
									@Override
									public int compare(Node o1, Node o2) {
										String c1 = o1.toStringWoLengths();
										String c2 = o2.toStringWoLengths();
										
										return c1.compareTo( c2 );
									}
								};
								tu.arrange( n, comp );
								tree = n.toStringWoLengths();
								
								for( int i = 0; i < 1000; i++ ) {
									Sequence.distanceMatrixNumeric( currentserifier.lseq, corr, idxs, true, cantor, ent, null );
									Node nn = tu.neighborJoin(corr, corrInd, null, false, true);
									tu.arrange( nn, comp );
									tu.compareTrees( tree, n, nn );
									
									//String btree = nn.toStringWoLengths();
									//System.err.println( btree );
								}
								tu.appendCompare( n );
							}
						}
						
						/*Map<String,String> namesMap = new HashMap<String,String>();
						namesMap.put("Chile", "Chile");
						namesMap.put("Yellowstone", "Yellowstone");
						namesMap.put("Iceland", "Iceland");
						namesMap.put("Australia", "Australia");
						namesMap.put("Oregon", "Oregon");
						namesMap.put("China", "China");
						namesMap.put("Japan", "Japan");
						namesMap.put("Bulgaria", "Bulgaria");
						tu.softReplaceNames( n, namesMap );*/
						//tu.nameParentNodes( n );
						tu.nameParentNodesMeta( n );
						tree = n.toString();
						
						boolean scc = true;
						if( tree.length() > 0 ) {
							try {
								JSObject win = JSObject.getWindow( DataTable.this );
								Object[] objs = { tree };
								System.err.println( "fuck you" + tree );
								console( "sko hey what the fuck" + tree );
								win.call("showTree", objs);
							} catch( Exception e1 ) {
								scc = false;
								console( e1.getMessage() );
								console( tree );
							}
							
							if( !scc ) {
								JTextArea	text = new JTextArea();
								text.setText( tree );
								JFrame frame = new JFrame();
								frame.setSize(800, 600);
								frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							
								JScrollPane	scrollpane = new JScrollPane( text );
								frame.add( scrollpane );
								frame.setVisible(true);
							}
						}
					}
				};
				
				//String tree = extractFasta("/thermales.fasta");
				//String distm = dist
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier, null );
				currentjavafasta = jf;
				jf.initDataStructures();
				/*Set<String> include = new HashSet<String>();
				for( Sequence seq : lseq ) {
					
				}*/
				loadAligned(jf, true, extraObjs);
			}
		});
		/*njmenu.add( new AbstractAction("NJTree w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				//String distm = dist
				
				runnable = new Runnable() {
					@Override
					public void run() {
						JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
						JCheckBox	boots = new JCheckBox("Bootstrap");
						//JCheckBox	boots = new JCheckBox("Bootstrap");
						JOptionPane.showMessageDialog( DataTable.this, new Object[] {jukes, boots} );
						boolean cantor = jukes.isSelected();
						boolean bootstrap = boots.isSelected();
						
						int start = Integer.MIN_VALUE;
						int end = Integer.MAX_VALUE;
						
						for( Sequence seq : currentjavafasta.lseq ) {
							if( seq.getRealStart() > start ) start = seq.getRealStart();
							if( seq.getRealStop() < end ) end = seq.getRealStop();
						}
						
						List<Integer>	idxs = new ArrayList<Integer>();
						for( int x = start; x < end; x++ ) {
							int i;
							boolean skip = false;
							for( Sequence seq : currentjavafasta.lseq ) {
								char c = seq.charAt( x );
								if( c != '-' && c != '.' && c == ' ' ) {
									skip = true;
									break;
								}
							}
							
							if( !skip ) {
								idxs.add( x );
							}
						}
						
						double[] corr = new double[ currentjavafasta.lseq.size()*currentjavafasta.lseq.size() ];
						Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, idxs, false, cantor, ent );
						List<String>	corrInd = currentjavafasta.getNames();
						
						TreeUtil	tu = new TreeUtil();
						Node n = tu.neighborJoin(corr, corrInd);
						
						if( bootstrap ) {
							Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
								@Override
								public int compare(Node o1, Node o2) {
									String c1 = o1.toStringWoLengths();
									String c2 = o2.toStringWoLengths();
									
									return c1.compareTo( c2 );
								}
							};
							tu.arrange( n, comp );
							String tree = n.toStringWoLengths();
							
							for( int i = 0; i < 1000; i++ ) {
								Sequence.distanceMatrixNumeric( currentjavafasta.lseq, corr, idxs, true, cantor, ent );
								Node nn = tu.neighborJoin(corr, corrInd);
								tu.arrange( nn, comp );
								tu.compareTrees( tree, n, nn );
								
								//String btree = nn.toStringWoLengths();
								//System.err.println( btree );
							}
							tu.appendCompare( n );
						}
						
						Object[] objs = { n.toString() };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("showTree", objs);	
					}
				};
				
				JavaFasta jf = new JavaFasta( DataTable.this );
				currentjavafasta = jf;
				jf.initDataStructures();
				/*Set<String> include = new HashSet<String>();
				for( Sequence seq : lseq ) {
					
				}*
				loadAligned(jf, true);
			}
		});*/
		njmenu.add( new AbstractAction("NJTree current view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox	jukes = new JCheckBox("Jukes-cantor correction");
				JCheckBox	boots = new JCheckBox("Bootstrap");
				JCheckBox	entropy = new JCheckBox("Entropy weighting");
				
				JOptionPane.showMessageDialog( DataTable.this, new Object[] {jukes, boots} );
				boolean cantor = jukes.isSelected();
				boolean bootstrap = boots.isSelected();
				boolean entr = entropy.isSelected();
				
				double[] ent = null;
				if( entr ) ent = Sequence.entropy( currentserifier.lseq );
				
				double[] corr = new double[ currentserifier.lseq.size()*currentserifier.lseq.size() ];
				Sequence.distanceMatrixNumeric( currentserifier.lseq, corr, null, false, cantor, ent, null );
				List<String>	corrInd = currentjavafasta.getNames();
				
				TreeUtil	tu = new TreeUtil();
				Node n = tu.neighborJoin(corr, corrInd, null, false, true);
				
				if( bootstrap ) {
					Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
						@Override
						public int compare(Node o1, Node o2) {
							String c1 = o1.toStringWoLengths();
							String c2 = o2.toStringWoLengths();
							
							return c1.compareTo( c2 );
						}
					};
					tu.arrange( n, comp );
					String tree = n.toStringWoLengths();
					
					for( int i = 0; i < 1000; i++ ) {
						Sequence.distanceMatrixNumeric( currentserifier.lseq, corr, null, true, cantor, ent, null );
						Node nn = tu.neighborJoin(corr, corrInd, null, false, true);
						tu.arrange( nn, comp );
						tu.compareTrees( tree, n, nn );
						
						//String btree = nn.toStringWoLengths();
						//System.err.println( btree );
					}
					tu.appendCompare( n );
				}
				
				Object[] objs = { n.toString() };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("showTree", objs);
			}
		});
		
		JMenu fasttreemenu = new JMenu("FastTree");
		popup.add( fasttreemenu );
		fasttreemenu.add( new AbstractAction("FastTree") {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*Runnable run = new Runnable() {
					Object[] objs = { "f"+tree };
					JSObject win = JSObject.getWindow( DataTable.this );
					win.call("fasttree", objs);
				}*/
				
				StringBuilder tree = extractFasta("/thermaceae_16S_aligned.fasta");
				String t1 = "f"+tree.substring(0, tree.length()/2);
				String t2 = tree.substring(tree.length()/2, tree.length());
				
				int tlen = tree.length()+1;
				JSObject win = JSObject.getWindow( DataTable.this );
				Object[] objs1 = { t1, tlen };
				win.call( "postModuleMessage", objs1 );
				Object[] objs2 = { t2, tlen };
				win.call( "postModuleMessage", objs2 );
				
				/*Object smod = win.getMember("simmiModule");
				System.err.println("about to call nacl");
				if( smod != null && smod instanceof JSObject ) {
					JSObject obj = (JSObject)smod;
					System.err.println("about to postmessage to nacl");
					obj.call("postMessage", objs);
				} else {
					System.err.println("fasttree fail");
				}*/
			}
		});
		fasttreemenu.add( new AbstractAction("FastTree w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				runnable = new Runnable() {
					public void run() {
						StringBuilder tree = currentjavafasta.getFastaWoGaps();
						Object[] objs = { "f"+tree.toString() };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("fasttree", objs);
					}
				};
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				currentjavafasta = jf;
				jf.initDataStructures();
				loadAligned(jf, true);
				//extractFastaWoGaps("/thermales.fasta", run);
			}
		});
		fasttreemenu.add( new AbstractAction("FastTree current view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fasta = currentjavafasta.getFasta( currentjavafasta.getSequences() );
				Object[] objs = { "f"+fasta.toString() };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("fasttree", objs);
			}
		});
		JMenu	dnaparsmenu = new JMenu("Dnapars");
		popup.add( dnaparsmenu );
		dnaparsmenu.add( new AbstractAction("Dnapars") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					@Override
					public void run() {
						String phy = currentjavafasta.getPhylip( true );
						Object[] objs = { "p"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);	
					}
				};
				
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				currentjavafasta = jf;
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnaparsmenu.add( new AbstractAction("Dnapars w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					@Override
					public void run() {
						currentserifier.removeGaps( currentjavafasta.getSequences() );
						String phy = currentjavafasta.getPhylip( true );
						
						Object[] objs = { "p"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);
					}
				};
				
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				currentjavafasta = jf;
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnaparsmenu.add( new AbstractAction("Dnapars current view") {
			@Override
			public void actionPerformed(ActionEvent e) {				
				String phy = currentjavafasta.getPhylip( true );
				Object[] objs = { "p"+phy };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("dnapars", objs);
			}
		});
		JMenu	dnamlmenu = new JMenu("Dnaml");
		popup.add( dnamlmenu );
		dnamlmenu.add( new AbstractAction("Dnaml") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					
					@Override
					public void run() {
						String phy = currentjavafasta.getPhylip( true );
						Object[] objs = { "c"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);
					}
				};
				
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnamlmenu.add( new AbstractAction("Dnaml w/o gaps") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String tree = extractFasta("/thermales.fasta");
				runnable = new Runnable() {
					@Override
					public void run() {
						currentserifier.removeGaps( currentjavafasta.getSequences() );
						String phy = currentjavafasta.getPhylip( true );
						
						Object[] objs = { "c"+phy };
						JSObject win = JSObject.getWindow( DataTable.this );
						win.call("dnapars", objs);
					}
				};
				
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				jf.initDataStructures();
				loadAligned(jf, true);
			}
		});
		dnamlmenu.add( new AbstractAction("Dnaml current view") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String phy = currentjavafasta.getPhylip( true );
				Object[] objs = { "c"+phy };
				JSObject win = JSObject.getWindow( DataTable.this );
				win.call("dnapars", objs);
			}
		});
		dnamlmenu.add( new AbstractAction("Show conserved species sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentserifier = new Serifier();
				final JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				JFrame frame = new JFrame();
				addSave( frame, jf );
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				conservedSpecies( jf, false );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("Show variant species sites") {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				conservedSpecies( jf, true );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("View aligned") {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				JFrame frame = new JFrame();
				addSave( frame, jf );
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				runnable = new Runnable() {
					@Override
					public void run() {
						currentjavafasta.updateView();
					}
				};
				viewAligned( jf, true );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("View unaligned") {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentserifier = new Serifier();
				JavaFasta jf = new JavaFasta( DataTable.this, currentserifier );
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.initGui(frame);
				currentjavafasta = jf;
				runnable = new Runnable() {
					@Override
					public void run() {
						currentjavafasta.updateView();
					}
				};
				viewAligned( jf, false );
				frame.setVisible(true);
			}
		});
		popup.add( new AbstractAction("Append aligned") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( currentjavafasta == null ) {
					currentserifier = new Serifier();
					currentjavafasta = new JavaFasta( DataTable.this, currentserifier );
					JFrame frame = new JFrame();
					frame.setSize(800, 600);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					currentjavafasta.initGui(frame);
					frame.setVisible(true);
				}
				viewAligned( currentjavafasta, true );
			}
		});
		/*popup.add( new AbstractAction("Show fasta") {
			@Override
			public void actionPerformed(ActionEvent e) {				
				JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
				dialog.setSize(800, 600);
				JTextArea textarea = new JTextArea();
				textarea.setDragEnabled(true);
				String fasta = extractFasta("/thermus_all_gaps.fasta");
				textarea.setText( fasta );
				
				JScrollPane	scrollpane = new JScrollPane( textarea );
				dialog.add( scrollpane );
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				dialog.setVisible( true );
			}
		});*/
		popup.addSeparator();
		popup.add( new AbstractAction("Show article") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel(r);
				if( i != -1 ) {
					Object[] str = rowList.get( i );
					String doi = (String)str[5];
					if( doi != null && doi.length() > 0 ) {
						try {
							URL url = new URL( "http://dx.doi.org/"+doi );
							DataTable.this.getAppletContext().showDocument( url );
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					} else {
						String pubmed = (String)str[6];
						try {
							URL url = new URL( "http://www.ncbi.nlm.nih.gov/pubmed/?term="+pubmed );
							DataTable.this.getAppletContext().showDocument( url );
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		popup.add( new AbstractAction("Article in new window") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				int i = table.convertRowIndexToModel(r);
				if( i != -1 ) {
					try {
						Object[] str = rowList.get( i );
						String doi = (String)str[5];
						if( doi != null && doi.length() > 0 ) {
						
							URL url = new URL( "http://dx.doi.org/"+doi );
							Desktop.getDesktop().browse( url.toURI() );
							//URL url = new URL( "http://dx.doi.org/"+doi );
							//DataTable.this.getAppletContext().showDocument( url );
						} else {
							String pubmed = (String)str[6];
							try {
								URL url = new URL( "http://www.ncbi.nlm.nih.gov/pubmed/?term="+pubmed );
								Desktop.getDesktop().browse( url.toURI() );
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							}
						}
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Selection of each") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int num = selectionOfEach();
				int[] rr = table.getSelectedRows();
				Map<String,Integer>	nameNum = new HashMap<String,Integer>();
				for( int r : rr ) {
					int rv = table.convertRowIndexToModel( r );
					Object[] obj = rowList.get( rv );
					
					String name = getFastaName(names, metas, obj);
					if( nameNum.containsKey( name ) ) {
						int nnum = nameNum.get( name );
						if( num > nnum ) nameNum.put( name, nnum+1 );
						else table.removeRowSelectionInterval(r, r);
					} else {
						nameNum.put( name, 1 );
					}
				}
			}
		});
		popup.add( new AbstractAction("Select marked") {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.removeRowSelectionInterval(0, table.getRowCount()-1);
				for( int r = 0; r < table.getRowCount(); r++ ) {
					boolean b = (Boolean)table.getValueAt(r, 11);
					if( b ) table.setRowSelectionInterval(r, r);
				}
			}
		});
		popup.add( new AbstractAction("Import selection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				// union_16S.txt
				
				FileOpenService fos = null;
				try {
		    		fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
		    	} catch( UnavailableServiceException e1 ) {
		    		fos = null;
		    	}
		    	
		        Set<String>				selection = new HashSet<String>();
		        try {
			        InputStream is = null;
				    if( fos != null ) {
				    	FileContents fc = fos.openFileDialog( null, null );
				    	if( fc != null ) is = fc.getInputStream();
				    }
				    
				    if( is == null ) {
				    	JFileChooser fc = new JFileChooser();
				    	if( fc.showOpenDialog( DataTable.this ) == JFileChooser.APPROVE_OPTION ) {
				    		is = new FileInputStream( fc.getSelectedFile() );
				    	}
				    }
					
					Reader rd = new InputStreamReader( is );
					BufferedReader 	br = new BufferedReader( rd );
					String line = br.readLine();
					while( line != null ) {
						String[] split = line.substring(1, line.length()-1).split(",");
						
						Map<String,String>		selmap = new HashMap<String,String>();
						for( String s : split ) {
							String strim = s.trim();
							if( tablemap.containsKey(strim) ) {
								Object[] obj = tablemap.get(strim);
								int tlen = (Integer)obj[4];
								String spec = (String)obj[3];
								String country = (String)obj[6];
								for( String key : namesMap.keySet() ) {
									if( country != null && country.contains(key) ) {
										country = namesMap.get( key );
										break;
									}
								}
								String specoun = spec+country;
								
								if( selmap.containsKey(specoun) ) {
									String acc = selmap.get(specoun);
									Object[] subobj = tablemap.get(acc);
									if( subobj == null ) {
										System.err.println( tablemap.size() + "  " + acc );
									} else {
										int len = (Integer)subobj[4];
										if( tlen > len ) {
											selmap.put(specoun, acc);
										}
									}
								} else {
									selmap.put(specoun, strim);
								}
							}
						}
						
						if( selmap.size() > 1 ) selmap.remove("");
						
						for( String str : selmap.keySet() ) {
							String acc = selmap.get( str );
							selection.add( acc );
						}
						//if( sel != null ) selection.add( sel );
						line = br.readLine();
					}
					br.close();
		        } catch( Exception e1 ) {
		        	e1.printStackTrace();
		        }
				
				table.removeRowSelectionInterval(0, table.getRowCount()-1);
				for( int r = 0; r < table.getRowCount(); r++ ) {
					boolean b = selection.contains( table.getValueAt(r, 1) );
					if( b ) {
						table.addRowSelectionInterval(r, r);
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( cbmi );
		
		table.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
					filterset.clear();
					updateFilter(table, filter);
				}
			}
		});
		table.setComponentPopupMenu( popup );
		
		try {
			if( cont instanceof JFrame ) {
				String res = getThermusFusion();
				loadData( res );
			} else {
				if( !load() ) {
					String res = getThermusFusion();
					loadData( res );
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		cont.add( scrollpane );
	}
	
	public void showTree( String tree ) {
		JDialog	dialog = new JDialog( SwingUtilities.getWindowAncestor( DataTable.this ) );
		dialog.setSize(800, 600);
		JTextArea textarea = new JTextArea();
		textarea.setDragEnabled(true);
		textarea.setText( tree );
		
		JScrollPane	scrollpane = new JScrollPane( textarea );
		dialog.add( scrollpane );
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		dialog.setVisible( true );
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	
	public static class StrId {
		public StrId(String teg, int len) {
			name = teg;
			this.len = len;
		}

		String name;
		int id;
		int len;
	};
	
	public static StringBuilder exportGeocode( String acc, String country, Map<String,StringBuilder> countryMap ) throws IOException {
		StringBuilder json = null;
		if( countryMap.containsKey( country ) ) {
			json = countryMap.get( country );
		}
		
		if( json == null ) {			
			json = new StringBuilder();
			
			String qcountry = country.replace(": ", " ").replace(":", " ").replace(" ", "+").replace(",+", "+").replace("Antarctica+East+Antarctica+Vostok+Glacier", "Antarctica+Vostok");
			System.err.println( qcountry );
			URL url = new URL( GEOCODE_SERVICE_URL + "/json?address="+qcountry+"&sensor=false" );
			
			InputStream is = url.openStream();
			BufferedReader bb = new BufferedReader( new InputStreamReader( is ) );
			String subline = bb.readLine();
			while( subline != null ) {
				json.append( subline + "\n" );
				subline = bb.readLine();
			}
			bb.close();
			
			countryMap.put( country, json );
		}
		
		return json;
	}
	
	public static boolean checkValid( String acc ) throws IOException {
		FileReader fr = new FileReader( "/home/sigmar/geocode/"+acc );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			if( line.contains("OVER_QUERY_LIMIT") || line.contains("ZERO_RESULTS") ) return false;
			line = br.readLine();
		}
		br.close();
		
		return true;
	}
	
	public static String fetchCoord( String acc ) throws IOException {
		String ret = null;
		
		FileReader fr = new FileReader( "/home/sigmar/geocode/"+acc );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			String trim = line.trim();
			if( trim.contains("\"location\" : {" ) ) {
				line = br.readLine();
				trim = line.trim();
				String[] ss = trim.split("[\t ]+");
				String lat = ss[ ss.length-1 ];
				line = br.readLine();
				trim = line.trim();
				ss = trim.split("[\t ]+");
				String lng = ss[ ss.length-1 ];
				
				ret = lng + "," + lat.substring(0,lat.length()-1);
				//ret = lat + lng;
				
				break;
			}
			line = br.readLine();
		}
		br.close();
		
		return ret;
	}
	
	public static Map<String,StringBuilder> loadCountryMap( Map<String,String> acm ) throws IOException {
		Map<String,StringBuilder>	ret = new HashMap<String,StringBuilder>();
		File f = new File( "/home/sigmar/geocode/" );
		File[] ff = f.listFiles();
		for( File tf : ff ) {
			String acc = tf.getName();
			if( acm.containsKey( acc ) ) {
				String country = acm.get( acc );
				
				if( country != null && country.length() > 2 && !ret.containsKey( country ) ) {
					StringBuilder json = new StringBuilder();
					BufferedReader bb = new BufferedReader( new FileReader( tf ) );
					String subline = bb.readLine();
					while( subline != null ) {
						json.append( subline + "\n" );
						subline = bb.readLine();
					}
					bb.close();
					
					if( json.indexOf("ZERO_RESULTS") == -1 && json.indexOf("OVER_QUERY_LIMIT") == -1 ) ret.put( country, json );
					else ret.put( country, null );
				}
			}
		}
		return ret;
	}
	
	public static void assigntax( BufferedReader br, BufferedWriter bw ) throws IOException {
		String line = br.readLine();
		String current = null;
		String currid = null;
		String currteg = null;
		while (line != null) {
			String trim = line.trim();
			if (trim.startsWith("Query=")) {
				String[] split = trim.substring(7).trim().split("[ ]+");
				current = split[0];
			} else if (line.startsWith("> ")) {
				String teg = line.substring(2);
				int k = teg.indexOf(' ');
				if( k != -1 ) {
					currid = teg.substring(0,k);
					teg = teg.substring(k+1);
					line = br.readLine();
					while (!line.startsWith("Length=")) {
						teg += line;
						line = br.readLine();
					}
					currteg = teg.replace(' ', '_');
					
					String[] count = currteg.split(";");
					if( count.length < 7 ) {
						String last = count[ count.length-1 ];
						for( int u = count.length; u < 7; u++ ) {
							currteg += ";"+last;
						}
					}
				} else {
					currid = teg;
					int y = teg.lastIndexOf('_');
					if( y != -1 ) teg = teg.substring(0,y);
					currteg = "Bacteria;Deinococcus-Thermus;Deinococci;Thermales;Thermaceae;Thermus;" + teg.replace("T.", "Thermus_");
				}
			} else if (trim.contains("Expect =")) {
				int k = trim.indexOf("Expect =");
				String evalstr = trim.substring( k+9 ).trim();
				//double evalue = Double.parseDouble( evalstr );
				
				bw.write( current + "\t" + currteg + "\t" + evalstr + "\t" + currid + "\n" );
			}

			line = br.readLine();
		}
		br.close();
		bw.close();
	}
	
	public static void writeObject( Object vo, StringBuilder sb, int level, int maxlevel, int maxsize ) {
		if( vo instanceof String ) {
			String vs = (String)vo;
			sb.append( "\""+vs+"\"" );
		} else if( vo instanceof ArrayList ) {
			ArrayList al = (ArrayList)vo;
			sb.append("[");
			if( al.size() < maxsize ) {
				boolean first = true;
				for( Object o : al ) {
					//String s = (String)o;
					if( !first ) sb.append(",");
					//if( level < maxlevel ) 
					writeObject( o, sb, level+1, maxlevel, maxsize );
					first = false;
				}
			}
			sb.append("]");
		} else if( vo instanceof HashMap ) {
			HashMap hm = (HashMap)vo;
			if( hm.size() < maxsize ) {
				sb.append("{");
				if( level < maxlevel ) sb.append("\n");
				boolean first = true;
				for( Object o : hm.keySet() ) {
					String s = (String)o;
					if( !first ) sb.append(",\n");
					sb.append("\""+s+"\":");
					Object svo = hm.get(o);
					//if( level < maxlevel ) 
					writeObject( svo, sb, level+1, maxlevel, maxsize );
					first = false;
				}
			}
			sb.append("}");
			if( level < maxlevel ) sb.append("\n");
		}
	}
	
	public static void saveBiomTableNashorn( Map<Object,Object> map, Path p ) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for( Object o : map.keySet() ) {
			String s = (String)o;
			if( !first ) sb.append(",");
			sb.append("\""+s+"\":");
			Object vo = map.get(o);
			writeObject( vo, sb, 1, Integer.MAX_VALUE, Integer.MAX_VALUE );
			first = false;
		}
		sb.append("}");
		
		Files.write( p, sb.toString().getBytes() );
	}
	
	public static Map<Object,Object> loadBiomTableNashorn( String biom ) throws JSONException, ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		
		Map<Object,Object> map = new HashMap<Object,Object>();
		engine.put("biom", biom);
		engine.put("jmap", map);
		
		engine.eval( 
				  "var ArrayList = Java.type('java.util.ArrayList');"
				+ "var HashMap = Java.type('java.util.HashMap');"
				+ "var arraycheck = function(arr) { "
				//+		"print( arr.constructor );"
				+ 		"if( arr instanceof Array ) {"
				+ 			"var narr = new ArrayList;"
				+ 			"for( k in arr ) { narr.add( arraycheck(arr[k]) ); };"
				+ 			"return narr;"
				+ 		"} else if( arr instanceof Object ) {"
				+ 			"var nmap = new HashMap;"
				+ 			"for( k in arr ) { nmap.put( k, arraycheck(arr[k]) ); };"
				+ 			"return nmap;"
				+ 		"}"
				//+		"if( typeof(arr) === 'string' || arr instanceof String ) print( arr );"
				+		"return arr;"
				+ "};"
				+ "var json = JSON.parse( biom );"
				+ "for (i in json) { var jsono = arraycheck(json[i]); jmap.put(i, jsono); }" );
		return map;
	}
	
	/*public static Map<Object,Object> parseMap( String json ) {
		int s = json.indexOf('"');
		int e = json.lastIndexOf('}');
		
		
	}*/
	
	public static void loadBiomTable( String biom ) throws IOException {
		int i = biom.indexOf('{');
		int e = biom.lastIndexOf('}');
		//Map<Object,Object> mobj = parseMap( biom.substring(i+1, e).trim() );
		
		/*BufferedReader reader = Files.newBufferedReader(biom);
		JsonReader jr = Json.createReader(reader);
		JsonObject jo = jr.readObject();
		jr.close();
		reader.close();
		
		for( JsonValue jv : jo.values() ) {
			ValueType vt = jv.getValueType();
			if( vt == ValueType.ARRAY ) {
				//jo.ge
			} if( vt == ValueType.STRING ) {
				System.err.println( vt );
			}
		}*/
	}
	
	public static void printArray( List aobj ) {
		for( Object o : aobj ) {
			//System.err.print( o + ":" );
			if( o instanceof HashMap ) printMap( (Map)o );
			else if( o instanceof ArrayList ) printArray( (List)o );
			else {
				if( o.toString().contains("{") ) {
					System.err.println();
				}
				System.err.print( o );
			}
			System.err.println();
		}
	}
	
	public static void printMap( Map<Object,Object> mobj ) {
		for( Object o : mobj.keySet() ) {
			Object val = mobj.get( o );
			
			if( o instanceof HashMap ) printMap( (Map)o );
			else if( o instanceof ArrayList ) printArray( (List)o );
			else {
				if( o.toString().contains("{") ) {
					System.err.println();
				}
				System.err.print( o );
			}
			
			System.err.print( ":" );
			
			if( val instanceof HashMap ) printMap( (Map)val );
			else if( val instanceof ArrayList ) printArray( (List)val );
			else {
				if( val != null && val.toString().contains("{") ) {
					System.err.println();
				}
				System.err.print( val );
			}
			
			System.err.println();
		}
	}
	
	static class Stuff implements Comparable<Stuff> {
		String 			str;
		List<String>	lstr;
		int				c;
		
		public Stuff( String str, int c ) {
			this.str = str;
			this.c = c;
		}
		
		public Stuff( List<String> lstr, int c ) {
			this.lstr = lstr;
			this.c = c;
		}
		
		@Override
		public int compareTo(Stuff o) {
			return Integer.compare(o.c, c);
		}
		
		@Override
		public String toString() {
			return c + "\t" + str;
		}
	}
	
	public static String countLevels( int val, Map<Object,Object> mobj, int maxcount, String level, String type ) throws URISyntaxException, IOException {
		String str = "['Taxa','Count'],\n";
		
		Map<List,Integer> countMap = new HashMap<List,Integer>();
		
		int total = 0;
		Map<Integer,Integer>	rowmap = new HashMap<Integer,Integer>();
		List<List> dl = (List<List>)mobj.get("data");
		for( List sl : dl ) {
			int row = ((Double)sl.get(0)).intValue();
			int count = ((Double)sl.get(2)).intValue();
			int pcount = 0;
			if( rowmap.containsKey( row ) ) pcount = rowmap.get(row);
			rowmap.put( row, pcount+count );
			total += count;
		}
		
		List<Map> l = (List<Map>)mobj.get("rows");
		int r = 0;
		for( Map m : l ) {
			Map sm = (Map)m.get("metadata");
			List sl = (List)sm.get("taxonomy");
			List lv = sl.subList( 0, Math.min(sl.size(), val) );
			
			int v = 0;
			int k = 0;
			if( rowmap.containsKey(r) ) k = rowmap.get(r);
			if( countMap.containsKey(lv) ) {
				v = countMap.get( lv );
			}
			countMap.put( lv, k+v );
			
			r++;
		};
		
		List<Stuff> s = new ArrayList<Stuff>();
		for( List lc : countMap.keySet() ) {
			s.add( new Stuff( lc.get(lc.size()-1).toString(), countMap.get(lc) ) );
		}
		Collections.sort( s );
		int ttotal = 0;
		for( int i = 0; i < Math.min(s.size(),maxcount); i++ ) {
			Stuff stuff = s.get(i);
			ttotal += stuff.c;
			str += "['"+stuff.str.replace("[", "").replace("]", "").replace(',', '_')+"',"+stuff.c+"],\n";
		}
		str += "['Other',"+ (total-ttotal) + "]";
		
		for( int i = 0; i < Math.min(s.size(),maxcount); i++ ) {
			System.out.println( (double)s.get(i).c/(double)total );
		}
		
		String urlstr = type.equals("pie") ? "/chart.html" : "/columnchart.html";
		Path path = Paths.get( String.class.getResource( urlstr ).toURI() );
		String valstr = new String( Files.readAllBytes( path ) );
		String repstr = valstr.replace("smuck", str).replace("typp", level);
		
		Path newp = new File("/Users/sigmar/Desktop/"+level+"_chart.html").toPath();
		Files.write( newp, repstr.getBytes() );
		Desktop.getDesktop().browse( newp.toUri() );
		
		return str;
	}
	
	public static String countLocation( int val, Map<Object,Object> mobj, int maxcount, String level, String type, boolean which ) throws URISyntaxException, IOException {
		String str = ""; //'Taxa','Count'],\n";
		
		List<List<String>>	rows = new ArrayList<List<String>>();
		List<String>		columns = new ArrayList<String>();
		
		List<Map> l = (List<Map>)mobj.get("columns");
		//int c = 0;
		for( Map m : l ) {
			String sm = (String)m.get("id");
			columns.add( sm );
		};
		
		int rr = 0;
		l = (List<Map>)mobj.get("rows");
		for( Map m : l ) {
			Map sm = (Map)m.get("metadata");
			List sl = (List)sm.get("taxonomy");
			rows.add( sl );
			
			if( sl.size() < 7 ) {
				System.err.println( rr );
			}
			/*List lv = sl.subList( 0, Math.min(sl.size(), val) );
			
			int v = 0;
			int k = 0;
			if( rowmap.containsKey(r) ) k = rowmap.get(r);
			if( countMap.containsKey(lv) ) {
				v = countMap.get( lv );
			}
			countMap.put( lv, k+v );
			
			r++;*/
			rr++;
		};
		
		int[] vals = new int[ rows.size()*columns.size() ];
		Arrays.fill(vals, 0);
		
		List<List> dl = (List<List>)mobj.get("data");
		for( List sl : dl ) {
			int row = ((Double)sl.get(0)).intValue();
			int column = ((Double)sl.get(1)).intValue();
			int count = ((Double)sl.get(2)).intValue();
			
			vals[row*columns.size()+column] = count;
		}
		
		Map<List<String>,Integer>	mstuffall = new HashMap<List<String>,Integer>();
		int rowval = 0;
		for( List<String> row : rows ) {
			int ival = 0;
			
			if( val <= row.size() ) {
				List<String> spec = row.subList(0, val);//.subList(0, val).toString();
				int colval = 0;
				for( String cstr : columns ) {
					ival += vals[ rowval*columns.size()+colval ];
					
					colval++;
				}
				
				int nval = 0;
				if( mstuffall.containsKey( spec ) ) {
					nval = mstuffall.get( spec );
				}
				mstuffall.put( spec, ival+nval );
			}
			
			rowval++;
		}
		
		Set<List<String>>	alllocs = new HashSet<List<String>>();
		List<Stuff>	s = new ArrayList<Stuff>();
		for( List<String> locstr : mstuffall.keySet() ) {
			s.add( new Stuff( locstr, mstuffall.get(locstr) ) );
		}
		Collections.sort( s );
		for( int i = 0; i < Math.min(s.size(),maxcount); i++ ) {
			Stuff stuff = s.get(i);
			alllocs.add( stuff.lstr );	
		}
		
		
		
		Map<String,Map<List<String>,Integer>>	stuffmap = new HashMap<String,Map<List<String>,Integer>>();
		int colval = 0;
		for( String cstr : columns ) {
			Map<List<String>,Integer>	mstuff;// = new HashMap<String,Integer>();
			
			cstr = cstr.substring( cstr.lastIndexOf('.')+1, cstr.length() );
			//cstr = cstr.substring(0, cstr.lastIndexOf('.') );
			
			if( stuffmap.containsKey( cstr ) ) mstuff = stuffmap.get( cstr );
			else {
				mstuff = new HashMap<List<String>,Integer>();
				stuffmap.put(cstr, mstuff);
			}
			
			rowval = 0;
			for( List<String>	row : rows ) {
				if( row.size() < 7 ) {
					//System.err.println( rowval + " " + row);
				} else {
					List<String> spec = row.subList(0, val);//.subList(0, val).toString();
					
					int ival = 0;
					if( mstuff.containsKey( spec ) ) {
						ival = mstuff.get( spec );
					}
					mstuff.put( spec, ival+vals[rowval*columns.size()+colval] );
				}
				
				rowval++;
			}
			
			/*List<Stuff>	ss = new ArrayList<Stuff>();
			for( String locstr : mstuff.keySet() ) {
				ss.add( new Stuff( locstr, mstuff.get(locstr) ) );
			}
			Collections.sort( ss );
			/*for( int i = 0; i < Math.min(s.size(),maxcount); i++ ) {
				Stuff stuff = s.get(i);
				alllocs.add( stuff.str );	
			}*/
			
			colval++;
		}
		
		str += "['Location'";
		for( List<String> all : alllocs ) {
			str+= ",'" + all.get(all.size()-1) + "'";
		}
		str += ",'Other'],\n";
		for( String stuffstr : stuffmap.keySet() ) {
			if( !stuffstr.equals("jardvlifm") ) {
				Map<List<String>,Integer> lstuff = stuffmap.get( stuffstr );
				
				int total = 0;
				int rest = 0;
				for( List<String> all : lstuff.keySet() ) {
					int ival = lstuff.get( all );
					total += ival;
					if( !alllocs.contains( all ) ) rest += ival;
				}
				
				str += "['"+stuffstr+"'";
				for( List<String> all : alllocs ) {
					int ival = lstuff.get( all );
					str += ","+ival/(double)total;
				}
				str += ","+rest/(double)total+"],\n";
			}
		}
		//str += "['Other',"+ (total-ttotal) + "]";
		
		/*for( int i = 0; i < Math.min(s.size(),maxcount); i++ ) {
			System.out.println( (double)s.get(i).c/(double)total );
		}*/
		
		String urlstr = type.equals("pie") ? "/chart.html" : "/columnchart.html";
		Path path = Paths.get( String.class.getResource( urlstr ).toURI() );
		String valstr = new String( Files.readAllBytes( path ) );
		String repstr = valstr.replace("smuck", str).replace("typp", level);
		
		Path newp = new File("/Users/sigmar/Desktop/"+level+"_chart.html").toPath();
		Files.write( newp, repstr.getBytes() );
		Desktop.getDesktop().browse( newp.toUri() );
		
		return str;
	}
	
	public static void countSpecies( int val, Map<Object,Object> mobj, String spec ) {
		List<List<String>>	rows = new ArrayList<List<String>>();
		List<String>		columns = new ArrayList<String>();
		
		List<Map> l = (List<Map>)mobj.get("columns");
		//int c = 0;
		for( Map m : l ) {
			String sm = (String)m.get("id");
			columns.add( sm );
		};
		
		int rr = 0;
		l = (List<Map>)mobj.get("rows");
		for( Map m : l ) {
			Map sm = (Map)m.get("metadata");
			List sl = (List)sm.get("taxonomy");
			rows.add( sl );
			rr++;
		};
		
		int[] vals = new int[ rows.size()*columns.size() ];
		Arrays.fill(vals, 0);
		
		List<List> dl = (List<List>)mobj.get("data");
		for( List sl : dl ) {
			int row = ((Double)sl.get(0)).intValue();
			int column = ((Double)sl.get(1)).intValue();
			int count = ((Double)sl.get(2)).intValue();
			
			vals[row*columns.size()+column] = count;
		}
		
		int c = 0;
		Map<String,Map<String,Integer>> locspecMap = new TreeMap<String,Map<String,Integer>>();
		for( String col : columns ) {
			col = col.substring(0, col.lastIndexOf('.'));
			//col = col.substring(col.lastIndexOf('.')+1,col.length());
			
			Map<String,Integer> map;
			if( locspecMap.containsKey( col ) ) map = locspecMap.get( col );
			else {
				map = new HashMap<String,Integer>();
				locspecMap.put(col, map);
			}
			
			int r = 0;
			for( List<String> row : rows ) {
				if( val <= row.size() ) {
					int ival = 0;
					int newval = vals[ r*columns.size()+c ];
					
					String specstr = row.subList(0, val).toString();
					if( map.containsKey(specstr) ) ival = map.get( specstr );
					map.put( specstr, newval+ival );
				}
				
				r++;
			}
			c++;
		}
		
		System.err.println( spec + " number" );
		for( String loc : locspecMap.keySet() ) {
			Map<String,Integer> imap = locspecMap.get(loc);
			int count = 0;
			for( String s : imap.keySet() ) {
				int k = imap.get(s);
				if( k > 2 ) count++;
			}
			System.err.println( loc + "\t" + count );
		}
	}
	
	static class Mapping {
		public Mapping( String name ) {
			this.name = name;
		}
		
		String	name;
		Map<String,String> mapping = new HashMap<String,String>();
	}
	
	public static void sortBiom( Map<Object, Object> map, List<Mapping> mapping, final String column, final boolean numeric ) {
		Collections.sort( mapping, new Comparator<Mapping>() {
			@Override
			public int compare(Mapping o1, Mapping o2) {
				String s1 = o1.mapping.get(column);
				String s2 = o2.mapping.get(column);
				if( numeric ) {
					float f1 = -1.0f;
					try {
						f1 = Float.parseFloat(s1);
					} catch( Exception e ) {}
					
					float f2 = -1.0f;
					try {
						f2 = Float.parseFloat(s2);
					} catch( Exception e ) {}
					
					return Float.compare(f1, f2);
				} else {
					return s1.compareTo(s2);
				}
			}
		});
	}
	
	public static List<Mapping> loadMapping( Path p ) throws IOException {
		List<Mapping> ml = new ArrayList<Mapping>();
		
		BufferedReader br = Files.newBufferedReader(p);
		String line = br.readLine();
		String[] split = line.split("\t");
		line = br.readLine();
		while( line != null ) {
			String[] ssplit = line.split("\t");
			Mapping m = new Mapping( ssplit[0] );
			for( int i = 1; i < ssplit.length; i++ ) {
				m.mapping.put( split[i], ssplit[i] );
			}
			line = br.readLine();
		}
		br.close();
		
		return ml;
	}
	
	public static void main(String[] args) {
		try {
			Path p = new File("/Users/sigmar/otu_table.biom").toPath();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Files.copy(p, baos);
			baos.close();
			String biom = baos.toString();
			Map<Object, Object> mobj = loadBiomTableNashorn( biom );
			
			StringBuilder sb = new StringBuilder();
			writeObject( mobj, sb, 0, 1, Integer.MAX_VALUE );
			System.err.println( sb.toString() );
			
			Path np = new File("/Users/sigmar/new_otu_table.biom").toPath();
			saveBiomTableNashorn( mobj, np );
			
			/*String type = "Column";
			
			countSpecies( 2, mobj, "Phylum" );
			
			countLocation( 7, mobj, 12, "Species", type, true );
			countLocation( 6, mobj, 12, "Genus", type, true );
			countLocation( 5, mobj, 12, "Family", type, true );
			countLocation( 4, mobj, 12, "Order", type, true );
			countLocation( 3, mobj, 12, "Class", type, true );
			countLocation( 2, mobj, 12, "Phylum", type, true );
			
			/*countLevels( 7, mobj, 12, "Species", type );
			countLevels( 6, mobj, 12, "Genus", type );
			countLevels( 5, mobj, 12, "Family", type );
			countLevels( 4, mobj, 12, "Order", type );
			countLevels( 3, mobj, 12, "Class", type );
			countLevels( 2, mobj, 12, "Phylum", type );*/
			
			/*for( Object os : mobj.keySet() ) {
				String ostr = os.toString();
				String mstr = mobj.get(os).toString();
				System.err.println( ostr + ":" + mstr.substring(0, Math.min(mstr.length(),1000)) );
			}*/
			//printMap( mobj );
			
			// br = Files.newBufferedReader( new File("/Users/sigmar/rep_set.blastout").toPath() ); //new BufferedReader( new InputStreamReader( new GZIPInputStream( Files.newInputStream( new File("/Users/sigmar/rep_set.blastout").toPath() ) ) ) );
			//BufferedWriter bw = Files.newBufferedWriter( new File("/Users/sigmar/res.txt").toPath() );
			//assigntax( br, bw );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}/* catch (URISyntaxException e) {
			e.printStackTrace();
		}*/
		
		/*JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		DataTable	dt = new DataTable();
		dt.initGUI( frame );
		frame.setVisible( true );*/
	}
	
	public static void main_older(String[] args) {
		try {
			FileReader fr = new FileReader( "/home/sigmar/Downloads/Thermus_16S_aligned.csv" );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			line = br.readLine();
			
			FileWriter fw = new FileWriter("/home/sigmar/kml.kml");
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			fw.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
			fw.write("<Document>");
			Map<String,Set<String>>		accsetMap = new HashMap<String,Set<String>>();
			Map<String,String>			accountryMap = new HashMap<String,String>();
			Map<String,String>			accspecMap = new HashMap<String,String>();
			//int count = 0;
			while( line != null ) {
				String[] split = csvSplit( line ); //line.split(",");
				String acc = split[1];
				String spec = split[3];
				String country = split[6];
				Set<String>	accset;
				if( accsetMap.containsKey( country ) ) {
					accset = accsetMap.get( country );
				} else {
					accset = new HashSet<String>();
					accsetMap.put( country, accset );
				}
				accset.add( acc );
				
				accountryMap.put( acc, country );
				accspecMap.put( acc, spec );
				
				line = br.readLine();
			}
			br.close();
				
			Map<String,StringBuilder>	countryMap = loadCountryMap( accountryMap );
			for( String acc : accountryMap.keySet() ) {
				String country = accountryMap.get( acc );
				if( country.length() > 2 ) {
					if( countryMap.get( country ) == null ) {
					//if( !checkValid( acc ) ) {
						//int i = country.indexOf(':');
						//if( i > 0 ) country = country.substring(0,i).trim()+" Gotthard";
						StringBuilder json = exportGeocode( acc, country, countryMap );
						if( json.indexOf("ZERO_RESULTS") == -1 && json.indexOf("OVER_QUERY_LIMIT") == -1 ) {
							System.err.println( "succ: "+acc );
							System.err.println( "succ: "+country );
							//System.err.println( "succ: "+json );
							for( String accs : accsetMap.get(country) ) {
								//System.err.println( "succsub: "+accs );
								FileWriter fwo = new FileWriter( "/home/sigmar/geocode/"+accs );
								fwo.write( json.toString() );
								fwo.close();
							}
						} else {
							System.err.println( "fail: "+country );
							System.err.println( "fail: "+json );
							
							break;
						}
						
						//break;
						/*try {
							Thread.sleep(900);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
					}
					//exportGeocode( split[1], split[6], countryMap );
					String coord = fetchCoord( acc );
					String spec = accspecMap.get( acc );
					if( coord != null && coord.length() > 0 ) {
						fw.write("<Placemark>\n");
						fw.write("<name>"+spec+"</name>\n");
						fw.write("<description>"+country+" "+acc+"</description>\n");
						fw.write("<Point>\n");
						fw.write("<coordinates>"+coord+"</coordinates>\n");
						fw.write("</Point>\n");
						fw.write("</Placemark>\n");
					}
				}
				
				//System.err.println( split[1] + "  " + split[6] );
				
				//count++;
				
				//if( count == 50 ) break;
				//break;
			}
			fw.write("</Document>\n");
			fw.write("</kml>\n");
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*File f = new File("/home/sigmar/sim.newick");
		try {
			char[] cbuf = new char[(int)f.length()];
			FileReader fr = new FileReader(f);
			int r = fr.read(cbuf);
			String str = new String( cbuf );
			String tree = str.replaceAll("[\r\n]+", "");
			TreeUtil	treeutil = new TreeUtil( tree, false, null, null, false, null, null, false );
			Node n = treeutil.getNode();
			String thetree = n.toString();
			FileWriter fw = new FileWriter("/home/sigmar/fw.newick");
			fw.write( thetree );
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			service.setUserCredentials("signinhelpdesk@gmail.com", "vid-311.hald", ClientLoginAccountType.GOOGLE);
			
			//String ret = run("select acc from "+oldtabhleid+" where country like '%hile%' and species like '%filiform%'", true);
			String ret = run("select acc, country from "+oldtableid+" where len(country) > 1", true);
			String[] split = ret.split("\n");
			Map<String,String>	oldids = new HashMap<String,String>();
			for( int i = 1; i < split.length; i++ ) {
				String s = split[i];
				int val = s.indexOf(',');
				if( val != -1 ) {
					oldids.put( s.substring(0, val), s.substring(val+1, s.length()) );
				} else {
					oldids.put( s, null );
				}
			}
			/*System.err.println( oldids.size() );
			System.err.println( oldids.keySet() );*
			
			ret = run("select acc, rowid from "+tableid+" where len(country) < 2", true);
			split = ret.split("\n");
			HashMap<String,String>	newids = new HashMap<String,String>();
			for( String s : split ) {
				int val = s.indexOf(',');
				newids.put( s.substring(0, val), s.substring(val+1, s.length()) );
			}
			
			newids.keySet().retainAll( oldids.keySet() );
			
			for( String id : newids.keySet() ) {
				String rowid = newids.get(id);
				String country = oldids.get(id).replace("\"", "");
				//System.err.println( id + "\t" + oldids.get(id) );
				run( "update "+tableid+" set country = '"+country+"' where rowid = '"+rowid+"'", true );
			}
			
			/*for( int i = 1; i < split.length; i++ ) {
				String row = split[i];
				//String[] subsplit = row.split(",");
				//int ident = Integer.parseInt( subsplit[1] );
				run( "update "+tableid+" set country = 'USA:Yellowstone' where rowid = '"+row+"'", true );
			}
			/*String ret = run("select rowid from "+tableid+" where name = 'Unl042jm'", true);
			System.err.println( ret );
			String[] lines = ret.split("\n");
			run("update "+tableid+" set species = 'Thermus antranikianii strain HN3-7 16S ribosomal RNA, partialsequence' where rowid = '"+lines[1]+"'", true);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void main_old(String[] args) {
		Map<String, StrId> tegmap = new HashMap<String, StrId>();
		//Map<String,String>	rowidmap = new HashMap<String,String>();
		
		try {
			FileReader fr = new FileReader("/home/sigmar/thermus16S_all.blastout");
			//FileReader fr = new FileReader("/home/sigmar/newthermus16S.blastout");
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String current = null;
			StrId currteg = null;
			int currlen = 0;
			boolean done = false;
			while (line != null) {
				String trim = line.trim();
				if (trim.startsWith("Query=")) {
					String[] split = trim.substring(7).trim().split("[ ]+");
					current = split[0];
					done = false;
				} else if (trim.startsWith("Length=")) {
					currlen = Integer.parseInt(trim.substring(7).trim());
				} else if (line.startsWith(">") && !done) {
					int i = line.lastIndexOf('|');
					if (i == -1)
						i = 0;
					String teg = line.substring(i + 1).trim();
					line = br.readLine();
					while (!line.startsWith("Length")) {
						teg += line;
						line = br.readLine();
					}
					//if (teg.contains("Thermus") || teg.startsWith("t.")) {
					currteg = new StrId(teg, currlen);
					tegmap.put(current, currteg);
					//}
				} else if (trim.startsWith("Ident") && !done) {
					if( currteg != null ) {
						int sv = trim.indexOf('(');
						int svl = trim.indexOf('%', sv + 1);
	
						String trimsub = trim.substring(sv + 1, svl);
						currteg.id = Integer.parseInt( trimsub );
					}
					done = true;
				}

				line = br.readLine();
			}
			fr.close();
			
			service = new GoogleService("fusiontables", "fusiontables.ApiExample");
			//service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
		
			//String ret = run("select name, rowid from "+tableid+" where name like 't.spCCB%'", true);
			String ret = run("select name, rowid from "+tableid, true);
			String[] lines = ret.split("\n");
			for( int i = 1; i < lines.length; i++ ) {
				line = lines[i];
				int comma = line.indexOf(',');
				String name = line.substring(0, comma);
				String rowid = line.substring( comma+1 );
				if( tegmap.containsKey(name) ) {
					StrId species = tegmap.get(name);
					//System.err.println( i + "  " + name + "  " + species.id + " of " + lines.length );
					run("update "+tableid+" set species = '"+species.name+"', ident = '"+species.id+"', len = '"+species.len+"' where rowid = '"+rowid+"'", true);
				} else {
					System.err.println( "fail "+name );
				}
				//rowidmap.put( name, rowid );
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		//System.err.println( tegmap.size() );
	}
}
