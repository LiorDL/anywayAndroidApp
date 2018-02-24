package il.co.anyway.app.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import il.co.anyway.app.DisqusActivity;
import il.co.anyway.app.R;
import il.co.anyway.app.StreetViewActivity;

public class PointDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = PointDialog.class.getSimpleName();

    private static final String ARGS_LOCATION = "args_location";
    private LatLng mLocation;

    public static PointDialog newInstance(LatLng location) {
        PointDialog dialog = new PointDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_LOCATION, location);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_point, container, false);
        TextView textViewTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        ImageView buttonClose = (ImageView) view.findViewById(R.id.iv_close_button);
        TextView buttonDiscussion = (TextView) view.findViewById(R.id.tv_start_new_discussion);
        TextView buttonStreetView = (TextView) view.findViewById(R.id.tv_button_street_view);
        textViewTitle.setText(R.string.confirm_opening_new_discussion);
        buttonDiscussion.setText(R.string.start_discussion);
        buttonStreetView.setText(R.string.google_street_view);

        mLocation = getArguments().getParcelable(ARGS_LOCATION);

        buttonClose.setOnClickListener(this);
        buttonDiscussion.setOnClickListener(this);
        buttonStreetView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_button:
                dismiss();
                break;
            case R.id.tv_start_new_discussion:
                String identifier = "(" + mLocation.latitude + ", " + mLocation.longitude + ")";
                Intent disqusIntent = new Intent(getActivity(), DisqusActivity.class);
                disqusIntent.putExtra(DisqusActivity.DISQUS_TALK_IDENTIFIER, identifier);
                disqusIntent.putExtra(DisqusActivity.DISQUS_LOCATION, mLocation);
                disqusIntent.putExtra(DisqusActivity.DISQUS_NEW, true);
                getActivity().startActivity(disqusIntent);
                break;
            case R.id.tv_button_street_view:
                Intent intent = new Intent(getActivity(), StreetViewActivity.class);
                intent.putExtra("lat", mLocation.latitude);
                intent.putExtra("lng", mLocation.longitude);
                getActivity().startActivity(intent);
                break;
        }
        dismiss();
    }

    /*
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        final LatLng latLng = (LatLng) args.get("location");
        // confirm user want to create new discussion,
        // if confirmed - create new discussion by HTTP POST request to Anyway and open discussion
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_opening_new_discussion)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        String identifier = "(" + latLng.latitude + ", " + latLng.longitude + ")";
                        Intent disqusIntent = new Intent(getActivity(), DisqusActivity.class);
                        disqusIntent.putExtra(DisqusActivity.DISQUS_TALK_IDENTIFIER, identifier);
                        disqusIntent.putExtra(DisqusActivity.DISQUS_LOCATION, latLng);
                        disqusIntent.putExtra(DisqusActivity.DISQUS_NEW, true);

                        getActivity().startActivity(disqusIntent);
                    }
                })
                .setNegativeButton("Street View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), StreetViewActivity.class);
                        intent.putExtra("lat", latLng.latitude);
                        intent.putExtra("lng", latLng.longitude);
                        getActivity().startActivity(intent);
                    }
                })
                .setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
    */
}
