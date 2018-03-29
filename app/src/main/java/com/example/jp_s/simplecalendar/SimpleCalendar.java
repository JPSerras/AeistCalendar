package com.example.jp_s.simplecalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class SimpleCalendar{
    private  LinearLayout[] weeks;
    private TextView[] days;
    private TextView[] weekDays;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private int daysOfMonth;
    private int currentDayOfWeek;
    private String[] monthNames= {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO","JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};
    private int todayDay;
    private Integer previousDaySelected;
    private Hashtable<String,List<String>> events;
    private Context context;
    private View globalview;
    private float x1,x2;
    private Typeface costumTypeface;
    private CalendarCallback calendarCallback ;

    public SimpleCalendar(Context context, View globalview, CalendarCallback calendarCallback) {
        this.calendarCallback = calendarCallback;
        this.context = context;
        this.globalview = globalview;
        events = new Hashtable<>();
        initializeCalendar();
        setWeeksDays();
        setTextViewConfig();
        todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        updateDates(Calendar.getInstance());
        debugCalendar();
        populateCalendar();

        setSelectedDayChangedListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                selectDay(view);
            }
        });

        globalview.setOnTouchListener(swipeListener());
    }

    private View.OnTouchListener swipeListener(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("swipe", "onTouch: calculating....." );
                switch (event.getAction()) {


                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        if (x1 > x2) {
                            Log.d("swipe", "onTouch: LEFT" );
                            swipeListener("Left");
                        }
                        if (x2 > x1) {
                            Log.d("swipe", "onTouch: RIGHT" );
                            swipeListener("Right");
                        }
                        return true;
                }

                return false;
            }
        };
    }

    private void swipeListener(String side){
        updateDates(swipeCalendarConditions(side));
        populateCalendar();
        populateCalendarEvents();
        debugCalendar();
    }

    public void setTextFont(String path){
        costumTypeface = Typeface.createFromAsset(context.getAssets(), path);
        TextView header = globalview.findViewById(R.id.calendar_header);
        header.setTypeface(costumTypeface, Typeface.BOLD);
        for(TextView textView : days)
            textView.setTypeface(costumTypeface);
        for(TextView textView : weekDays)
            textView.setTypeface(costumTypeface);
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
        LinearLayout weekOneLayout = globalview.findViewById(R.id.calendar_week_1);
        LinearLayout weekTwoLayout = globalview.findViewById(R.id.calendar_week_2);
        LinearLayout weekThreeLayout = globalview.findViewById(R.id.calendar_week_3);
        LinearLayout weekFourLayout = globalview.findViewById(R.id.calendar_week_4);
        LinearLayout weekFiveLayout = globalview.findViewById(R.id.calendar_week_5);
        LinearLayout weekSixLayout = globalview.findViewById(R.id.calendar_week_6);

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
        LinearLayout nwk = globalview.findViewById(R.id.calendar_week_days);
        String[] nameWeekDays = {"SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM"};
        for (int i = 0 ; i <= 6 ; ++i){
            TextView weekDay = new TextView(context);
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
                TextView day = new TextView(context);
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
        TextView textView = globalview.findViewById(R.id.calendar_header);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectDay(View view){
        if(view.getTag() != "") {
            if(previousDaySelected !=  null ){
                days[previousDaySelected].setTextColor(Color.parseColor("#d9d9d9"));
                days[previousDaySelected].setTypeface(costumTypeface, Typeface.NORMAL);
                if (events.get(String.valueOf(currentMonth) + String.valueOf(currentYear)).contains(String.valueOf(previousDaySelected - (currentDayOfWeek - 1))))
                    days[previousDaySelected].setBackgroundResource(R.drawable.textlines);
                else
                    days[previousDaySelected].setBackgroundResource(0);
            }
            int position = (int) view.getTag();
            days[position].setTextColor(Color.parseColor("#1a1a1a"));
            days[position].setTypeface(costumTypeface, Typeface.BOLD);
            days[position].setBackgroundResource(R.drawable.textlines_selected);
            previousDaySelected = position;
            if(!markTodayRule()){
                days[position].setTextColor(Color.parseColor("#1a1a1a"));
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(currentYear,currentMonth,position - currentDayOfWeek - 1);
            Log.d("MagicDate CalendarSide:", "selectDay: " + calendar);
            calendarCallback.onSelectedDayResponse(calendar);
        }
    }

    public void markEvents(Hashtable<String, List<String>> events){
        this.events = events;
        populateCalendarEvents();
    }

    private void populateCalendarEvents(){
        TextView view;
        if(events != null && events.containsKey(String.valueOf(currentMonth) + String.valueOf(currentYear))) {
            List<String> days = events.get(String.valueOf(currentMonth) + String.valueOf(currentYear));
            for (String day : days) {
                view = this.days[Integer.parseInt(day) + (currentDayOfWeek - 1)];
                view.setBackgroundResource(R.drawable.textlines);
            }
        }
    }
}
