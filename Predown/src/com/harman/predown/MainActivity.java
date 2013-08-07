package com.harman.predown;

import com.harman.predown.views.InfoFragment;
import com.harman.predown.views.SettingFragment;
import com.harman.predown.views.VideoFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private VideoFragment mFragment1 = new VideoFragment();
	private InfoFragment mFragment2 = new InfoFragment();
	private SettingFragment mFragment3 = new SettingFragment();

	private static final int TAB_INDEX_COUNT = 3;

	private static final int TAB_INDEX_ONE = 0;
	private static final int TAB_INDEX_TWO = 1;
	private static final int TAB_INDEX_THREE = 2;

	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// action bar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setUpActionBar();
		setUpViewPager();
		setUpTabs();
	}

	private void setUpActionBar() {
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	private void setUpViewPager() {
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						final ActionBar actionBar = getActionBar();
						actionBar.setSelectedNavigationItem(position);
					}

					@Override
					public void onPageScrollStateChanged(int state) {
						switch (state) {
						case ViewPager.SCROLL_STATE_IDLE:
							// TODO
							break;
						case ViewPager.SCROLL_STATE_DRAGGING:
							// TODO
							break;
						case ViewPager.SCROLL_STATE_SETTLING:
							// TODO
							break;
						default:
							// TODO
							break;
						}
					}
				});
	}

	private void setUpTabs() {
		final ActionBar actionBar = getActionBar();
		for (int i = 0; i < mViewPagerAdapter.getCount(); ++i) {
			actionBar.addTab(actionBar.newTab()
					.setText(mViewPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			switch (position) {
			// video
			case TAB_INDEX_ONE:
				return mFragment1;

				// info
			case TAB_INDEX_TWO:
				return mFragment2;

				// setting
			case TAB_INDEX_THREE:
				return mFragment3;
			}
			throw new IllegalStateException("No fragment at position "
					+ position);
		}

		@Override
		public int getCount() {
			return TAB_INDEX_COUNT;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String tabLabel = null;
			switch (position) {
			case TAB_INDEX_ONE:
				tabLabel = getString(R.string.tab_1);
				break;
			case TAB_INDEX_TWO:
				tabLabel = getString(R.string.tab_2);
				break;
			case TAB_INDEX_THREE:
				tabLabel = getString(R.string.tab_3);
				break;
			}
			return tabLabel;
		}
	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
		mViewPager.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {

	}
}