package il.co.anyway.app.filters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import il.co.anyway.app.R;



abstract class BinaryUriQueryParamAppenderBase extends UriQueryParamAppender {

    private static final int MSB = 2;
    private static final int LSB = 1;


    public BinaryUriQueryParamAppenderBase(Context context) {
        super(context);
    }

    @Override
    public void appendQueryParameter(Uri.Builder uriBuilder) {
        int urbanRoad =
                mSharedPrefs.getBoolean(getString(getMsbPerfKey()), true) ?
                        MSB : 0;
        int highway =
                mSharedPrefs.getBoolean(getString(getLsbPerfKey()), true) ?
                        LSB : 0;

        uriBuilder.appendQueryParameter(getQueryParamKey(), Integer.toString(urbanRoad | highway));
    }

    protected abstract String getQueryParamKey();

    protected abstract int getLsbPerfKey();

    protected abstract int getMsbPerfKey();
}
