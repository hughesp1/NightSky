/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;


import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class OpenGLES20Activity extends Activity {
	
	GPSTracker gps;
	
	int lastToast = 999;
    private GLSurfaceView mGLView;
    private SensorManager mSM;
    private mSensorEventListener mSEL;
       float[] inR = new float[16];
       float[] outR= new float[16];
       float[] I = new float[16];
       float[] gravity = new float[3];
       float[] geomag = new float[3];
       float[] orientVals = new float[3];
       
       float[] currentPos = new float[3];
       
       

       public void disableScreenTurnOff() {
   		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
   				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
   	}
       
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableScreenTurnOff();
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        
        mSM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSEL = new mSensorEventListener(); 
        
        
        mSM.registerListener(mSEL, mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSM.registerListener(mSEL, mSM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        //parse();
        
    }
    

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        mSM.unregisterListener(mSEL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
       
        mSM.registerListener(mSEL, mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
       	mSM.registerListener(mSEL, mSM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
       	
    }
    
    private class mSensorEventListener implements SensorEventListener{
    	
    	
   	  	@Override
    	public void onAccuracyChanged(Sensor arg0, int arg1) {}

    	@Override
    	public void onSensorChanged(SensorEvent event) {

    	// If the sensor data is unreliable return
    	//if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
    	//return;

    	// Gets the value of the sensor that has been changed
    	switch (event.sensor.getType()){  
    	   	case Sensor.TYPE_ACCELEROMETER:
    	   		gravity = event.values.clone();
    	   	break;
    	   	case Sensor.TYPE_MAGNETIC_FIELD:
    	   		geomag = event.values.clone();
    	   	break;
    	}
    	//double factor = 0.2;
    	//double azimuth = 0;
    	//azimuth = (1 - factor) * azimuth + factor;
    	// If gravity and geomag have values then find rotation matrix
    	if (gravity != null && geomag != null){
    		   
       	 	// checks that the rotation matrix is found
       	 	boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
       	 	if (success){
       	 		// Re-map coordinates so y-axis comes out of camera
       	 		SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, 
       	 		SensorManager.AXIS_Z, outR);
       	    		
       	 		// Finds the Azimuth and Pitch angles of the y-axis with 
       	 		// magnetic north and the horizon respectively
       	 		SensorManager.getOrientation(outR, orientVals);//compass, pitch, roll
       	 		MyGLRenderer.setRotateView((float)Math.toDegrees(orientVals[1]), (float)Math.toDegrees(orientVals[0]), (float)Math.toDegrees(orientVals[2]) );
       	 		//setToast();
       	 		testToast();
       	 		//printPH();
       	 	}
    	}	   
    }

    }
    int delay = 0;
    public void testToast(){
    	if(delay < 0){
    		checkRange();
    		delay = 20;
    		Log.d("TOAST", "HERE");
    	}else{
    		delay--;
    	}
    }
    
    void checkRange(){//check if constellation is within range and display a toast if it is
    	float Xnow = -MyGLRenderer.getX();// + MyGLRenderer.decOffset;//Math.abs(MyGLRenderer.getX());
		float Ynow = 180 - MyGLRenderer.getY();// + MyGLRenderer.raOffset;//Math.abs(MyGLRenderer.getY());
		Log.d("NOW", "X= " + Float.toString(Xnow) + "   Y= " + Float.toString(Ynow));
		for(int i=0; i<18; i++){
			float ra = MyGLRenderer.raAvg[i];// Math.abs(MyGLRenderer.raAvg[i]);
			float dec = MyGLRenderer.decAvg[i];//Math.abs(MyGLRenderer.decAvg[i]);
			if(Math.abs(Xnow-dec)<10 && Math.abs(Ynow-ra)<10){//in range
				ShowToast(i);
			}
		}
		if(Math.abs(Xnow)<10 && Math.abs(Ynow)<10){//in range
			toastGPS();
		}
    }
    
    void printPH(){//check if constellation is within range and display a toast if it is
    	float Xnow = MyGLRenderer.getX();//Math.abs(MyGLRenderer.getX());
		float Ynow = MyGLRenderer.getY()+180;//Math.abs(MyGLRenderer.getY());
		Log.d("NOW", "X= " + Float.toString(Xnow) + "   Y= " + Float.toString(Ynow));
    }
    boolean toasted = true;
/*    public void setToast(){
    	float Xnow = Math.abs(MyGLRenderer.getX());
		float Ynow = Math.abs(MyGLRenderer.getY());
		float ra = Math.abs(MyGLRenderer.raAvg[lastToast]);
		float dec = Math.abs(MyGLRenderer.decAvg[lastToast]);
    	if(!(Math.abs(Xnow-dec)>20 || Math.abs(Ynow-ra)>20)){//out of range
			lastToast = 999;
			Log.d("TOAST2", "t");
		}
    }*/
    /*
    public void setToast(){
    	Log.d("TOASTA", "toasted=" + Boolean.toString(toasted) + "lastToast=" + Integer.toString(lastToast));
    	if(!toasted){
    		toasted = true;
    		AToast(lastToast);
    		Log.d("TOAST1", "index = " + Integer.toString(lastToast));
    		//lastToast = 999;
    	}
    	if(lastToast!=999){
    		float Xnow = Math.abs(MyGLRenderer.getX());
    		float Ynow = Math.abs(MyGLRenderer.getY());
    		float ra = Math.abs(MyGLRenderer.raAvg[lastToast]);
			float dec = Math.abs(MyGLRenderer.decAvg[lastToast]);
    		if(!(Math.abs(Xnow-dec)>20 || Math.abs(Ynow-ra)>20)){//out of range
    			lastToast = 999;
    			Log.d("TOAST2", "t");
    		}
    	}
    	if(lastToast == 999){
    		float Xnow = Math.abs(MyGLRenderer.getX());
    		float Ynow = Math.abs(MyGLRenderer.getY());
    		for(int i=0; i<18; i++){
    			float ra = Math.abs(MyGLRenderer.raAvg[i]);
    			float dec = Math.abs(MyGLRenderer.decAvg[i]);
    			if(Math.abs(Xnow-dec)<10 && Math.abs(Ynow-ra)<10){//in range
    				
    				lastToast = i;
    				Log.d("TOAST3", "toast=" + Integer.toString(lastToast));
    				toasted = false;
    			}
    		}
    	}
    	Log.d("TOASTB", "toasted=" + Boolean.toString(toasted) + "lastToast=" + Integer.toString(lastToast));
    }
    */
    String[] names = {
    		"Canis Major", "Carina", "Boštes", "Centaurus", "Lyra", "Auriga", "Orion", "Canis Minor", "Eridanus", "Aquila",
    		 "Taurus", "Scorpius", "Virgo", "Gemini", "Piscis Austrinus", "Crux", "Cygnus", "Leo", "Grus"};
   
    public void ShowToast(int index){
    	String message = "yolo";
    	
    	message = names[index];
    	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();	
    }
    
    void toastGPS(){
    	gps = new GPSTracker(OpenGLES20Activity.this);
   	 
        // check if GPS enabled     
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
}
