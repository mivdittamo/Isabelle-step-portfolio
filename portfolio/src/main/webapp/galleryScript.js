// Functions for the slideshows in Gallery
var slideIndex = 1;
displaySlide(slideIndex);

function moveSlide(n) {
  displaySlide(slideIndex += n);
}

function displaySlide(n) {
  var slides = document.getElementsByClassName("EuropeSlideshow");
  if (n > slides.length) {
    slideIndex = 1;
  } else if (n < 1) {
    slideIndex = slideIndex = slides.length;
  }
  for (var i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  slides[slideIndex - 1].style.display = "block";
}
