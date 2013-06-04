package com.ms.android.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TrainStatusCollector 
{

	public static String TRAIN_ENQUIRY_SERVER = "http://mobile.trainenquiry.com/l";
	
	//public static String SCHEDULE_SERVER = "http://stage.railyatri.in/te/schedule/";
	//public static String SEARCH_SERVICE_HOST = "http://coa-search-193678880.ap-southeast-1.elb.amazonaws.com/te/schedule/";
	public static String SEARCH_SERVICE_HOST = "http://coa-search-193678880.ap-southeast-1.elb.amazonaws.com/";

	//public static String ETA_SERVER = "http://coa.railyatri.in/train/location/eta.json?";
	//public static String COA_SERVICE_HOST = "http://coa-433841822.ap-southeast-1.elb.amazonaws.com/train/location/eta.json?";
	public static String COA_SERVICE_HOST = "http://coa-433841822.ap-southeast-1.elb.amazonaws.com/";

	//public static String LOCATION_SERVER = "http://coa.railyatri.in/train/location.json?callback=jQuery164011875250330194831";
	//public static String LOCATION_SERVER = "http://coa-433841822.ap-southeast-1.elb.amazonaws.com/train/location.json?callback=jQuery164011875250330194831";
	public static String LOCATION_SERVER = TRAIN_ENQUIRY_SERVER + "/ajax/location.json?";
	
	

	public static final String JSON = ".json";

	public static final String DATE_FORMAT_BY_RAILWAYS_STA = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String DATE_FORMAT_BY_RAILWAY_DELAY = "mm";

	public static final String DATE_FORMAT_BY_ME = "HH:mm";

	public static final String tag = "TrainStatusCollector";

	private static final String EXTENDED_TRAIN_NAME = "extended_train_name";

	private static final String ROUTES = "routes";

	private static final String STATIONS = "stations"; 

	/**
	 * Return a list of station along with the train name.
	 * @param trainNumber
	 * @return a list of Station names and train Name. The first element in the list will
	 * train Name follow by list of station ( (station_code) station_name) where the train stops.
	 */
	public List<String> getListOfStations(String trainNumber)
	{
		String stationListUrl = SEARCH_SERVICE_HOST + "search" + JSON + "?q=" + trainNumber;
		List<String> listOfStations = null;
		String jsonData = fetchData(stationListUrl);
		if(jsonData != null)
		{
			listOfStations = new ArrayList<String>();
			try
			{
				JSONObject trainInfo = new JSONObject(jsonData);
				listOfStations.add(trainInfo.getString("extended_train_name"));
				JSONObject routes = trainInfo.getJSONObject("routes");
				String stationListData = routes.getString("stations");
				String[] stationList = stationListData.split(",");
				for(int i = 0; i < stationList.length; i++)
				{
					listOfStations.add(stationList[i]);
				}

			}
			catch(JSONException jsonEx)
			{
				Log.e(tag, jsonEx.getMessage());
				listOfStations = null;
			}
		}
		return listOfStations;
	}

	/**
	 * Returns a TrainInfo object populated with train name, list of station and list of start dates.
	 * @param trainNumber train number
	 * @return a TrainInfo object
	 */
	public TrainInfo getTrainInfo(String trainNumber)
	{
		String trainInfoUrl = SEARCH_SERVICE_HOST + "search" + JSON + "?q=" + trainNumber;
		List<String> listOfStations = null;
		List<String> startDate = null;
		TrainInfo trainInfo = null;
		String jsonData = fetchData(trainInfoUrl);
		if(jsonData != null)
		{
			trainInfo = new TrainInfo();
			listOfStations = new ArrayList<String>();
			startDate = new ArrayList<String>();

			try 
			{
				if((new JSONArray(jsonData)).length() > 0) {
					JSONObject jsonTrainInfo = (new JSONArray(jsonData)).getJSONObject(0);
					trainInfo.setValidTrain(true);
					trainInfo.setExtendedTrainName(jsonTrainInfo.getString(EXTENDED_TRAIN_NAME));

					JSONObject routes = jsonTrainInfo.getJSONArray(ROUTES).getJSONObject(0);
					String[] stations = (routes.getString(STATIONS)).split(",");
					for (String station : stations) 
					{
						listOfStations.add(station);
					}
					trainInfo.setListOfStations(listOfStations);

					String[] dates = (jsonTrainInfo.getString("start_dates")).split(",");
					for (String date : dates)
					{
						date = ((date.replaceAll("\"", "")).replaceAll("\\[", "")).replaceAll("\\]", "");
						startDate.add(date);
					}
					trainInfo.setStartDates(startDate);
				} else {
					trainInfo.setValidTrain(false);
				}
			}
			catch(JSONException jsonEx)
			{
				Log.e(tag, jsonEx.getMessage());
				trainInfo = null;
			}

		}
		return trainInfo;
	}

	/**
	 * 
	 * @param trainNumber
	 * @param startDate
	 * @param stationName is in the format ((station_code) station_name )
	 * @return a String saying when train left the last station
	 */
	public StationInfo getLastStationLocation(String trainNumber, String startDate, String stationName) {
		String lastStationUrl = LOCATION_SERVER + "t=" + trainNumber + "&s=" + startDate;
		String jsonData = fetchData(lastStationUrl);
		StationInfo stationInfo = null;
		if(jsonData != null) {
			
			try {
				
				JSONObject locationDataInJSON = new JSONObject(jsonData);
				String key = locationDataInJSON.getString("keys");
				locationDataInJSON = new JSONObject(locationDataInJSON.getString(key.substring(2, (key.length() -2))));
				stationInfo = new StationInfo();
				stationInfo.setStatusCode(locationDataInJSON.getString("status_code"));
				if(stationInfo.getStatusCode().equalsIgnoreCase("reached")) {
					stationInfo.setRunningStatus(locationDataInJSON.getString("status"));
				} else if(stationInfo.getStatusCode().equalsIgnoreCase("not_started")) {
					String notYetStarted = locationDataInJSON.getString("status") + "Scheduled to start at " + locationDataInJSON.getString("scheduled_start");
					stationInfo.setRunningStatus(notYetStarted);
				} else {
					JSONObject lastStationJSON  = (locationDataInJSON.getJSONObject("running_info")).getJSONObject("last_stn");
					stationInfo.setDelayInMinutes(locationDataInJSON.getInt("delay_mins"));
					stationInfo.setStationCode(lastStationJSON.getString("station_code"));
					stationInfo.setStationName(lastStationJSON.getString("station_name"));
					stationInfo.setStatus(lastStationJSON.getString("status"));
					stationInfo.setDepartedTime(lastStationJSON.getString("time"));				
				}
			} catch (Exception ex) {
				Log.e(TrainStatusCollector.class.toString(), ex.getMessage());
			}

		}
		
		return stationInfo;
	}

	public String getEstimatedTimeOfArrival(String trainNumber, String startDate, String stationName, int delayInMinutesLocation) {
		String scheduleInfoUrl = SEARCH_SERVICE_HOST + "te/schedule/" + trainNumber + "/" + startDate + ".json";
		
		String stationCode = stationName.substring((stationName.indexOf("(") + 1), stationName.lastIndexOf(")"));
		String etaInfoUrl = COA_SERVICE_HOST + "train/location/eta.json?callback=jQuery16405237756767310202_" + System.currentTimeMillis() + "&t=" + 
							trainNumber + "&s=" + startDate + "&codes=" + stationCode + "&_" + System.currentTimeMillis();										
		//String etaInfoUrl = COA_SERVICE_HOST + "train/location/eta.json?t=" + trainNumber + "&s=" + startDate + "&codes=" + stationCode;
		
		String standardTimeOfArrival = "";
		String delayInArrival = "";
		String estimatedTimeOfArrival = null;
		
		String scheduleInfo = fetchData(scheduleInfoUrl);
		String etaInfo = fetchData(etaInfoUrl);
		try {
			if((scheduleInfo != null) && (etaInfo != null)) {
				etaInfo = etaInfo.substring((etaInfo.indexOf("(") + 1), etaInfo.lastIndexOf(")"));
				delayInArrival = (new JSONArray(etaInfo)).getJSONObject(0).getString("delay_in_arrival");
				JSONArray scheduleInfoArray = new JSONArray(scheduleInfo);
				for(int i = 0; i < scheduleInfoArray.length(); i ++) {
					JSONObject schedInfoForStation = scheduleInfoArray.getJSONObject(i);
					if(stationCode.equalsIgnoreCase(schedInfoForStation.getString("station_code"))) {
						standardTimeOfArrival = schedInfoForStation.getString("sta");
						break;
					}
				}
				
				String temp = delayInArrival;
				temp = temp.replaceAll("-", "");
				if(delayInMinutesLocation > Integer.parseInt(delayInArrival)) {
					estimatedTimeOfArrival = calculateETA(standardTimeOfArrival, Integer.valueOf(delayInMinutesLocation).toString());
				} else {
					estimatedTimeOfArrival = calculateETA(standardTimeOfArrival, delayInArrival);
				}
			}
		} catch (JSONException jsonException) {
			Log.e(TrainStatusCollector.class.toString(), jsonException.getMessage());
		} catch (ParseException parseException) {
			Log.e(TrainStatusCollector.class.toString(), parseException.getMessage());
		}
		
		return estimatedTimeOfArrival;
	}
	
	private String fetchData(String url) {
		StringBuilder data = new StringBuilder();
		boolean isSuccess = false;
		HttpClient stageClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		RailyatriCookies railyatriCookies = getRailyatriCookies();
		if(railyatriCookies != null) {
			((DefaultHttpClient)stageClient).setCookieSpecs(railyatriCookies.getCookieSpecRegistry());
			((DefaultHttpClient)stageClient).setCookieStore(railyatriCookies.getCookieStore());
		}

		httpGet.setHeader("Accept", "application/json");
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.97 Safari/537.22");
		httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		try
		{
			HttpResponse response = stageClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if(statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = bufferedReader.readLine()) != null)
				{
					data.append(line);
					isSuccess = true;
				}				

			}
			else
			{
				Log.e(tag, "Failed to access: " + url + ". status code = " + statusCode);
				isSuccess = false;
			}
		}
		catch (ClientProtocolException clientProtocolException)
		{
			clientProtocolException.printStackTrace();
			Log.e(tag, clientProtocolException.getMessage());
			isSuccess = false;
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
			Log.e(tag, ioException.getMessage());
			isSuccess = false;
		}

		if(isSuccess)
		{
			return data.toString();
		}
		else
		{
			return null;
		}


	}

	private RailyatriCookies getRailyatriCookies () {

		String railyatriUrl = TRAIN_ENQUIRY_SERVER;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(railyatriUrl);
		RailyatriCookies railyatriCookies = null;
		try {

			HttpResponse httpResponse = httpClient.execute(httpGet);

			if(httpResponse.getStatusLine().getStatusCode() == 200) {
				railyatriCookies = new RailyatriCookies();
				railyatriCookies.setCookieSpecRegistry(httpClient.getCookieSpecs());
				railyatriCookies.setCookieStore(httpClient.getCookieStore());

				//For debugging only
				List<Cookie> cookies = railyatriCookies.getCookieStore().getCookies();
				if(cookies.isEmpty()) {
					System.out.println("No Cookies!!!");
				} else {
					for(int i = 0; i < cookies.size(); i++) {
						System.out.println("-" + cookies.get(i).toString());
					}
				}

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return railyatriCookies;
	}

	private String calculateETA(String standardTimeOfArrival, String delayInMinutes) throws ParseException {
		SimpleDateFormat sourceDateFormat = new SimpleDateFormat(DATE_FORMAT_BY_RAILWAYS_STA);
		SimpleDateFormat destinationDateFormat = new SimpleDateFormat(DATE_FORMAT_BY_ME);
		SimpleDateFormat delayDateFormat = new SimpleDateFormat(DATE_FORMAT_BY_RAILWAY_DELAY);
		boolean bTrainIsComingSoonerThanExpected = false;
		Date staDate  = sourceDateFormat.parse(standardTimeOfArrival);
		if(delayInMinutes.contains("-")) {
			delayInMinutes = delayInMinutes.replaceAll("-", "");
			bTrainIsComingSoonerThanExpected = true;
		}
		Date delayDate = delayDateFormat.parse(delayInMinutes);
		
		Calendar staCalendar = Calendar.getInstance();
		staCalendar.setTime(staDate);
		
		Calendar delayCalendar = Calendar.getInstance();
		delayCalendar.setTime(delayDate);
		
		Calendar etaCalendar = (Calendar) staCalendar.clone();
		if(bTrainIsComingSoonerThanExpected) {
			etaCalendar.add(Calendar.MINUTE, -delayCalendar.get(Calendar.MINUTE));
		} else {
			etaCalendar.add(Calendar.MINUTE, delayCalendar.get(Calendar.MINUTE));
		}
		
		return destinationDateFormat.format(etaCalendar.getTime());
	}

	public class RailyatriCookies {

		private CookieStore m_CookieStore;
		private CookieSpecRegistry m_CookieSpecRegistry;

		public void setCookieStore(CookieStore cookieStore) {
			m_CookieStore = cookieStore;
		}

		public CookieStore getCookieStore() {
			return m_CookieStore;
		}

		public void setCookieSpecRegistry(CookieSpecRegistry cookieSpecRegistry) {
			m_CookieSpecRegistry = cookieSpecRegistry;
		}

		public CookieSpecRegistry getCookieSpecRegistry() {
			return m_CookieSpecRegistry;
		}

	}
}





