package org.simmi.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

public class FileUploadServiceImpl extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	 
	private static String CLIENT_ID = "739519778026.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "9Ulqyj2bdtUonZgMF5TVpazP";
	
	private static final String SERVICE_ACCOUNT_EMAIL = "739519778026-v3gpn0n7071nm8qgoe93kplj6u865voi@developer.gserviceaccount.com";
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "704d11bd617b01c108129a90b9f2184c85fd981c-privatekey.p12";
	//private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	public static String gDrive( String name, String cont, byte[] bb, HttpServlet ths ) throws IOException, GeneralSecurityException {
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
	    /*if( !file.exists() ) {
	    	file = new java.io.File( "/home/sigmar/world/smasogur/war/"+SERVICE_ACCOUNT_PKCS12_FILE_PATH );
	    }*/
	    
	    //if( ths != null ) ths.log( "hey exists "+file.exists() );
	    
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
	    
		 GoogleCredential credential = new GoogleCredential.Builder()
		  .setTransport(httpTransport)
		  .setJsonFactory(jsonFactory)
		  .setServiceAccountScopes( Arrays.asList( new String[] {DriveScopes.DRIVE} ) )
		  .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
		  .setServiceAccountPrivateKeyFromP12File( file )
		  //.setServiceAccountPrivateKey( pk )
		  .build();
	    
	    /*GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	            httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
	            .setAccessType("online")
	            .setApprovalPrompt("auto").build();
		
	    String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
	    System.out.println("Please open the following URL in your browser then type the authorization code:");
	    System.out.println("  " + url);
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String code = br.readLine();
	    
	    GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
	    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
	    
	    Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();*/
		//Create a new authorized API client
	    Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
	    //.setHttpRequestInitializer(credential).build();

	    //Insert a file
	    File body = new File();
	    body.setTitle( name );
	    body.setDescription("A shortstory");
	    body.setMimeType( cont );

	    //java.io.File fileContent = new java.io.File("document.txt");
	    //FileContent mediaContent = new FileContent("text/plain", fileContent);
	    ByteArrayContent bac = new ByteArrayContent( "text/plain", bb );
	    
	    Files files = service.files();
	    Insert insert = files.insert(body, bac);
	    File f = insert.execute();
	    List l = service.files().list();
	    for( Entry<String,Object> ent : l.entrySet() ) {
	    	System.err.println( ent.getKey() + "  " + ent.getValue() );
	    }
	    
	    Permission perm = new Permission();
	    perm.setValue("default");
	    perm.setType("anyone");
	    perm.setRole("reader");
	    service.permissions().insert( f.getId(), perm ).execute();
	    String debugStr = "File ID: " + f.getId() + "  " + f.getWebContentLink();
	    
	    if( ths != null ) ths.log( debugStr );
	    //else System.err.println( debugStr );
	    
	    return f.getWebContentLink();
	}
	
	public static void main(String[] args) {
		try {
			java.io.File f = new java.io.File( "/home/sigmar/col.txt" );
			FileInputStream fis = new FileInputStream( f );
			byte[] bb = new byte[ (int)f.length() ];
			fis.read( bb );
			fis.close();
			gDrive("newfile3", "text/plain", bb, null);
		} catch (IOException e) {
			e.printStackTrace();
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
                name = URLDecoder.decode(name, "UTF-8");
                InputStream stream = item.openStream();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[8192];
                while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }

                byte[] bb = out.toByteArray();
                String fileurl = gDrive( name, cont, bb, this );
                //SmasagaServiceImpl.dropToBox(name, bb, this);
                
                
                /*int maxFileSize = 10*(1024*1024); //10 megs max 
                if (out.size() > maxFileSize) { 
                    throw new RuntimeException("File is > than " + maxFileSize);
                }*/
                
                res.getWriter().println( fileurl );
            }
        } catch(Exception e) {
        	this.log( "mu " + e.getMessage() );
            throw new RuntimeException(e);
        }
		
		//SmasagaServiceImpl.dropToBox( "", null );
	}
}
