package com.codepath.instagram.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.PhotoGridActivity;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class SearchUserResultsAdapter extends RecyclerView.Adapter<SearchUserResultsAdapter.SearchUserItemViewHolder> {
    private static final String TAG = "SearchUserResultsAdapter";

    protected List<InstagramUser> users;

    public SearchUserResultsAdapter() {
        this.users = new ArrayList<>();
    }

    public void replaceAll(List<InstagramUser> users) {
        this.users.clear();
        for (InstagramUser user : users) {
            this.users.add(user);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public SearchUserResultsAdapter.SearchUserItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_user_result, viewGroup, false);
        return new SearchUserItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchUserResultsAdapter.SearchUserItemViewHolder holder, int position) {
        holder.sdvProfileImage.setImageURI(null);

        final InstagramUser user = users.get(position);

        holder.tvUserName.setText(user.userName);
        holder.tvFullName.setText(user.fullName);

        Uri profilePictureUri = Uri.parse(user.profilePictureUrl);
        holder.sdvProfileImage.setImageURI(profilePictureUri);
    }

    @Override
    public int getItemCount() {
        return this.users == null ? 0 : this.users.size();
    }

    public final class SearchUserItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvUserName;
        TextView tvFullName;
        SimpleDraweeView sdvProfileImage;

        public SearchUserItemViewHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvFullName = (TextView) itemView.findViewById(R.id.tvFullName);
            sdvProfileImage = (SimpleDraweeView)itemView.findViewById(R.id.sdvProfileImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            InstagramUser user = users.get(getPosition());
            navToPhotoGridActivity(view, user.userId);
        }
    }

    private static void navToPhotoGridActivity(View fromView, String userId) {
        Intent intent = new Intent(fromView.getContext(), PhotoGridActivity.class);
        intent.putExtra(PhotoGridActivity.EXTRA_USER_ID, userId);
        fromView.getContext().startActivity(intent);
    }
}
