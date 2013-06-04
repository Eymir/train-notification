package com.ms.android.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Manoj
 *
 * Mar 7, 2013
 */


public class StationInfo {
	
	private String m_StationCode;
	private String m_StationName;
	private String m_DepartedTime;
	private String m_Status;
	private int m_DelayInMinutes;
	private String m_StatusCode;
	private String m_RunningStatus;
	
	public static final String DEPARTED_DATE_FORMAT_BY_RAILWAYS = "yyyy-MM-dd'T'HH:mm:ss";
	
	public static final String DEPARTED_DATE_FORMAT_FOR_HUMANS = "HH:mm"; 
	
	public String getStationCode() {
		return m_StationCode;
	}
	public String getStationName() {
		return m_StationName;
	}
	public String getDepartedTime() {
		return m_DepartedTime;
	}
	public String getStatus() {
		return m_Status;
	}
	public int getDelayInMinutes() {
		return m_DelayInMinutes;
	}	
	public void setStationCode(String m_StationCode) {
		this.m_StationCode = m_StationCode;
	}
	public void setStationName(String m_StationName) {
		this.m_StationName = m_StationName;
	}
	public void setDepartedTime(String m_DepartedTime) throws ParseException {
		SimpleDateFormat sourceDateFormat = new SimpleDateFormat(DEPARTED_DATE_FORMAT_BY_RAILWAYS);
		SimpleDateFormat destinationDateFormat = new SimpleDateFormat(DEPARTED_DATE_FORMAT_FOR_HUMANS);
		Date sourceDate = sourceDateFormat.parse(m_DepartedTime);
		this.m_DepartedTime = destinationDateFormat.format(sourceDate);
	}
	public void setStatus(String m_Status) {
		this.m_Status = m_Status;
	}
	public void setDelayInMinutes(int delayInMinutes){
		m_DelayInMinutes = delayInMinutes;
	}
	
	public void setRunningStatus(String runningStatus) {
		this.m_RunningStatus = runningStatus;
	}
	public String getRunningStatus() {
		return this.m_RunningStatus;
	}
	
	public void setStatusCode(String statusCode) {
		this.m_StatusCode = statusCode;
	}
	public String getStatusCode() {
		return this.m_StatusCode;
	}
	

}
