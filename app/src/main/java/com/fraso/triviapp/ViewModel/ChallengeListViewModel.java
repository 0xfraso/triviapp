package com.fraso.triviapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fraso.triviapp.Challenge;
import com.fraso.triviapp.Database.ChallengeRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChallengeListViewModel extends AndroidViewModel {
    private final MutableLiveData<Challenge> selectedItem = new MutableLiveData<>();
    public MutableLiveData<List<Challenge>> challengeListMutableLiveData;

    FirebaseFirestore firebaseFirestore;
    ChallengeRepository challengeRepository;

    public ChallengeListViewModel(@NonNull Application application) {
        super(application);
        challengeRepository = new ChallengeRepository();
        challengeListMutableLiveData = challengeRepository.getChallengeListMutableLiveData();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Challenge>> getLiveChallengeData() {
        return challengeListMutableLiveData;
    }

    public void refreshData() {
        challengeListMutableLiveData = challengeRepository.getChallengeListMutableLiveData();
    }

    public void setSelectedItem(Challenge challenge) {
        selectedItem.setValue(challenge);
    }

    public MutableLiveData<Challenge> getSelectedItem() {
        return selectedItem;
    }
}
