package org.stormroboticsnj.scoutingradar2022.homefragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.databinding.FragmentMatchRecordBinding;

/**
 * {@link RecyclerView.Adapter} that can display an {@link ObjectiveMatchData}.
 */
public class MatchRecordRecyclerViewAdapter extends ListAdapter<ObjectiveMatchData, MatchRecordRecyclerViewAdapter.MatchRecordViewHolder> {
    protected MatchRecordRecyclerViewAdapter(
            @NonNull
                    DiffUtil.ItemCallback<ObjectiveMatchData> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public MatchRecordViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new MatchRecordViewHolder(
                FragmentMatchRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false));
    }

    @Override
    public void onBindViewHolder(
            @NonNull MatchRecordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public static class MatchRecordViewHolder extends RecyclerView.ViewHolder {
        public final TextView mMatchNumView;
        public final TextView mTeamNumView;
        ObjectiveMatchData mItem;

        public MatchRecordViewHolder(FragmentMatchRecordBinding binding) {
            super(binding.getRoot());
            mMatchNumView = binding.recordTextMatchNumber;
            mTeamNumView = binding.recordTextTeamNumber;
        }

        public void bind(ObjectiveMatchData item) {
            mItem = item;
            mMatchNumView.setText(mMatchNumView.getContext().getResources().getString(R.string.record_text_match, item.getMatchNum()));
            mTeamNumView.setText(mTeamNumView.getContext().getResources().getString(R.string.record_text_team, item.getTeamNum()));
        }

        @Override
        @NonNull
        public String toString() {
            return super.toString() + " '" + mTeamNumView.getText() + "'";
        }
    }

    public static class RecordDiff extends DiffUtil.ItemCallback<ObjectiveMatchData> {
        @Override
        public boolean areItemsTheSame(
                @NonNull ObjectiveMatchData oldItem, @NonNull ObjectiveMatchData newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull ObjectiveMatchData oldItem, @NonNull ObjectiveMatchData newItem) {
            return (oldItem.getMatchNum() == newItem.getMatchNum() && oldItem.getTeamNum() ==
                                                                      newItem.getTeamNum());
        }
    }
}