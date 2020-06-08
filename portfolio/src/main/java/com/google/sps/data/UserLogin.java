package com.google.sps.data;
/**
* User Login class to hold fields relating to the user login process
*/
public class UserLogin {
  private final String status;
  private final String redirectURL;

  public UserLogin(String status, String redirectURL) {
    this.status = status;
    this.redirectURL = redirectURL;
  }
}