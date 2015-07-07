package com.hendrix.pdfmyxml.viewRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hendrix.pdfmyxml.utils.MeasureUtils;

/**
 * @author Tomer Shalev
 */
abstract public class AbstractViewRenderer implements IViewRenderer {
    protected View      _view               = null;
    protected Context   _ctx                = null;
    private Bitmap      _bmp                = null;
    private boolean     _flagReuseBitmap    = false;
    private Object      _data               = null;

    /**
     *
     * @param ctx           a context
     * @param layoutResId   a layout resource id
     */
    public AbstractViewRenderer(Context ctx, int layoutResId) {
        this(ctx, LayoutInflater.from(ctx).inflate(layoutResId, null));
    }

    /**
     *
     * @param ctx   a context
     * @param view  an inflated view
     */
    public AbstractViewRenderer(Context ctx, View view) {
        attachContext(ctx);

        _view = view;
    }

    protected AbstractViewRenderer() {
        throw new UnsupportedOperationException();
    }

    /**
     * attach a context to the renderer
     *
     * @param ctx the context
     */
    @Override
    public void attachContext(Context ctx) {
        _ctx = ctx;
    }

    /**
     * render the view with a reused {@link Bitmap}
     *
     * @param bitmap a bitmap to render into (reused)
     * @param width  the width
     * @param height the height
     *
     * <h1>Note: </h1>
     * on API <= 17, you must give explicit {@code width} and {@code height} because of a bug in {@link android.widget.RelativeLayout}
     */
    @Override
    public final Bitmap render(Bitmap bitmap, int width, int height) {
        validate();

        initView(getView());

        View view       = getView();

        int specWidth   = View.MeasureSpec.makeMeasureSpec(width,  width==0   ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY);
        int specHeight  = View.MeasureSpec.makeMeasureSpec(height, height==0  ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY);

        view.measure(specWidth, specHeight);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        // recycle bitmap
        Bitmap b        = bitmap;//Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c        = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());

        view.draw(c);

        return  _bmp=b;
    }

    /**
     * render the bitmap
     *
     * @param width  the wanted width for rendering, in Pixels. if 0, then the view will measure itself as big as it needs to be(only on <b>API > 17</b>).
     * @param height the wanted height for rendering, in Pixels. if 0, then the view will measure itself as big as it needs to be(only on <b>API > 17</b>).
     *
     * <h1>Note: </h1>
     * on <b>API <= 17</b>, you must give explicit {@code width} and {@code height} because of a bug in {@link android.widget.RelativeLayout}
     */
    @Override final public Bitmap render(int width, int height) {
        validate();

        initView(getView());

        View view       = getView();

        int specWidth   = View.MeasureSpec.makeMeasureSpec(width  == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : width,  width == 0 ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY);
        int specHeight  = View.MeasureSpec.makeMeasureSpec(height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : height, height==0  ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY);
        int a = ViewGroup.LayoutParams.MATCH_PARENT;
        System.out.println("a");

        try {
            view.measure(specWidth, specHeight);
        }
        catch (NullPointerException exc) {
            exc.printStackTrace();
        }

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap b;

        // recycle bitmap
        if(!_flagReuseBitmap) {
            disposeBitmap();
            b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        }
        else {
            // reuse bitmap
            b = (_bmp==null || _bmp.isRecycled()) ? Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888) : _bmp;
            b.eraseColor(Color.TRANSPARENT);
        }

        Canvas c        = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());

        view.draw(c);

        return _bmp=b;
    }

     final public Bitmap render2(int width, int height) {
        validate();

        initView(getView());

        View view       = getView();

        int specWidth   = View.MeasureSpec.makeMeasureSpec(MeasureUtils.DIPToPixels(_ctx, width),  width==0   ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY);
        int specHeight  = View.MeasureSpec.makeMeasureSpec(MeasureUtils.DIPToPixels(_ctx, height), height==0  ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY);

        try {
            view.measure(specWidth, specHeight);
        }
        catch (NullPointerException exc) {
            exc.printStackTrace();
        }

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap b;

        // recycle bitmap
        if(!_flagReuseBitmap) {
            disposeBitmap();
            b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        }
        else {
            // reuse bitmap
            b = (_bmp==null || _bmp.isRecycled()) ? Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888) : _bmp;
            b.eraseColor(Color.TRANSPARENT);
        }

        Canvas c        = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());

        view.draw(c);

        return _bmp=b;
    }

    /**
     * return the rendered bitmap
     *
     * @return the rendered bitmap
     */
    @Override
    public Bitmap getBitmap() {
        return _bmp;
    }

    /**
     * get the view to render
     *
     * @return the view
     */
    @Override
    public View getView() {
        return _view;
    }

    /**
     * width of the canvas
     *
     * @return width
     */
    @Override
    public int getWidth() {
        return getView().getWidth();
    }

    /**
     * height of the canvas
     *
     * @return height
     */
    @Override
    public int getHeight() {
        return getView().getHeight();
    }

    /**
     * init the view. this is where you will do setup of
     * the view's subviews, assign text etc...
     */
    abstract protected void initView(View view);

    /**
     * validate the input
     *
     * @throws IllegalArgumentException if context or view is null
     */
    private void validate() {
        if(_ctx==null)
            throw new IllegalArgumentException("ViewRenderer:: context was not set!!");
        if(_view==null)
            throw new IllegalArgumentException("ViewRenderer:: view or layout resource was not set!!");
    }

    /**
     *
     * @return flag reuse bitmap
     */
    public boolean isReuseBitmap() {
        return _flagReuseBitmap;
    }

    /**
     * flag for indicating bitmap re usage if possible for multiple.
     *
     * @param flagReuseBitmap {@code true/false}
     */
    public void setReuseBitmap(boolean flagReuseBitmap) {
        _flagReuseBitmap = flagReuseBitmap;
    }

    /**
     * dispose the item
     */
    @Override
    public void dispose() {
        _bmp.recycle();
        _view = null;
        _ctx = null;
    }

    /**
     * dispose the bitmap
     */
    public void disposeBitmap() {
        if(_bmp != null)
            _bmp.recycle();
        _bmp = null;
    }

    @Override
    public void setData(Object data) {
        _data = data;
    }

    @Override
    public Object getData() {
        return _data;
    }
}
