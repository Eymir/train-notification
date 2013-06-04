package com.ms.android.trainnotification;

import java.util.ArrayList;
import java.util.List;

import com.ms.android.data.TrainDO;
import com.ms.android.db.TrainDataSource;
import com.ms.android.handlers.FetchStationListHanlder;

import android.R.anim;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AddNotificationActivity extends Activity 
{
	
	private Button m_FetchStationListButton;
	private Button m_AddMoreStationButton;
	private EditText m_TrainNumberEditText;
	private ProgressBar m_StationListProgressBar;
	private Spinner m_StationListSpinner;
	private Switch m_StatusBarSwitch;
	private Switch m_SmsNotifySwitch;
	private Switch m_EmailNotifySwitch;
	private TextView m_ExtendedTrainName;
	private Context m_Context;
	public static List<String> m_ListOfStations; 
	private ImageView mRemoveSpinner;
	private int numberOfStationsAdded;
	private static int INDEX_NUMBER_OF_FIRST_SPINNER = 4;
	private LinearLayout llOuter;
	protected Object mActionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_notify_screen);
		
		//setting the app icon to act as navigation for previous hierarchical screen.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		m_Context = getBaseContext();
		m_FetchStationListButton = (Button) findViewById(R.id.fetchStationListButton);
		m_AddMoreStationButton = (Button) findViewById(R.id.AddMoreStationButton);
		m_StationListSpinner = (Spinner) findViewById(R.id.stationListSpinner3);
		m_TrainNumberEditText = (EditText) findViewById(R.id.trainNumberEditText);
		m_StationListProgressBar = (ProgressBar) findViewById(R.id.stationListProgressBar);
		m_StatusBarSwitch = (Switch) findViewById(R.id.statusBarNotifySwitch);
		m_SmsNotifySwitch = (Switch) findViewById(R.id.smsNotifySwitch);
		m_EmailNotifySwitch = (Switch) findViewById(R.id.emailNotifySwitch);	
		m_ExtendedTrainName = (TextView) findViewById(R.id.trainNameAddNotify);
		mRemoveSpinner = (ImageView) findViewById(R.id.removeStationSpinner3);
		mRemoveSpinner.setVisibility(ImageView.INVISIBLE);
		m_StationListProgressBar.setVisibility(ProgressBar.INVISIBLE);
		m_StationListSpinner.setVisibility(ProgressBar.INVISIBLE);
		m_AddMoreStationButton.setVisibility(Button.INVISIBLE);
		//m_StatusBarSwitch.setVisibility(Switch.INVISIBLE);
		//m_SmsNotifySwitch.setVisibility(Switch.INVISIBLE);
		//m_EmailNotifySwitch.setVisibility(Switch.INVISIBLE);
		
		m_FetchStationListButton.setOnClickListener(fetchStationList);
		m_AddMoreStationButton.setOnClickListener(addMoreStations);
		numberOfStationsAdded = 0;
		llOuter = (LinearLayout) findViewById(R.id.linearLayoutOuter);
		
	}
	
	OnClickListener addMoreStations = new OnClickListener() 
	{		
		public void onClick(View v) 
		{
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutOuter);
			
			LinearLayout stationLayout = new LinearLayout(m_Context);
			stationLayout.setOrientation(LinearLayout.HORIZONTAL);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 10, 0, 0);
			
			Spinner newSpinner = new Spinner(m_Context, Spinner.MODE_DROPDOWN);
			LayoutParams spinnerParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.8f);
			newSpinner.setLayoutParams(spinnerParams);
			m_ListOfStations.remove(0);
			m_ListOfStations.add(0, "Select a station for notification");
			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_item, 
															m_ListOfStations);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			newSpinner.setAdapter(spinnerAdapter);
			
			ImageView removeSpinner = new ImageView(m_Context);
			LayoutParams imageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.2f);
			removeSpinner.setLayoutParams(imageParams);
			removeSpinner.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
			removeSpinner.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayoutOuter);
					int position = ((Integer) ((View)v.getParent()).getTag()).intValue();
					ll.removeViewAt(INDEX_NUMBER_OF_FIRST_SPINNER + position);
					numberOfStationsAdded--;
					//Reset the tags of all the linear layouts created dynamically
					for(int i = 0; i < numberOfStationsAdded; i++) {
						((LinearLayout)ll.getChildAt(INDEX_NUMBER_OF_FIRST_SPINNER + i)).setTag(i);
					}
				}
			});
			
			stationLayout.addView(newSpinner);
			stationLayout.addView(removeSpinner);
			stationLayout.setTag(numberOfStationsAdded);
			linearLayout.addView(stationLayout, (INDEX_NUMBER_OF_FIRST_SPINNER + numberOfStationsAdded));
			numberOfStationsAdded++;			
		}
	};
	
	OnClickListener fetchStationList = new OnClickListener() 
	{		
		public void onClick(View v) 
		{
			new FetchStationListHanlder(m_Context, m_StationListProgressBar, m_ExtendedTrainName, 
					m_StationListSpinner, mRemoveSpinner, m_AddMoreStationButton).execute(m_TrainNumberEditText.getText().toString());
			
			mActionMode = AddNotificationActivity.this.startActionMode(mActionModeCallBack);			
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean returnValue = false;
		switch(item.getItemId())
		{
		case android.R.id.home:
			Intent previousScreenIntent = new Intent(this, SetupNotifyActivity.class);
			previousScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(previousScreenIntent);
			returnValue = true;
			break;
			
		default:
			returnValue = super.onOptionsItemSelected(item);
			break;
		}
		return returnValue;
	}
	
	public ActionMode.Callback mActionModeCallBack = new ActionMode.Callback() {
		
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			
		}
		
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater menuInflater = mode.getMenuInflater();
			menuInflater.inflate(R.menu.add_notify_contextual, menu);
			m_TrainNumberEditText.setEnabled(false);
			return true;
		}
		
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			boolean returnValue = false;
			switch(item.getItemId()) {
			case R.id.save:
				TrainDO selectedTrainData = new TrainDO();
				boolean bContinue = true;
				selectedTrainData.setTrainNumber(Integer.parseInt(m_TrainNumberEditText.getText().toString()));
				selectedTrainData.setTrainName(m_ExtendedTrainName.getText().toString());
				
				List<String> selectedStations = new ArrayList<String>();
				if(m_StationListSpinner.getSelectedItemPosition() > 0) {
					selectedStations.add((String) m_StationListSpinner.getSelectedItem());
				} else {
					bContinue = false;
				}
				
				if(bContinue) {
					for(int i = 0; i < numberOfStationsAdded; i++) {

						int position = ((Spinner) (((LinearLayout)llOuter.getChildAt(INDEX_NUMBER_OF_FIRST_SPINNER + i))
								.getChildAt(0))).getSelectedItemPosition();
						if(position > 0) {
							selectedStations.add((String)(((Spinner) (((LinearLayout)llOuter.getChildAt(INDEX_NUMBER_OF_FIRST_SPINNER + i))
									.getChildAt(0))).getSelectedItem()));
						} else {
							bContinue = false;
							break;
						}

					}
					selectedTrainData.setStationCodes(selectedStations);
				}
				
				if(bContinue) {
					if(m_StatusBarSwitch.isChecked()) {
						selectedTrainData.setStatusBarNofiy(1);
					} else {
						selectedTrainData.setStatusBarNofiy(0);
					}

					if(m_SmsNotifySwitch.isChecked()) {
						selectedTrainData.setSmsNotify(1);
					} else {
						selectedTrainData.setSmsNotify(0);
					}

					if(m_EmailNotifySwitch.isChecked()) {
						selectedTrainData.setEmailNotify(1);
					} else {
						selectedTrainData.setEmailNotify(0);
					}
					
					//Insert the data
					TrainDataSource tds = new TrainDataSource(m_Context);
					tds.insert(selectedTrainData);
					mode.finish();
					m_TrainNumberEditText.setEnabled(true);
					returnValue = true;
				} else {
					Toast.makeText(m_Context, "Check your selection!!!", Toast.LENGTH_SHORT).show();					
				}
				
			break;
				
			case R.id.cancel:
				m_ExtendedTrainName.setText("");
				m_ExtendedTrainName.setVisibility(TextView.INVISIBLE);
				
				m_StationListSpinner.setVisibility(Spinner.INVISIBLE);
				mRemoveSpinner.setVisibility(ImageView.INVISIBLE);
				m_AddMoreStationButton.setVisibility(Button.INVISIBLE);
				
				for(int i= 0; i < numberOfStationsAdded; i++) {
					llOuter.removeViewAt(INDEX_NUMBER_OF_FIRST_SPINNER);
				}
				
				m_StatusBarSwitch.setChecked(false);
				m_SmsNotifySwitch.setChecked(false);
				m_EmailNotifySwitch.setChecked(false);
				mode.finish();
				m_TrainNumberEditText.setEnabled(true);
				numberOfStationsAdded = 0;
				returnValue = true;
				break;
			}
			
			return returnValue;
		}
	};
}
