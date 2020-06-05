package com.google.sps.data;

public class NameCommentPair {
  private String name;
  private String comment;

  public NameCommentPair(String name, String comment) {
    this.name = name;
    this.comment = comment;
  }

  public String getName() {
    return this.name;
  }
  
  public String getComment() {
    return this.comment;
  }
}
