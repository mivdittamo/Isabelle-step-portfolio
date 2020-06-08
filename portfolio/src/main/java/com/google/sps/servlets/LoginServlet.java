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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;

@WebServlet("/login")
public class LoginServlet extends HttpServlet{

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String redirectURLAfterLogOut = "/login";
      String logoutURL = userService.createLogoutURL(redirectURLAfterLogOut);

      response.getWriter().println("<p>Hello " + userEmail + "! You are logged in!</p>");
      response.getWriter().println("<p>Click <a href=\"" + logoutURL + "\">here</a> to log out.</p>");
    } else {
      String redirectURLAfterLogIn = "/login";
      String loginURL = userService.createLoginURL(redirectURLAfterLogIn);

      response.getWriter().println("<p>Hello!</p>");
      response.getWriter().println("<p>Click <a href=\"" + loginURL + "\">here</a> to log in.</p>");
    }
  }
}
