package il.co.anyway.app.singletons;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import il.co.anyway.app.models.Accident;
import il.co.anyway.app.models.Discussion;

public class MarkersManager {

    public static final boolean DO_RESET = true;
    public static final boolean DO_NOT_RESET = false;
    private static MarkersManager instance = null;
    private final String LOG_TAG = MarkersManager.class.getSimpleName();

    private List<Accident> accidentsList;
    private List<Discussion> discussionList;
    private Set<OnNewAccidentListener> mOnNewAccidentListeners;
    private OnNewDiscussionListener mOnNewDiscussionListener;

    // making the default constructor private make sure there will be only one instance of the accidents manager
    private MarkersManager() {
        accidentsList = new ArrayList<>();
        discussionList = new ArrayList<>();
        mOnNewAccidentListeners = new CopyOnWriteArraySet<>();
        mOnNewDiscussionListener = null;
    }

    public static MarkersManager getInstance() {
        if (instance == null)
            instance = new MarkersManager();
        return instance;
    }

    /**
     * check if an accident exist in the markers manager
     *
     * @param toCheck the Accident object to check
     * @return true if exist, false if not or toCheck is null
     */
    private boolean isAccidentExist(Accident toCheck) {

        if (toCheck == null)
            return false;

        for (Accident a : accidentsList)
            if (a.getId() == toCheck.getId()) {
                return true;
            }

        return false;
    }

    /**
     * check if an discussion exist in the markers manager
     *
     * @param toCheck the Discussion object to check
     * @return true if exist, false if not or toCheck is null
     */
    private boolean isDiscussionExist(Discussion toCheck) {

        if (toCheck == null)
            return false;

        for (Discussion d : discussionList)
            if (d.getTitle().equals(toCheck.getTitle())) {
                return true;
            }

        return false;
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
            accidentsList.add(toAdd);
            if (mOnNewAccidentListeners != null) {
                for (OnNewAccidentListener onNewAccidentListener : mOnNewAccidentListeners) {
                    onNewAccidentListener.onNewAccident(toAdd);
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Add discussion to the list
     *
     * @param toAdd the Discussion object to add
     * @return true if discussion added, false if discussion already exist, or toAdd is null
     */
    public boolean addDiscussion(Discussion toAdd) {

        if (toAdd == null)
            return false;

        if (!isDiscussionExist(toAdd)) {
            discussionList.add(toAdd);
            if (mOnNewDiscussionListener != null)
                mOnNewDiscussionListener.onNewDiscussion(toAdd);
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
        if (reset)
            accidentsList.clear();

        if (toAddList == null)
            return 0;

        int counter = 0;

        for (Accident a : toAddList) {
            if (addAccident(a)) {
                counter++;
            }
        }


        //Log.i(LOG_TAG, counter + " accidents of " + toAddList.size() + " added");
        return counter;
    }

    /**
     * Add a list of accidents to the list
     *
     * @param toAddList the list of Accident objects
     * @param reset     use to select if you want to reset the list before adding the new list
     * @return How many accidents from the list actually taken(duplicate accident will ignore)
     */
    public int addAllDiscussions(List<Discussion> toAddList, boolean reset) {

        if (reset)
            discussionList.clear();

        if (toAddList == null)
            return 0;

        int counter = 0;

        for (Discussion d : toAddList) {
            if (addDiscussion(d)) {
                counter++;
            }
        }


        //Log.i(LOG_TAG, counter + " discussions of " + toAddList.size() + " added");
        return counter;
    }

    /**
     * @return the list of all accidents in the list
     */
    public List<Accident> getAllAccidents() {
        return accidentsList;
    }

    /**
     * @return the list of all discussions in the list
     */
    public List<Discussion> getAllDiscussions() {
        return discussionList;
    }

    /**
     * Get all the accidents that not on the map
     *
     * @return a list of accidents that not on the map
     */
    public List<Accident> getAllNewAccidents() {

        List<Accident> newAccidents = new ArrayList<>();

        for (Accident a : accidentsList) {
            if (!a.isMarkerAddedToMap()) {
                newAccidents.add(a);
            }
        }

        return newAccidents;
    }

    /**
     * Get all the discussions that not on the map
     *
     * @return a list of discussions that not on the map
     */
    public List<Discussion> getAllNewDiscussions() {

        List<Discussion> newDiscussions = new ArrayList<>();

        for (Discussion d : discussionList) {
            if (!d.isMarkerAddedToMap()) {
                newDiscussions.add(d);
            }
        }

        return newDiscussions;
    }

    /**
     * set all accidents and discussions marker as not on the map
     */
    public void setAllMarkersAsNotShownOnTheMap() {
        for (Accident a : accidentsList)
            a.setMarkerAddedToMap(false);

        for (Discussion d : discussionList)
            d.setMarkerAddedToMap(false);
    }

    public void registerNewAccidentListener(OnNewAccidentListener onNewAccidentListener) {
        mOnNewAccidentListeners.add(onNewAccidentListener);
    }

    public void unregisterAccidentListener(OnNewAccidentListener onNewAccidentListener) {
        mOnNewAccidentListeners.remove(onNewAccidentListener);
    }

    public void registerNewDiscussionListener(OnNewDiscussionListener onNewDiscussionListener) {
        mOnNewDiscussionListener = onNewDiscussionListener;
    }

    public void unregsiterDiscussionListener() {
        mOnNewDiscussionListener = null;
    }
}
