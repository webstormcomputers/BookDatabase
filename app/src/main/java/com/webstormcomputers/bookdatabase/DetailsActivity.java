package com.webstormcomputers.bookdatabase;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.webstormcomputers.bookdatabase.data.bookContract;
import com.webstormcomputers.bookdatabase.data.bookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity {
    //Set up variables and text views etc.
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    TextView mProductNameTextView;
    TextView mBookPriceTextView;
    TextView mBookQuantityTextView;
    TextView mSupplierNameTextView;
    Button mSupplierPhoneNumberButton;
    Button mPlusButton;
    Button mMinusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        mProductNameTextView = (TextView) findViewById(R.id.product_name);
        mBookPriceTextView = (TextView) findViewById(R.id.book_price);
        mBookQuantityTextView = (TextView) findViewById(R.id.quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.supplier_name);
        mSupplierPhoneNumberButton = (Button) findViewById(R.id.contact_supplier);
        mPlusButton = (Button) findViewById(R.id.plus_button);
        mMinusButton = (Button) findViewById(R.id.minus_button);

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        Cursor mCursor = getContentResolver().query(mCurrentBookUri, null, null, null, null);
        if (mCursor == null || mCursor.getCount() < 1) {
            return;
        }// always move cursor to first so you get the first entry of the data you requested.
        if (mCursor.moveToFirst()) {
            int nameColumnIndex = mCursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = mCursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnINdex = mCursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = mCursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = mCursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int idColumnIndex = mCursor.getColumnIndex(BookEntry._ID);

            String productName = mCursor.getString(nameColumnIndex);
            String price = mCursor.getString(priceColumnIndex);
            String quantity = mCursor.getString(quantityColumnINdex);
            String supplierName = mCursor.getString(supplierNameColumnIndex);
            final String supplierNumber = mCursor.getString(supplierNumberColumnIndex);
            Integer numQuantity = Integer.parseInt(quantity);
            final Integer id = mCursor.getInt(idColumnIndex);

            mProductNameTextView.setText(productName);
            mBookPriceTextView.setText(price);
            if (!quantity.isEmpty()){
                if (numQuantity < 0) {
                    mBookQuantityTextView.setText("0");
                } else {
                    mBookQuantityTextView.setText(quantity);
                }
            }
            if (!supplierName.isEmpty()) {
                mSupplierNameTextView.setText(supplierName);
            }
            if (!supplierNumber.isEmpty()) {
                SpannableString underLineSupplierNumber = new SpannableString(supplierNumber);
                underLineSupplierNumber.setSpan(new UnderlineSpan(), 0, underLineSupplierNumber.length(), 0);
                mSupplierPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplierNumber));
                        startActivity(phoneIntent);
                    }
                });
            }

            mPlusButton.setOnClickListener(new View.OnClickListener() {

                String bookQuantity = mBookQuantityTextView.getText().toString().trim();
                int numQuantity = Integer.parseInt(bookQuantity);

                @Override
                public void onClick(View v) {

                    numQuantity++;
                    ContentValues increaseValues = new ContentValues();
                    increaseValues.put(BookEntry.COLUMN_BOOK_QUANTITY, numQuantity);
                    Uri mCurrentBookuri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    int rowsupdate = getApplication().getContentResolver().update(mCurrentBookuri, increaseValues, null, null);
                    String numQuantityString = String.valueOf(numQuantity);
                    mBookQuantityTextView.setText(numQuantityString);
                }
            });
            mMinusButton.setOnClickListener(new View.OnClickListener() {

                String bookQuantity = mBookQuantityTextView.getText().toString().trim();
                int numQuantity = Integer.parseInt(bookQuantity);

                @Override
                public void onClick(View v) {
                    numQuantity--;

                    if (numQuantity <= 0) {
                        numQuantity = 0;
                        mBookQuantityTextView.setText("0");
                        ContentValues decreaseValues = new ContentValues();
                        decreaseValues.put(BookEntry.COLUMN_BOOK_QUANTITY, numQuantity);
                        Uri mCurrentBookuri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                        int rowsupdate = getApplication().getContentResolver().update(mCurrentBookuri, decreaseValues, null, null);
                        String numQuantityString = String.valueOf(numQuantity);
                        mBookQuantityTextView.setText(numQuantityString);
                    } else {
                        ContentValues decreaseValues = new ContentValues();
                        decreaseValues.put(BookEntry.COLUMN_BOOK_QUANTITY, numQuantity);
                        Uri mCurrentBookuri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                        int rowsupdate = getApplication().getContentResolver().update(mCurrentBookuri, decreaseValues, null, null);

                        String numQuantityString = String.valueOf(numQuantity);
                        mBookQuantityTextView.setText(numQuantityString);
                    }
                }
            });
        }

    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete_button_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel_button_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteBook() {

        if (mCurrentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish(); }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
        }
        return super.onOptionsItemSelected(item);
    }
}