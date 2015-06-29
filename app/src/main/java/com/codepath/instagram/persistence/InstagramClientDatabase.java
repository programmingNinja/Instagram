package com.codepath.instagram.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramImage;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramUser;

import java.util.ArrayList;
import java.util.List;

public class InstagramClientDatabase extends SQLiteOpenHelper {
    private static final String TAG = "InstagramClientDatabase";

    private static final String DATABASE_NAME = "instagramClientDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_IMAGES = "images";
    private static final String TABLE_COMMENTS = "comments";
    private static final String TABLE_POST_COMMENTS = "postComments";

    private static final String CONSTRAINT_POST_COMMENTS_PK = "postComments_pk";

    private static final String KEY_ID = "id";

    // Posts table columns
    private static final String KEY_POST_MEDIA_ID = "mediaId";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_IMAGE_ID_FK = "imageId";
    private static final String KEY_POST_CAPTION = "caption";
    private static final String KEY_POST_LIKES_COUNT = "likesCount";
    private static final String KEY_POST_COMMENTS_COUNT = "commentsCount";
    private static final String KEY_POST_CREATED_TIME = "createdTime";

    // Users table columns
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    // Images table columns
    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_IMAGE_HEIGHT = "imageHeight";
    private static final String KEY_IMAGE_WIDTH = "imageWidth";

    // Comments table columns
    private static final String KEY_COMMENT_USER_ID_FK = "userId";
    private static final String KEY_COMMENT_TEXT = "text";
    private static final String KEY_COMMENT_CREATED_TIME = "createdTime";

    // Post Comments table columns
    private static final String KEY_POST_COMMENT_POST_ID_FK = "postId";
    private static final String KEY_POST_COMMENT_COMMENT_ID_FK = "commentId";


    public InstagramClientDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_POST_MEDIA_ID + " TEXT," +
                KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," +
                KEY_POST_IMAGE_ID_FK + " INTEGER REFERENCES " + TABLE_IMAGES + "," +
                KEY_POST_CAPTION + " TEXT," +
                KEY_POST_LIKES_COUNT + " INTEGER," +
                KEY_POST_COMMENTS_COUNT + " INTEGER," +
                KEY_POST_CREATED_TIME + " INTEGER" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";

        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_IMAGE_URL + " TEXT," +
                KEY_IMAGE_HEIGHT + " INTEGER," +
                KEY_IMAGE_WIDTH + " INTEGER" +
                ")";

        String CREATE_COMMENTS_TABLE = "CREATE TABLE " + TABLE_COMMENTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_COMMENT_TEXT + " TEXT," +
                KEY_COMMENT_CREATED_TIME + " INTEGER," +
                KEY_COMMENT_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS +
                ")";

