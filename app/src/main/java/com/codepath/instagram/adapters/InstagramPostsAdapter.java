package com.codepath.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.codepath.instagram.R;
import com.codepath.instagram.activities.CommentsActivity;
import com.codepath.instagram.activities.ProfileActivity;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.ShareIntent;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramUser;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {

    private static final String TAG = InstagramPostsAdapter.class.getSimpleName();
    private List<InstagramPost> mSamplePostList;
    private Context mContext;

    private static final int MAX_COMMENTS_TO_SHOW = 2;

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

    private void configureView(final PostItemViewHolder postItemViewHolder, final InstagramPost instagramPost) {

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

        postItemViewHolder.sdvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(instagramPost.user);
            }
        });

        // Only show the "View All" if there are more than 2 comments
        boolean shouldShowViewAll = instagramPost.commentsCount > MAX_COMMENTS_TO_SHOW;

        postItemViewHolder.tvViewAll.setVisibility(shouldShowViewAll ? View.VISIBLE : View.GONE);

        if (shouldShowViewAll) {
            postItemViewHolder.tvViewAll.setText(mContext.getString(R.string.view_comments_text, instagramPost.commentsCount));
        }

        postItemViewHolder.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommentsActivity(instagramPost);
            }
        });

        postItemViewHolder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommentsActivity(instagramPost);
            }
        });

        postItemViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLikePostCall(postItemViewHolder.ivLike, instagramPost);
            }
        });

        boolean shouldShowComments = instagramPost.commentsCount > 0;
        postItemViewHolder.llComments.setVisibility(shouldShowComments ? View.VISIBLE : View.GONE);
        if (shouldShowComments) {
            populateComments(postItemViewHolder.llComments, instagramPost.comments);
        }

        postItemViewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShareButtonPress(v, instagramPost);
            }
        });
    }

    private void doLikePostCall(final ImageView ivLike, InstagramPost instagramPost) {
        if (Utils.isNetworkAvailable(mContext)) {
            MainApplication.getRestClient().postLikeMedia(instagramPost.mediaId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    changeResource(ivLike);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Could not like post error - " + statusCode + " - " + errorResponse.toString() );
                    Utils.showErrorMsg(mContext);
                    changeResource(ivLike);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String msg, Throwable throwable){
                    Log.e(TAG, "Could not like post error - " + statusCode + " - " + msg );
                    Utils.showErrorMsg(mContext);
                    changeResource(ivLike);
                }
            });
        } else {
            Utils.showErrorMsg(mContext, mContext.getString(R.string.err_no_internet));
        }
    }

    //TODO - Temporarily calling this method even onFailure because the current instagram
    //TODO-  client does not have permission to post new resources. Need to remove the method calls from onFailure()
    private void changeResource(ImageView ivLike) {
        ivLike.setImageResource(R.drawable.ic_heart_liked);
    }

    private void startProfileActivity(InstagramUser user) {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_OBJECT, user);
        mContext.startActivity(intent);
    }

    private void populateComments(LinearLayout llContainer, List<InstagramComment> comments) {
        llContainer.removeAllViews();

        int commentsCount = comments.size();
        int initialCommentIndex = Math.max(0, commentsCount - MAX_COMMENTS_TO_SHOW);

        for (int i = initialCommentIndex; i < commentsCount; i++) {
            InstagramComment comment = comments.get(i);
            View commentView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_comment, llContainer, false);
            TextView textView = (TextView) commentView.findViewById(R.id.tvComment);
            textView.setText(Utils.formatCaptionText(mContext, comment.user.userName, comment.text));
            llContainer.addView(textView);
        }
    }

    private void startCommentsActivity(InstagramPost instagramPost) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra(CommentsActivity.KEY_MEDIA_ID, instagramPost.mediaId);
        mContext.startActivity(intent);
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

        TextView tvUserName, tvRelativeTimestamp, tvCaption, tvLikes, tvViewAll;
        SimpleDraweeView sdvProfileImage, sdvPhoto;
        LinearLayout llComments;
        ImageView ivLike, ivShare, ivComment;

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
            tvViewAll = (TextView) itemView.findViewById(R.id.tvViewAll);
            llComments = (LinearLayout)itemView.findViewById(R.id.llComments);
            ivLike = (ImageView)itemView.findViewById(R.id.ivLike);
            ivShare = (ImageView)itemView.findViewById(R.id.ivShare);
            ivComment = (ImageView)itemView.findViewById(R.id.ivComment);
        }
    }

    private void handleShareButtonPress(View ivShare, InstagramPost instagramPost) {
        PopupMenu popup = new PopupMenu(ivShare.getContext(), ivShare);
        popup.getMenuInflater().inflate(R.menu.menu_share, popup.getMenu());
        popup.setOnMenuItemClickListener(new SharePopupMenuClickListener(ivShare.getContext(), instagramPost));
        popup.show();
    }

    /**
     * Custom click listener to keep track of which post we popped up a menu for
     */
    private class SharePopupMenuClickListener implements android.widget.PopupMenu.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {

        private final String TAG = SharePopupMenuClickListener.class.getSimpleName();
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
