package com.harman.predown;


import com.harman.predown.model.PdPerference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	private boolean isAutoConnect = false;
	// test 
//	private boolean isAutoConnect = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		// for test
		isAutoConnect = PdPerference.getInstance(getApplicationContext())
				.getAutoConnect();

		// if true , next screen
		if (!isAutoConnect) {
			Intent intent = new Intent(this, Login.class);
			this.startActivity(intent);
		} else {
//			Intent intent = new Intent(this, TabMain.class);
//			this.startActivity(intent);
			Intent intent = new Intent(this, GetLine.class);
			this.startActivity(intent);
		}
		this.finish();
		overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);

	}
}
