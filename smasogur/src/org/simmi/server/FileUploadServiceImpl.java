package org.simmi.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadServiceImpl extends HttpServlet {
	public void doPost( HttpServletRequest req, HttpServletResponse res ) {
		ServletFileUpload upload = new ServletFileUpload();
        try {
            FileItemIterator iter = upload.getItemIterator(req);

            while (iter.hasNext()) {
                FileItemStream item = iter.next();

                String name = item.getFieldName();
                InputStream stream = item.openStream();

                // Process the input stream
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[8192];
                while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }

                SmasagaServiceImpl.dropToBox(name, out.toByteArray());
                /*int maxFileSize = 10*(1024*1024); //10 megs max 
                if (out.size() > maxFileSize) { 
                    throw new RuntimeException("File is > than " + maxFileSize);
                }*/
                
                res.getWriter().println("tokst");
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
		
		//SmasagaServiceImpl.dropToBox( "", null );
	}
}
