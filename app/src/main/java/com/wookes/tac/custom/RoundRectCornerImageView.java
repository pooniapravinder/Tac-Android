package com.wookes.tac.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.wookes.tac.R;

public class RoundRectCornerImageView extends AppCompatImageView {

    private float cornerRadius;
    private Path path;
    private RectF rect;

    public RoundRectCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init() {
        path = new Path();
        rect = new RectF();
    }

    private void init(AttributeSet attrs) {
        path = new Path();
        rect = new RectF();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundRectCornerImageView);
        cornerRadius = typedArray.getFloat(R.styleable.RoundRectCornerImageView_corner_radius, 0);
        typedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        rect.set(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}