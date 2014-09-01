package com.dvd.fakeshutdown;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {

	@Override
	public void onEnabled(Context context, Intent intent) {

		Toast.makeText(context, R.string.acc_grant, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisabled(Context context, Intent intent) {

		Toast.makeText(context, R.string.acc_rem, Toast.LENGTH_SHORT).show();

	}

}
