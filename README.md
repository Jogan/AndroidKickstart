Kickstart App
=============

- A template/bootstrap Android app preconfigured with some useful classes &amp; libraries
- One notable feature is the debug drawer by ([@Jake Wharton][1]) as seen in the U2020 app

Usage
-----

<pre>
<code>
  kickstartapp.py [-h] [-d OUTPUT_DIR] APP_NAME PACKAGE PREFIX COMPILE_SDK_VERSION MIN_SDK_VERSION TARGET_SDK_VERSION

  Create an Android App skeleton
  
  positional arguments:
    APP_NAME              the name of the generated application
    PACKAGE               the name of the generated package
    PREFIX                prefix for import classes. Eg 'Awesome' for AwesomeApp
    COMPILE_SDK_VERSION   Sdk version to use to compile the app. Eg 19
    MIN_SDK_VERSION       Minimum sdk version the app targets. Eg 14
    TARGET_SDK_VERSION    Target sdk version the app targets. Eg 19
  
  optional arguments:
    -h, --help            show this help message and exit
    -d OUTPUT_DIR, --output_directory OUTPUT_DIR
                          Output direct for the generated project
</code>
</pre>

Example:

`./kickstartapp.py -d /home/john/ AwesomeApp com.example.awesomeapp Awesome 19 14 19`

To Open Project
---------------

* Start Android Studio
* Select "Open Project" and select the generated root Project folder
* You may be prompted with "Unlinked gradle project" -> Select "Import gradle project" and select the option to use the gradle wrapper
* You may also be prompted to change to the appropriate SDK folder for your local machine
* Once the project has compiled -> run the project!


[1]:https://github.com/JakeWharton
