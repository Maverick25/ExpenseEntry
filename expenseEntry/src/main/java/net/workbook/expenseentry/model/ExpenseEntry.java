package net.workbook.expenseentry.model;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;


public class ExpenseEntry implements Parcelable
{
	private String description;
	private double totalAmount;
	private int id,voucherCompanyId,approvalStatus,importDtlId;
	private boolean isApproved;
	private Date voucherDate;
	private ExpenseEntryType type;
	private Job job;
	private Company company;
	private Creditor creditor;
	private Activity activity;
	private File file;
	private Location location;
	private Currency currency;
	
	public ExpenseEntry() 
	{
		this.approvalStatus = 10;
		this.isApproved = false;
	}
	
	public ExpenseEntry
	(int id,ExpenseEntryType type,Job job,Date voucherDate,int voucherCompanyId,String description,Currency currency,double totalAmount,
	 Creditor creditor,boolean isApproved,Activity activity,int approvalStatus,File file,Location location,Company company,int importDtlId)
	{
		this.id = id;
		this.type = type;
		this.job = job;
		this.voucherDate = voucherDate;
		this.voucherCompanyId = voucherCompanyId;
		this.description = description;
		this.currency = currency;
		this.totalAmount = totalAmount;
		this.creditor = creditor;
		this.isApproved = isApproved;
		this.activity = activity;
		this.approvalStatus = approvalStatus;
		this.file = file;
		this.location = location;
		this.company = company;
		this.importDtlId = importDtlId;
	}

	public ExpenseEntry(Parcel in)
	{
		id = in.readInt();
		type = in.readParcelable(ExpenseEntryType.class.getClassLoader());
		job = in.readParcelable(Job.class.getClassLoader());
		
		int day = in.readInt();
		int month = in.readInt();
		int year = in.readInt();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		voucherDate = cal.getTime();
		
		voucherCompanyId = in.readInt();
		description = in.readString();
		currency = in.readParcelable(Currency.class.getClassLoader());
		totalAmount = in.readDouble();
		creditor = in.readParcelable(Creditor.class.getClassLoader());
		isApproved = in.readByte() == 1;
		activity = in.readParcelable(Activity.class.getClassLoader());
		approvalStatus = in.readInt();
		file = in.readParcelable(File.class.getClassLoader());
		location = in.readParcelable(Location.class.getClassLoader());
		company = in.readParcelable(Company.class.getClassLoader());
		importDtlId = in.readInt();
	}
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVoucherCompanyId() {
		return voucherCompanyId;
	}

	public void setVoucherCompanyId(int voucherCompanyId) {
		this.voucherCompanyId = voucherCompanyId;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(int approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

	public ExpenseEntryType getType() {
		return type;
	}

	public void setType(ExpenseEntryType type) {
		this.type = type;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Creditor getCreditor() {
		return creditor;
	}

	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public int getImportDtlId() {
		return importDtlId;
	}

	public void setImportDtlId(int importDtlId) {
		this.importDtlId = importDtlId;
	}

	public static Calendar dateToCalendar(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
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
		out.writeParcelable(type, flags);
		out.writeParcelable(job, flags);
		
		Calendar cal = dateToCalendar(voucherDate);
		
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		
		out.writeInt(day);
		out.writeInt(month);
		out.writeInt(year);
		
		out.writeInt(voucherCompanyId);
		out.writeString(description);
		out.writeParcelable(currency, flags);
		out.writeDouble(totalAmount);
		out.writeParcelable(creditor, flags);
		out.writeByte((byte) (isApproved ? 1 : 0));
		out.writeParcelable(activity, flags);
		out.writeInt(approvalStatus);
		out.writeParcelable(file, flags);
		out.writeParcelable(location, flags);
		out.writeParcelable(company, flags);
		try
		{
			out.writeInt(importDtlId);
		}
		catch (Exception e)
		{
			importDtlId = 0;
			out.writeInt(importDtlId);
		}
	}
	
	public static final Parcelable.Creator<ExpenseEntry> CREATOR = new Parcelable.Creator<ExpenseEntry>() 
			{
				public ExpenseEntry createFromParcel(Parcel in)
				{
					return new ExpenseEntry(in);
				}
				public ExpenseEntry[] newArray(int size)
				{
					return new ExpenseEntry[size];
				}
			};

}
