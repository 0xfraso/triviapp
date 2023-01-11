package com.fraso.triviapp.Database;

import java.util.ArrayList;

// listener used for updating UI elements after the request has finished
public interface TriviaRequestListener<T> {
    void onRequestComplete(ArrayList<T> list);
}
