package il.co.anyway.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.util.ArrayList;
import java.util.List;

import il.co.anyway.app.models.Accident;
import il.co.anyway.app.singletons.MarkersManager;
import il.co.anyway.app.singletons.OnNewAccidentListener;



public class StreetViewActivity extends FragmentActivity implements
        OnStreetViewPanoramaReadyCallback,
        StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener,
        StreetViewPanorama.OnStreetViewPanoramaChangeListener,
        OnNewAccidentListener {

    private static final double LAT_OFFSET = 0.001;
    private static final double LNG_OFFSET = 0.001;
    private static final int NOT_IN_VIEW = -1;
    private static final double HEIGHT_OF_STREET_VIEW_CAMERA = 2.5;
    private static final double HORIZONTAL_FIELD_OF_VIEW = 90.0;
    private static final double HORIZONTAL_FIELD_OF_VIEW_RADIANS = Math.toRadians(HORIZONTAL_FIELD_OF_VIEW);
    public static final int IMAGE_SIZE_FACTOR = 4000;
    public static final int MIN_DISTANCE_TO_FACTOR = 20;
    private LatLng mLatLng;
    private double mBearing;

    private List<ImageView> mImageViews = new ArrayList<>();
    private float mTilt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_activity);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        double lat = getIntent().getDoubleExtra("lat", 32.065953);
        double lng = getIntent().getDoubleExtra("lng", 34.775512);
        mLatLng = new LatLng(lat, lng);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        MarkersManager.getInstance().unregisterAccidentListener(this);

        super.onStop();
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(mLatLng);
        streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(this);

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(this);

        streetViewPanorama.setZoomGesturesEnabled(false);
    }

    /**
     * Invoked when tilt (elevation) or bearing (azimuth) are changed
     *
     * @param streetViewPanoramaCamera
     */
    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        removeAllImages();

        mBearing = streetViewPanoramaCamera.bearing;
        mTilt = streetViewPanoramaCamera.tilt;

        for (Accident accident : MarkersManager.getInstance().getAllAccidents()) {
            possiblyShowMarker(accident);
        }

    }

    /**
     * Invoked when the viewers location changes
     *
     * @param streetViewPanoramaLocation
     */
    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
        removeAllImages();

        // Clear all markers

        mLatLng = streetViewPanoramaLocation.position;

        MarkersManager.getInstance().registerNewAccidentListener(this);

        Utility.getMarkersByParameters(
                getLatLongBounds(streetViewPanoramaLocation.position),
                17,
                this,
                true);
    }

    private void removeAllImages() {
        ViewGroup rootView = getRootView();
        for (ImageView imageView : mImageViews) {
            rootView.removeView(imageView);
        }
    }

    @Override
    public void onNewAccident(Accident a) {
        possiblyShowMarker(a);
    }

    private void possiblyShowMarker(Accident a) {
        ViewGroup rootView = getRootView();
        float distance = Utility.getDistanceBetweenTwoLocations(mLatLng, a.getLocation());

        double leftPlacement = getHorizontalOffset(rootView, a);
        if (leftPlacement == NOT_IN_VIEW) {
            return;
        }

        double topPlacement = getVerticalOffset(distance, rootView);

        if (topPlacement == NOT_IN_VIEW) {
            return;
        }

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setFocusable(false);
        imageView.setClickable(false);
        imageView.setImageResource(Utility.getIconForMarker(a.getSeverity(), a.getSubType()));

        int imageSize = getImageSize(distance);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageSize, imageSize );
        layoutParams.leftMargin = (int) leftPlacement;
        layoutParams.topMargin = (int) topPlacement;

        imageView.setLayoutParams(layoutParams);
        mImageViews.add(imageView);
        rootView.addView(imageView);
    }

    private ViewGroup getRootView() {
        return (ViewGroup) findViewById(R.id.street_root);
    }

    private static LatLngBounds getLatLongBounds(LatLng latLng) {
        return new LatLngBounds(
                new LatLng(
                        latLng.latitude - LAT_OFFSET,
                        latLng.longitude - LNG_OFFSET),
                new LatLng(
                        latLng.latitude + LAT_OFFSET,
                        latLng.longitude + LNG_OFFSET));
    }

    private int getImageSize(float distance) {
        distance = Math.max(distance, MIN_DISTANCE_TO_FACTOR);

        return (int) (IMAGE_SIZE_FACTOR / distance);
    }

    private double getVerticalOffset(double distanceMeters, View rootView) {
        double realAngleRadians = Math.atan(distanceMeters / HEIGHT_OF_STREET_VIEW_CAMERA);
        double realAngleAgainstCenterRadiansTowardsDown = (Math.PI / 2.0) - realAngleRadians;
        double verticalFieldOfViewRadians = verticalFieldOfView(rootView);
        double tiltRadians = Math.toRadians(mTilt);
        if (Math.abs(tiltRadians - realAngleAgainstCenterRadiansTowardsDown) > verticalFieldOfViewRadians / 2.0) {
            return NOT_IN_VIEW;
        }

        return rootView.getHeight() * ((tiltRadians + (0.5 * verticalFieldOfViewRadians) + realAngleAgainstCenterRadiansTowardsDown) / verticalFieldOfViewRadians);
    }

    private double getHorizontalOffset(View rootView, Accident a) {
        double bearingToAccident = Utility.getBearingBetweenTwoLocations(mLatLng, a.getLocation());

        double rotation = (360 - mBearing + (HORIZONTAL_FIELD_OF_VIEW / 2)) % 360;

        double rotatedBearingToAccident = (bearingToAccident + rotation) % 360;

        if (rotatedBearingToAccident > HORIZONTAL_FIELD_OF_VIEW) {
            return NOT_IN_VIEW;
        }

        double leftPlacementRatio = rotatedBearingToAccident / HORIZONTAL_FIELD_OF_VIEW;
        return leftPlacementRatio * rootView.getWidth();
    }

    private double verticalFieldOfView(View rootView) {
        double height = rootView.getHeight();
        double width = rootView.getWidth();
        return (height / width) * HORIZONTAL_FIELD_OF_VIEW_RADIANS;
    }
}
