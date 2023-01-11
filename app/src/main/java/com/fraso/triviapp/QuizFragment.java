package com.fraso.triviapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuizFragment extends Fragment {
    private Activity activity;
    private Quiz quiz;
    private ArrayList<Question> questions;
    private Question currentQuestion;

    private ProgressBar timerProgressBar;
    private TextView mTextViewTitle, mTextViewCurrQuestion, mTextViewTime;
    private Button buttonNext;

    private RadioGroup radioGroup;
    private RadioButton optionA, optionB, optionC, optionD;

    private int selectedRadioButtonId;
    private RadioButton selectedRadioButton;

    private ListIterator<Question> questionsIterator;
    private MediaPlayer correctAnswerSound;
    private MediaPlayer wrongAnswerSound;
    private long counDownTimerProgress = 0;

    private CountDownTimer countDownTimer;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = activity.getIntent();
        quiz = (Quiz) intent.getSerializableExtra("quiz");

        timerProgressBar = view.findViewById(R.id.loader_time_progress);
        buttonNext = view.findViewById(R.id.button_done);
        mTextViewTitle = view.findViewById(R.id.text_view_title);
        mTextViewTime = view.findViewById(R.id.text_view_time);
        mTextViewCurrQuestion = view.findViewById(R.id.text_view_curr_question);
        radioGroup = view.findViewById(R.id.radio_group_answers);
        optionA = view.findViewById(R.id.option_A);
        optionB = view.findViewById(R.id.option_B);
        optionC = view.findViewById(R.id.option_C);
        optionD = view.findViewById(R.id.option_D);

        correctAnswerSound = MediaPlayer.create(activity, R.raw.correct);
        wrongAnswerSound = MediaPlayer.create(activity, R.raw.wrong);

        questions = quiz.getQuestions();

        questionsIterator = questions.listIterator();

        if (questionsIterator.hasNext()) {
            mTextViewCurrQuestion.setText(String.format("%s di %s", questionsIterator.nextIndex() + 1, questions.size()));
            nextQuestion();
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRadioButton != null && checkAnswer(currentQuestion)) {
                    quiz.addScorePoint();
                }
                mTextViewCurrQuestion.setText(String.format("%s di %s", questionsIterator.nextIndex() + 1, questions.size()));

                if (questionsIterator.hasNext()) {
                    nextQuestion();
                } else {
                    finishMatch();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                selectedRadioButton = (RadioButton) view.findViewById(selectedRadioButtonId);
            }
        });

        startTimer(quiz.getDuration());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireActivity(), R.style.AlertDialog)
                        .setCancelable(false)
                        .setTitle("Sei sicuro di voler uscire?")
                        .setMessage("Le risposte non date non verranno conteggiate.")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishMatch();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private boolean checkAnswer(Question q) {
        if (TextUtils.equals(q.getCorrectAnswer(), selectedRadioButton.getText().toString())) {
            correctAnswerSound.start();
            return true;
        } else {
            wrongAnswerSound.start();
            return false;
        }
    }

    private void nextQuestion() {
        radioGroup.clearCheck();
        currentQuestion = questionsIterator.next();
        List<String> answers = currentQuestion.getAnswers();
        mTextViewTitle.setText(currentQuestion.getQuestion());
        optionA.setText(answers.get(0));
        optionB.setText(answers.get(1));
        optionC.setText(answers.get(2));
        optionD.setText(answers.get(3));

        if (questionsIterator.hasNext()) {
            buttonNext.setText("Prossima domanda");
        } else {
            buttonNext.setText("Fine");
        }
    }

    private void finishMatch() {
        countDownTimer.cancel();
        Bundle bundle = new Bundle();
        bundle.putSerializable("quiz", quiz);
        getParentFragmentManager().setFragmentResult("quizResult", bundle);
        Utilities.insertFragment((AppCompatActivity) activity, R.id.quiz_fragment_container, new QuizResultFragment(), QuizResultFragment.class.getSimpleName(), true);
    }


    private String formatTime(long l) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(l);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(l);

        return String.format(Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds - TimeUnit.MINUTES.toSeconds(minutes));
    }

    private void startTimer(int maxTimeInSeconds) {
        //used for progress bar
        countDownTimer = new CountDownTimer(maxTimeInSeconds * 1000, 1000) {
            @Override
            public void onTick(long l) {
                counDownTimerProgress++;
                timerProgressBar.setProgress((int) counDownTimerProgress * 100 / maxTimeInSeconds);

                mTextViewTime.setText(formatTime(l));
            }

            @Override
            public void onFinish() {
                finishMatch();
            }
        }.start();
    }
}