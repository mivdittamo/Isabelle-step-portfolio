package com.google.sps.data;
/**
 * Comment class that represents the Comment entity stored in datastore
*/
public class Comment {
  private final String name;
  private final String content;

  public Comment(String name, String comment) {
    this.name = name;
    this.content = comment;
  }

  public String getName() {
    return this.name;
  }
  
  public String getContent() {
    return this.content;
  }
}
