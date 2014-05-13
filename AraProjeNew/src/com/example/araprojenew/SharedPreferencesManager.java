package com.example.araprojenew;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {
	private static final SharedPreferencesManager INSTANCE = new SharedPreferencesManager();
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	public SharedPreferencesManager(){
		preferences = PreferenceManager.getDefaultSharedPreferences(ResourcesManager.getInstance().activity.getApplicationContext());		
	}
	public static SharedPreferencesManager getInstance() {
		return INSTANCE;
	}
	
	public void setMusicEnabled(boolean b){
		editor = preferences.edit();
		editor.putBoolean("MUSIC", b);
		editor.commit();
	}
	
	public boolean getMusicEnabled(){
		return preferences.getBoolean("MUSIC", true);
	}
	
	public void setHighScore(int i){
		editor = preferences.edit();
		editor.putInt("HIGHSCORE", i);
		editor.commit();
	}
	
	public int getHighScore(){
		return preferences.getInt("HIGHSCORE", 0);
	}
	
	public void setFirstStart(boolean b){
		editor = preferences.edit();
		editor.putBoolean("FIRSTSTART", b);
		editor.commit();
	}
	
	public boolean getFirstStart(){
		return preferences.getBoolean("FIRSTSTART", true);
	}
}
