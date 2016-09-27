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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnGestureListener {

	private static final float SWIPE_THRESHOLD = 100;
	private static final float SWIPE_VELOCITY_THRESHOLD = 100;

	static SharedPreferences sharedPreferences;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	private GestureDetector gestureScanner; //for gesture scanner
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureScanner = new GestureDetector(this); //for gesture scanner
        sharedPreferences = getSharedPreferences("com.js.pocket.pws_preferences", 0);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        /**        
         *Check theme settings start
         */
        String theme = DefaultPreferences.themeSettings();
        String theme_color = DefaultPreferences.themeColor();
        long t = Long.parseLong(theme);
        if (t == 1) {
        	setContentView(R.layout.roboto_main_layout);
        	getWindow().getDecorView().setBackgroundColor(Color.parseColor(theme_color));
        	 refreshdata_roboto(); 
        }else{
        	setContentView(R.layout.dashboard_layout);
        	refreshdata_digital(); 
        }            
        /**
         *Check is the app first run 
         */
        if (DefaultPreferences.isFirstRun() == true) {
        	firstrun_dialog();
            DefaultPreferences.appRunned();
            DefaultPreferences.putVersion();
			}
        /**
         *What's new in app 
         */
        String longversion = DefaultPreferences.getVersion();
        long l = Long.parseLong(longversion);
        if (l < 4) {
        	whatsnew_dialog();
            DefaultPreferences.putVersion();
			return;
        }
        /**
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
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //Set the layout ad refresh data from XML
        String theme = DefaultPreferences.themeSettings();
        String theme_color = DefaultPreferences.themeColor();
        long t = Long.parseLong(theme);
        if (t == 1) {
        	setContentView(R.layout.roboto_main_layout);
        	getWindow().getDecorView().setBackgroundColor(Color.parseColor(theme_color));
        	refreshdata_roboto();
        }else{
        	setContentView(R.layout.dashboard_layout);
        	refreshdata_digital();
        }               
    }
/**
*   GESTURE DETECTOR
*/      
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
				return false;		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }


public void onSwipeRight() {
	finish();
	}

public void onSwipeLeft() {
	String theme = DefaultPreferences.themeSettings();
	long t = Long.parseLong(theme);
		if (t == 1) {
			Intent i = new Intent(getApplicationContext(), TodayRoboto.class);
			startActivity(i);
		}else{
			//Intent i = new Intent(getApplicationContext(), TodayDigital.class);
			//startActivity(i);
		}

	}

public void onSwipeTop() {
	Intent i = new Intent(getApplicationContext(), PrefsActivity.class);
	startActivity(i);
	}

public void onSwipeBottom() {
	about_dialog();
	}

public void onLongPress(MotionEvent e) {
	new getXML().execute();
	}   
/**
 *Define digital refreshdata
 */
