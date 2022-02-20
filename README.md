# 5p1d3r
web page parser with a javafx gui

## Overview

I love data analysis. I often find my self creating graph and charts of various things. This can be a tedious task; copying and pasting data from one place to another. Manually running DOM selectors with jquery in a page,or building a node/python based web parser. I really wanted something simple with a gui.

###### *This software is for educational purposes only. The author is not responsible for its misuse. Please use in accordance with data source policies, threshold, and limits.*


**5p1d3r** does this.

## Features

- request throttling
- multithreading
- link following
- url lists
- save and load configurations
- data transformation

## How to

- Download the source and build the jar
- Download a pre-built binary
    - download the far jar:
        - run: ```java -jar 5p1d3r-2.0.jar``` (requires jdk17+)
    - Windows installer: [5p1d3r-2.0.exe](https://github.com/adamyork/5p1d3r/releases/download/2.0/5p1d3r-2.0.exe) 
    - Mac: TBD
- run the app

![application preview](/app.png?raw=true "Application Preview")

- Select a URL method:
    - URL: a single starting url to fetch data from
    - URL List: multiple Uri's to start as source
        - Click the plus symbol next to the URL input text field to select a list.

- Application options
    - Throttling: Time in milliseconds to wait between requesting urls.
    - Threading: Number of concurrent urls to fetch 
    - Follow Links: For each url requested, parse the page for <a> tags that match the Link Pattern. Follow each link a run the dom queries listed.
    - DOM Queries: These are DOM selectors. Standard selection syntax applies. i.e. 
    ````css
    img .someClass > div a .someOtherClass
    ````
    - Transforms: For each result returned by a selector, feed it through a groovy transform. 
        - Two are provided with the app: 
            - basicJSONTransform
            - basicCSVTransform
        - Any valid groovy will work. However, the context is considered the internals of a function.
            - input type is : **org.jsoup.select.Elements** and **org.jsoup.nodes.Document**
            - input variable names are : element and document
            - must return a map or in the case of a csv transform, an array
            
- Select start
- Select a save file option compatible with the transforms you have provided.
- You can abort the process at any time.
- You can save your configuration under File -> Save

![bulk downloading](/dl.png?raw=true "Bulk Downloading")

If you have scraped a page and created a list of urls, there may be  a need to download the results.
This tool modal, will allow you to specify a single url list to download from.

**Note this is effectively beta software. I am sure there are issues. Please file a bug report if you find anything**

## Modular build commands
```
jdeps -R --multi-release 17 --ignore-missing-deps --class-path "build\libs\BOOT-INF\lib" --print-module-deps build\classes\java\main\com\github\adamyork\fx5p1d3r\Main.class > temp.txt
jdeps -R build\libs\5p1d3r-1.1.jar > temp.txt
jpackage --type exe --dest build/installer --input build/libs --name 5p1d3r --main-class org.springframework.boot.loader.JarLauncher --main-jar 5p1d3r-1.1.jar --java-options -Xmx2048m --runtime-image build/java-runtime --app-version 1.1 --win-dir-chooser
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules "java.base,java.desktop,java.logging" --output build/java-runtime
```

## TODO
- Tooltips
- Finish native packaging for Mac in build file
- Mac/linux tail file