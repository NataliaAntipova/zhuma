package com.violet.saper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.violet.saper.UI.GridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Game {
    Context context;
    ArrayList<Integer> records;
    Field field;
    int xDim, yDim;
    int mines;
    GridView gridView; //сетка
    ConstraintLayout constraintLayout;
    boolean[][] visualGrid;
    boolean isGameEnds;
    TextView gameStatusTextView; //итог
    List<View> dynamicsViews = new ArrayList<>();
    TextView timerTextView;
    CountDownTimer countDownTimer;
    int seconds = 0;
    int minutes = 0;
    int hours = 0;
    float textSize = 18f;

    Game(Context context) {
        this.context = context;
        records = getIntArray("records");
    }

    Game(Context context, int x, int y, int mines, GridView gridView, ConstraintLayout constraintLayout,
         TextView gameStatusTextView, TextView timerTextView) {
        this.context = context;
        xDim = x;
        yDim = y;
        this.mines = mines;
        this.gridView = gridView;
        this.constraintLayout = constraintLayout;
        this.gameStatusTextView = gameStatusTextView;
        this.timerTextView = timerTextView;
        if (xDim == 5) textSize = 32f;
        if(xDim == 15) textSize = 14f;
        init();
    }
    void init() {
        records = getIntArray("records");
        field = new Field(xDim, yDim);
        field.randomMines(mines);
        field.count();
        gridView.setDimension(xDim, yDim);
        visualGrid = new boolean[xDim][yDim];
        settingGrid();
        startTimer();
    }

    public void startTimer() {
        seconds = 0;
        minutes = 0;
        hours = 0;
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds++;
                if (seconds == 60) {
                    seconds = 0;
                    minutes++;
                    if (minutes == 60) {
                        minutes = 0;
                        hours++;
                    }
                }
                updateTimerText();
            }

            @Override
            public void onFinish() {
                // do nothing
            }
        };
        countDownTimer.start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void updateTimerText() {
        String timeString = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        if (hours > 0) {
            timeString = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        }
        timerTextView.setText(timeString);
    }

    void restart() {
        for (View v : dynamicsViews) {
            constraintLayout.removeView(v);
        }
        dynamicsViews = new ArrayList<>();
        field = new Field(xDim, yDim);
        field.randomMines(mines);
        field.count();
        visualGrid = new boolean[xDim][yDim];
        gameStatusTextView.setText("");
        isGameEnds = false;
        stopTimer();
        startTimer();
    }
    public void saveIntArray(ArrayList<Integer> arrayList, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < arrayList.size(); i++) {
            jsonArray.put(arrayList.get(i));
        }

        editor.putString(key, jsonArray.toString());
        editor.apply();
    }

    public ArrayList<Integer> getIntArray(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonArrayString = prefs.getString(key, "");

        ArrayList<Integer> arrayList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(jsonArray.optInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    @SuppressLint("ClickableViewAccessibility")
    void settingGrid() { // настраивает сетку
        int width = gridView.getLayoutParams().width;
        int height = gridView.getLayoutParams().height;
        int xGrid = gridView.getLeft();
        int yGrid = gridView.getTop();
        float xStep = (float) width / xDim;
        float yStep = (float) height / yDim;
        gridView.setOnTouchListener((v, event) -> {
            if (!isGameEnds) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float y = event.getY();
                    int xPos = (int) (x / width * xDim);
                    int yPos = (int) (y / height * yDim);
                    if (field.safeCheck(xPos, yPos)) {
                        if (!visualGrid[xPos][yPos]) {
                            visualGrid[xPos][yPos] = true;
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) xStep - 1, (int) yStep - 1);
                            if (field.matrix[xPos][yPos] == 0) {
                                List<int[]> nulls = field.getNullsWithOthers(field.matrix, xPos, yPos);
                                for (int[] aNull : nulls) {
                                    int xNull = aNull[0];
                                    int yNull = aNull[1];
                                    TextView textView = new TextView(context);
                                    textView.setTextSize(textSize);
                                    textView.setTypeface(null, Typeface.BOLD);
                                    switch (field.matrix[xNull][yNull]) {
                                        case 1:
                                            textView.setTextColor(Color.CYAN);
                                            break;
                                        case 2:
                                            textView.setTextColor(Color.GREEN);
                                            break;
                                        case 3:
                                            textView.setTextColor(Color.RED);
                                            break;
                                        case 4:
                                            textView.setTextColor(Color.BLUE);
                                            break;
                                        default:
                                            textView.setTextColor(Color.GRAY);
                                            textView.setTextSize(textSize - 4);
                                            textView.setTypeface(null, Typeface.NORMAL);
                                    }
                                    textView.setText(String.valueOf(field.matrix[xNull][yNull]));
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setLayoutParams(layoutParams);
                                    constraintLayout.addView(textView);
                                    dynamicsViews.add(textView);
                                    textView.setId(View.generateViewId());
                                    ConstraintSet constraintSet = new ConstraintSet();
                                    constraintSet.clone(constraintLayout);
                                    constraintSet.connect(textView.getId(), ConstraintSet.START, gridView.getId(), ConstraintSet.START, (int) (xGrid + xNull * xStep));
                                    constraintSet.connect(textView.getId(), ConstraintSet.TOP, gridView.getId(), ConstraintSet.TOP, (int) (yGrid + yNull * yStep));
                                    constraintSet.applyTo(constraintLayout);
                                    visualGrid[xNull][yNull] = true;
                                }
                            } else if (field.matrix[xPos][yPos] > 0) {
                                TextView textView = new TextView(context);
                                switch (field.matrix[xPos][yPos]) {
                                    case 1:
                                        textView.setTextColor(Color.CYAN);
                                        break;
                                    case 2:
                                        textView.setTextColor(Color.GREEN);
                                        break;
                                    case 3:
                                        textView.setTextColor(Color.RED);
                                        break;
                                    case 4:
                                        textView.setTextColor(Color.BLUE);
                                        break;
                                }
                                textView.setText(String.valueOf(field.matrix[xPos][yPos]));
                                textView.setTypeface(null, Typeface.BOLD);
                                textView.setTextSize(textSize);
                                textView.setGravity(Gravity.CENTER);
                                textView.setLayoutParams(layoutParams);
                                constraintLayout.addView(textView);
                                dynamicsViews.add(textView);
                                textView.setId(View.generateViewId());
                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(constraintLayout);
                                constraintSet.connect(textView.getId(), ConstraintSet.START, gridView.getId(), ConstraintSet.START, (int) (xGrid + xPos * xStep));
                                constraintSet.connect(textView.getId(), ConstraintSet.TOP, gridView.getId(), ConstraintSet.TOP, (int) (yGrid + yPos * yStep));
                                constraintSet.applyTo(constraintLayout);
                                visualGrid[xPos][yPos] = true;
                            } else {
                                isGameEnds = true;
                                stopTimer();
                                gameStatusTextView.setText("ПРОИГРЫШ!");
                                for (int[] aMines : field.minetrix) {
                                    ImageView imageView = new ImageView(context);
                                    imageView.setBackgroundColor(Color.RED);
                                    imageView.setLayoutParams(layoutParams);
                                    constraintLayout.addView(imageView);
                                    dynamicsViews.add(imageView);
                                    imageView.setId(View.generateViewId());
                                    ConstraintSet constraintSet = new ConstraintSet();
                                    constraintSet.clone(constraintLayout);
                                    constraintSet.connect(imageView.getId(), ConstraintSet.START, gridView.getId(), ConstraintSet.START, (int) (xGrid + aMines[0] * xStep));
                                    constraintSet.connect(imageView.getId(), ConstraintSet.TOP, gridView.getId(), ConstraintSet.TOP, (int) (yGrid + aMines[1] * yStep));
                                    constraintSet.applyTo(constraintLayout);
                                }
                            }
                            int boolCounter = 0;
                            for (int xVis = 0; xVis < xDim; xVis++) {
                                for (int yVis = 0; yVis < yDim; yVis++) {
                                    if (!visualGrid[xVis][yVis]) boolCounter++;
                                }
                            }
                            if (boolCounter <= mines) {
                                stopTimer();
                                isGameEnds = true;
                                records.add(seconds + minutes * 100 + hours * 10000);
                                saveIntArray(records, "records");
                                gameStatusTextView.setText("ПОБЕДА!");
                                for (int[] aMines : field.minetrix) {
                                    ImageView imageView = new ImageView(context);
                                    imageView.setBackgroundColor(Color.WHITE);
                                    imageView.setLayoutParams(layoutParams);
                                    constraintLayout.addView(imageView);
                                    dynamicsViews.add(imageView);
                                    imageView.setId(View.generateViewId());
                                    ConstraintSet constraintSet = new ConstraintSet();
                                    constraintSet.clone(constraintLayout);
                                    constraintSet.connect(imageView.getId(), ConstraintSet.START, gridView.getId(), ConstraintSet.START, (int) (xGrid + aMines[0] * xStep));
                                    constraintSet.connect(imageView.getId(), ConstraintSet.TOP, gridView.getId(), ConstraintSet.TOP, (int) (yGrid + aMines[1] * yStep));
                                    constraintSet.applyTo(constraintLayout);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        });
    }
}
