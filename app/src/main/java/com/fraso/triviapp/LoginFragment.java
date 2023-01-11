package com.fraso.triviapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fraso.triviapp.ViewModel.AuthViewModel;

public class LoginFragment extends Fragment {
    private Button buttonLogin;
    private EditText editTextEmail, editTextPassword;
    private TextView registerTextView;

    private AuthViewModel authViewModel;

    public LoginFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();

        buttonLogin = view.findViewById(R.id.login_activity_btn_register);
        editTextEmail = view.findViewById(R.id.login_activity_et_email);
        editTextPassword = view.findViewById(R.id.login_activity_et_password);
        registerTextView = view.findViewById(R.id.text_view_login_register);

        if (activity != null) {
            authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();


                    if(email.matches("")) {
                        editTextEmail.setError("Email necessaria!");
                        return;
                    }
                    if(password.matches("")) {
                        editTextPassword.setError("Password necessaria!");
                        return;
                    }

                    authViewModel.login(email, password);
                }
            });

            registerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utilities.insertFragment((AppCompatActivity) activity, R.id.fragment_container_view, new RegisterFragment(), RegisterFragment.class.getSimpleName(), true);
                }
            });
        }
    }
}