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
  $scope.isSending = false;

  $scope.timeoutInterval = 5;
  $('#slider').slider({
    min: $scope.timeoutInterval,
    max: 300,
    change: function(event, ui) {
      $scope.$apply(function () {$scope.timeoutInterval = ui.value;});
      if ($scope.isSending) {
        $scope.togglePolling();
        $scope.togglePolling();
      }
    },
    slide: function(event, ui) {
      $scope.$apply(function () {$scope.timeoutInterval = ui.value;});
    }
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
    if (!$scope.isSending) {
      // set the interval to start polling
      timeoutPromise = $interval(pollPicture, $scope.timeoutInterval * 1000);
    } else {
      // unset the interval
      $interval.cancel(timeoutPromise);
    }
    $scope.isSending = !$scope.isSending;
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

  $scope.sendImage = function () {
    pollPicture();
  }

  function pollPicture() { 
    $scope.uploadImage(takePicture());
  }

  // Captures an image from the video stream and put it in the image element
  function takePicture() {
    canvas.width = width;
    canvas.height = height;
    canvas.getContext('2d').drawImage(video, 0, 0, width, height);
    var data = canvas.toDataURL('image/jpeg');
    data = data.replace(/^data:image\/(png|jpeg);base64,/, "");
    return data;
  }

  $scope.getImage = function () {
    $http.get('/latest_image').success(function (res, status, headers, config) {
      if (res.status === 'success') {
        $scope.imgString = 'data:image/png;base64,' + $.trim(res.image);
      }
    }).error(function (res, status, headers, config) {
      alert('Image retrieval failed');
    });
  };
}
