package com.harman.predown;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	static final String TAG = "Login";

	public static final String KEY_SERVER_IP = "ip";

	static final String KEY_SERVER_PORT = "port";

	public static final int GET_GPS_STATUS = 0;

	public static final String KEY_GET_STATUS = "get gps status";

	public static final String KEY_LOGFILE = "log file";

	public static final int TRANSFER_STATUS = 1;

	public static final String KEY_TRANSFER_STATUS = "httpstatus";

	public static final String SESSIONID = "sessionid";

	public static final int CONNECT_FAILED = 0;

	public static final int CONNECT_SUCCEED = 1;

	private boolean autoReconnect;

	private EditText passwd;

	private EditText emailaddress;
	
	private String email;

	private EditText ip;

	private EditText port;

	private String ipadd;

	private String portstring;

	ProgressDialog dlg;

	private String sessionid;

	private String imei;

	private String devicemode;

	private String devicename;

	private String devicedescription;

	private String logpath;

	PopupWindow popWindow;

	private int width;

	private int height;

	private HttpNet httpConnect;

	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	private final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {

			case TRANSFER_STATUS:
				if (message.getData().getInt(KEY_TRANSFER_STATUS) == CONNECT_SUCCEED) {
					sessionid = message.getData().getString(SESSIONID);
					if (dlg != null && dlg.isShowing()) {
						dlg.dismiss();
						dlg = null;
					}
					loginScc();

				}
				if (message.getData().getInt(KEY_TRANSFER_STATUS) == CONNECT_FAILED) {
					if (dlg != null && dlg.isShowing()) {
						dlg.dismiss();
						dlg = null;
					}
					loginfailed();
				}
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Button bLogin = (Button) findViewById(R.id.login);
		emailaddress = (EditText) findViewById(R.id.username);
		passwd = (EditText) findViewById(R.id.passwd);
		ip = (EditText) findViewById(R.id.ip);
		port = (EditText) findViewById(R.id.port);

		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei = mTelephonyMgr.getDeviceId();
		devicedescription = mTelephonyMgr.getDeviceSoftwareVersion();
		// devicemode = Build.MANUFACTURER.toLowerCase();
		devicemode = android.os.Build.MODEL;
		devicename = android.os.Build.DEVICE;
		mTelephonyMgr = null;
		Log.i(TAG, "deviceID got :" + imei);

		WindowManager manager = getWindowManager();
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();

		bLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startLoginActivity();
			}
		});

	}


	private String readLog(String logpathds) {
		String content = "";
		try {
			InputStream instream = new FileInputStream(logpathds);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					content += line + "\n";
				}
				instream.close();
			}
		} catch (Exception e) {
		}
		return content;
	}

	private boolean checkInput() {
		boolean portEmpty = TextUtils.isEmpty(ipadd);
		boolean ipempty = TextUtils.isEmpty(portstring);
		return (!portEmpty && !ipempty);
	}

	protected void loginScc() {
		Toast.makeText(this, "Login Succed", Toast.LENGTH_SHORT).show();
		if (httpConnect != null) {
			httpConnect.stopThread();
			httpConnect = null;

		}
		Intent intent = new Intent(this, GetLine.class);
		Bundle map = new Bundle();
		map.putString("sessionid", sessionid);
		map.putString("IPADDRESS", ipadd);
		map.putString("PORT", portstring);
		map.putString("MAIL",  email);
		intent.putExtra("SESSIONID", map);
		startActivity(intent);
	}

	protected void loginfailed() {
		Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
		if (httpConnect != null) {
			httpConnect.stopThread();
			httpConnect = null;

		}
		dlg = ProgressDialog.show(this, "GPS Info", "Getting ....");
		dlg.setCancelable(true);
	}

	protected void startLoginActivity() {

		if (ip != null && port != null) {
			ipadd = ip.getText().toString();
			portstring = port.getText().toString();
			Log.i("TAG", ipadd + portstring);
		}
		if (!checkInput()) {
			Toast.makeText(this,
					"Please do not let the port or ip to be empty!",
					Toast.LENGTH_SHORT).show();
			// return;
		} else if (!checkUrl(ipadd)) {
			Toast.makeText(this, "IP Address is not correct!",
					Toast.LENGTH_SHORT).show();

		} else
			checkUser();
	}

	private void checkUser() {

		dlg = ProgressDialog.show(this, "Logining", "Please wait for a second");
		email = emailaddress.getText().toString();
		String passwds = passwd.getText().toString();
		String url = ipadd + ":" + portstring + "/tracking/api/login";

		JSONObject param = new JSONObject();
		try {
			param.put("account.email", email);
			param.put("account.pwd", passwds);
			param.put("device.id", imei);
			param.put("device.name", devicename);
			param.put("device.model", devicemode);
			param.put("device.description", devicedescription);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// try {
		// byte[] bytes = MessagePack.pack(param);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		httpConnect = new HttpNet(handler, url, param, 1);
		httpConnect.startHttp();

	}

	private boolean checkUrl(String ipadds) {
		String tmpstring = ipadds.substring(ipadds.lastIndexOf("/") + 1);
		Pattern p = Pattern.compile("(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})"
				+ "\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})"
				+ "\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})"
				+ "\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})");
		if (tmpstring != null) {
			Matcher m = p.matcher(tmpstring);
			if (m.matches()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (dlg != null && dlg.isShowing()) {
				dlg.dismiss();
				dlg = null;
			}
			if (httpConnect != null) {
				httpConnect.stopThread();
				httpConnect = null;
			}
			finish();
			System.exit(0);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
