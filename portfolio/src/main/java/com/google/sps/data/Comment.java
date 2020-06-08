package com.google.sps.data;
/**
 * Comment class that represents the Comment entity stored in datastore
*/
public class Comment {
  private final String NAME;
  private final String COMMENT_CONTENT;

  public Comment(String name, String comment) {
    this.NAME = name;
    this.COMMENT_CONTENT = comment;
  }

  public String getNAME() {
    return this.NAME;
  }
  
  public String getCOMMENT_CONTENT() {
    return this.COMMENT_CONTENT;
  }
}