        String CREATE_POST_COMMENTS_TABLE = "CREATE TABLE " + TABLE_POST_COMMENTS +
                "(" +
                KEY_POST_COMMENT_POST_ID_FK + " INTEGER," +
                KEY_POST_COMMENT_COMMENT_ID_FK + " INTEGER," +
                "constraint " + CONSTRAINT_POST_COMMENTS_PK +
                " PRIMARY KEY(" +
                KEY_POST_COMMENT_POST_ID_FK + "," +
                KEY_POST_COMMENT_COMMENT_ID_FK +
                ")" +
                ")";

        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_COMMENTS_TABLE);
        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_POST_COMMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            clearDatabase();
            onCreate(db);
        }
    }

    public synchronized void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();

        deleteIfExists(TABLE_USERS, db);
        deleteIfExists(TABLE_IMAGES, db);
        deleteIfExists(TABLE_COMMENTS, db);
        deleteIfExists(TABLE_POSTS, db);
        deleteIfExists(TABLE_POST_COMMENTS, db);

        closeDatabase();
    }

    public void deleteIfExists(String tableName, SQLiteDatabase writeableDatabase) {
        if (existsTable(tableName, writeableDatabase)) {
            writeableDatabase.execSQL("DELETE FROM " + tableName);
        }
    }

    public synchronized void addInstagramPosts(List<InstagramPost> posts) {
        if (posts == null) {
            return;
        }
        for (InstagramPost post : posts) {
            try {
                addInstagramPost(post);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized long addInstagramPost(InstagramPost post) throws Exception {
        if (post == null) {
            String errorText = "Attemping to add a null post";
            Log.wtf(TAG, errorText);
            throw new Exception(errorText);
        }

        SQLiteDatabase db = getWritableDatabase();

        long userId = addUser(post.user);
        long imageId = addImage(post.image);

        ContentValues values = new ContentValues();
        values.put(KEY_POST_MEDIA_ID, post.mediaId);
        values.put(KEY_POST_USER_ID_FK, userId);
        values.put(KEY_POST_IMAGE_ID_FK, imageId);
        values.put(KEY_POST_CAPTION, post.caption);
        values.put(KEY_POST_LIKES_COUNT, post.likesCount);
        values.put(KEY_POST_COMMENTS_COUNT, post.commentsCount);
        values.put(KEY_POST_CREATED_TIME, post.createdTime);

        long postId = db.insert(TABLE_POSTS, null, values);

        if (post.comments != null) {
            for (InstagramComment comment : post.comments) {
                addComment(comment, postId, userId);
            }
        }
        closeDatabase();
        return postId;
    }

    private long addUser(InstagramUser user) throws Exception {
        if (user == null) {
            String errorText = "Attemping to add a null user";
            Log.wtf(TAG, errorText);
            throw new Exception(errorText);
        }
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.userName);
        values.put(KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

        return db.insert(TABLE_USERS, null, values);
    }

    private long addImage(InstagramImage image) throws Exception {
        if (image == null) {
            String errorText = "Attemping to add a null user";
            Log.wtf(TAG, errorText);
            throw new Exception(errorText);
        }
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_URL, image.imageUrl);
        values.put(KEY_IMAGE_HEIGHT, image.imageHeight);
        values.put(KEY_IMAGE_WIDTH, image.imageHeight);

        return db.insert(TABLE_IMAGES, null, values);
    }

    private long addComment(InstagramComment comment, long postId, long userId) throws Exception {
        if (comment == null) {
            String errorText = "Attemping to add a null user";
            Log.wtf(TAG, errorText);
            throw new Exception(errorText);
        }
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COMMENT_TEXT, comment.text);
        values.put(KEY_COMMENT_USER_ID_FK, userId);
        values.put(KEY_COMMENT_CREATED_TIME, comment.createdTime);

        long commentId = db.insert(TABLE_COMMENTS, null, values);
        addPostCommentMapping(postId, commentId);
        return commentId;
    }

    private long addPostCommentMapping(long postId, long commentId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POST_COMMENT_COMMENT_ID_FK, commentId);
        values.put(KEY_POST_COMMENT_POST_ID_FK, postId);

        return db.insert(TABLE_POST_COMMENTS, null, values);
    }

    public synchronized List<InstagramPost> getAllInstagramPosts() {
        List<InstagramPost> posts = new ArrayList<>();

        String LEFT_OUTER_JOIN_FORMAT_STRING = "LEFT OUTER JOIN %s ON %s.%s = %s.%s";

        String userJoin = String.format(LEFT_OUTER_JOIN_FORMAT_STRING,
                TABLE_USERS, TABLE_POSTS, KEY_POST_USER_ID_FK, TABLE_USERS, KEY_ID);

        String imageJoin = String.format(LEFT_OUTER_JOIN_FORMAT_STRING,
                TABLE_IMAGES, TABLE_POSTS, KEY_POST_IMAGE_ID_FK, TABLE_IMAGES, KEY_ID);

        String postsSelectQuery = "SELECT * FROM " + TABLE_POSTS + " " + userJoin + " " + imageJoin;

        String commentJoin = String.format(LEFT_OUTER_JOIN_FORMAT_STRING,
                TABLE_COMMENTS, TABLE_POST_COMMENTS, KEY_POST_COMMENT_COMMENT_ID_FK, TABLE_COMMENTS, KEY_ID);
        String commentUserJoin = String.format(LEFT_OUTER_JOIN_FORMAT_STRING,
                TABLE_USERS, TABLE_USERS, KEY_ID, TABLE_COMMENTS, KEY_COMMENT_USER_ID_FK);

        String commentsSelectQuery = String.format("SELECT * FROM %s %s %s WHERE %s = ?",
                TABLE_POST_COMMENTS, commentJoin, commentUserJoin, KEY_POST_COMMENT_POST_ID_FK);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor postsCursor = db.rawQuery(postsSelectQuery, null);

        if (postsCursor.moveToFirst()) {
            do {
                try {
                    InstagramPost post = new InstagramPost();
                    post.mediaId = postsCursor.getString(getColumnIndex(postsCursor, KEY_POST_MEDIA_ID));
                    post.caption = postsCursor.getString(getColumnIndex(postsCursor, KEY_POST_CAPTION));
                    post.likesCount = postsCursor.getInt(getColumnIndex(postsCursor, KEY_POST_LIKES_COUNT));
                    post.commentsCount = postsCursor.getInt(getColumnIndex(postsCursor, KEY_POST_COMMENTS_COUNT));
                    post.createdTime = postsCursor.getLong(getColumnIndex(postsCursor, KEY_POST_CREATED_TIME));

                    InstagramUser user = new InstagramUser();
                    user.userName = postsCursor.getString(getColumnIndex(postsCursor, KEY_USER_NAME));
                    user.profilePictureUrl = postsCursor.getString(getColumnIndex(postsCursor, KEY_USER_PROFILE_PICTURE_URL));
                    post.user = user;

                    InstagramImage image = new InstagramImage();
                    image.imageUrl = postsCursor.getString(getColumnIndex(postsCursor, KEY_IMAGE_URL));
                    image.imageHeight = postsCursor.getInt(getColumnIndex(postsCursor, KEY_IMAGE_HEIGHT));
                    image.imageWidth = postsCursor.getInt(getColumnIndex(postsCursor, KEY_IMAGE_WIDTH));
                    post.image = image;

                    //int key = postsCursor.getInt(getColumnIndex(postsCursor, KEY_ID));
                    int key = postsCursor.getInt(0);
                    Cursor commentsCursor = db.rawQuery(commentsSelectQuery, new String[] { String.valueOf(key) });
                    if (commentsCursor.moveToFirst()) {
                        do {
                            InstagramComment comment = new InstagramComment();
                            comment.text = commentsCursor.getString(getColumnIndex(commentsCursor, KEY_COMMENT_TEXT));
                            comment.createdTime = commentsCursor.getLong(getColumnIndex(commentsCursor, KEY_COMMENT_CREATED_TIME));

                            InstagramUser commentUser = new InstagramUser();
                            commentUser.userName = commentsCursor.getString(getColumnIndex(commentsCursor, KEY_USER_NAME));
                            commentUser.profilePictureUrl = commentsCursor.getString(getColumnIndex(commentsCursor, KEY_USER_PROFILE_PICTURE_URL));
                            comment.user = commentUser;

                            post.appendComment(comment);
                        } while (commentsCursor.moveToNext());
                    }
                    commentsCursor.close();
                    posts.add(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (postsCursor.moveToNext());
        }

        postsCursor.close();
        return posts;
    }

    public void closeDatabase() {
        SQLiteDatabase database = this.getReadableDatabase();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    private int getColumnIndex(Cursor cursor, String columnName) throws Exception {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (index < 0) {
            String errorText = String.format("Unable to find %s in %s", columnName, DATABASE_NAME);
            // error case
            Log.wtf(TAG, errorText);
            throw new Exception(errorText);
        }
        return index;
    }

    public boolean existsTable(String tableName, SQLiteDatabase readableDatabase) {
        boolean exists = false;
        Cursor cursor = readableDatabase.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '"
                + tableName + "'", null);
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }
        return exists;
    }
}
