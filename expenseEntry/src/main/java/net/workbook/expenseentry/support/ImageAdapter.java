package net.workbook.expenseentry.support;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import net.workbook.expenseentry.MainActivity;
import net.workbook.expenseentry.R;
import net.workbook.expenseentry.interfaces.Finals;
import net.workbook.expenseentry.model.ReceiptFile;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter implements Finals
{
    private LayoutInflater inflater;
    private ArrayList<ReceiptFile> receipts;

    public ImageAdapter(Context context,ArrayList<ReceiptFile> receipts) 
    {
        inflater = LayoutInflater.from(context);
        this.receipts = receipts;
    }

    public int getCount() 
    {
        return receipts.size();
    }

    public Object getItem(int position) {
        return receipts.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	holder = new ViewHolder();
        	convertView = inflater.inflate(R.layout.receipt_grid, parent, false);
        	holder.photo = (ImageView) convertView.findViewById(R.id.photo);
        	holder.photoDescription = (TextView) convertView.findViewById(R.id.photoDescription);
            holder.photoUpload = (TextView) convertView.findViewById(R.id.photoUpload);
            convertView.setTag(holder);
        } 
        else 
        {
        	holder = (ViewHolder) convertView.getTag();
        }
        
        try
        {
        	
        	final ReceiptFile receipt =  receipts.get(position);
        	
        	holder.photo.setAnimation(null);
            
        	UrlImageViewHelper.setUrlDrawable(holder.photo, requestThumb(receipt.getId(), receipt.getCompanyId()), null, 0, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(300);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                        
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        loadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        receipt.setBitmapBytes(byteArray);
                    }
            });
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        try
        {
        	holder.photoDescription.setText(receipts.get(position).getDescription());
        }
        catch (Exception e) {}
        
        try
        {
        	String dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault()).format(receipts.get(position).getUploadDate());
		    holder.photoUpload.setText(dateFormat);
        }
        catch (Exception e) {}
        
        return convertView;
    }

	class ViewHolder
    {
    	ImageView photo;
    	TextView photoDescription;
    	TextView photoUpload;
    }
	
	public String requestThumb(int id,int companyId)
	{
		return "http://"+MainActivity.getServer()+GET_THUMB+companyId+"/"+id;
	}
	
}
