package net.workbook.expenseentry.interfaces;

public interface Finals 
{
	//// general
	public static final String NOTHING = "";
	
	//// user info
	public static final String SERVER = "SERVER";
	public static final String ID = "ID";
	public static final String COMPANY_ID = "COMPANY_ID";
	
	//// data info
	public static final String CATEGORY = "CATEGORY";
	public static final String SELECTED_ITEM = "SELECTED_ITEM";
	public static final String SELECTED_ENTRY = "SELECTED_ENTRY";
	public static final String SELECTED_DATE = "SELECTED_DATE";
	public static final String ADDED_DESCRIPTION = "ADDED_DESCRIPTION";
	public static final String SELECTED_AMOUNT = "SELECTED_AMOUNT";
	public static final String CODE = "CODE";
	public static final String CANCELLED_SELECTION = "CANCELLED_SELECTION";
	public static final String SELECTED_RECEIPT = "SELECTED_RECEIPT";
	public static final String SELECTED_BITMAP = "SELECTED_BITMAP";
	public static final String SELECTED_POSITION = "SELECTED_POSITION";
	public static final String SELECTED_PATH = "SELECTED_PATH";
	public static final String SELECTED_RESOLUTION = "SELECTED_RESOLUTION";
	public static final String CHANGED_AMOUNT = "CHANGED_AMOUNT";
	
	//// category codes
	public static final int COMPANY = 0;
	public static final int EXPENSE_TYPE = 1;
	public static final int JOB = 2;
	public static final int ACTIVITY = 3;
	public static final int DATE = 4;
	public static final int LOCATION = 5;
	public static final int CURRENCY = 6;
	public static final int TOTAL_AMOUNT = 7;
	public static final int DESCRIPTION = 8;
	public static final int CREDITOR = 9;
	public static final int RECEIPT = 10;
	
	public static final int LOOK_AT_PIC = 21;
	public static final int TAKE_PIC = 22;
	public static final int GET_PIC = 23;
	public static final int LOOK_AT_PIC_FOR_ENTRY = 24;
	public static final int TAKE_PIC_FOR_ENTRY = 25;
	public static final int GET_PIC_FOR_ENTRY = 26;
	
	public static final int DEFAULT_CODE = 30;
	public static final int EDIT = 31;
	public static final int ADD = 32;
	public static final int DELETE = 33;
	public static final int APPROVE = 34;
	public static final int REJECT = 35;
	public static final int DELETE_FILE = 36;
	public static final int EDIT_FOR_FILE = 37;
	public static final int FROM_GALLERY = 38;
	
	public static final int SELECTED_PICTURE = 41;
	public static final int CAMERA_REQUEST = 42;
	public static final int FOR_ENTRY = 43;
	
	//// labels
	public static final String COMPANY_LABEL = "Company";
	public static final String EXPENSE_TYPE_LABEL = "Expense type";
	public static final String JOB_LABEL = "Job";
	public static final String ACTIVITY_LABEL = "Activity";
	public static final String DATE_LABEL = "Date";
	public static final String LOCATION_LABEL = "Location";
	public static final String CURRENCY_LABEL = "Currency";
	public static final String TOTAL_AMOUNT_LABEL = "Amount";
	public static final String DESCRIPTION_LABEL = "Description";
	public static final String CREDITOR_LABEL = "Creditor";
	public static final String RECEIPT_LABEL = "Receipt";
	
	public static final String LOADING_DATA = "Loading data..";
	public static final String PROCESSING_REQUEST = "Processing request..";
	
	//// button labels
	public static final String IN_PREPARATION = "Under preparation";
	public static final String FOR_APPROVAL = "For approval";
	
	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";
	public static final String SYNC = "Sync";
	public static final String DISMISS = "Dismiss";
	
	public static final String APPROVAL = "Approve";
	public static final String NO_APPROVAL = "Save without approval";
	public static final String REJECT_WORD = "Reject";
	
	public static final String USE_THIS = "Use this file";
	
	public static final String TAKE_PICTURE = "Take a Picture";
	public static final String GET_FROM_GALLERY = "Get from Gallery";
	
	//// alert questions
	public static final String APPROVED_TITLE = "Approved";
	public static final String REJECTED_TITLE = "Rejected";
	public static final String NOT_APPROVED_TITLE = "Entry not approved";
	public static final String NOT_REJECTED_TITLE = "Entry not rejected";
	public static final String APPROVAL_TITLE = "Entry to approve";
	public static final String LOCKED_ENTRY_TITLE = "Entry locked";
	public static final String UPLOAD_TITLE = "Upload size";
	
	public static final String SYNC_QUESTION = "Would you like to sync the data with the database?";
	
	public static final String TAKE_OR_GET = "Replace the current picture:";
	public static final String SURE_DELETE = "Are you sure you want to delete this entry?";
	public static final String SURE_CANCEL = "Are you sure you want to cancel this entry?";
	public static final String SURE_REJECT = "Are you sure you want to reject this entry?\nType reject comments below:";
	public static final String SURE_MOVE = "Are you sure you want to move to the next entry? You will lose all changes that have been made.";
	public static final String READY_FOR_APPROVAL = "This entry is now ready for your approval. Would you like to approve it?";
	public static final String LOCKED_ENTRY = "This entry is locked. If you wish to make any changes, you must unlock the entry first.";
	public static final String APPROVED = "This entry has been successfully approved!";
	public static final String REJECTED = "This entry has been successfully rejected!";
	
	//// requests
	public static final String GET_ENTRIES = "/api/personalexpense/expenseentries/";
	public static final String GET_CURRENCIES = "/api/core/currencies";
	public static final String GET_TYPES = "/api/personalexpense/ExpenseEntry/types/";
	public static final String GET_RECEIPTS = "/api/personalexpense/ExpenseEntry/recieptfiles/";
	public static final String GET_THUMB = "/api/files/ExpenseEntry/recieptfile/Thumb/";
	public static final String GET_ENTRY_THUMB = "/api/files/ExpenseEntry/ExpenseEntryFile/Thumb/";
	public static final String GET_PHOTO = "/api/files/ExpenseEntry/recieptfile/";
	public static final String GET_ENTRY_PHOTO = "/api/files/ExpenseEntry/ExpenseEntryFile/";
}