public void refreshdata_digital() {
    	FileInputStream inputStream = null;

    	/** Create a new textview array to display the results */
    	TextView realtime[];
    	TextView units[];
    	TextView misc[];
    	ImageView image[];
 
        Typeface digital=Typeface.createFromAsset(getAssets(),
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
    			int version = Integer.valueOf(android.os.Build.VERSION.SDK);
    			
    			int ressourceId = getResources().getIdentifier(websiteElement.getAttribute("realtime"),"id",this.getBaseContext().getPackageName());
    			int ressourceId2 = getResources().getIdentifier(websiteElement.getAttribute("misc"),"id",this.getBaseContext().getPackageName());
    			int ressourceId3 = getResources().getIdentifier(lowercase,"drawable",this.getBaseContext().getPackageName());
    			int ressourceId4 = getResources().getIdentifier(websiteElement.getAttribute("units"),"id",this.getBaseContext().getPackageName());
    			//Check is textview id created, if not then do nothing
    			if (ressourceId !=0) {
    			realtime[i] = (TextView)findViewById(ressourceId);
    			realtime[i].setText(((Node) websiteList.item(0)).getNodeValue());
    			realtime[i].setTypeface(digital);
    			}
    		if (version >= 11){ 
    			//over version 11
    			if (ressourceId2 !=0) {
    				image[i] = (ImageView)findViewById(R.id.imgwinddir);
					image[i].setImageResource(R.drawable.n);
    				if (lowercase.toString().equals("n")) {
    					image[i].setRotation(0);
    				} 
    				else if (lowercase.toString().equals("e")) {
    					image[i].setRotation(90);
    				}
    				else if (lowercase.toString().equals("s")) {
    					image[i].setRotation(180);
    				}
    				else if (lowercase.toString().equals("w")) {
    					image[i].setRotation(270);
    				}
    				else if (lowercase.toString().equals("ne")) {
    					image[i].setRotation(45);
    				}
    				else if (lowercase.toString().equals("se")) {
    					image[i].setRotation(135);
    				}
    				else if (lowercase.toString().equals("sw")) {
    					image[i].setRotation(225);
    				}
    				else if (lowercase.toString().equals("nw")) {
    					image[i].setRotation(315);
    				}
    				else if (lowercase.toString().equals("nne")) {
    					image[i].setRotation(23);
    				}
    				else if (lowercase.toString().equals("ene")) {
    					image[i].setRotation(68);
    				}
    				else if (lowercase.toString().equals("ese")) {
    					image[i].setRotation(113);
    				}
    				else if (lowercase.toString().equals("sse")) {
    					image[i].setRotation(158);
    				}
    				else if (lowercase.toString().equals("ssw")) {
    					image[i].setRotation(203);
    				}
    				else if (lowercase.toString().equals("wsw")) {
    					image[i].setRotation(248);
    				}
    				else if (lowercase.toString().equals("wnw")) {
    					image[i].setRotation(293);
    				}
    				else if (lowercase.toString().equals("nnw")) {
    					image[i].setRotation(338);
    				}
    				else {
    					String checknumber = ((Node) websiteList.item(0)).getNodeValue();
    					try {
    					   long degree = Long.parseLong(checknumber);
    					   image[i].setRotation(degree);
    					} catch (NumberFormatException e) {
    					   Log.w("",checknumber+"is not a number");
    						}
    					}
        		}
    		}else {
    			//below version 11
    			if (ressourceId2 !=0) {
    				if (ressourceId3 !=0) {
    					image[i] = (ImageView)findViewById(ressourceId2);
    					image[i].setImageResource(ressourceId3);
						}
    				}
    		}
    			
    			if (ressourceId4 !=0) {
        			units[i] = (TextView)findViewById(ressourceId4);
        			units[i].setText(((Node) websiteList.item(0)).getNodeValue());
        			units[i].setTypeface(digital);
        			}
    		}
    		//MISC DATA PARSE
    		
    		NodeList nodeList2 = doc.getElementsByTagName("misc");
    		/** Assign textview array lenght by arraylist size */
       		misc = new TextView[nodeList2.getLength()];
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
    			}
    		}

    	} catch (Exception e) {
    		System.out.println("XML Parsing Expection = " + e);
    	}
    }
/**
 *Define roboto refreshdata
 */
