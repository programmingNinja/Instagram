package com.codepath.instagram.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codepath.instagram.R;

public final class SearchTagsItemViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlContainer;
    public TextView tvTag;
    public TextView tvPostCount;

    public SearchTagsItemViewHolder(View itemView) {
        super(itemView);
        rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
        tvTag = (TextView) itemView.findViewById(R.id.tvTag);
        tvPostCount = (TextView) itemView.findViewById(R.id.tvPostCount);
    }
}