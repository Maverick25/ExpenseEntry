package net.workbook.expenseentry;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
import net.workbook.expenseentry.support.JSONParser;
import net.workbook.expenseentry.support.SingleEntryAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class EntryActivity extends Activity 
implements OnClickListener,OnItemClickListener,Finals
{
	private TextView status;
	public TextView state;
	private ListView categories;
	private ImageButton lock;
	private Button cancel;
	public Button save;
	private TextView kind;
	private SingleEntryAdapter adapter;
	private boolean isJobType;
	private boolean creationMode;
	private boolean noChangesYet;
	private boolean duplicate;
	private boolean isCreditType;
	public static boolean isLocked;
	public static int companyId;
	private int resolution;
	
	private ArrayList<Integer> changedStuff;
	
	private ExpenseEntry entry;
	
	private Company company;
	private ExpenseEntryType type;
	private Job job;
	private net.workbook.expenseentry.model.Activity activity;
	private Date voucherDate;
	private Location location;
	private Currency currency;
	private double totalAmount;
	private String description;
	private Creditor creditor;
	public static File file;
	public static Bitmap bitmap;
	
	private String rejectComment;
	
	private boolean editForApproval;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_entry);
		
		categories = (ListView) findViewById(R.id.categories);
		status = (TextView) findViewById(R.id.status);
		lock = (ImageButton) findViewById(R.id.lock);
		cancel = (Button) findViewById(R.id.cancel);
		save = (Button) findViewById(R.id.save);
		state = (TextView) findViewById(R.id.entryState);
		kind = (TextView) findViewById(R.id.kind);
		
		resolution = 100;
		
		isJobType = true;
		int position;
		try
		{
			position = MainActivity.getPosition();
		}
		catch (Exception e)
		{
			position = 999999;
		}
		
		if (position != 999999)
		{
			entry = MainActivity.getEntries().get(position);
			creationMode = false;
			noChangesYet = true;
			if (entry.getType().getId()!=1)
			{
				isJobType = false;
			}
			companyId = entry.getCompany().getId();
			setTitle("Edit Entry");
			try
			{
				state.setText("Entry no. "+(MainActivity.getPosition()+1)+" of "+MainActivity.getEntries().size());
			}
			catch (Exception e) {}
			
			try
        	{
        		if (entry.getImportDtlId()==0)
        		{
        			throw new NullPointerException();
        		}
        		kind.setText("Credit Card Completion");
        		isCreditType = true;
        		cancel.setText("Reject");
        	}
        	catch (NullPointerException e)
        	{
        		kind.setText("Manual Registration");
        		isCreditType = false;
        		cancel.setText("Delete");
        	}
        	catch (Exception e)
        	{
        		kind.setText("Manual Registration");
        		isCreditType = false;
        		cancel.setText("Delete");
        	}
			
			if (entry.getApprovalStatus() == 10)
			{
				status.setText("Status: Under preparation");
				lock.setImageResource(R.drawable.ic_unlock);
				categories.setOnItemClickListener(this);
				isLocked = false;
			}
			else
			{
				status.setText("Status: For approval");
				lock.setImageResource(R.drawable.ic_lock);
				categories.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						TextView titleView = new TextView(EntryActivity.this);
						titleView.setText(LOCKED_ENTRY_TITLE);
						titleView.setGravity(Gravity.CENTER_HORIZONTAL);
						titleView.setTextSize(20);
					
						AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
						builder.setCustomTitle(titleView)
						   		.setMessage(LOCKED_ENTRY)
						   		.setCancelable(false)
						   		.setNeutralButton(DISMISS, new DialogInterface.OnClickListener() {
						   		   @Override
						    	   public void onClick(DialogInterface dialog, int which) {
						    		   	dialog.dismiss();
						    	   }
						   		});
						final AlertDialog alert = builder.create();
						alert.show();
					
						TextView messageView = (TextView)alert.findViewById(android.R.id.message);
						messageView.setGravity(Gravity.CENTER);
						messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0);
					}
				});
				isLocked = true;
			}
		}
		else
		{
			creationMode = true;
			noChangesYet = false;
			entry = new ExpenseEntry();
			entry.setType(new ExpenseEntryType(1, "Job", 1, true));
			entry.setVoucherDate(new Date());
			
			new ManipulateEntry().execute(ADD,MainActivity.getCompanyId());
			
			setTitle("Add Entry");
			state.setText("New Entry");
			status.setText("Status: Under preparation");
			lock.setImageResource(R.drawable.ic_unlock);
			isLocked = false;
			categories.setOnItemClickListener(this);
			cancel.setText("Delete");
			kind.setText("Manual Registration");
		}
		changedStuff = new ArrayList<Integer>();
		adapter = new SingleEntryAdapter(this,entry);
		categories.setAdapter(adapter);
		
		lock.setOnClickListener(this);
		cancel.setOnClickListener(this);
		save.setOnClickListener(this);
	}
	
	@Override
	public void onBackPressed() 
	{
		try
		{
			final Intent current = new Intent();
			if (state.getText().toString().equals("Not Saved"))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(SURE_CANCEL)
			       .setCancelable(false)
			       .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
			    	   @Override
			    	   public void onClick(DialogInterface dialog, int which) {
			    		   dialog.dismiss();
			    	   }
			       })
			       .setPositiveButton("Cancel Entry", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							reverseChanges();
							setResult(RESULT_CANCELED,current);
							EntryActivity.super.onBackPressed();
						}
			       });
				AlertDialog alert = builder.create();
				alert.show();
				TextView messageView = (TextView)alert.findViewById(android.R.id.message);
				messageView.setGravity(Gravity.CENTER);
				
			}
			else
			{
				if (noChangesYet)
				{
					setResult(RESULT_CANCELED,current);
				}
				else
				{
					setResult(RESULT_OK,current);
				}
				
				super.onBackPressed();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.entry, menu);
		
		MenuItem left = menu.findItem(R.id.leftEntry);
		MenuItem right = menu.findItem(R.id.rightEntry);
		
		if (creationMode)
		{
			left.setVisible(false);
			right.setVisible(false);
		}
		else
		{
			left.setVisible(true);
			right.setVisible(true);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		try
		{
			switch(item.getItemId())
			{
			case R.id.leftEntry:
				if (state.getText().toString().equals("Not Saved"))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(SURE_MOVE)
				       .setCancelable(false)
				       .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
				    	   @Override
				    	   public void onClick(DialogInterface dialog, int which) {
				    		   	dialog.dismiss();
				    	   }
				       })
				       .setPositiveButton("Move to next Entry", new DialogInterface.OnClickListener() {
				           @Override
				    	   public void onClick(DialogInterface dialog, int id) {
				        	   dialog.dismiss();
				        	   try
								{
				        		   	reverseChanges();
									entry = MainActivity.getEntries().get(MainActivity.getLeftExpEntPosition()-1);
									goToPreviousOne();
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
				           }
				       });
					AlertDialog alert = builder.create();
					alert.show();
					TextView messageView = (TextView)alert.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
				}
				else
				{
					try
					{
						entry = MainActivity.getEntries().get(MainActivity.getLeftExpEntPosition()-1);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return super.onOptionsItemSelected(item);
					}
					goToPreviousOne();
				}
				return true;
			case R.id.rightEntry:
				if (state.getText().toString().equals("Not Saved"))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(SURE_MOVE)
				       .setCancelable(false)
				       .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
				    	   @Override
				    	   public void onClick(DialogInterface dialog, int which) {
				    		   	dialog.dismiss();
				    	   }
				       })
				       .setPositiveButton("Move to next Entry", new DialogInterface.OnClickListener() {
				           @Override
				    	   public void onClick(DialogInterface dialog, int id) {
				        	   dialog.dismiss();
				        	   try
								{
				        		   	reverseChanges();
									entry = MainActivity.getEntries().get(MainActivity.getRightExpEntPosition()+1);
									goToNextOne();
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
				           }
				       });
					AlertDialog alert = builder.create();
					alert.show();
					TextView messageView = (TextView)alert.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
				}
				else
				{
					try
					{
						entry = MainActivity.getEntries().get(MainActivity.getRightExpEntPosition()+1);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return super.onOptionsItemSelected(item);
					}
					goToNextOne();
				}
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
	
	public static int getCompanyId()
	{
		return companyId;
	}
	
	public static Bitmap getBitmap()
	{
		return bitmap;
	}
	
	private void goToNextOne()
	{
		adapter = new SingleEntryAdapter(this, entry);
		categories.setAdapter(adapter);
		state.setText("Entry no. "+(MainActivity.getPosition()+1)+" of "+MainActivity.getEntries().size());
		noChangesYet = true;
		try
    	{
    		if (entry.getImportDtlId()==0)
    		{
    			throw new NullPointerException();
    		}
    		kind.setText("Credit Card Completion");
    		isCreditType = true;
    		cancel.setText("Reject");
    	}
    	catch (NullPointerException e)
    	{
    		kind.setText("Manual Registration");
    		isCreditType = false;
    		cancel.setText("Delete");
    	}
    	catch (Exception e)
    	{
    		kind.setText("Manual Registration");
    		isCreditType = false;
    		cancel.setText("Delete");
    	}
		
		if (entry.getApprovalStatus() != 10)
		{
			status.setText("Status: For approval");
			lock.setImageResource(R.drawable.ic_lock);
			categories.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					TextView titleView = new TextView(EntryActivity.this);
					titleView.setText(LOCKED_ENTRY_TITLE);
					titleView.setGravity(Gravity.CENTER_HORIZONTAL);
					titleView.setTextSize(20);
				
					AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
					builder.setCustomTitle(titleView)
					   		.setMessage(LOCKED_ENTRY)
					   		.setCancelable(false)
					   		.setNeutralButton(DISMISS, new DialogInterface.OnClickListener() {
					   		   @Override
					    	   public void onClick(DialogInterface dialog, int which) {
					    		   	dialog.dismiss();
					    	   }
					   		});
					final AlertDialog alert = builder.create();
					alert.show();
				
					TextView messageView = (TextView)alert.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
					messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0);
				}
			});
			isLocked = true;
		}
		if (entry.getType().getId()==1)
		{
			isJobType = true;
		}
		else
		{
			isJobType = false;
		}
	}
	
	private void goToPreviousOne()
	{
		adapter = new SingleEntryAdapter(this, entry);
		categories.setAdapter(adapter);
		state.setText("Entry no. "+(MainActivity.getPosition()+1)+" of "+MainActivity.getEntries().size());
		noChangesYet = true;
		try
    	{
    		if (entry.getImportDtlId()==0)
    		{
    			throw new NullPointerException();
    		}
    		kind.setText("Credit Card Completion");
    		isCreditType = true;
    		cancel.setText("Reject");
    	}
    	catch (NullPointerException e)
    	{
    		kind.setText("Manual Registration");
    		isCreditType = false;
    		cancel.setText("Delete");
    	}
    	catch (Exception e)
    	{
    		kind.setText("Manual Registration");
    		isCreditType = false;
    		cancel.setText("Delete");
    	}
		
		if (entry.getApprovalStatus() == 10)
		{
			status.setText("Status: Under Preparation");
			save.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save_disk, 0, 0);
			save.setText("Save");
			lock.setImageResource(R.drawable.ic_unlock);
			categories.setOnItemClickListener(this);
			isLocked = false;
		}
		
		if (entry.getType().getId()==1)
		{
			isJobType = true;
		}
		else
		{
			isJobType = false;
		}
	}
	
	private void reverseChanges()
	{
		try
		{
			for (int i : changedStuff)
			{
				switch(i)
				{
				case COMPANY:
					entry.setCompany(company);
					break;
				case EXPENSE_TYPE:
					entry.setType(type);
					if (type.getId() == 1)
					{
						isJobType = true;
					}
					else
					{
						isJobType = false;
					}
					break;
				case JOB:
					entry.setJob(job);
					break;
				case ACTIVITY:
					entry.setActivity(activity);
					break;
				case DATE:
					entry.setVoucherDate(voucherDate);
					break;
				case LOCATION:
					entry.setLocation(location);
					break;
				case CURRENCY:
					entry.setCurrency(currency);
					break;
				case TOTAL_AMOUNT:
					entry.setTotalAmount(totalAmount);
					break;
				case DESCRIPTION:
					entry.setDescription(description);
					break;
				case CREDITOR:
					entry.setCreditor(creditor);
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		company = null;
		type = null;
		job = null;
		activity = null;
		voucherDate = null;
		location = null;
		currency = null;
		totalAmount = 0;
		description = null;
		creditor = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		try
		{
			if (resultCode == RESULT_OK)
			{
				ReceiptFile receipt = null;
				switch(requestCode)
				{
				case COMPANY:
					Company company = data.getParcelableExtra(SELECTED_ITEM);
					entry.setCompany(company);
					companyId = entry.getCompany().getId();
					adapter.notifyDataSetChanged();
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == COMPANY)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(COMPANY);
						changedStuff.add(JOB);
					}
					if (company.getId() != entry.getJob().getCompanyId())
					{
						entry.setJob(null);
					}
					if (company.getId() != entry.getCreditor().getCompanyId())
					{
						entry.setCreditor(null);
					}
					state.setText("Not Saved");
					break;
				case EXPENSE_TYPE:
					ExpenseEntryType type = data.getParcelableExtra(SELECTED_ITEM);
					entry.setType(type);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == EXPENSE_TYPE)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(EXPENSE_TYPE);
					}
					if (entry.getType().getId()==1)
					{
						isJobType = true;
					}
					else
					{
						isJobType = false;
						entry.setJob(null);
						entry.setActivity(null);
					}
					state.setText("Not Saved");
					break;
				case JOB:
					Job job = data.getParcelableExtra(SELECTED_ITEM);
					entry.setJob(job);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == JOB)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(JOB);
					}
					state.setText("Not Saved");
					break;
				case ACTIVITY:
					net.workbook.expenseentry.model.Activity activity = data.getParcelableExtra(SELECTED_ITEM);
					entry.setActivity(activity);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == ACTIVITY)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(ACTIVITY);
					}
					state.setText("Not Saved");
					break;
				case DATE:
					Long time = data.getLongExtra(SELECTED_DATE, 0);
					entry.setVoucherDate(new Date(time));
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == DATE)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(DATE);
					}
					state.setText("Not Saved");
					break;
				case LOCATION:
					Location location = data.getParcelableExtra(SELECTED_ITEM);
					entry.setLocation(location);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == LOCATION)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(LOCATION);
					}
					state.setText("Not Saved");
					break;
				case CURRENCY:
					Currency currency = data.getParcelableExtra(SELECTED_ITEM);
					entry.setCurrency(currency);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == CURRENCY)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(CURRENCY);
					}
					state.setText("Not Saved");
					break;
				case TOTAL_AMOUNT:
					double amount = data.getDoubleExtra(SELECTED_AMOUNT, 0);
					entry.setTotalAmount(Math.ceil(amount * 2) / 2);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == TOTAL_AMOUNT)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(TOTAL_AMOUNT);
					}
					state.setText("Not Saved");
					break;
				case DESCRIPTION:
					String description = data.getStringExtra(ADDED_DESCRIPTION);
					entry.setDescription(description);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == DESCRIPTION)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(DESCRIPTION);
					}
					state.setText("Not Saved");
					break;
				case CREDITOR:
					Creditor creditor = data.getParcelableExtra(SELECTED_ITEM);
					entry.setCreditor(creditor);
					duplicate = false;
					for (int i : changedStuff)
					{
						if (i == CREDITOR)
						{
							duplicate = true;
							break;
						}
					}
					if (!duplicate)
					{
						changedStuff.add(CREDITOR);
					}
					state.setText("Not Saved");
					break;
				case TAKE_PIC_FOR_ENTRY:
    				bitmap = (Bitmap) data.getExtras().get("data"); 
    				
    				AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
				    builder.setTitle(UPLOAD_TITLE);
				    builder.setItems(new CharSequence[]
				            {"100 %", "50 %", "25 %", "5 %"},
				            new DialogInterface.OnClickListener() {
				                public void onClick(DialogInterface dialog, int which) 
				                {
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
									
				                    adapter.notifyDataSetChanged();
									
									new UseReceiptFile().execute();
				                }
				            });
				    builder.create().show();
        			
        			break;
				case GET_PIC_FOR_ENTRY:
					receipt = data.getParcelableExtra(SELECTED_RECEIPT);
					resolution = data.getIntExtra(SELECTED_RESOLUTION, 0);
					
					bitmap = BitmapFactory.decodeByteArray(receipt.getBitmapBytes(), 0, receipt.getBitmapBytes().length);
					
					adapter.notifyDataSetChanged();
					
					new UseReceiptFile().execute();
					
					break;
				case LOOK_AT_PIC_FOR_ENTRY:
					resolution = 100;
					receipt = data.getParcelableExtra(SELECTED_RECEIPT);
					
					bitmap = BitmapFactory.decodeByteArray(receipt.getBitmapBytes(), 0, receipt.getBitmapBytes().length);
					
					entry.setFile(receipt);
					adapter.notifyDataSetChanged();
					
					new UseReceiptFile().execute(receipt);
				
					break;
				}
				adapter.notifyDataSetChanged();
				noChangesYet = false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> list, View line, int position, long id) 
	{
		try
		{
			if (isJobType)
			{
				switch(position)
				{
				case COMPANY:
					if (!isCreditType)
					{
						try
						{
							if (company.getId() == 0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							company = entry.getCompany();
							job = entry.getJob();
						}
						Intent getCompany = new Intent(EntryActivity.this, SelectionActivity.class);
						getCompany.putExtra(CATEGORY, COMPANY);
						startActivityForResult(getCompany, COMPANY);
					}
					break;
				case EXPENSE_TYPE:
					try
					{
						if (type.getId() == 0)
						{
							throw new Exception();
						}
					}
					catch (Exception e)
					{
						type = entry.getType();
					}
					Intent getExpenseType = new Intent(EntryActivity.this, SelectionActivity.class);
					getExpenseType.putExtra(CATEGORY, EXPENSE_TYPE);
					startActivityForResult(getExpenseType, EXPENSE_TYPE);
					break;
				case JOB:
					try
					{
						if (job.getId() == 0)
						{
							throw new Exception();
						}
					}
					catch (Exception e)
					{
						job = entry.getJob();
					}
					Intent getJob = new Intent(EntryActivity.this, SelectionActivity.class);
					getJob.putExtra(CATEGORY, JOB);
					getJob.putExtra(COMPANY_ID, entry.getCompany().getId());
					startActivityForResult(getJob, JOB);
					break;
				case ACTIVITY:
					try
					{
						if (activity.getId() == 0)
						{
							throw new Exception();
						}
					}
					catch (Exception e)
					{
						activity = entry.getActivity();
					}
					Intent getActivity = new Intent(EntryActivity.this, SelectionActivity.class);
					getActivity.putExtra(CATEGORY, ACTIVITY);
					startActivityForResult(getActivity, ACTIVITY);
					break;
				case DATE:
					if (!isCreditType)
					{
						try
						{
							if (voucherDate == null)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							voucherDate = entry.getVoucherDate();
						}
						Intent getDate = new Intent(EntryActivity.this, DateActivity.class);
						try
						{
							getDate.putExtra(SELECTED_DATE,entry.getVoucherDate().getTime());
						}
						catch (NullPointerException e)
						{
							e.printStackTrace();
						}
						startActivityForResult(getDate,DATE);
					}
					break;
				case LOCATION:
					if (!isCreditType)
					{
						try
						{
							if (location.getId()==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							location = entry.getLocation();
						}
						Intent getLocation = new Intent(EntryActivity.this, SelectionActivity.class);
						getLocation.putExtra(CATEGORY, LOCATION);
						startActivityForResult(getLocation, LOCATION);
					}
					break;
				case CURRENCY:
					if (!isCreditType)
					{
						try
						{
							if (currency.getId()==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							currency = entry.getCurrency();
						}
						Intent getCurrency = new Intent(EntryActivity.this, SelectionActivity.class);
						getCurrency.putExtra(CATEGORY, CURRENCY);
						startActivityForResult(getCurrency, CURRENCY);
					}
					break;
				case TOTAL_AMOUNT:
					if (!isCreditType)
					{
						try
						{
							if (totalAmount==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							totalAmount = entry.getTotalAmount();
						}
						Intent getAmount = new Intent(EntryActivity.this, AmountActivity.class);
						try
						{
							getAmount.putExtra(CHANGED_AMOUNT, entry.getTotalAmount());
						}
						catch (Exception e) {}
						startActivityForResult(getAmount,TOTAL_AMOUNT);
					}
					break;
				case DESCRIPTION:
					try
					{
						if (description.equals(NOTHING))
						{
							throw new Exception();
						}
					}
					catch (Exception e)
					{
						description = entry.getDescription();
					}
					Intent addDescription = new Intent(EntryActivity.this, DescriptionActivity.class);
					try
					{
						addDescription.putExtra(ADDED_DESCRIPTION, entry.getDescription());
					}
					catch (NullPointerException e) {}
					startActivityForResult(addDescription, DESCRIPTION);
					break;
				case CREDITOR:
					if (!isCreditType)
					{
						try
						{
							if (creditor.getId()==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							creditor = entry.getCreditor();
						}
						Intent getCreditor = new Intent(EntryActivity.this, SelectionActivity.class);
						getCreditor.putExtra(CATEGORY, CREDITOR);
						startActivityForResult(getCreditor, CREDITOR);
					}
					break;
				}
			}
			else
			{
				switch(position)
				{
				case COMPANY:
					if (!isCreditType)
					{
						try
						{
							if (company.getId() == 0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							company = entry.getCompany();
						}
						Intent getCompany = new Intent(EntryActivity.this, SelectionActivity.class);
						getCompany.putExtra(CATEGORY, COMPANY);
						startActivityForResult(getCompany, COMPANY);
					}
					break;
				case EXPENSE_TYPE:
					try
					{
						if (type.getId() == 0)
						{
							throw new Exception();
						}
					}
					catch (Exception e)
					{
						type = entry.getType();
					}
					Intent getExpenseType = new Intent(EntryActivity.this, SelectionActivity.class);
					getExpenseType.putExtra(CATEGORY, EXPENSE_TYPE);
					startActivityForResult(getExpenseType, EXPENSE_TYPE);
					break;
				case DATE-2:
					if (!isCreditType)
					{
						try
						{
							if (voucherDate == null)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							voucherDate = entry.getVoucherDate();
						}
						Intent getDate = new Intent(EntryActivity.this, DateActivity.class);
						startActivityForResult(getDate,DATE);
					}
					break;
				case LOCATION-2:
					if (!isCreditType)
					{
						try
						{
							if (location.getId()==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							location = entry.getLocation();
						}
						Intent getLocation = new Intent(EntryActivity.this, SelectionActivity.class);
						getLocation.putExtra(CATEGORY, LOCATION);
						startActivityForResult(getLocation, LOCATION);
					}
					break;
				case CURRENCY-2:
					if (!isCreditType)
					{
						try
						{
							if (currency.getId()==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							currency = entry.getCurrency();
						}
						Intent getCurrency = new Intent(EntryActivity.this, SelectionActivity.class);
						getCurrency.putExtra(CATEGORY, CURRENCY);
						startActivityForResult(getCurrency, CURRENCY);
					}
					break;
				case TOTAL_AMOUNT-2:
					if (!isCreditType)
					{
						try
						{
							if (totalAmount==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							totalAmount = entry.getTotalAmount();
						}
						Intent getAmount = new Intent(EntryActivity.this, AmountActivity.class);
						try
						{
							getAmount.putExtra(CHANGED_AMOUNT, entry.getTotalAmount());
						}
						catch (Exception e) {}
						startActivityForResult(getAmount,TOTAL_AMOUNT);
					}
					break;
				case DESCRIPTION-2:
					try
					{
						if (description.equals(NOTHING))
						{
							throw new Exception();
						}
					}
					catch (Exception e)
					{
						description = entry.getDescription();
					}
					Intent addDescription = new Intent(EntryActivity.this, DescriptionActivity.class);
					try
					{
						addDescription.putExtra(ADDED_DESCRIPTION, entry.getDescription());
					}
					catch (Exception e) {}
					startActivityForResult(addDescription, DESCRIPTION);
					break;
				case CREDITOR-2:
					if (!isCreditType)
					{
						try
						{
							if (creditor.getId()==0)
							{
								throw new Exception();
							}
						}
						catch (Exception e)
						{
							creditor = entry.getCreditor();
						}
						Intent getCreditor = new Intent(EntryActivity.this, SelectionActivity.class);
						getCreditor.putExtra(CATEGORY, CREDITOR);
						startActivityForResult(getCreditor, CREDITOR);
					}
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(EntryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) 
	{
		try
		{
			switch (v.getId())
			{
			case R.id.cancel:
				if (isCreditType)
				{
					final EditText input = new EditText(this);
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					input.setInputType(InputType.TYPE_CLASS_TEXT);
					builder.setView(input);
					builder.setMessage(SURE_REJECT);
					builder.setPositiveButton(REJECT_WORD, new DialogInterface.OnClickListener() { 
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					        rejectComment = input.getText().toString();
					        new ManipulateEntry().execute(REJECT);
				        	System.out.println("I am rejecting"); 
				        	dialog.dismiss();
					    }
					});
					builder.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					        dialog.cancel();
					    }
					});
				    builder.setCancelable(false);
					AlertDialog alert = builder.create();
					alert.show();
					TextView messageView = (TextView)alert.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(SURE_DELETE)
				       .setCancelable(false)
				       .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
				    	   @Override
				    	   public void onClick(DialogInterface dialog, int which) {
				    		   	dialog.dismiss();
				    	   }
				       })
				       .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				           @Override
				    	   public void onClick(DialogInterface dialog, int id) {
				        	   new ManipulateEntry().execute(DELETE);
				        	   System.out.println("I am deleting"); 
				        	   dialog.dismiss();
				           }
				       });
					AlertDialog alert = builder.create();
					alert.show();
					TextView messageView = (TextView)alert.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
				}
				break;
			case R.id.save:
				if (entry.getApprovalStatus() != 10)
				{
					entry.setApprovalStatus(10);
				}
				new ManipulateEntry().execute(EDIT);
				System.out.println("I am editing");
				break;
			case R.id.lock:
				lock.setImageResource(R.drawable.ic_unlock);
				entry.setApprovalStatus(10);
				save.setText("Save");
				save.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save_disk, 0, 0);
				categories.setOnItemClickListener(this);	
				isLocked = false;
				adapter.notifyDataSetChanged();
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean readyForApproval()
	{
		try
		{
			if (entry.getApprovalStatus() == 10)
			{
				if (isJobType)
				{
					if (entry.getActivity().getId() == 0)
					{
						throw new Exception();
					}
					if (entry.getJob().getId() == 0)
					{
						throw new Exception();
					}
				}
				if (entry.getCompany().getId() == 0)
				{
					throw new Exception();
				}
				if (entry.getCreditor().getId() == 0)
				{
					throw new Exception();
				}
				if (entry.getCurrency().getId() == 0)
				{
					throw new Exception();
				}
				if (entry.getLocation().getId() == 0)
				{
					throw new Exception();
				}
				if (entry.getDescription() == null || entry.getDescription() == NOTHING)
				{
					throw new Exception();
				}
				if (entry.getFile().getFileName() == null || entry.getFile().getFileName() == NOTHING)
				{
					throw new Exception();
				}
				if (entry.getVoucherDate() == null)
				{
					throw new Exception();
				}
				if (entry.getTotalAmount() == 0)
				{
					throw new Exception();
				}
				if (entry.getType().getId() == 0)
				{
					throw new Exception();
				}
				return true;
			}
			throw new Exception();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	private String convertDate(Date date)
	{
		long millis = date.getTime();
		return "/Date("+millis+"-0000)/";
	}
	
	private class UseReceiptFile extends AsyncTask<ReceiptFile, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(ReceiptFile... params) 
		{
			JSONParser jParser = new JSONParser();
			String fileName;
			
			try
			{
				fileName = jParser.postBitmap(MainActivity.getServer(), expEntFileRequest(), bitmap, resolution);
				System.out.println(fileName);
				
				try
				{
					if (params[0] != null)
					{
						String useReceipt = jParser.putResponse(MainActivity.getServer(), usedRequest(params[0]));
						System.out.println(useReceipt);
					}
					else
					{
						entry.setFile(new File(fileName));
					}
				}
				catch (Exception ee)
				{
					entry.setFile(new File(fileName));
				}
				
				return true;
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
			if (success)
			{
				adapter.notifyDataSetChanged();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
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

		private String usedRequest(ReceiptFile file)
		{
			String request = "/api/personalexpense/ExpenseEntry/recieptfile?Id="+file.getId()+"&UsedDate="+convertDate(new Date());
			try
			{
				try
				{
					request += "&UploadDate="+convertDate(file.getUploadDate());
				}
				catch (Exception e)
				{}
				
				try
				{
					request += "&Description="+URLEncoder.encode(file.getDescription(), "utf-8");
				}
				catch (Exception e)
				{}
				
				try
				{
					request += "&CompanyId="+file.getCompanyId();
				}
				catch (Exception e)
				{}
				
				try
				{
					request += "&ReceiptFileName="+file.getFileName();
				}
				catch (Exception e)
				{}
				
				return request;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		private String expEntFileRequest()
		{
			try
			{
				return GET_ENTRY_PHOTO+entry.getCompany().getId()+"/"+entry.getId();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	public class ManipulateEntry extends AsyncTask<Integer, Void, Integer>
	{
		private String message;
		private boolean approveNotReject;

		@Override
		protected Integer doInBackground(Integer... params) 
		{
			JSONParser jParser = new JSONParser();
			String response = null;
			try
			{
				switch (params[0])
				{
				case EDIT:
					response = jParser.putResponse(MainActivity.getServer(), request(EDIT));
					System.out.println(response);
					return EDIT;
				case ADD:
					JSONObject dbCompany = jParser.getJSONbyGet(MainActivity.getServer(), requestCompany(params[1]));
					System.out.println(dbCompany.toString());
					
					String companyName = dbCompany.getString("CompName");
					
					entry.setCompany(new Company(params[1], companyName));
					companyId = params[1];
					
					response = jParser.getResponse(MainActivity.getServer(), request(ADD));
					System.out.println(response);
					
					entry.setId(Integer.parseInt(response));
					
					ReceiptFile receipt = getIntent().getParcelableExtra(SELECTED_RECEIPT);
					if (receipt != null)
					{
						resolution = 100;
						
						bitmap = BitmapFactory.decodeByteArray(receipt.getBitmapBytes(), 0, receipt.getBitmapBytes().length);
						
						entry.setFile(receipt);
					}
					
					return ADD;
				case DELETE:
					response = jParser.deleteResponse(MainActivity.getServer(), request(DELETE));
					System.out.println(response);
					return DELETE;
				case APPROVE:
					message = NOTHING;
					try
					{
						response = jParser.getResponse(MainActivity.getServer(), request(APPROVE));
						System.out.println(response);
						
						if (Integer.parseInt(response) == 0)
						{
							throw new Exception();
						}
						entry.setApprovalStatus(20);
						
						approveNotReject = true;
						return DEFAULT_CODE;
					}
					catch (Exception e)
					{
						try
						{
							JSONObject jsonResponse = new JSONObject(response);
							JSONObject responseStatus = jsonResponse.getJSONObject("ResponseStatus");
							message = responseStatus.getString("Message");
							int index = message.lastIndexOf("$");
							message = message.substring(index+1);
						}
						catch (Exception ee)
						{
							ee.printStackTrace();
						}
						return APPROVE;
					}
					
				case REJECT:
					message = NOTHING;
					try
					{
						response = jParser.getResponse(MainActivity.getServer(), request(REJECT));
						System.out.println(response);
						
						if (Integer.parseInt(response) == 0)
						{
							throw new Exception();
						}
						
						entry = null;
						approveNotReject = false;
						return DEFAULT_CODE;
					}
					catch (Exception e)
					{
						try
						{
							JSONObject jsonResponse = new JSONObject(response);
							JSONObject responseStatus = jsonResponse.getJSONObject("ResponseStatus");
							message = responseStatus.getString("Message");
							int index = message.lastIndexOf("$");
							message = message.substring(index+1);
						}
						catch (Exception ee)
						{
							ee.printStackTrace();
						}
						return REJECT;
					}
				case DELETE_FILE:
					response = jParser.deleteResponse(MainActivity.getServer(), request(DELETE_FILE));
					System.out.println(response);
					noChangesYet = false;
					return DELETE_FILE;
				case EDIT_FOR_FILE:
					response = jParser.putResponse(MainActivity.getServer(), request(EDIT));
					System.out.println(response);
					return EDIT_FOR_FILE;
				default:
					throw new Exception();
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
			Intent data;
			try
			{
				switch (code)
				{
				case ADD:
					adapter.notifyDataSetChanged();
					if (entry.getFile() != null)
					{
						new UseReceiptFile().execute((ReceiptFile) entry.getFile());
					}
					break;
				case EDIT:
					if (readyForApproval())
					{
						TextView titleView = new TextView(EntryActivity.this);
						titleView.setText(APPROVAL_TITLE);
						titleView.setGravity(Gravity.CENTER_HORIZONTAL);
						titleView.setTextSize(20);
					
						AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
						builder.setCustomTitle(titleView)
						   		.setMessage(READY_FOR_APPROVAL)
						   		.setCancelable(true)
						   		.setNegativeButton(NO_APPROVAL, new DialogInterface.OnClickListener() {
						   		   @Override
						    	   public void onClick(DialogInterface dialog, int which) {
						    		   	editForApproval = true;
						   			   	dialog.dismiss();
						   			   	new ManipulateEntry().execute(EDIT_FOR_FILE);
						    	   }
						   		})
					       		.setPositiveButton(APPROVAL, new DialogInterface.OnClickListener() {
						           @Override
						    	   public void onClick(DialogInterface dialog, int id) {
						        	   dialog.dismiss();
						        	   new ManipulateEntry().execute(APPROVE);
						           }
					       		});
						final AlertDialog alert = builder.create();
						alert.show();
					
						TextView messageView = (TextView)alert.findViewById(android.R.id.message);
						messageView.setGravity(Gravity.CENTER);
						messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);
					}
					else
					{
						data = new Intent();
		    		   	setResult(RESULT_OK,data);
		    		   	finish();
					}
					break;
				case APPROVE:
					printError(NOT_APPROVED_TITLE);
					break;
				case REJECT:
					printError(NOT_REJECTED_TITLE);
					break;
				case DELETE_FILE:
					new ManipulateEntry().execute(EDIT_FOR_FILE);
					break;
				case EDIT_FOR_FILE:
					if (editForApproval)
					{
						Intent dataForApproval = new Intent();
		    		   	setResult(RESULT_OK,dataForApproval);
		    		   	finish();
					}
					adapter.notifyDataSetChanged();
					break;
				case DELETE:
					data = new Intent();
	    		   	setResult(RESULT_OK,data);
	    		   	finish();
	    		   	break;
				case DEFAULT_CODE:
					if (approveNotReject)
					{
						TextView titleView = new TextView(EntryActivity.this);
						titleView.setText(APPROVED_TITLE);
						titleView.setGravity(Gravity.CENTER_HORIZONTAL);
						titleView.setTextSize(20);
						titleView.setTextColor(Color.rgb(75, 220, 98));
					
						AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
						builder.setCustomTitle(titleView)
						   		.setMessage(APPROVED)
						   		.setCancelable(false)
						   		.setNeutralButton(DISMISS, new DialogInterface.OnClickListener() 
						   		{
									@Override
									public void onClick(DialogInterface dialog, int which) 
									{
										dialog.dismiss();
										Intent data = new Intent();
						    		   	setResult(RESULT_OK,data);
						    		   	finish();
									}
								});
						final AlertDialog alert = builder.create();
						alert.show();
					
						TextView messageView = (TextView)alert.findViewById(android.R.id.message);
						messageView.setGravity(Gravity.CENTER);
						messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);
					}
					else
					{
						TextView titleView = new TextView(EntryActivity.this);
						titleView.setText(REJECTED_TITLE);
						titleView.setGravity(Gravity.CENTER_HORIZONTAL);
						titleView.setTextSize(20);
						titleView.setTextColor(Color.rgb(75, 220, 98));
					
						AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
						builder.setCustomTitle(titleView)
						   		.setMessage(REJECTED)
						   		.setCancelable(false)
						   		.setNeutralButton(DISMISS, new DialogInterface.OnClickListener() 
						   		{
									@Override
									public void onClick(DialogInterface dialog, int which) 
									{
										dialog.dismiss();
										Intent data = new Intent();
						    		   	setResult(RESULT_OK,data);
						    		   	finish();
									}
								});
						final AlertDialog alert = builder.create();
						alert.show();
					
						TextView messageView = (TextView)alert.findViewById(android.R.id.message);
						messageView.setGravity(Gravity.CENTER);
						messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trash, 0, 0, 0);
					}
					break;
				default:
					throw new Exception();
				}
				System.out.println("SUCCESS!!");
			}
			catch (Exception e)
			{
				System.out.println("ERROR");
				e.printStackTrace();
			}
		}
		
		private void printError(String title) throws Exception
		{
			if (message != NOTHING && message != null)
			{
				TextView titleView = new TextView(EntryActivity.this);
				titleView.setText(NOT_APPROVED_TITLE);
				titleView.setGravity(Gravity.CENTER_HORIZONTAL);
				titleView.setTextSize(20);
				titleView.setTextColor(Color.RED);
			
				AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
				builder.setCustomTitle(titleView)
				   		.setMessage(message)
				   		.setCancelable(false)
				   		.setNeutralButton(DISMISS, new DialogInterface.OnClickListener() 
				   		{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.dismiss();
							}
						});
				final AlertDialog alert = builder.create();
				alert.show();
			
				TextView messageView = (TextView)alert.findViewById(android.R.id.message);
				messageView.setGravity(Gravity.CENTER);
				messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_down, 0, 0, 0);
			}
			else
			{
				throw new Exception();
			}
		}

		private String request(int method)
		{
			try
			{
				String request;
				switch (method)
				{
				case ADD:
					request = "/api/personalexpense/ExpenseEntry?&EmployeeID="+MainActivity.getId()+"&ApprovalStatus="+entry.getApprovalStatus()+"&CompanyId="+entry.getCompany().getId()+"&ExpenseEntryTypeId="+entry.getType().getId()+"&VoucherDate="+convertDate(entry.getVoucherDate());
					break;
				case EDIT:	
					request = "/api/personalexpense/ExpenseEntry?" +
							"&EmployeeID="+MainActivity.getId()+
							"&Id="+entry.getId()+
							"&ApprovalStatus="+entry.getApprovalStatus();
					try
					{
						if (entry.getCompany().getId() == 0)
						{
							request += "&CompanyId="+MainActivity.getCompanyId();
						}
						else
						{
							request += "&CompanyId="+entry.getCompany().getId();
						}
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getType().getId() == 0)
						{
							throw new Exception();
						}
						request += "&ExpenseEntryTypeId="+entry.getType().getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getType().getId() > 1)
						{
							throw new Exception();
						}
						request += "&JobID="+entry.getJob().getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getType().getId() > 1)
						{
							throw new Exception();
						}
						request += "&ActivityID="+entry.getActivity().getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getVoucherDate().getTime() == 0)
						{
							throw new Exception();
						}
						request += "&VoucherDate="+convertDate(entry.getVoucherDate());
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getLocation().getId() == 0)
						{
							throw new Exception();
						}
						request += "&LocationId="+entry.getLocation().getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getCurrency().getId() == 0)
						{
							throw new Exception();
						}
						request += "&CurrencyID="+entry.getCurrency().getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getTotalAmount() == 0)
						{
							throw new Exception();
						}
						request += "&CurrencyAmount="+entry.getTotalAmount();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getDescription() == null || entry.getDescription() == NOTHING)
						{
							throw new Exception();
						}
						request += "&Description="+URLEncoder.encode(entry.getDescription(), "utf-8");
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getCreditor().getId() == 0)
						{
							throw new Exception();
						}
						request += "&CreditorID="+entry.getCreditor().getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (((ReceiptFile) entry.getFile()).getId() == 0)
						{
							throw new Exception();
						}
						request += "&ReciptFileId="+((ReceiptFile) entry.getFile()).getId();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getFile().getFileName() == NOTHING || entry.getFile().getFileName() == null)
						{
							throw new Exception();
						}
						request += "&ReceiptFile="+entry.getFile().getFileName();
					}
					catch (Exception e)
					{}
					try
					{
						if (entry.getImportDtlId() == 0)
						{
							throw new Exception();
						}
						request += "&ExpenseEntryImportDtlId="+entry.getImportDtlId();
					}
					catch (Exception e)
					{}	
					
					break;
				case DELETE:
					request = "/api/personalexpense/expenseentry/"+entry.getId()+"/"+MainActivity.getId();
					break;
				case APPROVE:
					request = "/api/personalexpense/Approve/ExpenseEntry?Id="+entry.getId()+"&ResourceId="+MainActivity.getId();
					break;
				case REJECT:
					request = "/api/personalexpense/Reject/ExpenseEntry?Id="+entry.getId()+"&ResourceId="+MainActivity.getId()+"&RejectComment="+URLEncoder.encode(rejectComment, "utf-8");
					break;
				case DELETE_FILE:
					request = "/api/files/ExpenseEntry/ExpenseEntryFile?Id="+entry.getId()+"&CompanyId="+entry.getCompany().getId();
					break;
				default:
					request = null;
				}	
				System.out.println(request);
				return request;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}								
		}
		private String requestCompany(int companyId)
		{
			return "/api/core/company/"+companyId;
		}
	}

}
