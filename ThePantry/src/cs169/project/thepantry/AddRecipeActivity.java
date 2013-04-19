package cs169.project.thepantry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class AddRecipeActivity extends Activity {
	
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_REQUEST = 1888; 
    private String selectedImagePath;
    private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_recipe);
		iv = (ImageView) findViewById(R.id.picture_preview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_recipe, menu);
		return true;
	}
	
	public void saveRecipe(View view) {
		
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
		                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		                startActivityForResult(cameraIntent, CAMERA_REQUEST);
		                return true;
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
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                iv.setImageURI(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST) {  
	            Bitmap photo = (Bitmap) data.getExtras().get("data");
	            iv.setImageBitmap(photo);
            }
        }  
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
