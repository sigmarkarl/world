package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import elemental.client.Browser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webworm implements EntryPoint, MouseDownHandler, MouseUpHandler,
		MouseMoveHandler, KeyDownHandler, KeyUpHandler, TouchStartHandler,
		TouchEndHandler, TouchMoveHandler {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	int highscore = 0;
	String huid = null;
	String highscoreholder = "no one";
	int highwidth = 1;
	int highheight = 1;
	double applex;
	double appley;
	double appler = 16.0;

	public int setApple(Context2d context) {
		int w = context.getCanvas().getWidth();
		int h = context.getCanvas().getHeight();

		applex = Random.nextDouble() * (w - appler * 2.0) + appler;
		appley = Random.nextDouble() * (h - appler * 2.0) + appler;

		ImageData id = context.getImageData(applex - appler, appley - appler,
				2.0 * appler, 2.0 * appler);
		for (int x = 0; x < id.getWidth(); x += 4) {
			int y;
			for (y = 0; y < id.getHeight(); y += 4) {
				int r = id.getRedAt(x, y);
				int g = id.getGreenAt(x, y);
				int b = id.getBlueAt(x, y);

				boolean inad = false;
				if (popup != null && popup.isShowing()) {
					if (applex < 160 + appler
							&& appley > (h - 600) / 2 - appler
							&& appley < (h + 600) / 2 + appler)
						inad = true;
				}

				if (inad || r != 0 || g != 0 || b != 0)
					return setApple(context);
			}
		}

		context.beginPath();
		context.setFillStyle("#dddddd");
		context.arc(applex, appley, appler - 4.0, 0, 2.0 * Math.PI);
		context.fill();
		context.closePath();

		return 0;
	}

	boolean lorcon = false;
	boolean criang = false;
	boolean quatel = false;
	boolean dipill = false;
	boolean deflec = false;
	boolean extlif = false;
	boolean luck = false;
	boolean mondes = false;

	/*
	 * public native void fbFetchPersonName( String uid, int score ) /*-{ var
	 * ths = this; var qStr = 'SELECT name FROM user WHERE uid = '+uid;
	 * 
	 * $wnd.FB.api( { method: 'fql.query', query: qStr }, function(response) {
	 * ths.@org.simmi.client.Webworm::makeHighScore(Ljava/lang/String;I)(
	 * response[0].name, score ); } ); }-
	 */

	public void makeHighScore(final int score, final Worm worm) {
		DialogBox db = new DialogBox();
		db.setText("Congratulations, you have the highscore among your friends! ("
				+ score + ")");
		// final TextBox tb = new TextBox();
		// tb.setWidth("400px");
		// tb.setText( name );
		db.setAutoHideEnabled(true);
		db.setModal(true);

		VerticalPanel vp = new VerticalPanel();
		VerticalPanel hp = new VerticalPanel();

		final RadioButton criang = new RadioButton("superpower", "Critical angle");
		final RadioButton quatel = new RadioButton("superpower", "Quantum teleportation");
		final RadioButton lorcon = new RadioButton("superpower", "Lorentz contraction");
		final RadioButton extlif = new RadioButton("superpower", "Extra life");
		final RadioButton luck = new RadioButton("superpower", "Luck");
		final RadioButton deflec = new RadioButton("superpower", "Deflection");
		final RadioButton dipill = new RadioButton("superpower", "Dietary pill");
		final RadioButton mondes = new RadioButton("superpower", "Monolith destroyer");

		criang.setEnabled(!powerset.contains("criang"));
		quatel.setEnabled(!powerset.contains("quatel"));
		lorcon.setEnabled(!powerset.contains("lorcon"));
		extlif.setEnabled(!powerset.contains("extlif"));
		luck.setEnabled(!powerset.contains("luck"));
		deflec.setEnabled(!powerset.contains("deflec"));
		dipill.setEnabled(!powerset.contains("dipill"));
		mondes.setEnabled(!powerset.contains("mondes"));

		hp.add(criang);
		hp.add(quatel);
		hp.add(lorcon);
		hp.add(extlif);
		hp.add(luck);
		hp.add(deflec);
		hp.add(dipill);
		hp.add(mondes);

		// vp.add( tb );
		vp.add(hp);

		db.add(vp);
		pause = true;
		db.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String superpowers = "";
				if (Webworm.this.lorcon)
					superpowers = superpowers.length() == 0 ? "lorcon"
							: "\tlorcon";
				if (Webworm.this.quatel)
					superpowers = superpowers.length() == 0 ? "quatel"
							: "\tquatel";
				if (Webworm.this.dipill)
					superpowers = superpowers.length() == 0 ? "dipill"
							: "\tdipill";
				if (Webworm.this.luck)
					superpowers = superpowers.length() == 0 ? "luck" : "\tluck";
				if (Webworm.this.extlif)
					superpowers = superpowers.length() == 0 ? "extlif"
							: "\textlif";
				if (Webworm.this.deflec)
					superpowers = superpowers.length() == 0 ? "deflec"
							: "\tdeflec";
				if (Webworm.this.criang)
					superpowers = superpowers.length() == 0 ? "criang"
							: "\tcriang";
				if (Webworm.this.mondes)
					superpowers = superpowers.length() == 0 ? "mondes"
							: "\tmondes";

				final String bonuspower;
				if (criang.getValue())
					bonuspower = "criang";
				else if (quatel.getValue())
					bonuspower = "quatel";
				else if (lorcon.getValue())
					bonuspower = "lorcon";
				else if (extlif.getValue())
					bonuspower = "extlif";
				else if (luck.getValue())
					bonuspower = "luck";
				else if (deflec.getValue())
					bonuspower = "deflec";
				else if (dipill.getValue())
					bonuspower = "dipill";
				else if (mondes.getValue())
					bonuspower = "mondes";
				else
					bonuspower = "";

				powerset.add(bonuspower);

				/*
				 * for( String pow : powerset ) { if( superpowers.length() == 0
				 * ) superpowers = pow; else superpowers += "\t"+pow; }
				 */

				// Window.alert(bonuspower + " " + dipill.getValue());

				// tb.getText()
				saveHighScore(score, worm, superpowers, bonuspower);
				// sendMessage(huid, "You have been challenged in WebWorm");
			}
		});
		db.center();
	}

	public void saveHighScore(int score, final Worm worm, String superpowers, String bonuspower) {
		greetingService.highScore(null, uid, score, cv.getCoordinateSpaceWidth(), cv.getCoordinateSpaceHeight(), superpowers, bonuspower, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				// Window.alert( caught.getStackTrace()[0].toString() );

				pause = false;
				if (worms.size() == 0) {
					updateCoordinates(cv, false);
					drawStartMessage(cv.getContext2d());
				}
			}

			@Override
			public void onSuccess(String result) {
				String[] split = result.split("\t");
				if (split.length > 3) {
					try {
						huid = split[0];
						long nuid = Long.parseLong(huid);
						highscoreholder = fuids.get(nuid);
						highscore = Integer
								.parseInt(split[split.length - 3]);
						highwidth = Integer
								.parseInt(split[split.length - 2]);
						highheight = Integer
								.parseInt(split[split.length - 1]);
					} catch (Exception e) {
					}
				}
				pause = false;
				if (worms.size() == 0) {
					updateCoordinates(cv, false);
					drawStartMessage(cv.getContext2d());
				}
			}
		});
	}

	boolean pause = false;

	class Worm {
		double r;
		double a;
		double s;
		String c;
		int d;
		double adiv;

		double tx;
		double ty;

		int left;
		int right;
		int leftv;
		int rightv;

		List<Double> xs;
		List<Double> ys;

		int i;
		int l;

		boolean quatel;
		boolean deflec;
		boolean extlif;

		int score;

		HorizontalPanel hp = new HorizontalPanel();
		TextBox scorebox = new TextBox();

		// ValueBox<Integer> scorebox = new ValueBox<Integer>();

		public Worm(String c, int left, int right, int leftv, int rightv,
				double angle) {
			this(c, left, right, leftv, rightv, angle, Webworm.this.lorcon,
					Webworm.this.criang, Webworm.this.quatel,
					Webworm.this.dipill, Webworm.this.deflec,
					Webworm.this.extlif);
		}

		public Worm(String c, int left, int right, int leftv, int rightv) {
			this(c, left, right, leftv, rightv, Math.PI / 2.0,
					Webworm.this.lorcon, Webworm.this.criang,
					Webworm.this.quatel, Webworm.this.dipill,
					Webworm.this.deflec, Webworm.this.extlif);
		}

		public Worm(String c, int left, int right, int leftv, int rightv,
				double angle, boolean lorcon, boolean criang, boolean quatel,
				boolean dipill, boolean deflec, boolean extlif) {
			r = 5.0;
			l = 24;
			i = 0;

			tx = -1.0;
			ty = -1.0;

			if (criang)
				adiv = 12.0;
			else
				adiv = 16.0;

			if (dipill)
				d = 12;
			else
				d = 18;

			if (lorcon)
				s = 6.5;
			else
				s = 8.5;

			this.quatel = quatel;
			this.deflec = deflec;
			this.extlif = extlif;

			score = 0;
			this.c = c;

			Canvas label = Canvas.createIfSupported();

			label.setSize("18px", "18px");
			label.setCoordinateSpaceWidth(18);
			label.setCoordinateSpaceHeight(18);
			Context2d context = label.getContext2d();
			context.clearRect(0.0, 0.0, 20.0, 20.0);
			context.setFillStyle(c);
			context.beginPath();
			context.arc(9.0, 10.0, 6.0, 0.0, Math.PI * 2.0);
			context.fill();
			context.closePath();
			scorebox.setSize("24px", "10px");
			scorebox.setPixelSize(24, 10);
			scorebox.setReadOnly(true);
			scorebox.setText("0");

			hp.add(label);
			hp.add(scorebox);

			hscore.add(hp);

			xs = new ArrayList<Double>();
			ys = new ArrayList<Double>();

			int w = cv.getCoordinateSpaceWidth();
			int h = cv.getCoordinateSpaceHeight();

			double x;
			double y;

			int ws = worms.size();
			if (ws == 0) {
				a = angle;
				x = w / 2.0;
				y = h - 10.0;
			} else if (ws == 1) {
				a = 0.0;
				x = 10.0;
				y = h / 2.0;
			} else {// if( ws == 2 ) {
				a = Math.PI;
				x = w - 10.0;
				y = h / 2.0;
			}

			xs.add(x);
			ys.add(y);

			this.left = left;
			this.right = right;
			this.leftv = leftv;
			this.rightv = rightv;
		}

		public void kill() {
			xs.clear();
			ys.clear();

			hscore.remove(hp);
			worms.remove(this);
			if (uid != null
					&& uid.length() > 0
					&& (score > highscore || (score == highscore && (cv
							.getCoordinateSpaceWidth()
							* cv.getCoordinateSpaceHeight() < highwidth
							* highheight)))) {
				// String name = fuids.get( Long.parseLong(uid) );
				if (uid != huid) {
					sendMessage(huid, "You have been challenged in WebWorm",
							score, this);
				} else {
					String superpowers = "";
					if (Webworm.this.lorcon)
						superpowers = superpowers.length() == 0 ? "lorcon"
								: "\tlorcon";
					if (Webworm.this.quatel)
						superpowers = superpowers.length() == 0 ? "quatel"
								: "\tquatel";
					if (Webworm.this.dipill)
						superpowers = superpowers.length() == 0 ? "dipill"
								: "\tdipill";
					if (Webworm.this.luck)
						superpowers = superpowers.length() == 0 ? "luck"
								: "\tluck";
					if (Webworm.this.extlif)
						superpowers = superpowers.length() == 0 ? "extlif"
								: "\textlif";
					if (Webworm.this.deflec)
						superpowers = superpowers.length() == 0 ? "deflec"
								: "\tdeflec";
					if (Webworm.this.criang)
						superpowers = superpowers.length() == 0 ? "criang"
								: "\tcriang";
					if (Webworm.this.mondes)
						superpowers = superpowers.length() == 0 ? "mondes"
								: "\tmondes";

					saveHighScore(score, this, superpowers, "");
				}
			}

			//if (timer != null && worms.size() == 0) timer.cancel();
		}

		public void setTarget(int x, int y) {
			tx = x;
			ty = y;
		}

		public void advance(Context2d context) {
			if (xs.size() == 0) {
				this.kill();
			} else {
				if (l < xs.size()) {
					i = Math.min(i, xs.size() - 1);
					double tx = xs.remove((i + 1) % xs.size());
					double ty = ys.remove((i + 1) % ys.size());
					draw(context, tx, ty, r + 2.0, "#000000", "#000000");
				} else {
					if (keyset.contains(left) || keyset.contains(leftv)) {
						a += Math.PI / adiv;
						tx = -1;
						// if( a >= Math.PI ) a -= 2.0*Math.PI;
					}

					if (keyset.contains(right) || keyset.contains(rightv)) {
						a -= Math.PI / adiv;
						tx = -1;
						// if( a < -Math.PI ) a += 2.0*Math.PI;
					}

					double ox = getX();
					double oy = getY();
					if (tx >= 0) {
						double angle = Math.atan2(oy - ty, tx - ox);
						double na = a / (2.0 * Math.PI);
						double fl = Math.floor(Math.abs(na));
						na = na >= 0 ? (na - fl) * 2.0 * Math.PI
								: (na + fl + 1.0) * 2.0 * Math.PI;
						na = na >= Math.PI ? na - 2.0 * Math.PI : na;

						double adel = Math.PI / adiv;
						double diffa = angle - na;
						double adiffa = Math.abs(diffa);
						if (adiffa < adel) {
							a = angle;
							tx = -1;
						} else if (adiffa < Math.PI) {
							a += angle < na ? -adel : adel;
						} else {
							a += angle < na ? adel : -adel;
						}
					}

					double dx = s * Math.cos(a);
					double dy = s * Math.sin(a);
					double x = ox + dx;
					double y = oy - dy;

					i = (i + 1) % l;
					if (xs.size() < l) {
						xs.add(i, x);
						ys.add(i, y);
					} else {
						double tx = xs.set(i, x);
						double ty = ys.set(i, y);
						draw(context, tx, ty, r + 2.0, "#000000", "#000000");
					}

					dx = (x - applex);
					dy = (y - appley);
					if (Math.sqrt(dx * dx + dy * dy) < appler + 1.0) {
						context.beginPath();
						context.setFillStyle("#000000");
						context.setStrokeStyle("#000000");
						context.arc(applex, appley, appler - 2.0, 0,
								2.0 * Math.PI);
						context.fill();
						context.stroke();
						context.closePath();

						score++;
						scorebox.setText("" + score);
						l += d;

						setApple(context);
					}

					ImageData id = context.getImageData(x, y, 1.0, 1.0);
					int blue = id.getBlueAt(0, 0);
					int red = id.getRedAt(0, 0);
					int green = id.getGreenAt(0, 0);

					int wbound = cv.getCoordinateSpaceWidth();
					int hbound = cv.getCoordinateSpaceHeight();

					if (x < 0 || y < 0 || x >= wbound || y >= hbound) {
						if (quatel) {
							if (x < 0)
								xs.set(i, x + wbound);
							else if (x >= wbound)
								xs.set(i, x - wbound);

							if (y < 0)
								ys.set(i, y + hbound);
							else if (y >= hbound)
								ys.set(i, y - hbound);
						} else if (deflec) {
							if (x < 0)
								a = -a + Math.PI;
							else if (x >= wbound)
								a = -a + Math.PI;

							if (y < 0)
								a = -a;
							else if (y >= hbound)
								a = -a;
						} else {
							l = 0;
							countLiving();
						}
					} else if (blue != 0 || green != 0 || red != 0) {
						if (extlif) {
							extlif = false;
							draw(context, x, y, r, c, "#111111");
						} else {
							l = 0;
							countLiving();
						}
					} else {
						draw(context, x, y, r, c, "#111111");
					}
				}
			}
		}

		public void draw(Context2d context, double tx, double ty, double tr,
				String fillcolor, String strokecolor) {
			context.setFillStyle(fillcolor);
			context.setStrokeStyle(strokecolor);
			context.beginPath();
			context.arc(tx, ty, tr, 0, 2.0 * Math.PI);
			context.fill();
			context.stroke();
			context.closePath();
		}

		public double getX() {
			return xs.get(i);
		}

		public double getY() {
			return ys.get(i);
		}
	};

	public void countLiving() {
		boolean stillliving = false;
		for (Worm w : worms) {
			if (w.l > 0) {
				stillliving = true;
				break;
			}
		}

		if (!stillliving) {
			killall();
		}
	}

	public void killall() {
		Set<Worm> tmpset = new HashSet<Worm>(worms);
		for (Worm w : tmpset) {
			w.kill();
		}
		tmpset.clear();
	}

	public int offset = 30;
	public int adw = 160;

	public void updateCoordinates(Canvas cv, boolean init) {
		final Context2d context = cv.getContext2d();
		if (init) {
			context.getCanvas().setWidth(w);
			context.getCanvas().setHeight(h - offset);
			cv.setWidth(w + "px");
			cv.setHeight((h - offset) + "px");
			cv.setCoordinateSpaceWidth(context.getCanvas().getWidth());
			cv.setCoordinateSpaceHeight(context.getCanvas().getHeight());
		}

		context.setFillStyle("#000000");
		context.fillRect(0, 0, cv.getCoordinateSpaceWidth(),
				cv.getCoordinateSpaceHeight());

		for (Worm w : worms) {
			List<Double> xs = w.xs;
			List<Double> ys = w.ys;
			for (int i = 0; i < xs.size(); i++) {
				double x = xs.get(i);
				double y = ys.get(i);
				w.draw(context, x, y, w.r, w.c, "#111111");
			}
		}
		if (!init)
			setApple(context);
	}

	public native void console(String s) /*-{
		if ($wnd.console)
			$wnd.console.log(s);
	}-*/;

	public void drawStartMessage(Context2d context) {
		if (audio != null && audio.isAttached() && audio.isEnabled() && !audio.isPaused()) {
			audio.pause();
			audio.setCurrentTime(0.0);
		}

		if (w > 480 && h > 320) {
			infohtml.setHTML("Press enter or mouseclick to add new worm (<- and -> to control)<br>"
					+ "Free Superpower of the month: Deflection<br>"
					+
					// "Invite a friend and get the Critical angle Superpower for free!<br>"
					// +
					"The highsore amoung your friends is: "
					+ highscoreholder
					+ " with "
					+ highscore
					+ " points ("
					+ highwidth
					+ "x"
					+ highheight
					+ ")<br>"
					+ "Beat the highscore, challenge friends and get a Superpower of your choice for free");
			info.center();
			gplusgo();
			fbParse();
		}

		/*
		 * String msg =
		 * "Press enter or mouseclick to add new worm (<- and -> to control)";
		 * context.setFillStyle("#eeeeee"); TextMetrics tm =
		 * context.measureText( msg ); context.fillText(msg,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0);
		 * 
		 * String jscore =
		 * "ãƒ�ã‚¤ã‚¹ã‚³ã‚¢ã‚’ãƒ“ãƒ¼ãƒˆã�¨ç„¡æ–™ã�§ã�Šå¥½ã�¿ã�®ã‚¹ãƒ¼ãƒ‘ãƒ¼ãƒ‘ãƒ¯ãƒ¼ã‚’å¾—ã‚‹"
		 * ; tm = context.measureText( jscore ); context.fillText(jscore,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0+90);
		 * 
		 * String japs =
		 * "æ–°ã�—ã�„ãƒ¯ãƒ¼ãƒ ã‚’è¿½åŠ ã�™ã‚‹ã�«ã�¯ã€�Enterã‚­ãƒ¼ã‚’æŠ¼ã�—ã�¾ã�™ï¼ˆ<ã‚’ - ã�¨ - >ã‚’åˆ¶å¾¡ã�™ã‚‹ï¼‰"
		 * ; tm = context.measureText( japs ); context.fillText(japs,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0+60);
		 * 
		 * String disc = "Free Superpower of the month: Deflection"; tm =
		 * context.measureText( disc ); context.fillText(disc,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0+30);
		 * 
		 * String invt =
		 * "Invite a friend and get the Critical angle Superpower for free!"; tm
		 * = context.measureText( invt ); context.fillText(invt,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0-30);
		 * 
		 * String nipp =
		 * "ãƒ�ã‚¤ã‚¹ã‚³ã‚¢: "+highscoreholder+" ã�¨ "+highscore+" ãƒ�ã‚¤ãƒ³ãƒˆ ("
		 * +highwidth+"x"+highheight+")"; tm = context.measureText( nipp );
		 * context.fillText(nipp,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0-120);
		 * 
		 * String allt =
		 * "All time highsore is: "+highscoreholder+" with "+highscore
		 * +" points ("+highwidth+"x"+highheight+")"; tm = context.measureText(
		 * allt ); context.fillText(allt,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0-90);
		 * 
		 * String beat =
		 * "Beat the all time highscore and get a Superpower of your choice for free"
		 * ; tm = context.measureText( beat ); context.fillText(beat,
		 * (context.getCanvas().getWidth()-tm.getWidth())/2.0,
		 * context.getCanvas().getHeight()/2.0-60);
		 */
	}

	String uid = null;

	public Element loginButton() {
		// <fb:login-button show-faces="true" width="200"
		// max-rows="1"></fb:login-button>

		/*
		 * Element elem = Document.get().createElement("fb:login-button");
		 * elem.setAttribute("width", "50"); //elem.setAttribute("show-faces",
		 * "true"); elem.setAttribute("max-rows", "1");
		 * //elem.setAttribute("perms",
		 * "user_birthday,friends_birthday,user_relationships,friends_relationships"
		 * ); elem.setId("fblogin");
		 */

		// <div class="fb-login-button" scope="email,user_checkins">

		Element elem = Document.get().createElement("div");
		elem.setAttribute("class", "fb-login-button");
		// elem.setAttribute("scope", "user");

		return elem;
	}

	public native void sendMessage(String fuids, String body, int score,
			Worm worm) /*-{
		var ths = this;

		var requestCallback = function(response) {
			if ($wnd.console)
				$wnd.console.log(response);
			if (response && response.request) {
				ths.@org.simmi.client.Webworm::makeHighScore(ILorg/simmi/client/Webworm$Worm;)( score, worm );
			} else {
				ths.@org.simmi.client.Webworm::saveHighScore(ILorg/simmi/client/Webworm$Worm;Ljava/lang/String;Ljava/lang/String;)( score, worm, null, null );
			}
		}

		$wnd.FB.ui({
			method : 'apprequests',
			message : body
		}, requestCallback);
	}-*/;

	public native void fbParse() /*-{
		if ($wnd.FB !== undefined) {
			$wnd.FB.XFBML.parse();
		}
	}-*/;

	public native void fbInit(String login) /*-{
		var ths = this;

		$wnd.fbAsyncInit = function() {
			$wnd.FB.init({
				appId : '215097581865564',
				status : true,
				cookie : true,
				xfbml : true,
				oauth : true
			});

			try {
				$wnd.console.log("login null");
				$wnd.FB
						.getLoginStatus(function(response) {
							$wnd.console.log("inside login response");
							try {
								$wnd.console.log(response.status);
								if (response.status === 'connected') {
									// the user is logged in and has authenticated your
									// app, and response.authResponse supplies
									// the user's ID, a valid access token, a signed
									// request, and the time the access token 
									// and signed request each expire
									var uid = response.authResponse.userID;
									var accessToken = response.authResponse.accessToken;
									ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( uid );
									$wnd.FB.XFBML.parse();
								} else if (response.status === 'not_authorized') {
									// the user is logged in to Facebook, 
									// but has not authenticated your app
									ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( "" );
								} else {
									// the user isn't logged in to Facebook.
									ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( "" );
								}
							} catch (e) {
								$wnd.console.log("getLoginStatus error");
								$wnd.console.log(e);
							}
						});
			} catch (e) {
				$wnd.console.log("gls error");
				$wnd.console.log(e);
			}
		};
	}-*/;

	public native void ieSpec() /*-{
		var s = this;
		function whatKeyDown(evt) {
			var keycode = evt.keyCode;
			s.@org.simmi.client.Webworm::keyDown(I)( keycode );
		}
		function whatKeyUp(evt) {
			var keycode = evt.keyCode;
			s.@org.simmi.client.Webworm::keyUp(I)( keycode );
		}
		$wnd.addEventListener('keydown', whatKeyDown, true);
		$wnd.addEventListener('keyup', whatKeyUp, true);
	}-*/;

	/*
	 * if (response.session) { var uid = response.session.uid;
	 * ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( uid ); }
	 * else { ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( ""
	 * ); } $wnd.FB.XFBML.parse();
	 */

	SimplePanel sp;
	SimplePanel splus;
	Element like;
	Element plus;
	Element login;

	public void setUserId(String val) {
		uid = val;

		NodeList<Node> childs = sp.getElement().getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			sp.getElement().removeChild(childs.getItem(i));
		}

		if (uid.length() > 0)
			sp.getElement().appendChild(like);
		else
			sp.getElement().appendChild(login);

		if (uid.length() > 0) {
			getSuperPowers(uid, null);
			sendListener(uid);
		}

		if (uid != null && uid.length() > 0)
			fetchMe();
		else
			drawStartMessage(cv.getContext2d());
	}

	public native void sendListener(String uid) /*-{
		var ths = this;
		$wnd.FB.Event
				.subscribe(
						'edge.create',
						function(response) {
							ths.@org.simmi.client.Webworm::getSuperPowers(Ljava/lang/String;Ljava/lang/String;)( uid, "criang" );
						});

		$wnd.FB.Event
				.subscribe(
						'comment.create',
						function(response) {
							ths.@org.simmi.client.Webworm::getSuperPowers(Ljava/lang/String;Ljava/lang/String;)( uid, "criang" );
						});

		$wnd.FB.Event
				.subscribe(
						'message.send',
						function(response) {
							$wnd.console.log("message sent " + uid);
							ths.@org.simmi.client.Webworm::getSuperPowers(Ljava/lang/String;Ljava/lang/String;)( uid, "criang" );
						});
	}-*/;

	Set<String> powerset = new HashSet<String>();

	public void getSuperPowers(final String uid, final String power) {
		greetingService.greetServer(uid, power, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Browser.getWindow().getConsole()
						.log("powerset: " + result + " " + power + " " + uid);

				String[] split = result.split("\t");
				powerset = new HashSet<String>(Arrays.asList(split));

				/*
				 * if( powerset.contains("criang") ) criang = true; if(
				 * powerset.contains("lorcon") ) lorcon = true; if(
				 * powerset.contains("quatel") ) quatel = true; if(
				 * powerset.contains("deflec") ) deflec = true; if(
				 * powerset.contains("luck") ) luck = true; if(
				 * powerset.contains("extlif") ) extlif = true; if(
				 * powerset.contains("dipill") ) dipill = true;
				 * 
				 * console( criang + " uh " + lorcon );
				 */
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});

		// insertSuperPowers( new HashSet<String>( Arrays.asList( new String[]
		// {power} ) ) );
	}

	/*
	 * public void insertSuperPowers( Set<String> powers ) { greetingService. }
	 */

	public native void fbBuy() /*-{
		var callback = function(data) {
			return true;
		};

		var obj = {
			method : 'pay',
			action : 'buy_credits',
		};
		$wnd.FB.ui(obj, callback);
	}-*/;

	DialogBox currentDb;

	public void updatePowers(String str) {
		if (str.contains("lorcon")) {
			powerset.add("lorcon");
			lorcon = true;
		} else if (str.contains("quatel")) {
			powerset.add("quatel");
			quatel = true;
		} else if (str.contains("mondes")) {
			powerset.add("mondes");
			mondes = true;
		} else if (str.contains("criang")) {
			powerset.add("criang");
			criang = true;
		} else if (str.contains("extlif")) {
			powerset.add("extlif");
			extlif = true;
		} else if (str.contains("luck")) {
			powerset.add("luck");
			luck = true;
		} else if (str.contains("dipill")) {
			powerset.add("dipill");
			dipill = true;
		} else if (str.contains("deflec")) {
			powerset.add("deflec");
			deflec = true;
		}

		if (currentDb != null)
			currentDb.hide();
	}

	public native void fbPay(String order_info) /*-{
		var ths = this;
		var callback = function(data) {
			if (data['order_id']) {
				$wnd.console.log(data);
				ths.@org.simmi.client.Webworm::updatePowers(Ljava/lang/String;)( order_info );

				return true;
			} else {
				return false;
			}
		};
		// calling the API ...
		var obj = {
			method : 'pay',
			order_info : order_info,
			action : 'buy_item',
			dev_purchase_params : {
				'oscif' : true
			}
		};
		$wnd.FB.ui(obj, callback);
	}-*/;

	public void addSuperPower(FlexTable table, Widget form, boolean selected,
			String cost, String html, int row, String id,
			ValueChangeHandler<Boolean> handler, String status) {
		table.setHTML(row, 0, html);
		table.setText(row, 1, cost);

		if (status != null) {
			if (status.length() > 0) {
				table.setText(row, 2, status);
			} else {
				CheckBox check = new CheckBox();
				check.setValue(selected);
				check.addValueChangeHandler(handler);
				table.setWidget(row, 2, check);
			}
		} else {
			/*
			 * FormPanel form = new FormPanel(); form.setAction(
			 * "https://www.paypal.com/cgi-bin/webscr" ); form.setMethod(
			 * FormPanel.METHOD_POST );
			 * 
			 * HorizontalPanel holder = new HorizontalPanel();
			 * holder.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
			 * holder.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
			 * 
			 * Hidden cmd = new Hidden("cmd", "_s-xclick"); Hidden
			 * hosted_button_id = new Hidden("hosted_button_id", id); Hidden
			 * fbid = new Hidden("custom", uid); Hidden notifyurl = new
			 * Hidden("notify_url", "http://webwormgame.appspot.com"); Image img
			 * = new Image(
			 * "https://www.paypalobjects.com/en_US/i/scr/pixel.gif" );
			 * img.setAltText(""); img.setWidth("1"); img.setHeight("1");
			 * 
			 * InputElement iel = Document.get().createImageInputElement();
			 * iel.setName("submit"); iel.setSrc(
			 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" );
			 * iel.setAlt( "PayPal - The safer, easier way to pay online!" );
			 * 
			 * SimplePanel imageinput = new SimplePanel();
			 * imageinput.getElement().appendChild( iel );
			 * 
			 * holder.add( cmd ); holder.add( hosted_button_id ); holder.add(
			 * fbid ); holder.add( notifyurl ); holder.add( imageinput );
			 * holder.add( img );
			 * 
			 * form.add( holder );
			 */
			// SimplePanel sp = new SimplePanel();
			// sp.getElement().appendChild( form );
			table.setWidget(row, 2, form);

			/*
			 * Anchor pay = new Anchor("Credits"); pay.addClickHandler( new
			 * ClickHandler() {
			 * 
			 * @Override public void onClick(ClickEvent event) { fbPay( "abc123"
			 * ); } }); table.setWidget(row, 3, pay);
			 */
		}
	}

	public native void gplusgo() /*-{
		$wnd.gapi.plusone.go();
	}-*/;

	public native void setGadsPars() /*-{
		$wnd.google_ad_client = "ca-pub-7204381538404733";
		$wnd.google_ad_slot = "9414308425";
		$wnd.google_ad_width = 160;
		$wnd.google_ad_height = 600;
	}-*/;

	public native void fetchMe() /*-{
		var ths = this;
		$wnd.FB
				.api(
						'/me',
						function(response) {
							ths.@org.simmi.client.Webworm::putFriend(Ljava/lang/String;Ljava/lang/String;)( response.id, response.name );
							ths.@org.simmi.client.Webworm::fetchFriends()();
						});
	}-*/;

	public native void fetchFriends() /*-{
		var ths = this;
		try {
			$wnd.FB
					.api(
							'/me/friends',
							function(response) {
								for (i = 0; i < response.data.length; i++) {
									var frd = response.data[i];
									ths.@org.simmi.client.Webworm::putFriend(Ljava/lang/String;Ljava/lang/String;)( frd.id, frd.name );
								}
								ths.@org.simmi.client.Webworm::fetchHighScores()();
							});
		} catch (e) {
			$wnd.console.log(e);
		}
	}-*/;

	public void fetchHighScores() {
		StringBuilder sb = new StringBuilder();
		for (long fuid : fuids.keySet()) {
			if (sb.length() == 0)
				sb.append("" + fuid);
			else
				sb.append("," + fuid);
		}
		String friends = sb.toString();
		greetingService.highScore(friends, uid, 0, 0, 0, "", "",
				new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						String[] split = result.split("\t");
						if (split.length > 3) {
							try {
								console("erm ");
								huid = split[0];
								highscoreholder = fuids.get(Long
										.parseLong(huid));
								highscore = Integer
										.parseInt(split[split.length - 3]);
								highwidth = Integer
										.parseInt(split[split.length - 2]);
								highheight = Integer
										.parseInt(split[split.length - 1]);
							} catch (Exception e) {
							}
						}

						if (worms.size() == 0) {
							drawStartMessage(cv.getContext2d());
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						console("epic failure " + caught.getMessage());
						console("epic " + caught.getStackTrace()[0].toString());
					}
				});
	}

	Map<Long, String> fuids = new HashMap<Long, String>();

	public void putFriend(String uid, String name) {
		fuids.put(Long.parseLong(uid), name);
	}

	public void resize(RootPanel rp, int ww, int hh) {
		w = ww;
		h = hh;
		rp.setWidth(w + "px");
		rp.setHeight(h + "px");

		// console( w + " " + Window.getClientWidth() + " " + h + " " +
		// Window.getClientHeight() );
		// rp.setWidth( (w-100)+"px" );
		// rp.setHeight( (h-100)+"px" );
		updateCoordinates(cv, true);

		/*
		 * if( popup != null ) { if( h >= 720 && w >= 720 ) {
		 * popup.setPopupPosition(0, (h-600)/2); popup.show(); } else
		 * popup.hide(); }
		 */

		if (info.isVisible() && w > info.getOffsetWidth()
				&& h > info.getOffsetHeight()) {
			info.setPopupPosition((w - info.getOffsetWidth()) / 2,
					(h - info.getOffsetHeight()) / 2);
		} else
			info.hide();

		if (w * h <= 320 * 480) {
			lorcon = true;
			criang = true;
		} else {
			if (!powerset.contains("lorcon"))
				lorcon = false;
			if (!powerset.contains("criang"))
				criang = false;
			delay = 40;
		}
	}

	public native boolean init() /*-{
		$wnd.requestAnimationFrame = $wnd.requestAnimationFrame
				|| $wnd.mozRequestAnimationFrame
				|| $wnd.webkitRequestAnimationFrame
				|| $wnd.msRequestAnimationFrame;
		$wnd.cancelAnimationFrame = $wnd.cancelAnimationFrame
				|| $wnd.mozCancelAnimationFrame;

		var s = this;
		$wnd.step = function(time) {
			s.@org.simmi.client.Webworm::step(D)( time );
		}

		return $wnd.requestAnimationFrame == null;
	}-*/;

	TextBox timebox;
	long count = 0;
	int currentFrame;

	public void step(double time) {
		// Browser.getWindow().getConsole().log( "erm" );
		if (worms.size() == 0) {
			// Browser.getWindow().getConsole().log("io");
			drawStartMessage(cv.getContext2d());
			if (timer == null)
				cancelAnimationFrame(currentFrame);
			else
				timer.cancel(); // dunno
			return;
			//this.cancel();
		}
		if (!pause) {
			Context2d context = cv.getContext2d();
			for (Worm w : worms) {
				w.advance(context);
			}
			if ((count++) % 50 == 0) {
				if (timebox != null)
					timebox.setText("" + (count / 50));
			}
			if (timer == null)
				requestAnimationFrame();
		}
	}

	public native void cancelAnimationFrame(int id) /*-{
		$wnd.cancelAnimationFrame(id);
	}-*/;

	public native int requestAnimationFrame() /*-{
		return $wnd.requestAnimationFrame($wnd.step);
	}-*/;

	int w, h;
	Canvas cv;
	Timer timer = null;
	Set<Worm> worms;
	HorizontalPanel hscore;
	PopupPanel popup;
	PopupPanel info;
	HTML infohtml;
	int delay;

	public void onModuleLoad() {
		Window.enableScrolling(false);

		final Element donateEl = Document.get().getElementById("donate");
		donateEl.removeFromParent();

		final Widget mondesWidget;
		final Widget lorconWidget;
		final Widget luckWidget;
		final Widget quatelWidget;
		final Widget criangWidget;
		final Widget deflecWidget;
		final Widget dipillWidget;
		final Widget extlifWidget;

		boolean nreq = init();

		String fbuid = null;
		NodeList<Element> nl = Document.get().getElementsByTagName("meta");
		int i;
		for (i = 0; i < nl.getLength(); i++) {
			Element e = nl.getItem(i);
			String prop = e.getAttribute("property");
			if (prop.equals("fbuid")) {
				// setUserId( e.getAttribute("content") );
				fbuid = e.getAttribute("content");
				break;
			}
		}

		final Element mondesEl = Document.get().getElementById("mondes");
		mondesEl.removeFromParent();
		final Element lorconEl = Document.get().getElementById("lorcon");
		lorconEl.removeFromParent();
		final Element luckEl = Document.get().getElementById("luck");
		luckEl.removeFromParent();
		final Element quatelEl = Document.get().getElementById("quatel");
		quatelEl.removeFromParent();
		final Element criangEl = Document.get().getElementById("criang");
		criangEl.removeFromParent();
		final Element deflecEl = Document.get().getElementById("deflec");
		deflecEl.removeFromParent();
		final Element dipillEl = Document.get().getElementById("dipill");
		dipillEl.removeFromParent();
		final Element extlifEl = Document.get().getElementById("extlif");
		extlifEl.removeFromParent();

		if (fbuid != null) {
			// offset = 120;

			final Button mondesAnc = new Button("Buy");
			mondesAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("mondes");
				}
			});
			mondesWidget = mondesAnc;
			final Button lorconAnc = new Button("Buy");
			lorconAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("lorcon");
				}
			});
			lorconWidget = lorconAnc;
			final Button luckAnc = new Button("Buy");
			luckAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("luck");
				}
			});
			luckWidget = luckAnc;
			final Button quatelAnc = new Button("Buy");
			quatelAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("quatel");
				}
			});
			quatelWidget = quatelAnc;
			final Button criangAnc = new Button("Buy");
			criangAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("criang");
				}
			});
			criangWidget = criangAnc;
			final Button deflecAnc = new Button("Buy");
			deflecAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("deflec");
				}
			});
			deflecWidget = deflecAnc;
			final Button dipillAnc = new Button("Buy");
			dipillAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("dipill");
				}
			});
			dipillWidget = dipillAnc;
			final Button extlifAnc = new Button("Buy");
			extlifAnc.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fbPay("extlif");
				}
			});
			extlifWidget = extlifAnc;
		} else {
			console("eldor");

			mondesEl.getStyle().setDisplay(Display.INLINE);
			lorconEl.getStyle().setDisplay(Display.INLINE);
			luckEl.getStyle().setDisplay(Display.INLINE);
			quatelEl.getStyle().setDisplay(Display.INLINE);
			criangEl.getStyle().setDisplay(Display.INLINE);
			deflecEl.getStyle().setDisplay(Display.INLINE);
			dipillEl.getStyle().setDisplay(Display.INLINE);
			extlifEl.getStyle().setDisplay(Display.INLINE);

			mondesWidget = new SimplePanel();
			mondesWidget.getElement().appendChild(mondesEl);
			lorconWidget = new SimplePanel();
			lorconWidget.getElement().appendChild(lorconEl);
			luckWidget = new SimplePanel();
			luckWidget.getElement().appendChild(luckEl);
			quatelWidget = new SimplePanel();
			quatelWidget.getElement().appendChild(quatelEl);
			criangWidget = new SimplePanel();
			criangWidget.getElement().appendChild(criangEl);
			deflecWidget = new SimplePanel();
			deflecWidget.getElement().appendChild(deflecEl);
			dipillWidget = new SimplePanel();
			dipillWidget.getElement().appendChild(dipillEl);
			extlifWidget = new SimplePanel();
			extlifWidget.getElement().appendChild(extlifEl);
		}
		donateEl.getStyle().setDisplay(Display.INLINE);

		final RootPanel rp = RootPanel.get("content");
		Style st = rp.getElement().getStyle();

		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		st.setBackgroundColor("#222222");

		final VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		vp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		st = vp.getElement().getStyle();
		st.setBackgroundColor("#333333");

		// FocusPanel fp = new FocusPanel();
		// fp.setWidth( "100%" );
		// fp.setHeight( "100%" );
		cv = Canvas.createIfSupported();
		st = cv.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		cv.setWidth("100%");
		cv.setHeight("100%");

		vp.setWidth("100%");
		vp.setHeight("100%");
		// fp.add( cv );

		worms = new HashSet<Worm>();
		w = Window.getClientWidth() - adw;
		h = Window.getClientHeight();
		rp.setWidth(w + "px");
		rp.setHeight(h + "px");

		if (w * h <= 320 * 480) {
			lorcon = true;
			criang = true;
			delay = 60;
		} else
			delay = 40;

		// w = Window.getClientWidth();
		// h = Window.getClientHeight();
		updateCoordinates(cv, true);
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth() - adw;
				int h = event.getHeight();
				resize(rp, w, h);
			}
		});

		timebox = new TextBox();
		timebox.setReadOnly(true);
		timebox.setPixelSize(32, 10);
		timebox.setSize("32px", "10px");

		// requestAnimationFrame();
		if (nreq) {
			timer = new Timer() {
				long count = 0;

				@Override
				public void run() {
					step(0);
				}

				public void cancel() {
					super.cancel();
					count = 0;
				}
			};
		}

		String useragent = Window.Navigator.getUserAgent();
		cv.addMouseDownHandler(this);
		cv.addMouseUpHandler(this);
		cv.addMouseMoveHandler(this);
		cv.addTouchStartHandler(this);
		cv.addTouchEndHandler(this);
		cv.addTouchMoveHandler(this);

		if (useragent.contains("MSIE")) {
			ieSpec();
		} else {
			cv.addKeyDownHandler(this);
			cv.addKeyUpHandler(this);
		}

		cv.setFocus(true);

		// Style style;
		HorizontalPanel hp = new HorizontalPanel();
		// hp.getElement().getStyle().setColor("#ffffff");
		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		// hp.setHeight("10px");
		hp.setSpacing(10);

		/*
		 * Button spcredit = new Button("SP Credits"); spcredit.addClickHandler(
		 * new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) { fbPay( "abc123" );
		 * } });
		 */

		Button power = new Button("Superpowers");
		power.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (uid == null || uid.length() == 0) {
					DialogBox dbox = new DialogBox(true, true);
					dbox.setText("Superpowers"); // ã‚¹ãƒ¼ãƒ‘ãƒ¼ãƒ‘ãƒ¯ãƒ¼");
					dbox.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							// pause =
						}
					});

					HTML html = new HTML("You must be logged into facebook to enable Superpowers");
					dbox.add(html);
					dbox.center();
				} else {
					DialogBox dbox = new DialogBox(true, true);
					dbox.setText("Superpowers"); // ã‚¹ãƒ¼ãƒ‘ãƒ¼ãƒ‘ãƒ¯ãƒ¼");

					/*
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="GTDHG7AXUUWWE"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="5GSE569LBQRN4"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="RZEJDBKH4VHJ2"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="DGR8KG2HZVPVG"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="X3LQBSTXA686A"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="W8FC3N9EJBEQL"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="CT6VST75J8Q6J"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="CT6VST75J8Q6J"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 * 
					 * 
					 * <form target="paypal"
					 * action="https://www.paypal.com/cgi-bin/webscr"
					 * method="post"> <input type="hidden" name="cmd"
					 * value="_s-xclick"> <input type="hidden"
					 * name="hosted_button_id" value="93ULNAMGHQ9VS"> <input
					 * type="image" src=
					 * "https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif"
					 * border="0" name="submit"
					 * alt="PayPal - The safer, easier way to pay online!"> <img
					 * alt="" border="0"
					 * src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
					 * width="1" height="1"> </form>
					 */

					ValueChangeHandler<Boolean> lorconHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							lorcon = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> quatelHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							quatel = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> criangHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							criang = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> deflecHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							deflec = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> dipillHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							dipill = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> extlifHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							extlif = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> luckHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							luck = event.getValue();
						}
					};

					ValueChangeHandler<Boolean> mondesHandler = new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							mondes = event.getValue();

							adw = mondes ? 0 : 160;
							final Element ads = Document.get().getElementById(
									"ads");
							ads.getStyle().setDisplay(
									mondes ? Display.NONE : Display.BLOCK);

							resize(rp, Window.getClientWidth() - adw,
									Window.getClientHeight());
							// if( mondes = true ) popup.hide();
							// else popup.show();
						}
					};

					FlexTable table = new FlexTable();
					addSuperPower(
							table,
							lorconWidget,
							lorcon,
							"$5",
							"<b>Lorentz contraction</b><br>As a stationary observer watching your worm in a 3D wormkowski space, you experience a relativistic length contraction in the direction of the worm movement",
							0, "5GSE569LBQRN4", lorconHandler,
							powerset.contains("lorcon") ? "" : null);
					addSuperPower(
							table,
							quatelWidget,
							quatel,
							"$4",
							"<b>Quantum teleportation</b><br>As your worm exists in information space it is subject to the law of entanglement-assisted teleportation resulting in the ability to travel through the walls",
							1, "RZEJDBKH4VHJ2", quatelHandler,
							powerset.contains("quatel") ? "" : null);
					addSuperPower(
							table,
							criangWidget,
							criang,
							"$3",
							"<b>Critical angle</b><br>Your worm is able to make more steep turns",
							2, "DGR8KG2HZVPVG", criangHandler,
							powerset.contains("criang") ? "" : null);
					addSuperPower(
							table,
							deflecWidget,
							deflec,
							"$3",
							"<b>Deflection</b><br>If the angle of impact is small enough, your worm will deflect from the walls",
							3, "X3LQBSTXA686A", deflecHandler, "");
					addSuperPower(
							table,
							luckWidget,
							luck,
							"$2",
							"<b>Luck</b><br>Like the apple that fell on Newtons head, the apples seem to fall closer to the mouth the worm, defying statistical laws",
							4, "W8FC3N9EJBEQL", luckHandler,
							powerset.contains("luck") ? "" : "Out of stock");
					addSuperPower(table, dipillWidget, dipill, "$6",
							"<b>Diet pill</b><br>Eat more, grow less!", 5,
							"CT6VST75J8Q6J", dipillHandler,
							powerset.contains("dipill") ? "" : null);
					addSuperPower(
							table,
							extlifWidget,
							extlif,
							"$1",
							"<b>Extra life</b><br>Get one chance of passing through if hitting a worm",
							6, "93ULNAMGHQ9VS", extlifHandler,
							powerset.contains("extlif") ? "" : null);
					addSuperPower(
							table,
							mondesWidget,
							mondes,
							"$1",
							"<b>Ad Monolith destroyer</b><br>If you could just get rid of the monolith from the film 2001: A Space Odyssey and make the film understandable. Besides, it probably just contained ads anyways",
							7, "GTDHG7AXUUWWE", mondesHandler,
							powerset.contains("mondes") ? "" : null);

					if (mondesWidget instanceof Button) {
						Button buyCredit = new Button("Buy credits");
						buyCredit.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								fbBuy();
							}
						});
						table.setWidget(8, 0, buyCredit);
					}
					dbox.add(table);

					currentDb = dbox;
					dbox.center();
				}
			}
		});

		/*
		 * <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		 * <input type="hidden" name="cmd" value="_s-xclick"> <input
		 * type="hidden" name="hosted_button_id" value="L4TC92APSZDHW"> <input
		 * type="image"
		 * src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif"
		 * border="0" name="submit"
		 * alt="PayPal - The safer, easier way to pay online!"> <img alt=""
		 * border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
		 * width="1" height="1"> </form>
		 */

		/*
		 * <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		 * <input type="hidden" name="cmd" value="_s-xclick"> <input
		 * type="hidden" name="hosted_button_id" value="8245UZK73CU5U"> <input
		 * type="image"
		 * src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif"
		 * border="0" name="submit"
		 * alt="PayPal - The safer, easier way to pay online!"> <img alt=""
		 * border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif"
		 * width="1" height="1"> </form>
		 */

		/*
		 * FormPanel form = new FormPanel(); form.setAction(
		 * "https://www.paypal.com/cgi-bin/webscr" ); form.setMethod(
		 * FormPanel.METHOD_POST );
		 * 
		 * HorizontalPanel holder = new HorizontalPanel();
		 * holder.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		 * holder.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		 * 
		 * Hidden cmd = new Hidden("cmd", "_s-xclick"); Hidden hosted_button_id
		 * = new Hidden("hosted_button_id", "8245UZK73CU5U"); Image img = new
		 * Image( "https://www.paypalobjects.com/en_US/i/scr/pixel.gif" );
		 * img.setAltText(""); img.setWidth("1"); img.setHeight("1");
		 * 
		 * InputElement iel = Document.get().createImageInputElement();
		 * iel.setName("submit"); iel.setSrc(
		 * "https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif" );
		 * iel.setAlt( "PayPal - The safer, easier way to pay online!" );
		 * 
		 * SimplePanel imageinput = new SimplePanel();
		 * imageinput.getElement().appendChild( iel );
		 * 
		 * holder.add( cmd ); holder.add( hosted_button_id ); holder.add(
		 * imageinput ); holder.add( img ); form.add( holder );
		 */

		hscore = new HorizontalPanel();
		hscore.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hscore.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		hscore.setWidth("160px");
		// hscore.setHeight("50px");

		Label timelabel = new Label("Time:");
		timelabel.getElement().getStyle().setColor("#eeeeee");
		// timelabel.setHeight("50px");

		HorizontalPanel coverpanel = new HorizontalPanel();
		coverpanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		coverpanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		coverpanel.setSpacing(5);
		coverpanel.setWidth("100%");
		// coverpanel.setHeight("50px");

		HorizontalPanel timepanel = new HorizontalPanel();
		timepanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		timepanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		timepanel.setWidth("150px");
		// timepanel.setHeight("50px");

		timepanel.add(timelabel);
		timepanel.add(timebox);

		plus = Document.get().createElement("g:plusone");
		plus.setAttribute("size", "small");

		like = Document.get().createElement("fb:like");
		like.setAttribute("width", "200");
		like.setAttribute("font", "arial");
		like.setAttribute("layout", "button_count");
		like.setAttribute("colorscheme", "dark");
		like.setAttribute("send", "true");
		like.setAttribute("href", "http://apps.facebook.com/webwormgame");
		like.setId("fblike");

		login = loginButton();

		sp = new SimplePanel();
		splus = new SimplePanel();

		SimplePanel form = new SimplePanel();
		form.getElement().appendChild(donateEl);

		hp.add(sp);
		// hp.add( spcredit );
		hp.add(power);
		hp.add(form);
		hp.add(splus);

		/*
		 * final Element ads = Document.get().getElementById("ads");
		 * ads.removeFromParent(); if( fbuid == null ) { if( ads != null ) {
		 * popup = new PopupPanel(); Style style =
		 * popup.getElement().getStyle(); style.setBorderWidth(0.0, Unit.PX);
		 * style.setMargin(0.0, Unit.PX);
		 * 
		 * //final SimplePanel spopup = new SimplePanel();
		 * //spopup.setWidth("160px"); //spopup.setHeight("600px");
		 * 
		 * //<script type="text/javascript"
		 * //src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
		 * //</script> //popup.add( spopup );
		 * 
		 * popup.getElement().appendChild( ads ); popup.setPopupPosition(0,
		 * (h-600)/2); popup.setPixelSize(156, 594);
		 * 
		 * if( h >= 720 ) { popup.show(); popup.setSize(156+"px", 594+"px"); }
		 * 
		 * /*popup.setPopupPositionAndShow( new PositionCallback() {
		 * 
		 * @Override public void setPosition(int offsetWidth, int offsetHeight)
		 * { popup.setPopupPosition(0, (h-600)/2); popup.setPixelSize(156, 594);
		 * /*setGadsPars(); ScriptElement adscript =
		 * Document.get().createScriptElement();
		 * adscript.setType("text/javascript");
		 * adscript.setSrc("http://pagead2.googlesyndication.com/pagead/show_ads.js"
		 * ); adscript.setAttribute("async", "true");
		 * spopup.getElement().appendChild( adscript ); } });* } }
		 */
		fbInit(fbuid);
		// fetchHighScores();

		String id = "facebook-jssdk";
		ScriptElement se = Document.get().createScriptElement();
		se.setId(id);
		se.setAttribute("async", "true");
		se.setSrc("//connect.facebook.net/en_US/all.js");
		Document.get().getElementById("fb-root").appendChild(se);

		// sp.getElement().appendChild( e );
		// sp.setWidth("170px");

		splus.getElement().appendChild(plus);

		// splus.setWidth("100px");

		// HorizontalPanel hsp = new HorizontalPanel();
		// hsp.setSpacing(2);
		// hsp.add( sp );
		// hsp.add( splus );

		// coverpanel.add( hsp );
		// coverpanel.add( form );
		coverpanel.add(hscore);
		coverpanel.add(timepanel);

		/*
		 * if( fbuid != null ) { SimplePanel adsPanel = new SimplePanel();
		 * adsPanel.getElement().appendChild( ads ); adsPanel.setPixelSize( 728,
		 * 90 ); adsPanel.setSize("728px", "90px"); vp.add( adsPanel ); offset =
		 * 120; }
		 */
		vp.add(coverpanel);
		vp.add(cv);
		// vp.add( hp );

		HorizontalPanel links = new HorizontalPanel();
		links.setSpacing(10);

		Anchor contact = new Anchor("huldaeggerts@gmail.com");
		contact.setHref("mailto:huldaeggerts@gmail.com");
		links.add(contact);

		HTML html = new HTML("|");
		links.add(html);

		if (fbuid == null) {
			Anchor smas = new Anchor("suggestadate.appspot.com");
			smas.setHref("http://suggestadate.appspot.com");
			links.add(smas);

			html = new HTML("|");
			links.add(html);

			Anchor fast = new Anchor("apps.facebook.com/webwormgame");
			fast.setHref("https://apps.facebook.com/webwormgame");
			links.add(fast);
		} else {
			Anchor smas = new Anchor("apps.facebook.com/suggestdate");
			smas.setHref("https://apps.facebook.com/suggestdate");
			smas.setTarget("_blank");
			links.add(smas);

			html = new HTML("|");
			links.add(html);

			Anchor fast = new Anchor("webwormgame.appspot.com");
			fast.setHref("http://webwormgame.appspot.com");
			fast.setTarget("_blank");
			links.add(fast);
		}

		info = new PopupPanel();
		VerticalPanel infov = new VerticalPanel();
		infov.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		infov.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		infov.setSpacing(15);
		infohtml = new HTML();

		Button play = new Button();
		play.setText("Play");
		play.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startGame();
			}
		});

		final RadioButton seren = new RadioButton("faudio", "Worm-serenade");
		final RadioButton rhaps = new RadioButton("faudio", "Worm-rhapsody");
		final RadioButton etude = new RadioButton("faudio", "Worm-etude");
		final RadioButton faudio = new RadioButton("faudio", "No eff-ing music");
		seren.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				audio.setEnabled(true);
				audio.setSrc("flabb2.webm");
			}
		});
		rhaps.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				audio.setEnabled(true);
				audio.setSrc("hey.webm");
			}
		});
		etude.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				audio.setEnabled(true);
				audio.setSrc("etude.webm");
			}
		});
		faudio.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				audio.setEnabled(false);
			}
		});

		HorizontalPanel hpaudio = new HorizontalPanel();
		hpaudio.setSpacing(5);
		hpaudio.add(seren);
		hpaudio.add(rhaps);
		hpaudio.add(etude);
		hpaudio.add(faudio);

		HTML musicmessage = new HTML("Coming soon, Worm-valz and Worm-toccata and fuge. <br>If you like the game soundtrack, stay tuned!");

		infov.add(play);
		infov.add(infohtml);
		infov.add(hp);
		infov.add(hpaudio);
		infov.add(musicmessage);
		infov.add(links);
		info.add(infov);

		audio = Audio.createIfSupported();
		audio.setSrc("flabb2.webm");
		audio.setLoop(true);
		audio.getAudioElement().setAttribute("loop", "true");
		coverpanel.add( audio );
		//hpaudio.add( audio );

		/*
		 * setGadsPars(); ScriptElement adscript =
		 * Document.get().createScriptElement();
		 * adscript.setType("text/javascript");
		 * adscript.setSrc("http://pagead2.googlesyndication.com/pagead/show_ads.js"
		 * ); adscript.setAttribute("async", "true");
		 * RootPanel.get("fb-root").getElement().appendChild( adscript );
		 */

		rp.add(vp);
	}

	Audio audio = null;

	public void playMusic() {
		Browser.getWindow().getConsole().log(audio.isAttached());
		if (audio != null && audio.isEnabled() && audio.isPaused() ) {
			audio.setCurrentTime(0.0);
			audio.play();
		}
	}

	public void startGame() {
		int ws = worms.size();
		if (ws == 0) {
			playMusic();

			worms.add(new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT));
			updateCoordinates(cv, false);

			if (timer != null) {
				info.hide();
				cv.setFocus(true);
				timer.scheduleRepeating(delay);
			}
			info.hide();
			cv.setFocus(true);
			currentFrame = requestAnimationFrame();
		} else if (ws == 1) {
			Worm w = null;
			for (Worm ww : worms) {
				w = ww;
				break;
			}
			if (w.c.equals("#00ff00"))
				worms.add(new Worm("#0000ff", 'z', 'x', 'Z', 'X'));
			else
				worms.add(new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT));
		} else {
			Worm w1 = null;
			Worm w2 = null;
			for (Worm ww : worms) {
				if (w1 == null)
					w1 = ww;
				else {
					w2 = ww;
					break;
				}
			}
			if ((w1.c.equals("#00ff00") && w2.c.equals("#ff0000")) || (w2.c.equals("#00ff00") && w1.c.equals("#ff0000"))) {
				worms.add(new Worm("#0000ff", 'z', 'x', 'Z', 'X'));
			} else if ((w1.c.equals("#0000ff") && w2.c.equals("#ff0000")) || (w2.c.equals("#0000ff") && w1.c.equals("#ff0000"))) {
				worms.add(new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT));
			} else {
				worms.add(new Worm("#ff0000", 'n', 'm', 'N', 'M'));
			}
		}
	}

	public void keyUp(int keyCode) {
		keyset.remove(keyCode);
	}

	public void keyDown(int keycode) {
		if (keycode == ' ') {
			pause = !pause;
		} else if (keycode == KeyCodes.KEY_ESCAPE) {
			if (timer != null)
				timer.cancel();
			Set<Worm> wset = new HashSet<Worm>(worms);
			for (Worm w : wset) {
				w.kill();
			}
			wset.clear();
		} else if (keycode == KeyCodes.KEY_ENTER) {
			startGame();
		} else {
			keyset.add(keycode);
		}
	}

	Set<Integer> keyset = new HashSet<Integer>();

	@Override
	public void onKeyUp(KeyUpEvent event) {
		keyUp(event.getNativeKeyCode());
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keycode = event.getNativeKeyCode();
		keyDown(keycode);
	}

	boolean mousedown = false;
	boolean touchdown = false;
	boolean wastouched = false;

	public void start( int x, int y ) {
		playMusic();

		double angle = Math.atan2(cv.getCoordinateSpaceHeight() - y, x - cv.getCoordinateSpaceWidth() / 2);
		worms.add(new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, angle));
		updateCoordinates(cv, false);

		if (timer != null) {
			info.hide();
			cv.setFocus(true);
			timer.scheduleRepeating(delay);
		}
		info.hide();
		cv.setFocus(true);
		currentFrame = requestAnimationFrame();
	}
	
	public void mouseTouch(int x, int y) {
		if (worms.size() == 0) {
			start( x, y );
		} else {
			Worm w = null;
			for (Worm wrm : worms) {
				w = wrm;
				w.setTarget(x, y);
				//break;
			}

			//w.setTarget(x, y);
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if( !wastouched ) {
			mousedown = true;
	
			int x = event.getX();
			int y = event.getY();
	
			mouseTouch(x, y);
		} else wastouched = false;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (mousedown) {
			int x = event.getX();
			int y = event.getY();

			Worm w = null;
			for (Worm wrm : worms) {
				w = wrm;
				break;
			}

			w.setTarget(x, y);
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mousedown = false;
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		Touch touch = event.getTouches().get(0);
		int x = touch.getClientX();
		int y = touch.getClientY();
		// int x = event.getX();
		// int y = event.getY();

		Worm w = null;
		for (Worm wrm : worms) {
			w = wrm;
			break;
		}

		w.setTarget(x, y);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		touchdown = false;
		
		keyset.remove( KeyCodes.KEY_LEFT );
		keyset.remove( KeyCodes.KEY_RIGHT );
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		touchdown = true;
		wastouched = true;

		// event.get
		JsArray<Touch> touches = event.getTouches();
		if (touches.length() > 1) {
			pause = !pause;
		} else {
			Touch touch = event.getTouches().get(0);
			int x = touch.getClientX();
			int y = touch.getClientY();

			if (worms.size() == 0) {
				start( x, y );
			} else if( cv != null ) {
				int w = cv.getCoordinateSpaceWidth();
				
				if( x < w/2 ) keyset.add( KeyCodes.KEY_LEFT );
				else keyset.add( KeyCodes.KEY_RIGHT );
			}
			//mouseTouch(x, y);
		}
	}
}
