
package com.harman.predown;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

public class HttpNet extends Thread {

    private volatile Thread runner;

    private Handler handler;

    private Context context;

    private String url;

    private boolean result;

    private String sessionid;

    private JSONObject param;
//    private byte[] param;

    private int option;

    public HttpNet(Handler handler, String url, JSONObject param, int option) {
//    public HttpNet(Handler handler, String url, byte[] param, int option) {
        this.handler = handler;
        this.url = url;
        this.param = param;
        result = false;
        this.option = option;
    }

    public HttpNet(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public synchronized void startHttp() {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void stopHttp() {
        if (runner != null) {
            runner.stop();
            runner = null;
        }
    }

    public synchronized void stopThread() {
        if (runner != null) {
            Thread moribund = runner;
            runner = null;
            moribund.interrupt();
        }
    }

    private synchronized void sendMsg(int what, int val) {
        if (Thread.currentThread() != runner) {
            return;
        }
        if (handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = what;
            Bundle bb = new Bundle();
            switch (what) {
                case Login.TRANSFER_STATUS:
                    bb.putInt(Login.KEY_TRANSFER_STATUS, val);
                    bb.putString(Login.SESSIONID, sessionid);
                    break;

            }
            msg.setData(bb);
            handler.sendMessage(msg);
        }
    }

    public void run() {
        if (option == 1)
            sessionid = "";
        DefaultHttpClient mHttpClient = new DefaultHttpClient();
        HttpPost mPost = new HttpPost(url);
        // if (option == 1 || option == 3)
        mPost.setHeader("User_Agent", String.format("%s", "Android_app"));

        StringEntity se;
        try {
            se = new StringEntity(param.toString());
            mPost.setEntity(se);

            HttpResponse response;
            response = mHttpClient.execute(mPost);
            int res = response.getStatusLine().getStatusCode();
            if (res == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String recResult;

                    recResult = EntityUtils.toString(entity);
                    if (option == 1) {
                        JSONObject jsonObject = null;
                        boolean resultd = false;
                        try {
                            jsonObject = new JSONObject(recResult);
                            resultd = jsonObject.getBoolean("result");
                            sessionid = jsonObject.getString("data");
                            System.out.println("receive data-----------" + result + "---"
                                    + recResult);
                            if (resultd)
                                result = true;
                            else
                                result = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("LOGIN", "JSON EXCEPTION");
                            sendMsg(Login.TRANSFER_STATUS, Login.CONNECT_FAILED);
                        }
                    } else if (option == 2) {
                        result = true;
                    }

                } else {
                    result = false;
                }
            }
        } catch (UnsupportedEncodingException e3) {
            if (option == 1)
                sendMsg(Login.TRANSFER_STATUS, Login.CONNECT_FAILED);
        } catch (ClientProtocolException e2) {
            if (option == 1)
                sendMsg(Login.TRANSFER_STATUS, Login.CONNECT_FAILED);
            e2.printStackTrace();
        } catch (IOException e2) {
            if (option == 1)
                sendMsg(Login.TRANSFER_STATUS, Login.CONNECT_FAILED);
            e2.printStackTrace();
        }

        if (Thread.currentThread() == runner) {
            if (result) {
                if (option == 1)
                    sendMsg(Login.TRANSFER_STATUS, Login.CONNECT_SUCCEED);

            } else {
                if (option == 1)
                    sendMsg(Login.TRANSFER_STATUS, Login.CONNECT_FAILED);
            }
        }
    }
}
