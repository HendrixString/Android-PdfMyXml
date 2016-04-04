package com.hendrix.pdfmyxml.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Tomer Shalev
 */
@SuppressWarnings("UnusedDeclaration")
public final class BitmapUtils
{
    private static DisplayMetrics _dm;
    private static Resources 			_resources;

    private BitmapUtils() {
    }

    public static void init(Resources resources)
    {
        _resources 	= resources;
        _dm 				= _resources.getDisplayMetrics();
    }

    public enum ScaleMode {
        STRETCH, LETTERBOX, ZOOM, NONE
    }

    /**
     * not the regular bitmap resizer. much smarter with resizing and cropping options and resizing with proportions.
     * @param bmSrc the source bitmap
     * @param rectDest rectangle, you need to pass at least width or height or both. For example, specify only width to scale, and height will be calc dynamically
     * @param scaleMode - one of the enums: ScaleMode {STRETCH,LETTERBOX,ZOOM,NONE}
     * @param fitBitmapResult effective cropping of dead pixels, applied only when <code>ScaleMode.LETTERBOX</code> is used
     * @param scaleConditionally scale only if <code>rectDest < bmSrc.rect</code>, i.e <code>rectDest</code> is contained inside <code>bmSrc.rect</code>
     * @return resulting bitmap
     *
     * <p><b>TODO:</b><br>
     * - add translation for zoom, center images<br>
     * - requires QA (i didnt have time)
     *
     */
    static public Bitmap resizeBitmap(Bitmap bmSrc, Rect rectDest, ScaleMode scaleMode, boolean fitBitmapResult,
                                      boolean scaleConditionally, boolean recycleBmpSource)
    {
        Matrix mat															=	new Matrix();
        Bitmap bmResult;

        int wOrig																=	bmSrc.getWidth();
        int hOrig																=	bmSrc.getHeight();

        int wScaleTo														=	rectDest.width();
        int hScaleTo														=	rectDest.height();

        if(wScaleTo==0 && hScaleTo==0)
            throw new Error("resizeBitmap: need at least $scaleTo.width or $scaleTo.height");

        float arW																=	(wScaleTo != 0) ? (float)wScaleTo / (float)wOrig : Float.NaN;
        float arH																=	(hScaleTo != 0) ? (float)hScaleTo / (float)hOrig : Float.NaN;

        arW																			=	Float.isNaN(arW) ? arH: arW;
        arH																			=	Float.isNaN(arH) ? arW: arH;

        float arMin															= Math.min(arW, arH);
        float arMax															= Math.max(arW, arH);

        wScaleTo																=	(wScaleTo != 0) ? wScaleTo : (int)(((float)wOrig)*arW);
        hScaleTo																=	(hScaleTo != 0) ? hScaleTo : (int)(((float)hOrig)*arH);

        if(scaleConditionally) {
            boolean isScaleConditionally					=	(wScaleTo >= bmSrc.getWidth()) && (hScaleTo >= bmSrc.getHeight());
            if(isScaleConditionally)
                return Bitmap.createBitmap(bmSrc);
        }

        bmResult 																= Bitmap.createBitmap(wScaleTo, hScaleTo, Config.ARGB_8888);

        Canvas canvas 													= new Canvas(bmResult);

        switch(scaleMode)
        {
            case STRETCH:
            {
                mat.reset();
                mat.postScale(arW, arH);

                canvas.drawBitmap(bmSrc, mat, null);

                break;
            }
            case LETTERBOX:
            {
                if(fitBitmapResult) {
                    if(bmResult != null)
                        bmResult.recycle();

                    wScaleTo													=	(int)((float)wOrig * arMin);
                    hScaleTo													=	(int)((float)hOrig * arMin);
                    bmResult													=	Bitmap.createBitmap(wScaleTo, hScaleTo, Config.ARGB_8888);

                    canvas.setBitmap(bmResult);
                }

                mat.reset();
                mat.postScale(arMin, arMin);

                canvas.drawBitmap(bmSrc, mat, null);

                break;
            }
            case ZOOM:
            {
                mat.reset();
                mat.postScale(arMax, arMax);

                canvas.drawBitmap(bmSrc, mat, null);

                break;
            }
            case NONE:
            {
                mat.reset();

                canvas.drawBitmap(bmSrc, mat, null);

                break;
            }

        }

        if(recycleBmpSource)
            bmSrc.recycle();

        return bmResult;
    }

    /**
     *
     * @param picturePath complete path name for the file to be decoded.
     * @return Bitmap instance
     */
    public static Bitmap pathToBitmap(String picturePath)
    {
        BitmapFactory.Options opts	=	new BitmapFactory.Options();

        opts.inDither								=	false;             //Disable Dithering mode
        opts.inPurgeable						=	true;              //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        opts.inInputShareable				=	true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        opts.inTempStorage					=	new byte[32 * 1024];

        return BitmapFactory.decodeFile(picturePath, opts);
    }

