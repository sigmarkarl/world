package org.simmi.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simmi.shared.TreeUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;
import com.google.gwt.user.datepicker.client.MonthSelector;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Frislbok implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private final FrislbokServiceAsync frislbokService = GWT.create(FrislbokService.class);

	public class DatePickerWithYearSelector extends DatePicker {
		public DatePickerWithYearSelector() {
			super(new MonthAndYearSelector(),new DefaultCalendarView(), new CalendarModel()) ;
			MonthAndYearSelector monthSelector = (MonthAndYearSelector)this.getMonthSelector() ;
			monthSelector.setPicker(this) ;
			monthSelector.setModel(this.getModel()) ;
		}

		public void refreshComponents() {
			super.refreshAll() ;
		}
	}
	
	private static String BASE_NAME = "datePicker" ;
	public  class MonthAndYearSelector extends MonthSelector {
		  private PushButton backwards;
		  private PushButton forwards;
		  private PushButton backwardsYear;
		  private PushButton forwardsYear;
		  private Grid grid;
		  private int previousYearColumn = 0;
		  private int previousMonthColumn = 1 ;
		  private int monthColumn = 2 ;
		  private int nextMonthColumn = 3;
		  private int nextYearColumn = 4 ;
		  private CalendarModel model ;
		  private DatePickerWithYearSelector picker ;

		public void setModel(CalendarModel model) {
			this.model = model;
		}

		public void setPicker(DatePickerWithYearSelector picker) {
			this.picker = picker;
		}

		@Override
		  protected void refresh() {
		    String formattedMonth = getModel().formatCurrentMonth();
		    grid.setText(0, monthColumn, formattedMonth);
		  }

		  @Override
		  protected void setup() {
		    // Set up backwards.
		    backwards = new PushButton();
		    backwards.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
		        addMonths(-1);
		      }
		    });

		    backwards.getUpFace().setHTML("&lsaquo;");
		    backwards.setStyleName(BASE_NAME + "PreviousButton");

		    forwards = new PushButton();
		    forwards.getUpFace().setHTML("&rsaquo;");
		    forwards.setStyleName(BASE_NAME + "NextButton");
		    forwards.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
		        addMonths(+1);
		      }
		    });

		    // Set up backwards year
		    backwardsYear = new PushButton();
		    backwardsYear.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
		        addMonths(-12);
		      }
		    });

		    backwardsYear.getUpFace().setHTML("&laquo;");
		    backwardsYear.setStyleName(BASE_NAME + "PreviousButton");

		    forwardsYear = new PushButton();
		    forwardsYear.getUpFace().setHTML("&raquo;");
		    forwardsYear.setStyleName(BASE_NAME + "NextButton");
		    forwardsYear.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
		        addMonths(+12);
		      }
		    });

		    // Set up grid.
		    grid = new Grid(1, 5);
		    grid.setWidget(0, previousYearColumn, backwardsYear);
		    grid.setWidget(0, previousMonthColumn, backwards);
		    grid.setWidget(0, nextMonthColumn, forwards);
		    grid.setWidget(0, nextYearColumn, forwardsYear);

		    CellFormatter formatter = grid.getCellFormatter();
		    formatter.setStyleName(0, monthColumn, BASE_NAME + "Month");
		    formatter.setWidth(0, previousYearColumn, "1");
		    formatter.setWidth(0, previousMonthColumn, "1");
		    formatter.setWidth(0, monthColumn, "100%");
		    formatter.setWidth(0, nextMonthColumn, "1");
		    formatter.setWidth(0, nextYearColumn, "1");
		    grid.setStyleName(BASE_NAME + "MonthSelector");
		    initWidget(grid);
		  }

		  public void addMonths(int numMonths) {
			    model.shiftCurrentMonth(numMonths);
			    picker.refreshComponents();
			  }

		}
	
	String	uid = null;
	public com.google.gwt.dom.client.Element loginButton() {
		//<fb:login-button show-faces="true" width="200" max-rows="1"></fb:login-button>
		
		com.google.gwt.dom.client.Element elem = Document.get().createElement("fb:login-button");
		elem.setAttribute("width", "200");
		elem.setAttribute("show-faces", "true");
		elem.setAttribute("max-rows", "1");
		elem.setAttribute("perms", "user_birthday,friends_birthday,user_relationships,friends_relationships" );
		elem.setId("fblogin");
		
		return elem;
	}
	
	public native void naut( String uid ) /*-{
		var ths = this;
		var qStr = 'SELECT uid2 FROM friend WHERE uid1 = '+uid;
		//$wnd.alert( qStr );
		//ths.@org.simmi.client.Frislbok::fbFetchPersonInfo(Ljava/lang/String;)( uid );
		
		$wnd.FB.api(
			{
				method: 'fql.query',
				query: qStr
			},
			function(response) {
				//var uids = uid;
				var uids = '('+uid;
				for( ind in response ) {
					uids += ','+response[ind].uid2;
					//break;
				}
				uids += ')';
				
				//ths.@org.simmi.client.Frislbok::fbFetchPersonInfo(Ljava/lang/String;)( uids );
				//ths.@org.simmi.client.Frislbok::fbFetchFamilyInfo(Ljava/lang/String;)( uids );
				$wnd.console.log( uids );
				ths.@org.simmi.client.Frislbok::setUids(Ljava/lang/String;)( uids );
			}
		);
	}-*/;
	
	public native void checkLoginStatus() /*-{
		var ths = this;
		$wnd.console.log( "starting login check" );
		
		$wnd.fbAsyncInit = function() {
	    	$wnd.FB.init({appId: '126977324050932', status: true, cookie: true, xfbml: true});
	    	$wnd.console.log( "init fb" );
	    	try {
				$wnd.FB.getLoginStatus( function(response) {
					$wnd.console.log( "inside login response" );
					try {
						$wnd.FB.XFBML.parse();
					    if (response.status === 'connected') {
					    	$wnd.console.log( "start check" );
					    	var uid = response.authResponse.userID;
					    	$wnd.console.log( "start check"+uid );
					        ths.@org.simmi.client.Frislbok::naut(Ljava/lang/String;)(uid);
					    } else if (response.status === 'not_authorized') {
					       	$wnd.FB.login(function(response) {
						        if (response.authResponse) {
						        	var uid = response.authResponse.userID;
						            ths.@org.simmi.client.Frislbok::naut(Ljava/lang/String;)(uid);
						        } else {
						            // cancelled
						        }
						    });
					    } else {
					        $wnd.FB.login(function(response) {
						        if (response.authResponse) {
						        	var uid = response.authResponse.userID;
						            ths.@org.simmi.client.Frislbok::naut(Ljava/lang/String;)(uid);
						        } else {
						            // cancelled
						        }
						    });
					    }
	
//						if (response.session) {
//							//ths.@org.simmi.client.Frislbok::setUserId(Ljava/lang/String;)( response.session.uid );
//							//$wnd.FB.
//							var uid = response.session.uid;
//							var qStr = 'SELECT uid2 FROM friend WHERE uid1 = '+uid;
//							//$wnd.alert( qStr );
//							
//							$wnd.FB.api(
//								{
//									method: 'fql.query',
//									query: qStr
//								},
//								function(response) {
//									//var uids = uid;
//									var uids = '('+uid;
//									for( ind in response ) {
//										uids += ','+response[ind].uid2;
//										//break;
//									}
//									uids += ')';
//									
//									//ths.@org.simmi.client.Frislbok::fbFetchPersonInfo(Ljava/lang/String;)( uids );
//									//ths.@org.simmi.client.Frislbok::fbFetchFamilyInfo(Ljava/lang/String;)( uids );
//									$wnd.console.log( uids );
//									ths.@org.simmi.client.Frislbok::setUids(Ljava/lang/String;)( uids );
//								}
//							);
//						} else {
//							$wnd.console.log( 'mu' );
//							ths.@org.simmi.client.Frislbok::setUserId(Ljava/lang/String;)( "" );
//						    //$wnd.FB.login();
//						}
					} catch( e ) {
						$wnd.console.log( e );
					}
					$wnd.console.log( "past login response" );
				});
			} catch( e ) {
				$wnd.console.log( e );
			}
	  	};
	  	
	  	(function(d){
		     var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
		     if (d.getElementById(id)) {return;}
		     js = d.createElement('script'); js.id = id; js.async = true;
		     js.src = "//connect.facebook.net/en_US/all.js";
		     ref.parentNode.insertBefore(js, ref);
		}($doc));
		
		$wnd.console.log( "past login check" );
	}-*/;
	
	String 		uids = null;
	Set<String>	uidset = new HashSet<String>();
	Set<String>	runuids = new HashSet<String>();
	String		tobeuids = null;
	public void setUids( String uids ) {
		this.uids = uids;
		
		List<String> uidList = Arrays.asList( uids.substring(1, uids.length()-1).split(",") );
		uidset.addAll( uidList );
		
		tobeuids = "("+uidList.get(0)+")";
		//fbFetchPersonInfo( uids );
		//fbFetchFamilyInfo( uids );
		
		Timer timer = new Timer() {
			public void run() {
				if( tobeuids != null ) {
					String localuids = tobeuids;
					tobeuids = null;
					
					List<String> uidList = Arrays.asList( localuids.substring(1, localuids.length()-1).split(",") );
					runuids.addAll( uidList );
					
					fbFetchPersonInfo( localuids );
					fbFetchFamilyInfo( localuids );
				} else if( runuids.size() == uidset.size() ) {
					saveFbPersons();
					this.cancel();
				} else {
					String localuids = "(";
					int i = 0;
					for( String uid : uidset ) {
						if( !runuids.contains(uid) ) {
							if( localuids.length() == 1 ) localuids += uid;
							else localuids += "," + uid;
						}
						if( ++i > 10 ) break;
					}
					localuids += ")";
					tobeuids = localuids;
				}
			}
		};
		timer.scheduleRepeating(12000);
	}
	
	public Person getCurrentPerson() {
		return currentPerson;
	}
	
	public Person getCurrentFather() {
		return currentPerson != null ? currentPerson.getFather() : null;
	}
	
	public Person getCurrentMother() {
		return currentPerson != null ? currentPerson.getMother() : null;
	}
	
	int		fbCount = 0;
	Person	currentPerson;
	public void setCurrentPerson( final Person person ) {
		//Window.alert( person.getName() + " " + person.getDateOfBirth() + " " + person.getGender() );
		
		removeCurrentChilds();
		currentPerson = person;
		
		personName.setText( person.getName() );
		dateBox.setValue( person.getDateOfBirth() );
		commentText.setValue( person.getComment() );
		int sex = person.getGender();
		if( sex == 1 ) maleButton.setValue( true );
		else if( sex == 2 ) femaleButton.setValue( true );
		
		String facebookUsername = person.getFacebookUsername();
		if( facebookUsername != null ) {
			facebookAnchor.setEnabled( true );
			facebookAnchor.setHref( "http://www.facebook.com/"+facebookUsername );
		} else {
			facebookAnchor.setEnabled( false );
		}
		
		final Person mother = person.getMother();
		if( mother == null ) {
			motherAnchor.setText("Skrá");
			//motherAnchor.setHref("");
		} else {
			String motherName = mother.getName();
			if( motherName != null && motherName.length() > 0 ) {
				motherAnchor.setText( motherName );
			} else {
				if( mother.getIslbokid() != null ) {
					if( mother.getIslbokid().equals("-10") ) motherAnchor.setText( "Skra" );
					else recursiveIslbokFetch( mother.getIslbokid(), person );
				} else motherAnchor.setText( "Nafnlaus" );
			}
			//motherAnchor.setText( motherName != null && motherName.length() > 0 ? motherName : "Nafnlaus" );
			//motherAnchor.setHref( mother.getKey() );
		}
		
		final Person father = person.getFather();
		if( father == null ) {
			fatherAnchor.setText("Skrá");
			//fatherAnchor.setHref("");
			
			//recursiveIslbokFetch(islbokid, child)
		} else {
			String fatherName = father.getName();
			//Browser.getWindow().getConsole().log("ok "+fatherName+" n "+father.getIslbokid());
			if( fatherName != null && fatherName.length() > 0 ) {
				fatherAnchor.setText(  fatherName );
			} else {
				//fatherAnchor.setText( "Nafnlaus" );
				if( father.getIslbokid() != null ) {
					if( father.getIslbokid().equals("-10") ) fatherAnchor.setText( "Skra" );
					else recursiveIslbokFetch( father.getIslbokid(), person );
				} else fatherAnchor.setText( "Nafnlaus" );
			}
			//fatherAnchor.setHref( father.getKey() );
		}
		
		Set<Person> children = person.getChildren();
		if( children == null ) {
			frislbokService.islbok_children( islbok_session, person.getIslbokid(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onSuccess(String result) {
					JSONValue 	jsonval = JSONParser.parseLenient( result );
					JSONArray	jsonarray = jsonval.isArray();
					if( jsonarray != null ) {
						for( int i = 0; i < jsonarray.size(); i++ ) {
							JSONValue jsonvalue = jsonarray.get(i);
							JSONObject jsonobj = jsonvalue.isObject();
							if( jsonobj != null ) {
								Person child = jsonPersonParse( jsonobj );
								addCurrentChild( child );
							}
						}
					}
				}
			});
		} else {
			for( Person child : children ) {
				String childName = child.getName();
				//Browser.getWindow().getConsole().log( childName );
				if( childName == null || childName.length() == 0 ) {
					if( child.getIslbokid() != null ) {
						recursiveIslbokFetchChilds( child.getIslbokid() );
					}
				} else addCurrentChild( child );
			}
		}
		
		Set<Person> siblings = person.getSiblings();
		if( siblings == null ) {
			final String motherislbokid = mother.getIslbokid();
			//Browser.getWindow().getConsole().log( "bl " + motherislbokid );
			/*frislbokService.islbok_children(islbok_session, motherislbokid, new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}

				@Override
				public void onSuccess(String result) {
					//Browser.getWindow().getConsole().log( "bl " + result + " " + motherislbokid );
					JSONValue 	jsonval = JSONParser.parseLenient( result );
					JSONArray	jsonarray = jsonval.isArray();
					if( jsonarray != null ) {
						for( int i = 0; i < jsonarray.size(); i++ ) {
							JSONValue jsonvalue = jsonarray.get(i);
							JSONObject jsonobj = jsonvalue.isObject();
							if( jsonobj != null ) {
								Person child = jsonPersonParse( jsonobj );
								person.addSibling( child );
								//Browser.getWindow().getConsole().log( sibling.getName() + "  " + sibling.getMother().getIslbokid() + "  " + person.getMother().getIslbokid() );
								if( mother.getIslbokid().equals(child.getMother().getIslbokid()) ) {
									mother.addChild( child );
								}
							}
						}
					}
				}
			});
			Browser.getWindow().getConsole().log( "ft " + father.getIslbokid() );
			frislbokService.islbok_children(islbok_session, father.getIslbokid(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}

				@Override
				public void onSuccess(String result) {
					JSONValue 	jsonval = JSONParser.parseLenient( result );
					JSONArray	jsonarray = jsonval.isArray();
					if( jsonarray != null ) {
						for( int i = 0; i < jsonarray.size(); i++ ) {
							JSONValue jsonvalue = jsonarray.get(i);
							JSONObject jsonobj = jsonvalue.isObject();
							if( jsonobj != null ) {
								Person child = jsonPersonParse( jsonobj );
								person.addSibling( child );
								//Browser.getWindow().getConsole().log( sibling.getName() + "  " + sibling.getMother().getIslbokid() + "  " + person.getMother().getIslbokid() );
								if( father.getIslbokid().equals(child.getFather().getIslbokid()) ) {
									father.addChild( child );
								}
							}
						}
					}
				}
			});*/
			frislbokService.islbok_siblings( islbok_session, person.getIslbokid(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onSuccess(String result) {
					Browser.getWindow().getConsole().log( "siblings " + result );
					JSONValue 	jsonval = JSONParser.parseLenient( result );
					JSONArray	jsonarray = jsonval.isArray();
					if( jsonarray != null ) {
						for( int i = 0; i < jsonarray.size(); i++ ) {
							JSONValue jsonvalue = jsonarray.get(i);
							JSONObject jsonobj = jsonvalue.isObject();
							if( jsonobj != null ) {
								Person sibling = jsonPersonParse( jsonobj );
								person.addSibling( sibling );
								//Browser.getWindow().getConsole().log( sibling.getName() + "  " + sibling.getMother().getIslbokid() + "  " + person.getMother().getIslbokid() );
								if( person.getMother().getIslbokid().equals(sibling.getMother().getIslbokid()) ) {
									person.getMother().addChild( sibling );
								}
								if( person.getFather().getIslbokid().equals(sibling.getFather().getIslbokid()) ) {
									person.getFather().addChild( sibling );
								}
							}
						}
					}
					if( person.getMother().getChildren() == null ) {
						
					}
					//Browser.getWindow().getConsole().log( person.getMother().getName() + "  " + person.getMother().getChildren().size() );
				}
			});
		}/* else {
			for( Person sibling : siblings ) {
				String siblingName = sibling.getName();
				if( childName == null || childName.length() == 0 ) {
					if( child.getIslbokid() != null ) {
						recursiveIslbokFetchChilds( child.getIslbokid() );
					}
				} else addCurrentChild( person );
			}
		}*/
	}
	
	public void removeCurrentChilds() {
		while( childrenPanel.getWidgetCount() > 2 ) childrenPanel.remove(1);
	}
	
	public void addCurrentChild( final Person child ) {
		final Anchor childAnchor = new Anchor( child.getName() );
		childAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setCurrentPerson( child );
			}
		});
		childrenPanel.insert( childAnchor, childrenPanel.getWidgetCount()-1 );
	}
	
	public void setCurrentMother( Person mother ) {
		if( mother == null ) motherAnchor.setText("Skrá");
		else if( mother.getName() == null || mother.getName().length() == 0 ) motherAnchor.setText("Nafnlaus");
		else motherAnchor.setText( mother.getName() );
	}
	
	public void setCurrentFather( Person father ) {
		if( father == null ) fatherAnchor.setText("Skrá");
		else if( father.getName() == null || father.getName().length() == 0 ) fatherAnchor.setText("Nafnlaus");
		else fatherAnchor.setText( father.getName() );
	}
	
	public void setPersonIslbokId( Person person, String islbokid ) {
		person.setIslbokid( islbokid );
		islbokidPerson.put( islbokid, person );
	}
	
	public void setPersonFacebookId( Person person, String fbid ) {
		person.setFacebookid( fbid );
		fbuidPerson.put( fbid, person );
	}
	
	Map<String,Person>	islbokidPerson = new HashMap<String,Person>();
	Map<String,Person>	fbuidPerson = new HashMap<String,Person>();
	Map<String,Person>	keyPerson = new HashMap<String,Person>();
	public void newCurrentPerson( String puid, String username, String name, String dateOfBirth, String gender, JsArrayMixed family ) {	
		//Person person = new Person( name, dateOfBirth, gender );
		
		int sex = 0;
		if( "male".equals(gender) ) sex = 1;
		if( "female".equals(gender) ) sex = 2;

		Date date = DateTimeFormat.getFormat("MM/dd/yyyy").parse(dateOfBirth);
		
		final Person person;
		if( fbuidPerson.containsKey(puid) ) {
			person = fbuidPerson.get( puid );
			person.setName( name );
			person.setDateOfBirth( date );
			person.setGender( sex );
		} else {
			person = new Person( name, date, sex );
			fbuidPerson.put(puid, person);
		}
		person.setFacebookid( puid );
		person.setFacebookUsername( username );
		person.setFbwriter( uid );
		
		for( int i = 0; i < family.length(); i++ ) {
			JSONObject		jsonObj = new JSONObject( family.getObject(i) );
			JSONString 		rel = jsonObj.get("relationship").isString();
			
			if( rel != null ) {
				String relationship = rel.stringValue();
				if( "parent".equals( relationship ) ) {
					
				} else if( "father".equals( relationship ) ) {
					
				} else if( "mother".equals( relationship ) ) {
					if( person.mother == null ) {
						person.mother = new Person();
						person.mother.setFbwriter(uid);
						person.mother.children.add( person );
						JSONString motherid = jsonObj.get("uid").isString();
						if( motherid != null ) {
							String motherfbid = motherid.stringValue();
							fbuidPerson.put( motherfbid, person.mother );
							person.mother.setFacebookid( motherfbid );
							fbFetch( "("+motherfbid+")" );
						} else {
							JSONString jName = jsonObj.get("name").isString();
							JSONString jDate = jsonObj.get("birthday").isString();
							
							if( jName != null ) person.mother.setName( jName.stringValue() );
							if( jDate != null ) {
								Date birthday = DateTimeFormat.getFormat("MM/dd/yyyy").parse( jDate.stringValue() );
								person.mother.setDateOfBirth( birthday );
							}
						}
					}
				} else if( "child".equals( relationship ) ) {
					
				} else if( "son".equals( relationship ) ) {
					
				} else if( "daughter".equals( relationship ) ) {
					
				} else if( "sibling".equals( relationship ) ) {
					
				} else if( "sister".equals( relationship ) ) {
					
				} else if( "brother".equals( relationship ) ) {
					
				}
			}
		}
		fbCount++;
		
		/*if( fatherid != null ) {
			Person fatherPerson = new Person();
			fatherPerson.setFacebookid( fatherid );
			fatherPerson.setFbwriter( uid );
		}*/
		
		//frislbokService
		
		frislbokService.savePerson( person, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				person.setKey( result );
			}
		});
		
		if( fbCount == fbuidPerson.size() ) {
			setCurrentPerson( fbuidPerson.get(uid) );
			
			Person[] persons = fbuidPerson.values().toArray( new Person[0] );
			frislbokService.savePersonArray( persons, new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					
				}

				@Override
				public void onSuccess(String result) {
					
				}
			});
		}
		
		/*if( uid.equals(person.getFacebookid()) ) setCurrentPerson( person );
		else {
			Person vip = fbuidPerson.get( uid );
			if( vip.mother != null && motherAnchor.getText().equals("Skrá") ) {
				motherAnchor.setText( vip.mother.getName() );
				motherAnchor.setHref( vip.mother.getKey() );
			}
			if( vip.father != null && fatherAnchor.getText().equals("Skrá") ) {
				fatherAnchor.setText( vip.father.getName() );
				fatherAnchor.setHref( vip.father.getKey() );
			}
		}*/
	}
	
	public native void fbFetch( String uids ) /*-{
		var ths = this;
		var qStr = 'SELECT name, birthday_date, sex, username, family, uid FROM user WHERE uid in '+uids;
		//$wnd.alert( qStr );
		
		$wnd.FB.api(
			{
				method: 'fql.query',
				query: qStr
			},
			function(response) {
				for( ind in response ) {
					//$wnd.alert( ind );
					//$wnd.alert( response[ind] );
					
					var personName = response[ind].name;
					var personBirthDay = response[ind].birthday_date;
					var personGender = response[ind].sex;
					var personFamily = response[ind].family;
					var username = response[ind].username;
					var uid = response[ind].uid;
					$wnd.console.log( personFamily );
					$wnd.FB.api(
						{
							method: 'fql.query',
							query: 'SELECT uid, name, birthday, relationship FROM family WHERE profile_id = '+uid
						},
						function(response2) {
							ths.@org.simmi.client.Frislbok::newCurrentPerson(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayMixed;)
								( uid, username, personName, personBirthDay, personGender, response2 );
						}
					);
				}
			}
		);
	}-*/;
	
	public native void fbFetchPersonInfo( String uids ) /*-{
		var ths = this;
		var qStr = 'SELECT name, birthday_date, sex, username, family, uid FROM user WHERE uid in '+uids;
		
		$wnd.console.log('sim ' + uids);
		$wnd.FB.api(
			{
				method: 'fql.query',
				query: qStr
			},
			function(response) {
				$wnd.console.log('resp');
				ths.@org.simmi.client.Frislbok::parsePersonInfo(Lcom/google/gwt/core/client/JsArrayMixed;)( response );
			}
		);
	}-*/;
	
	public native void fbFetchFamilyInfo( String uids ) /*-{
		var ths = this;
		var qStr = 'SELECT profile_id, uid, name, birthday, relationship FROM family WHERE profile_id in '+uids
		
		$wnd.console.log('ok');
		$wnd.FB.api(
			{
				method: 'fql.query',
				query: qStr
			},
			function(response) {
				$wnd.console.log('onse');
				ths.@org.simmi.client.Frislbok::parseFamilyInfo(Lcom/google/gwt/core/client/JsArrayMixed;)( response );
			}
		);
	}-*/;
	
	public native void console( String msg ) /*-{
		$wnd.console.log( msg );
	}-*/;
	
	public native void loadGoogleMaps( Element el ) /*-{
		var mapOptions = {
	          center: new $wnd.google.maps.LatLng( 64.82, -18.79 ),
	          zoom: 6,
	          mapTypeId: $wnd.google.maps.MapTypeId.ROADMAP
	    };
	    var map = new $wnd.google.maps.Map(el, mapOptions);
	}-*/;
	
	boolean done = false;
	public void parsePersonInfo( JsArrayMixed response ) {
		console( "presplen: "+response.length() );
		for( int i = 0; i < response.length(); i++ ) {
			JSONObject		jsonObj = new JSONObject( response.getObject(i) );
			JSONString 		name = jsonObj.get("name").isString();
			JSONString 		birthday = jsonObj.get("birthday_date").isString();
			JSONNumber 		sex = jsonObj.get("sex").isNumber();
			JSONString 		username = jsonObj.get("username").isString();
			JSONString 		uid = jsonObj.get("uid").isString();
			
			String 	fbuid = uid.stringValue();
			Person	person;
			if( fbuidPerson.containsKey(fbuid) ) {
				person = fbuidPerson.get(fbuid);
			} else {
				person = new Person();
				fbuidPerson.put(fbuid, person);
			}
			
			if( name != null ) person.setName( name.stringValue() );
			if( sex != null ) person.setGender( (int)sex.doubleValue() );
			if( birthday != null ) {
				Date date = DateTimeFormat.getFormat("MM/dd/yyyy").parse( birthday.stringValue() );
				person.setDateOfBirth( date );
			}
			if( username != null ) {
				person.setFacebookUsername( username.stringValue() );
			}
			person.setFacebookid( fbuid );
		}
		
		/*if( done ) {
			saveFbPersons();
		} else done = true;*/
	}
	
	public void parseFamilyInfo( JsArrayMixed response ) {
		console( "fresplen: "+response.length() );
		for( int i = 0; i < response.length(); i++ ) {
			JSONObject		jsonObj = new JSONObject( response.getObject(i) );
			JSONString 		pro = jsonObj.get("profile_id").isString();
			JSONString 		uid = jsonObj.get("uid").isString();
			JSONString 		name = jsonObj.get("name").isString();
			JSONString 		birthday = jsonObj.get("birthday").isString();
			JSONString 		rel = jsonObj.get("relationship").isString();
			
			String 	fbuid = pro.stringValue();
			Person	person;
			if( fbuidPerson.containsKey(fbuid) ) {
				person = fbuidPerson.get(fbuid);
			} else {
				person = new Person();
				fbuidPerson.put(fbuid, person);
			}
			
			String relationship = rel.stringValue();
			if( "parent".equals( relationship ) ) {
				
			} else if( "father".equals( relationship ) ) {
				Person p;
				if( person.father == null ) {
					if( uid != null ) {
						String fatherid = uid.stringValue();
						if( fbuidPerson.containsKey(fatherid) ) {
							p = fbuidPerson.get( fatherid );
						} else {
							p = new Person();
							fbuidPerson.put( fatherid, p );
							p.setFacebookid( fatherid );
						}
					} else {
						p = new Person();
						
						if( name != null ) p.setName( name.stringValue() );
						if( birthday != null ) {
							Date date = DateTimeFormat.getFormat("MM/dd/yyyy").parse( birthday.stringValue() );
							p.setDateOfBirth( date );
						}
					}				
					person.setFather( p );
				} else {
					p = person.father;
				}
				p.setGender( 1 );
				p.setFbwriter( fbuid );
			} else if( "mother".equals( relationship ) ) {
				Person p;
				if( person.mother == null ) {
					if( uid != null ) {
						String motherid = uid.stringValue();
						if( fbuidPerson.containsKey(motherid) ) {
							p = fbuidPerson.get( motherid );
						} else {
							p = new Person();
							fbuidPerson.put( motherid, p );
							p.setFacebookid( motherid );
						}
					} else {
						p = new Person();
						
						if( name != null ) p.setName( name.stringValue() );
						if( birthday != null ) {
							Date date = DateTimeFormat.getFormat("MM/dd/yyyy").parse( birthday.stringValue() );
							p.setDateOfBirth( date );
						}
					}				
					person.setMother( p );
				} else {
					p = person.mother;
				}
				p.setGender( 2 );
				p.setFbwriter( fbuid );
			} else if( "child".equals( relationship ) ) {
				
			} else if( "son".equals( relationship ) ) {
				
			} else if( "daughter".equals( relationship ) ) {
				
			} else if( "sibling".equals( relationship ) ) {
				
			} else if( "sister".equals( relationship ) ) {
				
			} else if( "brother".equals( relationship ) ) {
				Person p;
				if( uid != null ) {
					String brotherid = uid.stringValue();
					if( fbuidPerson.containsKey(brotherid) ) {
						p = fbuidPerson.get( brotherid );
					} else {
						p = new Person();
						fbuidPerson.put( brotherid, p );
						p.setFacebookid( brotherid );
					}
				} else {
					p = new Person();
					
					if( name != null ) p.setName( name.stringValue() );
					if( birthday != null ) {
						Date date = DateTimeFormat.getFormat("MM/dd/yyyy").parse( birthday.stringValue() );
						p.setDateOfBirth( date );
					}
				}
				p.setGender(1);
				person.addSibling( p );
			}
		}
		
		/*if( done ) {
			saveFbPersons();
		} else done = true;*/
	}
	
	public void saveFbPersons() {
		int i = 0;
		Person[] persons = new Person[fbuidPerson.size()];
		for( String fbuid : fbuidPerson.keySet() ) {
			persons[i++] = fbuidPerson.get(fbuid);
		}
		
		frislbokService.savePersonArray(persons, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				
			}
		});
	}
	
	public void setUserId( String val ) {
		uid = val;
		
		if( uid.length() > 0 ) {
			frislbokService.fetchFromFacebookId( uid, new AsyncCallback<Person>() {
				@Override
				public void onSuccess(Person result) {
					if( result == null ) {
						fbCount++;
						fbFetch( "("+uid+")" );
					} else {
						setCurrentPerson( result );
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					StackTraceElement[] stes = caught.getStackTrace();
					for( StackTraceElement ste : stes ) {
						Window.alert( ste.toString() );
					}
				}
			});
		}
	}
	
	public void recursiveIslbokFetchSiblings( final String islbokid ) {
		if( islbokidPerson.containsKey( islbokid ) ) {
			Person result = islbokidPerson.get( islbokid );
			//addCurrentSibling( result );
		} else {
			frislbokService.fetchFromIslbokId( islbokid, new AsyncCallback<Person>() {
				@Override
				public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }
	
				@Override
				public void onSuccess(Person result) {
					if( result == null ) {
						frislbokService.islbok_get( islbok_session, islbokid, new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }
	
							@Override
							public void onSuccess(String result) {
								//Browser.getWindow().getConsole().log( result );
								
								boolean fail = false;
								JSONValue jsonval = null;
								try {
									jsonval = JSONParser.parseLenient( result );
								} catch( Exception e ) {
									fail = true;
								}
								
								if( fail ) {
									int lasti = -1;
									boolean inside = false;
									StringBuilder sb = new StringBuilder( result );
									for( int i = 0; i < result.length(); i++ ) {
										char c = sb.charAt(i);
										if( c == '"' ) {
											if( inside ) {
												if( lasti != -1 ) {
													sb.replace(lasti, lasti+1, "\\\"");
													i+=1;
													lasti = i;
												} else lasti = i;
											} else {
												inside = true;
											}
										} else if( c == ',' || c == ':' ) {
											inside = false;
											lasti = -1;
										}
										//Browser.getWindow().getConsole().log( "fail " + i );
									}
									result = sb.toString();
									//Browser.getWindow().getConsole().log( "fail " + result );
									jsonval = JSONParser.parseLenient( result );
								}
								
								//JSONArray	jsonarray = jsonval.isArray();
								
								JSONObject jsonobj = jsonval.isObject();
								Person person = jsonPersonParse( jsonobj );
									
									/*final Person mother = new Person();
									mother.setIslbokid( motherislbokid.stringValue() );
									person.setMother( mother );
									final Person father = new Person();
									father.setIslbokid( fatherislbokid.stringValue() );
									person.setFather( father );*/
							}
						});
					} else {
						//addCurrentSibling( result );
					}
				}
			});
		}
	}
	
	public void recursiveIslbokFetchChilds( final String islbokid ) {
		if( islbokidPerson.containsKey( islbokid ) ) {
			Person result = islbokidPerson.get( islbokid );
			addCurrentChild( result );
		} else {
			frislbokService.fetchFromIslbokId( islbokid, new AsyncCallback<Person>() {
				@Override
				public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }
	
				@Override
				public void onSuccess(Person result) {
					if( result == null ) {
						frislbokService.islbok_get( islbok_session, islbokid, new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }
	
							@Override
							public void onSuccess(String result) {
								//Browser.getWindow().getConsole().log( result );
								
								boolean fail = false;
								JSONValue jsonval = null;
								try {
									jsonval = JSONParser.parseLenient( result );
								} catch( Exception e ) {
									fail = true;
								}
								
								if( fail ) {
									int lasti = -1;
									boolean inside = false;
									StringBuilder sb = new StringBuilder( result );
									for( int i = 0; i < result.length(); i++ ) {
										char c = sb.charAt(i);
										if( c == '"' ) {
											if( inside ) {
												if( lasti != -1 ) {
													sb.replace(lasti, lasti+1, "\\\"");
													i+=1;
													lasti = i;
												} else lasti = i;
											} else {
												inside = true;
											}
										} else if( c == ',' || c == ':' ) {
											inside = false;
											lasti = -1;
										}
										//Browser.getWindow().getConsole().log( "fail " + i );
									}
									result = sb.toString();
									//Browser.getWindow().getConsole().log( "fail " + result );
									jsonval = JSONParser.parseLenient( result );
								}
								
								//JSONArray	jsonarray = jsonval.isArray();
								
								JSONObject jsonobj = jsonval.isObject();
								Person person = jsonPersonParse( jsonobj );
								if( person != null ) {
									addCurrentChild( person );
								}
									
									/*final Person mother = new Person();
									mother.setIslbokid( motherislbokid.stringValue() );
									person.setMother( mother );
									final Person father = new Person();
									father.setIslbokid( fatherislbokid.stringValue() );
									person.setFather( father );*/
							}
						});
					} else {
						addCurrentChild( result );
					}
				}
			});
		}
	}

	public Person jsonPersonParse( JSONObject jsonobj ) {
		if( jsonobj != null ) {
			JSONString name = jsonobj.get("name").isString();
			
			JSONString dob = jsonobj.get("dob").isString();
			JSONNumber gender = jsonobj.get("gender").isNumber();
			JSONString text = jsonobj.get("text").isString();
			JSONNumber id = jsonobj.get("id").isNumber();
			
			JSONNumber motherislbokid = jsonobj.get("mother").isNumber();
			JSONNumber fatherislbokid = jsonobj.get("father").isNumber();
			
			String dateofbirth = dob.stringValue();
			DateTimeFormat dateformat = null;
			//DateTimeFormat.PredefinedFormat.YEAR_MONTH_DAY
			if( dateofbirth.length() == 8 ) {
				if( dateofbirth.endsWith("0000") ) {
					dateofbirth = dateofbirth.substring(0,4);
					dateformat = DateTimeFormat.getFormat("yyyy");
				} else if( dateofbirth.endsWith("00") ) {
					dateofbirth = dateofbirth.substring(0,6);
					dateformat = DateTimeFormat.getFormat("yyyyMM");
				} else dateformat = DateTimeFormat.getFormat("yyyyMMdd");
			}
			else if( dateofbirth.length() == 6 ) dateformat = DateTimeFormat.getFormat("yyyyMM");
			else if( dateofbirth.length() == 4 ) dateformat = DateTimeFormat.getFormat("yyyy");
			
			Date date = dateformat == null ? null : dateformat.parse(dateofbirth);
			
			String namestr = name.stringValue();
			int genderval = (int)gender.doubleValue();
			final Person person = new Person( namestr, date, genderval );
			setPersonIslbokId( person, Long.toString( (long)id.doubleValue() ) );
			person.setComment( text.stringValue() );
			
			Person father = new Person();
			father.setGender( 1 );
			setPersonIslbokId( father, Long.toString( (long)fatherislbokid.doubleValue() ) );
			person.setParent( father );
			Person mother = new Person();
			mother.setGender( 2 );
			setPersonIslbokId( mother, Long.toString( (long)motherislbokid.doubleValue() ) );
			person.setParent( mother );
			
			return person;
		}
		return null;
	}
	
	public void subRecursiveIslbokFetch( final String islbokid, final Person child ) {
		frislbokService.fetchFromIslbokId( islbokid, new AsyncCallback<Person>() {
			@Override
			public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }

			@Override
			public void onSuccess(Person result) {
				if( result == null ) {
					Browser.getWindow().getConsole().log( "about to fuck" );
					
					frislbokService.islbok_get( islbok_session, islbokid, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }

						@Override
						public void onSuccess(String result) {
							Browser.getWindow().getConsole().log( "islbok get result " + result );
							
							boolean fail = false;
							JSONValue jsonval = null;
							try {
								jsonval = JSONParser.parseLenient( result );
							} catch( Exception e ) {
								fail = true;
							}
							
							if( fail ) {
								int lasti = -1;
								boolean inside = false;
								StringBuilder sb = new StringBuilder( result );
								for( int i = 0; i < result.length(); i++ ) {
									char c = sb.charAt(i);
									if( c == '"' ) {
										if( inside ) {
											if( lasti != -1 ) {
												sb.replace(lasti, lasti+1, "\\\"");
												i+=1;
												lasti = i;
											} else lasti = i;
										} else {
											inside = true;
										}
									} else if( c == ',' || c == ':' ) {
										inside = false;
										lasti = -1;
									}
									//Browser.getWindow().getConsole().log( "fail " + i );
								}
								result = sb.toString();
								//Browser.getWindow().getConsole().log( "fail " + result );
								jsonval = JSONParser.parseLenient( result );
							}
							
							JSONObject jsonobj = jsonval.isObject();
							Person person = jsonPersonParse(jsonobj);
							if( person != null ) {								
								/*final Person mother = new Person();
								mother.setIslbokid( motherislbokid.stringValue() );
								person.setMother( mother );
								final Person father = new Person();
								father.setIslbokid( fatherislbokid.stringValue() );
								person.setFather( father );*/
								
								if( child == null ) {
									setCurrentPerson( person );
									recursiveIslbokFetch( person.getFather().getIslbokid(), person );
									recursiveIslbokFetch( person.getMother().getIslbokid(), person );
								} else {
									if( person.isMale() ) setCurrentFather( person );
									else setCurrentMother( person );
									
									child.setParent( person );
								}
							}
						}
					});
				} else {
					if( child == null ) {
						setCurrentPerson( result );
						if( result.getMother() != null && result.getMother().getIslbokid() != null ) {
							recursiveIslbokFetch( result.getMother().getIslbokid(), result );
						}
						if( result.getFather() != null && result.getFather().getIslbokid() != null ) {
							recursiveIslbokFetch( result.getFather().getIslbokid(), result );
						}

					} else {
						if( result.isMale() ) setCurrentFather( result );
						else setCurrentMother( result );
						
						child.setParent( result );
					}
				}
			}
		});
	}
	
	public void subRecursiveFacebookFetch( final String fbid, final Person child ) {
		frislbokService.fetchFromFacebookId( fbid, new AsyncCallback<Person>() {
			@Override
			public void onFailure(Throwable caught) { Browser.getWindow().getConsole().log( caught.getMessage() ); }

			@Override
			public void onSuccess(Person result) {
				if( result == null ) {
					fbFetchPersonInfo( fbid );
				} else {
					if( child == null ) {
						setCurrentPerson( result );
						if( result.getMother() != null && result.getMother().getFacebookid() != null ) {
							recursiveFacebookFetch( result.getMother().getFacebookid(), result );
						}
						if( result.getFather() != null && result.getFather().getFacebookid() != null ) {
							recursiveFacebookFetch( result.getFather().getFacebookid(), result );
						}

					} else {
						if( result.isMale() ) setCurrentFather( result );
						else setCurrentMother( result );
						
						child.setParent( result );
					}
				}
			}
		});
	}
	
	public void recursiveFacebookFetch( final String fbid, final Person child ) {
		if( fbuidPerson.containsKey( fbid ) ) {
			Person result = islbokidPerson.get( fbid );
			if( child == null ) {
				setCurrentPerson( result );
				if( result.getMother() != null && result.getMother().getIslbokid() != null ) {
					recursiveIslbokFetch( result.getMother().getIslbokid(), result );
				}
				if( result.getFather() != null && result.getFather().getIslbokid() != null ) {
					recursiveIslbokFetch( result.getFather().getIslbokid(), result );
				}
			} else {
				if( result.getName() == null ) {
					subRecursiveFacebookFetch( fbid, child );
				} else {
					if( result.isMale() ) setCurrentFather( result );
					else setCurrentMother( result );
					
					child.setParent( result );
				}
			}
		} else {
			subRecursiveFacebookFetch( fbid, child );
		}
	}
	
	public void recursiveIslbokFetch( final String islbokid, final Person child ) {
		if( islbokidPerson.containsKey( islbokid ) ) {
			Person result = islbokidPerson.get( islbokid );
			if( child == null ) {
				setCurrentPerson( result );
				if( result.getMother() != null && result.getMother().getIslbokid() != null ) {
					recursiveIslbokFetch( result.getMother().getIslbokid(), result );
				}
				if( result.getFather() != null && result.getFather().getIslbokid() != null ) {
					recursiveIslbokFetch( result.getFather().getIslbokid(), result );
				}
			} else {
				if( result.getName() == null ) {
					subRecursiveIslbokFetch( islbokid, child );
				} else {
					if( result.isMale() ) setCurrentFather( result );
					else setCurrentMother( result );
					
					child.setParent( result );
				}
			}
		} else {
			subRecursiveIslbokFetch( islbokid, child );
		}
	}

	String			islbok_session;
	TextBox			personName;
	DateBox			dateBox;
	TextArea		commentText;
	RadioButton		maleButton;
	RadioButton		femaleButton;
	Anchor			facebookAnchor;
	Anchor			fatherAnchor;
	Anchor			motherAnchor;
	VerticalPanel	sibanchors;
	HorizontalPanel	childrenPanel;
	
	elemental.html.Window myPopup = null;
	String treestr = null;
	public void handleMessage() {
		elemental.dom.Element e = Browser.getDocument().getElementById("listener");
		e.addEventListener("message", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				MessageEvent me = (MessageEvent) evt;
				Browser.getWindow().getConsole().log("jelp");
				treestr = (String)me.getData();
				myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=frislbok","TreeDraw");
				/*
				 * if( myPopup != null && treestr != null ) {
				 * myPopup.postMessage( treestr, "*" ); treestr = null; }
				 */
			}
		}, true);
	}
	
	int count = 0;
	public void recursiveNodeRename( final TreeUtil.Node renameNode, final int total, final boolean map, final int prevyear ) {
		count++;
		Browser.getWindow().getConsole().log( "count "+count+"  "+total );
		String islbokid = renameNode.getName().trim();
		if( !islbokid.equals("999999") ) frislbokService.islbok_get(islbok_session, islbokid, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String result) {
				Browser.getWindow().getConsole().log( "ancres get "+ result );
				boolean fail = false;
				JSONValue jsonval = null;
				try {
					jsonval = JSONParser.parseLenient( result );
				} catch( Exception e ) {
					fail = true;
				}
				
				if( fail ) {
					int lasti = -1;
					boolean inside = false;
					StringBuilder sb = new StringBuilder( result );
					for( int i = 0; i < result.length(); i++ ) {
						char c = sb.charAt(i);
						if( c == '"' ) {
							if( inside ) {
								if( lasti != -1 ) {
									sb.replace(lasti, lasti+1, "\\\"");
									i+=1;
									lasti = i;
								} else lasti = i;
							} else {
								inside = true;
							}
						} else if( c == ',' || c == ':' ) {
							inside = false;
							lasti = -1;
						}
					}
					result = sb.toString();
					jsonval = JSONParser.parseLenient( result );
				}
				
				//JSONArray	jsonarray = jsonval.isArray();
				
				JSONObject jsonobj = jsonval.isObject();
				Person person = jsonPersonParse( jsonobj );
				
				int dob = person.getDateOfBirth().getYear()+1900;
				renameNode.setName( person.getName()+" ["+dob+"]" );
				
				if( prevyear == 0 ) renameNode.seth( 25.0 );
				else renameNode.seth( prevyear-dob );
				
				if( map ) {
					Browser.getWindow().getConsole().log( person.getName() + " " + person.getComment() );
					renameNode.setMeta( person.getComment() );
				}
				
				for( TreeUtil.Node n : renameNode.getNodes() ) {
					recursiveNodeRename( n, total, map, dob );
				}
				
				//Browser.getWindow().getConsole().log( "count "+count+"  "+total );
				if( count == total ) {
					if( !map ) {
						treestr = renameNode.getRoot().toString();
						Browser.getWindow().getConsole().log( treestr );
						myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=frislbok","TreeDraw");
					} else {
						PopupPanel pp = new PopupPanel();
						pp.setSize("800px", "600px");
						pp.setAutoHideEnabled(true);
						pp.setAutoHideOnHistoryEventsEnabled( true );
						
						SimplePanel	sp = new SimplePanel();
						loadGoogleMaps( sp.getElement() );
						pp.add( sp );
						
						pp.center();
					}
				}
			}
		});
		else {
			for( TreeUtil.Node n : renameNode.getNodes() ) {
				recursiveNodeRename( n, total, map, prevyear+25 );
			}
			
			if( count == total ) {
				if( !map ) {
					treestr = renameNode.getRoot().toString();
					//Browser.getWindow().getConsole().log( treestr );
					myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=frislbok","TreeDraw");
				} else {
					PopupPanel pp = new PopupPanel();
					pp.setSize("800px", "600px");
					pp.setAutoHideEnabled(true);
					pp.setAutoHideOnHistoryEventsEnabled( true );
					
					SimplePanel	sp = new SimplePanel();
					loadGoogleMaps( sp.getElement() );
					pp.add( sp );
					
					pp.center();
				}
			}
		}
	}
	
	public void exportPhylo( final boolean map ) {
		Browser.getWindow().getConsole().log("cur "+currentPerson);
		if( currentPerson != null ) frislbokService.islbok_ancestors( islbok_session, currentPerson.getIslbokid(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Browser.getWindow().getConsole().log("ancerr "+caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				Browser.getWindow().getConsole().log("anc "+result);
				TreeUtil tu = new TreeUtil();
				//treestr = 
				tu.parseNodeList( result.substring(1, result.length()-1) );
				count = 0;
				recursiveNodeRename( tu.getNode(), tu.getNode().countLeaves()*2-1, map, 0 );
				Browser.getWindow().getConsole().log("anc2 "+treestr);
				//treestr = tu.getNode().toString();
				//myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta","TreeDraw");
			}
		});
	}
	
	public void onModuleLoad() {
		final RootPanel root = RootPanel.get();
		
		//handleMessage();
		elemental.html.Window wnd = Browser.getWindow();
		wnd.addEventListener("message", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				MessageEvent me = (MessageEvent) evt;
				String dstr = (String)me.getData();

				Console console = Browser.getWindow().getConsole();
				console.log("okbleh");
				console.log(dstr);

				if (dstr.equals("ready")) {
					elemental.html.Window source = myPopup;// me.getSource();
					console.log(dstr + " " + source);
					console.log(source.getName());

					if (treestr != null) {
						source.postMessage(treestr, "*");
						treestr = null;
					}
				} /*else if (dstr.startsWith("propagate")) {
					int fi = dstr.indexOf('{');
					int li = dstr.indexOf('}');
					String substr = dstr.substring(fi + 1, li);
					String[] split = substr.split(",");
					Set<String> splitset = new HashSet<String>(Arrays
							.asList(split));
					for (Sequence seq : val) {
						SequenceOld so = (SequenceOld) seq;
						String name = seq.getName();
						// console.log("trying "+name);
						if (splitset.contains(name))
							so.setSelected(true);
					}
					draw(xstart, ystart);
				}*/
			}
		}, true);
		
		String user = "sigmar1";
		String pass = "linsan sonar";
		
		frislbokService.login( user, pass, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Browser.getWindow().getConsole().log( caught.getMessage() );
			}

			@Override
			public void onSuccess(String result) {
				Browser.getWindow().getConsole().log( result );
				String[] split = result.split(",");
				final String session = split[0].trim();
				final String islbokid = split[1].trim();
				
				islbok_session = session;
				
				recursiveIslbokFetch( islbokid, null );
				
				//Browser.getWindow().getConsole().log( "about to islget"+session+" "+uid );
			}
		});
		
		/*String query = "login?user="+user+"&pwd="+URL.encode(pass);
		String qurl = "http://www.islendingabok.is/ib_app/"+query;
		Browser.getWindow().getConsole().log( qurl );
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,qurl);
		String requestData = "";
		try {
			rb.sendRequest( requestData, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					Browser.getWindow().getConsole().log( "ok "+response.getText() );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					Browser.getWindow().getConsole().log( "er "+exception.getMessage() );
				}
			});
		} catch (RequestException e1) {
			e1.printStackTrace();
		}*/
		
		
		Style rootstyle = root.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		root.setSize( w+"px", h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				root.setSize( w+"px", h+"px");
			}
		});
		
		VerticalPanel	overall = new VerticalPanel();
		overall.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		overall.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		overall.setSize("100%", "100%");
		
		VerticalPanel	subvp = new VerticalPanel();
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		overall.add( subvp );
		
		Label	title = new Label("Frjálsa Íslendingabókin");
		title.getElement().getStyle().setFontSize(16.0, Unit.PX);
		subvp.add( title );
		
		HorizontalPanel	parentPanel = new HorizontalPanel();
		parentPanel.setSpacing( 10 );
		Label	fatherLabel = new Label("Faðir:");
		fatherAnchor = new Anchor("Skrá");
		Label	motherLabel = new Label("Móðir:");
		motherAnchor = new Anchor("Skrá");
		
		fatherAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setCurrentPerson( getCurrentFather() );
			}
		});
		
		motherAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setCurrentPerson( getCurrentMother() );
			}
		});
		
		parentPanel.add( fatherLabel );
		parentPanel.add( fatherAnchor );
		parentPanel.add( motherLabel );
		parentPanel.add( motherAnchor );
		
		subvp.add( parentPanel );
		
		VerticalPanel	imagePanel = new VerticalPanel();
		imagePanel.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		Image	face = new Image("dummy_face.jpg");
		face.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		face.setPixelSize(64, 64);
		imagePanel.add( face );
		Label clicklabel = new Label("Tvíklikk til að skipta út mynd");
		clicklabel.getElement().getStyle().setFontSize(9.0, Unit.PX);
		imagePanel.add( clicklabel );
		subvp.add( imagePanel );
		
		HorizontalPanel	personPanel = new HorizontalPanel();
		personPanel.setSpacing( 10 );
		Label			personLabel = new Label("Nafn:");
		personName = new TextBox();
		Label			dateLabel = new Label("Fæðingardagur:");
		dateBox = new DateBox( new DatePickerWithYearSelector(), new Date( System.currentTimeMillis() ), new DateBox.DefaultFormat() );
		maleButton = new RadioButton( "gender", "Karl" );
		femaleButton = new RadioButton( "gender", "Kona" );
		
		personPanel.add( personLabel );
		personPanel.add( personName );
		personPanel.add( dateLabel );
		personPanel.add( dateBox );
		personPanel.add( maleButton );
		personPanel.add( femaleButton );
		
		subvp.add( personPanel );
		
		HorizontalPanel	detail = new HorizontalPanel();
		detail.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		detail.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE );
		detail.setSpacing( 10 );
		
		Label		commentLabel = new Label("Nánar:");
		commentText = new TextArea();
		commentText.setSize("400px", "50px");
		detail.add( commentLabel );
		detail.add( commentText );
		
		facebookAnchor = new Anchor("Facebook");
		detail.add( facebookAnchor );
		
		subvp.add( detail );
		
		HorizontalPanel	sibpanel = new HorizontalPanel();
		sibpanel.setSpacing( 10 );
		sibpanel.add( new HTML("Systkini:") );
		
		sibanchors = new VerticalPanel();
		sibanchors.setSpacing( 10 );
		sibpanel.add( sibanchors );
		
		childrenPanel = new HorizontalPanel();
		childrenPanel.setSpacing( 10 );
		Label	childrenLabel = new Label("Börn:");
		childrenPanel.add( childrenLabel );
		Anchor	childrenAnchor = new Anchor("Skrá");
		childrenPanel.add( childrenAnchor );
		
		childrenAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		
		subvp.add( childrenPanel );
		
		HorizontalPanel	optionsPanel = new HorizontalPanel();
		optionsPanel.setSpacing(5);
		optionsPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		subvp.add( optionsPanel );
		
		HorizontalPanel	disclPanel = new HorizontalPanel();
		disclPanel.setSpacing( 10 );
		Anchor	smasogur = new Anchor("http://smasogurnar.appspot.com");
		smasogur.setHref( "http://smasogurnar.appspot.com" );
		Label	kjall = new Label("Kjallarinn klukkan fimm ehf.");
		disclPanel.add( smasogur );
		disclPanel.add( kjall );
		
		subvp.add( disclPanel );
		
		SimplePanel	fbpanel = new SimplePanel();
		fbpanel.getElement().appendChild( loginButton() );
		subvp.add( fbpanel );
		
		//checkLoginStatus(); Facebook
		
		/*Element e = Document.get().createElement("script");
		e.setAttribute("async", "true");
		e.setAttribute("src", "http://connect.facebook.net/en_US/all.js" );
		Document.get().getElementById("fb-root").appendChild(e);*/
		
		Button showmap = new Button("Hvaðan af landinu");
		showmap.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				exportPhylo( true );
			}
		});
		Button shareaccount = new Button("Deila aðgangi");
		Button phylo = new Button("Sýna ættartré");
		phylo.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Browser.getWindow().getConsole().log("anc ermerm");
				exportPhylo( false );
				//treestr = exportPhylo();
				//myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta","TreeDraw");
			}
		});
		
		optionsPanel.add( showmap );
		optionsPanel.add( shareaccount );
		optionsPanel.add( phylo );
		
		root.add(overall);
	}
}
