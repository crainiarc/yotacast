angular.module('CameraApp', []).config(function ($interpolateProvider) {
  $interpolateProvider.startSymbol('[[').endSymbol(']]');
});

function CameraController ($scope, $http) {
  $('#slider').slider({
    max: 50
  });

  $scope.uploadImage = function () {
    $http.post('/upload', {
      image: 'iVBORw0KGgoAAAANSUhEUgAABAAAAACpCAYAAAC1QU+2AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA2hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDozQjQwNzQ2NDFBMjI2ODExODA4M0RCRTRENkVEN0RDOCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpEOEI4NTQ0MkZCMkExMUUzODRFOUVFNDMyNDRCREJDQyIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpEOEI4NTQ0MUZCMkExMUUzODRFOUVFNDMyNDRCREJDQyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ1M2IChNYWNpbnRvc2gpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6M0I0MDc0NjQxQTIyNjgxMTgwODNEQkU0RDZFRDdEQzgiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6M0I0MDc0NjQxQTIyNjgxMTgwODNEQkU0RDZFRDdEQzgiLz4gPC9yZGY6RGVzY3Jp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'
    }).success(function (res, status, headers, config) {
      console.log(res);
    }).error(function (res, status, headers, config) {
      alert('Post failed');
    });
  };
}
