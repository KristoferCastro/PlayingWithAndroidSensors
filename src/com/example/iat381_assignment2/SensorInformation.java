package com.example.iat381_assignment2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class SensorInformation extends Activity implements SensorEventListener{

	SensorManager sensorManager;
	
	Sensor currentSensor;
	TextView sensorInformationTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_information);
		
		sensorInformationTextView = (TextView) this.findViewById(R.id.sensorInformationTextView);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		Bundle extras = getIntent().getExtras();
		if (extras == null) 
			return;
		initializeSensors(extras);

	}

	/**
	 * Initialize the sensor to the type by looking at the sensor type provided
	 * by the intent.
	 * 
	 * @param extras
	 */
	private void initializeSensors(Bundle extras) {
		int sensorType = extras.getInt("sensor type");	
		switch(sensorType){
			case Sensor.TYPE_ACCELEROMETER: {
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				break;
			}
			case Sensor.TYPE_AMBIENT_TEMPERATURE:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
				break;
			}
			case Sensor.TYPE_GAME_ROTATION_VECTOR:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
				break;
			}
			case Sensor.TYPE_GRAVITY:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
				break;
			}
			case Sensor.TYPE_GYROSCOPE:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
				break;
			}
			case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
				break;
			}
			case Sensor.TYPE_LIGHT:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
				break;
			}
			case Sensor.TYPE_LINEAR_ACCELERATION:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
				break;
			}
			case Sensor.TYPE_ORIENTATION:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
				break;
			}	
			case Sensor.TYPE_PRESSURE:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
				break;
			}
			case Sensor.TYPE_PROXIMITY:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
				break;
			}
			case Sensor.TYPE_RELATIVE_HUMIDITY:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
				break;
			}
			case Sensor.TYPE_ROTATION_VECTOR:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
				break;
			}
			case Sensor.TYPE_SIGNIFICANT_MOTION:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
				break;
			}
			case Sensor.TYPE_TEMPERATURE:{
				currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
				break;
			}
			default:{
				Toast.makeText(this.getApplicationContext(), "didn't find sensor" , Toast.LENGTH_SHORT).show();

			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_information, menu);
		return true;
	}

	@Override
	public void onResume(){
		Toast.makeText(this.getApplicationContext(), "onResume()" , Toast.LENGTH_SHORT).show();
		super.onResume();
		sensorManager.registerListener(this, currentSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause(){
		Toast.makeText(this.getApplicationContext(), "onPause()" , Toast.LENGTH_SHORT).show();

		sensorManager.unregisterListener(this);
		super.onPause();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {		
		displaySensorInformation(event);
		checkIfLightSensorCovered(event);
		checkIfFlatOnTable(event);
	}

	/**
	 * Check if the device is flat on the table by checking if their x and y value is 0 (rounded).
	 * Vibrate for 5000 seconds
	 * @param event
	 */
	@SuppressLint("NewApi")
	private void checkIfFlatOnTable(SensorEvent event) {
		// it is flat if x and y is 0.
		if (currentSensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float[] values = event.values;
			int x = (int) values[0];
			int y = (int) values[1];
			boolean isFlat = ( x == 0 && y == 0)? true : false;
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

			if (isFlat){
				if (vibrator != null && vibrator.hasVibrator()){
					vibrator.vibrate(5000);
				}else{
					Toast.makeText(this.getApplicationContext(), "Flat on table. No Vibrator detected" , Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	}

	/**
	 * Check if light sensor is covered by checking if the ambient light levels is 0.
	 * Play the phones ring tone and display a toast message.
	 * @param event
	 */
	private void checkIfLightSensorCovered(SensorEvent event) {
		if (currentSensor.getType() == Sensor.TYPE_LIGHT){
			float ambientLightLevels = event.values[0];
			if (ambientLightLevels == 0){
				try {
			        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			        r.play();
					Toast.makeText(this.getApplicationContext(), "detected light levels to be 0.0" , Toast.LENGTH_SHORT).show();
			    } catch (Exception e) {}
			}
		}
		
	}

	/**
	 * Displays the appropriate sensor information depending on the type of sensor 
	 * given in the intent
	 * @param event
	 */
	@SuppressLint("NewApi")
	private void displaySensorInformation(SensorEvent event){
		StringBuilder displayInfo = new StringBuilder("Sensor Information for : " + currentSensor.getName() + "\n").append("-------------------------\n");
		float[] values = event.values;

		int sensorType = this.getIntent().getExtras().getInt("sensor type");	
		switch(sensorType){
			case Sensor.TYPE_ACCELEROMETER: {
				/*	
				 * What the event values mean:
				 * values[0]: Acceleration minus Gx on the x-axis
	    		 * values[1]: Acceleration minus Gy on the y-axis
	    		 * values[2]: Acceleration minus Gz on the z-axis
				 */

				try{
					displayInfo.append("Acceleration minus Gx on the x-axis: ").append(values[0]).append("\n");
					displayInfo.append("Acceleration minus Gy on the y-axis: ").append(values[1]).append("\n");
					displayInfo.append("Acceleration minus Gz on the z-axis: ").append(values[2]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_AMBIENT_TEMPERATURE:{
				/* 
				 * What the event values mean:
				 * values[0]: ambient (room) temperature in degree Celsius.
				 */
				displayInfo.append("ambient(room) temperature in degree Celsius: ").append(values[0]).append("\n");
				break;
			}
			case Sensor.TYPE_GAME_ROTATION_VECTOR:{
				/*
				 * What the event values mean:
				 * values[0] : angular speed (w/o drift compensation) around the X axis in rad/s
				 * values[1] : angular speed (w/o drift compensation) around the Y axis in rad/s
				 * values[2] : angular speed (w/o drift compensation) around the Z axis in rad/s
				 * values[3] : estimated drift around X axis in rad/s
				 * values[4] : estimated drift around Y axis in rad/s
				 * values[5] : estimated drift around Z axis in rad/s
				 */
				try{
					displayInfo.append("angular speed (w/o drift compensation) around the X axis in rad/s: ").append(values[0]).append("\n");
					displayInfo.append("angular speed (w/o drift compensation) around the Y axis in rad/s: ").append(values[1]).append("\n");
					displayInfo.append("angular speed (w/o drift compensation) around the Z axis in rad/s: ").append(values[2]).append("\n");
					displayInfo.append("estimated drift around X axis in rad/s: ").append(values[3]).append("\n");
					displayInfo.append("estimated drift around Y axis in rad/s: ").append(values[4]).append("\n");
					displayInfo.append("estimated drift around Z axis in rad/s: ").append(values[5]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_GRAVITY:{
				/*
				 * What the event values mean:
				 * A three dimensional vector indicating the direction and magnitude of gravity. 
				 * Units are m/s^2. The coordinate system is the same as is used by the 
				 * acceleration sensor.	 
				 */
				try{
					displayInfo.append("direction and magnitude of gravity on the x-axis: ").append(values[0]).append("\n");
					displayInfo.append("direction and magnitude of gravity on the y-axis: ").append(values[1]).append("\n");
					displayInfo.append("direction and magnitude of gravity on the z-axis: ").append(values[2]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_GYROSCOPE:{
				/*
				 * What the event values mean:
				 * values[0]: Angular speed around the x-axis
				 * values[1]: Angular speed around the y-axis
				 * values[2]: Angular speed around the z-axis
				 */
				try{
					displayInfo.append("Angular speed around the x-axis: ").append(values[0]).append("\n");
					displayInfo.append("Angular speed around the y-axis: ").append(values[1]).append("\n");
					displayInfo.append("Angular speed around the z-axis: ").append(values[2]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;

			}
			case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:{
				
				/*
				 * What the event values mean:
				 * values[0] : angular speed (w/o drift compensation) around the X axis in rad/s
				 * values[1] : angular speed (w/o drift compensation) around the Y axis in rad/s
				 * values[2] : angular speed (w/o drift compensation) around the Z axis in rad/s
				 * values[3] : estimated drift around X axis in rad/s
				 * values[4] : estimated drift around Y axis in rad/s
				 * values[5] : estimated drift around Z axis in rad/s
	
				 */
				try{
					displayInfo.append("angular speed (w/o drift compensation) around the X axis in rad/s: ")
					.append(values[0]).append("\n");
					displayInfo.append("angular speed (w/o drift compensation) around the Y axis in rad/s: ")
					.append(values[1]).append("\n");
					displayInfo.append("angular speed (w/o drift compensation) around the Z axis in rad/s: ")
					.append(values[2]).append("\n");
					
					displayInfo.append("estimated drift around X axis in rad/s: ")
					.append(values[3]).append("\n");
					displayInfo.append("estimated drift around Y axis in rad/s: ")
					.append(values[4]).append("\n");
					displayInfo.append("estimated drift around Z axis in rad/s: ")
					.append(values[5]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_LIGHT:{
				/*
				 * What the event values mean:
				 * values[0]: Ambient light level in SI lux units 
				 */
				displayInfo.append("Ambient light level in SI lux units: ").append(values[0]).append("\n");
				break;
			}
			case Sensor.TYPE_LINEAR_ACCELERATION:{
				/*
				 * What the event values mean:
				 * A three dimensional vector indicating acceleration along each 
				 * device axis, not including gravity. All values have units of m/s^2. 
				 * The coordinate system is the same as is used by the acceleration sensor. 
				 */
				try{
					displayInfo.append("Acceleration on the x-axis (not including gravity): ").append(values[0]).append("\n");
					displayInfo.append("Acceleration on the y-axis (not including gravity): ").append(values[1]).append("\n");
					displayInfo.append("Acceleration on the z-axis (not including gravity): ").append(values[2]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD:{
				/* 
				 * What the event values mean:
				 * All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis. 
				 */
				try{
					displayInfo.append("ambient magnetic field on the x-axis: ").append(values[0]).append("\n");
					displayInfo.append("ambient magnetic field on the y-axis: ").append(values[1]).append("\n");
					displayInfo.append("ambient magnetic field on the z-axis: ").append(values[2]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:{
				/*
				 *  What the event values mean
				 *  values[0] = x_uncalib
				 *  values[1] = y_uncalib
				 *  values[2] = z_uncalib
				 *  values[3] = x_bias
				 *  values[4] = y_bias
				 *  values[5] = z_bias
				 */
				try{
					displayInfo.append("x_uncalib: ").append(values[0]).append("\n");
					displayInfo.append("y_uncalib: ").append(values[1]).append("\n");
					displayInfo.append("z_uncalib: ").append(values[2]).append("\n");
					displayInfo.append("x_bias: ").append(values[3]).append("\n");
					displayInfo.append("y_bias: ").append(values[4]).append("\n");
					displayInfo.append("z_bias: ").append(values[5]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_ORIENTATION:{
				/*
				 * What the event values mean:
				 * values[0]: Azimuth, angle between the magnetic north direction and the y-axis, around
				 *  		  the z-axis (0 to 359). 0=North, 90=East, 180=South, 270=West
				 * values[1]: Pitch, rotation around x-axis (-180 to 180), with positive values when the
				 * 			  z-axis moves toward the y-axis.
				 * values[2]: Roll, rotation around the x-axis (-90 to 90) increasing as the device moves 
				 * 			  clockwise. 
				 */
				try{
					displayInfo.append("Azimuth, angle between the magnetic north direction and the y-axis, around the z-axis: ").append(values[0]).append("\n");
					displayInfo.append("Pitch, rotation around x-axis (-180 to 180), with positive values when the z-axis moves toward the y-axis: ").append(values[1]).append("\n");
					displayInfo.append("Roll, rotation around the x-axis (-90 to 90) increasing as the device moves clockwise: ").append(values[2]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());

				}
				break;
			}
			case Sensor.TYPE_PRESSURE:{
				/*
				 * What the event values mean:
				 * values[0]: Atmospheric pressure in hPa (millibar) 
				 */
				displayInfo.append("Atmospheric pressure in hPa (millibar): ").append(values[0]);
				break;
			}
			case Sensor.TYPE_PROXIMITY:{
				/*
				 * What the event values mean:
				 * values[0]: Proximity sensor distance measured in centimeters 
				 */
				displayInfo.append("Proximity sensor distance measured in centimeters: ").append(values[0]);
				break;
			}
			case Sensor.TYPE_RELATIVE_HUMIDITY:{
				/*
				 * What the event values mean:
				 * values[0]: Relative ambient air humidity in percent 
				 */
				displayInfo.append("Relative ambient air humidity in percent: ").append(values[0]);
				break;
			}
			case Sensor.TYPE_ROTATION_VECTOR:{
				/*
				 * What the event values mean:
				 * The rotation vector represents the orientation of the device 
				 * as a combination of an angle and an axis, in which the device
				 * has rotated through an angle θ around an axis <x, y, z>.
				 * 
				 *   values[0]: x*sin(θ/2)
				 *   values[1]: y*sin(θ/2)
				 *   values[2]: z*sin(θ/2)
				 *   values[3]: cos(θ/2)
				 *   values[4]: estimated heading Accuracy (in radians) (-1 if unavailable)
				 */
				try{
					displayInfo.append("x*sin(θ/2): " ).append(values[0]).append("\n");
					displayInfo.append("y*sin(θ/2): " ).append(values[1]).append("\n");
					displayInfo.append("z*sin(θ/2): " ).append(values[2]).append("\n");
					displayInfo.append("cos(θ/2): " ).append(values[3]).append("\n");
					displayInfo.append("estimated heading Accuracy (in radians) (-1 if unavailable): " ).append(values[4]).append("\n");
				}catch(ArrayIndexOutOfBoundsException e){
					Log.i(MainActivity.DEBUG_TAG, e.getMessage());
				}
				break;
			}
			case Sensor.TYPE_SIGNIFICANT_MOTION:{
				// documentation doesn't have any values for this
			}
			case Sensor.TYPE_TEMPERATURE:{
				// documentation doesn't have any values for this
			}
		}	
		
		displayInfo.append("\nCommon Sensor Data: \n").append("-------------------------").append("\n")
		.append("Maximum Range: ").append(currentSensor.getMaximumRange()).append("\n");
		displayInfo.append("Min Delay: ").append(currentSensor.getMinDelay()).append("\n");
		displayInfo.append("Name: ").append(currentSensor.getName()).append("\n");
		displayInfo.append("Power: ").append(currentSensor.getPower()).append("\n");
		displayInfo.append("Resolution: ").append(currentSensor.getResolution()).append("\n");
		displayInfo.append("Type: ").append(currentSensor.getType()).append("\n");
		displayInfo.append("Vendor: ").append(currentSensor.getVendor()).append("\n");
		displayInfo.append("Version: ").append(currentSensor.getVersion()).append("\n");
		this.sensorInformationTextView.setText(displayInfo.toString());
	}

}
