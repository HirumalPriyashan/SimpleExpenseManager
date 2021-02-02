package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.SQLiteDatabaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteDatabaseHandler sqLiteDatabaseHandler;
    private final String TABLE_NAME= "transactions";
    private final String KEY_ACCOUNT_NO = "accountNo";
    private final String KEY_DATE = "date";
    private final String KEY_EXPENSE_TYPE = "expenseType";
    private final String KEY_AMOUNT = "amount";
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public PersistentTransactionDAO(Context context) {
        this.sqLiteDatabaseHandler = SQLiteDatabaseHandler.getInstance(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, accountNo);
        values.put(KEY_DATE, DATE_FORMAT.format(date));
        values.put(KEY_EXPENSE_TYPE, expenseType.toString());
        values.put(KEY_AMOUNT,amount);

        // insert row
        sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getReadableDatabase();
        List<Transaction> transactions = new ArrayList<Transaction>();
        String[] projection = {
                KEY_ACCOUNT_NO,
                KEY_DATE,
                KEY_EXPENSE_TYPE,
                KEY_AMOUNT
        };
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,projection,null,null,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                String account_No = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_NO));
                String dateString = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                Date date = null;
                try {
                    date = DATE_FORMAT.parse(dateString);
                } catch (ParseException e) {
                }
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_TYPE)));
                double amount = cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT));

                transactions.add(new Transaction(date,account_No,expenseType,amount));
            } while (cursor.moveToNext());
        }

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getReadableDatabase();
        List<Transaction> transactions = new ArrayList<Transaction>();
        String[] projection = {
                KEY_ACCOUNT_NO,
                KEY_DATE,
                KEY_EXPENSE_TYPE,
                KEY_AMOUNT
        };
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,projection,null,null,null,null,null, String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                String account_No = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_NO));
                Date date = null;
                try {
                    date = DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                } catch (ParseException e) {

                }
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_TYPE)));
                double amount = cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT));

                transactions.add(new Transaction(date,account_No,expenseType,amount));
            } while (cursor.moveToNext());
        }

        return transactions;
    }
}
