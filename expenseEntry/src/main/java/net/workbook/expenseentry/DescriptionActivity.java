package net.workbook.expenseentry;

import net.workbook.expenseentry.interfaces.Finals;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DescriptionActivity extends Activity implements OnClickListener,Finals
{
	private EditText descriptionField;
	private Button done;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_description);
		
		descriptionField = (EditText) findViewById(R.id.descriptionField);
		done = (Button) findViewById(R.id.doneDescription);
		
		done.setOnClickListener(this);
		
		if (getIntent().getStringExtra(ADDED_DESCRIPTION) != null)
		{
			String description = getIntent().getStringExtra(ADDED_DESCRIPTION);
			descriptionField.setText(description);
			descriptionField.setSelection(description.length());
		}
	}

	@Override
	public void onClick(View v) 
	{
		try
		{
			String description = descriptionField.getText().toString();
			
			Intent data = new Intent();
			data.putExtra(ADDED_DESCRIPTION, description);
			setResult(Activity.RESULT_OK, data);
			finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
