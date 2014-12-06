import os
import json

from flask import Flask, render_template, request, make_response
from app import app
import time
import datetime
import cv2.cv as cv
import cv2 as cv2
import base64

UPLOAD_FOLDER = 'app/static/snapshots/'

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


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
        with open(os.path.join(app.config['UPLOAD_FOLDER'], str(no_microseconds_time) + '.txt'), 'w+') as f:
            f.write(base64_string)
            f.close()
        
        imgdata = base64.b64decode(base64_string)
        raw_image_filename = os.path.join(app.config['UPLOAD_FOLDER'], str(no_microseconds_time) + '.jpg')
        with open(raw_image_filename, 'wb') as f:
            f.write(imgdata)

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
