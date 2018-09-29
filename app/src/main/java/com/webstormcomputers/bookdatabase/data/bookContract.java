package com.webstormcomputers.bookdatabase.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class bookContract {
    //contract is like the schema of your database it lets you grab column names and the name of the table

    private bookContract() {}

    public static final String CONTENT_AUTHORITY = "com.webstormcomputers.bookdatabase";
    //Android says this can be private but I can't access it elsewhere when I need it if I do.
    //this also allows us to customize and change our database uri so we can do more with it if needed.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Defining the path part of the URI
    public static final String PATH_BOOKS = "books";

    public static abstract class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        //table field names
        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_BOOK_PRICE = "book_price";
        public static final String COLUMN_BOOK_QUANTITY = "book_quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
