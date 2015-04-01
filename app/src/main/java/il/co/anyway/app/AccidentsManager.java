package il.co.anyway.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AccidentsManager {

    public static final boolean DO_RESET = true;
    public static final boolean DO_NOT_RESET = false;

    private final String LOG_TAG = AccidentsManager.class.getSimpleName();

    private static AccidentsManager instance = null;
    private List<Accident> accidentsList;
    private List<AccidentsListSameLatLng> mMultipleAccidentsList;

    // making the default constructor private make sure there will be only one instance of the accidents manager
    private AccidentsManager() {
        accidentsList = new ArrayList<>();
        mMultipleAccidentsList = new ArrayList<>();
    }

    public static AccidentsManager getInstance() {
        if (instance == null)
            instance = new AccidentsManager();
        return instance;
    }

    /**
     * check if an accident exist in the accident manager
     *
     * @param toCheck the Accident object to check
     * @return true if exist, false if not or toCheck is null
     */
    private boolean isAccidentExist(Accident toCheck) {

        if (toCheck == null)
            return false;

        // check in regular list
        for (Accident a : accidentsList)
            if (a.getId() == toCheck.getId()) {
                return true;
            }

        // check in the multiple list
        for (AccidentsListSameLatLng accList: mMultipleAccidentsList) {
            for (Accident a : accList.getAccidentList()) {
                if (a.getId() == toCheck.getId()) {
                    return true;
                }
            }
        }

        return false;
    }

    private AccidentsListSameLatLng isThereAnotherAccidentInThisLocation(Accident toCheck) {

        // check for existing multiple list
        for (AccidentsListSameLatLng accList: mMultipleAccidentsList) {
            if (toCheck.getLocation().latitude == accList.getLatitude() &&
                    toCheck.getLocation().longitude == accList.getLongitude()) {

                return accList;
            }
        }

        // check the accident in the accidents list
        for (Accident a : accidentsList) {
            if (toCheck.getLocation().latitude == a.getLocation().latitude &&
                    toCheck.getLocation().longitude == a.getLocation().longitude) {

                AccidentsListSameLatLng accList = new AccidentsListSameLatLng(a.getLocation().latitude, a.getLocation().longitude);
                accList.addAccidentToList(a);
                mMultipleAccidentsList.add(accList);

                boolean removed = accidentsList.remove(a);
                if (!removed)
                    Log.e(LOG_TAG, "failed to remove accident from accident list when creating multiple list");

                return accList;
            }
        }

        return null;
    }

    /**
     * Add accident to the list
     *
     * @param toAdd the Accident object to add
     * @return true if accident added, false if accident already exist, or toAdd is null
     */
    public boolean addAccident(Accident toAdd) {

        if (toAdd == null)
            return false;

        if (!isAccidentExist(toAdd)) {

            AccidentsListSameLatLng accList = isThereAnotherAccidentInThisLocation(toAdd);
            if(accList == null) {
                accidentsList.add(toAdd);
            }
            else {
                accList.addAccidentToList(toAdd);
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Add a list of accidents to the list
     *
     * @param toAddList the list of Accident objects
     * @param reset     use to select if you want to reset the list before adding the new list
     * @return How many accidents from the list actually taken(duplicate accident will ignore)
     */
    public int addAllAccidents(List<Accident> toAddList, boolean reset) {

        if (reset == DO_RESET)
            accidentsList.clear();

        if (toAddList == null)
            return 0;

        int counter = 0;

        for (Accident a : toAddList) {
            if (addAccident(a)) {
                counter++;
            }
        }


        Log.i(LOG_TAG, counter + " accidents of " + toAddList.size() + " added");
        return counter;
    }

    /**
     * find a accident by it's marker ID
     *
     * @param markerID the marker id
     * @return The Accident object if found, null if not found
     */
    public Accident getAccidentByMarkerID(String markerID) {

        if (markerID == null)
            return null;

        for (Accident a : accidentsList) {
            if (a.getMarkerID().equals(markerID)) {
                return a;
            }
        }

        for (AccidentsListSameLatLng accList : mMultipleAccidentsList) {
            for (Accident a : accList.getAccidentList()) {
                if (a.getMarkerID().equals(markerID)) {
                    return a;
                }
            }
        }

        return null;
    }

    /**
     * @return the list of all accidents in the list
     */
    public List<Accident> getAllAccidents() {
        return accidentsList;
    }

    /**
     * Get all the accidents that not on the map
     *
     * @return a list of accidents that not on the map
     */
    public List<Accident> getAllNewAccidents() {

        List<Accident> newAccidents = new ArrayList<>();

        for (Accident a : accidentsList) {
            if (a.getMarkerID() == null) {
                newAccidents.add(a);
            }
        }

        return newAccidents;
    }

    public List<AccidentsListSameLatLng> getAllAccidentsList() {

        return mMultipleAccidentsList;
    }

    /**
     * set all accident's marker id to null
     */
    public void clearMarkersIDs() {
        for (Accident a : accidentsList)
            a.setMarkerID(null);

        for (AccidentsListSameLatLng accList : mMultipleAccidentsList)
            for (Accident a : accList.getAccidentList())
                a.setMarkerID(null);
    }

    public List<AccidentsListSameLatLng> getmMultipleAccidentsList() {
        return mMultipleAccidentsList;
    }
}
