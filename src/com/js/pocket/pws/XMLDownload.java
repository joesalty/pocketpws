package com.js.pocket.pws;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class XMLDownload{
	public XMLDownload() {
	}
	
	public static void downloaddata(){
	    try {
	    	URL url = new URL(DefaultPreferences.whatisurl());
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
	        Log.i("MainActivity","XML download completed");
	        DefaultPreferences.DownloadTime();
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
	}
}

