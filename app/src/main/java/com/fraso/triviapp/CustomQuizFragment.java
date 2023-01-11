package com.fraso.triviapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fraso.triviapp.Database.TriviaAPI;
import com.fraso.triviapp.Database.TriviaRequestListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomQuizFragment extends Fragment {
    private final static int MIN_TIME_SECS = 30;
    private final static int INITIAL_TIME_SECS = 60;

    private Activity activity;

    private TextView textViewLimit, textViewTime;
    private SeekBar requestLimit, timeLimit;
    private ProgressBar loaderCategories;
    private Spinner spinnerDifficulty;
    private ChipGroup categoriesChips;

    private RequestQueue requestQueue;

    private ArrayList<String> selectedCategories;

    public CustomQuizFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestQueue = Volley.newRequestQueue(activity);
        selectedCategories = new ArrayList<>();

        requestLimit = view.findViewById(R.id.request_limit);
        timeLimit = view.findViewById(R.id.time_limit);
        textViewLimit = view.findViewById(R.id.text_view_limit);
        textViewTime = view.findViewById(R.id.text_view_time);
        spinnerDifficulty = view.findViewById(R.id.spinner_difficulty);
        loaderCategories = view.findViewById(R.id.loader_categories);
        categoriesChips = view.findViewById(R.id.categories_chips);

        requestLimit.setProgress(5);
        timeLimit.setProgress(INITIAL_TIME_SECS);
        textViewLimit.setText(String.valueOf(Integer.valueOf(requestLimit.getProgress())));
        textViewTime.setText(String.valueOf(Integer.valueOf(timeLimit.getProgress())));
        ArrayList<String> difficulties = new ArrayList<String>(Difficulty.getStringValues());
        spinnerDifficulty.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, difficulties));

        requestLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 1) seekBar.setProgress(1);
                textViewLimit.setText(String.valueOf(Integer.valueOf(seekBar.getProgress())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        timeLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < MIN_TIME_SECS) seekBar.setProgress(MIN_TIME_SECS);
                textViewTime.setText(String.valueOf(Integer.valueOf(seekBar.getProgress())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        view.findViewById(R.id.fragment_custom_quiz_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Difficulty difficulty = Difficulty.valueOf(spinnerDifficulty.getSelectedItem().toString().toUpperCase(Locale.ROOT));

                List<Integer> chipsCategories = categoriesChips.getCheckedChipIds();

                for (Integer id : chipsCategories) {
                    Chip chip = view.findViewById(id);
                    selectedCategories.add(chip.getText().toString());
                }

                String url = TriviaAPI.composeURL(requestLimit.getProgress(), difficulty, selectedCategories);
                TriviaAPI.getQuiz(url, requestQueue, new TriviaRequestListener<Question>() {
                    @Override
                    public void onRequestComplete(ArrayList<Question> list) {
                        if (list != null && !list.isEmpty()) {
                            Intent intent = new Intent(activity, QuizActivity.class);
                            Quiz q = new Quiz(requestLimit.getProgress(), timeLimit.getProgress(), difficulty, list, selectedCategories);
                            intent.putExtra("quiz", q);

                            startActivity(intent);
                        } else
                            Toast.makeText(activity, "Error fetching Questions!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        TriviaAPI.getCategories(requestQueue, new TriviaRequestListener<String>() {
            @Override
            public void onRequestComplete(ArrayList<String> categories) {
                for (int i = 0; i < categories.size(); i++) {
                    Chip c = new Chip(activity);
                    c.setText(categories.get(i));
                    c.setTextColor(ContextCompat.getColorStateList(activity,R.color.white));
                    c.setCheckable(true);
                    c.setChipBackgroundColor(ContextCompat.getColorStateList(activity,R.color.dark_200));
                    categoriesChips.addView(c);
                }
                loaderCategories.setVisibility(View.GONE);
            }
        });
    }
}