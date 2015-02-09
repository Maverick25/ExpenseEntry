package net.workbook.expenseentry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.ReceiptFile;
import net.workbook.expenseentry.support.JSONParser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraActivity extends Activity implements OnClickListener,Finals
{    
	private ImageView portrait;
    private Button photoButton;
    private EditText descriptionField;
    private Button dateButton;
    private Button entryButton;
    private Button deleteButton;
    private TextView loadingMessage;
    
    private View appView;
    private View loadingView;
    
    private ReceiptFile file;
    private Bitmap bitmap;
    
    private int code;
    private long dateMillis;
    private boolean isTakingNotGetting;
    private boolean forEntry;
    private boolean creationMode;
    private boolean goingToNew;
    private boolean changedReceipt;
    
//    private int resolution;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        
        try
        {
        	code = getIntent().getIntExtra(CODE, 20);
        
        	switch (code)
        	{
        	// after selecting receipt file from the list to view it
        	case LOOK_AT_PIC:
        		file = getIntent().getParcelableExtra(SELECTED_RECEIPT);
            	int id = file.getId();
            	int companyId = file.getCompanyId();
            	new InitializePicture().execute(LOOK_AT_PIC,id,companyId);
            	creationMode = false;
            	changedReceipt = false;
        		break;
        	// after pressing "Take" to take a picture for a receipt file 
        	case TAKE_PIC:
        		new InitializePicture().execute(TAKE_PIC);
        		creationMode = true;
        		changedReceipt = false;
        		break;
        	// after pressing "Get" to get a picture from the gallery for a receipt file
        	case GET_PIC:
        		new InitializePicture().execute(GET_PIC);
        		creationMode = true;
        		changedReceipt = false;
        		break;
        	// after clicking in the EntryActivity to select from the receipt file list and choosing actual receipt file
        	case LOOK_AT_PIC_FOR_ENTRY:
        		file = getIntent().getParcelableExtra(SELECTED_RECEIPT);
            	int idForEntry = file.getId();
            	int companyIdForEntry = file.getCompanyId();
        		new InitializePicture().execute(LOOK_AT_PIC_FOR_ENTRY,idForEntry,companyIdForEntry);
        		creationMode = false;
        		changedReceipt = false;
        		break;
        	// after clicking in the EntryActivity to select from the gallery
        	case GET_PIC_FOR_ENTRY:
        		new InitializePicture().execute(GET_PIC_FOR_ENTRY);
        		creationMode = true;
        		changedReceipt = false;
        		break;
        	default:
        		throw new Exception();
        	}
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
    	try
    	{
    		if (resultCode == RESULT_OK) 
    		{  
    			ByteArrayOutputStream stream;
    			byte[] byteArray;
    			switch (requestCode)
    			{
    			// after taking a picture for a receipt file
    			case CAMERA_REQUEST:
    				Bitmap photo = (Bitmap) data.getExtras().get("data"); 
    				
    				stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byteArray = stream.toByteArray();
    				
        			portrait.setImageBitmap(photo);
        			if (file == null)
        			{
        				file = new ReceiptFile();
        			}
        			file.setBitmapBytes(byteArray);
        			changedReceipt = true;
        			break;
        		// after getting a picture from the gallery for a receipt file/expense entry file only as well
    			case SELECTED_PICTURE:
    				Uri selectedImage = data.getData();
    	            Bitmap pic = decodeUri(selectedImage);
    	            
    	            stream = new ByteArrayOutputStream();
					pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byteArray = stream.toByteArray();
					
                    portrait.setImageBitmap(pic);
                    if (file == null)
                    {
                    	file = new ReceiptFile();
                    }
                    
                    file.setBitmapBytes(byteArray);
                    changedReceipt = true;
    				break;
    			// after changing the date for a receipt file
    			case DATE:
    				dateMillis = data.getLongExtra(SELECTED_DATE, 0);
    				Date uploadDate = new Date(dateMillis);
    				file.setUploadDate(uploadDate);
    				String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault()).format(uploadDate);
    				dateButton.setText(dateFormat);
    				break;
    			}
    		}
    		else
    		{
    			switch (requestCode)
    			{
    			case CAMERA_REQUEST:
    			case SELECTED_PICTURE:
    				Intent current = new Intent();
    			    setResult(RESULT_OK, current);
    			    finish(); 
    				break;
    			}
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    } 
    
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException 
    {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) 
    {
    	getMenuInflater().inflate(R.menu.camera, menu);
    	return true;
	}
    
    private void showProgress(final boolean show) 
	{
		// If available, use these APIs to fade-in the progress spinner
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) 
		{
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			loadingView.setVisibility(View.VISIBLE);
			loadingView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() 
			{
						@Override
						public void onAnimationEnd(Animator animation) 
						{
							loadingView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
			});

			appView.setVisibility(View.VISIBLE);
			appView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() 
			{
						@Override
						public void onAnimationEnd(Animator animation) 
						{
							appView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
			});
		} 
		else 
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components
			loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
			appView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		try
		{
			rotate();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}

	private void rotate() 
    {
		Bitmap bitmap = ((BitmapDrawable)portrait.getDrawable()).getBitmap();
		
		Matrix matrix = new Matrix();

		matrix.postRotate(90);

		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
		portrait.setImageBitmap(rotatedBitmap);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		file.setBitmapBytes(byteArray);
    }
    
    private class InitializePicture extends AsyncTask<Integer, Void, Boolean>
    {

		@Override
		protected void onProgressUpdate(Void... values) 
		{
			loadingMessage.setText(LOADING_DATA);
			showProgress(true);
		}

		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try
			{
				appView = findViewById(R.id.appView);
				loadingView = findViewById(R.id.loadingView);
				loadingMessage = (TextView) findViewById(R.id.loadingMessage);
				
				publishProgress();
				
				descriptionField = (EditText) findViewById(R.id.descriptionBox);
				dateButton = (Button) findViewById(R.id.dateButton);
				
				portrait = (ImageView) findViewById(R.id.portrait);
				entryButton = (Button) findViewById(R.id.createNew);
				deleteButton = (Button) findViewById(R.id.deletePic);
			    photoButton = (Button) findViewById(R.id.takePhotoButton);
			      
			    dateButton.setOnClickListener(CameraActivity.this);
			    entryButton.setOnClickListener(CameraActivity.this);
			    deleteButton.setOnClickListener(CameraActivity.this);
			    photoButton.setOnClickListener(CameraActivity.this);
			    portrait.setOnClickListener(CameraActivity.this);
			    
			    
			    switch(params[0])
			    {
			 	// after selecting receipt file from the list to view it
			    case LOOK_AT_PIC:
			    	try
			        {	
			    		portrait.setDrawingCacheEnabled(false);
			        	portrait.setAnimation(null);
			        	
			            UrlImageViewHelper.setUrlDrawable(portrait, requestPic(params[1], params[2]), null,0, new UrlImageViewCallback() {
			                @Override
			                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
			                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
			                        scale.setDuration(300);
			                        scale.setInterpolator(new OvershootInterpolator());
			                        imageView.startAnimation(scale);
			                        
			                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
		                        	loadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		                        	byte[] byteArray = stream.toByteArray();
		                        	file.setBitmapBytes(byteArray);
		                        	
		                        	showProgress(false);
			                    }
			            });
			        }
			        catch (Exception e)
			        {
			        	e.printStackTrace();
			        }
			    	forEntry = false;
					return true;
				// after pressing "Take" to take a picture for a receipt file 
			    case TAKE_PIC:
			    	isTakingNotGetting = true;
			    	forEntry = false;
			    	return false;
		    	// after pressing "Get" to get a picture from the gallery for a receipt file
			    case GET_PIC:
			    	isTakingNotGetting = false;
			    	forEntry = false;
			    	return false;
		    	// after clicking in the EntryActivity to select from the receipt file list and choosing actual receipt file
			    case LOOK_AT_PIC_FOR_ENTRY:
			    	try
			        {	
			    		portrait.setDrawingCacheEnabled(false);
			        	portrait.setAnimation(null);
			            
			            UrlImageViewHelper.setUrlDrawable(portrait, requestPic(params[1], params[2]), null,0, new UrlImageViewCallback() {
			                @Override
			                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
			                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
			                        scale.setDuration(300);
			                        scale.setInterpolator(new OvershootInterpolator());
			                        imageView.startAnimation(scale);
			                        
			                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
		                        	loadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		                        	byte[] byteArray = stream.toByteArray();
		                        	file.setBitmapBytes(byteArray);
		                        	
		                        	showProgress(false);
			                    }
			            });
			        }
			        catch (Exception e)
			        {
			        	e.printStackTrace();
			        }
			    	forEntry = true;
			    	return true;
		    	// after clicking in the EntryActivity to select from the gallery
			    case GET_PIC_FOR_ENTRY:
			    	isTakingNotGetting = false;
			    	forEntry = true;
			    	return false;
			    default:
			    	throw new Exception();
			    }
			    
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean havingPic) 
		{
			try
			{
				descriptionField.setText(file.getDescription());
			}
			catch (Exception e) {}
			try
			{
				dateMillis = file.getUploadDate().getTime();
				String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault()).format(file.getUploadDate());
				dateButton.setText(dateFormat);
			}
			catch (Exception e)
			{
				dateMillis = new Date().getTime();
				String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault()).format(new Date());
				dateButton.setText(dateFormat);
			}
			
			// if from EntryActivity, change buttons layout 
			if (forEntry)
			{
				portrait.setEnabled(false);
				
				deleteButton.setVisibility(View.GONE);
				entryButton.setText(USE_THIS);
				photoButton.setText(CANCEL);
			}
			
			
			if (!havingPic)
			{
				try
				{
					if (isTakingNotGetting)
					{
						showProgress(false);
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
					}
					else
					{
						if (forEntry)
						{
							descriptionField.setVisibility(View.GONE);
	    					dateButton.setVisibility(View.GONE);
						}
						showProgress(false);
						Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, SELECTED_PICTURE);    
					}
				}
				catch (Exception e)
				{
					System.out.println("Let me show you an error of your way!");
					showProgress(false);
					e.printStackTrace();
				}
			}
		}

		public String requestPic(int id,int companyId)
		{
			return "http://"+MainActivity.getServer()+GET_PHOTO+companyId+"/"+id;
		}
    }
    
    
    
    
    private class ManipulateReceiptFile extends AsyncTask<Integer, Void, Boolean>
    {
    	private boolean fromGallery; 
    	private int resolution;
    	
		@Override
		protected void onPreExecute() 
		{
			loadingMessage.setText(PROCESSING_REQUEST);
			showProgress(true);
		}

		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			bitmap = null;
			try
			{
				JSONParser jParser = new JSONParser();
				
				switch (params[0])
				{
				// for adding a new receipt file
				case ADD:
					bitmap = ((BitmapDrawable)portrait.getDrawable()).getBitmap();
					String addReceipt = jParser.getResponse(MainActivity.getServer(), request(ADD));
					System.out.println(addReceipt);
					file.setId(Integer.parseInt(addReceipt));
					
					publishProgress();
					try
					{
						String addPhoto = jParser.postBitmap(MainActivity.getServer(), requestPhoto(), bitmap, params[1]);
						System.out.println(addPhoto);
						file.setFileName(addPhoto);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					fromGallery = false;
					return true;
				// for editing already existing receipt file
				case EDIT:
					bitmap = ((BitmapDrawable)portrait.getDrawable()).getBitmap();
					String editReceipt = jParser.putResponse(MainActivity.getServer(), request(EDIT));
					System.out.println(editReceipt);
					
					publishProgress();
					try
					{
						String editPhoto = jParser.postBitmap(MainActivity.getServer(), requestPhoto(), bitmap, params[1]);
						System.out.println(editPhoto);
						file.setFileName(editPhoto);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}	
					fromGallery = false;
					return true;
				// for adding a new expense entry file from gallery
				case FROM_GALLERY:
					resolution = params[1];
					fromGallery = true;
					return true;
				// for deleting already existing receipt file
				case DELETE:
					String deleteResponse = jParser.deleteResponse(MainActivity.getServer(), request(DELETE));
					System.out.println(deleteResponse);
					fromGallery = false;
					return true;
				default:
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) 
		{
			try
			{
				if (success)
				{
					if (goingToNew)
					{
						if (forEntry)
						{
							Intent data = new Intent();
							data.putExtra(SELECTED_RECEIPT, file);
							if (fromGallery)
							{
								data.putExtra(SELECTED_RESOLUTION, resolution);
							}
							setResult(RESULT_OK, data);
							finish();
						}
						else
						{
							Intent createNewEntry = new Intent(CameraActivity.this,MainActivity.class);
							createNewEntry.putExtra(SELECTED_RECEIPT, file);
							createNewEntry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(createNewEntry);
							finish();
						}
					}
					else
					{
					    Intent current = new Intent();
					    setResult(RESULT_OK, current);
					    finish();      
					}
				}
				else
				{
					showProgress(false);
					AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					builder.setMessage("Error occured while processing the request!")
				       .setCancelable(false)
				       .setNeutralButton(DISMISS, new DialogInterface.OnClickListener() {
				           @Override
				    	   public void onClick(DialogInterface dialog, int id) {
				        	   dialog.dismiss();
				           }
				       });
					AlertDialog alert = builder.create();
					alert.show();
					TextView messageView = (TextView)alert.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				showProgress(false);
			}
		}

		private String requestPhoto()
		{
			try
			{
				return GET_PHOTO+file.getCompanyId()+"/"+file.getId();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		
		private String request(int code)
		{
			try
			{
				if (file == null)
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				file = new ReceiptFile();
			}
			
			try
			{
				if (file.getCompanyId() == 0)
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				file.setCompanyId(MainActivity.getCompanyId());
			}
			
			try
			{
				if (file.getId() == 0)
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				
			}
			
			try
			{
				if (descriptionField.getText().toString() == null)
				{
					throw new Exception();
				}
				file.setDescription(descriptionField.getText().toString());
			}
			catch (Exception e)
			{
				file.setDescription(NOTHING);
			}
			
			try
			{
				if (file.getUploadDate() == null)
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				file.setUploadDate(new Date(dateMillis));
			}
			
			try
			{
				switch (code)
				{
				case ADD:
					return "/api/personalexpense/ExpenseEntry/recieptfile?&Description="+URLEncoder.encode(file.getDescription(), "utf-8")+"&EmployeeId="+MainActivity.getId()+"&UploadDate="+convertDate(dateMillis)+"&CompanyId="+file.getCompanyId();
				case EDIT:
					return "/api/personalexpense/ExpenseEntry/recieptfile?Id="+file.getId()+"&Description="+URLEncoder.encode(file.getDescription(), "utf-8")+"&EmployeeId="+MainActivity.getId()+"&UploadDate="+convertDate(dateMillis)+"&CompanyId="+file.getCompanyId();
				case DELETE:
					return "/api/personalexpense/ExpenseEntry/recieptfile/"+file.getId()+"/"+MainActivity.getId();
				default:
					return null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
    	
    	private String convertDate(long millis)
    	{
    		return "/Date("+millis+"-0000)/";
    	}
    	
    }

	@Override
	public void onClick(View v) 
	{
		try
		{
			switch (v.getId())
			{
			case R.id.dateButton:
				Intent getDate = new Intent(CameraActivity.this,DateActivity.class);
				getDate.putExtra(SELECTED_DATE, dateMillis);
				startActivityForResult(getDate, DATE);
				break;
			case R.id.takePhotoButton:
				if (forEntry)
				{
					Intent data = new Intent();
					data.putExtra(CANCELLED_SELECTION, true);
					setResult(RESULT_OK, data);
					finish();
				}
				else
				{
					if (creationMode)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					    builder.setTitle(UPLOAD_TITLE);
					    builder.setItems(new CharSequence[]
					            {"100 %", "50 %", "25 %", "5 %"},
					            new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int which) 
					                {
					                	int resolution = 0;
					                    switch (which) 
					                    {
					                        case 0:
					                            resolution = 100;
					                            break;
					                        case 1:
					                            resolution = 50;
					                            break;
					                        case 2:
					                            resolution = 25;
					                            break;
					                        case 3:
					                            resolution = 5;
					                            break;
					                    }
										new ManipulateReceiptFile().execute(ADD,resolution);
					                }
					            });
					    builder.create().show();
					}
					else if (changedReceipt)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					    builder.setTitle(UPLOAD_TITLE);
					    builder.setItems(new CharSequence[]
					            {"100 %", "50 %", "25 %", "5 %"},
					            new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int which) 
					                {
					                	int resolution = 0;
					                    switch (which) 
					                    {
					                        case 0:
					                            resolution = 100;
					                            break;
					                        case 1:
					                            resolution = 50;
					                            break;
					                        case 2:
					                            resolution = 25;
					                            break;
					                        case 3:
					                            resolution = 5;
					                            break;
					                    }
										new ManipulateReceiptFile().execute(EDIT,resolution);
					                }
					            });
					    builder.create().show();
					}
				}
				break;
			case R.id.deletePic:
				new ManipulateReceiptFile().execute(DELETE);
				break;
			case R.id.createNew:
				if (forEntry)
				{
					if (creationMode)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					    builder.setTitle(UPLOAD_TITLE);
					    builder.setItems(new CharSequence[]
					            {"100 %", "50 %", "25 %", "5 %"},
					            new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int which) 
					                {
					                	int resolution = 0;
					                    switch (which) 
					                    {
					                        case 0:
					                            resolution = 100;
					                            break;
					                        case 1:
					                            resolution = 50;
					                            break;
					                        case 2:
					                            resolution = 25;
					                            break;
					                        case 3:
					                            resolution = 5;
					                            break;
					                    }
					                    new ManipulateReceiptFile().execute(FROM_GALLERY,resolution);
					                }
					            });
					    builder.create().show();
					}
					else if (changedReceipt)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					    builder.setTitle(UPLOAD_TITLE);
					    builder.setItems(new CharSequence[]
					            {"100 %", "50 %", "25 %", "5 %"},
					            new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int which) 
					                {
					                	int resolution = 0;
					                    switch (which) 
					                    {
					                        case 0:
					                            resolution = 100;
					                            break;
					                        case 1:
					                            resolution = 50;
					                            break;
					                        case 2:
					                            resolution = 25;
					                            break;
					                        case 3:
					                            resolution = 5;
					                            break;
					                    }
					                    new ManipulateReceiptFile().execute(EDIT,resolution);
					                }
					            });
					    builder.create().show();
					}
					else
					{
						int resolution = 100;
						new ManipulateReceiptFile().execute(EDIT,resolution);
					}
				}
				else
				{
					if (creationMode)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					    builder.setTitle(UPLOAD_TITLE);
					    builder.setItems(new CharSequence[]
					            {"100 %", "50 %", "25 %", "5 %"},
					            new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int which) 
					                {
					                	int resolution = 0;
					                    switch (which) 
					                    {
					                        case 0:
					                            resolution = 100;
					                            break;
					                        case 1:
					                            resolution = 50;
					                            break;
					                        case 2:
					                            resolution = 25;
					                            break;
					                        case 3:
					                            resolution = 5;
					                            break;
					                    }
					                    new ManipulateReceiptFile().execute(ADD,resolution);
					                }
					            });
					    builder.create().show();
					}
					else if (changedReceipt)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					    builder.setTitle(UPLOAD_TITLE);
					    builder.setItems(new CharSequence[]
					            {"100 %", "50 %", "25 %", "5 %"},
					            new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int which) 
					                {
					                	int resolution = 0;
					                    switch (which) 
					                    {
					                        case 0:
					                            resolution = 100;
					                            break;
					                        case 1:
					                            resolution = 50;
					                            break;
					                        case 2:
					                            resolution = 25;
					                            break;
					                        case 3:
					                            resolution = 5;
					                            break;
					                    }
					                    new ManipulateReceiptFile().execute(EDIT,resolution);
					                }
					            });
					    builder.create().show();
					}
					else
					{
						int resolution = 100;
						new ManipulateReceiptFile().execute(EDIT,resolution);
					}
				}
				
				goingToNew = true;
				break;
			case R.id.portrait:
				if (isTakingNotGetting)
				{
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
					startActivityForResult(cameraIntent, CAMERA_REQUEST); 
				}
				else
				{
					if (creationMode)
					{
						Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, SELECTED_PICTURE);   
					}
					else
					{
						AlertDialog.Builder builderForPortrait = new AlertDialog.Builder(this);
						builderForPortrait.setMessage(TAKE_OR_GET)
					       .setCancelable(true)
					       .setNegativeButton(TAKE_PICTURE, new DialogInterface.OnClickListener() 
					       {
					    	   @Override
					    	   public void onClick(DialogInterface dialog, int which) {
					    		   	dialog.dismiss();
					    		   	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
									startActivityForResult(cameraIntent, CAMERA_REQUEST); 
					    	   }
					       })
					       .setPositiveButton(GET_FROM_GALLERY, new DialogInterface.OnClickListener() 
					       {
					           @Override
					    	   public void onClick(DialogInterface dialog, int id) 
					           {
					        	   dialog.dismiss();
					        	   Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
									photoPickerIntent.setType("image/*");
									startActivityForResult(photoPickerIntent, SELECTED_PICTURE);   
					           }
					       });
						AlertDialog alert = builderForPortrait.create();
						alert.show();
						TextView messageView = (TextView)alert.findViewById(android.R.id.message);
						messageView.setGravity(Gravity.CENTER);
					}
				}
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
