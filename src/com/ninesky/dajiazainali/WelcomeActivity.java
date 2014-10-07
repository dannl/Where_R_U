/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    Where_R_U
 *
 *    WelcomeActivity
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Oct 7, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.ninesky.dajiazainali;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * WelcomeActivity of Where_R_U.
 * @author danliu
 *
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                final Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

}
