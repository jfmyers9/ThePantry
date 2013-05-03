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
import android.os.AsyncTask;

public class UserRecipeSyncTask extends AsyncTask<String, String, JSONArray> {
	
	private Activity act;
	private ProgressDialog dialog;
	private String tableName, authToken;
	private String baseURL = "http://cockamamy-island-1557.herokuapp.com/";
	private DatabaseModel dm;
	
	public UserRecipeSyncTask(Activity act) {
		this.act = act;
		dialog = new ProgressDialog(act);		
	}
	
	@Override
	protected void onPreExecute() {
		this.dialog.setMessage("Syncing Databases");
		this.dialog.show();
	}
	
	public JSONArray updateFlags(String flag) {
		JSONArray recipes = new JSONArray();
		dm = new DatabaseModel(act, "thepantry");
    	ArrayList<Recipe> recs = dm.checkedRecipes(tableName, flag);
    	if (recs != null) {
    		for (Recipe recipe : recs) {
    			String name = recipe.name;
    			String id = recipe.id;
    			String imgUrl = recipe.images.hostedLargeUrl;
    			String ingLines = "";
    			for (String s : recipe.ingredientLines) {
    				if (!ingLines.equals("")) {
    					ingLines += ThePantryContract.SEPERATOR;
    				}
    				ingLines += s;
    			}
    			String dirLines = "";
    			for (String s : recipe.directionLines) {
    				if (!dirLines.equals("")) {
    					dirLines += ThePantryContract.SEPERATOR;
    				}
    				dirLines += s;
    			}
    			String cooked = dm.isCooked(tableName, id);
    			String favorite = dm.isFavorite(tableName, id);
    			String status = "add";
    			if (flag.equals(ThePantryContract.REMOVEFLAG)) {
    				status = "delete";
    			}
    			JSONObject recip = new JSONObject();
    			dm.check(tableName, id, ThePantryContract.ADDFLAG, false);
    			try {
    				recip.put("id", id);
    				recip.put("name", name);
    				recip.put("imgage", imgUrl);
    				recip.put("ingLines", ingLines);
    				recip.put("dirLines", dirLines);
    				recip.put("cooked", cooked);
    				recip.put("favorite", favorite);
    				recip.put("status", status);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			recipes.put(recip);
    		}
    	}
    	return recipes;
	}
	
	public JSONArray appendJson(JSONArray a1, JSONArray a2) {
		for (int i = 0; i < a2.length(); i++) {
			try {
				a1.put(a2.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return a1;
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		JSONArray recipes = new JSONArray();
		tableName = params[0];
		authToken = params[1];
		String urlAddon = "";
		if (authToken != null) {
			urlAddon = "user_recipe/?auth_token=" + authToken;
			String url = baseURL + urlAddon;
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
	    	HttpResponse resp;
	    	post.addHeader("Content-type", "application/json");
	    	JSONObject obj = new JSONObject();
	    	recipes = appendJson(updateFlags(ThePantryContract.ADDFLAG), 
	    					   updateFlags(ThePantryContract.REMOVEFLAG));
	    	try {
	    		obj.put("recipes", recipes);
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
		dm = new DatabaseModel(act, "thepantry");
		dm.clear(tableName);
		for (int i = 0; i < response.length(); i++) {
			try {
				JSONObject resp = (JSONObject) response.get(i);
				Recipe recipe = new Recipe();
				recipe.name = resp.getString("name");
				recipe.id = resp.getString("id");
				recipe.images = new RecipeImages(resp.getString("imgUrl"), resp.getString("imgUrl"));
				ArrayList<String> ingLines = new ArrayList<String>();
				for (String s : resp.getString("ingLines").split(ThePantryContract.SEPERATOR)) {
					ingLines.add(s);
				}
				recipe.ingredientLines = ingLines;
				ArrayList<String> dirLines = new ArrayList<String>();
				for (String s : resp.getString("dirLines").split(ThePantryContract.SEPERATOR)) {
					dirLines.add(s);
				}
				recipe.directionLines = dirLines;
				dm.addStorage(tableName, recipe);
				if (resp.getString("cooked").equals("true")) {
					dm.check(tableName, recipe.id, ThePantryContract.Storage.COOKED, true);
				}
				if (resp.getString("favorite").equals("true")) {
					dm.check(tableName, recipe.id, ThePantryContract.Storage.FAVORITE, true);
				}
				dm.check(tableName, recipe.id, ThePantryContract.ADDFLAG, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
