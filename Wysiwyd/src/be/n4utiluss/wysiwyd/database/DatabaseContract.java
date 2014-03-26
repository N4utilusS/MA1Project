package be.n4utiluss.wysiwyd.database;

import android.provider.BaseColumns;

public final class DatabaseContract {
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INTEGER_TYPE = " INT";
    private static final String REAL_TYPE = " REAL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String NOT_NULL = " NOT NULL";
    
    
	/**
	 * Inner class for the bottle table
	 * @author anthonydebruyn
	 *
	 */
	public static abstract class BottleTable implements BaseColumns {
		public static final String TABLE_NAME = "Bottle";
		public static final String COLUMN_NAME_CODE = "Code";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_COLOUR = "Colour";
		public static final String COLUMN_NAME_SUGAR = "Sugar";
		public static final String COLUMN_NAME_EFFERVESCENCE = "Effervescence";
		public static final String COLUMN_NAME_APPELLATION = "Appellation";
		public static final String COLUMN_NAME_REGION = "Region";
		public static final String COLUMN_NAME_VINTAGE = "Vintage";
		public static final String COLUMN_NAME_QUANTITY = "Quantity";
		public static final String COLUMN_NAME_ADD_DATE = "Add Date";
		public static final String COLUMN_NAME_APOGEE = "Apogee";
		public static final String COLUMN_NAME_PRICE = "Price";
		public static final String COLUMN_NAME_LOCATION = "Location";
		public static final String COLUMN_NAME_MARK = "Mark";
		public static final String COLUMN_NAME_IMAGE = "Image";
		public static final String COLUMN_NAME_NOTE = "Note";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
						_ID 						+ INTEGER_TYPE 	+ PRIMARY_KEY 	+ AUTOINCREMENT + COMMA_SEP +
						COLUMN_NAME_CODE 			+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_CODE + " >= 0)"+ COMMA_SEP +
						COLUMN_NAME_NAME 			+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
						COLUMN_NAME_COLOUR 			+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_COLOUR + " >= 0 and " + COLUMN_NAME_COLOUR + " <= 2)" + COMMA_SEP +
						COLUMN_NAME_SUGAR 			+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_SUGAR + " >= 0 and " + COLUMN_NAME_SUGAR + " <= 3)" + COMMA_SEP +
						COLUMN_NAME_EFFERVESCENCE 	+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_EFFERVESCENCE + " >= 0 and " + COLUMN_NAME_EFFERVESCENCE + " <= 3)" + COMMA_SEP +
						COLUMN_NAME_APPELLATION 	+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
						COLUMN_NAME_VINTAGE		 	+ INTEGER_TYPE 	+ " CHECK ((" + COLUMN_NAME_VINTAGE + " is not null and" + COLUMN_NAME_VINTAGE + " >= 0) or ( " + COLUMN_NAME_VINTAGE + " is null and " + COLUMN_NAME_EFFERVESCENCE + " > 0))"+ COMMA_SEP +
						COLUMN_NAME_QUANTITY 		+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_QUANTITY + " >= 0)"+ COMMA_SEP +
						COLUMN_NAME_ADD_DATE 		+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
						COLUMN_NAME_APOGEE 			+ INTEGER_TYPE 	+ " CHECK (" + COLUMN_NAME_APOGEE + " is null or (" + COLUMN_NAME_APOGEE + " >= 0))" + COMMA_SEP +
						COLUMN_NAME_PRICE 			+ REAL_TYPE 	+ " CHECK (" + COLUMN_NAME_PRICE + " is null or (" + COLUMN_NAME_APOGEE + " >= 0))" + COMMA_SEP +
						COLUMN_NAME_LOCATION 		+ TEXT_TYPE 	+ COMMA_SEP +
						COLUMN_NAME_MARK 			+ INTEGER_TYPE 	+ " CHECK (" + COLUMN_NAME_MARK + " is null or (" + COLUMN_NAME_MARK + " >= 1 and " + COLUMN_NAME_MARK + " <= 5))" + COMMA_SEP +
						COLUMN_NAME_IMAGE 			+ TEXT_TYPE 	+ COMMA_SEP +
						COLUMN_NAME_NOTE 			+ TEXT_TYPE 	+ COMMA_SEP +
				")";
		
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	/**
	 * Inner class for the variety table
	 * @author anthonydebruyn
	 *
	 */
	public static abstract class Variety implements BaseColumns {
		public static final String TABLE_NAME = "Variety";
		public static final String COLUMN_NAME_NAME = "Name";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
						_ID 				+ INTEGER_TYPE 	+ PRIMARY_KEY 	+ AUTOINCREMENT + COMMA_SEP +
						COLUMN_NAME_NAME	+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
				")";
		
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	/**
	 * Inner class for the bottle - variety relation
	 * @author anthonydebruyn
	 *
	 */
	public static abstract class BottleVariety implements BaseColumns {
		public static final String TABLE_NAME = "BottleVariety";
		public static final String COLUMN_NAME_BOTTLE_ID = "Bottle Id";
		public static final String COLUMN_NAME_VARIETY_ID = "Variety Id";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
						_ID 					+ INTEGER_TYPE 	+ PRIMARY_KEY 	+ AUTOINCREMENT + COMMA_SEP +
						COLUMN_NAME_BOTTLE_ID	+ INTEGER_TYPE	+ NOT_NULL + " CHECK (" + COLUMN_NAME_BOTTLE_ID + " >= 0)"+ COMMA_SEP +
						COLUMN_NAME_VARIETY_ID	+ INTEGER_TYPE	+ NOT_NULL + " CHECK (" + COLUMN_NAME_VARIETY_ID + " >= 0)"+ COMMA_SEP +
				")";
		
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
