package com.fraso.triviapp.RecyclerView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.fraso.triviapp.Challenge;

import java.util.List;

/**
 * Utility class to compare two List<Challenge>
 *     Android suggests the use of DiffUtil.Callback to improve the performance of the RecyclerView
 *     Each time the list needs to be updated, only the changed items will be reloaded.
 */
public class ChallengeDiffCallback extends DiffUtil.Callback {

    private final List<Challenge> oldChallengeList;
    private final List<Challenge> newChallengeList;

    /**
     * Constructor that takes the two lists
     * @param oldChallengeList the old list already displayed
     * @param newChallengeList the new list to display
     */
    public ChallengeDiffCallback(List<Challenge> oldChallengeList, List<Challenge> newChallengeList) {
        this.oldChallengeList = oldChallengeList;
        this.newChallengeList = newChallengeList;
    }

    @Override
    public int getOldListSize() {
        return oldChallengeList.size();
    }

    @Override
    public int getNewListSize() {
        return newChallengeList.size();
    }

    /**
     *
     * @param oldItemPosition position of the old item
     * @param newItemPosition position of the new item
     * @return true if the two items are the same
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldChallengeList.get(oldItemPosition) == newChallengeList.get(newItemPosition);
    }

    /**
     *
     * @param oldItemPosition position of the old item
     * @param newItemPosition position of the new item
     * @return true if the two item have the same content
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Challenge oldItem = oldChallengeList.get(oldItemPosition);
        final Challenge newItem = newChallengeList.get(newItemPosition);
        return oldItem.getChallengeState().equals(newItem.getChallengeState()) &&
                oldItem.getPlayers().equals(newItem.getPlayers()) &&
                oldItem.getPlayersScores().equals(newItem.getPlayersScores()) &&
                oldItem.getQuiz().equals(newItem.getQuiz());
    }

    /**
     * When areItemsTheSame(int, int) returns true for two items and
     * areContentsTheSame(int, int) returns false for the two items,
     * this method is called to get a payload about the change.
     * @param oldItemPosition position of the old item
     * @param newItemPosition position of the new item
     * @return an Object (it could be a bundle) that contains the changes (the only change in this case
     * id the content of the item to handle in the Adapter in the overridden method
     * onBindViewHolder(CryptoViewHolder holder, int position, List<Object> payloads)).
     *
     * It returns null by default.
     */
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
