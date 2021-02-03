package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "180463U";

    // Table Names
    private static final String TABLE_ACCOUNT = "account";
    private static final String TABLE_TRANSACTION= "transactions";

    // Common columns
    private static final String KEY_ACCOUNT_NO = "accountNo";

    // ACCOUNT Table - column names
    private static final String KEY_BANK_NAME = "bankName";
    private static final String KEY_ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static  final String KEY_BALANCE = "balance";

    // TRANSACTION Table - column names
    private static final String KEY_DATE = "date";
    private static final String KEY_EXPENSE_TYPE = "expenseType";
    private static final String KEY_AMOUNT = "amount";

    // Table Create Statements
    // Account table create statement
    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE "
            + TABLE_ACCOUNT + "(" + KEY_ACCOUNT_NO + " TEXT(50) PRIMARY KEY," + KEY_BANK_NAME
            + " TEXT(200) NOT NULL," + KEY_ACCOUNT_HOLDER_NAME + " TEXT(200) NOT NULL," + KEY_BALANCE
            + " REAL NOT NULL" + ")";

    // Transaction table create statement
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION
            + "(" + KEY_DATE + " DATE NOT NULL," + KEY_ACCOUNT_NO + " TEXT(50) NOT NULL,"
            + KEY_EXPENSE_TYPE + " TEXT NOT NULL CHECK (" + KEY_EXPENSE_TYPE + " == \"EXPENSE\" OR "
            + KEY_EXPENSE_TYPE + " == \"INCOME\")," + KEY_AMOUNT + " REAL NOT NULL," + " FOREIGN KEY " + "("
            + KEY_ACCOUNT_NO + ") REFERENCES " + TABLE_ACCOUNT + "(" + KEY_ACCOUNT_NO + ")" + ")" ;

    private static SQLiteDatabaseHandler sqLiteDatabaseHandlerInstance = null;

    private SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLiteDatabaseHandler getInstance(Context context)
    {
        if (sqLiteDatabaseHandlerInstance == null){
            sqLiteDatabaseHandlerInstance = new SQLiteDatabaseHandler(context);
        }
        return sqLiteDatabaseHandlerInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // creating required tables
        sqLiteDatabase.execSQL(CREATE_TABLE_ACCOUNT);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        // create new tables
        onCreate(sqLiteDatabase);
    }
}
