package com.example.thesocialquadruped;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
	
	private Handler myHandler;
	
	// intialize layout of SplashScreen.java
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_splash);
		
		myHandler = new Handler();
		
		// displays splash screen for 3000ms
		myHandler.postDelayed(new Runnable()
		{
			
			@Override
			public void run ()
			{
				finish ();
				
				Intent intent = new Intent (SplashScreen.this,ManualActivity.class);
				SplashScreen.this.startActivity (intent);
				
			}
		}, 3000);
	}

}
