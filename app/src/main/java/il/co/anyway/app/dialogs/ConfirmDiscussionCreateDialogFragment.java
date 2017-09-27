package il.co.anyway.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import il.co.anyway.app.DisqusActivity;
import il.co.anyway.app.R;
import il.co.anyway.app.StreetViewActivity;


public class ConfirmDiscussionCreateDialogFragment extends DialogFragment {

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

}
