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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class: MAMADbHelper
 * Database helper class
 * Create database in onCreate
 */
public class MAMADbHelper extends SQLiteOpenHelper{

    /* Variable Declarations */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MAMA.db";
    private static final String TINY_TEXT_TYPE = " TINYTEXT";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MAMAContract.ItemEntry.TABLE_NAME + " ("
            + MAMAContract.ItemEntry._ID + " INTEGER AUTO_INCREMENT PRIMARY KEY, "
            + MAMAContract.ItemEntry.COLUMN_NAME_ITEMATID + TINY_TEXT_TYPE + COMMA_SEP
            + MAMAContract.ItemEntry.COLUMN_NAME_TAGS + TEXT_TYPE + "); ";


    public MAMADbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        System.err.println(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
       // For future db updates
    }
}
