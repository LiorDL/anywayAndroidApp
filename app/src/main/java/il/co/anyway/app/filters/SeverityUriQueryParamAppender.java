package il.co.anyway.app.filters;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import il.co.anyway.app.R;



public class SeverityUriQueryParamAppender extends BooleansUriQueryParamAppender {

    protected SeverityUriQueryParamAppender(Context context) {
        super(context);
    }

    @Override
    protected Map<Integer, String> getKeysToParamNames() {
        Map<Integer, String> result = new HashMap<>();
        result.put(R.string.pref_accidents_severe_key, "show_severe");
        result.put(R.string.pref_accidents_fatal_key, "show_fatal");
        result.put(R.string.pref_accidents_light_key, "show_light");

        return result;
    }
}
