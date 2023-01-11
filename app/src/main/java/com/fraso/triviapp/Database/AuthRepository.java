package com.fraso.triviapp.Database;

import android.app.Application;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthRepository {
    private final Application application;

    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> currentUserLiveData;

    public AuthRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.currentUserLiveData = new MutableLiveData<>();

        this.firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUserLiveData.postValue(firebaseAuth.getCurrentUser());
            }
        });
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(application, "Login avvenuto con successo", Toast.LENGTH_SHORT).show();
                    currentUserLiveData.postValue(firebaseAuth.getCurrentUser());
                } else {
                    Toast.makeText(application, "Login fallito: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setUsername(String username) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    currentUserLiveData.postValue(firebaseAuth.getCurrentUser());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    firebaseAuth.getCurrentUser().delete();
                    Toast.makeText(application, "Errore registrazione utente" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(application, "Errore registrazione username", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (authResult.getUser() != null) {
                    Toast.makeText(application, "Utente creato con successo", Toast.LENGTH_SHORT).show();
                    currentUserLiveData.postValue(firebaseAuth.getCurrentUser());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, "Errore registrazione utente" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logOut() {
        firebaseAuth.signOut();
    }

    public MutableLiveData<FirebaseUser> getCurrentUserLiveData() {
        return this.currentUserLiveData;
    }
}