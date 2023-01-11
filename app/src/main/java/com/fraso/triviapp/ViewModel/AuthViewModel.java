package com.fraso.triviapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fraso.triviapp.Database.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<FirebaseUser> currentUserLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        currentUserLiveData = authRepository.getCurrentUserLiveData();
    }

    public MutableLiveData<FirebaseUser> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void register(String email, String password) {
        authRepository.register(email, password);
    }

    public void setUsername(String username) {
        authRepository.setUsername(username);
    }

    public void logOut() {
        authRepository.logOut();
    }
}