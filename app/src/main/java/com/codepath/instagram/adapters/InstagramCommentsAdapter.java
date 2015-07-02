package com.codepath.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codepath.instagram.R;
import com.codepath.instagram.activities.ProfileActivity;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class InstagramCommentsAdapter extends RecyclerView.Adapter<InstagramCommentsAdapter.CommentItemViewHolder> {

    private static final String TAG = InstagramCommentsAdapter.class.getSimpleName();
    private List<InstagramComment> comments;
    private Context mContext;

    public InstagramCommentsAdapter(List<InstagramComment> commentList, Context context) {
        this.comments = (commentList == null ? new ArrayList<InstagramComment>() : commentList);
        this.mContext = context;
    }

    @Override
    public CommentItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_detail_comment, viewGroup, false);
        return new CommentItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentItemViewHolder holder, int position) {
        final InstagramComment comment = comments.get(position);
        holder.sdvProfileImage.setImageURI(Uri.parse(comment.user.profilePictureUrl));
        holder.sdvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(comment.user);
            }
        });
        holder.tvComment.setText(Utils.formatCaptionText(mContext, comment.user.userName, comment.text));
        holder.tvRelativeTimestamp.setText(DateUtils.getRelativeTimeSpanString(comment.createdTime * 1000,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    public static final class CommentItemViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdvProfileImage;
        TextView tvComment, tvRelativeTimestamp;

        public CommentItemViewHolder(View itemView) {
            super(itemView);
            sdvProfileImage = (SimpleDraweeView)itemView.findViewById(R.id.sdvProfileImage);
            tvComment = (TextView)itemView.findViewById(R.id.tvComment);
            tvRelativeTimestamp = (TextView) itemView.findViewById(R.id.tvRelativeTimestamp);
        }
    }

    private void startProfileActivity(InstagramUser user) {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_OBJECT, user);
        mContext.startActivity(intent);
    }
}
