package com.violet.saper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.violet.saper.UI.GridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        game = new Game(this);
    }

    public void Play(View view) {
        RadioGroup radioGroup = findViewById(R.id.diffRadioGroup);
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        int checkedIndex = radioGroup.indexOfChild(findViewById(checkedRadioButtonId));
        setContentView(R.layout.game);
        GridView gridView = findViewById(R.id.gridView);
        ConstraintLayout constraintLayout = findViewById(R.id.gameConstraintLayout);
        TextView gameStatusTextView = findViewById(R.id.gameStatusTextView);
        TextView timerTextView = findViewById(R.id.timerTextView);
        int x = 5 + checkedIndex * 5;
        int y = 5 + checkedIndex * 5;
        int mines = 5 + checkedIndex * 5;
        game = new Game(this, x, y, mines, gridView, constraintLayout, gameStatusTextView, timerTextView);
    }

    public void restart(View view) {
        game.restart();
    }

    public void back(View view) {
        setContentView(R.layout.activity_main);
    }
    public void goRecords(View view) {
        setContentView(R.layout.records);
        LinearLayout linearLayout = findViewById(R.id.recordsLinearLayout);
        Comparator<Integer> comparator = Collections.reverseOrder();
        game.records.sort(comparator);
        int c = 0;
        for (Integer i : game.records) {
            c++;
            TextView textView = new TextView(this);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(24f);
            String s = c + ". " + 0 + ":" + i % 100 + ":" + i / 100;
            textView.setText(s);
            linearLayout.addView(textView);
        }
    }
}