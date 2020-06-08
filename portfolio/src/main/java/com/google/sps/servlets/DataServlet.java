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
import java.util.List;
import java.util.ArrayList;
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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  //constants for Datastore entity and property names
  private static final String ENTITY_COMMENT = "Comment";
  private static final String PROPERTY_CONTENT = "content";
  private static final String PROPERTY_TIMESTAMP = "timestamp";
  private static final String PROPERTY_COMMENT_NAME = "name";
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Integer maxComments = getMaxComments(request);
    if (maxComments == null) {
      maxComments = 0;
    }

    Query query = new Query(ENTITY_COMMENT).addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(maxComments)); 
    
    List<Comment> comments = new ArrayList<>();
    for (Entity entity: results) {
      String name = (String) entity.getProperty(PROPERTY_COMMENT_NAME);
      String comment = (String) entity.getProperty(PROPERTY_CONTENT);
      Comment commentEntity = new Comment(name, comment);
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

    long timestamp = System.currentTimeMillis();
    Entity commentEntity = new Entity(ENTITY_COMMENT);
    commentEntity.setProperty(PROPERTY_CONTENT, comment);
    commentEntity.setProperty(PROPERTY_TIMESTAMP, timestamp);
    commentEntity.setProperty(PROPERTY_COMMENT_NAME, commentAuthor);

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
      return null;
    }
    if (numMaxComments < 1) {
      System.err.println("Player choice is out of range: " + maxCommentsString);
      return null;
    }
    return numMaxComments;
  }
}

