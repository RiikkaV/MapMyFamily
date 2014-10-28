/*
 * Copyright (C) 2013
 *
 */

package riikka.mapmyfamily;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;



/**
 * Background service which is started after phone booting.
 * Sends coordinates to the MapMyFamily server whenever needed.
 * 
 * TODO:
 * - check network connection -> ask user to enable wifi or mobile data if not enabled
 * - get server URI from settings
 * - add location string to resources ("mapmyfamily.Location")
 */
public class MapMyFamilyService extends Service {

    public static final String LOG_TAG = "MapMyFamilyService";
    private ServiceObserver observer = null;
    private ConnectivityManager connMgr = null;
    private WebSocketConnection mConnection = new WebSocketConnection();
    ConnectivityManager mConnManager;
    private final String wsuri = "ws://XXXXXXXXXXXXXXX";
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
 
    	connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectSocket();
        return START_STICKY;
    }
    
    private void connectSocket(){
    	if( false == networkConnection() ){
            if( null != observer ){
          	  observer.networkConnectionDisabled();
            }
    		return;
    	}
        try{
            mConnection.connect(wsuri, new WebSocketHandler() {
                       @Override
                       public void onOpen() {
                          if( null != observer ){
                        	  observer.socketConnectionStatusChanged( "Connected ");
                          }
                       }
            
                       @Override
                       public void onTextMessage(String payload) {
                           if( null != observer && payload.startsWith("mapmyfamily.Location")){
                         	   // TODO: response from server?
                        	   //observer.locationSentToServer();
                           }
                       }
            
                       @Override
                       public void onClose(int code, String reason) {
                           if( null != observer ){
                         	  observer.socketConnectionStatusChanged( "Closed ");
                           }
                       }
                   });
           	} catch (WebSocketException e) {
           		Log.d(LOG_TAG, e.toString()); 
           	}  	
    }
    
    private boolean networkConnection() {
    	boolean status = false;
    	
    	NetworkInfo networkInfoWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
    	NetworkInfo networkInfoMobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	
    	if( true == networkInfoMobile.isConnected() || true == networkInfoWifi.isConnected() ){
    		status = true;
    	}

		return status;
	}


	@Override
    public void onDestroy() {
        if (mConnection.isConnected()) {
            mConnection.disconnect();
         }
    }

	
    protected void sendData( String message ) {

    	if ( mConnection == null || mConnection.isConnected() == false) {
    		connectSocket();
    	    return;
    	    }
    	
    	mConnection.sendTextMessage( message );
    }

	public void setObserver(ServiceObserver serviceObserver) {
		observer = serviceObserver;
		
	}

}
