package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity implements Parcelable
{
	private int id;
	private String text;
	
	public Activity(int id,String text)
	{
		this.id = id;
		this.text = text;
	}
	
	public Activity(Parcel in)
	{
		id = in.readInt();
		text = in.readString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(text);
	}
	
	public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() 
			{
				public Activity createFromParcel(Parcel in)
				{
					return new Activity(in);
				}
				public Activity[] newArray(int size)
				{
					return new Activity[size];
				}
			};

	@Override
	public String toString() 
	{
		if (id == 0)
		{
			return "";
		}
		return id+" - "+text;
	}
	
	
	
	
}
