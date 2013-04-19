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
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

public class IngredientSyncTask extends AsyncTask<String, String, JSONArray> {
	
	private String tableName, authToken;
	private DatabaseModel dm;
	private String baseURL = "http://cockamamy-island-1557.herokuapp.com/";
	private Activity act;
	private ProgressDialog dialog;
	
	public IngredientSyncTask(Activity act) {
		this.act = act;
		dialog = new ProgressDialog(act);		
	}
	
	@Override
	protected void onPreExecute() {
		this.dialog.setMessage("Syncing Databases");
		this.dialog.show();
	}
	
	public JSONArray updateFlags(String flag) {
		JSONArray ingrs = new JSONArray();
		dm = new DatabaseModel(act, "thepantry");
    	ArrayList<IngredientChild> items = dm.checkedItems(tableName, flag);
    	if (items != null) {
    		for (IngredientChild child : items) {
    			String ingredient = child.getName();
    			String group = child.getGroup();
    			String status = "add";
    			if (flag.equals(ThePantryContract.REMOVEFLAG)) {
    				status = "delete";
    			}
    			JSONObject ingr = new JSONObject();
    			dm.check(tableName, ingredient, ThePantryContract.ADDFLAG, false);
    			try {
    				ingr.put("ingredient", ingredient);
    				ingr.put("group", group);
    				ingr.put("status", status);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			ingrs.put(ingr);
    		}
    	}
    	return ingrs;
	}
	
	public JSONArray appendJson(JSONArray a1, JSONArray a2) {
		for (int i = 0; i < a2.length(); i++) {
			try {
				a1.put(a2.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return a1;
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		JSONArray ingrs = new JSONArray();
		
		// A little bit cutty
		int itemInd = 1;
		int typeInd = 2;
		
		tableName = params[0];
		authToken = params[1];
		String urlAddon = "";
		if (authToken != null) {
			if (tableName.equals(ThePantryContract.ShoppingList.TABLE_NAME)) {
				urlAddon = "shoplist/?auth_token=" + authToken;
				itemInd = ThePantryContract.ShoppingList.ITEMIND;
				typeInd = ThePantryContract.ShoppingList.TYPEIND;
			} else if (tableName.equals(ThePantryContract.Inventory.TABLE_NAME)) {
				urlAddon = "inventory/?auth_token=" + authToken;
				itemInd = ThePantryContract.Inventory.ITEMIND;
				typeInd = ThePantryContract.Inventory.TYPEIND;
			}
			String url = baseURL + urlAddon;
		   	HttpClient client = new DefaultHttpClient();
	    	HttpPost post = new HttpPost(url);
	    	HttpResponse resp;
	    	post.addHeader("Content-type", "application/json");
	    	JSONObject obj = new JSONObject();
	    	ingrs = appendJson(updateFlags(ThePantryContract.ADDFLAG), 
	    					   updateFlags(ThePantryContract.REMOVEFLAG));
	    	try {
	    		obj.put("ingredients", ingrs);
	    	} catch (JSONException e) {
	    		e.printStackTrace();
	    	}
	    	System.out.println(obj.toString());
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
	            System.out.println(result);
	            JSONArray respObj = new JSONArray(result);
	            return respObj;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return new JSONArray();
	    	}
		} else  {
			return new JSONArray();
		}
	}
	
	@Override
	public void onPostExecute(JSONArray response) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		System.out.println(tableName);
		System.out.println(response.toString());
		ArrayList<String> ingredients = new ArrayList<String>();
		ArrayList<String> groups = new ArrayList<String>();
		for (int i = 0; i < response.length(); i++) {
			try {
				JSONObject resp = (JSONObject) response.get(i);
				ingredients.add(resp.getString("ingredient"));
				groups.add(resp.getString("group"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		dm = new DatabaseModel(act, "thepantry");
		dm.clear(tableName);
		for (int i = 0; i < ingredients.size(); i++) {
			dm.addIngredient(tableName, ingredients.get(i), groups.get(i), "1");
			dm.check(tableName, ingredients.get(i),ThePantryContract.ADDFLAG, false);
		}
	}

}
