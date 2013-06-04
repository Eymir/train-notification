package com.ms.android.data;

import java.util.List;

public class TrainDO {

	private int mTrainNumber;
	private String mTrainName;
	private List<String> mStationCodes;
	private int mStatusBarNofiy;
	private int mSmsNotify;
	private int mEmailNotify;
	
	public int getTrainNumber() {
		return mTrainNumber;
	}
	public void setTrainNumber(int mTrainNumber) {
		this.mTrainNumber = mTrainNumber;
	}
	public String getTrainName() {
		return mTrainName;
	}
	public void setTrainName(String mTrainName) {
		this.mTrainName = mTrainName;
	}
	public List<String> getStationCodes() {
		return mStationCodes;
	}
	public void setStationCodes(List<String> mStationCodes) {
		
		this.mStationCodes = mStationCodes;
	}
	public int getStatusBarNofiy() {
		return mStatusBarNofiy;
	}
	public void setStatusBarNofiy(int mStatusBarNofiy) {
		this.mStatusBarNofiy = mStatusBarNofiy;
	}
	public int getSmsNotify() {
		return mSmsNotify;
	}
	public void setSmsNotify(int mSmsNotify) {
		this.mSmsNotify = mSmsNotify;
	}
	public int getEmailNotify() {
		return mEmailNotify;
	}
	public void setEmailNotify(int mEmailNotify) {
		this.mEmailNotify = mEmailNotify;
	}
	
	public void addStationCode(String stationCode) {
		this.mStationCodes.add(stationCode);
	}
	
	
	
}
