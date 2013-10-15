package com.example.iat381_assignment2;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

/**
 * Class that handles detecting environmental noise using media recorder.
 * @author Kristofer
 *
 */
public class SoundDetector {
	private MediaRecorder mediaRecorder;
	
	public void start(){
		if (mediaRecorder == null){
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile("/dev/null");
			
			try {
				mediaRecorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mediaRecorder.start();
		}
	}
	
	public void stop(){
		if ( mediaRecorder != null){
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}
	
	public double getNoiseLevel(){
		if ( mediaRecorder != null){
			return mediaRecorder.getMaxAmplitude();
		}
		return 0;
	}
}
