# Android-PdfMyXml
convert your android `XML` layouts into PDF document, works on all versions of Android.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--PdfMyXml-green.svg?style=flat)](https://android-arsenal.com/details/1/2297)

### Dependencies
* [`pdfjet`](https://github.com/soster/pdfjet)

## How to use

Option 1: Simply fork or download the project, you can also download and create `.aar` file yourself.

Option 2: Jitpack

Add Jitpack in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

Add to your dependencies:

```groovy
dependencies {
    compile 'com.github.se-bastiaan:Android-PdfMyXml:1.0.1'
}
```

## Notable features
* should work on all Android versions
* completely scalable
* supports bitmap re usage.
* production proved code. Used in a commercial project.

### Instructions
#### 1. create XML layouts
First create XML layouts. give it dimensions in **pixels** (and for all it's sub views) and proportions according landscape or portrait according to ratio **1:1.41**.<br/><br/>
page1.xml
```java
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="2115px"
                android:layout_height="1500px"
                android:background="@color/white">
  <TextView android:id="@+id/tv_hello"
                android:textColor="@color/black"
                android:textSize="27px"
                android:textStyle="bold"
                android:padding="6px"/>

</RelativeLayout>
```

you can create as many as pages/templates as you need.

#### 2. Implement a View renderer
implement your View renderer by extending `AbstractViewRenderer` or by anonymously instantiating it and injecting the layout id. the initView(View view) will supply you an inflated View automatically. There are other options but I wont cover it now.
```java
AbstractViewRenderer page = new AbstractViewRenderer(context, R.layout.page1) {
    private String _text;

    public void setText(String text) {
        _text = text;
    }

    @Override
    protected void initView(View view) {
        TextView tv_hello = (TextView)view.findViewById(R.id.tv_hello);
         tv_hello.setText(_text);
    }
};

// you can reuse the bitmap if you want
page.setReuseBitmap(true);

```

#### 3. Build the PDF document
Use `PdfDocument` or `PdfDocument.Builder` to add pages and render and run it all at background with progress bar.
```java
PdfDocument doc            = new PdfDocument(ctx);

// add as many pages as you have
doc.addPage(page);

doc.setRenderWidth(2115);
doc.setRenderHeight(1500);
doc.setOrientation(PdfDocument.A4_MODE.LANDSCAPE);
doc.setProgressTitle(R.string.gen_please_wait);
doc.setProgressMessage(R.string.gen_pdf_file);
doc.setFileName("test");
doc.setInflateOnMainThread(false);
doc.setListener(new PdfDocument.Callback() {
    @Override
    public void onComplete(File file) {
        Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete");
    }

    @Override
    public void onError(Exception e) {
        Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
    }
});

doc.createPdf(ctx);

```

or use `PdfDocument.Builder`
```java
new PdfDocument.Builder(ctx).addPage(page).filename("test").orientation(PdfDocument.A4_MODE.LANDSCAPE)
                         .progressMessage(R.string.gen_pdf_file).progressTitle(R.string.gen_please_wait).renderWidth(2115).renderHeight(1500)
                         .listener(new PdfDocument.Callback() {
                             @Override
                             public void onComplete(File file) {
                                 Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete");
                             }

                             @Override
                             public void onError(Exception e) {
                                 Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
                             }
                         }).create().createPdf(this);
```

### License
If you like it -> star or share it with others

```
Copyright (C) 2016 Tomer Shalev (https://github.com/HendrixString)  

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

### Contact Author
* [tomer.shalev@gmail.com](tomer.shalev@gmail.com)
* [Google+ TomershalevMan](https://plus.google.com/+TomershalevMan/about)
* [Facebook - HendrixString](https://www.facebook.com/HendrixString)
