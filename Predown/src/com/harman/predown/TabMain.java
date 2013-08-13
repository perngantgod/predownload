package com.harman.predown;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.harman.predown.MainActivity.ViewPagerAdapter;
import com.harman.predown.views.InfoFragment;
import com.harman.predown.views.SettingFragment;
import com.harman.predown.views.VideoFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class TabMain extends FragmentActivity {
	private TabHost tabHost;
	private static int currentlayout = 0;
	TextView mAttention;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintab);
		mAttention = (TextView) findViewById(R.id.attentions);
		String name = new  String("1 km ahead of the &lt;b>slower&lt;/b> download speed, please note that check important mails, etc."); 
		int count = 12345; 
		
		String format2 = String.format(getResources().getString(R.string.attention), TextUtils.htmlEncode(name),count); 
        CharSequence styledText2 = Html.fromHtml(format2); 
        mAttention.setText(styledText2);
        
		setFragment();
		changeLayout();

	}

	private void changeLayout() {
		// 根据用户的点击位置的下标显示相应的fragment
		tabHost.setCurrentTab(currentlayout);
	}

	private void setFragment() {
		// 通过组件的id初始化tabHost的实例
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		// 往tabHost分别添加fragment
		tabHost.addTab(tabHost
				.newTabSpec("main_video")
				.setIndicator(
						"Video",
						this.getResources().getDrawable(
								R.drawable.video))
				.setContent(R.id.frag_video));
		tabHost.addTab(tabHost
				.newTabSpec("main_info")
				.setIndicator(
						"Info",
						this.getResources().getDrawable(
								R.drawable.infos))
				.setContent(R.id.frag_info));
		tabHost.addTab(tabHost
				.newTabSpec("main_setting")
				.setIndicator(
						"Setting",
						this.getResources().getDrawable(
								R.drawable.settings))
				.setContent(R.id.frag_setting));
		tabHost.setCurrentTab(0);
	}

}