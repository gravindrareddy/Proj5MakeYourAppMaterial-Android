package redgun.example.materialdesign.ui;

import android.support.v4.app.LoaderManager;
import android.content.Intent;


import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import redgun.example.materialdesign.R;
import redgun.example.materialdesign.data.ArticleLoader;
import redgun.example.materialdesign.data.ItemsContract;


/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private Cursor mCursor;
    private long mStartId;
    private Bundle intentReceivedArticleDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        Intent i = getIntent();
        if (null != i.getExtras())
            intentReceivedArticleDetails = i.getExtras().getBundle("bundle");

        if (savedInstanceState == null) {
            ArticleDetailFragment articleFragment = new ArticleDetailFragment();
            articleFragment.setArguments(intentReceivedArticleDetails);
            getSupportFragmentManager().beginTransaction().add(R.id.article_details_fragment, articleFragment).commit();
        } else {
            //ToDo - get from savedInstanceState
        }
    }

}
