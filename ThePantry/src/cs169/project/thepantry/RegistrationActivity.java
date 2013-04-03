package cs169.project.thepantry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	
	private final String urlAdd = "http://cockamamy-island-1557.herokuapp.com/registration";
	private final String LOGGED_IN= "log_in";
	
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	
	SharedPreferences logged_in;
	
	private GetJsonObject gsjo = null;
	
	private String mEmail;
	private String mPassword;
	private String mPasswordConf;
	
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordConfView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		logged_in = PreferenceManager.getDefaultSharedPreferences(this);
		
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfView = (EditText) findViewById(R.id.password_confirmation);
		mEmailView.setText(mEmail);
		
		findViewById(R.id.registerButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						attemptRegister();
					}
				});
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void attemptRegister() {

		mEmailView.setError(null);
		mPasswordView.setError(null);
		mPasswordConfView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPasswordConf = mPasswordConfView.getText().toString();

		boolean cancel = false;
		View focusView = null;
//
//		// Check for a valid password.
//		if (TextUtils.isEmpty(mPassword)) {
//			mPasswordView.setError(getString(R.string.error_field_required));
//			focusView = mPasswordView;
//			cancel = true;
//		} else if (mPassword.length() < 8) {
//			mPasswordView.setError(getString(R.string.error_invalid_password));
//			focusView = mPasswordView;
//			cancel = true;
//		} else if (mPasswordConf.length() < 8) {
//			mPasswordConfView.setError(getString(R.string.error_invalid_password));
//			focusView = mPasswordView;
//			cancel = true;
//		}

		// Check for a valid email address.
//		if (TextUtils.isEmpty(mEmail)) {
//			mEmailView.setError(getString(R.string.error_field_required));
//			focusView = mEmailView;
//			cancel = true;
//		} 

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
	    	String user = mEmail;
	    	String password = mPassword;
	    	String passConf = mPasswordConf;
	    	JSONObject obj = new JSONObject();
	    	try {
	    		JSONObject userObj = new JSONObject();
	    		userObj.put("email", user);
	    		userObj.put("password", password);
	    		userObj.put("password_confirmation", passConf);
	    		obj.put("user", userObj);
	    		gsjo = new GetJsonObject(obj);
	    		gsjo.execute(urlAdd);
	    	} catch (Exception e) {
				Context context = getApplicationContext();
				CharSequence text = "Something went wrong1.";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
	    	}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}
	
private class GetJsonObject extends AsyncTask<String, String, JSONObject> {
    	
    	private JSONObject mjson;
    	private ProgressDialog dialog;
    	
        public GetJsonObject(JSONObject json) {
            mjson = json;
            this.dialog = new ProgressDialog(getApplicationContext());
        }
        
    	@Override
    	protected void onPreExecute() {
    		this.dialog.setMessage("Registering User");
    		this.dialog.show();
    	}

		@Override
		protected JSONObject doInBackground(String... urls) {
		   	HttpClient client = new DefaultHttpClient();
		   	System.out.print(urls[0]);
	    	HttpPost post = new HttpPost(urls[0]);
	    	HttpResponse resp;
	    	post.addHeader("Content-type", "application/json");
	    	try {
	    		post.setEntity(new StringEntity(mjson.toString()));
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
	            JSONObject respObj = new JSONObject(result);
	            return respObj;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return new JSONObject();
	    	}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				boolean success = (Boolean)result.get("success");
				String info = (String)result.get("info");
				if (success) {
					JSONObject data = (JSONObject)result.get("data");
					JSONObject user = (JSONObject)data.get("user");
					String auth_token = (String)data.get("auth_token");
					if (logged_in.getString(LOGGED_IN, null) == null) {
						SharedPreferences.Editor editor = logged_in.edit();
						editor.putString(LOGGED_IN, auth_token);
						editor.commit();
					} else {
						Context context = getApplicationContext();
						CharSequence text = "Something went horribly wrong3.";
						int duration = Toast.LENGTH_LONG;
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					Context context = getApplicationContext();
					Intent intent = new Intent(context, TutorialActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else if (!success) {
					Context context = getApplicationContext();
					CharSequence text = info;
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				Context context = getApplicationContext();
				CharSequence text = "Something went horribly wrong2.";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
    	
    }

}
