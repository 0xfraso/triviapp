package com.fraso.triviapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fraso.triviapp.Database.Matchmaking;
import com.fraso.triviapp.ViewModel.ChallengeListViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ChallengeDetailsFragment extends Fragment {
    private TextView textViewChallengeState, textViewCurrentUserScore, textViewChallengerScore, textViewWinner;
    private Button buttonPlay;
    private Challenge currentChallenge;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String currentUserUid;
    private Activity activity;
    private ChallengeListViewModel challengeListViewModel;

    public ChallengeDetailsFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_challenge_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textViewChallengeState = view.findViewById(R.id.challenge_info_textview_challengestate);
        textViewCurrentUserScore = view.findViewById(R.id.challenge_info_textview_currentuser_score);
        textViewChallengerScore = view.findViewById(R.id.challenge_info_textview_challenger_score);
        textViewWinner = view.findViewById(R.id.challenge_info_textview_winner);
        buttonPlay = view.findViewById(R.id.challenge_info_button_play);

        challengeListViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ChallengeListViewModel.class);

        challengeListViewModel.getSelectedItem().observe(getViewLifecycleOwner(), new Observer<Challenge>() {
            @Override
            public void onChanged(Challenge challenge) {
                currentChallenge = challenge;
                String challengerUsername = challenge.getChallengerUsername(currentUserUid);
                String challengerUid = challenge.getChallengerUid(currentUserUid);

                switch (challenge.getChallengeState()) {
                    case READY:
                        textViewChallengeState.setText("Sfida in corso");
                        textViewChallengerScore.setText("Il punteggio di " + challengerUsername  + " : NASCOSTO");
                        textViewChallengeState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_play_circle_outline_24, 0,0,0);
                        if (!challenge.playerHasScore(currentUserUid)) {
                            textViewCurrentUserScore.setText("Tocca a te!");
                            buttonPlay.setText("Gioca");
                            buttonPlay.setClickable(true);
                            buttonPlay.setBackgroundResource(R.drawable.button_primary);
                        } else {
                            textViewCurrentUserScore.setText("Il tuo punteggio: " + String.valueOf(challenge.getPlayerScore(currentUserUid)));
                            buttonPlay.setText("In attesa");
                            buttonPlay.setClickable(false);
                            buttonPlay.setBackgroundResource(R.drawable.button_secondary);
                        }
                        break;
                    case COMPLETED:
                        textViewChallengeState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_24, 0,0,0);
                        textViewChallengeState.setText("Sfida completata");
                        textViewCurrentUserScore.setText("Il tuo punteggio: " + String.valueOf(challenge.getPlayerScore(currentUserUid)));
                        textViewChallengerScore.setText("Il punteggio di " + challengerUsername + ": " + String.valueOf(challenge.getPlayerScore(challengerUid)));
                        buttonPlay.setVisibility(View.GONE);

                        textViewWinner.setVisibility(View.VISIBLE);

                        if (challenge.getPlayerScore(currentUserUid) > challenge.getPlayerScore(challengerUid)) {
                            textViewWinner.setText("Hai vinto!");
                        } else {
                            textViewWinner.setText("Hai perso!");
                        }
                        break;
                    default:
                        break;
                }

                if (buttonPlay.isClickable())
                    buttonPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (challenge.getChallengeState() != ChallengeState.COMPLETED) {
                                playChallenge(challenge);
                            }
                        }
                    });
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Utilities.QUIZ_RESULT_CODE) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Quiz resultQuiz = (Quiz) intent.getSerializableExtra("quiz");
                        currentChallenge.addPlayerScore(currentUserUid, resultQuiz.getScore());
                        Matchmaking.updateChallenge(activity, currentChallenge.getChallengeId(), currentChallenge);
                        challengeListViewModel.setSelectedItem(currentChallenge);
                    }
                }
            }
        });
    }

    private void playChallenge(Challenge challenge) {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        intent.putExtra("quiz", challenge.getQuiz());

        activityResultLauncher.launch(intent);
    }
}