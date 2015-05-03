package com.hendrix.pdfmyxml.viewRenderer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * a View renderer with recycling capabilities.
 *
 * @author Tomer Shalev
 */
public class RecycledViewRenderer extends SimpleViewRenderer {

    public RecycledViewRenderer() {
    }

    /**
     * recycle the view renderer with a new view
     *
     * @param ctx the context
     * @param view a new view
     */
    public RecycledViewRenderer recycleWith(Context ctx, View view) {
        attachContext(ctx);

        _view = view;

        return this;
    }

    /**
     * recycle the view renderer with a new view
     *
     * @param ctx the context
     * @param layoutResId a resource layout id
     */
    public RecycledViewRenderer recycleWith(Context ctx, int layoutResId) {
        return recycleWith(ctx, LayoutInflater.from(ctx).inflate(layoutResId, null));
    }

}
