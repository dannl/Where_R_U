/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    Where_R_U
 *
 *    DeviceHelper
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Oct 6, 2014
 *    @version: 1.0
 *
 ******************************************************************************/

package com.ninesky.dajiazainali;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * DeviceHelper of Where_R_U.
 *
 * @author danliu
 */
public class DeviceHelper {

    public static final String getIME(final Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

}
