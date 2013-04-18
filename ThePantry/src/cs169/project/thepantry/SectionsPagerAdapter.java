package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A FragmentPagerAdapter that returns a fragment corresponding to
 * one of the sections
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	Context context;
	ArrayList<Fragment> fragments;

	public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> frags) {
		super(fm);
		fragments = frags;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return context.getString(R.string.title_recommended).toUpperCase(l);
		case 1:
			return context.getString(R.string.title_recent).toUpperCase(l);
		case 2:
			return context.getString(R.string.title_favorited).toUpperCase(l);
		}
		return null;
	}
}