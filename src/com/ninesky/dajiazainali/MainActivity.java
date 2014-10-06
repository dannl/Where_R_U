package com.ninesky.dajiazainali;

import com.baidu.location.LocationClient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.EditText;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {
     private EditText editText=null;
     private MyReceiver receiver=null;

     public LocationClient mLocationClient=null;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)findViewById(R.id.editText);
        startService(new Intent(MainActivity.this, CountService.class));


        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.ljq.activity.CountService");
        MainActivity.this.registerReceiver(receiver,filter);


    }


    @Override
     protected void onDestroy() {
            stopService(new Intent(MainActivity.this, CountService.class));
            super.onDestroy();
     }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
         Bundle bundle=intent.getExtras();
         String locStr=bundle.getString("locStr");
         editText.setText(locStr);

        }
    }
    /*
    public class BootCompletedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Toast.makeText(context, "starting...",Toast.LENGTH_SHORT).show();
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                Intent newIntent=new Intent(context,CountService.class);
                context.startService(newIntent);
            }
        }
    }
    */

}
