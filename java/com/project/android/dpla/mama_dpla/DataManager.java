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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Class: DataManager
 * Singleton class for interacting with DPLA as well as local db
 */

public class DataManager {

    /* Variable Declarations */

    private static DataManager sInstance_ = null;
    // Tag for log files
    private static final String TAG = "MAMA_DPLA/DataManager";
    // JSON node names
    private static final String TAG_DOCS = "docs";
    private static final String TAG_TITLE = "sourceResource.title";
    private static final String TAG_URL = "isShownAt";
    private static final String TAG_PROVIDER = "dataProvider";
    private static final String TAG_COLLECTION = "sourceResource.collection.title";
    private static final String TAG_ID = "@id";
    private static final String TAG_TYPE = "sourceResource.type";
    private static final String APIKEY = "606d14833afd79665413f72a981fe811";

    public String error_code =  ReturnStatus.SUCCESS ;
    private ArrayList<DPLAItem> mItems_ = null;
    public JSONArray docs = null;


    /* Member Function Definitions */

    protected DataManager() {
        // to defeat instantiation
    }

    public static DataManager getInstance() {
        if (sInstance_ == null) {
            sInstance_ = new DataManager();
        }
        return sInstance_;
    }

    /**
     * Fill in variable mItems_ with item info from DPLA and tags info from local db
     * @param keyword : User-typed searching keyword
     * @param activityContext : context of the activity that calls this function
     * @return error_code : the reason of exceptions. If successfully fetch data, return ReturnStatus.Success
     */
    public String getSearchResultsWithTags (String keyword, Context activityContext) {
        String returnStatus = doSearch(keyword, activityContext);
        if (returnStatus == ReturnStatus.SUCCESS)
            updateResultsWithTags(activityContext);
        return returnStatus;
    }

    /**
     * Complete variable mItems_ with tags by accessing local db
     * Called by getSearchResultsWithTags (String keyword, Context activityContext)
     * @param activityContext : context of the activity that calls this function
     */
    public void updateResultsWithTags (Context activityContext) {
        int items_len = mItems_.size();

        // Open database for read
        MAMADbHelper helper = new MAMADbHelper(activityContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        // iterate mItems_ arraylist to fill each item
        for (int iter = 0; iter < items_len; iter++) {
            DPLAItem curr_item = mItems_.get(iter);
            String selection = MAMAContract.ItemEntry.COLUMN_NAME_ITEMATID + " = ?";
            String[] selectionArgs = {curr_item.getId()};
            Cursor cur = db.query(
                    MAMAContract.ItemEntry.TABLE_NAME,
                    MAMAContract.ItemEntry.ALL_COLUMS,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cur.getCount() == 0) { // If there are no tags, set null
                curr_item.setTags(null);
            }
            else{
                cur.moveToFirst();
                curr_item.setTags(cur.getString(MAMAContract.ItemEntry.TAG_COLUMN_NUMBER));
                Log.d(TAG,curr_item.getTags());
            }
        }
    } // end of updateResultsWithTags()

    /**
     * Perform the search using DPLA api based on a keyword and store results in mItems_
     * Called by getSearchResultsWithTags (String keyword, Context activityContext)
     * @param keyword : User-typed searching keyword
     * @param activityContext : context of the activity that calls this function
     * @return error_code : the reason of exceptions. If successfully fetch data, return ReturnStatus.Success
     */
    private String doSearch(String keyword, Context activityContext) {
        error_code =  ReturnStatus.SUCCESS ;
        try {
            // Format Searching URL
            String SERVER_URL = String.format("http://api.dp.la/v2/items?q=%s&%s", keyword,
                    "&fields=sourceResource.title,@id,dataProvider,sourceResource.collection.title," +
                    "isShownAt,sourceResource.type&api_key=" + APIKEY);
            // Request with POST method and check the status code
            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet(SERVER_URL);
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            //Clear mItems_
            mItems_ = null;

            // Fetch and decode data
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                Log.v(TAG, "success!");  // Success tag for successful connection to the dpla
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String contentString;
                while ((contentString = streamReader.readLine()) != null)
                    responseStrBuilder.append(contentString);
                String json_string = responseStrBuilder.toString();
                if (json_string != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(json_string);
                        // Getting JSON Array node
                        docs = jsonObj.getJSONArray(TAG_DOCS);
                        mItems_ = new ArrayList<DPLAItem>();
                        // Iterate on docs, Parsing JSON objects one by one
                        for (int iter = 0; iter < docs.length(); iter++) {
                            JSONObject dplaFromWeb = docs.getJSONObject(iter);
                            if (dplaFromWeb.has(TAG_URL) && dplaFromWeb.has(TAG_ID)) { // NOTE: we do not handle DPLA items which do not have a url or @id
                                String collection_title = null;
                                if (dplaFromWeb.has(TAG_COLLECTION)) { // stripe all special symbols in collection title
                                    collection_title = dplaFromWeb.getString(TAG_COLLECTION).replaceAll("[^\\w\\s]","");
                                }
                                String item_title = dplaFromWeb.getString(TAG_TITLE).replaceAll("[^\\w\\s]","");
                                String item_type=null;
                                if (dplaFromWeb.has(TAG_TYPE)) {
                                    item_type = dplaFromWeb.getString(TAG_TYPE).replaceAll("[^\\w\\s]", ""); //sourceResource.type
                                }
                               //Considering 'data provider' is not NULL as mentioned by DPLA
                                String item_data_provider = dplaFromWeb.getString(TAG_PROVIDER).replaceAll("[^\\w\\s]","");
                                DPLAItem di = new DPLAItem(dplaFromWeb.getString(TAG_ID), item_title, item_data_provider,
                                        dplaFromWeb.getString(TAG_URL), collection_title,item_type);
                                mItems_.add(di);
                            }
                        }
                    } catch (JSONException json_exp) {
                        error_code =  ReturnStatus.JSONFAIL ;
                    }
                }
            } else {
                Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
            }
        } catch (UnknownHostException ex) {
            error_code =  ReturnStatus.HTTPFAIL ;
        } catch (UnsupportedEncodingException ex) {
            error_code =  ReturnStatus.HTTPFAIL ;
        } catch (ClientProtocolException ex) {
            error_code =  ReturnStatus.HTTPFAIL ;
        } catch (IOException ex) {
            error_code =  ReturnStatus.HTTPFAIL ;
        }
        return error_code;
    }


    public ArrayList<DPLAItem> getSearchResults() {
        return mItems_; // Returning the previously searched results here
    }
}
