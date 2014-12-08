angular.module('CameraApp', ['ngAnimate']).config(function ($interpolateProvider) {
  $interpolateProvider.startSymbol('[[').endSymbol(']]');
}).filter('capitalize', function() {
  return function(input, all) {
    return (!!input) ? input.replace(/([^\W_]+[^\s-]*) */g, function (txt){
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    }) : '';
  };
});

function CameraController ($scope, $http, $interval) {
  var streaming = false;
  var timeoutPromise = null;
  var video = document.querySelector('.video-container');
  var canvas = document.querySelector('.hidden-canvas');
  var photo = document.querySelector('.hidden-photo');
  var width = 970;
  var height = 0;
  
  var modes = {
    burglar: 'alarm',
    lullaby: 'lullaby',
  };
  $scope.mode = 'burglar'; // Either 'burglar' or 'lullaby'
  var audio = new Audio('/static/sound/alarm.mp3');
  audio.loop = true;

  $scope.snapshots = [];
  $scope.movementThreshold = 15;
  $scope.isCasting = false;
  $scope.timeoutInterval = 1;

  $('#slider').slider({
    min: 1,
    max: 60,

    change: function(event, ui) {
      if ($scope.isCasting) {
        $scope.togglePolling();
        $scope.togglePolling();
      }
    },

    slide: function(event, ui) {
      $scope.$apply(function () {$scope.timeoutInterval = ui.value;});
    }
  });

  $('#threshold-slider').slider({
    min: 1,
    max: 60,

    slide: function(event, ui) {
      $scope.$apply(function () {$scope.movementThreshold = ui.value;});
    }
  });

  $scope.changeMode = function (newMode) {
    $scope.mode = newMode;
    audio.src = '/static/sound/' + modes[newMode] + '.mp3';
  };

  // Set up the camera
  (function () {
    navigator.getMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia ||
      navigator.mozGetUserMedia || navigator.msGetUserMedia);

    // Link the video tag to the WebCam User Media Stream
    navigator.getMedia({video: true}, function (stream) {
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

  // Toggles between the option of polling and not polling
  $scope.togglePolling = function () {
    if (!$scope.isCasting) {
      timeoutPromise = $interval(pollPicture, $scope.timeoutInterval * 1000);
    } else {
      $interval.cancel(timeoutPromise);
    }
    $scope.isCasting = !$scope.isCasting;
  };

  $scope.uploadImage = function (data) {
    $http.post('/upload', {
      image: data
    }).success(function (res, status, headers, config) {
      console.log(res);
      getProcessedImage();
    }).error(function (res, status, headers, config) {
      alert('Post failed');
    });
  };

  $scope.sendImage = function () {
    pollPicture();
  };

  function pollPicture () { 
    $scope.uploadImage(takePicture());
  }

  $scope.activateAlert = function () {
    $http.post('/play_alert', {}).success(function (res, status, headers, config) {
      console.log(res);
    }).error(function (res, status, headers, config) {
      alert('Post failed');
    });
  };

  $scope.stopAlert = function () {
    $http.post('/stop_alert', {}).success(function (res, status, headers, config) {
      console.log(res);
    }).error(function (res, status, headers, config) {
      alert('Post failed');
    });
  };

  // Captures an image from the video stream and put it in the image element
  function takePicture () {
    canvas.width = width;
    canvas.height = height;
    canvas.getContext('2d').drawImage(video, 0, 0, width, height);
    var data = canvas.toDataURL('image/jpeg');
    data = data.replace(/^data:image\/(png|jpeg);base64,/, "");
    return data;
  }

  function getProcessedImage () {
    $http.get('/latest_image').success(function(data, status, headers, config) {
      data.movement = data.diff > $scope.movementThreshold;
      $scope.snapshots.unshift(data);
      console.log(data.play_alert);
      if (data.play_alert) {
        audio.play();
      } else {
        audio.pause();
      }
    }).error(function(data, status, headers, config) {
      alert('Post failed');
    });
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

  $scope.chooseImage = function (image) {
    $scope.chosenImage = image;
    $('#snapshot-modal').modal();
  }
}
