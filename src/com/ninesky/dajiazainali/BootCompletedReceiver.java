package com.ninesky.dajiazainali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
