package com.js.pocket.pws;

import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

	static SharedPreferences sharedPreferences;
	
	// Progress Dialog
	private ProgressDialog pDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        sharedPreferences = getSharedPreferences("com.js.pocket.pws_preferences", 0);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        refreshdata();      
        /*
         *Check is the app first run 
         */
        if (DefaultPreferences.isFirstRun() == true) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle(R.string.app_name);
            alertbox.setMessage(R.string.hello);
            alertbox.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                	Intent i = new Intent(getApplicationContext(), PrefsActivity.class);
        			startActivity(i);
        			return;
                }
            });
            alertbox.setIcon(R.drawable.icon);
            AlertDialog dialog = alertbox.show();
            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);
            dialog.show();
            DefaultPreferences.appRunned();
			}
        /*
         * Check is activity is truly created
         */ 
        /*
         * Check is activity is truly created
         */ 
       
        if(savedInstanceState == null){
     	   /**Can we download automatically?*/
           if (DefaultPreferences.DownloadEnabled() == true) {
         	   /**Can application download on startup?*/
        	   if (DefaultPreferences.AppDownload()== true){
             	   /**Can we use 3G?*/
        		  if (DefaultPreferences.onlyWifiDownload() == true){
        		        /**checking wifi state*/
        			  if (!isWifi) {}else{
                    	  new getXML().execute();
                      }
        		  }else{
            	new getXML().execute();
        		  }
        	   }
           }
     	 }
    }
/*Define refreshdata()*/
    public void refreshdata() {
    	
    	FileInputStream inputStream = null;

    	/** Create a new textview array to display the results */
    	TextView realtime[];
    	TextView units[];
    	TextView misc[];
    	ImageView image[];
    	Typeface face=Typeface.createFromAsset(getAssets(),
              "digital.ttf");
    	
    	try {
    		inputStream = openFileInput("realtime.xml");
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		Document doc = db.parse(new InputSource(inputStream));
    		doc.getDocumentElement().normalize();

    		NodeList nodeList = doc.getElementsByTagName("realtime");
    		realtime = new TextView[nodeList.getLength()];
    		units = new TextView[nodeList.getLength()];
    		image = new ImageView[nodeList.getLength()];
    //REALTIME DATA PARSE
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node node = nodeList.item(i);
    			realtime[i] = new TextView(this);
    			image[i] = new ImageView(this);
    			units[i] = new TextView(this);
    			Element fstElmnt = (Element) node;
    			NodeList websiteList = fstElmnt.getElementsByTagName("data");
    			Element websiteElement = (Element) websiteList.item(0);
    			websiteList = websiteElement.getChildNodes();
    			String lowercase = (((Node) websiteList.item(0)).getNodeValue()).toLowerCase();
    			int ressourceId = getResources().getIdentifier(websiteElement.getAttribute("realtime"),"id",this.getBaseContext().getPackageName());
    			int ressourceId2 = getResources().getIdentifier(websiteElement.getAttribute("misc"),"id",this.getBaseContext().getPackageName());
    			int ressourceId3 = getResources().getIdentifier(lowercase,"drawable",this.getBaseContext().getPackageName());
    			int ressourceId4 = getResources().getIdentifier(websiteElement.getAttribute("units"),"id",this.getBaseContext().getPackageName());
    			//Check is textview id created, if not then do nothing
    			if (ressourceId !=0) {
    			realtime[i] = (TextView)findViewById(ressourceId);
    			realtime[i].setText(((Node) websiteList.item(0)).getNodeValue());
    			realtime[i].setTypeface(face);
    			}
    			if (ressourceId2 !=0) {
        			if (ressourceId3 !=0) {
    					image[i] = (ImageView)findViewById(ressourceId2);
    					image[i].setImageResource(ressourceId3);
    					}
        			}
    			if (ressourceId4 !=0) {
        			units[i] = (TextView)findViewById(ressourceId4);
        			units[i].setText(((Node) websiteList.item(0)).getNodeValue());
        			units[i].setTypeface(face);
        			}
    		}

    		NodeList nodeList2 = doc.getElementsByTagName("misc");
    		/** Assign textview array lenght by arraylist size */
       		misc = new TextView[nodeList2.getLength()];
    		
   //MISC DATA PARSE
    		for (int i = 0; i < nodeList2.getLength(); i++) {
    			Node node2 = nodeList2.item(i);
    			misc[i] = new TextView(this);
    			Element fstElmnt = (Element) node2;
    			NodeList websiteList = fstElmnt.getElementsByTagName("misc");
    			Element websiteElement = (Element) websiteList.item(0);
    			websiteList = websiteElement.getChildNodes();
    			int ressourceId5 = getResources().getIdentifier(websiteElement.getAttribute("data"),"id",this.getBaseContext().getPackageName());
    			//Check is textview id created if not then do nothing
    			if (ressourceId5 !=0) {
    			misc[i] = (TextView)findViewById(ressourceId5);
    			misc[i].setText(((Node) websiteList.item(0)).getNodeValue());
    			//misc[i].setTypeface(face);
    			}
    		}

    	} catch (Exception e) {
    		System.out.println("XML Parsing Expection = " + e);
    	}
    }
 // Downloading XML in Background Thread
    class getXML extends AsyncTask<String, String, String> {
		/**Before starting background thread Show Progress Dialog*/
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(getString(R.string.xml_downloading));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		/**getting XML*/
		protected String doInBackground(String... args) {
			XMLDownload.downloaddata();
			return null;		
		}
		/**After completing background task Dismiss the progress dialog*/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			refreshdata();
			pDialog.dismiss();
		}
	}

    /*CREATE MENU ITEM*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pws_menu, menu);
        return true;
    }
	/*MENU*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*MENU ITEMS*/
		case R.id.about_id:			
			// prepare the alert box
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle(R.string.about);
            alertbox.setMessage(R.string.about_text);
            alertbox.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
            // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {}
            });
            alertbox.setIcon(R.drawable.icon);
            AlertDialog dialog = alertbox.show();
            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);
            dialog.show();
			return(true);
			
		case R.id.refresh_id:
			
			new getXML().execute();
			return(true);
			
		case R.id.preferences_id:
			
			Intent i = new Intent(getApplicationContext(), PrefsActivity.class);
			startActivity(i);
			return(true);
		
		default:
            return super.onOptionsItemSelected(item);
			}
	}

}
