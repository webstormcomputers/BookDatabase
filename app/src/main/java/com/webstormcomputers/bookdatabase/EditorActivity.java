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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.webstormcomputers.bookdatabase.data.bookContract;
import com.webstormcomputers.bookdatabase.data.bookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private boolean mBookHasChanged = false;
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    //setup variables to hold our field entries
    private EditText mProductNameEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNUmberEditText;
    private Button mPlusButton;
    private Button mMinusButton;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.New_book_title_for_edit));
            invalidateOptionsMenu();
            ;
        } else {
            setTitle(getString(R.string.editor_activiry_existing_book_title));

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mBookPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNUmberEditText = (EditText) findViewById(R.id.edit_supplier_number);
        mPlusButton = (Button) findViewById(R.id.plus_button);
        mMinusButton = (Button) findViewById(R.id.minus_button);
    }

    private void saveBook() {
        //snag our text to edit
        String productNameString = mProductNameEditText.getText().toString().trim();
        String bookPrice = mBookPriceEditText.getText().toString().trim();
        String bookQuantity = mBookQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNUmber = mSupplierPhoneNUmberEditText.getText().toString().trim();

        if(mCurrentBookUri == null && TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(bookPrice)
                && TextUtils.isEmpty(bookQuantity) && TextUtils.isEmpty(supplierName)) {
            return;
        }
        //Build our insert statement
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString );
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNUmber);

        if(mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.Error_save_and_update_message, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, R.string.Error_saving_Book_message , Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0){
                Toast.makeText(this, R.string.Error_save_and_update_message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.Error_saving_Book_message , Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
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
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //snag variables from entered data and do a quick check to see if they are empty, toast if they are.
                String productNameString = mProductNameEditText.getText().toString().trim();
                String bookPrice = mBookPriceEditText.getText().toString().trim();
                String bookQuantity = mBookQuantityEditText.getText().toString().trim();
                String supplierName = mSupplierNameEditText.getText().toString().trim();
                String supplierPhoneNUmber = mSupplierPhoneNUmberEditText.getText().toString().trim();
                if (TextUtils.isEmpty(productNameString)) {
                    Toast.makeText(this,"Please Enter a Product Name and try again", Toast.LENGTH_LONG).show();
                    break;
                } else if (TextUtils.isEmpty(bookPrice)) {
                    Toast.makeText(this,"Please Enter a Book Price and try again", Toast.LENGTH_LONG).show();
                    break;
                } else if (TextUtils.isEmpty(bookQuantity)) {
                    Toast.makeText(this, "Please enter a Quantity and try again", Toast.LENGTH_LONG ).show();
                }
                else if (TextUtils.isEmpty(supplierName)) {
                    Toast.makeText(this, "Please enter a Supplier Name and try again", Toast.LENGTH_LONG ).show();
                }
                else if (TextUtils.isEmpty(supplierPhoneNUmber)) {
                    Toast.makeText(this, "Please enter a Supplier Phone Number and try again", Toast.LENGTH_LONG ).show();
                }
                else {
                    saveBook();
                    finish();
                    return true;}
                // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if(!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int suppliernamewColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);

            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            String bookPrice = price.toString();
            Integer quantity = cursor.getInt(quantityColumnIndex);
            String bookQuantity = quantity.toString();
            String supplierName = cursor.getString(suppliernamewColumnIndex);
            String supplierNumber = cursor.getString(supplierPhoneNumberIndex);
            final Integer id = cursor.getInt(idColumnIndex);

            mProductNameEditText.setText(name);
            mBookPriceEditText.setText(bookPrice);
            mBookQuantityEditText.setText(bookQuantity);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNUmberEditText.setText(supplierNumber);
            mPlusButton.setOnClickListener(new View.OnClickListener() {

                String bookQuantity = mBookQuantityEditText.getText().toString().trim();
                int numQuantity = Integer.parseInt(bookQuantity);

                @Override
                public void onClick(View v) {

                    numQuantity++;
                    ContentValues increaseValues = new ContentValues();
                    increaseValues.put(BookEntry.COLUMN_BOOK_QUANTITY, numQuantity);
                    Uri mCurrentBookuri = ContentUris.withAppendedId(bookContract.BookEntry.CONTENT_URI, id);

                    int rowsupdate = getApplication().getContentResolver().update(mCurrentBookuri, increaseValues, null, null);

                }
            });
            mMinusButton.setOnClickListener(new View.OnClickListener() {

                String bookQuantity = mBookQuantityEditText.getText().toString().trim();
                int numQuantity = Integer.parseInt(bookQuantity);

                @Override
                public void onClick(View v) {

                    numQuantity--;
                    ContentValues decreaseValues = new ContentValues();
                    decreaseValues.put(BookEntry.COLUMN_BOOK_QUANTITY, numQuantity);
                    Uri mCurrentBookuri = ContentUris.withAppendedId(bookContract.BookEntry.CONTENT_URI, id);

                    int rowsupdate = getApplication().getContentResolver().update(mCurrentBookuri, decreaseValues, null, null);

                }
            });
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNUmberEditText.setText("");
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.aler_dialog_question);
        builder.setPositiveButton(R.string.discard_alert_message, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
}