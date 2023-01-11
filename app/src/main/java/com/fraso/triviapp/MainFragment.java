package com.fraso.triviapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fraso.triviapp.Database.Matchmaking;
import com.fraso.triviapp.Database.TriviaAPI;
import com.fraso.triviapp.Database.TriviaRequestListener;
import com.fraso.triviapp.RecyclerView.ChallengesListAdapter;
import com.fraso.triviapp.RecyclerView.OnItemClickListener;
import com.fraso.triviapp.ViewModel.AuthViewModel;
import com.fraso.triviapp.ViewModel.ChallengeListViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainFragment extends Fragment implements OnItemClickListener {
    private ChallengesListAdapter challengesWaitingAdapter;
    private ChallengesListAdapter challengesReadyAdapter;
    private ChallengesListAdapter challengesCompletedAdapter;

    private List<Challenge> challenges = new ArrayList<>();

    private Button buttonNewChallenge;
    private TextView textViewWaiting, textViewReady, textViewCompleted, textViewWelcome;

    private String currentUserUid;
    private String currentUserUsername;

    private RequestQueue requestQueue;
    private ChallengeListViewModel challengeListViewModel;

    private AuthViewModel authViewModel;

    private RecyclerView challengesRecyclerViewWaiting;
    private RecyclerView challengesRecyclerViewReady;
    private RecyclerView challengesRecyclerViewCompleted;
    private FragmentActivity activity;

    public MainFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = requireActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = Volley.newRequestQueue(activity);

        challengeListViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ChallengeListViewModel.class);
        challengeListViewModel.getLiveChallengeData().observe(getViewLifecycleOwner(), new Observer<List<Challenge>>() {
            @Override
            public void onChanged(List<Challenge> challenges) {
                challengesWaitingAdapter.setData(challenges.stream().filter(challenge -> ChallengeState.WAITING.equals(challenge.getChallengeState())).collect(Collectors.toList()));
                challengesReadyAdapter.setData(challenges.stream().filter(challenge -> ChallengeState.READY.equals(challenge.getChallengeState())).collect(Collectors.toList()));
                challengesCompletedAdapter.setData(challenges.stream().filter(challenge -> ChallengeState.COMPLETED.equals(challenge.getChallengeState())).collect(Collectors.toList()));

                if (challengesWaitingAdapter.getItemCount() < 1) {
                    textViewWaiting.setVisibility(View.GONE);
                } else {
                    textViewWaiting.setVisibility(View.VISIBLE);
                }

                if (challengesReadyAdapter.getItemCount() < 1)
                    textViewReady.setVisibility(View.GONE);
                else textViewReady.setVisibility(View.VISIBLE);

                if (challengesCompletedAdapter.getItemCount() < 1)
                    textViewCompleted.setVisibility(View.GONE);
                else textViewCompleted.setVisibility(View.VISIBLE);
            }
        });
        authViewModel = new ViewModelProvider(activity).get(AuthViewModel.class);
        authViewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    challengeListViewModel.refreshData();
                    currentUserUid = firebaseUser.getUid();
                    currentUserUsername = firebaseUser.getDisplayName();
                    textViewWelcome.setText(currentUserUsername);
                    challengeListViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ChallengeListViewModel.class);
                } else {
                    // empty the backstack so the user can't get back to the main fragment after logout
                    getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    challengeListViewModel.challengeListMutableLiveData.setValue(new ArrayList<>());
                    Utilities.insertFragment((AppCompatActivity) activity, R.id.fragment_container_view, new LoginFragment(), LoginFragment.class.getSimpleName(), true);
                }
            }
        });

        initUI(activity, view);
    }

    private void createChallenge() {
        final String url = TriviaAPI.composeURL(5, Difficulty.ANY, null);
        TriviaAPI.getQuiz(url, requestQueue, new TriviaRequestListener<Question>() {
            @Override
            public void onRequestComplete(ArrayList<Question> questions) {
                if (questions != null)
                    Matchmaking.findChallenge(activity, currentUserUid, currentUserUsername, new Quiz(questions));
                else
                    Toast.makeText(activity, "Nessuna domanda trovata!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI(final Activity activity, View view) {
        challengesRecyclerViewWaiting = view.findViewById(R.id.recyclerview_challenges_waiting);
        challengesRecyclerViewReady = view.findViewById(R.id.recyclerview_challenges_ready);
        challengesRecyclerViewCompleted = view.findViewById(R.id.recyclerview_challenges_completed);

        challengesWaitingAdapter = new ChallengesListAdapter(challenges, this);
        challengesRecyclerViewWaiting.setAdapter(challengesWaitingAdapter);
        challengesRecyclerViewWaiting.setLayoutManager(new LinearLayoutManager(activity));

        challengesReadyAdapter = new ChallengesListAdapter(challenges, this);
        challengesRecyclerViewReady.setAdapter(challengesReadyAdapter);
        challengesRecyclerViewReady.setLayoutManager(new LinearLayoutManager(activity));

        challengesCompletedAdapter = new ChallengesListAdapter(challenges, this);
        challengesRecyclerViewCompleted.setAdapter(challengesCompletedAdapter);
        challengesRecyclerViewCompleted.setLayoutManager(new LinearLayoutManager(activity));

        textViewWelcome = view.findViewById(R.id.fragment_main_welcome_username);
        textViewWaiting = view.findViewById(R.id.label_waiting);
        textViewReady = view.findViewById(R.id.label_ready);
        textViewCompleted = view.findViewById(R.id.label_completed);
        buttonNewChallenge = view.findViewById(R.id.button_new_challenge);


        buttonNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (challengesWaitingAdapter.getItemCount() < 1) {
                    createChallenge();
                } else {
                    Utilities.insertFragment((AppCompatActivity) requireActivity(), R.id.fragment_container_view, new CustomQuizFragment(), CustomQuizFragment.class.getSimpleName(), true);
                }
            }
        });
    }


    @Override
    public void onItemClick(Challenge challenge) {
        Activity activity = getActivity();

        if (challenge.getChallengeState() != ChallengeState.WAITING) {
            if (activity != null) {
                Utilities.insertFragment((AppCompatActivity) activity, R.id.fragment_container_view, new ChallengeDetailsFragment(), ChallengeDetailsFragment.class.getSimpleName(), true);
                challengeListViewModel.setSelectedItem(challenge);
            }
        } else {
            Toast.makeText(activity, "In attesa di uno sfidante..", Toast.LENGTH_SHORT).show();
        }
    }
}