package com.codepath.instagram.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {

    private List<InstagramPost> mSamplePostList;
    private Context mContext;

    public InstagramPostsAdapter(List<InstagramPost> samplePostList, Context context) {
        this.mSamplePostList = (samplePostList == null) ? new ArrayList<InstagramPost>() : samplePostList;
        this.mContext = context;
    }

    @Override
    public PostItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.layout_item_post, viewGroup, false);
        return new InstagramPostsAdapter.PostItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostItemViewHolder postItemViewHolder, int position) {
        final InstagramPost instagramPost = mSamplePostList.get(position);
        configureView(postItemViewHolder, instagramPost);
    }

    private void configureView(PostItemViewHolder postItemViewHolder, InstagramPost instagramPost) {

        postItemViewHolder.tvCaption.setText(Utils.formatCaptionText(mContext,
                instagramPost.user.userName, instagramPost.caption));
        postItemViewHolder.tvCaption.setVisibility(TextUtils.isEmpty(instagramPost.caption) ? View.GONE : View.VISIBLE);

        postItemViewHolder.tvUserName.setText(instagramPost.user.userName);
        postItemViewHolder.tvRelativeTimestamp.setText(
                DateUtils.getRelativeTimeSpanString(instagramPost.createdTime * 1000,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        int likesCount = instagramPost.likesCount;
        postItemViewHolder.tvLikes.setText(Utils.getLikesString(likesCount));
        postItemViewHolder.tvLikes.setVisibility(likesCount == 0 ? View.GONE : View.VISIBLE);

        postItemViewHolder.sdvPhoto.setImageURI(Uri.parse(instagramPost.image.imageUrl));
        postItemViewHolder.sdvPhoto.setAspectRatio(calculateImageAspectRatio(instagramPost));
        postItemViewHolder.sdvProfileImage.setImageURI(Uri.parse(instagramPost.user.profilePictureUrl));
    }

    private float calculateImageAspectRatio(InstagramPost instagramPost) {
        int width = instagramPost.image.imageWidth;
        int height = instagramPost.image.imageHeight;
        return height > 0 ? (float)width / (float)height : 1;
    }

    @Override
    public int getItemCount() {
        return mSamplePostList.size();
    }

    public static final class PostItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvRelativeTimestamp, tvCaption, tvLikes;
        SimpleDraweeView sdvProfileImage, sdvPhoto;

        public PostItemViewHolder(View itemView) {
            super(itemView);
            initUi(itemView);
        }

        private void initUi(View itemView) {
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvRelativeTimestamp = (TextView) itemView.findViewById(R.id.tvRelativeTimestamp);
            sdvProfileImage = (SimpleDraweeView) itemView.findViewById(R.id.sdvProfileImage);
            sdvPhoto = (SimpleDraweeView) itemView.findViewById(R.id.sdvPhoto);
            tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
            tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
        }
    }
}
