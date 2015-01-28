package sk.henrichg.phoneprofileshelper;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e("StartActivity.onCreate", "xxx");
		
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		finish();
	}
}
