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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private GetJsonObject gsjo = null;
	private DatabaseModel dm;
	
	SharedPreferences logged_in;
	
	public final static String EXTRA_USER = "cs169.warmup.warmupproject.USER";
	public final static String EXTRA_COUNT = "cs169.warmup.warmupproject.COUNT";
	
	private final String urlLogin = "http://cockamamy-island-1557.herokuapp.com/session";
	private final String LOGGED_IN= "log_in";

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		logged_in = PreferenceManager.getDefaultSharedPreferences(this);
		dm = new DatabaseModel(this, "thepantry");

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.log_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		findViewById(R.id.register).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						attemptRegister();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		Context context = getApplicationContext();
		Intent intent = new Intent(context, RegistrationActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
	}
	
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} 

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
	    	String user = mEmail;
	    	String password = mPassword;
			SharedPreferences.Editor editor = logged_in.edit();
			editor.putString("username", mEmail.split("@")[0]);
			editor.commit();
	    	JSONObject obj = new JSONObject();
	    	try {
	    		JSONObject userObj = new JSONObject();
	    		userObj.put("email", user);
	    		userObj.put("password", password);
	    		obj.put("user", userObj);
	    		gsjo = new GetJsonObject(obj);
	    		gsjo.execute(urlLogin);
	    	} catch (Exception e) {
	    		showProgress(false);
				Context context = getApplicationContext();
				CharSequence text = "Something went wrong.";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
	    	}
		}
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

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
private class GetJsonObject extends AsyncTask<String, String, JSONObject> {
    	
    	private JSONObject mjson;
    	
        public GetJsonObject(JSONObject json) {
            mjson = json;
        }

		@Override
		protected JSONObject doInBackground(String... urls) {
		   	HttpClient client = new DefaultHttpClient();
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
	            JSONObject respObj = new JSONObject(result);
	            return respObj;
	    	} catch (Exception e) {
	    		showProgress(false);
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
					String auth_token = (String)((JSONObject)result.get("data")).get("auth_token");
					if (logged_in.getString(LOGGED_IN, null) == null) {
						SharedPreferences.Editor editor = logged_in.edit();
						editor.putString(LOGGED_IN, auth_token);
						editor.commit();
					} else {
						Context context = getApplicationContext();
						CharSequence text = "Something went horribly wrong.";
						int duration = Toast.LENGTH_LONG;
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					dm.clear(ThePantryContract.ShoppingList.TABLE_NAME);
					dm.clear(ThePantryContract.Inventory.TABLE_NAME);
					Context context = getApplicationContext();
					Intent intent = new Intent(context, ProfileActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				} else if (!success) {
					showProgress(false);
					Context context = getApplicationContext();
					CharSequence text = info;
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			} catch (Exception e) {
				showProgress(false);
				Context context = getApplicationContext();
				CharSequence text = "Something went horribly wrong.";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
    	
    }
    
}
