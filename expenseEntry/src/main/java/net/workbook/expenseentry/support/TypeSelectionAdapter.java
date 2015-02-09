package net.workbook.expenseentry.support;

import java.util.ArrayList;

import net.workbook.expenseentry.R;
import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.ExpenseEntryType;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TypeSelectionAdapter extends BaseAdapter implements Finals
{
	private ArrayList<ExpenseEntryType> types;
	private LayoutInflater inflater;
	
	public TypeSelectionAdapter(Context context, ArrayList<Object> types)
	{
		inflater = LayoutInflater.from(context);
		this.types = new ArrayList<ExpenseEntryType>();
		for (int i=0; i<types.size(); i++)
		{
			this.types.add((ExpenseEntryType) types.get(i));
		}
	}

	@Override
	public int getCount() 
	{
		return types.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return types.get(position);
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
            convertView = inflater.inflate(R.layout.type_selection_row, parent, false);
            holder.color = convertView.findViewById(R.id.typeColor);
            holder.value = (TextView) convertView.findViewById(R.id.typeValue);
           
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }
        
        ExpenseEntryType type = types.get(position);
        
        if (type.getPostType() == 1)
        {
        	holder.color.setBackgroundColor(Color.rgb(75, 220, 98));
        }
        else if (type.getPostType() == 2)
        {
        	holder.color.setBackgroundColor(Color.rgb(255, 179, 41));
        }
        else if (type.getPostType() == 3)
        {
        	holder.color.setBackgroundColor(Color.rgb(59, 175, 255));
        }
        
        holder.value.setText(type.getTitle());
        
        return convertView;
	}
	
	class ViewHolder
	{
		View color;
		TextView value;
	}
}
