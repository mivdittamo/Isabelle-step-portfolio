package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;
import com.google.sps.data.UserLogin;

/**
* Servlet that handles login requests and returns login/logout urls
*/
@WebServlet("/login")
public class LoginServlet extends HttpServlet{

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    UserService userService = UserServiceFactory.getUserService();
    UserLogin loginInfo;
    if (userService.isUserLoggedIn()) {
      String redirectURLAfterLogOut = "/";
      String logoutURL = userService.createLogoutURL(redirectURLAfterLogOut);

      loginInfo = new UserLogin(true, logoutURL);
    } else {
      String redirectURLAfterLogIn = "/";
      String loginURL = userService.createLoginURL(redirectURLAfterLogIn);

      loginInfo = new UserLogin(false, loginURL);
    }
    String json = convertToJson(loginInfo);
    response.getWriter().println(json);
  }

  public String convertToJson(UserLogin content) {
    Gson gson = new Gson();
    String json = gson.toJson(content);
    return json;
  }
}
