angular.module('CameraApp', []).config(function ($interpolateProvider) {
  $interpolateProvider.startSymbol('[[').endSymbol(']]');
});

function CameraController ($scope, $http) {
  $('#slider').slider({
    max: 50
  });

  // Set up the camera
  (function() {
    video = document.querySelector('.video-container');
    canvas = document.querySelector('.hidden-canvas');
    photo = document.querySelector('.hidden-photo');
    width = 970;
    
    navigator.getMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia ||
      navigator.mozGetUserMedia || navigator.msGetUserMedia);

    navigator.getMedia({video: true}, function(stream) {
      if (navigator.mozGetUserMedia) {
        video.mozSrcObject = stream;
      } else {
        var vendorURL = window.URL || window.webkitURL;
        video.src = vendorURL.createObjectURL(stream);
      }
      video.play();
    }, function (err) { console.log('An error occured: ' + err) });

    // video.addEventListener('canplay', function(ev){
    //   if (!streaming) {
    //     height = video.videoHeight / (video.videoWidth/width);
    //     video.setAttribute('width', width);
    //     video.setAttribute('height', height);
    //     canvas.setAttribute('width', width);
    //     canvas.setAttribute('height', height);
    //     streaming = true;
    //   }
    // }, false);
  })();

  $scope.uploadImage = function () {
    $http.post('/upload', {
      image: 'Yo Waihon'
    }).success(function (res, status, headers, config) {
      console.log(res);
    }).error(function (res, status, headers, config) {
      alert('Post failed');
    });
  };
}
