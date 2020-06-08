package com.google.sps.data;
/**
* User Login class to hold fields relating to the user login process
*/
public class UserLogin {
  private final boolean status;
  private final String redirectURL;

  public UserLogin(boolean status, String redirectURL) {
    this.status = status;
    this.redirectURL = redirectURL;
  }
}