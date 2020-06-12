package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/*
* Servlet that handles requests for a blob given its blobkey
*/
@WebServlet("/blob-key")
public class BlobServlet extends HttpServlet{
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    System.out.println(request.getParameter("imageKey"));
    BlobKey blobKey = new BlobKey(request.getParameter("imageKey"));
    blobstoreService.serve(blobKey, response);
  }
}
