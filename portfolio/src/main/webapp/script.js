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


function getComments() {
  var numComments = document.getElementById("num-comments").value;
  fetch('/data?max-comments='+numComments).then(response => response.json()).then((comments) => {
    const commentsContainer = document.getElementById('comments-container');
    commentsContainer.innerHTML = '';
    console.log(comments);
    console.log(comments.length);
    if (comments.length == 0) {
      const iElement = document.createElement('i');
      iElement.innerText = "No comments to display";
      commentsContainer.appendChild(iElement);
    } else {
      for (i = 0; i < comments.length; i++) {
        commentsContainer.appendChild(createPElement(comments[i]));
      }
    }
  });
}

function createPElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}