package com.webstormcomputers.bookdatabase;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.webstormcomputers.bookdatabase.data.BookDbHelper;
import com.webstormcomputers.bookdatabase.data.bookContract;
import com.webstormcomputers.bookdatabase.data.bookContract.BookEntry;

public class EditorListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    //Set up variables
    private static final String CAT_TAG = "Catalog Activity";
    private static final int BOOK_LOADER = 0;
    private int LAYOUT_EDIT = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_list);
        ListView bookListView = (ListView) findViewById(R.id.edit_list);
        View emptyView = findViewById(R.id.edit_empty_view);
        bookListView.setEmptyView(emptyView);
        //setup cursor adapter so we can add it to our layout.
        mCursorAdapter = new BookCursorAdapter(this, null, LAYOUT_EDIT);
        bookListView.setAdapter(mCursorAdapter);
        //set on click listener so we can detect clicks
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(EditorListActivity.this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_list, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};
        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(EditorListActivity.this, EditorActivity.class );
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}