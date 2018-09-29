package com.webstormcomputers.bookdatabase.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper{
    //actual db file name
    private static final String DATABASE_NAME = "books.db";

    private static final int DATABASE_VERSION = 1;
    //one time setup of the database unless you're doing an upgrade
    public BookDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //code for actually creating the database
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + bookContract.BookEntry.TABLE_NAME + " ("
                + bookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + bookContract.BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + bookContract.BookEntry.COLUMN_BOOK_PRICE + " FLOAT NOT NULL,"
                + bookContract.BookEntry.COLUMN_BOOK_QUANTITY + " INT,"
                + bookContract.BookEntry.COLUMN_SUPPLIER_NAME + " STRING NOT NULL,"
                + bookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INT NOT NULL);";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //database at version 1
    }
}