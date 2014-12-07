# Yotacast

This project was a submission to [Hackatron Asia 2014](http://hackatron.techinasia.com), which we subsequently won first place. Yotacast is now released open-source so that everyone can get a feel on how to make a YotaPhone app.

## Introduction
Yotacast is a baby monitor/home surveilance app made specificially for the [YotaPhone's](http://yotaphone.com/ru-en) e-ink display. While there are many baby monitor apps are there in the market, those implementations require users to leave their screens turned on constantly in order to get the camera feeds. With YotaPhone's e-ink display, we can now stream snapshots at preset time intervals to the e-ink display. This means **constant access** to monitor your baby/house, and at the same time save your phone's power consumption.

Using OpenCV, we added motion detectors to the cameras and will notify your phone whenever the your webcam detects movements at home. At the same time, we used local histogram equalization algorithms to enhance the grayscale image so that the images are displayed in greater detail in the e-ink display.