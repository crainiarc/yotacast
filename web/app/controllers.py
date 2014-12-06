import os
import json

from flask import Flask, render_template, request, make_response
from app import app
from werkzeug import secure_filename


UPLOAD_FOLDER = '/static/snapshots/'

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


@app.route('/')
def index():
  return render_template('index.html')





@app.route('/upload', methods=['POST'])        
def upload_file():

	data = json.loads(request.data)

    if data["image"]:

        image = data["image"]

        file.save(os.path.join(app.config['UPLOAD_FOLDER'], image))

        