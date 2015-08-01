package com.d2112.weather.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class IconManager {
    private static final String ICON_IMAGE_PREFIX = "icon";
    private Context context;

    public IconManager(Context context) {
        this.context = context;
    }

    public Drawable getIconByForecastCode(String forecastCode) {
        Resources resources = context.getResources();
        String imageName = ICON_IMAGE_PREFIX + forecastCode;
        final int resourceId = resources.getIdentifier(imageName, "drawable", context.getPackageName());
        return resources.getDrawable(resourceId);
    }
}
