package sk.henrichg.phoneprofileshelper;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


public class ReceiversService extends Service {

	private final PPHeplerBroadcastReceiver receiver = new PPHeplerBroadcastReceiver();

	public static boolean radioStateChange = false; 
	
	@Override
    public void onCreate()
	{
		SystemRoutines.logE("ReceiversService.onCreate", "xxx");
		
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction(PPHeplerBroadcastReceiver.ACTION_SETPROFILEPREFERENCES);
		intentFilter1.addAction(PPHeplerBroadcastReceiver.ACTION_RADIOCHANGESTATE);
		registerReceiver(receiver, intentFilter1);
	}
	 
	@Override
    public void onDestroy()
	{
		unregisterReceiver(receiver);
		SystemRoutines.logE("ReceiversService.onDestroy", "xxx");
    }
	 
	@Override
    public int onStartCommand(Intent intent, int flags, int startId)
	{
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

}
