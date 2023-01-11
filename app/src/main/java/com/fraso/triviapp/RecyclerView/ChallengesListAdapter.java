package com.fraso.triviapp.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fraso.triviapp.Challenge;
import com.fraso.triviapp.ChallengeState;
import com.fraso.triviapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChallengesListAdapter extends RecyclerView.Adapter<ChallengesListAdapter.ChallengeViewHolder> {
    private final OnItemClickListener itemClickListener;
    private List<Challenge> challengesList;

    public ChallengesListAdapter(List<Challenge> challengesList, OnItemClickListener itemClickListener) {
        this.challengesList = challengesList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return this.challengesList.size();
    }

    public void setData(List<Challenge> list){
        final ChallengeDiffCallback diffCallback =
                new ChallengeDiffCallback(this.challengesList, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.challengesList = new ArrayList<>(list);

        diffResult.dispatchUpdatesTo(this);
    }


    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenges_item_layout, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        String challenger = challengesList.get(position).getChallengerUsername(FirebaseAuth.getInstance().getCurrentUser().getUid());
        holder.challengeIdTextView.setText(challenger);

        if (challengesList.get(position).getChallengeState() == ChallengeState.WAITING) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.challengeIdTextView.setText(R.string.waiting);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }
    }

    class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView challengeIdTextView;
        ProgressBar progressBar;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            challengeIdTextView = itemView.findViewById(R.id.challenges_recycler_player_name);
            progressBar = itemView.findViewById(R.id.challenges_recycler_progressbar);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(challengesList.get(getAdapterPosition()));
        }
    }
}
