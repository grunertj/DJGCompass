package com.jwg.grunert.djgcompass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Werner-Jens Grunert on 3/10/2016.
 */
public class SquareScaleImageView extends ImageView {

    public SquareScaleImageView(Context context) {
        super(context);
    }

    public SquareScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth()/2;
        int measuredHeight = getMeasuredHeight()/2;

        if (measuredWidth > measuredHeight) {
            setMeasuredDimension(measuredHeight, measuredHeight);
        } else {
            setMeasuredDimension(measuredWidth, measuredWidth);
        }
    }
}