package be.n4utiluss.wysiwyd.database;

import android.provider.BaseColumns;

/**
 * Class defining the database structure, and the scripts of tables creation.
 * @author anthonydebruyn
 *
 */
public final class DatabaseContract {
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";
    private static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ", ";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String UNIQUE = " UNIQUE";
    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    public static final String STRING_DELIMITER = "'";
    public static final String LIKE = " LIKE ";
    public static final String ANY_STRING_WILDCARD = "%";
    public static final String BEQ = ">=";
    public static final String LEQ = "<=";
    public static final String EQ = "=";
    public static final String IN = " IN ";
    public static final String OPENING_PAR = "(";
    public static final String CLOSING_PAR = ")";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String ASC = " ASC ";
    public static final String DESC = " DESC ";
    public static final String RANDOM = " RANDOM() ";
    public static final String NOT = " NOT ";
    public static final String NULL = " NULL ";
    public static final String LIMIT = " LIMIT ";

    
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
		public static final String COLUMN_NAME_ADD_DATE = "Add_Date";
		public static final String COLUMN_NAME_APOGEE = "Apogee";
		public static final String COLUMN_NAME_PRICE = "Price";
		public static final String COLUMN_NAME_LOCATION = "Location";
		public static final String COLUMN_NAME_MARK = "Mark";
		public static final String COLUMN_NAME_IMAGE = "Image";
		public static final String COLUMN_NAME_NOTE = "Note";
		
		public static final int WHITE = 0;
		public static final int RED = 1;
		public static final int ROSE = 2;
		
		public static final int DRY = 0;
		public static final int MEDIUM_DRY = 1;
		public static final int MEDIUM_SWEET = 2;
		public static final int SWEET = 3;
		
		public static final int NOT_SPARKLING = 0;
		public static final int LIGHT_SPARKLING = 1;
		public static final int MEDIUM_SPARKLING = 2;
		public static final int HIGH_SPARKLING = 3;

		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
						_ID 						+ INTEGER_TYPE 	+ PRIMARY_KEY 	+ AUTOINCREMENT + COMMA_SEP +
						COLUMN_NAME_CODE 			+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_CODE + " >= 0)"+ COMMA_SEP +
						COLUMN_NAME_NAME 			+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
						COLUMN_NAME_COLOUR 			+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_COLOUR + " >= 0 and " + COLUMN_NAME_COLOUR + " <= 2)" + COMMA_SEP +
						COLUMN_NAME_SUGAR 			+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_SUGAR + " >= 0 and " + COLUMN_NAME_SUGAR + " <= 3)" + COMMA_SEP +
						COLUMN_NAME_EFFERVESCENCE 	+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_EFFERVESCENCE + " >= 0 and " + COLUMN_NAME_EFFERVESCENCE + " <= 3)" + COMMA_SEP +
						COLUMN_NAME_REGION			+ TEXT_TYPE		+ COMMA_SEP +
						COLUMN_NAME_APPELLATION 	+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
						COLUMN_NAME_VINTAGE		 	+ INTEGER_TYPE 	+ " CHECK ((" + COLUMN_NAME_VINTAGE + " is not null and " + COLUMN_NAME_VINTAGE + " >= 0) or ( " + COLUMN_NAME_VINTAGE + " is null and " + COLUMN_NAME_EFFERVESCENCE + " > 0))"+ COMMA_SEP +
						COLUMN_NAME_QUANTITY 		+ INTEGER_TYPE 	+ NOT_NULL + " CHECK (" + COLUMN_NAME_QUANTITY + " >= 0)"+ COMMA_SEP +
						COLUMN_NAME_ADD_DATE 		+ TEXT_TYPE 	+ NOT_NULL + COMMA_SEP +
						COLUMN_NAME_APOGEE 			+ TEXT_TYPE 	+ COMMA_SEP +
						COLUMN_NAME_PRICE 			+ REAL_TYPE 	+ " CHECK (" + COLUMN_NAME_PRICE + " is null or (" + COLUMN_NAME_PRICE + " >= 0))" + COMMA_SEP +
						COLUMN_NAME_LOCATION 		+ TEXT_TYPE 	+ COMMA_SEP +
						COLUMN_NAME_MARK 			+ INTEGER_TYPE 	+ " CHECK (" + COLUMN_NAME_MARK + " is null or (" + COLUMN_NAME_MARK + " >= 1 and " + COLUMN_NAME_MARK + " <= 5))" + COMMA_SEP +
						COLUMN_NAME_IMAGE 			+ TEXT_TYPE 	+ COMMA_SEP +
						COLUMN_NAME_NOTE 			+ TEXT_TYPE 	+ 
				")";
		
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	/**
	 * Inner class for the variety table
	 * @author anthonydebruyn
	 *
	 */
	public static abstract class VarietyTable implements BaseColumns {
		public static final String TABLE_NAME = "Variety";
		public static final String COLUMN_NAME_NAME = "Name";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
						_ID 				+ INTEGER_TYPE 	+ PRIMARY_KEY 	+ AUTOINCREMENT + COMMA_SEP +
						COLUMN_NAME_NAME	+ TEXT_TYPE 	+ UNIQUE 		+ NOT_NULL +
				")";
		
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	/**
	 * Inner class for the bottle - variety relation
	 * @author anthonydebruyn
	 *
	 */
	public static abstract class BottleVarietyTable implements BaseColumns {
		public static final String TABLE_NAME = "BottleVariety";
		public static final String COLUMN_NAME_BOTTLE_ID = "Bottle_Id";
		public static final String COLUMN_NAME_VARIETY_ID = "Variety_Id";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
						_ID 					+ INTEGER_TYPE 	+ PRIMARY_KEY 	+ AUTOINCREMENT + COMMA_SEP +
						COLUMN_NAME_BOTTLE_ID	+ INTEGER_TYPE	+ NOT_NULL + " CHECK (" + COLUMN_NAME_BOTTLE_ID + " >= 0)"+ COMMA_SEP +
						COLUMN_NAME_VARIETY_ID	+ INTEGER_TYPE	+ NOT_NULL + " CHECK (" + COLUMN_NAME_VARIETY_ID + " >= 0)"+
				")";
		
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
