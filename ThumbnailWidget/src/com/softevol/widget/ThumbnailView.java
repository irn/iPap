package com.softevol.widget;

import com.softevol.util.ThumbnailUtil;
import com.softevol.widget.thumbnailview.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class ThumbnailView extends View {
    private static String TAG = ThumbnailView.class.getName();
    
    public ThumbnailView(Context context) {
        this(context, null, 0);
    }
    
    public ThumbnailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbnailView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        
        if (attrs == null) {
            mMatrix = new Matrix();
            return;
        }
        
        TypedArray a = context.obtainStyledAttributes(attrs, com.softevol.widget.thumbnailview.R.styleable.ThumbnailView);        
        int resId = a.getResourceId(R.styleable.ThumbnailView_src, 0);
        setImageResource(resId);
        mMatrix = new Matrix();
    }

    /**
     * Return the view's drawable, or null if no drawable has been assigned.
    */
    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Sets a drawable as the content of this ThumbnailView.
     * 
     * @param resId the resource identifier of the the drawable
     */
    public void setImageResource(int resId) {
        updateDrawable(null);
        mResource = resId;
        mUri = null;
        loadImage();
        requestLayout();
        invalidate();
    }

    /**
     * Sets the content of this ThumbnailView to the specified Uri.
     * 
     * @param uri The Uri of an image
     */
    public void setImageURI(Uri uri) {
        updateDrawable(null);
        mResource = 0;
        mUri = uri;
        loadImage();
        requestLayout();
        invalidate();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        resizeFromDrawable();
    }
    
  /**
  * Load image from the last selected source.
  */
    private void loadImage() {
        if (getWidth() <= 0 && getHeight() <= 0) {
            return;
        }
        
        Resources rsrc = getResources();
        if (rsrc == null) {
            return;
        }

        Drawable d = null;

        if (mResource != 0) {
            try {
                d = getDrawableFromResource();
            } catch (Exception e) {
                // Don't try again.
                mUri = null;
            }
        } else if (mUri != null) {
            d = getDrawableFromUri();
            
            if (d == null) {
                // Don't try again.
                mUri = null;
            }
        } else {
            return;
        }

        updateDrawable(d);
    }

    /**
     * Load drawable from the selected resource.
     */
    private Drawable getDrawableFromResource() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), mResource, options);
        int inSampleSize = 0;
        
        if (getWidth() > 0 && getHeight() > 0) {
            inSampleSize = ThumbnailUtil.getInSampleSize(options.outWidth, options.outHeight, getWidth(), getHeight());
        }
        
        Log.d(TAG, "In image:\t" + options.outWidth + "x" + options.outHeight);
        
        options.inJustDecodeBounds = false;
        options.inSampleSize = Math.max(1 , inSampleSize);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mResource, options);
        
        Log.d(TAG, "Out image:\t" + options.outWidth + "x" + options.outHeight);
        Log.d(TAG, "View size:\t" + getWidth() + "x" + getHeight());
        Log.d(TAG, "inSampleSize:\t" + options.inSampleSize);
        Log.d(TAG, "Scale:\t\t" + (float)1/options.inSampleSize);
        
        return new BitmapDrawable(getContext().getResources(), bitmap);
    }
    
    /**
     * Load drawable from the selected uri.
     */
    private Drawable getDrawableFromUri() {
        String path = mUri.getPath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int inSampleSize = 0;
        
        if (getWidth() > 0 && getHeight() > 0) {
            inSampleSize = ThumbnailUtil.getInSampleSize(options.outWidth, options.outHeight, getWidth(), getHeight());
        }
        
        Log.d(TAG, "In image:\t" + options.outWidth + "x" + options.outHeight);
        
        options.inJustDecodeBounds = false;
        options.inSampleSize = Math.max(1 , inSampleSize);
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        
        Log.d(TAG, "Out image:\t" + options.outWidth + "x" + options.outHeight);
        Log.d(TAG, "View size:\t" + getWidth() + "x" + getHeight());
        Log.d(TAG, "inSampleSize:\t" + options.inSampleSize);
        Log.d(TAG, "Scale:\t\t" + (float)1/options.inSampleSize);
        
        return new BitmapDrawable(getContext().getResources(), bitmap);
    }
    
    private void updateDrawable(Drawable d) {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
            unscheduleDrawable(mDrawable);
        }
        mDrawable = d;
        if (d != null) {
            d.setCallback(this);
            
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }

            mDrawableWidth = d.getIntrinsicWidth();
            mDrawableHeight = d.getIntrinsicHeight();
            configureBounds();
        }
    }

    private void resizeFromDrawable() {
        Drawable d = mDrawable;
        if (d != null) {
            int w = d.getIntrinsicWidth();
            if (w < 0) w = mDrawableWidth;
            int h = d.getIntrinsicHeight();
            if (h < 0) h = mDrawableHeight;
            if (w != mDrawableWidth || h != mDrawableHeight) {
                mDrawableWidth = w;
                mDrawableHeight = h;
                requestLayout();
            }
        }
    }

    /**
     * <p>
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by measure(int, int) and
     * should be overriden by subclasses to provide accurate and efficient
     * measurement of their contents.
     * </p>
     *
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent.
     *                         The requirements are encoded with
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                         The requirements are encoded with
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w;
        int h;
        
        if (mDrawable == null) {
            mDrawableWidth = -1;
            mDrawableHeight = -1;
            w = h = 0;
        } else {
            w = mDrawableWidth;
            h = mDrawableHeight;
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;
        }
        
        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        int widthSize;
        int heightSize;

        w += pleft + pright;
        h += ptop + pbottom;
            
        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        widthSize = resolveSize(w, widthMeasureSpec);
        heightSize = resolveSize(h, heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * <p>This is called during layout when the size of this ThrumbnailView has changed.
     * If you were just added to the view hierarchy, you're called with the old values of 0.
     * In this method image will be reload.
     *
     * @param w Current width of this ThrumbnailView.
     * @param h Current height of this ThrumbnailView.
     * @param oldw Old width of this ThrumbnailView
     * @param oldh Old height of this ThrumbnailView
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        loadImage();
    }

    /**
     * <p>
     * This method configure bounds and set the matrix for draw
     * </p>
     */
    private void configureBounds() {
        if (mDrawable == null) {
            return;
        }

        int dwidth = mDrawableWidth;
        int dheight = mDrawableHeight;

        int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vheight = getHeight() - getPaddingTop() - getPaddingBottom();

        mDrawable.setBounds(0, 0, dwidth, dheight);
        
        mDrawMatrix = mMatrix;
        
        float scale;
        float dx;
        float dy;
        
        scale = Math.min((float) vwidth / (float) dwidth, 
                (float) vheight / (float) dheight);
        
        dx = (vwidth - dwidth * scale) * 0.5f;
        dy = (vheight - dheight * scale) * 0.5f;

        mDrawMatrix.setScale(scale, scale);
        mDrawMatrix.postTranslate(dx, dy);
    }

    @Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawable == null) {
            return;
        }

        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            int saveCount = canvas.getSaveCount();
            canvas.save();
            
            canvas.translate(getPaddingLeft(), getPaddingTop());

            if (mDrawMatrix != null) {
                canvas.concat(mDrawMatrix);
            }
            mDrawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    private Uri mUri;
    private int mResource = 0;
    private Matrix mMatrix;

    private Drawable mDrawable = null;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private Matrix mDrawMatrix = null;
}
