package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable
{
	private int id;
	private String description;
	
	public Location(int id,String description)
	{
		this.id = id;
		this.description = description;
	}
	
	public Location(Parcel in)
	{
		id = in.readInt();
		description = in.readString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(description);
	}
	
	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() 
			{
				public Location createFromParcel(Parcel in)
				{
					return new Location(in);
				}
				public Location[] newArray(int size)
				{
					return new Location[size];
				}
			};

	@Override
	public String toString() {
		return description;
	}
	
}
