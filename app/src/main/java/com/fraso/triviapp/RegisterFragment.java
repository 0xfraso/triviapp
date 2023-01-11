package com.fraso.triviapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fraso.triviapp.ViewModel.AuthViewModel;

public class RegisterFragment extends Fragment {
    private Activity activity;
    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword;
    private TextView loginTextView;

    private AuthViewModel authViewModel;

    public RegisterFragment() {}

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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonRegister = view.findViewById(R.id.register_activity_btn_register);
        editTextEmail = view.findViewById(R.id.register_activity_et_email);
        editTextPassword = view.findViewById(R.id.register_activity_et_password);
        loginTextView = view.findViewById(R.id.text_view_register_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(email.matches("")) {
                    editTextEmail.setError("Email necessaria!");
                    editTextEmail.requestFocus();
                    return;
                }
                if(password.matches("")) {
                    editTextPassword.setError("Password necessaria!");
                    editTextPassword.requestFocus();
                    return;
                }

                authViewModel.register(email, password);
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.insertFragment((AppCompatActivity) activity, R.id.fragment_container_view, new LoginFragment(), LoginFragment.class.getSimpleName(), true);
            }
        });
    }
}