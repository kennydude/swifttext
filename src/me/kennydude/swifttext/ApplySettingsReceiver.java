package me.kennydude.swifttext;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import me.kennydude.swifttext.R;
import me.kennydude.swifttext.SwiftTextActivity;

public class ApplySettingsReceiver extends BroadcastReceiver {
	public static final int NOTIFICATION_ID = 9232;

	public void onReceive(Context context, Intent intent) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if(sp.getBoolean("show_notify", false) == true){

			NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

			notification.setOngoing(true);

			notification.setSmallIcon(R.drawable.ic_stat_open);
			notification.setContentTitle(context.getString(R.string.app_name));
			notification.setContentText(context.getString(R.string.launch_app));
			notification.setPriority(NotificationCompat.PRIORITY_MIN);

			Intent i = new Intent( context, SwiftTextActivity.class );
			i.setAction("me.kennydude.swifttext.OPEN_AS_SELECTOR");

			notification.setContentIntent(PendingIntent.getActivity( context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT ));

			nm.notify(NOTIFICATION_ID, notification.build());

		} else{

			nm.cancel(NOTIFICATION_ID);

		}

	}
}
