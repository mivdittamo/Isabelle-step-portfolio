package com.google.sps.data;

public class UserLogin {
  private final String status;
  private final String redirectURL;

  public UserLogin(String status, String redirectURL) {
    this.status = status;
    this.redirectURL = redirectURL;
  }
}