package com.mistapp.mistandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Event;
import com.mistapp.mistandroid.model.Teammate;
import com.roughike.bottombar.BottomBar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by aadil on 1/15/17.
 */

public class MyScheduleFragment extends Fragment {

    private static final String TAG = MyMistActivity.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private CacheHandler cacheHandler;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getActivity().getApplication(), sharedPref, editor);
        String userType = cacheHandler.getUserType();
        String userJson = cacheHandler.getUserJson();
        progressBar = (ProgressBar) view.findViewById(R.id.schedule_progress);


        Gson gson = new Gson();
        String eventsJson = cacheHandler.getCachedEventsJson();
        final EventAdapter adapter = new EventAdapter();

        progressBar.setVisibility(View.VISIBLE);

        //events are not cached already- hit database and store in cache
        if (eventsJson.equals("")){
            Log.d(TAG, "events not already cached - querying database");

            final ArrayList<String> registeredEvents = new ArrayList<String>();

            if (userType.equals("competitor")){
                //adding competition names that the user is registered in into registeredEvents array
                Competitor currentUser = gson.fromJson(userJson, Competitor.class);
                String groupProjectCompetition = currentUser.getGroupProject();
                String bracketCompetition = currentUser.getBrackets();
                String artCompetition = currentUser.getArt();
                String knowledgeCompetition = currentUser.getKnowledge();
                String writingCompetition = currentUser.getWriting();
                String sportsCompetition = currentUser.getSports();

                //for each of the competitions, check if they exist -> if so -> add it to the list of registered events
                if (groupProjectCompetition!=null && !groupProjectCompetition.equals("")) {
                    registeredEvents.add(groupProjectCompetition);
                }
                if (bracketCompetition!=null && !bracketCompetition.equals("")) {
                    registeredEvents.add(bracketCompetition);
                }
                if (artCompetition!=null && !artCompetition.equals("")) {
                    registeredEvents.add(artCompetition);
                }
                if (knowledgeCompetition!=null && !knowledgeCompetition.equals("")) {
                    registeredEvents.add(knowledgeCompetition);
                }
                if (writingCompetition!=null && !writingCompetition.equals("")) {
                    registeredEvents.add(writingCompetition);
                }
                if (sportsCompetition!=null && !sportsCompetition.equals("")) {
                    registeredEvents.add(sportsCompetition);
                }
                ///need to add other events - lunch, social, prayer, etc
                //registeredEvents.addAll(Arrays.asList("Lunch", "Workshop - sometitle", "Ice-cream Social", "Registration", "Orientation", "Dinner", "Dhuhr", "Asr", "Maghrib"));

            }



            mDatabase = FirebaseDatabase.getInstance().getReference();
            ref = mDatabase.child(getResources().getString(R.string.firebase_event_table));

            //for each competition that the user has registered for, retreive it from db, create event object, and add to listview
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Event>eventArrayList = new ArrayList<Event>();
                    int numFriday = 0;
                    int numSaturday = 0;
                    int numSunday = 0;

                    for (String competition: registeredEvents){
                        competition = competition.replaceAll("/", "_");
                        Log.d(TAG,competition + " ----");
                        DataSnapshot currentEventSnapshot = dataSnapshot.child(competition).child("locationArray");
                        Log.d(TAG,currentEventSnapshot.toString());

                        Object eventListObject = (Object)currentEventSnapshot.getValue();
                        Log.d(TAG,eventListObject.toString());
                        ArrayList<HashMap> eventList = (ArrayList<HashMap>)(eventListObject);
                        Log.d(TAG,eventList.toString());
                        for (HashMap map: eventList){
                            String date = (String)map.get("date");
                            String location = (String)map.get("location");
                            String startTime = (String)map.get("startTime");
                            ArrayList<Long> roomNumbers = (ArrayList<Long>)map.get("roomNums");
                            String endTime = (String)map.get("endTime");
                            Event e = new Event(competition, location, date, endTime, roomNumbers, startTime);

                            //add event to list - this is so it can be sorted
                            eventArrayList.add(e);
                            if (e.getDay() == 17){
                                numFriday++;
                            }
                            else if (e.getDay() == 18){
                                numSaturday++;
                            }
                            else if (e.getDay() == 19){
                                numSunday++;
                            }

                        }


                    }

                    //add event list to cache
                    cacheHandler.cacheEvents(eventArrayList);
                    cacheHandler.commitToCache();

                    Log.d(TAG, "fri: " + numFriday + " sat "+ numSaturday + " sun " + numSunday);
                    progressBar.setVisibility(View.GONE);
                    addToAdapter(adapter, eventArrayList, numFriday, numSaturday, numSunday);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                }

            });

            ListView events_lv = (ListView)view.findViewById(R.id.my_schedule_list);
            events_lv.setAdapter(adapter);
        }
        //Events were cached - need to retreive
        else{
            Log.d(TAG, "events were cached - retreiving");
            String jsonList = cacheHandler.getCachedEventsJson();
            ArrayList<Event> allEvents = gson.fromJson(jsonList, new TypeToken<ArrayList<Event>>() {}.getType());
            Log.d(TAG,allEvents.toString());
            int numFriday = 0;
            int numSaturday = 0;
            int numSunday = 0;
            for (Event e: allEvents) {
                Log.d(TAG,e.toString());
                if (e.getDay() == 17) {
                    numFriday++;
                } else if (e.getDay() == 18) {
                    numSaturday++;
                } else if (e.getDay() == 19) {
                    numSunday++;
                }
            }
            addToAdapter(adapter, allEvents, numFriday, numSaturday, numSunday);
            ListView events_lv = (ListView)view.findViewById(R.id.my_schedule_list);
            progressBar.setVisibility(View.GONE);
            events_lv.setAdapter(adapter);
        }

        return view;
    }


    //adds event items and separater items to the adapter
    public void addToAdapter(EventAdapter adapter, ArrayList<Event>eventArrayList, int numFriday, int numSaturday, int numSunday){
        //sort array and add items and separator items to adapter
        for (int x=0;x<eventArrayList.size();x++){
            Log.d("TAG", "NAME: " + eventArrayList.get(x).toString());
        }
        Event[] eventArray = eventArrayList.toArray(new Event[eventArrayList.size()]);
        Arrays.sort(eventArray, Event.EventTimeComparator);
        for (int x=0; x<eventArray.length; x++){
            adapter.addItem(eventArray[x]);
        }
        if (numFriday != 0){
            adapter.addSeparatorItem("Friday, March 17",0);
        }
        if (numSaturday != 0){
            //if nothing on friday, add as first item, else add to index num_friday + 1 (fridaySeparatorItem)
            int indexToAdd = ((numFriday==0) ? 0 : numFriday+1);
            adapter.addSeparatorItem("Saturday, March 18",indexToAdd);
        }
        if (numSunday != 0){
            int indexToAdd = 0;
            if (numFriday!=0 ){
                indexToAdd+=numFriday+1;
            }
            if (numSaturday!=0 ){
                indexToAdd+=numSaturday+1;
            }
            adapter.addSeparatorItem("Sunday, March 19",indexToAdd);
        }

    }

    private class EventAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<Object> mData = new ArrayList<Object>();
        private LayoutInflater mInflater;

        private TreeSet mSeparatorsSet = new TreeSet();

        public EventAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final Event event) {
            mData.add(event);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item, int indexOfAddition) {
            mData.add(indexOfAddition, item);
            // save separator position
            mSeparatorsSet.add(indexOfAddition);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            int type = getItemViewType(position);

            switch(type){
                case TYPE_ITEM:
                    EventViewHolder holder1;

                    View v = convertView;
                    if (v == null) {
                        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(R.layout.item_event, parent, false);

                        holder1 = new EventViewHolder();
                        holder1.nameAndTimeText = (TextView) v.findViewById(R.id.event_name_and_time);
                        holder1.locationText = (TextView) v.findViewById(R.id.event_location);
                        holder1.roomNumText = (TextView) v.findViewById(R.id.event_room_num);
                        v.setTag(holder1);
                    }
                    else {
                        holder1 = (EventViewHolder) v.getTag();
                    }

                    Object myItem = mData.get(position);
                    if (myItem instanceof Event) {
                        myItem = (Event)myItem;
                        // set up the list item
                        if (myItem != null) {
                            // set item text

                            String startTime = ((Event) myItem).getStartTime();
                            String endTime = ((Event) myItem).getEndTime();

                            //adds extra space if time is short - so competition names are aligned
                            String extraSpace1 = "";
                            String extraSpace2 = "";
                            int mostTimeCharacters = 14; //12:40am has 7 characters max number of chars is 17

                            int difference = mostTimeCharacters - (startTime.length() + endTime.length());
                            Log.d(TAG, "difference in length: " + difference);
                            for (int x=0;x<difference;x++){
                                if (x%2==0) {
                                    extraSpace1 += " ";
                                }else{
                                    extraSpace2+=" ";
                                }
                            }
                            if (holder1.nameAndTimeText != null) {
                                holder1.nameAndTimeText.setText(startTime + " - " + endTime + extraSpace1 + " : " + extraSpace2 +((Event) myItem).getName());
                            }
                            if (holder1.locationText != null){
                                holder1.locationText.setText(((Event) myItem).getLocation());
                            }
                            if (holder1.roomNumText != null){
                                ArrayList<Long> roomNumbers = ((Event) myItem).getRoomNumbers();
                                String roomNumsString = roomNumbers.toString();
                                roomNumsString = roomNumsString.substring(1,roomNumsString.length()-1);
//                                String allLocations = (roomNumbers.toString().substring(1,roomNumbers.size()-1));
                                holder1.roomNumText.setText("Rooms: " + roomNumsString);
                            }
                        }
                    }

                    // return the created view
                    return v;

                case TYPE_SEPARATOR:
                    TitleViewHolder holder2;

                    View v1 = convertView;
                    if (v1 == null) {
                        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v1 = vi.inflate(R.layout.item_day_header, parent, false);
                        holder2 = new TitleViewHolder();
                        holder2.dayText = (TextView) v1.findViewById(R.id.event_day_header);
                        v1.setTag(holder2);
                    }
                    else {
                        holder2 = (TitleViewHolder) v1.getTag();
                    }

                    Object myItem1 = mData.get(position);
                    if (myItem1 instanceof String) {
                        myItem1 = (String)myItem1;
                        // set up the list item
                        if (myItem1 != null) {
                            // set item text
                            if (holder2.dayText != null) {
                                holder2.dayText.setText(((String) myItem1));
                            }
                        }
                    }


                    // return the created view
                    return v1;
            }

            return convertView;
        }

    }

    public static class EventViewHolder {
        public TextView nameAndTimeText;
        public TextView locationText;
        public TextView roomNumText;
    }

    public static class TitleViewHolder {
        public TextView dayText;
    }

}




