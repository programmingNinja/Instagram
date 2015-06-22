package com.codepath.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.CommentsActivity;
import com.codepath.instagram.helpers.ShareIntent;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {
    private static final String TAG = "InstagramPostsAdapter";

    private static final int COMMENTS_TO_SHOW = 2;

    private List<InstagramPost> posts;

    public InstagramPostsAdapter(List<InstagramPost> posts) {
        this.posts = (posts == null ? new ArrayList<InstagramPost>() : posts);
    }

    @Override
    public PostItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        return new PostItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostItemViewHolder postItemViewHolder, int position) {
        final InstagramPost instagramPost = posts.get(position);

        if (instagramPost.caption != null) {
            postItemViewHolder.tvCaption.setText(
                    Utils.formatUserNameAndText(instagramPost.user.userName, instagramPost.caption));
        }

        postItemViewHolder.tvCaption.setVisibility(
                TextUtils.isEmpty(instagramPost.caption) ? View.GONE : View.VISIBLE);

        postItemViewHolder.tvUserName.setText(instagramPost.user.userName);
        postItemViewHolder.tvRelativeTimestamp.setText(
                DateUtils.getRelativeTimeSpanString(instagramPost.createdTime * 1000,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        int likesCount = instagramPost.likesCount;
        postItemViewHolder.tvLikes.setText(Utils.formatNumberForDisplay(likesCount) +
                " like" + (likesCount != 1 ? "s" : ""));
        postItemViewHolder.tvLikes.setVisibility(likesCount == 0 ? View.GONE : View.VISIBLE);

        // Reset image views
        postItemViewHolder.sdvPhoto.setImageURI(null);
        postItemViewHolder.sdvProfileImage.setImageURI(null);

        int width = instagramPost.image.imageWidth;
        int height = instagramPost.image.imageHeight;

        float aspectRatio = height > 0 ? (float)width / (float)height : 1;

        Uri imageUri = Uri.parse(instagramPost.image.imageUrl);
        postItemViewHolder.sdvPhoto.setImageURI(imageUri);
        postItemViewHolder.sdvPhoto.setAspectRatio(aspectRatio);

        Uri profilePictureUri = Uri.parse(instagramPost.user.profilePictureUrl);
        postItemViewHolder.sdvProfileImage.setImageURI(profilePictureUri);

        // Only show the "View All" if there are more than 2 comments
        boolean shouldShowViewAll = instagramPost.commentsCount > COMMENTS_TO_SHOW;

        postItemViewHolder.tvViewAll.setVisibility(shouldShowViewAll ? View.VISIBLE : View.GONE);
        postItemViewHolder.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navToCommentsActivity(v, instagramPost);
            }
        });

        if (shouldShowViewAll) {
            postItemViewHolder.tvViewAll.setText(String.format("view all %d comments", instagramPost.commentsCount));
        }

        boolean shouldShowComments = instagramPost.commentsCount > 0;
        postItemViewHolder.llComments.setVisibility(shouldShowComments ? View.VISIBLE : View.GONE);
        if (shouldShowComments) {
            populateComments(postItemViewHolder.llComments, instagramPost.comments);
        }

        postItemViewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShareButtonPress(v, instagramPost);
            }
        });
    }

    private void populateComments(LinearLayout llContainer, List<InstagramComment> comments) {
        llContainer.removeAllViews();

        int commentsCount = comments.size();
        int initialCommentIndex = Math.max(0, commentsCount - COMMENTS_TO_SHOW);

        Context context = llContainer.getContext();

        for (int i = initialCommentIndex; i < commentsCount; i++) {
            InstagramComment comment = comments.get(i);

            TextView textView = new TextView(context);
            textView.setText(Utils.formatUserNameAndText(comment.user.userName, comment.text));
            textView.setTextSize(14);
            textView.setLineSpacing(0.0f, 1.2f);
            textView.setBackgroundColor(Color.TRANSPARENT);

            llContainer.addView(textView);
        }
    }

    private void handleShareButtonPress(final View btnShare, final InstagramPost post) {
        Context context = btnShare.getContext();
        PopupMenu popup = new PopupMenu(context, btnShare);
        popup.getMenuInflater().inflate(R.menu.menu_share, popup.getMenu());
        popup.setOnMenuItemClickListener(new SharePopupMenuClickListener(context, post));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    private void navToCommentsActivity(View fromView, InstagramPost post) {
        Context context = fromView.getContext();
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(CommentsActivity.EXTRA_MEDIA_ID, post.mediaId);
        context.startActivity(intent);
    }

    public static final class PostItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvRelativeTimestamp;

        SimpleDraweeView sdvProfileImage;
        SimpleDraweeView sdvPhoto;

        TextView tvCaption;
        TextView tvLikes;

        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnShare;

        Button tvViewAll;
        LinearLayout llComments;

        public PostItemViewHolder(View itemView) {
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvRelativeTimestamp = (TextView) itemView.findViewById(R.id.tvRelativeTimestamp);

            sdvProfileImage = (SimpleDraweeView) itemView.findViewById(R.id.sdvProfileImage);
            sdvPhoto = (SimpleDraweeView) itemView.findViewById(R.id.sdvPhoto);

            tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
            tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);

            btnLike = (ImageButton)itemView.findViewById(R.id.btnLike);
            btnComment = (ImageButton)itemView.findViewById(R.id.btnComment);
            btnShare = (ImageButton)itemView.findViewById(R.id.btnShare);

            tvViewAll = (Button)itemView.findViewById(R.id.btnViewAll);
            llComments = (LinearLayout)itemView.findViewById(R.id.llComments);
        }
    }

    /**
     * Custom click listener to keep track of which post we popped up a menu for
     */
    private class SharePopupMenuClickListener implements android.widget.PopupMenu.OnMenuItemClickListener {

        private InstagramPost tappedPost;
        private Context context;

        public SharePopupMenuClickListener(Context context, InstagramPost post) {
            this.tappedPost = post;
            this.context = context;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_share_photo:
                    shareFrescoBitmap();
                    return true;
                default:
                    return false;
            }
        }

        public void shareFrescoBitmap() {
            Uri imageUri = Uri.parse(tappedPost.image.imageUrl);

            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(imageUri)
                    .setRequestPriority(Priority.HIGH)
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();

            DataSource<CloseableReference<CloseableImage>> dataSource =
                    imagePipeline.fetchDecodedImage(imageRequest, this);
            try {
                dataSource.subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    public void onNewResultImpl(@Nullable Bitmap bitmap) {
                        if (bitmap == null) {
                            Log.wtf(TAG, "Bitmap data source returned success, but bitmap null");
                            return;
                        }

                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                bitmap, "Instagram Post", null);

                        Uri uri = Uri.parse(path);
                        Intent intent = ShareIntent.getImageIntent(uri);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailureImpl(DataSource dataSource) {
                        Log.wtf(TAG, "Failure while trying to get bitmap from Fresco");
                    }
                }, CallerThreadExecutor.getInstance());
            } finally {
                dataSource.close();
            }
        }
    }
}
