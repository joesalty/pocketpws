package com.js.pocket.pws;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;


public class Widget3 extends AppWidgetProvider {
	//update rate in milliseconds
	public final int UPDATE_RATE = 3000;
	//public final int layout = R.layout.widget_layout_21;
	@Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {       
            setAlarm(context, appWidgetId, -1);
        }
        super.onDeleted(context, appWidgetIds);
    }	
	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context,WidgetService2.class));
		super.onDisabled(context);
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			setAlarm(context, appWidgetId, UPDATE_RATE);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	public static void setAlarm(Context context, int appWidgetId, int updateRate) {
        PendingIntent newPending = makeControlPendingIntent(context,WidgetService2.UPDATE,appWidgetId);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (updateRate >= 0) {
            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRate, newPending);
        } else {
        	// on a negative updateRate stop the refreshing 
            alarms.cancel(newPending);
        }
    }
	
	public static PendingIntent makeControlPendingIntent(Context context, String command, int appWidgetId) {
        Intent active = new Intent(context,WidgetService2.class);
        active.setAction(command);
        active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        active.putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, R.layout.widget_layout_21);
        //this Uri data is to make the PendingIntent unique, so it wont be updated by FLAG_UPDATE_CURRENT
        //so if there are multiple widget instances they wont override each other
        Uri data = Uri.withAppendedPath(Uri.parse("weatherwidget://widget/id/#"+command+appWidgetId), String.valueOf(appWidgetId));
        active.setData(data);
        return(PendingIntent.getService(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}