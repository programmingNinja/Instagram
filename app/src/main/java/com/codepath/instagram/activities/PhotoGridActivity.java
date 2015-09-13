package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.instagram.R;
import com.codepath.instagram.fragments.PhotoGridFragment;

public class PhotoGridActivity extends AppCompatActivity {

  private static final String TAG = "PhotoGridActivity";

  public static final String EXTRA_USER_ID = "userId";
  public static final String EXTRA_SEARCH_TAG = "searchTag";

  private String userId;
  private String searchTag;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_photo_grid);
    userId = getIntent().getStringExtra(EXTRA_USER_ID);
    searchTag = getIntent().getStringExtra(EXTRA_SEARCH_TAG);
    if (savedInstanceState == null) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.frPhotoGridContainer, PhotoGridFragment.newInstance(userId, searchTag));
      ft.commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_photo_grid, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
