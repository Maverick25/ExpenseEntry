package net.workbook.expenseentry;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.ReceiptFile;
import net.workbook.expenseentry.support.ImageAdapter;
import net.workbook.expenseentry.support.JSONParser;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class ReceiptFilesActivity extends Activity implements OnClickListener,OnItemClickListener,Finals
{
	private GridView gridview;
	private Button takePicture;
	private Button getFromCameraRoll;
	private ArrayList<ReceiptFile> receipts;
	private View appView;
	private View loadingView;
	private ImageAdapter adapter;
	private View buttonsLayout;
	private boolean isForEntry;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_receipt_files);
		
		if (getIntent().getIntExtra(CODE, 20) == LOOK_AT_PIC_FOR_ENTRY)
		{
			new InitializeFiles().execute(LOOK_AT_PIC_FOR_ENTRY);
		}
		else
		{
			new InitializeFiles().execute();
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		try
		{
			if (resultCode == RESULT_OK)
			{
				switch (requestCode)
				{
				case LOOK_AT_PIC_FOR_ENTRY:
					try
					{
						ReceiptFile receipt = data.getParcelableExtra(SELECTED_RECEIPT);
						
						if (receipt == null)
						{
							throw new Exception();
						}
						
						Intent currentData = new Intent();
						currentData.putExtra(SELECTED_RECEIPT, receipt);
						setResult(RESULT_OK,currentData);
						finish();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						finish();
					}
					break;
				case LOOK_AT_PIC:
					new InitializeFiles().execute();
					break;
				default:
					new InitializeFiles().execute();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receipt_files, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		try
		{
			switch (item.getItemId())
			{
			case R.id.syncReceipts:
				if (isForEntry)
				{
					new InitializeFiles().execute(LOOK_AT_PIC_FOR_ENTRY);
				}
				else
				{
					new InitializeFiles().execute();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onClick(View v) 
	{
		try
		{
			switch(v.getId())
			{
			case R.id.takePicture:
				Intent takePicture = new Intent(ReceiptFilesActivity.this, CameraActivity.class);
				takePicture.putExtra(CODE, TAKE_PIC);
				startActivityForResult(takePicture, TAKE_PIC);
				break;
			case R.id.getFromCameraRoll:
				Intent getFromGallery = new Intent(ReceiptFilesActivity.this, CameraActivity.class);
				getFromGallery.putExtra(CODE, GET_PIC);
				startActivityForResult(getFromGallery, GET_PIC);
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> list, View line, int position, long id) 
	{
		Intent lookAtPic = new Intent(ReceiptFilesActivity.this, CameraActivity.class);
		lookAtPic.putExtra(CODE, isForEntry ? LOOK_AT_PIC_FOR_ENTRY : LOOK_AT_PIC);
		lookAtPic.putExtra(SELECTED_RECEIPT, receipts.get(position));
		startActivityForResult(lookAtPic, isForEntry ? LOOK_AT_PIC_FOR_ENTRY : LOOK_AT_PIC);
	}
	
	private class InitializeFiles extends AsyncTask<Integer, Void, Integer>
	{
		
		@Override
		protected Integer doInBackground(Integer... params) 
		{
			try
			{
				appView = findViewById(R.id.appView);
				loadingView = findViewById(R.id.loadingView);
				publishProgress();
				
				buttonsLayout = findViewById(R.id.buttonsLayout);
				takePicture = (Button) findViewById(R.id.takePicture);
			    getFromCameraRoll = (Button) findViewById(R.id.getFromCameraRoll);
			    gridview = (GridView) findViewById(R.id.receiptFiles);
			    
			    takePicture.setOnClickListener(ReceiptFilesActivity.this);
			    getFromCameraRoll.setOnClickListener(ReceiptFilesActivity.this);
			    JSONParser jParser = new JSONParser();
			    try
				{
					receipts = new ArrayList<ReceiptFile>();
					JSONArray dbReceipts = jParser.getJSONArrayFromUrl(MainActivity.getServer(), GET_RECEIPTS+MainActivity.getId());
					for (int i=0; i<dbReceipts.length(); i++)
					{
						JSONObject dbReceipt = dbReceipts.getJSONObject(i);
						
						int id = dbReceipt.getInt("Id");
						
						String fileName;
						try
						{
							fileName = dbReceipt.getString("ReceiptFileName");
						}
						catch (Exception e)
						{
							fileName = NOTHING;
						}
						
						String description;
						try
						{
							description = dbReceipt.getString("Description");
						}
						catch (Exception e)
						{
							description = NOTHING;
						}
						
						int companyId;
						try
						{
							companyId = dbReceipt.getInt("CompanyId");
						}
						catch (Exception e)
						{
							companyId = MainActivity.getCompanyId();
						}
						
						Date uploadDate;
						try
						{
							String dateString = dbReceipt.getString("UploadDate");
							dateString = dateString.substring(0, 19);
							dateString = dateString.replace("/Date(", "");
							long time = Long.parseLong(dateString);
							uploadDate = new Date(time);
						}
						catch (Exception e)
						{
							uploadDate = new Date();
						}
						receipts.add(new ReceiptFile(id, fileName, description, companyId, uploadDate));
					}
					try
					{
						return params[0];
					}
					catch (Exception e)
					{
						return 20;
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
					return null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		

		@Override
		protected void onPostExecute(Integer code) 
		{
			try
			{
				if (code == LOOK_AT_PIC_FOR_ENTRY)
				{
					buttonsLayout.setVisibility(View.GONE);
					isForEntry = true;
				}
				else
				{
					isForEntry = false;
				}
				
				adapter = new ImageAdapter(ReceiptFilesActivity.this, receipts);
				gridview.setAdapter(adapter);

				gridview.setOnItemClickListener(ReceiptFilesActivity.this);
				showProgress(false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				showProgress(false);
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) 
		{
			showProgress(true);
		}
		
	}

	

}
