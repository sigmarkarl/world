package org.simmi.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
	
	public native void checkLoginStatus() /*-{
		var ths = this;
		$wnd.console.log( "starting login check" );
		
		$wnd.fbAsyncInit = function() {
	    	$wnd.FB.init({appId: '126977324050932', status: true, cookie: true, xfbml: true});
	    	
	    	try {
				$wnd.FB.getLoginStatus( function(response) {
					$wnd.console.log( "inside login response" );
					try {
						$wnd.FB.XFBML.parse();
						if (response.session) {
							//ths.@org.simmi.client.Frislbok::setUserId(Ljava/lang/String;)( response.session.uid );
							//$wnd.FB.
							var uid = response.session.uid;
							var qStr = 'SELECT uid2 FROM friend WHERE uid1 = '+uid;
							//$wnd.alert( qStr );
							
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
									
									ths.@org.simmi.client.Frislbok::setUids(Ljava/lang/String;)( uids );
								}
							);
						} else {
							ths.@org.simmi.client.Frislbok::setUserId(Ljava/lang/String;)( "" );
						    //$wnd.FB.login();
						}
					} catch( e ) {
						$wnd.console.log( e );
					}
					$wnd.console.log( "past login response" );
				});
			} catch( e ) {
				$wnd.console.log( e );
			}
	  	};
		
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
	
	int		fbCount = 0;
	Person	currentPerson;
	public void setCurrentPerson( Person person ) {
		//Window.alert( person.getName() + " " + person.getDateOfBirth() + " " + person.getGender() );
		
		currentPerson = person;
		
		personName.setText( person.getName() );
		dateBox.setValue( person.getDateOfBirth() );
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
		
		Person mother = person.getMother();
		if( mother == null ) {
			motherAnchor.setText("Skrá");
			motherAnchor.setHref("");
		} else {
			String motherName = mother.getName();
			motherAnchor.setText( motherName != null && motherName.length() > 0 ? motherName : "Nafnlaus" );
			motherAnchor.setHref( mother.getKey() );
		}
		
		Person father = person.getFather();
		if( father == null ) {
			fatherAnchor.setText("Skrá");
			fatherAnchor.setHref("");
		} else {
			String fatherName = father.getName();
			fatherAnchor.setText( fatherName != null && fatherName.length() > 0 ? fatherName : "Nafnlaus" );
			fatherAnchor.setHref( father.getKey() );
		}
	}
	
	Person	currentMother;
	public void setCurrentMother( Person mother ) {
		Window.alert( mother.getName() + " " + mother.getDateOfBirth() + " " + mother.getGender() );
		
		currentMother = mother;
		
		/*personName.setText( mother.getName() );
		dateBox.setValue( mother.getDateOfBirth() );
		int sex = mother.getGender();
		if( sex == 1 ) maleButton.setValue( true );
		else if( sex == 2 ) femaleButton.setValue( true );
		
		String facebookUsername = mother.getFacebookUsername();
		if( facebookUsername != null ) {
			facebookAnchor.setEnabled( true );
			facebookAnchor.setHref( "http://www.facebook.com/"+facebookUsername );
		} else {
			facebookAnchor.setEnabled( false );
		}*/
	}
	
	Person	currentFather;
	public void setCurrentFather( Person father ) {
		//Window.alert( mother.getName() + " " + mother.getDateOfBirth() + " " + mother.getGender() );
		
		currentFather = father;
		
		/*personName.setText( .getName() );
		dateBox.setValue( mother.getDateOfBirth() );
		int sex = mother.getGender();
		if( sex == 1 ) maleButton.setValue( true );
		else if( sex == 2 ) femaleButton.setValue( true );
		
		String facebookUsername = mother.getFacebookUsername();
		if( facebookUsername != null ) {
			facebookAnchor.setEnabled( true );
			facebookAnchor.setHref( "http://www.facebook.com/"+facebookUsername );
		} else {
			facebookAnchor.setEnabled( false );
		}*/
	}
	
	Map<String,Person>	fbuidPerson = new HashMap<String,Person>();
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

	TextBox			personName;
	DateBox			dateBox;
	RadioButton		maleButton;
	RadioButton		femaleButton;
	Anchor			facebookAnchor;
	Anchor			fatherAnchor;
	Anchor			motherAnchor;
	VerticalPanel	sibanchors;
	public void onModuleLoad() {
		final RootPanel root = RootPanel.get();
		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,"http://www.islendingabok.is/is_app/");
		String requestData = "login/user=sigmar1&pwd=linsan sonar";
		try {
			rb.sendRequest( requestData, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					Browser.getWindow().getConsole().log( response.getText() );
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					Browser.getWindow().getConsole().log( exception.getMessage() );
				}
			});
		} catch (RequestException e1) {
			e1.printStackTrace();
		}
		
		
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
		fatherAnchor = new Anchor("Srká");
		Label	motherLabel = new Label("Móðir:");
		motherAnchor = new Anchor("Skrá");
		
		fatherAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//frislService.
			}
		});
		
		parentPanel.add( fatherLabel );
		parentPanel.add( fatherAnchor );
		parentPanel.add( motherLabel );
		parentPanel.add( motherAnchor );
		
		subvp.add( parentPanel );
		
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
		TextArea	commentText = new TextArea();
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
		
		HorizontalPanel	childrenPanel = new HorizontalPanel();
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
		
		checkLoginStatus();
		
		Element e = Document.get().createElement("script");
		e.setAttribute("async", "true");
		e.setAttribute("src", "http://connect.facebook.net/en_US/all.js" );
		Document.get().getElementById("fb-root").appendChild(e);
		
		root.add(overall);
	}
}
