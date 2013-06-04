package com.ms.android.handlers;

import java.util.List;

import com.ms.android.data.TrainInfo;
import com.ms.android.data.TrainStatusCollector;
import com.ms.android.trainnotification.AddNotificationActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FetchStationListHanlder extends AsyncTask<String, Integer, TrainInfo> 
{
	
	private static final String tag = FetchStationListHanlder.class.toString();
	private static final String INITIAL_STATION_SELECTION_TEXT = "Select a station for borading";
	
	private ProgressBar m_ProgressBar;
	private Spinner m_Spinner;
	private TextView m_ExtendedTrainName;
	private Button m_AddMoreStations;
	private Context m_Context;
	private ImageView mRemoveSpinner;

	
	public FetchStationListHanlder(Context context, ProgressBar progressBar, TextView extendedTrainName, Spinner stationListSpinner,
										ImageView removeSpinner, Button addMoreStations)
	{
		m_Context = context;
		m_ProgressBar = progressBar;
		m_ExtendedTrainName = extendedTrainName;
		m_Spinner = stationListSpinner;
		mRemoveSpinner = removeSpinner;
		m_AddMoreStations = addMoreStations;
	}
		
	protected TrainInfo doInBackground(String... params) 
	{
		TrainStatusCollector trainStatus = new TrainStatusCollector();
		publishProgress(0);
		return trainStatus.getTrainInfo(params[0]);		
	}
	
	@Override
	protected void onProgressUpdate (Integer... values)
	{
		m_ProgressBar.setVisibility(ProgressBar.VISIBLE);
		
	}
	
	@Override
	protected void onPostExecute (TrainInfo trainInfo)	
	{
		m_ProgressBar.setVisibility(ProgressBar.INVISIBLE);
		if(trainInfo == null) {
			Toast.makeText(m_Context, "Unable to list of station. Check your Internet connection", Toast.LENGTH_SHORT).show();
		} else {
			
			m_ExtendedTrainName.setVisibility(TextView.VISIBLE);
			m_ExtendedTrainName.setText(trainInfo.getExtendedTrainName());
			
			m_AddMoreStations.setVisibility(Button.VISIBLE);
			m_Spinner.setVisibility(Spinner.VISIBLE);
			trainInfo.getListOfStations().add(0, INITIAL_STATION_SELECTION_TEXT);
			ArrayAdapter<String> stationListAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_item,
														trainInfo.getListOfStations());
			stationListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			m_Spinner.setAdapter(stationListAdapter);
			
			mRemoveSpinner.setVisibility(ImageView.VISIBLE);
			AddNotificationActivity.m_ListOfStations = trainInfo.getListOfStations();
			
		}
	}

}
