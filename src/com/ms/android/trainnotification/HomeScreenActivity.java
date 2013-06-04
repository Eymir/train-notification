package com.ms.android.trainnotification;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeScreenActivity extends Activity 
{

	public Button m_liveStatusButton;
	public Button m_setupNaviButton;
	public Button m_stationCodetoNameButton;
	public Context m_context;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        m_context = getApplicationContext();
        
        m_liveStatusButton = (Button) findViewById(R.id.liveStatusButton);
        m_setupNaviButton = (Button) findViewById(R.id.setupNotificationButton);
        
        m_liveStatusButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(m_context, LiveStatusActivity.class);
				startActivity(intent);				
			}
		});
        m_setupNaviButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(m_context, SetupNotifyActivity.class);
				startActivity(intent);				
			}
		});        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
    
    /*
    OnClickListener setupNotifyScreen = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent = new Intent(m_context, SetupNotifyActivity.class);
			startActivity(intent);
		}
	};
    
    OnClickListener liveStatusScreen = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent = new Intent(m_context, LiveStatusActivity.class);
			startActivity(intent);
		}
	};
	*/	
}
