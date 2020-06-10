package com.google.sps.data;
/**
 * Comment class that represents the Comment entity stored in datastore
*/
public class Comment {
  private final String name;
  private final String content;
  private final String userEmail;
  private final String imageURL;

  public Comment(String name, String comment, String userEmail, String imageURL) {
    this.name = name;
    this.content = comment;
    this.userEmail = userEmail;
    this.imageURL = imageURL;
  }

  public String getName() {
    return this.name;
  }
  
  public String getContent() {
    return this.content;
  }

  public String getUserEmail() {
    return this.userEmail;
  }

  public String getImageURL() {
    return this.imageURL;
  }
}
