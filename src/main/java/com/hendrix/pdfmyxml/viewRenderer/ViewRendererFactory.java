package com.hendrix.pdfmyxml.viewRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * a recycled view renderer factory.
 *
 * @author Tomer Shalev
 */
public class ViewRendererFactory {
    volatile static private RecycledViewRenderer _rvr = new RecycledViewRenderer();

    /**
     * recycle a view renderer with the following
     *
     * @param ctx the context
     * @param view the view to renderer
     * @param width the width
     * @param height the height
     *
     * @return a bitmap of the view
     */
    static public Bitmap getBitmapOfView(Context ctx, View view, int width, int height) {
        return _rvr.recycleWith(ctx, view).render(width, height);
    }

    /**
     * recycle a view renderer with the following
     *
     * @param ctx the context
     * @param layoutResId the layout resource identifier
     * @param width the width
     * @param height the height
     *
     * @return a bitmap of the view
     */
    static public Bitmap getBitmapOfView(Context ctx, int layoutResId, int width, int height) {
        return _rvr.recycleWith(ctx, layoutResId).render(width, height);
    }

}
