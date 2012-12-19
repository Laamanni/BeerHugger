package com.example.beerhugger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.beerhugger.MESSAGE";
	
	private static final String DEBUG_TAG = "HttpExample";
    private EditText urlText;
    private TextView textView;
    private String baseUrl = "http://m.olutnorsu.fi/data.php?haku=";
    
    private String location = "60.16985, 24.93855";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlText = (EditText) findViewById(R.id.myUrl);
        textView = (TextView) findViewById(R.id.myText);
        
        // GPS
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		
    }

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    public void myClickHandler(View view) {

    	// Inform user that somethings happening
    	textView.setText("Retrieving data..");
    	
        // Gets the URL from the UI's text field.
    	String stringUrl = urlText.getText().toString();
    	
    	if ( stringUrl.length() == 0 ) {
    		stringUrl = location;
    	}
    	
    	URI uri = null;
    	URL url = null;
		try {
			uri = new URI(baseUrl, stringUrl, "");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			url = uri.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Convert url to string.
        stringUrl = url.toString();
        
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageText().execute(stringUrl);
        } else {
            textView.setText("No network connection available.");
        }
    }

     // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
     // URL string and uses it to create an HttpUrlConnection. Once the connection
     // has been established, the AsyncTask downloads the contents of the webpage as
     // an InputStream. Finally, the InputStream is converted into a string, which is
     // displayed in the UI by the AsyncTask's onPostExecute method.
     private class DownloadWebpageText extends AsyncTask<String, String, String> {
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(String result) {
            textView.setText(Html.fromHtml(result));
       }

    }
     
     // Given a URL, establishes an HttpUrlConnection and retrieves
  	 // the web page content as a InputStream, which it returns as
  	 // a string.
  	 private String downloadUrl(String myurl) throws IOException {
  	     InputStream is = null;
  	     // Only display the first 5000 characters of the retrieved
  	     // web page content.
  	     int len = 5000;
  	         
  	     try {
  	         URL url = new URL(myurl);
  	         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
  	         conn.setReadTimeout(10000 /* milliseconds */);
  	         conn.setConnectTimeout(15000 /* milliseconds */);
  	         conn.setRequestMethod("GET");
  	         conn.setDoInput(true);
  	         // Starts the query
  	         conn.connect();
  	         int response = conn.getResponseCode();
  	         Log.d(DEBUG_TAG, "The response is: " + response);
  	         is = conn.getInputStream();
  	
  	         // Convert the InputStream into a string
  	         String contentAsString = readIt(is, len);
  	         return contentAsString;
  	         
  	     // Makes sure that the InputStream is closed after the app is
  	     // finished using it.
  	     } finally {
  	         if (is != null) {
  	             is.close();
  	         } 
  	     }
  	 }
  	// Reads an InputStream and converts it to a String.
  	 public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
  	     Reader reader = null;
  	     reader = new InputStreamReader(stream, "ISO-8859-10");        
  	     char[] buffer = new char[len];
  	     reader.read(buffer);
  	     return new String(buffer);
  	 }
  	 
  	/* Class My Location Listener */

 	public class MyLocationListener implements LocationListener
 	{
 		public void onLocationChanged(Location loc)
 		{
 			//loc.getLatitude();
 			//loc.getLongitude();
 			//String Text = "My current location is: " + "Latitude = " + loc.getLatitude() + "Longitude = " + loc.getLongitude();

 			location = loc.getLatitude() + ", " + loc.getLongitude();
 			
 			// Toast.makeText( getApplicationContext(),
 			//		Text,
 			//		Toast.LENGTH_SHORT).show();
 		}

 		public void onProviderDisabled(String provider)
 		{
 			textView.setText("Gps Disabled");
 			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
 		}

 		public void onProviderEnabled(String provider)
 		{
 			textView.setText("Gps Enabled");
 			Toast.makeText( getApplicationContext(), "Gps Enabled",	Toast.LENGTH_SHORT).show();
 			
 		}


 		public void onStatusChanged(String provider, int status, Bundle extras)
 		{
 			
 		}

 	}/* End of Class MyLocationListener */
    
}
