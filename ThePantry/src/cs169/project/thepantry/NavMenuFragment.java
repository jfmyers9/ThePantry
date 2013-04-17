package cs169.project.thepantry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NavMenuFragment extends ListFragment {
	
	private String urlLogout = "http://cockamamy-island-1557.herokuapp.com/session";
	SharedPreferences shared_pref;
	private String auth_token;
	private final String LOGGED_IN = "log_in";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		shared_pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		auth_token = shared_pref.getString(LOGGED_IN, null);
		String[] menu = getResources().getStringArray(R.array.nav_menu_items);
		ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, android.R.id.text1, menu);
		setListAdapter(menuAdapter);
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Context context = getActivity();
		boolean open = true;
		Intent intent = null;
		switch (position) {
		case 0:
			System.out.println(context.toString());
			if (context instanceof HomePageActivity) {
				open = false;
			}
			intent = new Intent(context, HomePageActivity.class);
			break;
		case 1:
			if (context instanceof InventoryActivity) {
				open = false;
			}
			InventoryActivity invAct = new InventoryActivity();
			intent = new Intent(context, invAct.getClass());
			break;
		case 2:
			if (context instanceof ShoppingListActivity) {
				open = false;
			}
			intent = new Intent(context, ShoppingListActivity.class);
			break;
		case 3:
			intent = new Intent(context, ProfileActivity.class);
			break;
		case 4:
			intent = new Intent(context, SettingsActivity.class);
			break;
		case 5:
			IngredientSyncTask slSync = new IngredientSyncTask(getActivity());
			slSync.execute(ThePantryContract.ShoppingList.TABLE_NAME, auth_token);
			try {
				slSync.get(3000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			IngredientSyncTask invSync = new IngredientSyncTask(getActivity());
			invSync.execute(ThePantryContract.Inventory.TABLE_NAME, auth_token);
			try {
				invSync.get(3000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logout();
			if (context instanceof HomePageActivity) {
				open = false;
			}
			intent = new Intent(context, HomePageActivity.class);
			break;
		}
		if (!open) {
			BasicMenuActivity ba = (BasicMenuActivity) getActivity();
			ba.toggle();
		} else {
			startActivity(intent);
		}
	}
	
	public void logout() {
		auth_token = shared_pref.getString(LOGGED_IN, null);
		if (auth_token != null) {
			LogoutTask lo = new LogoutTask();
			lo.execute(urlLogout);
		}
		
	}
	
private class LogoutTask extends AsyncTask<String, String, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... urls) {
		   	HttpClient client = new DefaultHttpClient();
		   	String url = urls[0] + "/?auth_token=" + auth_token;
	    	HttpDelete del = new HttpDelete(url);
	    	HttpResponse resp;
	    	del.addHeader("Content-type", "application/json");
	    	try {
	    		resp = client.execute(del);
	    		HttpEntity ent = resp.getEntity();
	            InputStream instream = ent.getContent();
	            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
	            StringBuilder builder = new StringBuilder();
	            String line = br.readLine();
	            while (line != null) {
	            	builder.append(line + "\n");
	            	line = br.readLine();
	            }
	            instream.close();
	            String result = builder.toString();
	            System.out.println(result);
	            JSONObject respObj = new JSONObject(result);
	            return respObj;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return new JSONObject();
	    	}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				boolean success = (Boolean)result.get("success");
				String info = (String)result.get("info");
				if (success) {
					DatabaseModel dm = new DatabaseModel(getActivity(), "thepantry");
					dm.clear(ThePantryContract.ShoppingList.TABLE_NAME);
					dm.clear(ThePantryContract.Inventory.TABLE_NAME);
					SharedPreferences.Editor editor = shared_pref.edit();
					editor.putString(LOGGED_IN, null);
					editor.commit();
				} else {
					SharedPreferences.Editor editor = shared_pref.edit();
					editor.putString(LOGGED_IN, null);
					editor.commit();
					CharSequence text = "Already Logged Out";
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(getActivity(), text, duration);
				}
			} catch (Exception e) {
				CharSequence text = "Error Logging Out";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(getActivity(), text, duration);
			}
		}
    	
    }

}
