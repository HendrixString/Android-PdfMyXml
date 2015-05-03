package com.hendrix.pdfmyxml.utils;

import android.content.Context;

/**
 * @author Tomer Shalev
 */
@SuppressWarnings("unused")
public class MeasureUtils {

    private MeasureUtils() {
    }

    static public int DIPToPixels(Context context, float dip)
    {
        final float scale = context.getResources().getDisplayMetrics().density;

        // also can use
        //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, context.getResources().getDisplayMetrics());

        return (int) (dip * scale + 0.5f);
    }

    static public int pixelsToDIP(Context context, int pixels)
    {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) ((pixels - 0.5f) / scale);
    }

}