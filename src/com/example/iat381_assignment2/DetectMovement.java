package com.example.iat381_assignment2;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class that handles detecting if the user is moving or stationary.
 * We identify the user as moving if the device is moving using the 
 * linear accoloremeter.
 * 
 * @author Kristofer Castro
 *
 */
public class DetectMovement extends Activity implements OnClickListener, SensorEventListener {

	Button updateStatusButton;
	TextView statusTextView;
	SensorManager sensorManager;
	SensorEvent currentSensorEvent;
	Sensor acc;
	boolean isUserMoving;
	private final float MOVEMENT_THRESHOLD = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detect_movement);
		
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		acc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
				
		updateStatusButton = (Button) this.findViewById(R.id.detectButton);
		statusTextView = (TextView) this.findViewById(R.id.movementStatusTextView);
		
		updateStatusButton.setOnClickListener(this);
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detect_movement, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.detectButton ){
			if (isUserMoving)
				statusTextView.setText("Status: User is moving!");
			else
				statusTextView.setText("Status: User is stationary...");
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause(){
		sensorManager.unregisterListener(this);
		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] newAccelerationValues = event.values;
		currentSensorEvent = event;
		
		if ( accelerationChanged(newAccelerationValues) ){
			isUserMoving = true;
		}else{
			isUserMoving = false;
		}
	}
	
	/**
	 * Compares the length of the linear acceleration vector
	 * to the movement threshold.  If it goes over the threshold
	 * then we say movement has occurered.
	 * @param newAccValues 
	 * @return
	 */
	private boolean accelerationChanged(float[] newAccValues){
		
		// converts to int, only look at integer changes
			
		float newX = newAccValues[0];
		float newY = newAccValues[1];
		float newZ = newAccValues[2];
		
		float newLength = FloatMath.sqrt(newX*newX + newY*newY + newZ*newZ);
		
		//Log.i(MainActivity.DEBUG_TAG, "old length: " + oldLength +" | new Length: " + newLength);
		Log.i(MainActivity.DEBUG_TAG, "length: " + Math.abs(newLength));

		if ( Math.abs(newLength) >= MOVEMENT_THRESHOLD )
			return true;
		return false;

	}

}
