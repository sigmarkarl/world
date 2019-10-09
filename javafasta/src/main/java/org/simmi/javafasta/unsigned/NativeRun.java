package org.simmi.javafasta.unsigned;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javafx.scene.layout.Pane;

public class NativeRun {
	public Runnable 			run;
	public Pane					pane;
	public Container			cnt = null;
	
	public NativeRun() {}
	public NativeRun( Runnable run ) {
		setRun( run );
	}
	
	public void setRun( Runnable run ) {
		this.run = run;
	}
	
	public Path checkProdigalInstall( Path dir, List<Path> urls ) throws IOException, URISyntaxException {
		Path check1 = dir.resolve( "prodigal.v2_60.windows.exe" );
		Path check2 = dir.resolve( "prodigal.v2_60.linux" );
		Path check;
		if( !Files.exists(check1) && !Files.exists(check2) ) {
			check = installProdigal( dir, urls );
		} else check = Files.exists(check1) ? check1 : check2;
		
		return check;
	}
	
	public void checkInstall( File dir ) throws IOException {
		File check1 = new File( "/opt/ncbi-blast-2.2.28+/bin/blastp" );
		File check2 = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\blastp.exe" );
		if( !check1.exists() && !check2.exists() ) {
			File f = installBlast( dir );
		}
	}
	
