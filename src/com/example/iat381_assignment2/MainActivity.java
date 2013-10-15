package com.example.iat381_assignment2;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	static final String DEBUG_TAG = "SensorProgram";
	
	MediaRecorder recorder;
	
	SensorManager sensorManager;
	LocationManager locationManager;
	SoundDetector soundDetector;
	
	// stores the button references
	LinkedList<Button> sensorButtons;
	List<Sensor> mySensors;
	
	// key: button, value: sensor associated to the button
	HashMap<Button, Sensor> buttonSensorHash;

	private LinearLayout myLayout;
	private LinearLayout detectMovementLayout;
	private LinearLayout detectEnvironmentLayout;
	
	private TextView detectEnvironmentTextView;

	private String bestProvider;

	private Location myLocation;

	private Button detectMovementButton;
	private Button detectEnvironmentNoiseButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Toast.makeText(this.getApplicationContext(), "On Create", Toast.LENGTH_SHORT).show();

		// initialize main layout
		myLayout = (LinearLayout) this.findViewById(R.id.main_layout);
		myLayout.setBackgroundColor(Color.WHITE);
		myLayout.setOrientation(LinearLayout.VERTICAL);
		
		displayDetectMovementAndEnvironment();
		displayAllSensors();
		registerEventListeners();
		
		soundDetector = new SoundDetector();
			
	}

	private void configureCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		bestProvider = locationManager.getBestProvider(criteria, true);
	}

	/**
	 * This function initializes the buttons and text view for the detect movement
	 * and environment noise feature
	 */
	private void displayDetectMovementAndEnvironment() {
		
		// initialize text views
		detectEnvironmentTextView = new TextView(this.getApplicationContext());
		detectEnvironmentTextView.setText("Click button to get status update");
		
		// initialize inner layouts
		detectMovementLayout = new LinearLayout(getApplicationContext());
		detectEnvironmentLayout = new LinearLayout(getApplicationContext());
		
		// change layout to horizontal for the inner layouts
		detectMovementLayout.setOrientation(LinearLayout.HORIZONTAL);
		detectEnvironmentLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		// add inner layouts to main layout
		myLayout.addView(detectMovementLayout);
		LinearLayout.LayoutParams margin = (LayoutParams) detectMovementLayout.getLayoutParams();
		margin.bottomMargin += 10;		
		myLayout.addView(detectEnvironmentLayout);
		LinearLayout.LayoutParams margin2 = (LayoutParams) detectEnvironmentLayout.getLayoutParams();
		margin2.bottomMargin += 10;	
		
		buttonSensorHash = new HashMap<Button, Sensor>();
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		
		int imgID = getResources().getIdentifier("button_pressed2", "drawable", getApplication().getPackageName());

		// add detect movement button to inner layout
		detectMovementButton = new Button(this.getApplicationContext());
		detectMovementButton.setText("Go to Detect Movement Activity");
		detectMovementButton.setBackgroundColor(Color.argb(255, 0, 0, 0));
		detectMovementButton.setPadding(0, 25, 0, 25);	
		detectMovementButton.setBackgroundResource(imgID);
		detectMovementLayout.addView(detectMovementButton);
		
		LayoutParams param = (LayoutParams) detectMovementButton.getLayoutParams();
		param.width = LayoutParams.MATCH_PARENT;
		detectMovementButton.setLayoutParams(param);
		
		// add detect environment button to inner layout
		detectEnvironmentNoiseButton = new Button(this.getApplicationContext());
		detectEnvironmentNoiseButton.setText("Detect Environment Noise");
		detectEnvironmentNoiseButton.setBackgroundColor(Color.argb(255, 0, 0, 0));
		detectEnvironmentNoiseButton.setPadding(0, 25, 0, 25);	
		detectEnvironmentNoiseButton.setBackgroundResource(imgID);
		detectEnvironmentLayout.addView(detectEnvironmentNoiseButton);
		
		LayoutParams param2 = (LayoutParams) detectEnvironmentNoiseButton.getLayoutParams();
		param2.width = LayoutParams.MATCH_PARENT;
		param2.weight = (float) 0.5;
		detectMovementButton.setLayoutParams(param2);

		detectEnvironmentLayout.addView(detectEnvironmentTextView);
		detectEnvironmentTextView.setLayoutParams(param2);
		
		LayoutParams paddingParams = (LayoutParams) detectEnvironmentTextView.getLayoutParams();
		paddingParams.leftMargin = 20;
		paddingParams.rightMargin = 20;
		detectEnvironmentTextView.setLayoutParams(paddingParams);
		
		detectEnvironmentNoiseButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				soundDetector.start();			
				detectEnvironmentTextView.setText("Amplitude: " +  soundDetector.getNoiseLevel());
			}
		});
	
		detectMovementButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, DetectMovement.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
				getApplicationContext().startActivity(intent);
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * This functions creates buttons for all the sensors in the device 
	 */
	private void displayAllSensors(){
	
		mySensors = sensorManager.getSensorList(Sensor.TYPE_ALL); // grab all of the sensors
		
		// display them on the UI
		for (int i = 0 ; i < mySensors.size(); i++){
			Sensor sensor = mySensors.get(i);
			
			Button sensorButton = new Button(this.getApplicationContext());
			sensorButton.setText(sensor.getName());
			sensorButton.setBackgroundColor(Color.argb(255, 20, 151, 204));
			
			int imgID = getResources().getIdentifier("button_pressed", "drawable", getApplication().getPackageName());
			sensorButton.setBackgroundResource(imgID);

			sensorButton.setPadding(0, 25, 0, 25);
			myLayout.addView(sensorButton);
			
			LinearLayout.LayoutParams margin = (LayoutParams) sensorButton.getLayoutParams();
			margin.bottomMargin += 10;		
			
			// associate the button to its sensor
			buttonSensorHash.put(sensorButton, sensor);
			
		}
	}

	/**
	 * This function registers onClick event listeners to each button.
	 */
	private void registerEventListeners(){
		
		for (final Entry<Button, Sensor> entry : this.buttonSensorHash.entrySet()){
			final Button sensorButton = entry.getKey();
			final Sensor sensor = entry.getValue();
			
			final Context context = this.getApplicationContext();

			sensorButton.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), sensorButton.getText(), Toast.LENGTH_SHORT).show();
					final Intent intent = new Intent(MainActivity.this, SensorInformation.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
					intent.putExtra("sensor type", sensor.getType());	
					context.startActivity(intent);
				}	
			});
		}
	}
	@Override
	protected void onStop(){
		this.soundDetector.stop();
		super.onStop();
	}
	
	@Override
	protected void onStart(){
		//Toast.makeText(this.getApplicationContext(), "On Start", Toast.LENGTH_SHORT).show();
		super.onStart();
	}
}
