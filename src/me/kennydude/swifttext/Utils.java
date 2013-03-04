package me.kennydude.swifttext;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created with IntelliJ IDEA.
 * User: kennydude
 * Date: 02/03/13
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

	public static void setupTheme(Activity activity){

		if(activity.getResources().getBoolean(R.bool.needs_theme)){
			//To show activity as dialog and dim the background, you need to declare android:theme="@style/PopupTheme" on for the chosen activity on the manifest
			activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			WindowManager.LayoutParams params = activity.getWindow().getAttributes();
			params.height = 850; //fixed height
			params.width = 600; //fixed width
			params.alpha = 1.0f;
			params.dimAmount = 0.5f;
			activity.getWindow().setAttributes(params);

		}

	}

}
