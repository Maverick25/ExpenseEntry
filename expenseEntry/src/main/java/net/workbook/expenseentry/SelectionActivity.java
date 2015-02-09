package net.workbook.expenseentry;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.interfaces.Sectionizer;
import net.workbook.expenseentry.model.Company;
import net.workbook.expenseentry.model.Creditor;
import net.workbook.expenseentry.model.Currency;
import net.workbook.expenseentry.model.ExpenseEntryType;
import net.workbook.expenseentry.model.Job;
import net.workbook.expenseentry.model.Location;
import net.workbook.expenseentry.support.JSONParser;
import net.workbook.expenseentry.support.SectionJobAdapter;
import net.workbook.expenseentry.support.TypeSelectionAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectionActivity extends Activity 
implements OnItemClickListener,TextWatcher,Finals
{	
	private ArrayAdapter<Object> adapter;
	private TypeSelectionAdapter typesAdapter;
	private ArrayList<Object> finalObjects;
	private SectionJobAdapter<Job> jobsAdapter;
	private AdapterView<?> list;
	private Intent currentData;
	
	private ListView selectionList;
	private EditText inputSearch;
	private View selectionView;
	private View loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_selection);
		
		selectionList = (ListView) findViewById(R.id.selectionList);
	    inputSearch = (EditText) findViewById(R.id.inputSearch);
	        
	    selectionView = findViewById(R.id.selectionView);
	    loadingView = findViewById(R.id.loadingView);
	     
	    try
	    {
	    	switch (getIntent().getIntExtra(CATEGORY, 404))
	    	{
	    	case COMPANY:
	    		new GetObjects().execute(COMPANY);
	    		break;
	    	case EXPENSE_TYPE:
	    		new GetObjects().execute(EXPENSE_TYPE);
	    		break;
	    	case JOB:
	    		new GetObjects().execute(JOB);
	    		break;
	    	case ACTIVITY:
	    		new GetObjects().execute(ACTIVITY);
	    		break;
	    	case LOCATION:
	    		new GetObjects().execute(LOCATION);
	    		break;
	    	case CURRENCY:
	    		new GetObjects().execute(CURRENCY);
	    		break;
	    	case CREDITOR:
	    		new GetObjects().execute(CREDITOR);
	    		break;
	    	} 	
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    inputSearch.addTextChangedListener(this);
    	selectionList.setOnItemClickListener(this);
	}
	
	private void showProgress(final boolean show) 
	{
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

			selectionView.setVisibility(View.VISIBLE);
			selectionView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() 
			{
						@Override
						public void onAnimationEnd(Animator animation) 
						{
							selectionView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
			});
		} 
		else 
		{
			loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
			selectionView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	@Override
	public void afterTextChanged(Editable search) {
		
			if (jobsAdapter == null)
			{
				if (search.toString().equals(NOTHING))
				{
					adapter = new ArrayAdapter<Object>(SelectionActivity.this, R.layout.simple_selection_row,finalObjects);
					selectionList.setAdapter(adapter);
				}
				else
				{
					ArrayList<Object> objects = new ArrayList<Object>();
					String toLower = search.toString().toLowerCase();
					for (Object object : finalObjects)
					{
						String lowerObject = object.toString().toLowerCase();
						if (lowerObject.contains(toLower))
						{
							objects.add(object);
						}
					}
					adapter = new ArrayAdapter<Object>(SelectionActivity.this,R.layout.simple_selection_row,objects);
					selectionList.setAdapter(adapter);
				}
			}
			else
			{
				ArrayList<Job> finalJobs = new ArrayList<Job>();
				for (int i=0; i<finalObjects.size(); i++)
				{
					finalJobs.add((Job)finalObjects.get(i));
				}
				selectionList.setAdapter(jobsAdapter.filterSections(finalJobs,search.toString()));
			}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> list, View line, int position, long id) 
	{
		try
		{
			SelectionActivity.this.list = list;
			currentData = new Intent();
			switch(getIntent().getIntExtra(CATEGORY, 404))
			{
			case COMPANY:
				new SetListener().execute(COMPANY,position);
				break;
			case EXPENSE_TYPE:
				new SetListener().execute(EXPENSE_TYPE,position);
				break;
			case JOB:
				new SetListener().execute(JOB,position);
				break;
			case ACTIVITY:
				new SetListener().execute(ACTIVITY,position);
				break;
			case LOCATION:
				new SetListener().execute(LOCATION,position);
				break;
			case CURRENCY:
				new SetListener().execute(CURRENCY,position);
				break;
			case CREDITOR:
				new SetListener().execute(CREDITOR,position);
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class GetObjects extends AsyncTask<Integer, String, ArrayList<Object>>
	{

		@Override
		protected void onPreExecute() 
		{
			adapter = null;
			jobsAdapter = null;
			finalObjects = null;
			showProgress(true);
		}

		@Override
		protected ArrayList<Object> doInBackground(Integer... categories) 
		{
			ArrayList<Object> objects = new ArrayList<Object>();
			JSONParser jParser = new JSONParser();
			try
			{
				JSONArray dbObjects = jParser.getJSONArrayFromUrl(MainActivity.getServer(),request(categories[0]));
				JSONObject dbObject;
				switch(categories[0])
				{
				case COMPANY:
					publishProgress(COMPANY_LABEL);
					
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int id = dbObject.getInt("CompID");
						String name = dbObject.getString("CompName");
						
						objects.add(new Company(id, name));
					}
					break;
				case EXPENSE_TYPE:
					publishProgress(EXPENSE_TYPE_LABEL);
					
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int id = dbObject.getInt("ExpenseEntryTypeId");
						String title = dbObject.getString("Title");
						int postType = dbObject.getInt("ExpensePostType");
						boolean isActive = dbObject.getBoolean("IsActive");
						
						objects.add(new ExpenseEntryType(id, title, postType, isActive));
					}
					break;
				case JOB:
					publishProgress(JOB_LABEL);
					int companyId = getIntent().getIntExtra(COMPANY_ID, 0);
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int jobCompanyId = dbObject.getInt("CompanyId");
						if (jobCompanyId == companyId)
						{
							int id = dbObject.getInt("JobId");
							String name = dbObject.getString("JobName");
							String customerName = dbObject.getString("CustName");
						
							objects.add(new Job(id, name, customerName, jobCompanyId));
						}
					}
					break;
				case ACTIVITY:
					publishProgress(ACTIVITY_LABEL);
					
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int id = dbObject.getInt("ID");
						String text = dbObject.getString("ActTxt");
						
						objects.add(new net.workbook.expenseentry.model.Activity(id, text));
					}
					break;
				case LOCATION:
					publishProgress(LOCATION_LABEL);
					
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int id = dbObject.getInt("Id");
						String description = dbObject.getString("Description");
						
						objects.add(new Location(id, description));
					}
					break;
				case CURRENCY:
					publishProgress(CURRENCY_LABEL);
					System.out.println(dbObjects.length());
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int id = dbObject.getInt("Id");
						String name = dbObject.getString("Name");
						String isoCode = dbObject.getString("IsoCode");
						String unit;
						try
						{
							unit = dbObject.getString("CurrencyUnit");
						}
						catch (Exception e)
						{
							unit = NOTHING;
						}
						
						String fraction;
						try
						{
							fraction = dbObject.getString("CurrencyFraction");
						}
						catch (Exception e)
						{
							fraction = NOTHING;
						}
						
						String unitPlural;
						try
						{
							unitPlural = dbObject.getString("CurrencyUnitPlural");
						}
						catch (Exception e)
						{
							unitPlural = NOTHING;
						}
						
						String fractionPlural;
						try
						{
							fractionPlural = dbObject.getString("CurrencyFractionPlural");
						}
						catch (Exception e)
						{
							fractionPlural = NOTHING;
						}
						
						objects.add(new Currency(id, name, isoCode, unit, fraction, unitPlural, fractionPlural));
					}
					break;
				case CREDITOR:
					publishProgress(CREDITOR_LABEL);
					for (int i=0; i<dbObjects.length(); i++)
					{
						dbObject = dbObjects.getJSONObject(i);
						
						int id = dbObject.getInt("CreditorId");
						String no = dbObject.getString("CreditorNo");
						String name = dbObject.getString("CreditorName");
						int companyIdforCreditor = dbObject.getInt("CompanyID");
						int isBlocked = dbObject.getInt("IsBlocked");
						
						objects.add(new Creditor(id, name, no, companyIdforCreditor, isBlocked));
					}
					break;
				}
				
				return objects;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onProgressUpdate(String... values) 
		{
			setTitle("Select "+values[0]);
			inputSearch.setHint("Search "+values[0]+"..");
		}
		
		@Override
		protected void onPostExecute(ArrayList<Object> result) 
		{
			try
			{
				finalObjects = result;
				adapter = new ArrayAdapter<Object>(SelectionActivity.this,R.layout.simple_selection_row,result);
				
				if (result.get(0) instanceof Job)
				{
					Sectionizer<Job> customerSectionizer = new Sectionizer<Job>() 
					{		
						@Override
						public String getSectionTitleForItem(Job instance) 
						{
							return instance.getCustomerName();
						}
					};
				
					jobsAdapter = new SectionJobAdapter<Job>(SelectionActivity.this, adapter, R.layout.jobs_section, R.id.list_item_section_text, customerSectionizer);
					selectionList.setAdapter(jobsAdapter);
				}
				else if (result.get(0) instanceof ExpenseEntryType)
				{
					typesAdapter = new TypeSelectionAdapter(SelectionActivity.this, result);
					selectionList.setAdapter(typesAdapter);
				}
				else
				{
					selectionList.setAdapter(adapter);
				}
				showProgress(false);
			}
			catch(Exception e)
			{
				showProgress(false);
				e.printStackTrace();
			}
		}
		
		private String request(int category)
		{
	    	switch(category)
	    	{
	    	case COMPANY:
	    		return "/api/core/companies";
	    	case EXPENSE_TYPE:
	    		return "/api/personalexpense/ExpenseEntry/types/"+MainActivity.getId();
	    	case JOB:
	    		return "/api/personalexpense/expenseentry/jobs/"+MainActivity.getId();
	    	case ACTIVITY:
	    		return "/api/personalexpense/expenseentry/Activities";
	    	case LOCATION:
	    		return "/api/personalexpense/ExpenseEntry/Locations";
	    	case CURRENCY:
	    		return "/api/core/currencies";
	    	case CREDITOR:
	    		return "/api/personalexpense/ExpenseEntry/EmployeeCreditors/"+MainActivity.getId()+"/"+EntryActivity.getCompanyId();
	    	default:
	    		return null;
	    	}
		}
		
	}

	private class SetListener extends AsyncTask<Integer, Integer, Void>
	{

		@Override
		protected Void doInBackground(Integer... params) 
		{
			try
			{
				switch(params[0])
				{
				case COMPANY:
					Company company = (Company) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, company);
					break;
				case EXPENSE_TYPE:
					ExpenseEntryType type = (ExpenseEntryType) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, type);
					break;
				case JOB:
					Job job = (Job) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, job);
					break;
				case ACTIVITY:
					net.workbook.expenseentry.model.Activity activity = (net.workbook.expenseentry.model.Activity) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, activity);
					break;
				case LOCATION:
					Location location = (Location) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, location);
					break;
				case CURRENCY:
					Currency currency = (Currency) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, currency);
					break;
				case CREDITOR:
					Creditor creditor = (Creditor) list.getItemAtPosition(params[1]);
					
					currentData.putExtra(SELECTED_ITEM, creditor);
					break;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			try
			{
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
				setResult(Activity.RESULT_OK,currentData);
				finish();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
	}

	
}
