package net.workbook.expenseentry;

import java.util.Calendar;
import java.util.Date;

import net.workbook.expenseentry.interfaces.Finals;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

public class DateActivity extends Activity implements OnClickListener,Finals
{
	private Button done;
	private DatePicker datePicker;
	private Date voucherDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_date);
		
		done = (Button) findViewById(R.id.doneDate);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		
		if (getIntent().getLongExtra(SELECTED_DATE, 0) != 0)
		{
			voucherDate = new Date(getIntent().getLongExtra(SELECTED_DATE, 0));
			setDatePicker(voucherDate);
		}
		
		done.setOnClickListener(this);
	}
	
	private void setDatePicker(Date date)
	{
		Calendar calendar = convertDate(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		datePicker.init(year, month, day, null);
	}

	@Override
	public void onClick(View v) 
	{
		try
		{
			int day = datePicker.getDayOfMonth();
			int month = datePicker.getMonth();
			int year = datePicker.getYear();
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day);
			long time = calendar.getTimeInMillis();
	    	
	    	Intent data = new Intent();
	    	data.putExtra(SELECTED_DATE, time);
	    	setResult(RESULT_OK,data);
	    	finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Calendar convertDate(Date date) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

}
