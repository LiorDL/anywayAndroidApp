package il.co.anyway.app.filters;

import android.content.Context;

import java.util.Collections;
import java.util.Map;

import il.co.anyway.app.R;



public class InaccuracyUriQueryParamAppender extends BooleansUriQueryParamAppender {

    InaccuracyUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    protected Map<Integer, String> getKeysToParamNames() {
        return Collections.singletonMap(R.string.pref_accidents_inaccurate_key, "show_inaccurate");
    }
}
