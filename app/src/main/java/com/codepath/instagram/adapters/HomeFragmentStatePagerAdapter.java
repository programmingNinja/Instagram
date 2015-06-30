package com.codepath.instagram.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import com.codepath.instagram.R;
import com.codepath.instagram.fragments.PostsFragment;
import com.codepath.instagram.fragments.SearchFragment;
import com.codepath.instagram.helpers.SmartFragmentStatePagerAdapter;

public class HomeFragmentStatePagerAdapter extends SmartFragmentStatePagerAdapter {

    private Context mContext;

    private static final int[] TAB_RESOURCE_IDS = {
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_capture,
            R.drawable.ic_notifs,
            R.drawable.ic_profile
    };

    public HomeFragmentStatePagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PostsFragment.newInstance();
            // Return Search Fragment for everything else
            default:
                return SearchFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return TAB_RESOURCE_IDS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mContext.getResources().getDrawable(TAB_RESOURCE_IDS[position]);
        if (image != null) {
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        } else {
            return "";
        }
    }
}