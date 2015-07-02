package com.codepath.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codepath.instagram.R;
import com.codepath.instagram.activities.PhotosGridActivity;
import com.codepath.instagram.activities.ProfileActivity;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;
import com.codepath.instagram.models.InstagramUser;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    public enum SearchType {

        SEARCH_USERS(0), SEARCH_TAGS(1);

        private int itemType;

        SearchType(int i) {
            this.itemType = i;
        }

        public int getItemType() {
            return itemType;
        }
    }

    private static final String TAG = SearchResultsAdapter.class.getSimpleName();

    protected List<InstagramSearchTag> searchTagsList;
    protected List<InstagramUser> searchUsersList;
    private SearchType mSearchType;

    public SearchResultsAdapter(SearchType searchType, Context context) {
        this.mSearchType = searchType;
        this.mContext = context;
        initList();
    }

    private void initList() {
        if (mSearchType == SearchType.SEARCH_TAGS) {
            this.searchTagsList = new ArrayList<>();
        } else {
            this.searchUsersList = new ArrayList<>();
        }
    }

    public void replaceAllTags(List<InstagramSearchTag> searchResultList) {
        this.searchTagsList.clear();
        for (InstagramSearchTag tag : searchResultList) {
            this.searchTagsList.add(tag);
        }
        this.notifyDataSetChanged();
    }

    public void replaceAllUsers(List<InstagramUser> searchResultList) {
        this.searchUsersList.clear();
        for (InstagramUser user : searchResultList) {
            this.searchUsersList.add(user);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case 0:
                View v1 = inflater.inflate(R.layout.layout_item_user_result, viewGroup, false);
                viewHolder = new SearchUserItemViewHolder(v1);
                break;
            case 1:
                View v2 = inflater.inflate(R.layout.layout_item_search_tags, viewGroup, false);
                viewHolder = new SearchTagsItemViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mSearchType == SearchType.SEARCH_TAGS) {
            final InstagramSearchTag tag = searchTagsList.get(position);
            configureTagView((SearchTagsItemViewHolder) holder, tag);
        } else {
            final InstagramUser user = searchUsersList.get(position);
            configureUserView((SearchUserItemViewHolder) holder, user);
        }
    }

    private void configureUserView(SearchUserItemViewHolder holder, final InstagramUser user) {
        holder.tvUserName.setText(user.userName);
        holder.tvFullName.setText(user.fullName);
        Uri profilePictureUri = Uri.parse(user.profilePictureUrl);
        holder.sdvProfileImage.setImageURI(profilePictureUri);
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(user);
            }
        });
    }

    private void startProfileActivity(InstagramUser user) {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_OBJECT, user);
        mContext.startActivity(intent);
    }

    private void configureTagView(SearchTagsItemViewHolder holder, final InstagramSearchTag tag) {
        Resources resources = mContext.getResources();
        holder.tvTag.setText("#" + tag.tag);
        String postFix = tag.count == 1 ? resources.getString(R.string.tag_count_postfix) :
                resources.getString(R.string.tag_count_postfix_plural);
        holder.tvPostCount.setText(Utils.formatNumberForDisplay(tag.count) + " " + postFix);
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navToPhotoGridActivity(tag.tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mSearchType == SearchType.SEARCH_TAGS) {
            return this.searchTagsList == null ? 0 : this.searchTagsList.size();
        } else if (mSearchType == SearchType.SEARCH_USERS) {
            return this.searchUsersList == null ? 0 : this.searchUsersList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mSearchType.getItemType();
    }

    private void navToPhotoGridActivity(String searchTag) {
        Intent intent = new Intent(mContext, PhotosGridActivity.class);
        String key = mSearchType == SearchType.SEARCH_TAGS ?
                PhotosGridActivity.EXTRA_SEARCH_TAG : PhotosGridActivity.EXTRA_USER_ID;
        intent.putExtra(key, searchTag);
        mContext.startActivity(intent);
    }
}