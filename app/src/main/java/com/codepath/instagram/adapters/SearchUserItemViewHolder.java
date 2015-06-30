package com.codepath.instagram.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codepath.instagram.R;
import com.facebook.drawee.view.SimpleDraweeView;

public final class SearchUserItemViewHolder extends RecyclerView.ViewHolder {

    TextView tvUserName, tvFullName;
    RelativeLayout rlContainer;
    SimpleDraweeView sdvProfileImage;

    public SearchUserItemViewHolder(View itemView) {
        super(itemView);
        rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        tvFullName = (TextView) itemView.findViewById(R.id.tvFullName);
        sdvProfileImage = (SimpleDraweeView)itemView.findViewById(R.id.sdvProfileImage);
    }
}