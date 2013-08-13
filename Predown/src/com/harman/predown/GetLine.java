package com.harman.predown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.GoogleRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GetLine extends Activity implements LocationListener {

	private String sessionid;
	private String ipaddress;
	private String ports;
	private String mail;
	private Button getline;
	private Button search;
	private EditText destinationEdit;
	protected GeoPoint startPoint, destinationPoint;
	protected Road mRoad;
	protected String destname;
	private String providers;
	private ProgressDialog dlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		providers = locationManager.getBestProvider(criteria, true);

		if (providers != null) {
			locationManager.requestLocationUpdates(providers, 1000, 0, this,
					Looper.myLooper());
		}

		sessionid = this.getIntent().getBundleExtra("SESSIONID")
				.getString("sessionid");
		ipaddress = this.getIntent().getBundleExtra("SESSIONID")
				.getString("IPADDRESS");
		ports = this.getIntent().getBundleExtra("SESSIONID").getString("PORT");
		mail = this.getIntent().getBundleExtra("SESSIONID").getString("MAIL");
		if (savedInstanceState == null) {
			Location l = locationManager.getLastKnownLocation(providers);
			if (l != null) {
				startPoint = new GeoPoint(l.getLatitude(), l.getLongitude());
			} else {
				// we put a hard-coded start
				startPoint = new GeoPoint(31.15556, 121.42483);
			}
			destinationPoint = null;
		} else {
			startPoint = savedInstanceState.getParcelable("start");
			destinationPoint = savedInstanceState.getParcelable("destination");
		}
		initView();

	}

	private void initView() {
		TextView user = (TextView) findViewById(R.id.mapuser);
		user.setText(mail);

		getline = (Button) findViewById(R.id.getline);
		search = (Button) findViewById(R.id.search_address);
		getline.setOnClickListener(getlineClickListener);
		search.setOnClickListener(searchdestListener);
		destinationEdit = (EditText) findViewById(R.id.destination);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	OnClickListener searchdestListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(destinationEdit.getWindowToken(), 0);
			handleSearch();
		}
	};

	OnClickListener getlineClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// check the value
			getRoadAsync();
		}
	};

	private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {

		@Override
		protected void onPostExecute(Road result) {
			mRoad = result;
			// send road to server
			Log.i("TEST", mRoad.toString());
			if (dlg != null && dlg.isShowing()) {
				dlg.dismiss();
				dlg = null;
			}
			jumpMain();
		}

		@Override
		protected Road doInBackground(Object... params) {
			ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
			RoadManager roadManager = new GoogleRoadManager();
			// RoadManager roadManager = new OSRMRoadManager();
			/*
			 * RoadManager roadManager = new MapQuestRoadManager(); Locale
			 * locale = Locale.getDefault();
			 * roadManager.addRequestOption("locale="
			 * +locale.getLanguage()+"_"+locale.getCountry());
			 */
			return roadManager.getRoad(waypoints);
		}
	}

	protected void handleSearch() {

		String destinationAddress = destinationEdit.getText().toString();
		new GetSearchResult().execute(destinationAddress);

	}

	public void jumpMain() {
		
		Intent maintent = new Intent(this, TabMain.class);
		startActivity(maintent);
		this.finish();
	}

	private class GetSearchResult extends AsyncTask<String, Void, GeoPoint> {

		@Override
		protected void onPostExecute(GeoPoint result) {
			Log.i("TEST", result.toDoubleString());
		}

		@Override
		protected GeoPoint doInBackground(String... params) {

			GeocoderNominatim geocoder = new GeocoderNominatim(getBaseContext());
			geocoder.setOptions(true); // ask for enclosing polygon (if any)
			try {
				List<Address> foundAdresses = geocoder.getFromLocationName(
						params[0], 1);
				Log.i("TEST", foundAdresses.size() + "");
				if (foundAdresses.size() == 0) { // if no address found, display
													// an error
				} else {
					Address address = foundAdresses.get(0); // get first address
					destinationPoint = new GeoPoint(address.getLatitude(),
							address.getLongitude());
					// Bundle extras = address.getExtras();
					// if(extras != null && extras.containsKey("display_name")){
					// destname = extras.getString("display_name");
					// Log.d("DEBUG",
					// "display_name:"+extras.getString("display_name"));
					// }
					// if (extras != null &&
					// extras.containsKey("polygonpoints")){
					// ArrayList<GeoPoint> polygons =
					// extras.getParcelableArrayList("polygonpoints");
					// Log.d("DEBUG", "polygon:"+polygons.size());
					// }
				}
			} catch (IOException e) {
			}
			return destinationPoint;
		}
	}

	protected void getRoadAsync() {
		mRoad = null;
		if (startPoint == null || destinationPoint == null) {
			Toast.makeText(this, "Set start or dest point error",
					Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>(2);
		waypoints.add(startPoint);
		waypoints.add(destinationPoint);
		dlg = ProgressDialog.show(this,
				"Getting Route line and send to server ",
				"Please wait for a second", true);
		// dlg.setCancelable(true);
		new UpdateRoadTask().execute(waypoints);
	}

	@Override
	public void onLocationChanged(Location ll) {
		// re-get the road
		startPoint = new GeoPoint(ll.getLatitude(), ll.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
