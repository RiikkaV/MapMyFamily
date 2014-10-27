
package riikka.mapmyfamily;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;
 
/**
 * 
 * @author Riikka Vuorio
 * BootCompletedReceiver class handles starting  of the MapMyFamilyService 
 * on Android boot. onReceive method is called when phone boot is completed.
 */
public class BootCompletedReceiver extends BroadcastReceiver{
	
	public static final String LOG_TAG = "MapMyFamilyService";
     
	@Override
     public void onReceive(Context context, Intent intent) {
 
    	 if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
    	 {
    		 // start the MapMyFamilyService            
    		 Intent serviceIntent = new Intent(context, MapMyFamilyService.class);
    		 context.startService(serviceIntent);
    		 
    		 Log.d(LOG_TAG, "MapMyFamily:BootCompleted");
    		 
    		 // create location manager
    		 MapMyFamilyLocationManager locManager = new MapMyFamilyLocationManager( context );
    		 locManager.StartTracking();
    	 }
     }
}