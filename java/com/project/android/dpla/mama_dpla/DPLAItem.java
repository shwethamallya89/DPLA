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

public class DPLAItem {

    /* Variable Declarations*/
    private String mTitle_; // item title
    private String mDataProvider_; // data provider
    private String mId_; // @id
    private String mUrl_; // is_shown_at
    private String mCollection_; // collection title
    private String mTags_; // tag
    private String mType_; // item type

    /* Member Method Definitions */
    public String getTitle(){return mTitle_;}

    public String getDataProvider(){return mDataProvider_;}

    public String getUrl() {return mUrl_;}

    public String getCollection() {return mCollection_;}

    public String getId() {return this.mId_;}

    public String getType() {return this.mType_;}

    public String getTags() {return this.mTags_;}

    public void setTags(String tags) {this.mTags_ = tags;}


    public DPLAItem(String id, String title,  String dataProvider, String url, String collection, String type)
    {
        this.mId_ = id;
        this.mTitle_ = title;
        this.mDataProvider_ = dataProvider;
        this.mUrl_ = url;
        this.mCollection_ = collection;
        this.mType_=type;

    }


}
