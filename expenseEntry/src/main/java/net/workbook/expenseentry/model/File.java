package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class File implements Parcelable
{
	private String fileName;
	private byte[] bitmapBytes;
	
	public File()
	{
	}
	
	public File(String fileName)
	{
		this.fileName = fileName;
	}
	
	public File(String fileName,byte[] bitmapBytes)
	{
		this.fileName = fileName;
		this.bitmapBytes = bitmapBytes;
	}
	
	public File (Parcel in)
	{
		fileName = in.readString();
	}

	public String getFileName() 
	{
		return fileName;
	}

	public void setFileName(String fileName) 
	{
		this.fileName = fileName;
	}

	public byte[] getBitmapBytes() {
		return bitmapBytes;
	}

	public void setBitmapBytes(byte[] bitmapBytes) {
		this.bitmapBytes = bitmapBytes;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeString(fileName);
	}
	
	public static final Parcelable.Creator<File> CREATOR = new Parcelable.Creator<File>() 
			{
				public File createFromParcel(Parcel in)
				{
					return new File(in);
				}
				public File[] newArray(int size)
				{
					return new File[size];
				}
			};

	@Override
	public String toString() {
		return fileName;
	}
	
	
}
