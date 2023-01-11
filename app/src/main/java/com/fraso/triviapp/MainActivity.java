package com.fraso.triviapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fraso.triviapp.ViewModel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Snackbar snackbar;

    private boolean logoutOptionItemVisible;
    private boolean customQuizOptionItemVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getCurrentUserLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser == null) {
                    Utilities.insertFragment((AppCompatActivity) MainActivity.this, R.id.fragment_container_view, new LoginFragment(), LoginFragment.class.getSimpleName(), false);
                    logoutOptionItemVisible = false;
                    customQuizOptionItemVisible = false;
                }
                else if (firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().matches("")){
                    Utilities.insertFragment((AppCompatActivity) MainActivity.this, R.id.fragment_container_view, new UsernameFragment(), UsernameFragment.class.getSimpleName(), true);
                    logoutOptionItemVisible = true;
                    customQuizOptionItemVisible = false;
                } else {
                    Utilities.insertFragment((AppCompatActivity) MainActivity.this, R.id.fragment_container_view, new MainFragment(), MainFragment.class.getSimpleName(), true);
                    logoutOptionItemVisible = true;
                    customQuizOptionItemVisible = true;
                }
                invalidateOptionsMenu();
            }
        });

        snackbar = Snackbar.make(findViewById(R.id.fragment_container_view), "No Internet Available", Snackbar.LENGTH_INDEFINITE).setAction("Settings", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                snackbar.dismiss();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                snackbar.show();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem customQuizItem = menu.findItem(R.id.app_bar_custom_quiz);
        MenuItem logoutItem = menu.findItem(R.id.app_bar_logout);

        customQuizItem.setVisible(customQuizOptionItemVisible);
        logoutItem.setVisible(logoutOptionItemVisible);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.app_bar_logout) {
            new AlertDialog.Builder(this, R.style.AlertDialog)
                    .setTitle("Logout")
                    .setMessage("Sei sicuro di voler effettuare il logout?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            authViewModel.logOut();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.app_bar_custom_quiz) {
            Utilities.insertFragment(MainActivity.this, R.id.fragment_container_view, new CustomQuizFragment(), CustomQuizFragment.class.getSimpleName(), true);
        }

        return false;
    }

    private void registerNetworkCallback(Activity activity) {
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        }
    }

    private void unregisterNetworkCallback() {
        connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } else {
            snackbar.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerNetworkCallback(MainActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterNetworkCallback();
    }
}