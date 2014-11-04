/*
 * Copyright (C) 2013
 *
 */

package riikka.mapmyfamily;


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
 * - get server URI from settings
 * - secure websocket connection??
 */
public class MapMyFamilyService extends Service {

    public static final String LOG_TAG = "MapMyFamilyService";
    private ServiceObserver observer = null;
    private ConnectivityManager connMgr = null;
    private WebSocketConnection mConnection = new WebSocketConnection();
    ConnectivityManager mConnManager;
    // for debug, localhost
    //private final String wsuri = "ws://10.0.2.2:5000";
    private final String wsuri = "ws://mapmyfamily.herokuapp.com";
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
          	  observer.socketConnectionStatusChanged( NetworkStatus.NO_CONNECTION );
            }
    		return;
    	}
        try{
            mConnection.connect(wsuri, new WebSocketHandler() {
                       @Override
                       public void onOpen() {
                          if( null != observer ){
                        	  observer.socketConnectionStatusChanged( NetworkStatus.CONNECTED );
                          }
                          // TODO: send identification data to the server
                       }
            
                       @Override
                       public void onTextMessage(String payload) {
                           if( null != observer ){
                         	   // TODO: response from server?
                        	   observer.messageReceived( payload );
                           }
                       }
            
                       @Override
                       public void onClose(int code, String reason) {
                           if( null != observer ){
                         	  observer.socketConnectionStatusChanged( NetworkStatus.CLOSED );
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

	/**
	 * Send data to the server
	 * @param message
	 */
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
