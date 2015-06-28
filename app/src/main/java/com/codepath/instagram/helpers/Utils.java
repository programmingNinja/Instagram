package com.codepath.instagram.helpers;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import com.codepath.instagram.R;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramSearchTag;
import com.codepath.instagram.models.InstagramUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";
    private static final NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
    }

    public static String getLikesString(int number) {
        return formatNumberForDisplay(number) + " like" + (number != 1 ? "s" : "");
    }

    public static String formatNumberForDisplay(int number) {
        return numberFormat.format(number);
    }

    public static JSONObject loadJsonFromAsset(Context context, String fileName) throws IOException, JSONException {
        InputStream inputStream = context.getResources().getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String line;
        StringBuilder builder = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }

        JSONObject result = new JSONObject(builder.toString());

        inputStream.close();
        bufferedReader.close();

        return result;
    }

    public static List<InstagramPost> decodePostsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramPost> posts = InstagramPost.fromJson(getDataJsonArray(jsonObject));
        return posts == null ? new ArrayList<InstagramPost>() : posts;
    }

    public static List<InstagramComment> decodeCommentsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramComment> comments = InstagramComment.fromJson(getDataJsonArray(jsonObject));
        return comments == null ? new ArrayList<InstagramComment>() : comments;
    }

    public static List<InstagramUser> decodeUsersFromJsonResponse(JSONObject jsonObject) {
        List<InstagramUser> users = InstagramUser.fromJson(getDataJsonArray(jsonObject));
        return users == null ? new ArrayList<InstagramUser>() : users;
    }

    public static List<InstagramSearchTag> decodeSearchTagsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramSearchTag> searchTags = InstagramSearchTag.fromJson(getDataJsonArray(jsonObject));
        return searchTags == null ? new ArrayList<InstagramSearchTag>() : searchTags;
    }

    private static JSONArray getDataJsonArray(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        if (jsonObject != null) {
            jsonArray = jsonObject.optJSONArray("data");
        }
        return jsonArray;
    }

    public static SpannableStringBuilder formatCaptionText(Context context, String instagramUsername, String instagramCaption) {

        String userName = TextUtils.isEmpty(instagramUsername) ? "" : instagramUsername;
        String caption = TextUtils.isEmpty(instagramCaption) ? "" : instagramCaption;

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(userName);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.blue_text));
        TypefaceSpan typefaceSpanMedium = new TypefaceSpan("sans-serif-medium");
        spannableStringBuilder.setSpan(typefaceSpanMedium, 0,
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0,
                spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(caption);

        TypefaceSpan typefaceSpan = new TypefaceSpan("sans-serif");
        spannableStringBuilder.setSpan(typefaceSpan, spannableStringBuilder.length() - caption.length(),
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }
}
