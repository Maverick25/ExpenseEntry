package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ExpenseEntryType implements Parcelable
{
	private int id,postType;
	private String title;
	private boolean isActive;
	
	public ExpenseEntryType(int id,String title,int postType,boolean isActive)
	{
		this.id = id;
		this.title = title;
		this.postType = postType;
		this.isActive = isActive;
	}
	
	public ExpenseEntryType(Parcel in)
	{
		id = in.readInt();
		title = in.readString();
		postType = in.readInt();
		isActive = in.readByte() == 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPostType() {
		return postType;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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
		out.writeString(title);
		out.writeInt(postType);
		out.writeByte((byte) (isActive ? 1 : 0));
	}
	
	public static final Parcelable.Creator<ExpenseEntryType> CREATOR = new Parcelable.Creator<ExpenseEntryType>() 
			{
				public ExpenseEntryType createFromParcel(Parcel in)
				{
					return new ExpenseEntryType(in);
				}
				public ExpenseEntryType[] newArray(int size)
				{
					return new ExpenseEntryType[size];
				}
			};

	@Override
	public String toString() 
	{
		return title;
	}
	
	
}
