package com.example.jp_s.simplecalendar;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pwittchen.swipe.library.rx2.Swipe;

import java.util.Hashtable;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Calendar.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Calendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private  LinearLayout[] weeks;
    private TextView[] days;
    private TextView[] weekDays;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private int daysOfMonth;
    private int currentDayOfWeek;
    private String[] monthNames= {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO","JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};
    private Swipe swipe;
    private int todayDay;
    private Integer previousDaySelected;
    private Hashtable<String,List<String>> events;

    private OnFragmentInteractionListener mListener;

    public Calendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Calendar.
     */
    // TODO: Rename and change types and number of parameters
    public static Calendar newInstance(String param1, String param2) {
        Calendar fragment = new Calendar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        events = new Hashtable<>();
        getActivity().setContentView(R.layout.fragment_calendar);
        initializeCalendar();
        setWeeksDays();
        setTextViewConfig();
        todayDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
        updateDates(java.util.Calendar.getInstance());
        debugCalendar();
        populateCalendar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public void setTextFontDays(AssetManager assets , String path){
        Typeface textFont = Typeface.createFromAsset(assets, path);
        for(TextView textView : days)
            textView.setTypeface(textFont);
    }

    public void setTextFontWeekDays(AssetManager assets, String path){
        Typeface textFont = Typeface.createFromAsset(assets, path);
        for(TextView textView : weekDays)
            textView.setTypeface(textFont);
    }

    public void setTodayColor(String HexColor){
        Log.d("setTodayColor", "setTodayColor: " + (currentDay+(currentDayOfWeek - 1)));
        days[todayDay+(currentDayOfWeek - 1)].setTextColor(Color.parseColor(HexColor));
    }

    public void setSelectedDayChangedListener(TextView.OnClickListener listener){
        for (TextView textView : days)
            textView.setOnClickListener(listener);
    }

    private void initializeCalendar(){
        LinearLayout weekOneLayout = getView().findViewById(R.id.calendar_week_1);
        LinearLayout weekTwoLayout = getView().findViewById(R.id.calendar_week_2);
        LinearLayout weekThreeLayout = getView().findViewById(R.id.calendar_week_3);
        LinearLayout weekFourLayout = getView().findViewById(R.id.calendar_week_4);
        LinearLayout weekFiveLayout = getView().findViewById(R.id.calendar_week_5);
        LinearLayout weekSixLayout = getView().findViewById(R.id.calendar_week_6);

        weeks = new LinearLayout[6];
        weekDays = new TextView[7];
        days = new TextView[6 * 7];

        weeks[0] = weekOneLayout;
        weeks[1] = weekTwoLayout;
        weeks[2] = weekThreeLayout;
        weeks[3] = weekFourLayout;
        weeks[4] = weekFiveLayout;
        weeks[5] = weekSixLayout;
    }

    private void setWeeksDays(){
        LinearLayout nwk = getView().findViewById(R.id.calendar_week_days);
        String[] nameWeekDays = {"SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM"};
        for (int i = 0 ; i <= 6 ; ++i){
            TextView weekDay = new TextView(getActivity().getApplicationContext());
            weekDay.setTextColor(Color.BLACK);
            weekDay.setTypeface(null, Typeface.BOLD);
            weekDay.setTextSize(12);
            weekDay.setBackgroundColor(Color.TRANSPARENT);
            weekDay.setGravity(Gravity.CENTER);
            weekDay.setLayoutParams(textViewParameter());
            weekDay.setText(nameWeekDays[i]);
            weekDays[i] = weekDay;
            nwk.addView(weekDay);
        }
    }

    private LinearLayout.LayoutParams textViewParameter(){
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        textParams.weight = 1;
        return textParams;
    }

    private void setTextViewConfig(){
        int daysArrayCount = 0;
        for (int weekNumber = 0; weekNumber < 6; ++weekNumber) {
            for (int dayInWeek = 0; dayInWeek < 7; ++dayInWeek) {
                TextView day = new TextView(getActivity().getApplicationContext());
                day.setTextColor(Color.BLACK);
                day.setBackgroundColor(Color.TRANSPARENT);
                day.setLayoutParams(textViewParameter());
                day.setGravity(Gravity.CENTER);
                day.setTextSize(25);
                day.setPadding(0,10,0,10);
                days[daysArrayCount] = day;
                weeks[weekNumber].addView(day);

                ++daysArrayCount;
            }
        }
    }

    private void updateDates(java.util.Calendar calendar){
        currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(java.util.Calendar.MONTH);
        currentYear = calendar.get(java.util.Calendar.YEAR);
        setHeader(currentMonth);
        daysOfMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 0);
        currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }

    private void populateCalendar(){
        cleanCalendarDays();
        int dayNumber = 1;
        for(int i = currentDayOfWeek; i < daysOfMonth + currentDayOfWeek; i++){
            days[i].setText(String.valueOf(dayNumber));
            days[i].setTag(i);
            dayNumber++;
        }
        markTodayRule();
    }

    private void cleanCalendarDays(){
        for(int i = 0; i < days.length ; i++){
            days[i].setText("");
            days[i].setTag("");
            days[i].setTextColor(Color.parseColor("#d9d9d9"));
            days[i].setTypeface(null,Typeface.NORMAL);
            days[i].setBackgroundResource(0);
        }
    }

    private void debugCalendar(){
        Log.d("debugCalendar", "debugCalendar: currentDay:"+ currentDay);
        Log.d("debugCalendar", "debugCalendar: currentDayOfWeek:"+ currentDayOfWeek);
        Log.d("debugCalendar", "debugCalendar: currentMonth:"+ currentMonth);
        Log.d("debugCalendar", "debugCalendar: currentYear:"+ currentYear);
        Log.d("debugCalendar", "debugCalendar: daysOfMonth:"+ daysOfMonth);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        swipe.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }*/

    private java.util.Calendar swipeCalendarConditions(String direction){
        Log.d("debug", "swipeCalendarConditions: " + direction);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        if (direction.equals("Left")){
            if (currentMonth == 11){
                calendar.set(currentYear + 1, 0,1);
            }
            else {
                calendar.set(currentYear, currentMonth + 1,1);
            }
        }
        if (direction.equals("Right")){
            if (currentMonth == 0){
                calendar.set(currentYear - 1, 11,1);
            }
            else {
                calendar.set(currentYear, currentMonth - 1,1);
            }
        }
        return calendar;
    }

    private void setHeader(int position){
        TextView textView = getView().findViewById(R.id.calendar_header);
        textView.setText(monthNames[position] + " " + currentYear);
    }

    private boolean markTodayRule(){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        if (currentMonth == calendar.get(java.util.Calendar.MONTH) &&
                currentYear == calendar.get(java.util.Calendar.YEAR)) {
            setTodayColor("#0082c8");
            return true;
        }
        else {
            setTodayColor("#d9d9d9");
            return false;
        }
    }

    public void selectDay(View view){
        if(view.getTag() != "") {
            if(previousDaySelected !=  null ){
                days[previousDaySelected].setTextColor(Color.parseColor("#d9d9d9"));
                days[previousDaySelected].setTypeface(null, Typeface.NORMAL);
                if (events.contains(previousDaySelected - (currentDayOfWeek - 1)))
                    days[previousDaySelected].setBackgroundResource(R.drawable.textlines);
                else
                    days[previousDaySelected].setBackgroundResource(0);
            }
            int position = (int) view.getTag();
            days[position].setTextColor(Color.parseColor("#1a1a1a"));
            days[position].setTypeface(null, Typeface.BOLD);
            days[position].setBackgroundResource(R.drawable.textlines_selected);
            previousDaySelected = position;
            if(!markTodayRule()){
                days[position].setTextColor(Color.parseColor("#1a1a1a"));
            }
        }
    }

    public void markEvents(Hashtable<String, List<String>> events){
        this.events = events;
        populateCalendar();
    }

    private void populateCalendarEvents(){
        TextView view;
        if(events.containsKey(currentMonth + currentYear)) {
            List<String> days = events.get(currentMonth + currentYear);
            for (String day : days) {
                view = this.days[Integer.parseInt(day) + (currentDayOfWeek - 1)];
                view.setBackgroundResource(R.drawable.textlines);
            }
        }
    }
}
