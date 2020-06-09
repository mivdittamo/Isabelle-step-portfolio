package com.google.sps.data;
/**
* User Login class to hold fields relating to the user login process
*/
public class UserLogin {
  private final boolean isLoggedIn;
  private final String loginOrLogoutURL;

  public UserLogin(boolean isLoggedIn, String loginOrLogoutURL) {
    this.isLoggedIn = isLoggedIn;
    this.loginOrLogoutURL = loginOrLogoutURL;
  }
}