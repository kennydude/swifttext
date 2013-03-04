package me.kennydude.swifttext;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

public class InsertTextService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void start(String text){
		if(RootTools.isRootAvailable()){
			List<String> cmd = new ArrayList<String>();
			cmd.add("wait 1");
			
			if(text.contains(" ")){
				for(String bit : text.split(" ")){
					cmd.add("input text " + bit);
					cmd.add("input keyevent 62");
				}
			} else if(!text.equals(" ")){
				cmd.add("input text " + text);
			}
    		
			try{
				RootTools.sendShell(cmd.toArray(new String[]{}), 100, -1);
			}catch(Exception e){
				Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
			}
    	} else{
    		Toast.makeText(this, R.string.no_root, Toast.LENGTH_SHORT).show();
    	}
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		start(intent.getStringExtra("text"));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		start(intent.getStringExtra("text"));
		return Service.START_NOT_STICKY;
	}

}
