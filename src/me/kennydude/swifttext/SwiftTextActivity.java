package me.kennydude.swifttext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootTools.Result;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SwiftTextActivity extends Activity {
	SharedPreferences pref;
	List<String> presets;
	private ArrayAdapter<String> bonder;
	
	
	@SuppressWarnings("unchecked")
	void loadPresets(){
		// Load presets from file

		this.presets.clear();
		SharedPreferences presets = this.getSharedPreferences("presets", Context.MODE_PRIVATE);
		this.presets.addAll ( (Collection<? extends String>) presets.getAll().values() );
		this.bonder.notifyDataSetChanged();
	}
	
	void savePresets(){
		// Save presets

		SharedPreferences presets = this.getSharedPreferences("presets", Context.MODE_PRIVATE);
		Editor e = presets.edit();
		e.clear();
		
		int i = 0;
		for(String x : this.presets){
			e.putString(i + "", x);
			Log.d("saving", x);
			i = i + 1;
		}
		
		e.commit();
		loadPresets();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    Utils.setupTheme(this);

        setContentView(R.layout.main);

        // If we launched via Launcher, disable actually writing stuff
        // and show intro text
	    String ac = this.getIntent().getAction();
	    if(ac == null){ ac = ""; }

        boolean main_view = ac.equals("android.intent.action.MAIN");
                
        pref = this.getSharedPreferences("STA", Context.MODE_PRIVATE);
        presets = new ArrayList<String>();
        
        bonder = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, presets);
        ListView list = (ListView)findViewById(R.id.items);

	    // Add new item
	    TextView addNew = new TextView(this);
	    addNew.setText(R.string.add);
	    addNew.setPadding(10,10,10,10);
	    addNew.setOnClickListener(new View.OnClickListener(){

		    @Override
		    public void onClick(View f) {
			    AlertDialog.Builder ab = new AlertDialog.Builder(SwiftTextActivity.this);
			    ab.setTitle(R.string.add);

			    final View v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.edit, null);

			    ab.setView(v);
			    ab.setPositiveButton(R.string.add, new android.content.DialogInterface.OnClickListener(){
				    public void onClick(DialogInterface dialog, int which) {

					    presets.add(((EditText) v.findViewById(R.id.val)).getText().toString());
					    savePresets();
					    dialog.dismiss();

				    }
			    });
			    ab.setNegativeButton(android.R.string.cancel, new android.content.DialogInterface.OnClickListener(){

				    public void onClick(DialogInterface dialog, int which) {
					    dialog.dismiss();
				    }

			    });

			    ab.show();
		    }

	    });

	    list.addFooterView(addNew);

        list.setAdapter(bonder);
        loadPresets();
        
        if(!main_view){
	        list.setOnItemClickListener(new OnItemClickListener(){
	
				public void onItemClick(AdapterView<?> arg0, View arg1, int location,
						long arg3) {
					finish();
					InsertText(presets.get(location));
				}
	        	
	        });
	        
	        this.setTitle(R.string.swift_real);
	        this.findViewById(R.id.intro).setVisibility(View.GONE);
        }
        
        list.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int pos, long arg3) {
				AlertDialog.Builder ab = new AlertDialog.Builder(SwiftTextActivity.this);
				ab.setItems(new CharSequence[]{ getText(R.string.delete) }, new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
						AlertDialog.Builder ab = new AlertDialog.Builder(SwiftTextActivity.this);
						ab.setMessage(R.string.are_you_sure);
						ab.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								presets.remove(pos);
								savePresets();
							}
							
						});
						ab.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
							
						});
						ab.show();
					}
					
				});
				ab.show();
				return true;
			}
        	
        });
        
        new Thread(new Runnable(){

			public void run() {
				displayWarning();
			}
        	
        }).start();

	    sendBroadcast(new Intent(this, ApplySettingsReceiver.class));
    }
    
    
    void showWarn(){
    	runOnUiThread(new Runnable(){

			public void run() {
				findViewById(R.id.su_notice).setVisibility(View.VISIBLE);
			}
    		
    	});
    }
    
    /**
     * Version bellow 3.1.1 have a bug
     */
    void displayWarning(){
    	try{
	    	if(RootTools.isRootAvailable()){
	    		RootTools.sendShell("su --version", new Result(){

					@Override
					public void onComplete(int arg0) {}

					@Override
					public void onFailure(Exception arg0) {}

					@Override
					public void process(String line) throws Exception {
						Log.d("swifttext", line);

						if(!line.equals("")){
							String[] version = line.split(".");

							if( Integer.parseInt(version[0]) >= 3 ){
				        		if(Integer.parseInt(version[1]) >= 1){
				        			if( Integer.parseInt(version[2]) >= 1){
				        				return;
				        			}
				        		}
				        	}

							showWarn();
						}
					}

					@Override
					public void processError(String arg0) throws Exception {}

	    		}, -1);
	        } else{ showWarn(); }
    	} catch(Exception e){ e.printStackTrace(); showWarn(); }
    }
    
    /**
     * Insert Text via Shell
     * (copied from Monkey)    
     * @param text
     */
    public void InsertText(String text){
    	Intent intent = new Intent(SwiftTextActivity.this, InsertTextService.class);
    	intent.putExtra("text", text);
    	startService(intent);
    }

	@Override
	public boolean onCreateOptionsMenu (Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item){
		switch(item.getItemId()){
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.kisses:
				// Kisses Code
				// {
				AlertDialog.Builder ab = new AlertDialog.Builder(SwiftTextActivity.this);
				ab.setTitle(R.string.kisses);
				ab.setMessage(R.string.count_kisses);

				final View v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spinner, null);

				final EditText e = ((EditText) v.findViewById(R.id.val));
				e.setText(pref.getString("lastKiss", "1"));

				Button b = (Button) v.findViewById(R.id.up);
				b.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						try{
							e.setText((Integer.parseInt(e.getText().toString()) + 1) + "");
						} catch(Exception e){
							e.printStackTrace();
						}
					}
				});

				b = (Button) v.findViewById(R.id.down);
				b.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						try{
							e.setText((Integer.parseInt(e.getText().toString()) - 1) + "");
						} catch(Exception e){
							e.printStackTrace();
						}
					}
				});

				ab.setView(v);

				ab.setPositiveButton(R.string.insert, new android.content.DialogInterface.OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						String x = e.getText().toString();
						try{
							Integer i = Integer.parseInt(x);
							String t = "";
							for(int c = 1; c < i; c++){
								t += "x";
							}

							pref.edit().putString("lastKiss", x).commit();

							arg0.dismiss();
							finish();

							InsertText(t);
						} catch(Exception e){
							e.printStackTrace();
						}
					}

				});
				ab.setNegativeButton(android.R.string.cancel, new android.content.DialogInterface.OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}

				});

				ab.create().show();

				// }
				// End
				break;
		}
		return true;
	}
}