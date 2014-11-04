
package riikka.mapmyfamily;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;
 
/**
 * 
 * BootCompletedReceiver class handles starting  of the MapMyFamilyService 
 * on Android boot. onReceive method is called when phone boot is completed.
 * TODO:
 * starts the process but current implementation actually requires activity
 * 
 */
public class BootCompletedReceiver extends BroadcastReceiver{
	
	public static final String LOG_TAG = "MapMyFamilyBootReceiver";
     
	@Override
     public void onReceive(Context context, Intent intent) {
 
    	 if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
    	 {
    		 // start the MapMyFamilyActivity
    		 Intent serviceIntent = new Intent(context, MapMyFamilyActivity.class);
    		 serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		 context.startActivity(serviceIntent);
    		 
    		 Log.d(LOG_TAG, "BootCompleted");
    	 }
     }
}