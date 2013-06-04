package com.ms.android.handlers;

import com.ms.android.data.StationInfo;
import com.ms.android.data.TrainStatusCollector;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FetchCurrentLocation extends AsyncTask<String, Integer, String> {

	private Context m_Context;
	private ProgressBar m_ProgressBar;
	private TextView m_CurrentLocationTextView;
	
	public FetchCurrentLocation(Context context, ProgressBar progressBar, TextView currentLocation) {
		m_Context = context;
		m_ProgressBar = progressBar;
		m_CurrentLocationTextView = currentLocation;
	}
	
	@Override
	protected String doInBackground(String... params) {
		TrainStatusCollector infoCollector = new TrainStatusCollector();
		publishProgress(10);
		String currentLocation = "";
		StationInfo lastStationInfo = infoCollector.getLastStationLocation(params[0], params[1], params[2]);
		if(lastStationInfo != null) {
			//Toast.makeText(m_Context, "Unable to fetch current location details", Toast.LENGTH_SHORT).show();
			if(lastStationInfo.getStatusCode().equalsIgnoreCase("reached")) {
				currentLocation += lastStationInfo.getRunningStatus();
			} else if(lastStationInfo.getStatusCode().equalsIgnoreCase("not_started")) {
				currentLocation += lastStationInfo.getRunningStatus();
			} else {
				String postfix = "";
				if(lastStationInfo.getDelayInMinutes() > 0) {
					postfix += " Delayed by " + lastStationInfo.getDelayInMinutes() + " minutes.";
				} else if(lastStationInfo.getDelayInMinutes() == 0) {
					postfix += " On time.";
				} else {
					postfix += " Coming earlier by " + lastStationInfo.getDelayInMinutes() + " minutes.";
				}
				currentLocation += "Departed station (" + lastStationInfo.getStationCode() + ") " + 
									lastStationInfo.getStationName() + " at " + lastStationInfo.getDepartedTime() + "." +
									postfix + "\n";
				
				String estimateTimeOfArrival = infoCollector.getEstimatedTimeOfArrival(params[0], params[1], params[2], lastStationInfo.getDelayInMinutes());
				if(estimateTimeOfArrival != null) {
					currentLocation += "Expected arrival at " + params[2] + " is " + estimateTimeOfArrival + ".";
				}
			}
		}
		
		
		return currentLocation;
	}

	protected void onProgressUpdate(Integer... values) {
		m_ProgressBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	protected void onPostExecute(String currentLocation) {
		m_ProgressBar.setVisibility(ProgressBar.INVISIBLE);
		m_CurrentLocationTextView.setVisibility(TextView.VISIBLE);
		m_CurrentLocationTextView.setText(currentLocation);		
	}
	
}
