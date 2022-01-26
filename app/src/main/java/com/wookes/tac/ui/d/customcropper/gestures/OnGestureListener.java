package com.wookes.tac.ui.d.customcropper.gestures;

public interface OnGestureListener {

    void onDrag(float dx, float dy);

    void onFling(float startX, float startY, float velocityX, float velocityY);

    void onScale(float scaleFactor, float focusX, float focusY);

}