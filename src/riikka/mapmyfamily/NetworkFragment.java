package riikka.mapmyfamily;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Fragment for network information.
 * 
 * @author RiikkaV
 *
 */
public class NetworkFragment extends Fragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.network_fragment, container, false);
    }
    
    public void networkStatusChanged( NetworkStatus status){
    	TextView connectionStatus = (TextView) getActivity().findViewById( R.id.connectionToServer);
    	String text=null;
    	
    	switch( status ){
	    	case CONNECTING:{
	    		text = (String) getActivity().getString( R.string.waitingForConnection );
	    		
	    	}
	    	break;
	    	case CONNECTED:{
	    		removeProgressBar();
	    		text = (String) getActivity().getString( R.string.connected );
	    	}
	    	break;
	    	case NO_CONNECTION:{
	    		removeProgressBar();
	    		text = (String) getActivity().getString( R.string.askToEnableNetworkConnection );
	    	}
	    	break;
	    	case CLOSED:{
	    		text = (String) getActivity().getString( R.string.connectionClosed );
	    	}
	    	break;
    	}
    	connectionStatus.setText( text );
    }
    
    public void updateLastConnectionTime( String time ){
    	// TODO: do we need this?
    	//TextView connectionTime = (TextView) getActivity().findViewById( R.id.connectionTime );
    	
    }
    
    private void removeProgressBar(){
    	ProgressBar progressSpinner = (ProgressBar) getActivity().findViewById( R.id.progressSpinnerConnection );
    	if( null != progressSpinner ){
    		progressSpinner.setVisibility( ViewGroup.GONE );
    	}
    }

}
