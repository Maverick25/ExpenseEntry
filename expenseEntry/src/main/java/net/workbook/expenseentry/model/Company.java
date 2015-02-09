package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable
{
	private int id;
	private String name;
	
	public Company(int id,String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public Company(Parcel in)
	{
		id = in.readInt();
		name = in.readString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(name);
	}
	
	public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() 
			{
				public Company createFromParcel(Parcel in)
				{
					return new Company(in);
				}
				public Company[] newArray(int size)
				{
					return new Company[size];
				}
			};

	@Override
	public String toString() {
		return name;
	}
	
	
	
	
}
