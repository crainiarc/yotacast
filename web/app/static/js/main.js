angular.module('CameraApp', []).config(function ($interpolateProvider) {
  $interpolateProvider.startSymbol('[[').endSymbol(']]');
});

function CameraController ($scope, $http) {
  $('#slider').slider({
    max: 50
  });

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
