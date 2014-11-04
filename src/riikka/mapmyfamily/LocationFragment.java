package riikka.mapmyfamily;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Fragment for location information.
 * 
 * @author RiikkaV
 *
 */

public class LocationFragment extends Fragment {
	private final String LOG_TAG = "MapMyFamilyLocationFragment";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        return inflater.inflate(R.layout.location_fragment, container, false);
    }

    public void updatecurrentLocation( JSONObject message ){
    	this.parseJSONMessage( message );
    }
    
    private void parseJSONMessage( JSONObject message ){
        TextView locationPos = (TextView) getActivity().findViewById( R.id.currentLocation );
        TextView locationDate = (TextView) getActivity().findViewById( R.id.LocationDate );
        TextView locationTime = (TextView) getActivity().findViewById( R.id.locationTime);
        TextView locationProvider = (TextView) getActivity().findViewById( R.id.locationProvider);
        TextView locationAccuracy = (TextView) getActivity().findViewById( R.id.accuracy);
        TextView locationAccuracyText = (TextView) getActivity().findViewById( R.id.accuracyText);
        
       
    	Object longitude=null;
    	Object latitude=null;
    	Object timestamp=null;
    	Object provider=null;
    	Object accuracy=null;
    	  
    	try{
    		longitude = message.get( "latitude" );
    		latitude = message.get( "longitude" );
    		timestamp = message.get( "time" );
    		provider = message.get("locationProvider");
    		accuracy = message.get("accuracy");
    		
    	}catch ( JSONException e){
    		Log.i(LOG_TAG, "JSON exception");
    	}

    	if (longitude != null && null != latitude && null != timestamp) {
    		
    		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    		String localDate = parseUTCTime( timestamp.toString(), format );
    		format.applyPattern( "HH:mm" );
    		String localTime = parseUTCTime( timestamp.toString(), format );
    		
    		locationPos.setText(  longitude.toString() + "," + latitude.toString() );
    		locationDate.setText( localDate );
    		locationTime.setText( localTime );
    	}
    	if( provider != null ){
    		String currentProvider = provider.toString();
    		String newText=null;
    		if( null != currentProvider && currentProvider.equals("gps") ){
    			newText = (String)getActivity().getString( R.string.gpsLocationProvider );
    		}else{
    			newText = (String)getActivity().getString( R.string.networkLocationProvider );
    		}
    		locationProvider.setText( newText );
    	}
    	if( accuracy != null ){
    		locationAccuracy.setText( accuracy.toString() );
    		String accuracyText = (String)getActivity().getString( R.string.accuracyMeters );
    		locationAccuracyText.setText( accuracyText );
    	}
    }
    
	private String parseUTCTime( String timestamp, SimpleDateFormat format ) {

		Date date = new Date( Long.parseLong(timestamp));
		String formattedDate = format.format(date);
		
		return formattedDate;
	}


	public void locationProviderChanged(String event) {
		// TODO Auto-generated method stub
		
	}

    

}
