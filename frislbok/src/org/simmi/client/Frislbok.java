package org.simmi.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
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
							ths.@org.simmi.client.Frislbok::setUserId(Ljava/lang/String;)( response.session.uid );
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
	
	Person	currentPerson;
	public void setCurrentPerson( Person person ) {
		Window.alert( person.getName() + " " + person.getDateOfBirth() + " " + person.getGender() );
		
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
	}
	
	public void newCurrentPerson( String name, String dateOfBirth, String gender, String mother, String motherid ) {	
		//Person person = new Person( name, dateOfBirth, gender );
		
		int sex = 0;
		if( "male".equals(gender) ) sex = 1;
		if( "female".equals(gender) ) sex = 2;

		Date date = DateTimeFormat.getFormat("MM/DD/YYYY").parse(dateOfBirth);
		
		Person person = new Person( name, date, sex );
		person.setFacebookid( uid );
		person.setFbwriter( uid );
		
		frislbokService.savePerson( person, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				
			}
		});
		
		setCurrentPerson( person );
	}
	
	public native void fbFetch( String uid ) /*-{
		var ths = this;
		$wnd.FB.api(
			{
				method: 'fql.query',
				query: 'SELECT name, birthday_date, sex, family FROM user WHERE uid='+uid
			},
			function(response) {
				var personName = response[0].name;
				var personBirthDay = response[0].birthday_date;
				var personGender = response[0].sex;
				var personFamily = response[0].family;
				$wnd.console.log( personFamily );
				$wnd.FB.api(
					{
						method: 'fql.query',
						query: 'SELECT uid, name, birthday, relationship FROM family WHERE profile_id='+uid
					},
					function(response2) {
						var mother;
						var motherid;
						for( ind in response2 ) {
							var resp = response2[ind];
							$wnd.console.log( resp );
							if( resp.relationship == 'mother' ) {
								mother = resp.name;
								motherid = resp.uid;
							}
						}
						ths.@org.simmi.client.Frislbok::newCurrentPerson(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( personName, personBirthDay, personGender, mother, motherid );
					}
				);
			}
		);
	}-*/;
	
	public void setUserId( String val ) {
		uid = val;
		
		if( uid.length() > 0 ) {
			frislbokService.fetchFromFacebookId( uid, new AsyncCallback<Person>() {
				@Override
				public void onSuccess(Person result) {
					if( result == null ) {
						fbFetch( uid );
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
	public void onModuleLoad() {
		final RootPanel root = RootPanel.get();
		
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
		Anchor	fatherAnchor = new Anchor("Srká");
		Label	motherLabel = new Label("Móðir:");
		Anchor	motherAnchor = new Anchor("Skrá");
		
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
		
		Label		commentLabel = new Label("Nánar:");
		TextArea	commentText = new TextArea();
		commentText.setSize("400px", "50px");
		subvp.add( commentLabel );
		subvp.add( commentText );
		
		facebookAnchor = new Anchor("Facebook");
		subvp.add( facebookAnchor );
		
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
