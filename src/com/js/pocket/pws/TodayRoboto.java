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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TodayRoboto extends Activity implements OnGestureListener {
	
	private static final float SWIPE_THRESHOLD = 100;
	private static final float SWIPE_VELOCITY_THRESHOLD = 100;
	static SharedPreferences sharedPreferences;
	private ProgressDialog pDialog;
	private GestureDetector gestureScanner;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		gestureScanner = new GestureDetector(this);
		sharedPreferences = getSharedPreferences("com.js.pocket.pws_preferences", 0);
        String theme_color = DefaultPreferences.themeColor();
        	setContentView(R.layout.roboto_today_layout);
        	getWindow().getDecorView().setBackgroundColor(Color.parseColor(theme_color));
        	refreshdata_roboto();
	}
	@Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //Set the layout ad refresh data from XML
        String theme_color = DefaultPreferences.themeColor();
        	setContentView(R.layout.roboto_today_layout);
        	getWindow().getDecorView().setBackgroundColor(Color.parseColor(theme_color));
        	refreshdata_roboto();               
    }
	/**
	*   GESTURE DETECTOR
	*/      
	    //@Override
	    public boolean onTouchEvent(MotionEvent me) {
	        return gestureScanner.onTouchEvent(me);
	    }

		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
					return false;		
		}
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

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
	 *Downloading XML in Background Thread
	 */
	    class getXML extends AsyncTask<String, String, String> {
			/**Before starting background thread Show Progress Dialog*/
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(TodayRoboto.this);
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
		        	refreshdata_roboto(); 
				pDialog.dismiss();
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
	    	//TextView custom[];
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
	        			      			
	        			int ressourceId6 = getResources().getIdentifier("rt" + websiteElement.getAttribute("realtime"),"id",this.getBaseContext().getPackageName());
	        			int ressourceId9 = getResources().getIdentifier("rt" + websiteElement.getAttribute("units"),"id",this.getBaseContext().getPackageName());
	        			int ressourceId3 = getResources().getIdentifier("rt" + websiteElement.getAttribute("units") + "2","id",this.getBaseContext().getPackageName());
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
	        			if (ressourceId3 !=0) {
                			units[i] = (TextView)findViewById(ressourceId3);
                			units[i].setText(((Node) websiteList.item(0)).getNodeValue());
                			units[i].setTypeface(roboto);
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
	        			
	        			int ressourceId11 = getResources().getIdentifier("rt" + websiteElement.getAttribute("data"),"id",this.getBaseContext().getPackageName());
	        			//Check is textview id created if not then do nothing

	        			if (ressourceId11 !=0) {
	            			misc[i] = (TextView)findViewById(ressourceId11);
	            			misc[i].setText(((Node) websiteList.item(0)).getNodeValue());
	            			misc[i].setTypeface(roboto);
	            			}
	        		}
	        	} 
	    		catch (Exception e) {
	        		System.out.println("XML Parsing Expection = " + e);
	        	}
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
}
