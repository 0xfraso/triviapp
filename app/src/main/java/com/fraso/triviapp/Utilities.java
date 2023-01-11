package com.fraso.triviapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class Utilities {
    public static final int QUIZ_RESULT_CODE = 0;

    public static void insertFragment(AppCompatActivity activity, int fragmentId, Fragment fragment, String tag, boolean animated) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (animated) {
            transaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
            );
        }

        // Replace whatever is in the fragment_container_view with this fragment
        transaction.replace(fragmentId, fragment, tag);

        //add the transaction to the back stack so the user can navigate back except for the first fragment of the stack
        if (!(fragment instanceof MainFragment || fragment instanceof LoginFragment || fragment instanceof UsernameFragment)) {
            transaction.addToBackStack(tag);
        }

        // Commit the transaction
        transaction.commit();
    }
}
