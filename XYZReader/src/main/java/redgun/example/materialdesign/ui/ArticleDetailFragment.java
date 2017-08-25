package redgun.example.materialdesign.ui;


import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import redgun.example.materialdesign.R;
import redgun.example.materialdesign.data.ArticleLoader;


/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */

public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;
    CoordinatorLayout activity_detail_coordinatorlayout;
    AppBarLayout article_detail_appbarlayout;
    CollapsingToolbarLayout article_detail_collapsetoolbarlayout;
    ImageView article_detail_image;
    TextView article_detail_title, article_byline, article_detail_description;
    Toolbar article_detail_toolbar;
    FloatingActionButton article_detail_share_fab;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("selected_article_id")) {
            mItemId = getArguments().getLong("selected_article_id");
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        bindViews();

        return mRootView;
    }


    private void bindViews() {
        if (mRootView == null) {
            return;
        }


        activity_detail_coordinatorlayout = (CoordinatorLayout) mRootView.findViewById(R.id.activity_detail_coordinatorlayout);
        article_detail_share_fab = (FloatingActionButton) mRootView.findViewById(R.id.article_detail_share_fab);
        article_detail_appbarlayout = (AppBarLayout) mRootView.findViewById(R.id.article_detail_appbarlayout);
        article_detail_collapsetoolbarlayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.article_detail_collapsetoolbarlayout);
        article_detail_image = (ImageView) mRootView.findViewById(R.id.article_detail_image);
        article_detail_title = (TextView) mRootView.findViewById(R.id.article_detail_title);
        article_byline = (TextView) mRootView.findViewById(R.id.article_byline);
        article_detail_description = (TextView) mRootView.findViewById(R.id.article_detail_description);
        article_detail_toolbar = (Toolbar) mRootView.findViewById(R.id.article_detail_toolbar);
        article_detail_share_fab = (FloatingActionButton) mRootView.findViewById(R.id.article_detail_share_fab);


        //ToDo-title dynamic after fetching content from Loader
        article_detail_collapsetoolbarlayout.setTitle("Ravindra");
        article_detail_collapsetoolbarlayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        mRootView.findViewById(R.id.article_detail_share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

    }

    void populateViews(){

        if (mCursor != null) {
            mCursor.moveToFirst();
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            article_detail_title.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            article_byline.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</font>"));
            article_detail_description.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.generate(bitmap, 12);
                                article_detail_image.setImageBitmap(imageContainer.getBitmap());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        } else {
            //mRootView.setVisibility(View.GONE);
            article_detail_title.setText("N/A");
            article_byline.setText("N/A");
            article_detail_description.setText("N/A");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(getActivity());
        //return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        populateViews();
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }


}
