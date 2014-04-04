package org.simmi.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileDownloadServiceImpl extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String CLIENT_ID = "739519778026.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "9Ulqyj2bdtUonZgMF5TVpazP";
	
	private static final String SERVICE_ACCOUNT_EMAIL = "739519778026-v3gpn0n7071nm8qgoe93kplj6u865voi@developer.gserviceaccount.com";
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "704d11bd617b01c108129a90b9f2184c85fd981c-privatekey.p12";
	//private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	
	public static void main(String[] args) {
		try {
			java.io.File f = new java.io.File( "/home/sigmar/col.txt" );
			FileInputStream fis = new FileInputStream( f );
			byte[] bb = new byte[ (int)f.length() ];
			fis.read( bb );
			fis.close();
			//gDrive("newfile3", "text/plain", bb, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doGet( HttpServletRequest req, HttpServletResponse res ) {
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		try {
			String sms = req.getParameter("smasaga");
			String urlstr = URLDecoder.decode(sms, "UTF8");
			
			URL url = new URL( urlstr );
			InputStream is = url.openStream();
			int r = is.read();
			while( r >= 0 ) {
				baos.write( r );
				r = is.read();
			}
			baos.close();
			
			res.getOutputStream().write( baos.toByteArray() );
		} catch( IOException e) {
			e.printStackTrace();
		}
	}
}
