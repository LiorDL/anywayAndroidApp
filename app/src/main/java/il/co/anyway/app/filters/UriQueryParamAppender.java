package il.co.anyway.app.filters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;



public abstract class UriQueryParamAppender {

    protected final SharedPreferences mSharedPrefs;
    protected final Context mContext;

    protected UriQueryParamAppender(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public abstract void appendQueryParameter(Uri.Builder uriBuilder);

    protected String getString(int id) {
        return mContext.getString(id);
    }
}
