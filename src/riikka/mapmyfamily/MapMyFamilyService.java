/*
 * Copyright (C) 2013
 *
 */

package riikka.mapmyfamily;


import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;



/**
 * Background service which is started after phone booting.
 * Sends coordinates to the MapMyFamily server whenever needed.
 */
public class MapMyFamilyService extends Service {

    public static final String LOG_TAG = "MapMyFamilyService";
    private WebSocketConnection mConnection = new WebSocketConnection();
    ConnectivityManager mConnManager;
    // TODO: get from settings
    private final String wsuri = "ws://XXXXXXXXXXXXXXXXX";
    private final IBinder mBinder = new LocalBinder();
    
    public class LocalBinder extends Binder {
    	MapMyFamilyService getService() {
            return MapMyFamilyService.this;
        }
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
    
    
    @Override
    public void onCreate() {


    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MapMyFamilyService", "Received start id " + startId + ": " + intent);
        connectSocket();
        return START_STICKY;
    }
    
    private void connectSocket(){
        try{
            mConnection.connect(wsuri, new WebSocketHandler() {
                       @Override
                       public void onOpen() {
                          Log.d(LOG_TAG, "Status: Connected to " + wsuri);
                       }
            
                       @Override
                       public void onTextMessage(String payload) {
                          Log.d(LOG_TAG, "Got echo: " + payload);
                       }
            
                       @Override
                       public void onClose(int code, String reason) {
                          Log.d(LOG_TAG, "Connection lost.");
                       }
                   });
           	} catch (WebSocketException e) {
           		Log.d(LOG_TAG, e.toString()); 
           	}  	
    }
    
    @Override
    public void onDestroy() {
        // do we need to "clean" location manager? 
        if (mConnection.isConnected()) {
            mConnection.disconnect();
         }
    }

    protected void sendData( String location ) {

    	if ( mConnection == null || mConnection.isConnected() == false) {
    		// TODO: check connectivity - ask user to enable mobile data/wifi (notification)?
    		// try to reconnect
    		connectSocket();
    	    return;
    	    }

    	if (location.startsWith("mapmyfamily.Location")) {
    		mConnection.sendTextMessage( location );
    	} 

    }




}
