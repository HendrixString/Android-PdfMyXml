package com.hendrix.pdfmyxml.viewRenderer;

import android.content.Context;
import android.view.View;

/**
 * a simple view renderer implementation of abstract view renderer with empty init.
 * i.e - use it to render a view that was already measured or a layout resource
 * that will get as big as it needs to
 * @author Tomer Shalev
 */
public class SimpleViewRenderer extends AbstractViewRenderer {
    /**
     * @param ctx         a context
     * @param layoutResId a layout resource id
     */
    public SimpleViewRenderer(Context ctx, int layoutResId) {
        super(ctx, layoutResId);
    }

    /**
     * @param ctx         a context
     * @param view        a view
     */
    public SimpleViewRenderer(Context ctx,View view) {
        super(ctx, view);
    }

    public SimpleViewRenderer() {}

    /**
     * empty view init
     *
     * @param view the view
     */
    @Override
    protected void initView(View view) {
    }

}
