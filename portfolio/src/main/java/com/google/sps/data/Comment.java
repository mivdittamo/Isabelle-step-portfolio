package com.google.sps.data;
/**
 * Comment class that represents the Comment entity stored in datastore
*/
public class Comment {
  private final String name;
  private final String content;
  private final String userEmail;
  private final String imageBlobKey;

  public Comment(String name, String comment, String userEmail, String imageBlobKey) {
    this.name = name;
    this.content = comment;
    this.userEmail = userEmail;
    this.imageBlobKey = imageBlobKey;
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

  public String getImageBlobKey() {
    return this.imageBlobKey;
  }
}
