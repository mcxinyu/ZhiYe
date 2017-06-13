package com.about.zhiye.data;

import android.os.Parcel;

/**
 * Created by huangyuefeng on 2017/6/6.
 * Contact me : mcxinyu@foxmail.com
 */
public class SearchNewsSuggestion implements com.arlib.floatingsearchview.suggestions.model.SearchSuggestion {

    private String mTitle;
    private boolean mIsHistory;

    public SearchNewsSuggestion(String suggestion) {
        this.mTitle = suggestion;
    }

    public SearchNewsSuggestion(Parcel source) {
        this.mTitle = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public boolean isHistory() {
        return mIsHistory;
    }

    public void setIsHistory(boolean history) {
        mIsHistory = history;
    }

    @Override
    public String getBody() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeInt(mIsHistory ? 1 : 0);
    }

    public static final Creator<SearchNewsSuggestion> CREATOR = new Creator<SearchNewsSuggestion>() {
        @Override
        public SearchNewsSuggestion createFromParcel(Parcel source) {
            return new SearchNewsSuggestion(source);
        }

        @Override
        public SearchNewsSuggestion[] newArray(int size) {
            return new SearchNewsSuggestion[size];
        }
    };
}
