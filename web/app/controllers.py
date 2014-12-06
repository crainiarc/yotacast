import os
import json

from flask import Flask, render_template, request, make_response
from app import app
import time
import datetime

def module_exists(module_name):
    try:
        __import__(module_name)
    except ImportError:
        return False
    else:
        return True

has_cv2 = module_exists('cv2')
if has_cv2:
    import cv2.cv as cv
    import cv2 as cv2

import base64

UPLOAD_FOLDER = 'app/static/snapshots/'

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

previous_grayscale_img = None

@app.route('/')
def index():
  return render_template('index.html')

@app.route('/upload', methods=['POST'])        
def upload_file():
    data = json.loads(request.data)
    if data['image']:
        base64_string = data['image']
        posix_now = time.time()
        d = datetime.datetime.fromtimestamp(posix_now)
        no_microseconds_time = int(time.mktime(d.timetuple()))
        
        # Save base64 string as <timestamp>.txt
        with open(os.path.join(app.config['UPLOAD_FOLDER'], str(no_microseconds_time) + '.txt'), 'w+') as f:
            f.write(base64_string)
            f.close()
        
        # Save raw image as <timestamp>.jpg
        imgdata = base64.b64decode(base64_string)
        raw_image_filename = str(no_microseconds_time) + '.jpg'
        raw_image_filepath = os.path.join(app.config['UPLOAD_FOLDER'],  raw_image_filename)
        with open(raw_image_filepath, 'wb') as f:
            f.write(imgdata)

        if has_cv2:
            # Save grayscale image as <timestamp>-grayscale.jpg
            grayscale_image = cv2.imread(raw_image_filepath, cv2.CV_LOAD_IMAGE_GRAYSCALE)
            grayscale_image_filename = str(no_microseconds_time) + '-grayscale.jpg'
            grayscale_image_filepath = os.path.join(app.config['UPLOAD_FOLDER'], grayscale_image_filename)
            cv2.imwrite(grayscale_image_filepath, grayscale_image)

            global previous_grayscale_img
            movement = False
            if previous_grayscale_img is not None:
                diff_img = cv2.absdiff(previous_grayscale_img, grayscale_image)
                total_diff = sum([sum(row) for row in diff_img])
                width = diff_img.shape[0]
                height = diff_img.shape[1]
                total_pixels = width * height
                average_diff = float(total_diff) / total_pixels
                if average_diff > 3:
                    movement = True
            previous_grayscale_img = grayscale_image

            # Save equalized image as <timestamp>-equalized.jpg
            clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8,8))
            eq = clahe.apply(grayscale_image)
            equalized_image_filename = str(no_microseconds_time) + '-equalized.jpg'
            equalized_image_filepath = os.path.join(app.config['UPLOAD_FOLDER'], equalized_image_filename)
            cv2.imwrite(equalized_image_filepath, eq)

            # Save latest.json
            with open(os.path.join(app.config['UPLOAD_FOLDER'], 'latest.json'), 'w+') as f:
                f.write(json.dumps({
                    'raw_string': base64_string,
                    'raw_image': raw_image_filename,
                    'grayscale_image': grayscale_image_filename,
                    'equalized_image': equalized_image_filename,
                    'movement': movement
                }))
        else:
            with open(os.path.join(app.config['UPLOAD_FOLDER'], 'latest.json'), 'w+') as f:
                f.write(json.dumps({
                    'raw_string': base64_string,
                    'raw_image': raw_image_filename
                }))
                f.close()

        return json.dumps({'status':'success'})

@app.route('/latest_image', methods=['GET'])        
def latest_image():
    with open(os.path.join(app.config['UPLOAD_FOLDER'], 'latest.txt'), 'r+') as f:
        base64_string = f.readline()
        f.close()
    return json.dumps({'status':'success', 'image': base64_string})
