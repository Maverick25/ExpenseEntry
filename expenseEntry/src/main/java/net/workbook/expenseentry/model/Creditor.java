package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Creditor implements Parcelable
{
	private int id,companyId,isBlocked;
	private String name,number;
	
	public Creditor(int id,String name,String number, int companyId)
	{
		this.id = id;
		this.name = name;
		this.number = number;
		this.companyId = companyId;
	}
	
	public Creditor(int id,String name,String number, int companyId, int isBlocked)
	{
		this.id = id;
		this.name = name;
		this.number = number;
		this.companyId = companyId;
		this.isBlocked = isBlocked;
	}
	
	public Creditor(Parcel in)
	{
		id = in.readInt();
		name = in.readString();
		number = in.readString();
		companyId = in.readInt();
		isBlocked = in.readInt();
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(int isBlocked) {
		this.isBlocked = isBlocked;
	}

	@Override
	public String toString() 
	{
		return name;
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
		out.writeString(number);
		out.writeInt(companyId);
		out.writeInt(isBlocked);
	}
	
	public static final Parcelable.Creator<Creditor> CREATOR = new Parcelable.Creator<Creditor>() 
			{
				public Creditor createFromParcel(Parcel in)
				{
					return new Creditor(in);
				}
				public Creditor[] newArray(int size)
				{
					return new Creditor[size];
				}
			};
	
	
	
}
