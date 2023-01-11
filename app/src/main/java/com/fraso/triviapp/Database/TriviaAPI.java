package com.fraso.triviapp.Database;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fraso.triviapp.Difficulty;
import com.fraso.triviapp.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TriviaAPI {
    private static final String triviaApiUrl = "https://the-trivia-api.com/api/questions";

    public static void getQuiz(String url,RequestQueue requestQueue, TriviaRequestListener<Question> listener) {
        ArrayList<Question> questions = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                JSONObject question = response.getJSONObject(i);
                                String id = question.getString("id");
                                String question_text = question.getString("question");
                                String correctAnswer = question.getString("correctAnswer");

                                JSONArray incorrectAnswersJSON = question.getJSONArray("incorrectAnswers");
                                List<String> incorrectAnswers = new ArrayList<String>();
                                for(int j = 0; j < incorrectAnswersJSON.length(); j++){
                                    String answer = incorrectAnswersJSON.getString(j);
                                    incorrectAnswers.add(answer);
                                }
                                questions.add(new Question(id, question_text, correctAnswer, incorrectAnswers));
                            }
                            listener.onRequestComplete(questions);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public static void getCategories(RequestQueue requestQueue, TriviaRequestListener<String> listener) {
        ArrayList<String> categories = new ArrayList<>();
        String categoriesUrl = "https://the-trivia-api.com/api/categories";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                categoriesUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> keys = response.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            try {
                                JSONArray categoryArray = response.getJSONArray(key);

                                for(int i=0;i<categoryArray.length();i++){
                                    String category = categoryArray.getString(i);
                                    categories.add(category);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onRequestComplete(categories);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getTags(RequestQueue requestQueue, TriviaRequestListener<String> listener) {
        ArrayList<String> tags = new ArrayList<>();
        String tagsUrl = "https://the-trivia-api.com/api/tags";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                tagsUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                String tag = response.getString(i);
                                tags.add(tag);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        listener.onRequestComplete(tags);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    public static String composeURL(int numberOfQuestions, Difficulty difficulty, ArrayList<String> categories) {
        String finalURL = triviaApiUrl;

        if(numberOfQuestions > 1) {
            finalURL += "?limit=" + String.valueOf(numberOfQuestions);
        } else finalURL += "?limit=1";


        if (difficulty != null  && difficulty != difficulty.ANY) {
            finalURL += "&difficulty=" + difficulty.toString();
        }

        if(categories != null && !categories.isEmpty()) {
            finalURL += "&categories=";
            for (int i = 0; i < categories.size(); i++) {
                finalURL += categories.get(i).toString() + ',';
            }
        }

        return finalURL;
    }
}
