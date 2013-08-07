package com.harman.predown.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PdPerference {
	
	// For debugging
	private static final String TAG = "PdPerference";
	
	// Preferences name
	private static final String pp_PREFERENCE = "pd_preference";

	private static final String KEY_AUTOCONNECT = "autoconnect";

	private static final String KEY_AUTOPREDOWN = "autopredown";
	
	/** Instance */
	private static PdPerference m_Instance;  

	/** Context application */
	private Context m_Context;

	private PdPerference(Context context) {
		this.m_Context = context;
	}

	public synchronized static PdPerference getInstance(Context context) {
		if (m_Instance == null) {
			
			m_Instance = new PdPerference(context);
		}

		return m_Instance;
	}

	public void setAutoConnect(boolean bIsFirst){
		SharedPreferences preferences = m_Context.getSharedPreferences(
				pp_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor settingEditor = preferences.edit();
		settingEditor.putBoolean(KEY_AUTOCONNECT, bIsFirst);
		settingEditor.commit();
		
	}
	
	public boolean getAutoConnect() {
		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				pp_PREFERENCE, Context.MODE_PRIVATE);

		return preferences.getBoolean(KEY_AUTOCONNECT, false);
	}
	
	
	public void setAutoPredown(boolean bIsAccept) {
		SharedPreferences preferences = m_Context.getSharedPreferences(
				pp_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor settingEditor = preferences.edit();
		settingEditor.putBoolean(KEY_AUTOPREDOWN, bIsAccept);
		settingEditor.commit();
	}

	public boolean getAutoPredown() {
		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				pp_PREFERENCE, Context.MODE_PRIVATE);

		return preferences.getBoolean(KEY_AUTOPREDOWN, false);
	}

}
