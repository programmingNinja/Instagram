package com.codepath.instagram.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.PhotoGridActivity;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;

import java.util.ArrayList;
import java.util.List;

public class SearchTagResultsAdapter
        extends RecyclerView.Adapter<SearchTagResultsAdapter.SearchTagItemViewHolder> {
  private static final String TAG = "SearchTagResultsAdapter";

  protected List<InstagramSearchTag> tags;

  public SearchTagResultsAdapter() {
    this.tags = new ArrayList<>();
  }

  public void replaceAll(List<InstagramSearchTag> tags) {
    this.tags.clear();
    for (InstagramSearchTag tag : tags) {
      this.tags.add(tag);
    }
    this.notifyDataSetChanged();
  }

  @Override
  public SearchTagResultsAdapter.SearchTagItemViewHolder onCreateViewHolder(
          ViewGroup viewGroup,
          int viewType) {
    View itemView = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.item_search_tag_result, viewGroup, false);
    return new SearchTagItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(
          SearchTagResultsAdapter.SearchTagItemViewHolder holder,
          int position) {
    final InstagramSearchTag tag = tags.get(position);
    Resources resources = MainApplication.sharedApplication().getResources();
    holder.tvTag.setText(resources.getString(R.string.tag_search_result_prefix) + tag.tag);
    String postFix = tag.count == 1 ? resources.getString(R.string.tag_count_postfix) :
            resources.getString(R.string.tag_count_postfix_plural);
    holder.tvPostCount.setText(Utils.formatNumberForDisplay(tag.count) + " " + postFix);
  }

  @Override
  public int getItemCount() {
    return this.tags == null ? 0 : this.tags.size();
  }

  public final class SearchTagItemViewHolder
          extends RecyclerView.ViewHolder
          implements View.OnClickListener {
    TextView tvTag;
    TextView tvPostCount;

    public SearchTagItemViewHolder(View itemView) {
      super(itemView);
      tvTag = (TextView) itemView.findViewById(R.id.tvTag);
      tvPostCount = (TextView) itemView.findViewById(R.id.tvPostCount);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      InstagramSearchTag tag = tags.get(getPosition());
      navToPhotoGridActivity(view, tag.tag);
    }
  }

  private static void navToPhotoGridActivity(View fromView, String searchTag) {
    Intent intent = new Intent(fromView.getContext(), PhotoGridActivity.class);
    intent.putExtra(PhotoGridActivity.EXTRA_SEARCH_TAG, searchTag);
    fromView.getContext().startActivity(intent);
  }
}
