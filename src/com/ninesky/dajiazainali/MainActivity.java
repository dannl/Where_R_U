
package com.ninesky.dajiazainali;

import com.baidu.location.LocationClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends ActionBarActivity {
    private TextView mLocationTxtView = null;
    private TextView mHintTxtView = null;
    private MyReceiver mLocationReciever = null;

    public LocationClient mLocationClient = null;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean isFirstTimeToStart = preferences.getBoolean("PREF_IS_FIRST_TIME", true);
        if (isFirstTimeToStart) {
            preferences.edit().putBoolean("PREF_IS_FIRST_TIME", false).apply();
            final Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mLocationTxtView = (TextView) findViewById(R.id.location);
        mHintTxtView = (TextView) findViewById(R.id.hint);
        startService(new Intent(MainActivity.this, CountService.class));

        mLocationReciever = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ljq.activity.CountService");
        MainActivity.this.registerReceiver(mLocationReciever, filter);
        checkUpdate();
        setupContent();
    }

    private void checkUpdate() {
        int versionCode = -1;
        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e1) {
        }
        String requestUrl = Constants.CHECKUPDATE + "?client_v=" + versionCode;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(requestUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response != null) {
                    try {
                        //这里要保证不同版本的APK的下载链接是不同的，客户端将通过下载链接判断是否已经下载过该APK，防止重复下载。
                        //请在更换Server上的APK时，将
                        //apk的版本名写到文件名中以保证下载链接不同。
                        final String downloadUrl = response.getString("download_path");
                        if (!TextUtils.isEmpty(downloadUrl)) {
                            //download the apk and install.
                            downloadAndInstall(downloadUrl);
                        }
                    } catch (Exception e) {
                    }
                } else {
                    //no update.
                }
            }
        });
    }

    private void downloadAndInstall(final String url) {
        final File tempApkFile = new File(getExternalCacheDir(), String.valueOf(url).hashCode() + ".apk");
        if (tempApkFile.exists()) {
            install(tempApkFile);
            return;
        }
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(url, new FileAsyncHttpResponseHandler(tempApkFile) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (file.exists()) {
                    install(tempApkFile);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            }
        });
    }

    private void install(File tempApkFile) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(tempApkFile), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void setupContent() {
        setLocation("");
        final String IMEI = DeviceHelper.getIME(this);
        mHintTxtView.setText(getString(R.string.hint_template, IMEI));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, CountService.class));
        super.onDestroy();
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String locStr = bundle.getString("locStr");
            setLocation(locStr);
        }
    }

    private void setLocation(final String text) {
        final String location = getString(R.string.template, text);
        mLocationTxtView.setText(location);
    }
    /*
     * public class BootCompletedReceiver extends BroadcastReceiver{
     * @Override public void onReceive(Context context, Intent intent) { // TODO
     * Auto-generated method stub Toast.makeText(context,
     * "starting...",Toast.LENGTH_SHORT).show();
     * if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){ Intent
     * newIntent=new Intent(context,CountService.class);
     * context.startService(newIntent); } } }
     */

}
