package il.co.anyway.app.filters;

import android.content.Context;
import android.net.Uri;

import java.util.Map;

import il.co.anyway.app.R;



abstract class BooleansUriQueryParamAppender extends UriQueryParamAppender {
    protected BooleansUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    public void appendQueryParameter(Uri.Builder uriBuilder) {
        for (Map.Entry<Integer, String> keyToParamName : getKeysToParamNames().entrySet()) {
            Boolean show_inaccurate = mSharedPrefs.getBoolean(mContext.getString(keyToParamName.getKey()), true);
            uriBuilder.appendQueryParameter(keyToParamName.getValue(), show_inaccurate ? "1" : "");
        }

    }

    protected abstract Map<Integer, String> getKeysToParamNames();
}
