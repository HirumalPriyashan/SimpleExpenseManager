package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.SQLiteDatabaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteDatabaseHandler sqLiteDatabaseHandler;
    private final String TABLE_NAME = "account";
    private final String KEY_ACCOUNT_NO = "accountNo";
    private final String KEY_BANK_NAME = "bankName";
    private final String KEY_ACCOUNT_HOLDER_NAME = "accountHolderName";
    private final String KEY_BALANCE = "balance";

    public PersistentAccountDAO(Context context) {
        this.sqLiteDatabaseHandler = SQLiteDatabaseHandler.getInstance(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getReadableDatabase();
        List<String> accountNos = new ArrayList<String>();
        String[] projection = {
                KEY_ACCOUNT_NO
        };
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,projection,null,null,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                String account_No = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_NO));
                accountNos.add(account_No);
            } while (cursor.moveToNext());
        }

        return accountNos;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getReadableDatabase();
        List<Account> accounts = new ArrayList<Account>();
        String[] projection = {
                KEY_ACCOUNT_NO,
                KEY_BANK_NAME,
                KEY_ACCOUNT_HOLDER_NAME,
                KEY_BALANCE
        };
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,projection,null,null,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                String account_No = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_NO));
                String bankName = cursor.getString(cursor.getColumnIndex(KEY_BANK_NAME));
                String accountHolderName = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_HOLDER_NAME));
                double balance = cursor.getDouble(cursor.getColumnIndex(KEY_BALANCE));

                accounts.add(new Account(account_No, bankName, accountHolderName, balance));
            } while (cursor.moveToNext());
        }

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getReadableDatabase();
        try {
            String[] projection = {
                    KEY_ACCOUNT_NO,
                    KEY_BANK_NAME,
                    KEY_ACCOUNT_HOLDER_NAME,
                    KEY_BALANCE
            };
            String selection = KEY_ACCOUNT_NO + " = ?";
            String[] selectionArgs = { accountNo };

            Cursor cursor = sqLiteDatabase.query(TABLE_NAME,projection,selection,selectionArgs,null,null,null);
            cursor.moveToFirst();

            String account_No = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndex(KEY_BANK_NAME));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_HOLDER_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndex(KEY_BALANCE));

            return new Account(account_No, bankName,accountHolderName,balance);
        }
        catch (Exception exception){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void addAccount(Account account) {
        List<String> accountNos = this.getAccountNumbersList();
        if ( accountNos.contains( account.getAccountNo() ) ){
            System.out.println("Already created account with that id");
            return;
        }

        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, account.getAccountNo());
        values.put(KEY_BANK_NAME, account.getBankName());
        values.put(KEY_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(KEY_BALANCE, account.getBalance());

        // insert row
        sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        // Get reference to writable DB
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getWritableDatabase();
        try {
            String selection = KEY_ACCOUNT_NO + " = ?";
            String[] selectionArgs = { accountNo };
            sqLiteDatabase.delete(TABLE_NAME, selection, selectionArgs);
        }
        catch (Exception exception){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        try {
            // get data for the relevant Account No
            Account account = this.getAccount(accountNo);
            switch (expenseType) {
                case EXPENSE:
                    account.setBalance(account.getBalance() - amount);
                    break;
                case INCOME:
                    account.setBalance(account.getBalance() + amount);
                    break;
            }
            ContentValues values = new ContentValues();
            values.put(KEY_BALANCE, account.getBalance());
            String selection = KEY_ACCOUNT_NO + " = ?";
            String[] selectionArgs = { account.getAccountNo() };

            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHandler.getWritableDatabase();
            // update database
            sqLiteDatabase.update(TABLE_NAME, values, selection, selectionArgs);
        }
        catch (Exception exception){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}
