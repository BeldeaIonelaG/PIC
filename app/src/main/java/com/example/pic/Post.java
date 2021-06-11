package com.example.pic;

public class Post {
    private String mQuote;
    private String mImageUrl;
    private String mUser;

    public Post() {} //empty constructor needed

    public Post(String quote, String imageUrl, String username)
    {
        mUser = username;
        mQuote = quote;
        mImageUrl = imageUrl;
    }

    public String getmQuote() {
        return mQuote;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmUser() {
        return mUser;
    }

    public void setmQuote(String mQuote) {
        this.mQuote = mQuote;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setmUser(String mUser) {
        this.mUser = mUser;
    }
}
