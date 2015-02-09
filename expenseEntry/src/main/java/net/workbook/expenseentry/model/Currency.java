package net.workbook.expenseentry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable
{
	private int id;
	private String name,isoCode,unit,fraction,unitPlural,fractionPlural;
	
	public Currency(int id,String name,String isoCode,String unit,String fraction,String unitPlural,String fractionPlural)
	{
		this.id = id;
		this.name = name;
		this.isoCode = isoCode;
		this.unit = unit;
		this.fraction = fraction;
		this.unitPlural = unitPlural;
		this.fractionPlural = fractionPlural;
	}
	
	public Currency(Parcel in)
	{
		id = in.readInt();
		name = in.readString();
		isoCode = in.readString();
		unit = in.readString();
		fraction = in.readString();
		unitPlural = in.readString();
		fractionPlural = in.readString();
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

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getFraction() {
		return fraction;
	}

	public void setFraction(String fraction) {
		this.fraction = fraction;
	}

	public String getUnitPlural() {
		return unitPlural;
	}

	public void setUnitPlural(String unitPlural) {
		this.unitPlural = unitPlural;
	}

	public String getFractionPlural() {
		return fractionPlural;
	}

	public void setFractionPlural(String fractionPlural) {
		this.fractionPlural = fractionPlural;
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
		out.writeString(isoCode);
		out.writeString(unit);
		out.writeString(fraction);
		out.writeString(unitPlural);
		out.writeString(fractionPlural);
	}
	
	public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() 
			{
				public Currency createFromParcel(Parcel in)
				{
					return new Currency(in);
				}
				public Currency[] newArray(int size)
				{
					return new Currency[size];
				}
			};

	@Override
	public String toString() {
		return isoCode+" - "+name;
	}
	
	
}
