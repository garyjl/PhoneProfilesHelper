package sk.henrichg.phoneprofileshelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		SystemRoutines.logE("BootUpReceiver.onReceive", "xxx");
		
		// start ReceiverService
		context.startService(new Intent(context.getApplicationContext(), ReceiversService.class));

	}

}
