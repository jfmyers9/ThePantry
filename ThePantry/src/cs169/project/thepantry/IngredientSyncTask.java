package cs169.project.thepantry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;

public class IngredientSyncTask extends AsyncTask<String, String, JSONArray> {
	
	private String tableName, authToken;
	private DatabaseModel dbModel;
	private String baseURL = "http://cocamamy-island-1557.herokuapp.com/";
	private Activity act;
	
	public IngredientSyncTask(Activity act) {
		this.act = act;
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		JSONArray ingrs = new JSONArray();
		tableName = params[0];
		authToken = params[1];
		String urlAddon = "";
		if (tableName.equals(ThePantryContract.ShoppingList.TABLE_NAME)) {
			urlAddon = "shoplist?auth_token=" + authToken;
		} else if (tableName.equals(ThePantryContract.Inventory.TABLE_NAME)) {
			urlAddon = "inventory?auth_token=" + authToken;
		}
		String url = baseURL + urlAddon;
	   	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(url);
    	HttpResponse resp;
    	post.addHeader("Content-type", "application/json");
    	JSONObject obj = new JSONObject();
    	dbModel = new DatabaseModel(act, tableName);
    	Cursor items = dbModel.checkedItems(tableName, ThePantryContract.ADDFLAG);
    	if (items != null) {
    		while (!items.isAfterLast()) {
    			String ingredient = items.getString(0);
    			String status = "add";
    			JSONObject ingr = new JSONObject();
    			dbModel.check(tableName, ingredient, ThePantryContract.ADDFLAG, false);
    			try {
    				ingr.put("ingredient", ingredient);
    				ingr.put("status", status);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			ingrs.put(ingr);
    			items.moveToNext();
    		}
    	}
    	items.close();
    	items = dbModel.checkedItems(tableName, ThePantryContract.REMOVEFLAG);
    	if (items != null) {
    		while (!items.isAfterLast()) {
    			String ingredient = items.getString(0);
    			String status = "remove";
    			dbModel.remove(tableName, ingredient);
    			JSONObject ingr = new JSONObject();
    			try {
    				ingr.put("ingredient", ingredient);
    				ingr.put("status", status);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			ingrs.put(ingr);
    			items.moveToNext();
    		}
    	}
    	items.close();
    	try {
    		obj.put("ingredients", ingrs);
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	
    	try {
    		post.setEntity(new StringEntity(obj.toString()));
    		resp = client.execute(post);
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
            JSONArray respObj = new JSONArray(result);
            return respObj;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new JSONArray();
    	}
	}
	
	@Override
	public void onPostExecute(JSONArray response) {
		ArrayList<String> ingredients = new ArrayList<String>();
		for (int i = 0; i < response.length(); i++) {
			try {
				ingredients.add(((JSONObject)response.get(i)).getString("ingredient"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		dbModel = new DatabaseModel(act, tableName);
		//dbModel.removeAllBut(tableName, ingredients);
	}

}
