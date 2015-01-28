package sk.henrichg.phoneprofileshelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.stericson.RootTools.RootTools;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;


public class SystemRoutines {

	public static boolean logIntoLogCat = true;
	public static boolean logIntoFile = false;
	private static boolean rootToolsDebug = false;
	public static String logFilterTags = "BootUpReceiver|"+
			                             "ReceiversService|"+
										 "SetProfilePreferenceBroadcastReceiver|"+
			                             "SetProfilePreferenceService";
	
	public static final String EXPORT_PATH = "/PhoneProfilesHelper";
	public static final String LOG_FILENAME = "log.txt";
	
	static boolean isSystemApp(Context context)
	{
		ApplicationInfo ai = context.getApplicationInfo();
		
		if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
		{
			//Log.d(TAG, "isSystemApp==true");
			return true;
		}
		return false;
	}
	
	static boolean hasWriteSecurePermission(Context context)
	{
	    String permission = Manifest.permission.WRITE_SECURE_SETTINGS;
	    int res = context.checkCallingOrSelfPermission(permission);
	    return (res == PackageManager.PERMISSION_GRANTED);            
	}
    
	static private boolean isSELinuxEnforcingChecked = false;
	static private boolean isSELinuxEnforcing = false;
	//static private String suVersion = null;
	//static private boolean suVersionChecked = false;
	
    /**
     * Detect if SELinux is set to enforcing, caches result
     * 
     * @return true if SELinux set to enforcing, or false in the case of
     *         permissive or not present
     */
    public static boolean isSELinuxEnforcing()
    {
    	RootTools.debugMode = rootToolsDebug;
    	
        if (!isSELinuxEnforcingChecked)
        {
            boolean enforcing = false;

            // First known firmware with SELinux built-in was a 4.2 (17)
            // leak
            if (android.os.Build.VERSION.SDK_INT >= 17)
            {
                // Detect enforcing through sysfs, not always present
                File f = new File("/sys/fs/selinux/enforce");
                if (f.exists())
                {
                    try {
                        InputStream is = new FileInputStream("/sys/fs/selinux/enforce");
                        try {
                            enforcing = (is.read() == '1');
                        } finally {
                            is.close();
                        }
                    } catch (Exception e) {
                    }
                }

                /*
                // 4.4+ builds are enforcing by default, take the gamble
                if (!enforcing)
                {
                    enforcing = (android.os.Build.VERSION.SDK_INT >= 19);
                }
                */
            }

            isSELinuxEnforcing = enforcing;
            isSELinuxEnforcingChecked = true; 
            
        }
        
		logE("GlobalData.isSELinuxEnforcing", "isSELinuxEnforcing="+isSELinuxEnforcing);
        
        return isSELinuxEnforcing;
    }
    
    /*
    public static String getSELinuxEnforceCommand(String command, Shell.ShellContext context)
    {
    	if ((suVersion != null) && suVersion.contains("SUPERSU"))
    		return "su --context " + context.getValue() + " -c \"" + command + "\"  < /dev/null";
    	else
    		return command;
    }

    public static String getSUVersion()
    {
    	if (!suVersionChecked)
    	{
	    	Command command = new Command(0, false, "su -v")
	    	{
	    		@Override
	    		public void commandOutput(int id, String line) {
	    			Log.e("GlobalData.getSUVersion","version="+line);
	    			suVersion = line;
	    			
	    			super.commandOutput(id, line);
	    		}
	    	}
	    	;
			try {
				RootTools.getShell(false).add(command);
				commandWait(command);
				//RootTools.closeAllShells();
    			suVersionChecked = true;
			} catch (Exception e) {
				Log.e("GlobalData.getSUVersion", "Error on run su");
			}
    	}
    	return suVersion;
    }
    
    
	private static void commandWait(Command cmd) throws Exception {
        int waitTill = 50;
        int waitTillMultiplier = 2;
        int waitTillLimit = 3200; //7 tries, 6350 msec

        while (!cmd.isFinished() && waitTill<=waitTillLimit) {
            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(waitTill);
                        waitTill *= waitTillMultiplier;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!cmd.isFinished()){
            Log.e("GlobaData.commandWait", "Could not finish root command in " + (waitTill/waitTillMultiplier));
        }
    }
    */    
    
	//--------------------------------------------------------------
	
	static private void resetLog()
	{
		File sd = Environment.getExternalStorageDirectory();
		File exportDir = new File(sd, EXPORT_PATH);
		if (!(exportDir.exists() && exportDir.isDirectory()))
			exportDir.mkdirs();
		
		File logFile = new File(sd, EXPORT_PATH + "/" + LOG_FILENAME);
		logFile.delete();
	}

	@SuppressLint("SimpleDateFormat")
	static private void logIntoFile(String type, String tag, String text)
	{
		if (!logIntoFile)
			return;

		File sd = Environment.getExternalStorageDirectory();
		File exportDir = new File(sd, EXPORT_PATH);
		if (!(exportDir.exists() && exportDir.isDirectory()))
			exportDir.mkdirs();
		
		File logFile = new File(sd, EXPORT_PATH + "/" + LOG_FILENAME);

		if (logFile.length() > 1024 * 10000)
			resetLog();

	    if (!logFile.exists())
	    {
	        try
	        {
	            logFile.createNewFile();
	        } 
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    }
	    try
	    {
	        //BufferedWriter for performance, true to set append to file flag
	        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
	        String log = "";
		    SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yy HH:mm:ss:S");
		    String time = sdf.format(Calendar.getInstance().getTimeInMillis());
	        log = log + time + "--" + type + "-----" + tag + "------" + text;
	        buf.append(log);
	        buf.newLine();
	        buf.flush();
	        buf.close();
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }		
	}

	private static boolean logContainsFilterTag(String tag)
	{
		boolean contains = false;
		String[] splits = logFilterTags.split("\\|");
		for (int i = 0; i < splits.length; i++)
		{
			if (tag.contains(splits[i]))
			{
				contains = true;
				break;		
			}
		}
		return contains;
	}
	
	static public void logI(String tag, String text)
	{
		if (!(logIntoLogCat || logIntoFile))
			return;

		if (logContainsFilterTag(tag))
		{
			if (logIntoLogCat) Log.i(tag, text);
			logIntoFile("I", tag, text);
		}
	}
	
	static public void logW(String tag, String text)
	{
		if (!(logIntoLogCat || logIntoFile))
			return;

		if (logContainsFilterTag(tag))
		{
			if (logIntoLogCat) Log.w(tag, text);
			logIntoFile("W", tag, text);
		}
	}
	
	static public void logE(String tag, String text)
	{
		if (!(logIntoLogCat || logIntoFile))
			return;

		if (logContainsFilterTag(tag))
		{
			if (logIntoLogCat) Log.e(tag, text);
			logIntoFile("E", tag, text);
		}
	}

	static public void logD(String tag, String text)
	{
		if (!(logIntoLogCat || logIntoFile))
			return;

		if (logContainsFilterTag(tag))
		{
			if (logIntoLogCat) Log.d(tag, text);
			logIntoFile("D", tag, text);
		}
	}
	
	//--------------------------------------------------------------
	
	
}
