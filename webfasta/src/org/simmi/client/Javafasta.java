package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Javafasta implements EntryPoint {
	
	public native void transferData( JavaScriptObject dataTransfer ) /*-{
		$wnd.transData( dataTransfer );
	}-*/;

	@Override
	public void onModuleLoad() {
		//final FocusPanel	fp = new FocusPanel();
		//fp.setHeight("20px");
		
		final RootPanel rp = RootPanel.get();
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
		Window.enableScrolling( false );
		
		/*fp.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {}
		});
		fp.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {}
		});
		fp.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {}
		});
		fp.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {}
		});
		fp.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {}
		});
		fp.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {}
		});
		fp.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				DataTransfer dt = event.getDataTransfer();
				transferData( dt );
			}
		});
		
		final RootPanel stuff = RootPanel.get("stuff");
		stuff.add( fp );*/
	}
}