    /**
     * cut a circle from a bitmap
     *
     * @param picturePath complete path name for the file.
     * @param destCube the cube dimension of dest bitmap
     * @return the bitmap
     */
    public static Bitmap cutCircleFromBitmap(String picturePath, int destCube)
    {
        BitmapFactory.Options opts	=	new BitmapFactory.Options();

        opts.inDither								=	false;             //Disable Dithering mode
        opts.inPurgeable						=	true;              //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        opts.inInputShareable				=	true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        opts.inTempStorage					=	new byte[32 * 1024];

        Bitmap bitmapImg 						= BitmapFactory.decodeFile(picturePath, opts);

        int cube 										= destCube;

        if(bitmapImg == null)
            return null;

        int smallest 								= Math.min(bitmapImg.getWidth(), bitmapImg.getHeight());

        Bitmap output 							= Bitmap.createBitmap(cube, cube, Config.ARGB_8888);
        Canvas canvas 							= new Canvas(output);

        final int color 						= 0xff424242;
        final Paint paint 					= new Paint();

        int left 										= (int) ((bitmapImg.getWidth() - smallest) * 0.5);
        int top 										= (int) ((bitmapImg.getHeight() - smallest) * 0.5);

        final Rect rectSrc 					= new Rect(left, top, left + smallest, top + smallest);
        final Rect rectDest				 	= new Rect(0, 0, cube, cube);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawCircle(cube / 2, cube / 2, cube / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(bitmapImg, rectSrc, rectDest, paint);

        bitmapImg.recycle();

        return output;
    }

    /**
     * Bitmap into compressed PNG
     *
     * @param image the Bitmap
     * @return byte array of PNG
     */
    public static byte[] bitmapToPng(final Bitmap image)
    {
        if (image == null)
            return null;

        ByteArrayOutputStream ba = new ByteArrayOutputStream();

        if (image.compress(CompressFormat.PNG, 100, ba))
            return ba.toByteArray();
        else
            return null;
    }

    /**
     * Bitmap into compressed PNG as InputStream object
     *
     * @param image the Bitmap
     * @return compressed PNG as InputStream object
     */
    public static ByteArrayInputStream bitmapToPngInputStream(final Bitmap image)
    {
        return new ByteArrayInputStream(bitmapToPng(image));
    }

    /**
     * Bitmap into compressed jpeg
     *
     * @param image the Bitmap
     * @param quality quality of compression
     * @return byte array of jpeg
     */
    public static byte[] bitmapToJpg(final Bitmap image, final int quality)
    {
        if (image == null)
            return null;

        ByteArrayOutputStream ba = new ByteArrayOutputStream();

        if (image.compress(CompressFormat.JPEG, quality, ba))
            return ba.toByteArray();
        else
            return null;
    }

    /**
     * raw byteArray into Bitmap
     *
     * @param image the byteArray
     * @return Bitmap
     */
    public static Bitmap imgToBitmap(final byte[] image)
    {
        if (image == null)
            return null;

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * dp to pixels
     * @param val dp
     * @return pixels
     */
    public static int dpToPx(float val)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, _dm);
    }

    /**
     * mask bitmap
     * @param src todo
     * @param mask todo
     * @param dst todo
     * @param dstCanvas todo
     * @param paint todo
     * @param paintMode todo
     * @return todo
     */
    public static Bitmap maskBitmap(Bitmap src, Bitmap mask, Bitmap dst, Canvas dstCanvas, Paint paint, PorterDuffXfermode paintMode)
    {
        if (dst == null)
            dst 				= Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);

        if (dstCanvas == null)
            dstCanvas 	= new Canvas(dst);

        if (paintMode == null)
            paintMode 	= new PorterDuffXfermode(Mode.DST_IN);

        if (paint == null) {
            paint 			= new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(paintMode);
        }

        dstCanvas.drawBitmap(src, 0, 0, null);
        dstCanvas.drawBitmap(mask, 0, 0, paint);

        paint.setXfermode(null);

        return dst;
    }

    /**
     * mask a bitmap
     *
     * @param src the bitmap to mask
     * @param drawableResId a resource id of the mask
     * @return a bitmap
     */
    public static Bitmap maskBitmap(Bitmap src, int drawableResId)
    {
        Bitmap mask = BitmapFactory.decodeResource(_resources, drawableResId);

        return maskBitmap(src, mask, null, null, null, null);
    }

    /**
     * create a circle from cutout from a bitmap.
     * does not alter sizes.
     *
     * @param bitmap the bitmap
     * @see #cutCircleFromBitmap(String, int)
     * @return a bitmap circle cutout
     */
    public static Bitmap roundBitMap(Bitmap bitmap)
    {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        BitmapShader shader = new BitmapShader (bitmap,  TileMode.CLAMP, TileMode.CLAMP);
        Paint paint 				= new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setShader(shader);

        Canvas c 						= new Canvas(circleBitmap);

        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

        return circleBitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius)
    {
        Bitmap sbmp;

        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp 					= Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp 					= bmp;

        Bitmap output 		= Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
        Canvas canvas 		= new Canvas(output);

        //final int color = 0xffa19774;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Rect rect 	= new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.parseColor("#BAB399"));

        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f, sbmp.getWidth() / 2+0.1f, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

}