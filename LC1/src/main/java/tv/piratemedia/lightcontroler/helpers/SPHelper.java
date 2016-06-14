/*
 * Copyright (c) 2014. David Alejandro Fernández Sancho.
 * All rights reserved.
 */

package tv.piratemedia.lightcontroler.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SPHelper {

    public static void putBrightness(Context context, int zone, int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putInt("brightness_" + zone, value);
        edit.apply();
    }

    public static int getBrightness(Context context, int zone){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("brightness_" + zone, 15);
    }

    public static void putColor(Context context, int zone, int color){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putInt("color_" + zone, color);
        edit.apply();
    }

    public static int getColor(Context context, int zone){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("color_" + zone, 0);
    }

    public static void putOnState(Context context, int zone, boolean val){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putBoolean("onoff_" + zone, val);
        edit.apply();
    }

    public static boolean getOnState(Context context, int zone){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("onoff_" + zone, false);
    }
}
