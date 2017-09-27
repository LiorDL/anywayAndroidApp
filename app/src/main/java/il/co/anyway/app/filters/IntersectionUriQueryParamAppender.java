package il.co.anyway.app.filters;

import android.content.Context;
import android.net.Uri;

import il.co.anyway.app.R;
import il.co.anyway.app.filters.UriQueryParamAppender;



public class IntersectionUriQueryParamAppender extends BinaryUriQueryParamAppenderBase {

    public IntersectionUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    protected String getQueryParamKey() {
        return "show_intersection";
    }

    @Override
    protected int getLsbPerfKey() {
        return R.string.pref_non_intersection_key;
    }

    @Override
    protected int getMsbPerfKey() {
        return R.string.pref_intersection_key;
    }
}
