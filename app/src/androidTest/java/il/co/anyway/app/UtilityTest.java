package il.co.anyway.app;


import android.content.Context;
import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import il.co.anyway.app.models.Accident;
import il.co.anyway.app.models.Discussion;

public class UtilityTest extends AndroidTestCase {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = UtilityTest.class.getSimpleName();

    public void testJsonParsing() {

        // check if one accident added
        String jsonToTest = "{\"markers\": [{\"address\": \"\", \"created\": \"2013-12-05T07:00:00\", \"description\": \"\", \"followers\": [], \"following\": null, \"id\": \"32013054220\", \"latitude\": 32.0829391544824, \"locationAccuracy\": 1, \"longitude\": 34.8021309172237, \"severity\": 3, \"subtype\": 3, \"title\": \"Accident\", \"type\": 1, \"user\": \"\"}]}";
        int status = -10;

        List<Accident> accidents = new ArrayList<>();
        List<Discussion> discussions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonToTest);
            status = Utility.getMarkersDataFromJson(jsonObject, accidents, discussions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(1, accidents.size());
        assertEquals(0, discussions.size());
        assertEquals(status, 0);

        // check if two accidents added
        jsonToTest = "{\"markers\": [{\"address\": \"\", \"created\": \"2013-12-05T07:00:00\", \"description\": \"\", \"followers\": [], \"following\": null, \"id\": \"32013054220\", \"latitude\": 32.0829391544824, \"locationAccuracy\": 1, \"longitude\": 34.8021309172237, \"severity\": 3, \"subtype\": 3, \"title\": \"Accident\", \"type\": 1, \"user\": \"\"},{\"address\": \"\", \"created\": \"2013-12-05T07:00:00\", \"description\": \"\", \"followers\": [], \"following\": null, \"id\": \"32013054220\", \"latitude\": 32.0829391544824, \"locationAccuracy\": 1, \"longitude\": 34.8021309172237, \"severity\": 3, \"subtype\": 3, \"title\": \"Accident\", \"type\": 1, \"user\": \"\"}]}";
        accidents = new ArrayList<>();
        discussions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonToTest);
            status = Utility.getMarkersDataFromJson(jsonObject, accidents, discussions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(2, accidents.size());
        assertEquals(0, discussions.size());
        assertEquals(status, 0);
    }

    public void testTimeStampCreation() {

        // Device need to be in Jerusalem Time zone for this test to pass
        String ts = Utility.getTimeStamp("01/01/2013");
        assertEquals("1356991200", ts);

    }

    public void testClusterImageByCountOfAccidents() {

        int count = 5;
        int image = Utility.getClusterImageByCountOfAccidents(count);
        assertEquals(R.drawable.m1, image);

        count = 14;
        image = Utility.getClusterImageByCountOfAccidents(count);
        assertEquals(R.drawable.m2, image);

        count = 452;
        image = Utility.getClusterImageByCountOfAccidents(count);
        assertEquals(R.drawable.m3, image);

        count = 17823;
        image = Utility.getClusterImageByCountOfAccidents(count);
        assertEquals(R.drawable.m4, image);

    }

    public void testAccidentTypeByIndex()
    {
        String a = Utility.getAccidentTypeByIndex(
                Accident.ACCIDENT_TYPE_CAR_TO_PEDESTRIAN,
                getContext()
        );

        assertEquals(a, getContext().getString(R.string.str_ACCIDENT_TYPE_CAR_TO_PEDESTRIAN));
    }

    public void testIconForMarker() {

        int icon = Utility.getIconForMarker(
                Accident.SEVERITY_FATAL,
                Accident.ACCIDENT_TYPE_CAR_TO_PEDESTRIAN
        );
        assertEquals(icon, R.drawable.vehicle_person_lethal);
    }

    public void testCurrentPositionStringURI() {

        LatLng latLng = new LatLng(33.21, 34.65);
        String url = Utility.getCurrentPositionStringURI(latLng, 15, getContext());

        assertEquals("http://www.anyway.co.il/?start_date=2013-1-1&end_date=2013-12-31&show_fatal=1&show_severe=1&show_light=1&show_inaccurate=0&zoom=15&lat=33.21&lon=34.65", url);

    }
}
