angular.module('CameraApp', []).config(function ($interpolateProvider) {
  $interpolateProvider.startSymbol('[[').endSymbol(']]');
});
var test;
function CameraController ($scope, $http, $interval) {
  var streaming = false,
  timeoutPromise = null,
  video = document.querySelector('.video-container'),
  canvas = document.querySelector('.hidden-canvas'),
  photo = document.querySelector('.hidden-photo'),
  width = 970,
  height = 0;

  $('#slider').slider({
    min: 5,
    max: 300,
  });

  // Set up the camera
  (function() {
    navigator.getMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia ||
      navigator.mozGetUserMedia || navigator.msGetUserMedia);

    // Link the video tag to the WebCam User Media Stream
    navigator.getMedia({video: true}, function(stream) {
      if (navigator.mozGetUserMedia) {
        video.mozSrcObject = stream;
      } else {
        var vendorURL = window.URL || window.webkitURL;
        video.src = vendorURL.createObjectURL(stream);
      }
      video.play();
    }, function (err) { console.log('An error occured: ' + err) });

    // Set height variables for canvas
    video.addEventListener('canplay', function(ev){
      if (!streaming) {
        height = video.videoHeight / (video.videoWidth/width);
        video.setAttribute('width', width);
        video.setAttribute('height', height);
        canvas.setAttribute('width', width);
        canvas.setAttribute('height', height);
        streaming = true;
      }
    }, false);
  })();

  $scope.togglePolling = function () {
    if (!timeoutPromise) {
      // set the interval to start polling
      console.log($('#slider').slider('value') * 1000);
      timeoutPromise = $interval(pollPicture, 1000);
    } else {
      // unset the interval
      $interval.cancel(timeoutPromise);
    }
  };

  $scope.uploadImage = function (data) {
    $http.post('/upload', {
      image: data
    }).success(function (res, status, headers, config) {
      console.log(res);
    }).error(function (res, status, headers, config) {
      alert('Post failed');
    });
  };

  function pollPicture() {
    var data = takePicture();
    $scope.uploadImage(data);
  }

  // Captures an image from the video stream and put it in the image element
  function takePicture() {
    canvas.width = width;
    canvas.height = height;
    canvas.getContext('2d').drawImage(video, 0, 0, width, height);
    var data = canvas.toDataURL('image/jpeg');
    data = data.replace(/^data:image\/(png|jpeg);base64,/, "");
  }

      startbutton.addEventListener('click', function(ev){
        takePicture();
      ev.preventDefault();
    }, false);
}
