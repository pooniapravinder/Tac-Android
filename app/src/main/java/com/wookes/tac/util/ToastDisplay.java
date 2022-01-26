package com.wookes.tac.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastDisplay {

    public static void a(Context var0, int var1, int var2) {
        Toast toast = Toast.makeText(var0, var1, var2 == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void a(Context var0, String var1, int var2) {
        Toast toast = Toast.makeText(var0, var1, var2 == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
