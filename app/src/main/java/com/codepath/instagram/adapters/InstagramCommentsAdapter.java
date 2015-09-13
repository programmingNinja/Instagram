package com.codepath.instagram.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class InstagramCommentsAdapter
        extends RecyclerView.Adapter<InstagramCommentsAdapter.CommentItemViewHolder> {
  private static final String TAG = "InstagramCommentsAdapter";
  private List<InstagramComment> comments;

  public InstagramCommentsAdapter(List<InstagramComment> comments) {
    this.comments = (comments == null ? new ArrayList<InstagramComment>() : comments);
  }

  @Override
  public CommentItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View itemView = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.item_comment, viewGroup, false);
    return new CommentItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(CommentItemViewHolder holder, int position) {
    InstagramComment comment = comments.get(position);

    holder.sdvProfileImage.setImageURI(null);

    Uri profilePictureUri = Uri.parse(comment.user.profilePictureUrl);
    holder.sdvProfileImage.setImageURI(profilePictureUri);
    holder.tvComment.setText(Utils.formatUserNameAndText(comment.user.userName, comment.text));
    holder.tvRelativeTimestamp.setText(
            DateUtils.getRelativeTimeSpanString(comment.createdTime * 1000,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
  }

  @Override
  public int getItemCount() {
    return comments == null ? 0 : comments.size();
  }

  public static final class CommentItemViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView sdvProfileImage;
    TextView tvComment;
    TextView tvRelativeTimestamp;

    public CommentItemViewHolder(View itemView) {
      super(itemView);

      sdvProfileImage = (SimpleDraweeView) itemView.findViewById(R.id.sdvProfileImage);
      tvComment = (TextView) itemView.findViewById(R.id.tvComment);
      tvRelativeTimestamp = (TextView) itemView.findViewById(R.id.tvRelativeTimestamp);
    }
  }
}
