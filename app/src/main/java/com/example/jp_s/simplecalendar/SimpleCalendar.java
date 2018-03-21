package com.example.jp_s.simplecalendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class SimpleCalendar extends FragmentActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*events = new Hashtable<>();
        setContentView(R.layout.activity_calendar);
        initializeCalendar();
        setWeeksDays();
        setTextViewConfig();
        todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        updateDates(Calendar.getInstance());
        debugCalendar();
        populateCalendar();

        setSelectedDayChangedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDay(view);
            }
        });

        swipe = new Swipe();
        swipe.setListener(new SwipeListener() {
            @Override public void onSwipingLeft(final MotionEvent event) {

            }

            @Override public boolean onSwipedLeft(final MotionEvent event) {
                updateDates(swipeCalendarConditions("Left"));
                populateCalendar();
                debugCalendar();
                populateCalendarEvents();
                return true;
            }

            @Override public void onSwipingRight(final MotionEvent event) {
            }

            @Override public boolean onSwipedRight(final MotionEvent event) {
                updateDates(swipeCalendarConditions("Right"));
                populateCalendar();
                populateCalendarEvents();
                debugCalendar();
                return true;
            }

            @Override
            public void onSwipingUp(MotionEvent event) {

            }

            @Override
            public boolean onSwipedUp(MotionEvent event) {
                return false;
            }

            @Override
            public void onSwipingDown(MotionEvent event) {

            }

            @Override
            public boolean onSwipedDown(MotionEvent event) {
                return false;
            }
        });*/

    }

    public void setTextFontDays(String path){
        Typeface textFont = Typeface.createFromAsset(getApplicationContext().getAssets(), path);
        for(TextView textView : days)
            textView.setTypeface(textFont);
    }

    public void setTextFontWeekDays(String path){
        Typeface textFont = Typeface.createFromAsset(getApplicationContext().getAssets(), path);
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
        LinearLayout weekOneLayout = findViewById(R.id.calendar_week_1);
        LinearLayout weekTwoLayout = findViewById(R.id.calendar_week_2);
        LinearLayout weekThreeLayout = findViewById(R.id.calendar_week_3);
        LinearLayout weekFourLayout = findViewById(R.id.calendar_week_4);
        LinearLayout weekFiveLayout = findViewById(R.id.calendar_week_5);
        LinearLayout weekSixLayout = findViewById(R.id.calendar_week_6);

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
        LinearLayout nwk = findViewById(R.id.calendar_week_days);
        String[] nameWeekDays = {"SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM"};
        for (int i = 0 ; i <= 6 ; ++i){
            TextView weekDay = new TextView(getApplicationContext());
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
                TextView day = new TextView(getApplicationContext());
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

    private void updateDates(Calendar calendar){
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        setHeader(currentMonth);
        daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        swipe.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private Calendar swipeCalendarConditions(String direction){
        Log.d("debug", "swipeCalendarConditions: " + direction);
        Calendar calendar = Calendar.getInstance();
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
        TextView textView = findViewById(R.id.calendar_header);
        textView.setText(monthNames[position] + " " + currentYear);
    }

    private boolean markTodayRule(){
        Calendar calendar = Calendar.getInstance();
        if (currentMonth == calendar.get(Calendar.MONTH) &&
                currentYear == calendar.get(Calendar.YEAR)) {
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