public void refreshdata_roboto() {
    	FileInputStream inputStream = null;

    	/** Create a new textview array to display the results */
    	TextView realtime[];
    	TextView units[];
    	TextView misc[];
    	ImageView image[];
        Typeface roboto=Typeface.createFromAsset(getAssets(),
                "roboto-condensed.ttf");
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
        			int ressourceId6 = getResources().getIdentifier("r" + websiteElement.getAttribute("realtime"),"id",this.getBaseContext().getPackageName());
        			int ressourceId9 = getResources().getIdentifier("r" + websiteElement.getAttribute("units"),"id",this.getBaseContext().getPackageName());
        			int ressourceId10 = getResources().getIdentifier("r" + websiteElement.getAttribute("customdata"),"id",this.getBaseContext().getPackageName());
        			int ressourceId7 = getResources().getIdentifier("r" + websiteElement.getAttribute("custompic"),"id",this.getBaseContext().getPackageName());
        			int ressourceId8 = getResources().getIdentifier("r" + lowercase,"drawable",this.getBaseContext().getPackageName());
        			
        			//Check is textview id created, if not then do nothing
        			if (ressourceId6 !=0) {
            			realtime[i] = (TextView)findViewById(ressourceId6);
            			realtime[i].setText(((Node) websiteList.item(0)).getNodeValue());
            			realtime[i].setTypeface(roboto);
            			}
        			if (ressourceId9 !=0) {
            			units[i] = (TextView)findViewById(ressourceId9);
            			units[i].setText(((Node) websiteList.item(0)).getNodeValue());
            			units[i].setTypeface(roboto);
            			}
        			
        			if (ressourceId10 !=0) {
                    	realtime[i] = (TextView)findViewById(ressourceId10);
                    	realtime[i].setText(((Node) websiteList.item(0)).getNodeValue());
                    	realtime[i].setTypeface(roboto);
                    			}
        			if (ressourceId7 !=0) {
                			if (ressourceId8 !=0) {
            					image[i] = (ImageView)findViewById(ressourceId7);
            					image[i].setImageResource(ressourceId8);
            				}
                	
                		}

            			
        			
        		}
        		//MISC DATA PARSE
        		
        		NodeList nodeList2 = doc.getElementsByTagName("misc");
        		/** Assign textview array lenght by arraylist size */
           		misc = new TextView[nodeList2.getLength()];
        		for (int i = 0; i < nodeList2.getLength(); i++) {
        			Node node2 = nodeList2.item(i);
        			misc[i] = new TextView(this);
        			Element fstElmnt = (Element) node2;
        			NodeList websiteList = fstElmnt.getElementsByTagName("misc");
        			Element websiteElement = (Element) websiteList.item(0);
        			websiteList = websiteElement.getChildNodes();
        			
        			int ressourceId10 = getResources().getIdentifier("r" + websiteElement.getAttribute("data"),"id",this.getBaseContext().getPackageName());
        			//Check is textview id created if not then do nothing

        			if (ressourceId10 !=0) {
            			misc[i] = (TextView)findViewById(ressourceId10);
            			misc[i].setText(((Node) websiteList.item(0)).getNodeValue());
            			misc[i].setTypeface(roboto);
            			}
        		}

        	} catch (Exception e) {
        		System.out.println("XML Parsing Expection = " + e);
        	}
}
/**
 *Whatsnew dialog
 */
public void whatsnew_dialog() {
	final Dialog dialog = new Dialog(this);

    dialog.setContentView(R.layout.whatsnew_layout);
    dialog.setTitle(R.string.app_name);
   
    Button dialogButton = (Button) dialog.findViewById(R.id.buttonOkWhatsnew);
    dialogButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.dismiss();
		}
	});
    dialog.show();
	}
/**
 *About dialog
 */
public void about_dialog() {
	final Dialog dialog = new Dialog(this);

    dialog.setContentView(R.layout.about_layout);
    dialog.setTitle(R.string.about);
   
    Button dialogButton = (Button) dialog.findViewById(R.id.buttonOkAbout);
    dialogButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.dismiss();
		}
	});
    dialog.show();
	}
/**
 *Firstrun dialog
 */
public void firstrun_dialog() {
	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    alertbox.setTitle(R.string.app_name);
    alertbox.setMessage(R.string.hello);
    alertbox.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
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
	}
/**
 *Downloading XML in Background Thread
 */
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
			DefaultPreferences.clearDownloading_now();
			String theme = DefaultPreferences.themeSettings();
	        long t = Long.parseLong(theme);
	        if (t == 1) {
	        	refreshdata_roboto();
	        }else{
	        	refreshdata_digital();
	        } 
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
			about_dialog();
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
