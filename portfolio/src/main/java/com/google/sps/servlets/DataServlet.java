// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.sps.data.Comment;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  //constants for Datastore entity and property names
  private static final String ENTITY_COMMENT = "Comment";
  private static final String PROPERTY_COMMENT_CONTENT = "content";
  private static final String PROPERTY_COMMENT_TIMESTAMP = "timestamp";
  private static final String PROPERTY_COMMENT_NAME = "name";
  private static final String PROPERTY_COMMENT_USER_EMAIL = "userEmail";
  private static final String PROPERTY_COMMENT_IMAGE_URL = "imageURL";
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Integer maxComments = getMaxComments(request);

    Query query = new Query(ENTITY_COMMENT).addSort(PROPERTY_COMMENT_TIMESTAMP, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(maxComments)); 
    
    List<Comment> comments = new ArrayList<>();
    for (Entity entity: results) {
      String name = (String) entity.getProperty(PROPERTY_COMMENT_NAME);
      String comment = (String) entity.getProperty(PROPERTY_COMMENT_CONTENT);
      String userEmail = (String) entity.getProperty(PROPERTY_COMMENT_USER_EMAIL);
      String imageURL = (String) entity.getProperty(PROPERTY_COMMENT_IMAGE_URL);
      Comment commentEntity = new Comment(name, comment, userEmail, imageURL);
      comments.add(commentEntity);
    }

    response.setContentType("application/json;");
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comments-input");
    if (comment == null) {
      comment = "";
    }
    String commentAuthor = request.getParameter("name-input");

    String imageUrl = getUploadedFileUrl(request, "image");
    System.out.println("IMAGE URL: " + imageUrl);

    long timestamp = System.currentTimeMillis();

    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    Entity commentEntity = new Entity(ENTITY_COMMENT);
    commentEntity.setProperty(PROPERTY_COMMENT_CONTENT, comment);
    commentEntity.setProperty(PROPERTY_COMMENT_TIMESTAMP, timestamp);
    commentEntity.setProperty(PROPERTY_COMMENT_NAME, commentAuthor);
    commentEntity.setProperty(PROPERTY_COMMENT_USER_EMAIL, userEmail);
    commentEntity.setProperty(PROPERTY_COMMENT_IMAGE_URL, imageUrl);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }

  private Integer getMaxComments(HttpServletRequest request) {
    String maxCommentsString = request.getParameter("max-comments");
    int numMaxComments;
    try {
      numMaxComments = Integer.parseInt(maxCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + maxCommentsString);
      return 0;
    }
    if (numMaxComments < 1) {
      System.err.println("Player choice is out of range: " + maxCommentsString);
      return 0;
    }
    return numMaxComments;
  }

  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's dev server, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}

