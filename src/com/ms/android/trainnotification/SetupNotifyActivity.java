package com.ms.android.trainnotification;

import java.util.List;

import com.ms.android.data.TrainDO;
import com.ms.android.db.TrainDataSource;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SetupNotifyActivity extends Activity 
{
	
	ListView mNotificationList;
	Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_notify_layout);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        mNotificationList = (ListView) findViewById(R.id.notifyList);
        mContext = getBaseContext();
        TrainDataSource tds = new TrainDataSource(mContext);
        List<TrainDO> listOfTrains = tds.getAllTrains();
        CustomeAdapter adapter = new CustomeAdapter(this, listOfTrains);
        mNotificationList.setAdapter(adapter);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.setup_notify_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	boolean returnValue = false;
    	switch(item.getItemId())
    	{
    	case android.R.id.home:
    		Intent backScreenIntent = new Intent(this, HomeScreenActivity.class);
    		backScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(backScreenIntent);
    		returnValue = true;
    		break;
    		
    	case R.id.addNotify:
    		Intent addNotificationIntent = new Intent(this, AddNotificationActivity.class);
    		addNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(addNotificationIntent);
    		returnValue = true;
    		break;
    		
    	default:
    		returnValue = super.onOptionsItemSelected(item);
    		break;
    	}
    	
    	return returnValue;
    }
    
    
    public class CustomeAdapter extends BaseAdapter {
    	
    	private Activity activity;
    	private List<TrainDO> data;
    	private LayoutInflater inflater = null;
    	
    	public CustomeAdapter(Activity activity, List<TrainDO> d) {
    		this.activity = activity;
    		data = d;
    		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}

		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if(convertView == null) {
				view = inflater.inflate(R.layout.notify_list_row, null);
			}
			
			TextView trainName = (TextView) view.findViewById(R.id.trainNameListRow);
			TextView stationsTextView = (TextView) view.findViewById(R.id.stationCodesListRow);
			TrainDO trainData = data.get(position);
			
			String strTrainName = (Integer.valueOf(trainData.getTrainNumber())).toString() + " - " +
										trainData.getTrainName();
			trainName.setText(strTrainName);
			
			String stationsList = "";
			String comma = "";
			for(String station : trainData.getStationCodes()) {
				stationsList += comma + station;
				comma = ",";
			}
			
			stationsTextView.setText(stationsList);
			return view;
			
		}
    	
    }

}
