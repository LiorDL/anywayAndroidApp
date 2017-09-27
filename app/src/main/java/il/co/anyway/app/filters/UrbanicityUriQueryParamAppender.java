package il.co.anyway.app.filters;

import android.content.Context;
import android.net.Uri;

import il.co.anyway.app.R;



public class UrbanicityUriQueryParamAppender extends BinaryUriQueryParamAppenderBase {

    public UrbanicityUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    protected String getQueryParamKey() {
        return "show_urban";
    }

    @Override
    protected int getLsbPerfKey() {
        return R.string.pref_highway_key;
    }

    @Override
    protected int getMsbPerfKey() {
        return R.string.pref_urban_road_key;
    }
}
