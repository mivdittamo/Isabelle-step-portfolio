// Functions for the slideshows in Gallery

//Initial index for diplaying slides
var slideIndex = 1;

//all of the slides in the europeSlideshow
var slides = document.getElementsByClassName("EuropeSlideshow");

//initially display the first slide
displaySlide();

//Moves the slideIndex by the indicated factor
function moveSlide(moveFactor) {
  slideIndex += moveFactor;
  if (slideIndex > slides.length) {
    slideIndex = 1;
  } else if (slideIndex < 1) {
    slideIndex = slides.length;
  }
}

//Displays the slide at the current slideIndex
function displaySlide() {
  for (var i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  slides[slideIndex - 1].style.display = "block";
}