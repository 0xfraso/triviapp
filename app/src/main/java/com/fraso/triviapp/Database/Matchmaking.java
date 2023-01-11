package com.fraso.triviapp.Database;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fraso.triviapp.Challenge;
import com.fraso.triviapp.ChallengeState;
import com.fraso.triviapp.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

public abstract class Matchmaking {
    private final static CollectionReference challengesCollection = FirebaseFirestore.getInstance().collection("challenges");
    private final static DocumentReference matchmakerRef = FirebaseFirestore.getInstance().collection("matchmaker").document("matchmaker");

    public static CollectionReference getChallengesCollectionRef() {
        return challengesCollection;
    }
    public static DocumentReference getMatchmakerReference() {
        return matchmakerRef;
    }

    public static void updateChallenge(Activity activity, String challengeId, Challenge challenge) {
        challengesCollection.document(challengeId).set(challenge).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful())
                    Toast.makeText(activity, "Errore aggiornamento sfida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void findChallenge(Activity activity, String playerUid, String playerUsername, Quiz quiz) {
        matchmakerRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String matchmaker = documentSnapshot.getString("value");
                if (matchmaker != null) {
                    if (TextUtils.equals(matchmaker, "")) {
                        findChallengeFirstArriver(activity, playerUid, playerUsername, quiz);
                    } else {
                        findChallengeSecondArriver(activity, matchmaker, playerUid, playerUsername, quiz);
                    }
                } else {
                    Toast.makeText(activity, "Matchmaking Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void findChallengeFirstArriver(Activity activity, String playerUid, String playerUsername, Quiz quiz) {
        getChallengesCollectionRef().add(new Challenge(quiz, playerUid, playerUsername)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<String>() {
                    @Override
                    public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(Matchmaking.getMatchmakerReference());
                        if (snapshot.exists()) {
                            if (TextUtils.equals(snapshot.getString("value"), "")) {
                                transaction.update(Matchmaking.getMatchmakerReference(), "value", documentReference.getId());
                                getChallengesCollectionRef().document(documentReference.getId()).update("challengeId", documentReference.getId());
                                return null;
                            } else {
                                throw new FirebaseFirestoreException("Errore matchmaking", FirebaseFirestoreException.Code.ABORTED);
                            }
                        }
                        return null;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        documentReference.delete();
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void findChallengeSecondArriver(Activity activity, String matchmaker, String playerUid, String playerUsername, Quiz quiz) {
        Matchmaking.getChallengesCollectionRef().document(matchmaker).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, String> players = (Map<String, String>) documentSnapshot.get("players");

                if (documentSnapshot.exists()) {
                    if (players != null && !players.containsKey(playerUid)) {
                        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<String>() {
                            @Override
                            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot matchmakerSnapshot = transaction.get(Matchmaking.getMatchmakerReference());
                                DocumentSnapshot challengeSnapshot = transaction.get(Matchmaking.getChallengesCollectionRef().document(matchmaker));
                                if (TextUtils.equals(matchmakerSnapshot.getString("value"), matchmaker)) {
                                    Challenge updatedChallenge = challengeSnapshot.toObject(Challenge.class);
                                    if (updatedChallenge != null) {
                                        updatedChallenge.addPlayer(playerUid, playerUsername);
                                        updatedChallenge.setChallengeState(ChallengeState.READY);

                                        transaction.update(Matchmaking.getMatchmakerReference(), "value", "");
                                        transaction.set(Matchmaking.getChallengesCollectionRef().document(matchmaker), updatedChallenge);
                                        return "Nuovo sfidante trovato: " + updatedChallenge.getChallengerUsername(playerUsername);
                                    } else
                                        throw new FirebaseFirestoreException("Errore matchmaking", FirebaseFirestoreException.Code.ABORTED);
                                } else
                                    throw new FirebaseFirestoreException("Errore matchmaking", FirebaseFirestoreException.Code.ABORTED);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Challenge not found, empty matchmaker value
                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<String>() {
                        @Override
                        public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(Matchmaking.getMatchmakerReference());
                            if (TextUtils.equals(snapshot.getString("value"), matchmaker)) {
                                transaction.update(Matchmaking.getMatchmakerReference(), "value", "");
                                return null;
                            } else
                                throw new FirebaseFirestoreException("Errore matchmaking", FirebaseFirestoreException.Code.ABORTED);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            findChallenge(activity, playerUid, playerUsername, quiz);
                        }
                    });
                }
            }
        });
    }
}
