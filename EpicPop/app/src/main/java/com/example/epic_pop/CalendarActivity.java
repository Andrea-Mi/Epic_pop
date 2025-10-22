package com.example.epic_pop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epic_pop.database.EpicPopDatabase;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView rvDays;
    private StreakCalendarAdapter adapter;
    private TextView tvMonthYear, tvCurrentStreak;
    private TextView btnPrevMonth, btnNextMonth;
    private Calendar currentMonth; // points to first day of visible month
    private EpicPopDatabase db;
    private int userId;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        db = EpicPopDatabase.getDatabase(this);
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        rvDays = findViewById(R.id.rv_calendar_days);
        tvMonthYear = findViewById(R.id.tv_month_year);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        executor = Executors.newSingleThreadExecutor();

        rvDays.setLayoutManager(new GridLayoutManager(this, 7));
        adapter = new StreakCalendarAdapter(new ArrayList<>(), day -> {
            if (day.dayOfMonth <= 0) return;
            switch (day.type) {
                case ON_TIME:
                    Toast.makeText(this, getString(R.string.streak_day_on_time), Toast.LENGTH_SHORT).show();
                    break;
                case CATCH_UP:
                    Toast.makeText(this, getString(R.string.streak_day_catch_up), Toast.LENGTH_SHORT).show();
                    break;
                case NONE:
                default:
                    Toast.makeText(this, getString(R.string.streak_day_missed), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        rvDays.setAdapter(adapter);

        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            loadMonth();
        });
        btnNextMonth.setOnClickListener(v -> {
            if (canGoNextMonth()) {
                currentMonth.add(Calendar.MONTH, 1);
                loadMonth();
            }
        });

        loadMonth();
    }

    private boolean canGoNextMonth() {
        Calendar today = Calendar.getInstance();
        if (currentMonth.get(Calendar.YEAR) > today.get(Calendar.YEAR)) return false;
        if (currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentMonth.get(Calendar.MONTH) >= today.get(Calendar.MONTH)) return false;
        return true;
    }

    private void updateNavButtons() {
        boolean enableNext = canGoNextMonth();
        btnNextMonth.setAlpha(enableNext ? 1f : 0.3f);
        btnNextMonth.setClickable(enableNext);
    }

    private void loadMonth() {
        // Update month label early
        String monthName = new DateFormatSymbols(new Locale("es")).getMonths()[currentMonth.get(Calendar.MONTH)];
        tvMonthYear.setText(capitalize(monthName) + " " + currentMonth.get(Calendar.YEAR));
        updateNavButtons();

        if (userId == -1) {
            adapter.updateDays(new ArrayList<>());
            tvCurrentStreak.setText("");
            return;
        }

        executor.execute(() -> {
            List<StreakDay> resultDays = buildMonthDays();
            Calendar todayCal = Calendar.getInstance();
            setToDayStart(todayCal);
            long todayStart = todayCal.getTimeInMillis();
            runOnUiThread(() -> {
                adapter.updateDays(resultDays);
                updateCurrentStreak(todayStart, resultDays);
            });
        });
    }

    private List<StreakDay> buildMonthDays() {
        List<StreakDay> days = new ArrayList<>();
        Calendar work = (Calendar) currentMonth.clone();
        int firstWeekday = weekDayToColumn(work.get(Calendar.DAY_OF_WEEK));
        for (int i = 0; i < firstWeekday; i++) {
            days.add(new StreakDay(0, 0, false, StreakDay.Type.NONE));
        }
        int maxDay = work.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar todayCal = Calendar.getInstance(); setToDayStart(todayCal); long todayStart = todayCal.getTimeInMillis();
        Calendar monthStart = (Calendar) currentMonth.clone(); setToDayStart(monthStart);
        Calendar monthEnd = (Calendar) currentMonth.clone(); monthEnd.set(Calendar.DAY_OF_MONTH, maxDay); setToDayStart(monthEnd);
        long endRange = monthEnd.getTimeInMillis() + 24L * 60 * 60 * 1000 - 1;
        Set<Long> completionDayStarts = new HashSet<>();
        try {
            List<String> completedDates = db.retoDao().getCompletedDatesInRange(userId, monthStart.getTimeInMillis(), endRange);
            for (String tsStr : completedDates) {
                try {
                    long ts = Long.parseLong(tsStr);
                    Calendar c = Calendar.getInstance(); c.setTimeInMillis(ts); setToDayStart(c);
                    completionDayStarts.add(c.getTimeInMillis());
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        Calendar dayIter = (Calendar) currentMonth.clone();
        for (int d = 1; d <= maxDay; d++) {
            dayIter.set(Calendar.DAY_OF_MONTH, d); setToDayStart(dayIter); long dayStart = dayIter.getTimeInMillis();
            boolean isFuture = dayStart > todayStart;
            StreakDay.Type type;
            if (completionDayStarts.contains(dayStart)) {
                type = StreakDay.Type.ON_TIME;
            } else {
                Calendar next = (Calendar) dayIter.clone(); next.add(Calendar.DAY_OF_MONTH, 1); setToDayStart(next);
                long nextStart = next.getTimeInMillis(); boolean nextHas = completionDayStarts.contains(nextStart);
                if (!isFuture && nextHas && next.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)) {
                    type = StreakDay.Type.CATCH_UP;
                } else {
                    type = StreakDay.Type.NONE;
                }
            }
            days.add(new StreakDay(d, dayStart, true, type));
        }
        return days;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }

    private void updateCurrentStreak(long todayStart, List<StreakDay> days) {
        // Streak = consecutive days ending today (or yesterday if catch-up?) that have star (ON_TIME or CATCH_UP)
        Calendar today = Calendar.getInstance();
        int todayMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);
        if (todayMonth != currentMonth.get(Calendar.MONTH) || todayYear != currentMonth.get(Calendar.YEAR)) {
            tvCurrentStreak.setText("");
            return;
        }
        int todayDay = today.get(Calendar.DAY_OF_MONTH);
        int streak = 0;
        for (int d = todayDay; d >= 1; d--) {
            final int searchDay = d; // asegurar efectivamente final si se reutiliza
            StreakDay sd = null;
            for (StreakDay candidate : days) {
                if (candidate.dayOfMonth == searchDay) {
                    sd = candidate;
                    break;
                }
            }
            if (sd == null) break;
            if (sd.type == StreakDay.Type.ON_TIME || sd.type == StreakDay.Type.CATCH_UP) {
                streak++;
            } else {
                break;
            }
        }
        if (streak > 0) {
            tvCurrentStreak.setText(getString(R.string.streak_current, streak));
        } else {
            tvCurrentStreak.setText("");
        }
    }

    private int weekDayToColumn(int calDayOfWeek) {
        // Calendar.MONDAY=2 .. SUNDAY=1 ; We want columns starting Monday=0
        switch (calDayOfWeek) {
            case Calendar.MONDAY: return 0;
            case Calendar.TUESDAY: return 1;
            case Calendar.WEDNESDAY: return 2;
            case Calendar.THURSDAY: return 3;
            case Calendar.FRIDAY: return 4;
            case Calendar.SATURDAY: return 5;
            case Calendar.SUNDAY: return 6;
        }
        return 0;
    }

    private void setToDayStart(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0,1).toUpperCase(Locale.getDefault()) + text.substring(1);
    }
}
