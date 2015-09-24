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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.mama_dpla.R;


import java.util.ArrayList;

/**
 * Class: StartActivity
 * First Screen of the App
 */

public class StartActivity extends ActionBarActivity {

    /* Variable declerations */

    private static final String TAG = "MAMA_DPLA";
    private String mKeyword_ = null ;
    private ListView mListView;
    private ArrayList<DPLAItem> mItems_; // this should be a member variable because of the Listview/Arrayadapter interaction
    private Intent mIntent;
    private static ArrayAdapter<DPLAItem> sAdapter = null;

    /**
     * Called when launching the screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mIntent = new Intent(this, ResultActivity.class);
    }

    /**
     * Response Method of button "Search"
     * @param view
     */
    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        mKeyword_ = editText.getText().toString().trim();
        if (mKeyword_.length() == 0) {
            CharSequence no_text = "No keywords entered";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(StartActivity.this, no_text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            hideKeyboard();
            /*
		    * Notes:
		    * We cannot connect to the Internet in the main thread
		    * Android prohibits us from performing any time-consuming tasks in the main thread
		    * Instead, using a nested class called GetFetcher. See definition below
		    */
            GetFetcher fetcher = new GetFetcher();
            fetcher.execute(); // exceptions are handled in getSearchResults() in DataManager

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    /**
     * Refresh mListView's data
     */
    @Override
    public void onRestart()
    {  // After a pause OR at startup
        super.onRestart();
        sAdapter.notifyDataSetChanged();
    }

    /**
     * Hide keyboard automatically when pointing to somewhere else
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(StartActivity.this.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Clear mListView's data
     */
    public void clearActivity(View view){
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setText("");
        sAdapter.clear();
    }



    /**
     * First Inner class: GetFetcher
     * Fetch DPLA data in background thread
     */
    private class GetFetcher extends AsyncTask<Void, Void, String> {
        /**
         * Fill static variable mItems_ in DataManager with info from DPLA and tags from local DB
         * @return returnStatus
         */
        protected String doInBackground(Void... params) {
            DataManager manager = DataManager.getInstance();
            String returnStatus = manager.getSearchResultsWithTags(mKeyword_,StartActivity.this) ;
            return returnStatus;
        }

        /**
         * Display searching results in mListView
         * Display error message in toast
         * @param result : returnStatus returned by doInBackground
         */
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals(ReturnStatus.SUCCESS)) { // If doInBackground() performs search successfully, display searching results in mListView
                mListView = (ListView) findViewById(R.id.search_list);
                DataManager manager = DataManager.getInstance();
                mItems_ = manager.getSearchResults(); // This is here since mItems_ is valid only when the return value is SUCCESS
                 /* Define a new Adapter
                  * Parameters - Context,Layout for the row,the Array of data */

                sAdapter = new ArrayAdapter<DPLAItem>(StartActivity.this,
                        android.R.layout.simple_list_item_2, android.R.id.text1, mItems_) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        DPLAItem   curr_item = mItems_.get(position);
                        String provider = curr_item.getDataProvider();
                        String tags = "No existing tags";
                        String type = "Type not available";
                        if (curr_item.getTags() != null) tags = curr_item.getTags();
                        if (curr_item.getType()!= null)  type = curr_item.getType();
                        String sub_list_title = provider + "\n" + "Type: " + type+ "\n" + "Tags: " + tags;
                        text1.setText(curr_item.getTitle());
                        text2.setText(sub_list_title);
                        return view;
                    }
                };
                mListView.setAdapter(sAdapter);
                mListView.setOnItemClickListener(new OnItemClickListenerImpl());
            }
            else{ // If doInBackground() failed to performs searching, display error message in toast
                CharSequence text;
                if (result.equals(ReturnStatus.HTTPFAIL)) text = "Unable to access Internet!";
                else text = "Database Error";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(StartActivity.this, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    /**
     * Second Inner Class: OnItemClickListenerImpl
     * Pass item's position in mListView to ResultActivity
     */
    class OnItemClickListenerImpl implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // position and id are same. Both correspond to the location on the listview
            //      we will be using 'position' and passing it through intent
            mIntent.putExtra("position",position);
            startActivity(mIntent);
        }
    }
}

