package riikka.mapmyfamily;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * An adapter to populate fragments inside of a ViewPager.
 * Holds fragments and saves fragment's state.
 * 
 * @author RiikkaV
 *
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {
	 private ArrayList<Fragment> mFragments=null;
	  
	 public FragmentAdapter(FragmentManager fm) {
		 super(fm);
	 }
	  
	 @Override
	 public Fragment getItem( int i ) {
		 if( null != mFragments && i < mFragments.size() ){
			 return mFragments.get( i );
		 }
		 return null;
	 }
  
	 @Override
	 public int getCount() {
		 return ((null == mFragments) ? 0 : mFragments.size());
	 }
	 
	 /**
	  * NOTE! In order to view fresh to work this must be implemented
	  * and explicitly return POSITION_NONE!
	  */
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
	 
	 public void setFragments( ArrayList<Fragment> fragments ){
		 mFragments=fragments;
	 }
}
