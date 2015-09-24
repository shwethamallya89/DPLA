/**
 * Original Author: Shwetha Mallya, Minxuan Guo
 *
 * Copyright (C) 2014 INF 385T MAMA Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.project.android.dpla.mama_dpla;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.project.android.mama_dpla.R;

import java.util.ArrayList;


public class ResultActivity extends Activity {

    /* Variable Declarations */
    private int mCurr_item_position = -1; // to be accessible throughout the class

    /**
     * Called when launching the screen for the first time
     * Get item's position in items_ from Intent
     * Get other info from DataManager's static variable items_
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);
        mCurr_item_position = position;
        ArrayList<DPLAItem> items_ = DataManager.getInstance().getSearchResults();
        DPLAItem item = items_.get(position);
        String itemTitle = item.getTitle();
        String linkURL = item.getUrl();
        String collectionTitle = item.getCollection();
        String tags = item.getTags();
        String type = item.getType();

        if(collectionTitle == null) collectionTitle = "No title available";

        final TextView results = (TextView) findViewById(R.id.resultPage);
        results.setMovementMethod(LinkMovementMethod.getInstance());
        results.setText(itemTitle);

        final TextView mURL = (TextView) findViewById(R.id.link);
        mURL.setMovementMethod(LinkMovementMethod.getInstance());
        mURL.setText(linkURL);

        final TextView collTitle = (TextView) findViewById(R.id.collectionTitle);
        collTitle.setMovementMethod(LinkMovementMethod.getInstance());
        collTitle.setText(collectionTitle);

        final TextView tagView = (TextView) findViewById(R.id.tagView);
        tagView.setMovementMethod(LinkMovementMethod.getInstance());

        if (tags == null) tags = "No Tags";
        tagView.setText(tags);

        if (type == null) type = "No type available";
        final TextView showType = (TextView) findViewById(R.id.TypeView);
        showType.setMovementMethod(LinkMovementMethod.getInstance());
        showType.setText(type);
    }

    /**
     * Response Method of button "Ok" (@id/enterTag)
     */
    public void addTag (View view){
        ArrayList<DPLAItem> items_ = DataManager.getInstance().getSearchResults();
        DPLAItem item = items_.get(mCurr_item_position);
        EditText enterTag = (EditText) findViewById(R.id.enterTag);
        String newTag = enterTag.getText().toString().trim();
        hideKeyboard();
        String oldTag = item.getTags();
        String atId = item.getId();

        if (newTag.length() == 0) return;

        // Append new tag to old tags
        String allTags = (oldTag == null )? newTag : (oldTag + " , " + newTag);
        item.setTags( allTags);
        MAMADbHelper helper = new MAMADbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        // Store tag to database
        String selection = MAMAContract.ItemEntry.COLUMN_NAME_ITEMATID + " = ?" ;
        String[] selectionArgs = {atId};
        Cursor c = db.query(
                MAMAContract.ItemEntry.TABLE_NAME,
                MAMAContract.ItemEntry.ALL_COLUMS,
                selection ,
                selectionArgs,
                null,
                null,
                null
        );
        ContentValues values = new ContentValues();
        if (c.getCount() == 0) { // if there is no record, insert
            values.put(MAMAContract.ItemEntry.COLUMN_NAME_ITEMATID, atId);
            values.put(MAMAContract.ItemEntry.COLUMN_NAME_TAGS, allTags);
            long newRowId = db.insert(MAMAContract.ItemEntry.TABLE_NAME, null, values);
            if (newRowId < 0) {
                System.err.println("not insert!");
            }
        } else {
            values.put(MAMAContract.ItemEntry.COLUMN_NAME_TAGS, allTags);
            helper = new MAMADbHelper(this);
            db = helper.getReadableDatabase();
            db.update (MAMAContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        }

        // Display the tags
        final TextView tagView = (TextView) findViewById(R.id.tagView);
        tagView.setMovementMethod(LinkMovementMethod.getInstance());
        tagView.setText(allTags);
        c.close();
        db.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);
    }

    /**
     * Hide keyboard automatically when pointing to somewhere else
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager =
                    (InputMethodManager) this.getSystemService(ResultActivity.this.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
