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

import android.provider.BaseColumns;

/**
 * Class: MAMA Contract
 * Macro Def for local database
 */
public final class MAMAContract {
    public MAMAContract(){}

    /**
     * Inner class: Macro Def for table "itementry"
     */
    public static abstract class ItemEntry implements BaseColumns{
        public static final String TABLE_NAME = "itementry";
        public static final String COLUMN_NAME_ITEMATID = "itematid";
        public static final String COLUMN_NAME_TAGS = "tags";
        public static final int TAG_COLUMN_NUMBER = 1;

        public static final String[] ALL_COLUMS = { MAMAContract.ItemEntry.COLUMN_NAME_ITEMATID,
                MAMAContract.ItemEntry.COLUMN_NAME_TAGS};

    }
}
