package com.harman.predown;


import java.util.ArrayList;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GetLine extends Activity{

	
	private String sessionid;
	private String ipaddress;
	private String ports;
	private String mail;
	private Button getline;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        sessionid = this.getIntent().getBundleExtra("SESSIONID").getString("sessionid");
        ipaddress = this.getIntent().getBundleExtra("SESSIONID").getString("IPADDRESS");
        ports = this.getIntent().getBundleExtra("SESSIONID").getString("PORT");
        mail = this.getIntent().getBundleExtra("SESSIONID").getString("MAIL");
        
        initView();
        

	}
	private void initView() {
		TextView user = (TextView)findViewById(R.id.mapuser);
		user.setText(mail);
		
		getline = (Button) findViewById(R.id.getline);
		
		getline.setOnClickListener(getlineClickListener);
		
		
		
	}
	
	@Override
    protected void onStart() {
        super.onStart();
//        enableMyLocation();
	}
	
	OnClickListener getlineClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// check the value
			RoadManager roadManager = new OSRMRoadManager();
			roadManager.addRequestOption("routeType=fastest");
			ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
	        waypoints.add(new GeoPoint(40.2, -2.3)); // start point
	        waypoints.add(new GeoPoint(48.4, -1.9)); //end point
	        Road road = roadManager.getRoad(waypoints);
			
		}};
}
