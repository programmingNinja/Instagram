package com.codepath.instagram.helpers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ShareIntent {
    private static final String TAG = "ShareIntent";

    public static Intent getImageIntent(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");

        if (imageUri == null) {
            Log.wtf(TAG, "Trying to share a null image");
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        }

        return Intent.createChooser(intent, null);
    }
}
