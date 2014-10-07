
package com.ninesky.dajiazainali;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClientOption.LocationMode;

public class CountService extends Service {

    public LocationClient mLocationClient = null;

    private String mLastLocation = "";
    private String mLocation = "";
    private String mAddress = "";
    private ReportLocationThread mThread;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        InitLocation();
        mLocationClient.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("CountService", "on destroy");
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 5000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocation = location.getLongitude() + "," + location.getLatitude();
            mAddress = location.getAddrStr();
            Log.i("dwtedx", mAddress);
            if (mThread == null) {
                mThread = new ReportLocationThread();
                mThread.start();
            }
        }
    }

    private class ReportLocationThread extends Thread {

        @Override
        public void run() {

            if (TextUtils.equals(mLastLocation, mLocation)) {
                //do not report duplicated location.
                return;
            }
            mLastLocation = mLocation;

            Intent intent = new Intent();
            intent.putExtra("locStr", mAddress);
            intent.setAction("com.ljq.activity.CountService");
            sendBroadcast(intent);

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String theImei = DeviceHelper.getIME(CountService.this);
            params.add(new BasicNameValuePair("imei", theImei));
            params.add(new BasicNameValuePair("loc", mLocation));
            params.add(new BasicNameValuePair("jsoncallback",
                    "jQuery110108208465098869056_1411286271256"));

            String param = URLEncodedUtils.format(params, "UTF-8");

            String baseUrl = "http://112.124.6.192:8000/message/new";

            HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
            HttpClient httpClient = new DefaultHttpClient();

            try {
                HttpResponse response = httpClient.execute(getMethod);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mThread = null;
        }

    }

}
