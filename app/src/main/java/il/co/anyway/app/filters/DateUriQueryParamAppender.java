package il.co.anyway.app.filters;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import il.co.anyway.app.R;



public class DateUriQueryParamAppender extends UriQueryParamAppender {

    protected DateUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    public void appendQueryParameter(Uri.Builder uriBuilder) {
        String fromDate = mSharedPrefs.getString(mContext.getString(R.string.pref_from_date_key), mContext.getString(R.string.pref_default_from_date));
        String toDate = mSharedPrefs.getString(mContext.getString(R.string.pref_to_date_key), mContext.getString(R.string.pref_default_to_date));

        uriBuilder
                .appendQueryParameter("start_date", fromDate)
                .appendQueryParameter("end_date", toDate);
    }
}
