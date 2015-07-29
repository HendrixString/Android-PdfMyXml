# Android-PdfMyXml
convert your android `XML` layouts into PDF document, works on all versions of Android.

### Dependencies
* [`pdfjet`](https://github.com/soster/pdfjet)

## How to use
simply fork or download the project, you can also download and create `.aar` file yourself.

## Notable features
* should work on all Android versions
* completely scalable
* supports bitmap re usage

##### Examples
1. First create XML layouts. give it dimensions in pixels (and for all it's sub views) and proportions according landscape or portrait according to ratio 1:1.41.<br/><br/>
page1.xml
```
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

2. implement you View renderer by extending `AbstractViewRenderer` or by anonymously instantiating it and injecting the layout id.
```
AbstractViewRenderer page = new AbstractViewRenderer(this, R.layout.page1) {
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

3. Use `PdfDocument` to render and run it all at background with progress bar.
```
PdfDocument doc            = new PdfDocument(ctx);

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
    public void onError() {
        Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
    }
});

doc.createPdf(ctx);

```

or use `PdfDocument.Builder`
```
new PdfDocument.Builder().context(this).addPage(page).filename("test").orientation(PdfDocument.A4_MODE.LANDSCAPE)
                         .progressMessage(R.string.gen_pdf_file).progressTitle(R.string.gen_please_wait).renderWidth(2115).renderHeight(1500)
                         .listener(new PdfDocument.Callback() {
                             @Override
                             public void onComplete(File file) {
                                 Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete");
                             }

                             @Override
                             public void onError() {
                                 Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
                             }
                         }).create().createPdf(this);
```

### Terms
* completely free source code. [Apache License, Version 2.0.](http://www.apache.org/licenses/LICENSE-2.0)
* if you like it -> star or share it with others

### Contact Author
* [tomer.shalev@gmail.com](tomer.shalev@gmail.com)
* [Google+ TomershalevMan](https://plus.google.com/+TomershalevMan/about)
* [Facebook - HendrixString](https://www.facebook.com/HendrixString)
