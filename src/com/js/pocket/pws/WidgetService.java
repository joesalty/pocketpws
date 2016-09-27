package com.js.pocket.pws;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

public class WidgetService extends Service {
	public static final String UPDATE = "update";
	public static final String PLUS = "plus";
	
	@Override
			
	public void onStart(Intent intent, int startId) {
				int appWidgetId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID);
		int layoutId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_CUSTOM_INFO);
		RemoteViews remoteView = new RemoteViews(getApplicationContext()
				.getPackageName(), layoutId);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        SharedPreferences prefs = getSharedPreferences("com.js.pocket.pws_preferences", 0);
    	boolean downloadenabled = prefs.getBoolean("auto_download", true);
    	boolean widgetdownload = prefs.getBoolean("widget_auto_download", false);
    	boolean wifidownload = prefs.getBoolean("download_on_wifi", false);
        long current_time = new Date().getTime();
		long download_time = prefs.getLong("download_time", 0);
		String refreshtime = prefs.getString("refresh_time", "900000");
//		String theme_color =prefs.getString("theme_color", "#0099cc");
		long refresh_time = Long.parseLong(refreshtime);
		long dt = refresh_time+download_time;
		boolean downloading_now = prefs.getBoolean("downloading_now", false);
//download xml from server
	if (downloading_now != true){
		if (dt >= current_time) {
			//Log.i("WidgetService", "> current time");
		}
		else{
			if (downloadenabled == true) {
				//Log.i("WidgetService", "Download = true");
	         	   /**Can application download on startup?*/
	        	   if (widgetdownload== true){
	        		   //Log.i("WidgetService", "Widget Download = true");
	             	   /**Can we use 3G?*/
	        		  if (wifidownload == true){
	        			  //Log.i("WidgetService", "WIFI download = true");
	        		        /**checking wifi state*/
	        			  if (!isWifi) {}else{
	                    	  new getData().execute();
	                      }
	        		  }else{
	            	new getData().execute();
	        		  }
	        	   }
	           }
		} 
		
	}
		//sending data to activity
		Intent notificationIntent = new Intent(WidgetService.this, MainActivity.class);
		notificationIntent.putExtra("appWidgetId", intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID));
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), appWidgetId, notificationIntent, 0);
		
		//DATA UPDATE START
		FileInputStream inputStream = null;
		
		try {
			inputStream = openFileInput("realtime.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(inputStream));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("realtime");
			
			for (int i = 0; i < nodeList.getLength(); i++) {

				Node node = nodeList.item(i);
				Element fstElmnt = (Element) node;
				NodeList websiteList = fstElmnt.getElementsByTagName("data");
				Element websiteElement = (Element) websiteList.item(0);
				websiteList = websiteElement.getChildNodes();
				int ressourceId = getResources().getIdentifier("w"+websiteElement.getAttribute("realtime"),"id",this.getBaseContext().getPackageName());
				int ressourceId2 = getResources().getIdentifier("w"+websiteElement.getAttribute("units"),"id",this.getBaseContext().getPackageName());
//				int ressourceId3 = getResources().getIdentifier("rw"+websiteElement.getAttribute("realtime"),"id",this.getBaseContext().getPackageName());
//				int ressourceId4 = getResources().getIdentifier("rw"+websiteElement.getAttribute("units"),"id",this.getBaseContext().getPackageName());
				//Check is textview id true (if not then do nothing), and put the text into the textView
				if (ressourceId !=0) {
					remoteView.setTextViewText(ressourceId, ((websiteList.item(0)).getNodeValue()));
				}
				if (ressourceId2 !=0) {
					remoteView.setTextViewText(ressourceId2, ((websiteList.item(0)).getNodeValue()));
				}
//				if (ressourceId3 !=0) {
//					remoteView.setTextViewText(ressourceId3, ((websiteList.item(0)).getNodeValue()));
//				}
//				if (ressourceId4 !=0) {
//					remoteView.setTextViewText(ressourceId4, ((websiteList.item(0)).getNodeValue()));
//				}
			}

			NodeList nodeList2 = doc.getElementsByTagName("misc");
			
			for (int i = 0; i < nodeList2.getLength(); i++) {

				Node node2 = nodeList2.item(i);
				Element fstElmnt = (Element) node2;
				NodeList websiteList2 = fstElmnt.getElementsByTagName("misc");
				Element websiteElement = (Element) websiteList2.item(0);
				websiteList2 = websiteElement.getChildNodes();
				int ressourceId3 = getResources().getIdentifier("w"+websiteElement.getAttribute("data"),"id",this.getBaseContext().getPackageName());
//				int ressourceId4 = getResources().getIdentifier("rw"+websiteElement.getAttribute("data"),"id",this.getBaseContext().getPackageName());
				//Check is textview id true (if not then do nothing), and put the text into the textView
				if (ressourceId3 !=0) {
					remoteView.setTextViewText(ressourceId3, ((websiteList2.item(0)).getNodeValue()));
				}
//				if (ressourceId4 !=0) {
//					remoteView.setTextViewText(ressourceId4, ((websiteList2.item(0)).getNodeValue()));
//				}
			}
		} catch (Exception e) {
			//System.out.println("XML Pasing Excpetion = " + e);
		}
//}
//DATA UPDATE END
		// apply changes to widget
		//Log.i("WidgetService", "Refreshing data");
				remoteView.setOnClickPendingIntent(R.id.weatherwidget, contentIntent);
//				remoteView.setOnClickPendingIntent(R.id.weatherwidget2, contentIntent);
//				remoteView.setInt(R.id.weatherwidget2, "setBackgroundColor", 
//				        android.graphics.Color.parseColor(theme_color));
		appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    class getData extends AsyncTask<String, String, String> {
		/**Before starting background thread change the downloading value*/
		@Override
		protected void onPreExecute() {
			SharedPreferences prefs = getSharedPreferences("com.js.pocket.pws_preferences", 0);
			SharedPreferences.Editor edit=prefs.edit();
			edit.putBoolean("downloading_now",true);
			edit.commit();
		}
		/**getting XML*/
		protected String doInBackground(String... args) {
			SharedPreferences prefs = getSharedPreferences("com.js.pocket.pws_preferences", 0);
			String xmlurl = prefs.getString("url", "http://android.teszdesign.hu/wview.xml");
			try {
		    	URL url = new URL(xmlurl);
		        URLConnection conexion = url.openConnection();
		        conexion.setReadTimeout(15000);//milliseconds
		        conexion.setConnectTimeout(3500);
		        conexion.connect();
		        int lenghtOfFile = conexion.getContentLength();
		        InputStream is = url.openStream();
		 // TODO rename hardcoded file place!!!
		        FileOutputStream fos = new FileOutputStream("/data/data/com.js.pocket.pws/files/realtime.xml");
		        byte data[] = new byte[1024];
		        int count = 0;
		        long total = 0;
		        int progress = 0;
		        while ((count = is.read(data)) != -1) {
		            total += count;
		            int progress_temp = (int) total * 100 / lenghtOfFile;
		            if (progress_temp % 10 == 0 && progress != progress_temp) {
		                progress = progress_temp;
		            }
		            fos.write(data, 0, count);
		        }
		        is.close();
		        fos.close();
		        //Log.i("WidgetService","XML download completed");
		        
		        SharedPreferences.Editor edit=prefs.edit();
		        long current_time = new Date().getTime();
		        edit.putLong("download_time",current_time);
		        edit.commit();
		        
		    } catch (MalformedURLException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    } catch (FileNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }	    
			return null;		
		}
		/**After completing background change back the downloading value*/
		protected void onPostExecute(String file_url) {
			SharedPreferences prefs = getSharedPreferences("com.js.pocket.pws_preferences", 0);
			SharedPreferences.Editor edit=prefs.edit();
			edit.putBoolean("downloading_now",false);
			edit.commit();
		}
	}
} 