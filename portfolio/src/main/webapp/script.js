// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!', 'Winter is coming!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function getIntroduction() {
  const responsePromise = fetch('/data');
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  const textPromise = response.text();
  textPromise.then(addToDOM);
}

function addToDOM(textResponse) {
  const textContainer = document.getElementById('introduction-container');
  textContainer.innerText = textResponse;
}

function loadHomePageContent() {
  fetchUserLoginStatus();
  getComments();
  fetchBlobstoreURL();
}

function fetchUserLoginStatus() {
  const commentsFormMessageContainer = document.getElementById("comments-form-message");
  fetch('/login').then(response => response.json()).then((result) => {
    if (result.isLoggedIn) {
      commentsFormMessageContainer.innerText = '';
      commentsFormMessageContainer.appendChild(document.createTextNode("Click "));
      commentsFormMessageContainer.appendChild(createAElement("here", result.loginOrLogoutURL));
      commentsFormMessageContainer.appendChild(document.createTextNode(" to log out."));
      document.getElementById("comments-form").style.display = "block";
    } else {
      commentsFormMessageContainer.appendChild(document.createTextNode("Log in "));
      commentsFormMessageContainer.appendChild(createAElement("here", result.loginOrLogoutURL));
      commentsFormMessageContainer.appendChild(document.createTextNode(" to add your own comments."));
      document.getElementById("comments-form").style.display = "none";
    }
  });
}

function getComments() {
  var numComments = document.getElementById("num-comments").value;
  fetch('/data?max-comments='+numComments).then(response => response.json()).then((comments) => {
    const commentsContainer = document.getElementById('comments-container');
    commentsContainer.innerText = '';
    if (comments.length == 0) {
      const iElement = document.createElement('i');
      iElement.innerText = "No comments to display";
      commentsContainer.appendChild(iElement);
    } else {
      comments.forEach((comment) => {
        commentsContainer.appendChild(createCommentElement(comment));
      });
    }
  });
}

function createCommentElement(commentEntity) {
  const commentElement = document.createElement('li');
  commentElement.innerHTML = '';

  const name = createH4Element(commentEntity.name);
  commentElement.appendChild(name);

  const content = createSpanElement(commentEntity.content);
  commentElement.appendChild(content);
  
  if (commentEntity.imageBlobKey) {
    console.log(commentEntity.imageBlobKey);
    fetch('/blob-key?imageKey='+commentEntity.imageBlobKey).then((response) => {
      console.log(response);
      return response.blob();
    }).then((blobContent) => {
      var blobURL = URL.createObjectURL(blobContent);
      commentElement.appendChild(createImgElement(blobURL));
    });
  }

  return commentElement;
}

function deleteComments() {
  const request = new Request('/delete-data', {method: 'POST'});
  fetch(request).then(response => {
    getComments();
  });
}

function fetchBlobstoreURL() {
  fetch('/blobstore-url').then((response) => {
    return response.text();
  })
  .then((imageUploadURL) => {
    const commentsForm = document.getElementById("comments-form");
    commentsForm.action = imageUploadURL;
  })
}

//Functions for creating individual html elements:

function createSpanElement(text) {
  const spanElement = document.createElement('span');
  spanElement.innerText = text;
  return spanElement;
}

function createH4Element(text) {
  const h4element = document.createElement('h4');
  h4element.innerText = text;
  return h4element;
}

function createAElement(text, url) {
  const aElement = document.createElement('a');
  aElement.innerText = text;
  aElement.title = text;
  aElement.href = url;
  return aElement;
}

function createImgElement(imageURL) {
  const imgElement = document.createElement('img');
  imgElement.src = imageURL;
  return imgElement;
}
