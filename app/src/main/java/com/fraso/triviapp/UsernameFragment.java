package com.fraso.triviapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fraso.triviapp.ViewModel.AuthViewModel;

public class UsernameFragment extends Fragment {
    private EditText editTextUsername;
    private Button buttonDone;

    private AuthViewModel authViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextUsername = view.findViewById(R.id.fragment_username_textinput);
        buttonDone = view.findViewById(R.id.fragment_username_button_done);
        authViewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity()).get(AuthViewModel.class);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String username = editTextUsername.getText().toString();

                 if (username.matches("")) {
                     editTextUsername.setError("Devi inserire un username!");
                     return;
                 }
                 authViewModel.setUsername(username);
            }
        });
    }
}