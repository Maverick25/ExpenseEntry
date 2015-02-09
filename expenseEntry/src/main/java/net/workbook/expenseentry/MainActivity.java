package net.workbook.expenseentry;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.Company;
import net.workbook.expenseentry.model.Creditor;
import net.workbook.expenseentry.model.Currency;
import net.workbook.expenseentry.model.ExpenseEntry;
import net.workbook.expenseentry.model.ExpenseEntryType;
import net.workbook.expenseentry.model.File;
import net.workbook.expenseentry.model.Job;
import net.workbook.expenseentry.model.Location;
import net.workbook.expenseentry.model.ReceiptFile;
import net.workbook.expenseentry.model.Activity;
import net.workbook.expenseentry.support.EntriesAdapter;
import net.workbook.expenseentry.support.JSONParser;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends android.app.Activity 
implements OnClickListener,OnItemClickListener,Finals
{
	private SharedPreferences preferences;
	private static String server;
	private static int id;
	private static int companyId;
	
	private View list;
	private int index;
	private int top;
	
	private View appView;
	private View loadingView;
	private TextView errorMessage;
	
	private StickyListHeadersListView entries;
	private Button addEntry;
	private Button receiptFiles;
	
	private EntriesAdapter adapter;
	private static ArrayList<Currency> currencies;
	private static ArrayList<ExpenseEntryType> types;
	private static ArrayList<ExpenseEntry> expEntries;
	private static ArrayList<ReceiptFile> receipts;
	private static int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		ReceiptFile receipt = getIntent().getParcelableExtra(SELECTED_RECEIPT);
		if (receipt != null)
		{
			setExpEntPosition(999999);
			
			Intent addEntry = new Intent(MainActivity.this,EntryActivity.class);
			addEntry.putExtra(SELECTED_RECEIPT, receipt);
			startActivityForResult(addEntry, 0);
		}
		else
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(this);
			server = getIntent().getStringExtra(SERVER);
			id = getIntent().getIntExtra(ID, 0);
			companyId = getIntent().getIntExtra(COMPANY_ID, 0);
			
			new SyncEntries().execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		try
		{
			switch (resultCode)
			{
			case RESULT_OK:
				new SyncEntries().execute();
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		catch (Exception e)
		{
			super.onActivityResult(requestCode, resultCode, data);
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
	
	public static String getServer()
	{
		return server;
	}
	
	public static int getId()
	{
		return id;
	}
	
	public static int getCompanyId()
	{
		return companyId;
	}
	
	public static ArrayList<ExpenseEntry> getEntries()
	{
		return expEntries;
	}
	
	public static ReceiptFile getReceipt(int id) throws NullPointerException
	{
		for (ReceiptFile receipt : receipts)
		{
			if (receipt.getId() == id)
			{
				return receipt;
			}
		}
		if (id == 0)
		{
			return null;
		}
		throw new NullPointerException();
	}
	
	
	public static int getPosition()
	{
		return position;
	}
	
	public static int getRightExpEntPosition()
	{
		if (position<expEntries.size())
		{
			position++;
		}
		return position-1;
	}
	
	public static int getLeftExpEntPosition()
	{
		if (position>0)
		{
			position--;
		}
		return position+1;
	}
	
	public static void setExpEntPosition(int index)
	{
		position = index;
	}
	
	public static Currency getCurrency(int currencyId)
	{
		for (Currency oneCurrency : currencies)
		{
			if (currencyId == oneCurrency.getId())
			{
				return oneCurrency;
			}
		}
		return new Currency(0, NOTHING, NOTHING, NOTHING, NOTHING, NOTHING, NOTHING);
	}
	
	public static ArrayList<ExpenseEntryType> getTypes()
	{
		return types;
	}
	
	public static ExpenseEntryType getType(int typeId)
	{
		for (ExpenseEntryType type : types)
		{
			if (typeId == type.getId())
			{
				return type;
			}
		}
		return null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> list, View line, int position, long id) 
	{
		try
		{
			setExpEntPosition(position);
			
			Intent editEntry = new Intent(MainActivity.this, EntryActivity.class);
			startActivityForResult(editEntry, 0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View view) 
	{
		try
		{
			switch (view.getId())
			{
			case R.id.addEntry:
				setExpEntPosition(999999);
				
				Intent addEntry = new Intent(MainActivity.this,EntryActivity.class);
				startActivityForResult(addEntry, 0);
				break;
			case R.id.receiptFiles:
				Intent goToReceiptFiles = new Intent(MainActivity.this,ReceiptFilesActivity.class);
				startActivity(goToReceiptFiles);
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		try
		{
			switch (item.getItemId())
			{
			case R.id.sync:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(SYNC_QUESTION)
				       .setCancelable(false)
				       .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
				    	   @Override
				    	   public void onClick(DialogInterface dialog, int which) {
				    		   	dialog.dismiss();
				    	   }
				       })
				       .setPositiveButton(SYNC, new DialogInterface.OnClickListener() {
				           @Override
				    	   public void onClick(DialogInterface dialog, int id) {
				                dialog.dismiss();
				                new SyncEntries().execute();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				TextView messageView = (TextView)alert.findViewById(android.R.id.message);
				messageView.setGravity(Gravity.CENTER);
				return true;
			case R.id.logout:
				Editor edit = preferences.edit();
				edit.putBoolean("saveLogin", false);
				edit.putBoolean("logout", true);
				edit.commit();
				Intent logout = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(logout);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}

	private class SyncEntries extends AsyncTask<Void, Integer, ArrayList<ExpenseEntry>>
    {

		@Override
		protected void onPreExecute() 
		{
			try
			{
				index = entries.getFirstVisiblePosition()+2;
				list = entries.getChildAt(0);
				top = (list == null) ? 0 : list.getTop();
			}
			catch (Exception e)
			{}
		}

		@Override
		protected ArrayList<ExpenseEntry> doInBackground(Void... params) 
		{
			try
			{
				appView = findViewById(R.id.appView);
				loadingView = findViewById(R.id.loadingView);
				errorMessage = (TextView) findViewById(R.id.errorMessage);
				
				publishProgress();
				
				addEntry = (Button) findViewById(R.id.addEntry);
				receiptFiles = (Button) findViewById(R.id.receiptFiles);
				
				addEntry.setOnClickListener(MainActivity.this);
				receiptFiles.setOnClickListener(MainActivity.this);
				
				entries = (StickyListHeadersListView) findViewById(R.id.entryList);
				
				JSONParser jParser = new JSONParser();
				ArrayList<ExpenseEntry> entries = new ArrayList<ExpenseEntry>();
				try
				{
					currencies = new ArrayList<Currency>();
					JSONArray dbCurrencies = jParser.getJSONArrayFromUrl(server, GET_CURRENCIES);
					for (int i=0; i<dbCurrencies.length(); i++)
					{
						JSONObject dbCurrency = dbCurrencies.getJSONObject(i);
						int id = dbCurrency.getInt("Id");
						String name = dbCurrency.getString("Name");
						String isoCode = dbCurrency.getString("IsoCode");
						String unit;
						try
						{
							unit = dbCurrency.getString("CurrencyUnit");
						}
						catch (Exception e)
						{
							unit = NOTHING;
						}
						String fraction;
						try
						{
							fraction = dbCurrency.getString("CurrencyFraction");	
						}
						catch (Exception e)
						{
							fraction = NOTHING;
						}
						String unitPlural;
						try
						{
							unitPlural = dbCurrency.getString("CurrencyUnitPlural");
						}
						catch (Exception e)
						{
							unitPlural = NOTHING;
						}
						String fractionPlural;
						try
						{
							fractionPlural = dbCurrency.getString("CurrencyFractionPlural");
						}
						catch (Exception e)
						{
							fractionPlural = NOTHING;
						}
						currencies.add(new Currency(id, name, isoCode, unit, fraction, unitPlural, fractionPlural));
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					receipts = new ArrayList<ReceiptFile>();
					JSONArray dbReceipts = jParser.getJSONArrayFromUrl(server, GET_RECEIPTS+id);
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
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					types = new ArrayList<ExpenseEntryType>();
					JSONArray dbTypes = jParser.getJSONArrayFromUrl(server, GET_TYPES+id);
					for (int i=0; i<dbTypes.length(); i++)
					{
						JSONObject dbType = dbTypes.getJSONObject(i);
						
						int id = dbType.getInt("ExpenseEntryTypeId");
						String title = dbType.getString("Title");
						int postType = dbType.getInt("ExpensePostType");
						boolean isActive = dbType.getBoolean("IsActive");
						
						types.add(new ExpenseEntryType(id, title, postType, isActive));
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				
				try
				{	
					int index = 0;
					JSONArray dbEntries = jParser.getJSONArrayFromUrl(server, GET_ENTRIES+id);
					for (int i=0; i<dbEntries.length(); i++)
					{
						JSONObject dbEntry = dbEntries.getJSONObject(i);
						int id = dbEntry.getInt("Id");
						boolean isApproved = dbEntry.getBoolean("IsApproved");
						Date voucherDate;
						try
						{
							String dateString = dbEntry.getString("VoucherDate");
							dateString = dateString.substring(0, 19);
							dateString = dateString.replace("/Date(", "");
							long time = Long.parseLong(dateString);
							voucherDate = new Date(time);
						}
						catch (Exception e)
						{
							voucherDate = new Date();
						}
						int voucherCompanyId;
						try
						{
							voucherCompanyId = dbEntry.getInt("VoucherCompanyId");
						}
						catch (Exception e)
						{
							voucherCompanyId = 0;
						}
						
						String description;
						try
						{
							description = dbEntry.getString("Description");
						}
						catch (Exception e)
						{
							description = NOTHING;
						}
						int totalAmount;
						try
						{
							totalAmount = dbEntry.getInt("CurrencyAmount");
						}
						catch (Exception e)
						{
							totalAmount = 0;
						}
						int approvalStatus = dbEntry.getInt("ApprovalStatus");
						int currencyId;
						try
						{
							currencyId = dbEntry.getInt("CurrencyID");
						}
						catch (Exception e)
						{
							currencyId = 0;
						}
						
						Currency currency = getCurrency(currencyId);
						
						int expEntTypeId;
						try
						{
							expEntTypeId = dbEntry.getInt("ExpenseEntryTypeId");
						}
						catch (Exception e)
						{
							expEntTypeId = 0;
						}
						
						ExpenseEntryType type = getType(expEntTypeId);
						
						int jobId;
						try
						{
							jobId = dbEntry.getInt("JobID");
						}
						catch (Exception e)
						{
							jobId = 0;
						}
						
						String jobName;
						try
						{
							jobName = dbEntry.getString("jobname");
						}
						catch (Exception e)
						{
							jobName = NOTHING;
						}
						String customerName;
						try
						{
							customerName = dbEntry.getString("CustomerName");
						}
						catch (Exception e)
						{
							customerName = NOTHING;
						}
						Job job = new Job(jobId, jobName, customerName,voucherCompanyId);
						
						int creditorId;
						try
						{
							creditorId = dbEntry.getInt("CreditorID");
						}
						catch (Exception e)
						{
							creditorId = 0;
						}
						String creditorNumber;
						try
						{
							creditorNumber = dbEntry.getString("CreditorNumber");
						}
						catch (Exception e)
						{
							creditorNumber = NOTHING;
						}
						String creditorName;
						try
						{
							creditorName = dbEntry.getString("CreditorName");
						}
						catch (Exception e)
						{
							creditorName = NOTHING;
						}
						int companyId;
						try
						{
							companyId = dbEntry.getInt("CompanyId");
						}
						catch (Exception e)
						{
							companyId = 0;
						}
						Creditor creditor = new Creditor(creditorId, creditorName, creditorNumber, companyId);
						
						int activityId;
						try
						{
							activityId = dbEntry.getInt("ActivityID");
						}
						catch (Exception e)
						{
							activityId = 0;
						}
						String activityText;
						try
						{
							activityText = dbEntry.getString("ActivityTxt");
						}
						catch (Exception e)
						{
							activityText = NOTHING;
						}
						Activity activity = new Activity(activityId, activityText);
						
						String companyName;
						try
						{
							companyName = dbEntry.getString("CompanyName");
						}
						catch (Exception e)
						{
							companyName = NOTHING;
						}
						Company company = new Company(companyId, companyName);
						
						int receiptFileId;
						try
						{
							receiptFileId = dbEntry.getInt("ReciptFileId");
						}
						catch (Exception e)
						{
							receiptFileId = 0;
						}
						
						ReceiptFile receipt;
						File file = null;
						try
						{
							receipt = getReceipt(receiptFileId);
							if (receipt == null)
							{
								String fileName;
								try
								{
									fileName = dbEntry.getString("ReceiptFile");
									file = new File(fileName);
								}
								catch (Exception e)
								{
								}
							}
						}
						catch (NullPointerException e)
						{
							try
							{
								String filename;
								try
								{
									filename = dbEntry.getString("ReceiptFile");
								}
								catch (Exception ee)
								{
									filename = NOTHING;
								}
								
								JSONObject dbReceipt = jParser.getJSONFromUrl(requestReceipt(receiptFileId));
								
								String receiptDescription;
								try
								{
									receiptDescription = dbReceipt.getString("Description");
								}
								catch (Exception ee)
								{
									receiptDescription = NOTHING;
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
								catch (Exception ee)
								{
									uploadDate = new Date();
								}
								
								int receiptCompanyId;
								try
								{
									receiptCompanyId = dbReceipt.getInt("CompanyId");
								}
								catch (Exception ee)
								{
									receiptCompanyId = getCompanyId();
								}
								
								receipt = new ReceiptFile(receiptFileId, filename, receiptDescription, receiptCompanyId, uploadDate);
							}
							catch (Exception ee)
							{
								receipt = null;
								ee.printStackTrace();
							}
						}
						
						int locationId;
						try
						{
							locationId = dbEntry.getInt("LocationId");
						}
						catch (Exception e)
						{
							locationId = 0;
						}
						String locationName;
						try
						{
							locationName = dbEntry.getString("LocationName");
						}
						catch (Exception e)
						{
							locationName = NOTHING;
						}
						Location location = new Location(locationId, locationName);
						
						int importDtlId;
						try
						{
							importDtlId = dbEntry.getInt("ExpenseEntryImportDtlId");
						}
						catch (Exception e)
						{
							importDtlId = 0;
						}
						
						ExpenseEntry expEntry;
						if (file == null)
						{
							expEntry = new ExpenseEntry(id, type, job, voucherDate, voucherCompanyId, description, currency, totalAmount, creditor, isApproved, activity, approvalStatus, receipt, location, company, importDtlId);
						}
						else
						{
							expEntry = new ExpenseEntry(id, type, job, voucherDate, voucherCompanyId, description, currency, totalAmount, creditor, isApproved, activity, approvalStatus, file, location, company, importDtlId);
						}
						
						if (expEntry.getApprovalStatus()==10)
						{
							entries.add(index, expEntry);
							index++;
						}
						else
						{
							entries.add(expEntry);
						}
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				
				
				
				return entries;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			try
			{
				showProgress(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(ArrayList<ExpenseEntry> result) 
		{	
			try
			{	
				if (result == null || result.size()==0)
				{
					throw new NullPointerException();
				}
				expEntries = result;
				adapter = new EntriesAdapter(MainActivity.this,result);
				entries.setAdapter(adapter);
				entries.setOnItemClickListener(MainActivity.this);
				errorMessage.setVisibility(View.GONE);
				try
				{
					entries.setSelectionFromTop(index, top);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				showProgress(false);
			}
			catch (NullPointerException e)
			{
				errorMessage.setVisibility(View.VISIBLE);
				e.printStackTrace();
				showProgress(false);
			}
			catch (Exception e)
			{
				errorMessage.setVisibility(View.VISIBLE);
				e.printStackTrace();
				showProgress(false);
			}
		}	
		
		public String requestReceipt(int id)
		{
			return "http://"+getServer()+"/api/personalexpense/ExpenseEntry/recieptfile/"+id;
		}
    }

	
}
