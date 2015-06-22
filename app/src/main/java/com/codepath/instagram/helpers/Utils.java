package com.codepath.instagram.helpers;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;

import com.codepath.instagram.R;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;

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

    public static String formatNumberForDisplay(int number) {
        return numberFormat.format(number);
    }

    public static SpannableStringBuilder formatUserNameAndText(String userName, String text) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(userName);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
                MainApplication.sharedApplication().getResources().getColor(R.color.blue_text));
        TypefaceSpan typefaceSpanMedium = new TypefaceSpan("sans-serif-medium");
        spannableStringBuilder.setSpan(typefaceSpanMedium, 0,
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0,
                spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(text);

        TypefaceSpan typefaceSpan = new TypefaceSpan("sans-serif");
        spannableStringBuilder.setSpan(typefaceSpan, spannableStringBuilder.length() - text.length(),
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
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

    public static List<InstagramPost> decodePostsFromJson(JSONObject jsonObject) throws JSONException {
        List<InstagramPost> posts = new ArrayList<>();
        JSONArray postsJson = jsonObject.optJSONArray("data");
        if (postsJson != null) {
            for (int i = 0; i < postsJson.length(); i++) {
                InstagramPost instagramPost = InstagramPost.fromJson(postsJson.getJSONObject(i));
                posts.add(instagramPost);
            }
        }
        return posts;
    }

    public static List<InstagramComment> decodeCommentsFromJson(JSONObject jsonObject) throws JSONException {
        List<InstagramComment> comments = new ArrayList<>();
        JSONArray commentsJson = jsonObject.optJSONArray("data");
        if (commentsJson != null) {
            for (int i = 0; i < commentsJson.length(); i++) {
                InstagramComment instagramComment = InstagramComment.fromJson(commentsJson.getJSONObject(i));
                comments.add(instagramComment);
            }
        }
        return comments;
    }
}
