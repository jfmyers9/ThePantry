package cs169.project.thepantry;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

public class AddRecipeActivity extends Activity {
	
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
	private static final String JPEG_FILE_PREFIX = "CookBook";
	private static final String JPEG_FILE_SUFFIX = ".jpg"; 
    private String selectedImagePath;
    private ImageButton ib;
    private AmazonS3Client s3Client;
    private final String MY_ACCESS_KEY_ID = "AKIAIDQSE7PHPGL35IYQ";
    private final String MY_SECRET_KEY = "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";
    private final String MY_PICTURE_BUCKET = "thepantryproject";
	private final String LOGGED_IN = "log_in";
	private String login_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_recipe);
		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(this);
		login_status = shared_pref.getString(LOGGED_IN, null);
		if (login_status == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		ib = (ImageButton) findViewById(R.id.add_recipe_picture_button);
		s3Client = new AmazonS3Client( new BasicAWSCredentials( MY_ACCESS_KEY_ID, MY_SECRET_KEY ) );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_recipe, menu);
		return true;
	}
	
	public void saveRecipe(View view) {
		try {
			String recName = ((EditText)findViewById(R.id.recipe_title)).getText().toString();
			String ingredients = ((EditText)findViewById(R.id.ingredients)).getText().toString();
			String instructions = ((EditText)findViewById(R.id.instructions)).getText().toString();
			String picName = recName + login_status;
			
			AmazonS3AsyncTask s3Task = new AmazonS3AsyncTask(picName, getApplicationContext(), recName, ingredients, instructions, this);
			s3Task.execute();
		} catch (Exception e) {
			System.out.println("2");
			e.printStackTrace();
		}		
	}
	
	
	public void addPicture(View view) {
		PopupMenu popup = new PopupMenu(this, view);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem item) {
				switch(item.getItemId()) {
					case R.id.pic_from_gal:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), SELECT_PICTURE);
                        return true;
					case R.id.pic_from_cam:
						try {
							File f = createImageFile();
			                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			                startActivityForResult(cameraIntent, CAMERA_REQUEST);
			                return true;
						} catch (Exception e) {
							System.out.println("1");
							e.printStackTrace();
							return false;
						}
					default:
						return false;
				}
			}
		});
		popup.inflate(R.menu.add_picture);
		popup.show();
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            BitmapDrawable bd = (BitmapDrawable)ib.getDrawable();
            bd.getBitmap().recycle();
            ib.setImageBitmap(null);
            try {
	            if (requestCode == SELECT_PICTURE) {
	                Uri selectedImageUri = data.getData();
	                selectedImagePath = getPath(selectedImageUri);
	                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
	                bm = Bitmap.createScaledBitmap(bm, 50, 50, false);
	                ib.setImageBitmap(bm);
	            } else if (requestCode == CAMERA_REQUEST) {
	            	Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(selectedImagePath)));
	                bm = Bitmap.createScaledBitmap(bm, 50, 50, false);
	                ib.setImageBitmap(bm);
	            }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }  
    }
    
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File image = File.createTempFile(
            imageFileName, 
            JPEG_FILE_SUFFIX, 
            getAlbumDir()
        );
        selectedImagePath = image.getAbsolutePath();
        return image;
    }
    
    public File getAlbumDir() {
    	File storageDir;
    	storageDir = new File(android.os.Environment.getExternalStorageDirectory(),getAlbumName());
    	if(!storageDir.exists())
    	    storageDir.mkdirs();
    	return storageDir;
    }
    
    public String getAlbumName() {
    	return "ThePantry";
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
    private class AmazonS3AsyncTask extends AsyncTask<Void, URL, URL> {
    	
    	private String picName, recName, ingredients, instructions;
    	private Context context;
    	private ProgressDialog dialog;
    	
    	public AmazonS3AsyncTask(String picName, Context context, String recName, String ingredients, String instructions, Activity act) {
    		this.picName = picName;
    		this.context = context;
    		this.recName = recName;
    		this.ingredients = ingredients;
    		this.instructions = instructions;
    		dialog = new ProgressDialog(act);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		this.dialog.setMessage("Adding Recipe");
    		this.dialog.show();
    	}

		@Override
		protected URL doInBackground(Void ... params) {
			
			PutObjectRequest por = new PutObjectRequest(MY_PICTURE_BUCKET, picName, new java.io.File(selectedImagePath));  
			s3Client.putObject(por);
			
			ResponseHeaderOverrides override = new ResponseHeaderOverrides();
			override.setContentType( "image/jpeg" );
			GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(MY_PICTURE_BUCKET, picName);
			urlRequest.setExpiration( new Date( System.currentTimeMillis() + 3600000 ) );  // Added an hour's worth of milliseconds to the current time.
			urlRequest.setResponseHeaders(override);
			URL url = s3Client.generatePresignedUrl(urlRequest);
			
			return url;
		}
		
		@Override
		protected void onPostExecute(URL url) {
			dialog.dismiss();
			try {
				Recipe recipe = new Recipe();
				recipe.name = recName;
				recipe.id = recName;
				RecipeImages img = new RecipeImages(url.toURI().toString(), url.toURI().toString());
				recipe.images = img;
				System.out.println(recName);
				System.out.println(ingredients);
				System.out.println(instructions);
				ArrayList<String> ingLines = new ArrayList<String>();
				for (String s : ingredients.split(",")) {
					ingLines.add(s);
				}
				recipe.ingredientLines = ingLines;
				ArrayList<String> instLines = new ArrayList<String>();
				for(String s : instructions.split("\\.")) {
					instLines.add(s);
				}
				recipe.directionLines = instLines;
				DatabaseModel dm = new DatabaseModel(context, ThePantryContract.DATABASE_NAME);
				dm.addStorage(ThePantryContract.CookBook.TABLE_NAME, recipe);
				dm.close();
				Intent intent = new Intent(context, CookBookActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	
    }

}
