package sk.henrichg.phoneprofileshelper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class PPHeplerBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String ACTION_SETPROFILEPREFERENCES = "sk.henrichg.phoneprofileshelper.ACTION_SETPROFILEPREFERENCES";
	public static final String ACTION_RADIOCHANGESTATE = "sk.henrichg.phoneprofileshelper.ACTION_RADIOCHANGESTATE";
	public static final String EXTRA_RADIOCHANGESTATE = "sk.henrichg.phoneprofileshelper.EXTRA_RADIOCHANGESTATE";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","xxx");
		
		String action = intent.getAction();
		SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","action="+action);
		
		if (action.equals (ACTION_SETPROFILEPREFERENCES))
		{
			String	procedure = intent.getStringExtra (SetProfilePreferenceService.PROCEDURE);

			SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","procedure="+procedure);
			
			// start service
			Intent serviceIntent = new Intent(context, SetProfilePreferenceService.class);
			serviceIntent.putExtra(SetProfilePreferenceService.PROCEDURE, procedure);
			if (procedure.equals(SetProfilePreferenceService.PROCEDURE_RADIO_CHANGE))
			{
				int GPSChange = intent.getIntExtra(SetProfilePreferenceService.GPS_CHANGE, 0);
				int airplaneModeChange = intent.getIntExtra(SetProfilePreferenceService.AIRPLANE_MODE_CHANGE, 0);
				int NFCChange = intent.getIntExtra(SetProfilePreferenceService.NFC_CHANGE, 0);
				int WifiChange = intent.getIntExtra(SetProfilePreferenceService.WIFI_CHANGE, 0);
				int bluetoothChange = intent.getIntExtra(SetProfilePreferenceService.BLUETOOTH_CHANGE, 0);
				int mobileDataChange = intent.getIntExtra(SetProfilePreferenceService.MOBILE_DATA_CHANGE, 0);

				SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","GPSChange="+GPSChange);
				SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","airplaneModeChange="+airplaneModeChange);
				SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","NFCChange="+NFCChange);
				SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","WifiChange="+WifiChange);
				SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","bluetoothChange="+bluetoothChange);
				SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","mobileDataChange="+mobileDataChange);
				serviceIntent.putExtra(SetProfilePreferenceService.GPS_CHANGE, GPSChange);
				serviceIntent.putExtra(SetProfilePreferenceService.AIRPLANE_MODE_CHANGE, airplaneModeChange);
				serviceIntent.putExtra(SetProfilePreferenceService.NFC_CHANGE, NFCChange);
				serviceIntent.putExtra(SetProfilePreferenceService.WIFI_CHANGE, WifiChange);
				serviceIntent.putExtra(SetProfilePreferenceService.BLUETOOTH_CHANGE, bluetoothChange);
				serviceIntent.putExtra(SetProfilePreferenceService.MOBILE_DATA_CHANGE, mobileDataChange);
			}
			startWakefulService(context, serviceIntent);
		}		
		
		if (action.equals (ACTION_RADIOCHANGESTATE))
		{
			ReceiversService.radioStateChange = intent.getBooleanExtra(EXTRA_RADIOCHANGESTATE, false);
			SystemRoutines.logE("SetProfilePreferenceBroadcastReceiver.onReceive","radioStateChange="+ReceiversService.radioStateChange);
		}
	}

}
