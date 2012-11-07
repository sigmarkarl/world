package org.simmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public class FileUploadServiceImpl extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String CLIENT_ID = "739519778026.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "9Ulqyj2bdtUonZgMF5TVpazP";
	
	private static final String SERVICE_ACCOUNT_EMAIL = "739519778026-v3gpn0n7071nm8qgoe93kplj6u865voi@developer.gserviceaccount.com";
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "704d11bd617b01c108129a90b9f2184c85fd981c-privatekey.p12";
	//private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	public void gDrive( String name, String cont, byte[] bb ) throws IOException {
		HttpTransport httpTransport = new NetHttpTransport();
	    JsonFactory jsonFactory = new JacksonFactory();

	    /*GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	        httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
	        .setAccessType("online")
	        .setApprovalPrompt("auto").build();

	    String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
	    this.log("Please open the following URL in your browser then type the authorization code:");
	    System.out.println("  " + url);
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String code = br.readLine();*/

	    //GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
	    //GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
	    
	    java.io.File file = new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH);
	    if( !file.exists() ) {
	    	file = new java.io.File( "/"+SERVICE_ACCOUNT_PKCS12_FILE_PATH );
	    }
	    if( !file.exists() ) {
	    	file = new java.io.File( "org/simmi/server/"+SERVICE_ACCOUNT_PKCS12_FILE_PATH );
	    }
	    
	    this.log( "hey exists "+file.exists() );
	    
	    //Clientb
	    
	    PrivateKey pk = new PrivateKey() {
			@Override
			public String getAlgorithm() {
				return "PKCS12";
			}

			@Override
			public String getFormat() {
				return null;
			}

			@Override
			public byte[] getEncoded() {
				return null;
			}
	    };
	    
		try {
			 GoogleCredential credential = new GoogleCredential.Builder()
			  .setTransport(httpTransport)
			  .setJsonFactory(jsonFactory)
			  .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
			  .setServiceAccountScopes(DriveScopes.DRIVE_FILE)
			  .setServiceAccountPrivateKeyFromP12File( file )
			  //.setServiceAccountPrivateKey( pk )
			  .build();
			
			//Create a new authorized API client
		    Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();

		    //Insert a file
		    File body = new File();
		    body.setTitle( name );
		    body.setDescription("A shortstory");
		    body.setMimeType( cont );

		    //java.io.File fileContent = new java.io.File("document.txt");
		    //FileContent mediaContent = new FileContent("text/plain", fileContent);
		    ByteArrayContent bac = new ByteArrayContent( "text/plain", bb );

		    File f = service.files().insert(body, bac).execute();
		    this.log( "File ID: " + f.getId() );
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	public void doPost( HttpServletRequest req, HttpServletResponse res ) {
		ServletFileUpload upload = new ServletFileUpload();
        try {
            FileItemIterator iter = upload.getItemIterator(req);
            
            while (iter.hasNext()) {
                FileItemStream 	item = iter.next();
                String 			cont = item.getContentType();

                String name = item.getFieldName();
                InputStream stream = item.openStream();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[8192];
                while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }

                byte[] bb = out.toByteArray();
                //gDrive( name, cont, bb );
                SmasagaServiceImpl.dropToBox(name, bb, this);
                /*int maxFileSize = 10*(1024*1024); //10 megs max 
                if (out.size() > maxFileSize) { 
                    throw new RuntimeException("File is > than " + maxFileSize);
                }*/
                
                res.getWriter().println("tokst");
            }
        } catch(Exception e) {
        	this.log( "mu " + e.getMessage() );
            throw new RuntimeException(e);
        }
		
		//SmasagaServiceImpl.dropToBox( "", null );
	}
}
