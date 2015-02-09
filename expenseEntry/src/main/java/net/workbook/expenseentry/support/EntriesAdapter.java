package net.workbook.expenseentry.support;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import net.workbook.expenseentry.R;
import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.ExpenseEntry;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EntriesAdapter extends BaseAdapter 
implements StickyListHeadersAdapter,Finals 
{
    private ArrayList<ExpenseEntry> entries;
    private LayoutInflater inflater;

    public EntriesAdapter(Context context,ArrayList<ExpenseEntry> entries) 
    {
        inflater = LayoutInflater.from(context);
        this.entries = entries;
    }

    @Override
    public int getCount() 
    {
        return entries.size();
    }

    @Override
    public Object getItem(int position) 
    {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) 
    {
        return position;
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        ViewHolder holder;

        if (convertView == null) 
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.entries_row, parent, false);
            holder.row = convertView.findViewById(R.id.row);
            holder.pin = (ImageView) convertView.findViewById(R.id.receiptFile);
            holder.job = (TextView) convertView.findViewById(R.id.job);
            holder.customer = (TextView) convertView.findViewById(R.id.customer);
            holder.currency = (TextView) convertView.findViewById(R.id.currency);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.description = (TextView) convertView.findViewById(R.id.description);
           
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }
        
        ExpenseEntry entry = entries.get(position);
        
       
        try
        {
        	if (entry.getFile().getFileName() == null || entry.getFile().getFileName() == NOTHING || !entry.getFile().getFileName().contains(".jpg"))
        	{
        		try
	        	{
	        		if (entry.getImportDtlId()==0)
	        		{
	        			throw new NullPointerException();
	        		}
	        		holder.pin.setImageResource(R.drawable.creditcard_b);
	        	}
	        	catch (NullPointerException e)
	        	{
	        		holder.pin.setImageResource(R.drawable.moneystack_b);
	        	}
	        	catch (Exception e)
	        	{
	        		holder.pin.setImageResource(R.drawable.moneystack_b);
	        	}
	        }
	        else
	        {
	        	try
	        	{
	        		if (entry.getImportDtlId()==0)
	        		{
	        			throw new NullPointerException();
	        		}
	        		holder.pin.setImageResource(R.drawable.attachment_with_creditcard_b);
	        	}
	        	catch (NullPointerException e)
	        	{
	        		holder.pin.setImageResource(R.drawable.attachment_with_money_b);
	        	}
	        	catch (Exception e)
	        	{
	        		holder.pin.setImageResource(R.drawable.attachment_with_money_b);
	        	}
	        }
        }
        catch (Exception e)
        {
        	try
        	{
        		if (entry.getImportDtlId()==0)
        		{
        			throw new NullPointerException();
        		}
        		holder.pin.setImageResource(R.drawable.creditcard_b);
        	}
        	catch (NullPointerException ee)
        	{
        		holder.pin.setImageResource(R.drawable.moneystack_b);
        	}
        	catch (Exception ee)
        	{
        		holder.pin.setImageResource(R.drawable.moneystack_b);
        	}
        }
        
        if (entry.getJob().getId() == 0)
        {
        	holder.job.setVisibility(View.GONE);
        	holder.customer.setVisibility(View.GONE);
        }
        else
        {
        	holder.job.setVisibility(View.VISIBLE);
        	holder.job.setText(entry.getJob().toString());
        	
        	holder.customer.setVisibility(View.VISIBLE);
        	holder.customer.setText(entry.getJob().getCustomerName());
        }
        
        Double totalAmount = Double.valueOf(entry.getTotalAmount());
        DecimalFormat twoZeroes = new DecimalFormat("0.00");
        holder.currency.setText(entry.getCurrency().getIsoCode()+" - "+twoZeroes.format(totalAmount));
        
        String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.getDefault()).format(entry.getVoucherDate());
        holder.date.setText("Date: "+dateFormat);
        
        if (entry.getDescription().length()>42)
        {
        	holder.description.setText(entry.getDescription().substring(0, 41)+"...");
        }
        else
        {
        	holder.description.setText(entry.getDescription());
        }
        
        LayerDrawable layerList = (LayerDrawable) holder.row.getBackground();
    	GradientDrawable shapeList = (GradientDrawable) layerList.findDrawableByLayerId(R.id.leftStripe);
        if (entry.getType().getId() == 1)
        {
        	//green
        	shapeList.setColor(Color.rgb(75, 220, 98));
        }
        else if (entry.getType().getId() == 2)
        {
        	//orange
        	shapeList.setColor(Color.rgb(255, 179, 41));
        }
        else if (entry.getType().getId() > 2)
        {
        	//blue
        	shapeList.setColor(Color.rgb(59, 175, 255));
        	holder.customer.setVisibility(View.VISIBLE);
        	holder.customer.setText(entry.getType().getTitle());
        }
        else if (entry.getType().getId() == 0)
        {
        	shapeList.setColor(Color.BLACK);
        }
        
        return convertView;
    }

    @Override 
    public View getHeaderView(int position, View convertView, ViewGroup parent) 
    {
        HeaderViewHolder holder;
        if (convertView == null) 
        {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.entries_section, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.entriesSection);
            holder.lock = (ImageView) convertView.findViewById(R.id.lockEntry);
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        
        String sectionText;
        if (entries.get(position).getApprovalStatus()==10)
        {
        	sectionText = IN_PREPARATION; 
        	holder.text.setText(sectionText);
        	holder.lock.setBackgroundResource(R.drawable.ic_unlock);
        }
        else if (entries.get(position).getApprovalStatus()==20)
        {
        	sectionText = FOR_APPROVAL;
        	holder.text.setText(sectionText);
        	holder.lock.setBackgroundResource(R.drawable.ic_lock);
        }
        
        return convertView;
    }

    @Override
    public long getHeaderId(int position) 
    {
        return entries.get(position).getApprovalStatus();
    }

    class HeaderViewHolder 
    {
        TextView text;
        ImageView lock;
    }

    class ViewHolder 
    {
    	View row;
    	ImageView pin;
        TextView job;
        TextView customer;
        TextView currency;
        TextView date;
        TextView description;
    }
    
  
}