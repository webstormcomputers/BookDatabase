package com.webstormcomputers.bookdatabase;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.webstormcomputers.bookdatabase.data.bookContract;

import java.lang.reflect.Method;
import java.sql.BatchUpdateException;

public class BookCursorAdapter extends CursorAdapter {
    /**
     * Setup constants that let us use alternate lists depending
     * on where the user taps an entry. If from the Edit list then we use layout_edit
     * all others use layout list
     */
    private int LAYOUT_EDIT = 0;
    private int LAYOUT_LIST = 1;
    private int layoutType = LAYOUT_LIST;

    public BookCursorAdapter(Context context, Cursor c, int layoutType) {
        super(context, c, 0);
        this.layoutType = layoutType; //<- this line required me ask for help. I couldn't figure out how to bring the variable into scope
        // I figured out the rest of this code almost by myself but that line if from a udacity mentor
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (layoutType == LAYOUT_EDIT) {
            return LayoutInflater.from(context).inflate(R.layout.edit_list, parent, false);
        }
        else {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button button = (Button) view.findViewById(R.id.sell_button);
        int idColumnIndex = cursor.getColumnIndex(bookContract.BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_BOOK_QUANTITY);

        final Integer id = cursor.getInt(idColumnIndex);
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(bookName);
        summaryTextView.setText(bookPrice);
        quantityTextView.setText(quantity);
        //Probably should figure out how to put this in a separate file so it can be reused.
        if (layoutType == LAYOUT_LIST) {
            button.setOnClickListener(new View.OnClickListener() {
                int numQuantity = Integer.parseInt(quantity);

                @Override
                public void onClick(View v) {

                    numQuantity--;
                    if (numQuantity < 0) {
                        numQuantity = 0;
                    } else {
                        ContentValues decreaseValues = new ContentValues();
                        decreaseValues.put(bookContract.BookEntry.COLUMN_BOOK_QUANTITY, numQuantity);
                        Uri mCurrentBookuri = ContentUris.withAppendedId(bookContract.BookEntry.CONTENT_URI, id);

                        int rowsupdate = context.getContentResolver().update(mCurrentBookuri, decreaseValues, null, null);
                    }
                }
            });
        }
    }
}
