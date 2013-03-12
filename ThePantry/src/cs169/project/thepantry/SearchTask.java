package cs169.project.thepantry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.os.AsyncTask;

/* How to use SearchTask:
 * new SearchTask().execute(String request_type, String query)
 * if request_type is "recipe", SearchTask will return the recipe with query = recipe_ID
 * if request_type is "search", SearchTask will return the result of searching Yummly with query
 * 
 * this will be extended to take in other parameters, such as user ingredients, dietary restrictions, etc.
 */

/* SearchTask extends AsyncTask and performs a search on the Yummly server asynchronously
 * AsyncTask is created with 3 types: <Params, Progress, Result>
 * Params is the type of parameters that are passed into the task's main method, doInBackground
 * Progress is the type of progress that is published during background computation
 * Result is the type of the result of background computation
 */
public class SearchTask extends AsyncTask<String, String, Storage> {
	//for accessing the api - id and key, plus timeout and retry info
	private String API_ID = "a169c318";
	private String API_KEY = "4778f72efbcabe0f761efefd92afe5b0";
	int TIMEOUT_MILLISEC = 10000;
	
	// some default URL Strings
	String URL_BASE    = "http://api.yummly.com/v1/api";
	String URL_GET     = URL_BASE + "/recipe/";
    String URL_SEARCH  = URL_BASE + "/recipes";
    String URL_META    = URL_BASE + "/metadata";

    // TODO onPreExecute is called by the UI thread before execution to show a progress bar or something in the UI
	//protected void onPreExecute() {
	//	
	//}
	
	// TODO onProgressUpdate is invoked on the UI thread after a call to publishProgress(Progress...)
	//protected void onProgressUpdate(String... strings) {
	//}
	
    /* doInBackground(Params...) is invoked on the background thread immediately after onPreExecute() finishes executing. 
	 * This step is used to perform background computation that can take a long time. 
	 * The parameters of the asynchronous task are passed to this step. 
	 * The result of the computation must be returned by this step and will be passed back to the last step. 
	 * This step can also use publishProgress(Progress...) to publish one or more units of progress. 
	 * These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
	 */
	@Override
	protected Storage doInBackground(String... strings) {
		
		//the first string is the type of request, either "recipe" (get recipe) or "search" (search for recipe)
		//the second string passed in is either the recipe ID or the search query q in String format
		//this can be extended to multiple strings to search for individual ingredients, allergies, cuisines, etc.
		String type = strings[0];
		String q = strings[1];
    	try {
    		// generate the correct URL for the get request
    		// create http parameters for the get request that will be appended to the URL in the request
    		String getURL;
        	HttpParams httpParams = new BasicHttpParams();
    		if (type == "recipe") {
    			getURL = URL_GET + q;
    		}
    		else if (type == "search") {
    			httpParams.setParameter("q", q);
    			getURL = URL_SEARCH;
    		}
    		else {
    			// TODO throw invalid request exception?
    			getURL = "";
    		}
    		
    		//create an http connection and client
    		//set timeout for connection and socket
        	HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
        	HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
        	HttpClient client = new DefaultHttpClient(httpParams);
    		
        	// create the get request and set authorization headers
            HttpGet httpget = new HttpGet(getURL);
            httpget.setHeader("X-Yummly-App-ID", API_ID);
            httpget.setHeader("X-Yummly-App-Key", API_KEY);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            
    		// issue the get request to the Yummly server
            // create a Storage object out of the JSON response
            String responseBody = client.execute(httpget, responseHandler);
            JSONObject jsonResponse = new JSONObject(responseBody);
            Storage result;
            if (type == "recipe") {
            	result = new Recipe(jsonResponse);
            } else if (type == "search") {
            	result = new SearchResult(jsonResponse);
            } else {
            	// TODO invalid request
            	result = new Recipe(new JSONObject());
            }
            
            return result;
            
    	} catch (Exception e) {
    		// TODO return some kind of default results (not these)
    		e.printStackTrace();
    		Storage defaultresult = new Recipe(new JSONObject());
    		return defaultresult;
        }
	}
	
	// TODO onPostExecute(Result) is invoked on the UI thread after the background computation finishes. 
	//The result of the background computation is passed to this step as a parameter.
//	@Override
/*	protected void onPostExecute(Storage result) {
		 // create an intent with the Storage object, this is the format:
		 // Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
		 // intent.putExtra(field, message);
		 // startActivity(intent);
    }*/
}