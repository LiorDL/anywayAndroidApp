package il.co.anyway.app.filters;

import android.content.Context;

import il.co.anyway.app.R;



public class NumWaysUriQueryParamAppender extends BinaryUriQueryParamAppenderBase {

    public NumWaysUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    protected String getQueryParamKey() {
        return "show_lane";
    }

    @Override
    protected int getLsbPerfKey() {
        return R.string.pref_one_way_key;
    }

    @Override
    protected int getMsbPerfKey() {
        return R.string.pref_two_way_key;
    }
}
