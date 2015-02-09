package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Job implements Parcelable
{
	private int id,companyId;
	private String name,customerName;
	
	public Job(int id,String name,String customerName,int companyId)
	{
		this.id = id;
		this.name = name;
		this.customerName = customerName;
		this.companyId = companyId;
	}
	
	public Job(Parcel in)
	{
		id = in.readInt();
		name = in.readString();
		customerName = in.readString();
		companyId = in.readInt();
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() 
	{
		if (id==0)
		{
			return "";
		}
		return id+" - "+name;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeInt(id);
		out.writeString(name);
		out.writeString(customerName);
		out.writeInt(companyId);
	}
	
	public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>()
			{
				public Job createFromParcel(Parcel in)
				{
					return new Job(in);
				}
				public Job[] newArray(int size)
				{
					return new Job[size];
				}
			};

	
}
