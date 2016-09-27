package com.js.pocket.pws;

import java.util.Date;

import android.content.SharedPreferences;

public class DefaultPreferences {

	
    protected static boolean isFirstRun() {
                //default value to return if this preference does not exist is <em>true</em>
        return MainActivity.sharedPreferences.getBoolean("isFirstRun", true);
        
    }
    
    protected static void appRunned() {
        SharedPreferences.Editor edit = MainActivity.sharedPreferences.edit();
        //value of "isFirstRun" changed to false
        edit.putBoolean("isFirstRun", false);
        edit.commit();
    }
    
    protected static void DownloadTime() {
        SharedPreferences.Editor edit = MainActivity.sharedPreferences.edit();
        long current_time = new Date().getTime();
        //value of "isFirstRun" changed to false
        edit.putLong("download_time",current_time);
        edit.commit();
    }
    
    
    protected static boolean DownloadEnabled() {
        //default value to return if this preference does not exist is <em>true</em>
    	return MainActivity.sharedPreferences.getBoolean("auto_download", true);

    }
    
    protected static boolean WidgetDownload() {
        //default value to return if this preference does not exist is <em>true</em>
    	return MainActivity.sharedPreferences.getBoolean("widget_auto_download", false);

    }
    protected static boolean AppDownload() {
        //default value to return if this preference does not exist is <em>false</em>
    	return MainActivity.sharedPreferences.getBoolean("app_auto_download", false);

    }
    protected static boolean onlyWifiDownload() {
        //default value to return if this preference does not exist is <em>false</em>
    	return MainActivity.sharedPreferences.getBoolean("download_on_wifi", false);

    }
    
    protected static String whatisurl() {
        //default value to return if this preference does not exist is <em>true</em>
    	return MainActivity.sharedPreferences.getString("url", "http://joesalty77.no-ip.org/wview.xml");

    }
    
    protected static String refreshTime() {
        //default value to return if this preference does not exist is <em>true</em>
    	return MainActivity.sharedPreferences.getString("refresh_time", "900000");

    }    
    
}
