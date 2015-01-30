package com.dr.yokohamarally.core;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtility {

    public static Typeface getTypefaceFromAssets( Context context, String path ) {
        return Typeface.createFromAsset(context.getAssets(), path);
    }

}
