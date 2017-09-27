package il.co.anyway.app.filters;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;



public class FiltersRepository {
    private static List<UriQueryParamAppender> sFilters;

    public static synchronized Iterable<UriQueryParamAppender> getFilters(Context context) {
        sFilters = new ArrayList<>();
        sFilters.add(new UrbanicityUriQueryParamAppender(context));
        sFilters.add(new IntersectionUriQueryParamAppender(context));
        sFilters.add(new NumWaysUriQueryParamAppender(context));
        sFilters.add(new ShowDayUriQueryParamAppender(context));
        sFilters.add(new SeverityUriQueryParamAppender(context));
        sFilters.add(new InaccuracyUriQueryParamAppender(context));
        sFilters.add(new DateUriQueryParamAppender(context));

        return sFilters;
    }
}
