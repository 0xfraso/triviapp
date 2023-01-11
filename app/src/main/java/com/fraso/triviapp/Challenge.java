package com.fraso.triviapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Challenge implements Serializable {
    private String challengeId;
    private Quiz quiz;
    private ChallengeState challengeState;

    private Map<String, String> players = new HashMap<>(2);
    private Map<String, Integer> playersScores = new HashMap<String, Integer>(2);

    public Challenge() {
    }

    public Challenge(Quiz quiz, String playerUid, String playerUsername) {
        this.quiz = quiz;
        this.addPlayer(playerUid, playerUsername);
        this.challengeState = ChallengeState.WAITING;
    }

    public void addPlayer(String playerUid, String playerUsername) {
        if (this.players.size() < 2) this.players.put(playerUid, playerUsername);
    }

    public void addPlayerScore(String playerUid, int score) {
        this.playersScores.put(playerUid, score);
        getChallengeState();
    }

    public String getChallengerUsername(String playerUid) {
        for (Map.Entry<String, String> entry : this.players.entrySet())
            if (!entry.getKey().equals(playerUid)) return entry.getValue();
        return null;
    }

    public String getChallengerUid(String playerUid) {
        for (Map.Entry<String, String> entry : this.players.entrySet())
            if (!entry.getKey().equals(playerUid)) return entry.getKey();
        return null;
    }

    public Map<String, Integer> getPlayersScores() {
        return this.playersScores;
    }

    public Map<String, String> getPlayers() {
        return this.players;
    }

    public int getPlayerScore(String playerUid) {
        for (Map.Entry<String, Integer> entry : this.playersScores.entrySet())
            if (entry.getKey().equals(playerUid)) return entry.getValue();
        return -1;
    }

    public boolean playerHasScore(String playerUid) {
        return this.playersScores.containsKey(playerUid);
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

    public ChallengeState getChallengeState() {
        if (playersScores.size() > 1) setChallengeState(ChallengeState.COMPLETED);
        return challengeState;
    }

    public void setChallengeState(ChallengeState challengeState) {
        this.challengeState = challengeState;
    }

    public String getChallengeId() {
        return this.challengeId;
    }
}
