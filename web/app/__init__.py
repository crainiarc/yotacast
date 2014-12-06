__author__ = 'yangshun'

from flask import Flask

app = Flask(__name__)

STATIC_PATH = './app/static'

import controllers
