
package com.ninesky.dajiazainali;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    private boolean threadDisable = false;
    MainActivity varMainActivity = new MainActivity();
    public LocationClient mLocationClient = null;
    private EditText editText = null;

    private String locStr = "";

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        InitLocation();
        mLocationClient.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadDisable) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    String theImei = DeviceHelper.getIME(CountService.this);
                    params.add(new BasicNameValuePair("imei", theImei));
                    params.add(new BasicNameValuePair("loc", locStr));
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

                    Intent intent = new Intent();
                    intent.putExtra("locStr", locStr);
                    intent.setAction("com.ljq.activity.CountService");
                    sendBroadcast(intent);
                }
            }
        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadDisable = true;
        Log.v("CountService", "on destroy");
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 5000;
        option.setScanSpan(span);
        option.setIsNeedAddress(false);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            locStr = location.getLongitude() + "," + location.getLatitude();
            Log.i("dwtedx", locStr);
        }

    }

}
