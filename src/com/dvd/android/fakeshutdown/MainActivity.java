package com.dvd.android.fakeshutdown;

import com.dvd.android.fakeshutdown.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

	static final int RESULT_ENABLE = 1;

	DevicePolicyManager deviceManger;
	ActivityManager activityManager;
	ComponentName compName;
	private static int myProgress;
	private ProgressDialog progressDialog;
	private final Handler myHandler = new Handler();
	private int progressStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PendingIntent RESTART_INTENT = PendingIntent.getActivity(this
				.getBaseContext(), 0, new Intent(getIntent()), getIntent()
				.getFlags());

		deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		compName = new ComponentName(this, MyAdmin.class);

		boolean active = deviceManger.isAdminActive(compName);
		if (active) {
			if (Build.VERSION.SDK_INT == 20) {
				start();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);

				alertDialogBuilder.setTitle(getApplicationContext().getString(
						R.string.app_name));
				alertDialogBuilder.setMessage(getApplicationContext()
						.getString(R.string.warn));

				alertDialogBuilder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

								start();

							}
						});

				alertDialogBuilder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.setCancelable(false);
				alertDialog.show();
			}
		}

		else {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					(this.getString(R.string.perm)));
			startActivityForResult(intent, RESULT_ENABLE);
			AlarmManager mgr = (AlarmManager) this
					.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
					RESTART_INTENT);
			System.exit(2);
			finish();
		}

	}

	public void start() {
		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setCancelable(true);

		progressDialog.setTitle(getString(R.string.title_activity_shutdown));

		progressDialog.setMessage(getString(R.string.descr));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.show();
		progressStatus = 0;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (progressStatus < 10) {
					progressStatus = performTask();

				}
				myHandler.post(new Runnable() {

					@Override
					public void run() {

						progressDialog.dismiss();

						progressStatus = 0;
						myProgress = 0;
						deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
						deviceManger.lockNow();

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								MainActivity.this);

						alertDialogBuilder.setTitle(getApplicationContext()
								.getString(R.string.hi));
						alertDialogBuilder.setMessage(getApplicationContext()
								.getString(R.string.cred));
						alertDialogBuilder.setPositiveButton(
								android.R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										finish();
									}
								});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.setCancelable(false);
						alertDialog.show();

					}
				});

			}

			private int performTask() {
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return ++myProgress;
			}
		}).start();
	}
}