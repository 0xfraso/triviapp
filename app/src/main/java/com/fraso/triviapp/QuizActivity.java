package com.fraso.triviapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        Utilities.insertFragment(QuizActivity.this, R.id.quiz_fragment_container, new QuizFragment(), QuizFragment.class.getSimpleName(), false);
    }
}