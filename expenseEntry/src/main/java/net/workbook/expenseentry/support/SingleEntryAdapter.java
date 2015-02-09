package net.workbook.expenseentry.support;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import net.workbook.expenseentry.CameraActivity;
import net.workbook.expenseentry.EntryActivity;
import net.workbook.expenseentry.MainActivity;
import net.workbook.expenseentry.R;
import net.workbook.expenseentry.ReceiptFilesActivity;
import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.ExpenseEntry;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SingleEntryAdapter extends BaseAdapter implements OnClickListener,Finals
{
	private LayoutInflater inflater;
	private ExpenseEntry entry;
	private ReceiptViewHolder receiptHolder;
	private ViewHolder holder;
	private Context context;
	
	public SingleEntryAdapter(Context context,ExpenseEntry entry)
	{
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.entry = entry;
	}

	@Override
	public int getCount() 
	{
		try
		{
			if (entry.getType().getId() == 1)
			{
				return 11;
			}
			return 9;
		}
		catch (NullPointerException e)
		{
			return 11;
		}
	}

	@Override
	public Object getItem(int position) 
	{
		if (getCount() == 9)
		{
			switch (position)
			{
			case COMPANY:
				return entry.getCompany();
			case EXPENSE_TYPE:
				return entry.getType();
			case DATE-2:
				return entry.getVoucherDate();
			case LOCATION-2:
				return entry.getLocation();
			case CURRENCY-2:
				return entry.getCurrency();
			case TOTAL_AMOUNT-2:
				return entry.getTotalAmount();
			case DESCRIPTION-2:
				return entry.getDescription();
			case CREDITOR-2:
				return entry.getCreditor();
			case RECEIPT-2:
				return entry.getFile();
			default:
				return null;
			}
		}
		else
		{
			switch (position)
			{
			case COMPANY:
				return entry.getCompany();
			case EXPENSE_TYPE:
				return entry.getType();
			case JOB:
				return entry.getJob();
			case ACTIVITY:
				return entry.getActivity();
			case DATE:
				return entry.getVoucherDate();
			case LOCATION:
				return entry.getLocation();
			case CURRENCY:
				return entry.getCurrency();
			case TOTAL_AMOUNT:
				return entry.getTotalAmount();
			case DESCRIPTION:
				return entry.getDescription();
			case CREDITOR:
				return entry.getCreditor();
			case RECEIPT:
				return entry.getFile();
			default:
				return null;
			}
		}
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (position < getCount()-1)
		{	
			if (convertView == null || convertView.getTag() instanceof ReceiptViewHolder)
			{
				holder = new ViewHolder();
			
				convertView = inflater.inflate(R.layout.single_entry_row, null);
				
				holder.label = (TextView) convertView.findViewById(R.id.entryLabel);
				holder.type = convertView.findViewById(R.id.typeColor);
				holder.value = (TextView) convertView.findViewById(R.id.entryValue);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (getCount() == 9)
			{
				switch (position)
				{
				case COMPANY:
					holder.label.setText(COMPANY_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getCompany().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case EXPENSE_TYPE:
					holder.label.setText(EXPENSE_TYPE_LABEL);
					holder.type.setVisibility(View.VISIBLE);
					try
					{
						holder.value.setText(entry.getType().getTitle());
						if (entry.getType().getId() == 1)
				        {
				        	holder.type.setBackgroundColor(Color.rgb(75, 220, 98));
				        }
				        else if (entry.getType().getId() == 2)
				        {
				        	holder.type.setBackgroundColor(Color.rgb(255, 179, 41));
				        }
				        else if (entry.getType().getId() >= 3)
				        {
				        	holder.type.setBackgroundColor(Color.rgb(59, 175, 255));
				        }
					}
					catch (NullPointerException e)
					{
						holder.value.setText(MainActivity.getTypes().get(0).getTitle());
						holder.type.setBackgroundColor(Color.rgb(75, 220, 98));
					}
					break;
				case DATE-2:
					holder.label.setText(DATE_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.getDefault()).format(entry.getVoucherDate());
					    holder.value.setText(dateFormat);
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case LOCATION-2:
					holder.label.setText(LOCATION_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getLocation().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case CURRENCY-2:
					holder.label.setText(CURRENCY_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getCurrency().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case TOTAL_AMOUNT-2:
					holder.label.setText(TOTAL_AMOUNT_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						 Double totalAmount = Double.valueOf(entry.getTotalAmount());
					     DecimalFormat twoZeroes = new DecimalFormat("0.00");
					     holder.value.setText(twoZeroes.format(totalAmount));
					}
					catch (NullPointerException e)
					{
						holder.value.setText("0.00");
					}
					break;
				case DESCRIPTION-2:
					holder.label.setText(DESCRIPTION_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getDescription());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case CREDITOR-2:
					holder.label.setText(CREDITOR_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getCreditor().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				}
			}
			else
			{
				switch (position)
				{
				case COMPANY:
					holder.label.setText(COMPANY_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getCompany().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case EXPENSE_TYPE:
					holder.label.setText(EXPENSE_TYPE_LABEL);
					holder.type.setVisibility(View.VISIBLE);
					try
					{	
						holder.value.setText(entry.getType().getTitle());
						if (entry.getType().getId() == 1)
				        {
				        	holder.type.setBackgroundColor(Color.rgb(75, 220, 98));
				        }
				        else if (entry.getType().getId() == 2)
				        {
				        	holder.type.setBackgroundResource(Color.rgb(255, 179, 41));
				        }
				        else if (entry.getType().getId() >= 3)
				        {
				        	holder.type.setBackgroundResource(Color.rgb(59, 175, 255));
				        }
					}
					catch (NullPointerException e)
					{
						holder.value.setText(MainActivity.getTypes().get(0).getTitle());
						holder.type.setBackgroundColor(Color.rgb(75, 220, 98));
					}
					break;
				case JOB:
					holder.label.setText(JOB_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getJob().toString()+"\n"+entry.getJob().getCustomerName());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case ACTIVITY:
					holder.label.setText(ACTIVITY_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getActivity().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case DATE:
					holder.label.setText(DATE_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.getDefault()).format(entry.getVoucherDate());
					    holder.value.setText(dateFormat);
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case LOCATION:
					holder.label.setText(LOCATION_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getLocation().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case CURRENCY:
					holder.label.setText(CURRENCY_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getCurrency().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case TOTAL_AMOUNT:
					holder.label.setText(TOTAL_AMOUNT_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						Double totalAmount = Double.valueOf(entry.getTotalAmount());
				        DecimalFormat twoZeroes = new DecimalFormat("0.00");
				        holder.value.setText(twoZeroes.format(totalAmount));
					}
					catch (NullPointerException e)
					{
						holder.value.setText("0.00");
					}
					break;
				case DESCRIPTION:
					holder.label.setText(DESCRIPTION_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getDescription());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				case CREDITOR:
					holder.label.setText(CREDITOR_LABEL);
					holder.type.setVisibility(View.GONE);
					try
					{
						holder.value.setText(entry.getCreditor().toString());
					}
					catch (NullPointerException e)
					{
						holder.value.setText(NOTHING);
					}
					break;
				}
			}
		}
		else
		{
			if (convertView == null || convertView.getTag() instanceof ViewHolder)
			{
				receiptHolder = new ReceiptViewHolder();
				
				convertView = inflater.inflate(R.layout.receipt_entry_row, null);
				
				receiptHolder.receiptLabel = (TextView) convertView.findViewById(R.id.receiptLabel);
				receiptHolder.takePhoto = (Button) convertView.findViewById(R.id.takePhoto);
				receiptHolder.getFromList = (Button) convertView.findViewById(R.id.getFromList);
				receiptHolder.getFromCameraRoll = (Button) convertView.findViewById(R.id.getFromCameraRoll);
				receiptHolder.removeFile = (Button) convertView.findViewById(R.id.removeFile);
				receiptHolder.loadingThumb = (ProgressBar) convertView.findViewById(R.id.loadingThumb);
				receiptHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumb);
				
				convertView.setTag(receiptHolder);
			}
			else
			{
				receiptHolder = (ReceiptViewHolder) convertView.getTag();
			}	
			
			if (entry.getApprovalStatus()!=10 && EntryActivity.isLocked)
			{
				blockListeners();
			}
			else
			{
				receiptHolder.takePhoto.setOnClickListener(this);
				receiptHolder.getFromCameraRoll.setOnClickListener(this);
				receiptHolder.getFromList.setOnClickListener(this);
				receiptHolder.removeFile.setOnClickListener(this);
			}
			
			try
			{
				if (entry.getFile().getFileName() != null && entry.getFile().getFileName() != NOTHING)
				{
					if (entry.getFile().getBitmapBytes() == null)
					{
						try
				        {
							receiptHolder.thumbnail.setVisibility(View.GONE);
							receiptHolder.loadingThumb.setVisibility(View.VISIBLE);
				        	receiptHolder.thumbnail.setAnimation(null);
				            
				            UrlImageViewHelper.setUrlDrawable(receiptHolder.thumbnail, requestThumb(entry.getId(), entry.getCompany().getId()), null, 0, new UrlImageViewCallback() {
				                @Override
				                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
				                        try
				                        {
					                		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					                        scale.setDuration(300);
					                        scale.setInterpolator(new OvershootInterpolator());
					                        imageView.startAnimation(scale);

				                        	ByteArrayOutputStream stream = new ByteArrayOutputStream();
				                        	loadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				                        	byte[] byteArray = stream.toByteArray();
				                        	entry.getFile().setBitmapBytes(byteArray);
				                        }
				                        catch (Exception e)
				                        {
				                        	imageView.setImageResource(R.drawable.nothumb);
				                        }
				                        receiptHolder.loadingThumb.setVisibility(View.GONE);
				                        receiptHolder.thumbnail.setVisibility(View.VISIBLE);
				                    }
				            });
				        }
				        catch (Exception e)
				        {
				        	e.printStackTrace();
				        	receiptHolder.thumbnail.setImageResource(R.drawable.nothumb);
				        	receiptHolder.loadingThumb.setVisibility(View.GONE);
				        	receiptHolder.thumbnail.setVisibility(View.VISIBLE);
				        }
					}
					else
					{
						byte[] byteArray = entry.getFile().getBitmapBytes();
						Bitmap thumb = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
						receiptHolder.thumbnail.setImageBitmap(thumb);
						receiptHolder.loadingThumb.setVisibility(View.GONE);
						receiptHolder.thumbnail.setVisibility(View.VISIBLE);
					}
				}
				else
				{
					receiptHolder.thumbnail.setImageResource(R.drawable.nothumb);
					receiptHolder.loadingThumb.setVisibility(View.GONE);
					receiptHolder.thumbnail.setVisibility(View.VISIBLE);
				}
			}
			catch (Exception e)
			{
				receiptHolder.thumbnail.setImageResource(R.drawable.nothumb);
				receiptHolder.loadingThumb.setVisibility(View.GONE);
				receiptHolder.thumbnail.setVisibility(View.VISIBLE);
			}
		}
			
		return convertView;
	}
	
	class ViewHolder
	{
		TextView label;
		View type;
		TextView value;
	}
	
	class ReceiptViewHolder
	{
		TextView receiptLabel;
		Button takePhoto;
		Button getFromList;
		Button getFromCameraRoll;
		Button removeFile;
		ProgressBar loadingThumb;
		ImageView thumbnail;
	}
	
	private String requestThumb(int id,int companyId)
	{
		return "http://"+MainActivity.getServer()+GET_ENTRY_THUMB+companyId+"/"+id;
	}

	@Override
	public void onClick(View v) 
	{
		try
		{
			try
			{
				if (EntryActivity.file.getFileName() == null || EntryActivity.file.getFileName() == NOTHING)
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				EntryActivity.file = entry.getFile();
			}
			
			switch (v.getId())
			{
			case R.id.takePhoto:
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                ((Activity) context).startActivityForResult(cameraIntent, TAKE_PIC_FOR_ENTRY); 
				break;
			case R.id.getFromList:
				Intent goGetFromList = new Intent(context,ReceiptFilesActivity.class);
				goGetFromList.putExtra(CODE, LOOK_AT_PIC_FOR_ENTRY);
				((Activity) context).startActivityForResult(goGetFromList, LOOK_AT_PIC_FOR_ENTRY);
				break;
			case R.id.getFromCameraRoll:
				Intent goGetFromCameraRoll = new Intent(context,CameraActivity.class);
				goGetFromCameraRoll.putExtra(CODE, GET_PIC_FOR_ENTRY);
				((Activity) context).startActivityForResult(goGetFromCameraRoll, GET_PIC_FOR_ENTRY);
				break;
			case R.id.removeFile:
				if (entry.getFile() != null)
				{
					entry.getFile().setBitmapBytes(null);
					entry.setFile(null);
					((EntryActivity) context).new ManipulateEntry().execute(DELETE_FILE);
					notifyDataSetChanged();
				}
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void blockListeners()
	{
		receiptHolder.takePhoto.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				TextView titleView = new TextView(context);
				titleView.setText(LOCKED_ENTRY_TITLE);
				titleView.setGravity(Gravity.CENTER_HORIZONTAL);
				titleView.setTextSize(20);
			
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
		receiptHolder.getFromCameraRoll.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				TextView titleView = new TextView(context);
				titleView.setText(LOCKED_ENTRY_TITLE);
				titleView.setGravity(Gravity.CENTER_HORIZONTAL);
				titleView.setTextSize(20);
			
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
		receiptHolder.getFromList.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				TextView titleView = new TextView(context);
				titleView.setText(LOCKED_ENTRY_TITLE);
				titleView.setGravity(Gravity.CENTER_HORIZONTAL);
				titleView.setTextSize(20);
			
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
		receiptHolder.removeFile.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				TextView titleView = new TextView(context);
				titleView.setText(LOCKED_ENTRY_TITLE);
				titleView.setGravity(Gravity.CENTER_HORIZONTAL);
				titleView.setTextSize(20);
			
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
	}


}
