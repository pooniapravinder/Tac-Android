package com.wookes.tac.util;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.widget.TextView;

public class TextShader {
    public static void shader(TextView textView){
        TextPaint paint = textView.getPaint();
        float width = paint.measureText("Tianjin, China");

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#F97C3C"),
                        Color.parseColor("#FDB54E"),
                        Color.parseColor("#64B678"),
                        Color.parseColor("#478AEA"),
                        Color.parseColor("#8446CC"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
    }
}
