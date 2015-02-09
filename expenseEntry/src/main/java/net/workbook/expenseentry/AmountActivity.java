package net.workbook.expenseentry;

import java.text.DecimalFormat;

import net.workbook.expenseentry.interfaces.Finals;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AmountActivity extends Activity implements OnClickListener,Finals
{
	private EditText amountField;
	private Button done;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_amount);
		
		amountField = (EditText) findViewById(R.id.amountField);
		double previousAmount = getIntent().getDoubleExtra(CHANGED_AMOUNT, 0);
		if (previousAmount != 0)
		{
			DecimalFormat twoZeroes = new DecimalFormat("0.00");
			amountField.setText(twoZeroes.format(previousAmount));
		}
		else
		{
			amountField.setText("0.00");
		}
		
		done = (Button) findViewById(R.id.doneAmount);
		done.setOnClickListener(this);
	}



	@Override
	public void onClick(View v) 
	{
		try
		{
			double amount = Double.parseDouble(amountField.getText().toString());
			
			Intent data = new Intent();
			data.putExtra(SELECTED_AMOUNT, amount);
			setResult(RESULT_OK,data);
			finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
}
