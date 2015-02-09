package net.workbook.expenseentry.model;

import java.util.Calendar;
import java.util.Date;


import android.os.Parcel;
import android.os.Parcelable;

public class ReceiptFile extends File implements Parcelable
{
	private int id,companyId;
	private String description;
	private Date uploadDate;
	private byte[] bitmapBytes;
	
	public ReceiptFile() 
	{
		super();
	}
	
	public ReceiptFile(int id,String fileName,String description,int companyId,Date uploadDate)
	{
		super(fileName);
		this.id = id;
		this.description = description;
		this.companyId = companyId;
		this.uploadDate = uploadDate;
	}
	
	public ReceiptFile(int id,String fileName,String description,int companyId,Date uploadDate,byte[] bitmapBytes)
	{
		super(fileName);
		this.id = id;
		this.description = description;
		this.companyId = companyId;
		this.uploadDate = uploadDate;
		this.bitmapBytes = bitmapBytes;
	}

	public ReceiptFile(Parcel in)
	{
		super(in);
		
		id = in.readInt();
		description = in.readString();
		companyId = in.readInt();
		
		int day = in.readInt();
		int month = in.readInt();
		int year = in.readInt();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		uploadDate = cal.getTime();
		try
		{
			bitmapBytes = new byte[in.readInt()];
			in.readByteArray(bitmapBytes);
		}
		catch(Exception e) {}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getFileName() {
		return super.getFileName();
	}

	public void setFileName(String fileName) {
		super.setFileName(fileName);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public byte[] getBitmapBytes() {
		return bitmapBytes;
	}

	public void setBitmapBytes(byte[] bitmapBytes) {
		this.bitmapBytes = bitmapBytes;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public static Calendar dateToCalendar(Date date)
	{
		try
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		}
		catch (NullPointerException e)
		{
			Calendar calendar = Calendar.getInstance();
			return calendar;
		}
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		super.writeToParcel(out, flags);
		out.writeInt(id);
		out.writeString(description);
		out.writeInt(companyId);
		
		Calendar cal = dateToCalendar(uploadDate);
		
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		
		out.writeInt(day);
		out.writeInt(month);
		out.writeInt(year);
		
		if (bitmapBytes != null)
		{
			out.writeInt(bitmapBytes.length);
			out.writeByteArray(bitmapBytes);
		}
	}
	
	public static final Parcelable.Creator<ReceiptFile> CREATOR = new Parcelable.Creator<ReceiptFile>() 
			{
				public ReceiptFile createFromParcel(Parcel in)
				{
					return new ReceiptFile(in);
				}
				public ReceiptFile[] newArray(int size)
				{
					return new ReceiptFile[size];
				}
			};

	@Override
	public String toString() {
		return super.toString();
	}
	
}
