var CANVAS_WIDTH = 828;
var CANVAS_HEIGHT = 495;

var BOUNDARY_RECT_TOP_LEFT_X = 237;
var BOUNDARY_RECT_TOP_LEFT_Y = 138;
var BOUNDARY_RECT_BOTTOM_LEFT_X = 700;
var BOUNDARY_RECT_BOTTOM_LEFT_Y = 347;
var BOUNDARY_RECT_REAL_WIDTH = 2095;
var BOUNDARY_RECT_REAL_HEIGHT = 740;
var BOUNDARY_RECT_FILL_SELECTED = 'rgba(0,188,140,0.2)';
var BOUNDARY_RECT_FILL_DISABLED = 'rgba(178,74,24,0.2)';

var POINT_RADIUS = 5;
var POINT_FILL_COLOR = 'red';
var POINT_LABEL_FONT = 'sans-serif';
var POINT_LABEL_FONT_SIZE = 14;
var POINT_LABEL_FONT_WEIGHT = 'bold';

var CAMERA_DEFAULT_HEIGHT = 40;

angular.module('CameraApp', []).config(function ($interpolateProvider) {
  $interpolateProvider.startSymbol('[[').endSymbol(']]');
});

function CameraController ($scope, $http) {
  var images = {
    cmu: {
      image: 'cmu.jpg',
      plan: 'cmu-plan.jpg'
    },
    stanford: {
      image: 'stanford.jpg',
      plan: 'stanford-plan.jpg'
    }
  }
  var type = window.location.hash.substring(1);
  if (!type) {
    type = 'cmu';
  }
  $scope.selectedImage = images[type];
  
  // Plan view slicing
  planViewCanvas = new fabric.Canvas('map-container', {
    width: CANVAS_WIDTH,
    height: CANVAS_HEIGHT
  });

  $scope.topLeft = {x: BOUNDARY_RECT_TOP_LEFT_X, y: BOUNDARY_RECT_TOP_LEFT_Y};
  $scope.bottomRight = {x: BOUNDARY_RECT_BOTTOM_LEFT_X, y: BOUNDARY_RECT_BOTTOM_LEFT_Y};
  $scope.boundaryWidth = BOUNDARY_RECT_REAL_WIDTH;
  $scope.boundaryHeight = BOUNDARY_RECT_REAL_HEIGHT;
  $scope.boundaryRectEditable = false;

  var boundaryRect = new fabric.Rect({
    lockRotation: true
  });

  function getBoundaryRectProps () {
    return {
      scale: 1,
      scaleX: 1,
      scaleY: 1,
      width: $scope.bottomRight.x - $scope.topLeft.x, 
      height: $scope.bottomRight.y - $scope.topLeft.y,
      left: $scope.topLeft.x, 
      top: $scope.topLeft.y
    }
  }

  planViewCanvas.on('object:modified', function (object) {
    if (object.target === boundaryRect) {
      updateBoundaryRect(object.target);
    }
  });

  function updateBoundaryRect (rect) {
    $scope.topLeft.x = Math.round(rect.left);
    $scope.topLeft.y = Math.round(rect.top);
    $scope.bottomRight.x = Math.round(rect.left + rect.currentWidth);
    $scope.bottomRight.y = Math.round(rect.top + rect.currentHeight);
    $scope.$apply();
  }

  function toggleBoundaryRectEditing (state) {
    $scope.boundaryRectEditable = typeof state !== 'undefined' ? state : !$scope.boundaryRectEditable;
    boundaryRect.set({
      selectable: $scope.boundaryRectEditable,
      fill: $scope.boundaryRectEditable ? BOUNDARY_RECT_FILL_SELECTED : BOUNDARY_RECT_FILL_DISABLED,
      hasControls: $scope.boundaryRectEditable,
      hasBorders: $scope.boundaryRectEditable
    });
    planViewCanvas.renderAll();
  };

  $scope.renderBoundaryRect = function () {
    boundaryRect.set(getBoundaryRectProps());
    planViewCanvas.renderAll();
  };

  $scope.pathPoints = [];

  planViewCanvas.on('mouse:down', function(options) {
    if ($scope.boundaryRectEditable) {
      // Prevent point from being added when editing boundary rect
      return;
    }

    var point = options.e;

    var circle = new fabric.Circle({
      radius: POINT_RADIUS, 
      fill: POINT_FILL_COLOR, 
      left: point.offsetX - POINT_RADIUS,
      top: point.offsetY - POINT_RADIUS,
      selectable: false
    });
    circle.z = CAMERA_DEFAULT_HEIGHT; // Set default z-value for video;

    var label = new fabric.Text(($scope.pathPoints.length + 1).toString(), {
      fontWeight: POINT_LABEL_FONT_WEIGHT,
      fontSize: POINT_LABEL_FONT_SIZE,
      fontFamily: POINT_LABEL_FONT,
      left: point.offsetX - POINT_RADIUS/2,
      top: point.offsetY + POINT_RADIUS,
    });
    $scope.pathPoints.push({
      point: circle,
      label: label
    })
    planViewCanvas.add(circle);
    planViewCanvas.add(label);
    $scope.$apply();
  });

  $scope.deletePoint = function (index) {
    var pt = $scope.pathPoints[index];
    planViewCanvas.remove(pt.point);
    planViewCanvas.remove(pt.label);
    $scope.pathPoints.splice(index, 1);
    for (var i = 0; i < $scope.pathPoints.length; i++) {
      $scope.pathPoints[i].label.setText((i+1).toString());
    }
    planViewCanvas.renderAll();
  }

  $scope.normalizePathPoint = function (point) {
    var x = point.left - $scope.topLeft.x;
    var y = point.top - $scope.topLeft.y;
    var boundaryBoxX = x;
    var boundaryBoxY = y;
    return {
      x: Math.round(((-boundaryBoxY/boundaryRect.currentHeight) + 0.5) * $scope.boundaryHeight, 2),
      y: Math.round(-boundaryBoxX/boundaryRect.currentWidth * $scope.boundaryWidth, 2),
      z: point.z 
    };
  }

  function normalizePathPoints (pathPoints) {
    return pathPoints.map(function (point) {
      // Map to boundary box coordinates
      return $scope.normalizePathPoint(point);
    });
  }

  $scope.renderingVideo = false;

  $scope.renderVideoWithPath = function () {
    var pathPoints = $scope.pathPoints.map(function (pt) {
      return pt.point;
    });
    var cameraPathPoints = normalizePathPoints(pathPoints);

    $scope.renderingVideo = true;
    $http.post('/generate_video', {
      camera_path: cameraPathPoints,
      file_name: new Date().getTime(),
      world: $scope.world,
      planeRect: {
        x: parseInt($scope.planeTopLeft.x/scale),
        y: parseInt($scope.planeTopLeft.y/scale),
        width: parseInt(($scope.planeBottomRight.x - $scope.planeTopLeft.x)/scale),
        height: parseInt(($scope.planeBottomRight.y - $scope.planeTopLeft.y)/scale)
      },
      vanishingPoint: {
        x: parseInt($scope.vanishingPoint.left/scale),
        y: parseInt($scope.vanishingPoint.top/scale),
      },
      image: $scope.selectedImage.image
    }).success(function (res, status, headers, config) {
      if (res.status === 'success') {
        $scope.renderingVideo = false;
        var video = res.video;
        var $videoPlayer = $('.video-player');
        $videoPlayer.width(video.width);
        $videoPlayer.height(video.height);
        $videoPlayer.attr('src', video.src);
        $('#video-modal').modal();
      }
    }).error(function (res, status, headers, config) {
      alert('Rendering failed');
      $scope.renderingVideo = false;
    });
  }

  $scope.getRenderVideoButtonState = function () {
    var pathObject = planViewCanvas.getActiveObject();
    return pathObject && pathObject !== boundaryRect && pathObject.path;
  };

  toggleBoundaryRectEditing($scope.boundaryRectEditable);
  boundaryRect.set(getBoundaryRectProps());

  $scope.toggleBoundaryRectEditing = toggleBoundaryRectEditing;

  fabric.Image.fromURL('/static/img/' + $scope.selectedImage.plan, function (img) {
    img.set('selectable', false);
    planViewCanvas.add(img);
    planViewCanvas.add(boundaryRect);
  });

  // Plane slicing section
  imageCanvas = null;

  $scope.planeTopLeft = {x: 198, y: 225};
  $scope.planeBottomRight = {x: 198+378, y: 225+226};

  var planeBoundaryRect = new fabric.Rect({
    lockRotation: true,
    top: 225,
    left: 198,
    width: 378,
    height: 226,
    fill: 'rgba(178,74,24,0.5)'
  });

  var img = new Image();
  var scale;
  img.src = '/static/img/' + $scope.selectedImage.image;
  img.onload = function () {
    var imgWidth = this.width;
    var imgHeight = this.height;
    fabric.Image.fromURL('/static/img/' + $scope.selectedImage.image, function (img) {
      img.set('selectable', false);
      img.set('width', CANVAS_WIDTH);
      scale = CANVAS_WIDTH/imgWidth;
      var canvasHeight = scale * imgHeight; 
      img.set('height', canvasHeight);

      imageCanvas = new fabric.Canvas('image-container', {
        width: CANVAS_WIDTH,
        height: canvasHeight
      });
      imageCanvas.add(img);
      imageCanvas.add(planeBoundaryRect);
      
      $scope.vanishingPoint = new fabric.Circle({
        radius: 5, 
        fill: POINT_FILL_COLOR,
        left: 335,
        top: 415.5,
        lockScalingX: true,
        lockScalingY: true
      });

      $scope.vanishingPoint.setControlsVisibility({
        bl: false,
        br: false,
        mb: false,
        ml: false,
        mr: false,
        mt: false,
        tl: false,
        tr: false,
        mtr: false
      })
      imageCanvas.add($scope.vanishingPoint);

      imageCanvas.on('object:modified', function (object) {
        if (object.target === planeBoundaryRect) {
          updatePlaneBoundaryRect(object.target);
        }
        $scope.$apply();
      });
      $scope.renderPlaneCanvas();
      $scope.$apply();
    });
  };

  function updatePlaneBoundaryRect (rect) {
    $scope.planeTopLeft.x = Math.round(rect.left);
    $scope.planeTopLeft.y = Math.round(rect.top);
    $scope.planeBottomRight.x = Math.round(rect.left + rect.currentWidth);
    $scope.planeBottomRight.y = Math.round(rect.top + rect.currentHeight);
  }

  function getPlaneBoundaryRectProps () {
    return {
      scale: 1,
      scaleX: 1,
      scaleY: 1,
      width: $scope.planeBottomRight.x - $scope.planeTopLeft.x, 
      height: $scope.planeBottomRight.y - $scope.planeTopLeft.y,
      left: $scope.planeTopLeft.x,
      top: $scope.planeTopLeft.y
    };
  }

  $scope.renderPlaneCanvas = function () {
    planeBoundaryRect.set(getPlaneBoundaryRectProps());
    imageCanvas.renderAll();
  };

  $scope.world = {
    width: 741,
    height: 304,
    depth: 2095
  };
}
