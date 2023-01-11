package com.fraso.triviapp.Database;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.fraso.triviapp.Challenge;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChallengeRepository {
    MutableLiveData<List<Challenge>> challengeListMutableLiveData;
    FirebaseFirestore mFirestore;

    public ChallengeRepository() {
        this.challengeListMutableLiveData = new MutableLiveData<>();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Challenge>> getChallengeListMutableLiveData() {
        mFirestore.collection("challenges").whereEqualTo("players." + FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<Challenge> challengeList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        challengeList.add(doc.toObject(Challenge.class));
                    }
                }
                challengeListMutableLiveData.postValue(challengeList);
            }
        });
        return challengeListMutableLiveData;
    }
}