	public File installBlast( final File homedir ) throws IOException {
		final URL url = new URL("ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.2.28/ncbi-blast-2.2.28+-win32.exe");
		String fileurl = url.getFile();
		String[] split = fileurl.split("/");
		
		final File f = new File( homedir, split[split.length-1] );
		final ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		final byte[] bb = new byte[100000];
		if( !f.exists() ) {
			final JDialog		dialog = new JDialog();
			final JProgressBar	pbar = new JProgressBar();
			Runnable run = new Runnable() {
				boolean interrupted = false;
				
				public void run() {
					dialog.addWindowListener( new WindowListener() {
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
							interrupted = true;
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );

					try {
						InputStream is = url.openStream();
						int r = is.read(bb);
						while( r > 0 && !interrupted ) {
							baos.write( bb, 0, r );
							r = is.read( bb );
						}
						is.close();
						//f.mkdirs();
						
						if( !interrupted ) {
							FileOutputStream fos = new FileOutputStream( f );
							fos.write( baos.toByteArray() );
							fos.close();
							baos.close();
							
							byte[] bb = new byte[100000];
							
							String path = f.getAbsolutePath();
							//String[] cmds = new String[] { "wine", path };
							ProcessBuilder pb = new ProcessBuilder( path );
							pb.directory( homedir );
							Process p = pb.start();
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							is = p.getInputStream();
							r = is.read(bb);
							while( r > 0 ) {
								baos.write( bb, 0, r );
								r = is.read( bb );
							}
							is.close();
							
							is = p.getErrorStream();
							r = is.read(bb);
							while( r > 0 ) {
								baos.write( bb, 0, r );
								r = is.read( bb );
							}
							is.close();
							
							System.out.println( "erm " + baos.toString() );
							baos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			runProcess( "Downloading blast...", run, dialog, pbar );
		}
		
		return f;
	}
	
	public boolean startProcess( Object commands, List<Object> commandsList, Path workingdir, Object input, Object output, final Appendable ta, boolean paralell ) {
		//Object commands = commandsList.get( w );
		boolean blist = commands instanceof List;
		List<String> lcmd = (List)(blist ? commands : commandsList.get((Integer)commands));
		
		int k = lcmd.indexOf(";");
		while( k != -1 ) {	
			//runProcess(lcmd.subList(0, k), workingdir, input, output, ta);
			//runProcess(lcmd.subList(k+1,lcmd.size()), workingdir, input, output, ta);
		
			runProcess( lcmd.subList(0, k), workingdir, input, output, ta );
			lcmd = lcmd.subList(k+1,lcmd.size());
			
			k = lcmd.indexOf(";");
		}
		if( lcmd.size() > 0 ) runProcess(lcmd, workingdir, input, output, ta);

		return blist;
	}
	
	public void runProcess( List lcmd, Path workingdir, Object input, Object output, final Appendable ta ) {
		//String path = System.getenv("PATH");
		//if( path == null || !path.contains("/usr/local/bin") ) System.getenv().put("PATH", (path != null && path.length()>0 ? path+":" : "")+"/usr/local/bin");
		ProcessBuilder pb = new ProcessBuilder( lcmd );
		
		//System.err.println( pb.environment() );
		//System.err.println( System.getenv() );
		//pb.environment().putAll( System.getenv() );
		//pb.environment().put("PATH", pb.environment().get("PATH")+":/usr/local/bin");
		//pb.environment().put("PYTHONPATH", "/Users/sigmar/antiSMASH2/python/Lib/site-packages");
		//pb.environment().put("PATH", pb.environment().get("PATH")+";c:/cygwin64/bin");
		//pb.environment().put("PATH", "c:\\cygwin64\\bin");
		//System.err.println( pb.environment() );
		if( workingdir != null ) {
			pb.directory( workingdir.toFile() );
		}
		
		Process fp = null;
		//pb.redirectErrorStream( true );
		try {
			final Process p = pb.start();
			final OutputStream os = p.getOutputStream();
			fp = p;
			if( input != null ) {
				if( input instanceof Path ) {
					final Path inp = (Path)input;
					new Thread(() -> {
                        try {
                            OutputStream os1 = p.getOutputStream();
                            //os.write( "simmi".getBytes() );
                            Files.copy(inp, os1);
                            os1.close();
                        } catch( Exception e ) {
                            e.printStackTrace();
                        }
                    }).start();
				} else {
					final byte[] binput = (byte[])input;
					new Thread(() -> {
                        try {
                            os.write( binput );
                            os.close();
                        } catch( Exception e ) {
                            e.printStackTrace();
                        }
                    }).start();
				}
			}
			
			new Thread(() -> {
                try {
                    InputStream os12 = p.getErrorStream();
                    BufferedReader br = new BufferedReader( new InputStreamReader(os12) );
                    String line = br.readLine();
                    while( line != null ) {
                        //while( os.read() != -1 ) ;
                        //os.write( binput );
                        if( ta != null ) ta.append( line+"\n" );
                        else System.err.println( line );

                        line = br.readLine();
                    }
                    os12.close();
                } catch( Exception e ) {
                    e.printStackTrace();
                }
            }).start();
			
			if( output != null ) {
				if( output instanceof Path ) {
					Path outp = (Path)output;
					try {
						InputStream is = p.getInputStream();
						if( outp.getFileName().toString().endsWith(".gz") ) {
							OutputStream oos = Files.newOutputStream( outp );
							GZIPOutputStream gos = new GZIPOutputStream( oos );
							
							int b = is.read();
							while( b != -1 ) {
								gos.write(b);
								b = is.read();
							}
							gos.close();
							os.close();
						} else {
							if( Files.exists(outp) && outp.toString().contains("trnas.txt") ) {
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								int r = is.read();
								while( r != -1 ) {
									baos.write(r);
									r = is.read();
								}
								Files.write(outp, baos.toByteArray(), StandardOpenOption.APPEND);
							} else {
								//Path tmp = Files.createTempFile("res", ".blastout");
								//Files.copy(is, tmp);
								//Files.copy(is, outp, StandardCopyOption.REPLACE_EXISTING);
								
								BufferedWriter bw = Files.newBufferedWriter(outp, StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
								InputStreamReader isr = new InputStreamReader( is );
								BufferedReader br = new BufferedReader( isr );
								String line = br.readLine();
								int k = 0;
								while( line != null ) {
									k++;
									if( k % 10000 == 0 ) {
										String str = k + " lines done";
										if( ta != null && k <= 10000000 ) ta.append( str+"\n" );
										else System.out.println( str );
									}
									bw.write(line+"\n");
									line = br.readLine();
								}
								bw.close();
								
								//Files.copy(tmp, outp, StandardCopyOption.REPLACE_EXISTING);
							}
						}
						is.close();
					} catch( Exception e ) {
						e.printStackTrace();
					}
				} else {
					final OutputStream bout = (OutputStream)output;
					
					try {
						InputStream is = p.getInputStream();
						//is.read( outp );
						BufferedReader br = new BufferedReader( new InputStreamReader(is) );
						String line = br.readLine();
						int k = 0;
						while( line != null ) {
							String str = line + "\n";
							bout.write( str.getBytes() );
							
							k++;
							if( k % 100 == 0 ) {
								str = k + " lines done";
								if( ta != null ) ta.append( str+"\n" );
								else System.out.println( str );
							}
							
							line = br.readLine();
						}
						br.close();
						
						is.close();
					} catch( Exception e ) {
						e.printStackTrace();
					}
					
					bout.close();
				}
			} else {
				try {
					InputStream is = p.getInputStream();
					BufferedReader br = new BufferedReader( new InputStreamReader(is) );
					String line = br.readLine();
					while( line != null ) {
						if( ta != null ) ta.append( line+"\n" );
						else System.out.println( line );
						
						line = br.readLine();
					}
					br.close();
					is.close();
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if( fp != null ) {
			try {
				fp.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*boolean blist = commands instanceof List;
		List<String> lcmd = blist ? (List)commands : commandsList;
		/*for( String s : lcmd ) {
			System.err.println( s );
		}*
		ProcessBuilder pb = new ProcessBuilder( lcmd );
		//pb.environment().putAll( System.getenv() );
		//System.err.println( pb.environment() );
		if( workingdir != null ) {
			//System.err.println( "blblblbl " + workingdir.toFile() );
			pb.directory( workingdir.toFile() );
		}
		//pb.redirectErrorStream( true );
		final Process p = pb.start();
		dialog.addWindowListener( new WindowListener() {
			
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
				if( p != null ) {
					interupted = true;
					p.destroy();
					//tt.interrupt();
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		dialog.setVisible( true );
		
		if( input != null ) {
			if( input instanceof Path ) {
				final Path inp = (Path)input;
				new Thread() {
					public void run() {
						try {
							OutputStream os = p.getOutputStream();
							//os.write( "simmi".getBytes() );
							Files.copy(inp, os);
							os.close();
						} catch( Exception e ) {
							e.printStackTrace();
						}
					}
				}.start();
			} else {
				final byte[] binput = (byte[])input;
				new Thread() {
					public void run() {
						try {
							OutputStream os = p.getOutputStream();
							os.write( binput );
							os.close();
						} catch( Exception e ) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		}
		
		if( output != null ) {
			//if( output instanceof Path ) {
				Path outp = (Path)output;
				try {
					InputStream is = p.getInputStream();
					if( outp.getFileName().toString().endsWith(".gz") ) is = new GZIPInputStream( is );
					Files.copy(is, outp, StandardCopyOption.REPLACE_EXISTING);
					is.close();
				} catch( Exception e ) {
					e.printStackTrace();
				}
			/*} else {
				final byte[] bout = (byte[])output;
				
				try {
					InputStream is = p.getInputStream();
					is.read( outp );
					is.close();
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}*
		} else {
			try {
				InputStream is = p.getInputStream();
				BufferedReader br = new BufferedReader( new InputStreamReader(is) );
				String line = br.readLine();
				while( line != null ) {
					String str = line + "\n";
					ta.append( str );
					
					line = br.readLine();
				}
				br.close();
				is.close();
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}*/
	}
	
	int tcount;
	int ttotal;
	int where = 0;
	int waiting = 0;
	boolean ginterupted = false;
	Object inp = null;
	Object outp = null;
	Path wdir = null;
	public void runProcessBuilder( String title, @SuppressWarnings("rawtypes") final List commandsList, final Object[] cont, boolean paralell, Runnable trun, boolean headless ) throws IOException {
		if( !headless ) {
			SwingUtilities.invokeLater(() -> {
				final JDialog		dialog;
				final JProgressBar	pbar;
				final JTextArea		ta;
				final Appendable	append;

				dialog = new JDialog();
				dialog.setTitle( title );
				dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				dialog.setSize(400, 300);

				JComponent comp = new JComponent() {};
				comp.setLayout( new BorderLayout() );

				ta = new JTextArea();
				for( Object commands : commandsList ) {
					if( commands instanceof List ) {
						for( Object cmd : (List)commands ) {
							ta.append(cmd+" ");
						}
						ta.append("\n");
					} else {
						ta.append(commands+" ");
					}
				}
				ta.append("\n");

				ta.setEditable( false );
				final JScrollPane	sp = new JScrollPane( ta );
				pbar = new JProgressBar();

				dialog.add( comp );
				comp.add( pbar, BorderLayout.NORTH );
				comp.add( sp, BorderLayout.CENTER );
				pbar.setIndeterminate( true );

				append = new Appendable() {
					@Override
					public Appendable append(CharSequence csq) throws IOException {
						ta.append(csq.toString());
						return this;
					}

					@Override
					public Appendable append(CharSequence csq, int start, int end) throws IOException {
						ta.append(csq.subSequence(start,end).toString());
						return this;
					}

					@Override
					public Appendable append(char c) throws IOException {
						ta.append(Character.toString(c));
						return this;
					}
				};

				System.err.println( "about to run" );
				for( Object commands : commandsList ) {
					if( commands instanceof List ) {
						for( Object c : (List)commands ) {
							System.err.print( c+" " );
						}
						System.err.println();
					} else {
						System.err.print( commands+" " );
					}
				}
				System.err.println();

				if( paralell ) {
					//where = 0;
					final int ncpu = Runtime.getRuntime().availableProcessors();

					final ExecutorService es = Executors.newFixedThreadPool( ncpu );
					//int usedCpus = 0;

					dialog.addWindowListener( new WindowListener() {
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
							es.shutdownNow();

							if( pbar.isEnabled() ) {
								String result = ta != null ? ta.getText().trim() : "";
								if( trun != null ) {
									cont[0] = null;
									cont[1] = result;
									cont[2] = new Date( System.currentTimeMillis() ).toString();
									trun.run();
								}

								if( !headless ) {
									pbar.setIndeterminate( false );
									pbar.setEnabled( false );
								}
							}
						}

						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );

					for( final Object commands : commandsList ) {
						//final Object commands = commandsList.get( where );
						if( commands instanceof Path[] ) {
							Path[] pp = (Path[])commands;
							inp = pp[0];
							outp = pp[1];
							wdir = pp[2];
						} else if( commands instanceof Object[] ) {
							Object[] pp = (Object[])commands;
							inp = pp[0];
							outp = pp[1];
							wdir = (Path)pp[2];
						} else {
							ttotal++;

							final Object input = inp;
							final Object output = outp;
							final Path workingdir = wdir;

							inp = null;
							outp = null;
							wdir = null;

							final Runnable runnable = () -> {
                                startProcess(commands, commandsList, workingdir, input, output, append, true);

                                if( ++tcount == ttotal ) {
                                    String result = ta != null ? ta.getText().trim() : "";
                                    if( trun != null ) {
                                        cont[0] = null;
                                        cont[1] = result;
                                        cont[2] = new Date( System.currentTimeMillis() ).toString();
                                        trun.run();
                                    }

                                    if( !headless ) {
                                        pbar.setIndeterminate( false );
                                        pbar.setEnabled( false );
                                    }
                                }
                            };
							es.execute( runnable );
						}
					}
					es.shutdown();
					//es.
				} else {
					Runnable runnable = new Runnable() {
						boolean interupted = false;

						@Override
						public void run() {
							where = 0;
							for( Object commands : commandsList ) {
								if( commands instanceof Path[] ) {
									Path[] pp = (Path[])commands;
									inp = pp[0];
									outp = pp[1];
									wdir = pp[2];
								} else if( commands instanceof Object[] ) {
									Object[] pp = (Object[])commands;
									inp = pp[0];
									outp = pp[1];
									wdir = (Path)pp[2];
								} else {
									final Object input = inp;
									final Object output = outp;
									final Path workingdir = wdir;

									inp = null;
									outp = null;
									wdir = null;

									//int k = commandsList.indexOf(";");
									//if( k != -1 ) {
									//	startProcess( where, commandsList.subList(0, k), workingdir, input, output, ta, false );
									//	startProcess( where, commandsList.subList(k+1, commandsList.size()), workingdir, input, output, ta, false );
									//} else {
									boolean blist = startProcess( where, commandsList, workingdir, input, output, append, false );
									//}

									//if( !blist ) break;
								}
								where++;
							}
							//dialog.setVisible( true );

                        /*System.err.println("hereok");

                        is = p.getErrorStream();
                        br = new BufferedReader( new InputStreamReader(is) );

                        line = br.readLine();
                        while( line != null ) {
                            String str = line + "\n";
                            ta.append( str );

                            System.err.println("hereerm " + line);

                            line = br.readLine();
                        }
                        br.close();
                        is.close();

                        System.err.println("here");*/

							System.err.println("run "+trun);
							String result = ta != null ? ta.getText().trim() : "";
							if( trun != null ) {
								cont[0] = interupted ? null : "";
								cont[1] = result;
								cont[2] = new Date( System.currentTimeMillis() ).toString();
								trun.run();
							}

							if( !headless ) {
								pbar.setIndeterminate( false );
								pbar.setEnabled( false );
							}
						}
					};
					final Thread trd = new Thread( runnable );

					dialog.addWindowListener( new WindowListener() {
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
							trd.interrupt();
						}

						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );

					trd.start();
				}
			});
		} else {
			Appendable append = new StringWriter();
			if( paralell ) {
				final int ncpu = Runtime.getRuntime().availableProcessors();
				final ExecutorService es = Executors.newFixedThreadPool( ncpu );

				for( final Object commands : commandsList ) {
					//final Object commands = commandsList.get( where );
					if( commands instanceof Path[] ) {
						Path[] pp = (Path[])commands;
						inp = pp[0];
						outp = pp[1];
						wdir = pp[2];
					} else if( commands instanceof Object[] ) {
						Object[] pp = (Object[])commands;
						inp = pp[0];
						outp = pp[1];
						wdir = (Path)pp[2];
					} else {
						ttotal++;

						final Object input = inp;
						final Object output = outp;
						final Path workingdir = wdir;

						inp = null;
						outp = null;
						wdir = null;

						final Runnable runnable = () -> {
                            startProcess( commands, commandsList, workingdir, input, output, append, true );

                            if( ++tcount == ttotal ) {
                                String result = append != null ? append.toString().trim() : "";
                                if( trun != null ) {
                                    cont[0] = null;
                                    cont[1] = result;
                                    cont[2] = new Date( System.currentTimeMillis() ).toString();
                                    trun.run();
                                }
                            }
                        };
						es.execute( runnable );
					}
				}
				es.shutdown();
				//es.
			} else {
				Runnable runnable = new Runnable() {
					boolean interupted = false;

					@Override
					public void run() {
						where = 0;
						for( Object commands : commandsList ) {
							if( commands instanceof Path[] ) {
								Path[] pp = (Path[])commands;
								inp = pp[0];
								outp = pp[1];
								wdir = pp[2];
							} else if( commands instanceof Object[] ) {
								Object[] pp = (Object[])commands;
								inp = pp[0];
								outp = pp[1];
								wdir = (Path)pp[2];
							} else {
								final Object input = inp;
								final Object output = outp;
								final Path workingdir = wdir;

								inp = null;
								outp = null;
								wdir = null;

								boolean blist = startProcess( where, commandsList, workingdir, input, output, append, false );
							}
							where++;
						}
						System.err.println("run "+trun);
						String result = append != null ? append.toString().trim() : "";
						if( trun != null ) {
							cont[0] = interupted ? null : "";
							cont[1] = result;
							cont[2] = new Date( System.currentTimeMillis() ).toString();
							trun.run();
						}

						System.err.println( append.toString() );
					}
				};
				final Thread trd = new Thread( runnable );
				trd.start();
			}
		}
	}
	
	public static String cygPath( String path ) {
		if( path.contains(":") ) return "/cygdrive/"+path.substring(0, 1).toLowerCase()+path.substring(2, path.length()).replace('\\', '/');
		return path;
	}
	
	public static String fixPath( String path ) {
		String[] split = path.split("\\\\");
		String res = "";
		for( String s : split ) {
			if( s == split[0] ) res += s;
			else if( s.contains(" ") ) {
				s = s.replace(" ", "");
				if( s.length() > 8 ) {
					res += "\\"+s.substring(0, 6)+"~1";
				} else {
					res += "\\"+s;
				}
			} else {
				res += "\\"+s;
			}
		}
		
		return res;
	}
	
	public Path installProdigal( final Path homedir, final List<Path> urls ) throws IOException, URISyntaxException {
		final Path url = Paths.get( new URL("http://prodigal.googlecode.com/files/prodigal.v2_60.windows.exe").toURI() );
		String fileurl = url.getFileName().toString();
		String[] split = fileurl.split("/");
		
		final Path f = homedir.resolve( split[split.length-1] );
		if( !Files.exists(f) ) {
			final JDialog dialog;
			java.awt.Window window = SwingUtilities.windowForComponent(null);//cnt);
			if( window != null ) dialog = new JDialog( window );
			else dialog = new JDialog();
			final JProgressBar	pbar = new JProgressBar();
			
			Runnable run = new Runnable() {
				boolean interrupted = false;
				
				public void run() {
					dialog.addWindowListener( new WindowListener() {
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
							interrupted = true;
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );
					
					try {
						/*byte[] bb = new byte[100000];
						ByteArrayOutputStream	baos = new ByteArrayOutputStream();
						InputStream is = url.openStream();
						int r = is.read(bb);
						while( r > 0 ) {
							baos.write( bb, 0, r );
							r = is.read( bb );
						}
						is.close();
						//f.mkdirs();
						FileOutputStream fos = new FileOutputStream( f );
						fos.write( baos.toByteArray() );
						fos.close();
						baos.close();*/
						
						Files.copy( url, f );
						
						// ermermerm doProdigal(homedir, NativeRun.this.cnt, f, urls);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			};
			runProcess( "Downloading Prodigal ...", run, dialog, pbar );
			
			/*JProgressBar pb = new JProgressBar();
			pb.setIndeterminate( true );
			
			dialog.setTitle("Downloading Prodigal ...");
			dialog.add( pb );
			dialog.setVisible( true );
			
			InputStream is = url.openStream();
			int r = is.read(bb);
			while( r > 0 ) {
				baos.write( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			//f.mkdirs();
			FileOutputStream fos = new FileOutputStream( f );
			fos.write( baos.toByteArray() );
			fos.close();
			baos.close();
			
			dialog.setVisible( false );*/
			
			return null;
		}
		
		return f;
	}
	
	public void runProcess( String title, Runnable run, JDialog dialog, JProgressBar pbar ) {
		dialog.setTitle( title );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.setSize(400, 300);
		
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		
		final JTextArea		ta = new JTextArea();
		ta.setEditable( false );
		final JScrollPane	sp = new JScrollPane( ta );
		
		dialog.add( comp );
		comp.add( pbar, BorderLayout.NORTH );
		comp.add( sp, BorderLayout.CENTER );
		pbar.setIndeterminate( true );
		
		Thread thread = new Thread( run );
		thread.start();
	}
}
