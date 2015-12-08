package fr.manumehdi.gazouille.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

import javax.inject.Inject;

import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.adapter.TweetAdapter;
import fr.manumehdi.gazouille.data.di.DataComponent;
import fr.manumehdi.gazouille.data.manager.GazouilleManager;
import fr.manumehdi.gazouille.data.manager.UserManager;
import fr.manumehdi.gazouille.data.model.Gazouillis;
import fr.manumehdi.gazouille.views.span.HashTagSpan;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ScrollingActivity extends BaseActivity {


    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;
    @Inject
    protected GazouilleManager gazouilleManager;
    @Inject
    protected UserManager userManager;

    public static final String START_WITH_HASHTAG = "hashtag";

    @Override
    public void initComponent(DataComponent dataComponent) {
        dataComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.content_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddTweetDialogFragment().show(getSupportFragmentManager(), "");
            }
        });
        {

        }
        refreshData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }

    private void refreshData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            gazouilleManager.getMessages(true).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Action1<List<Gazouillis>>() {
                        @Override
                        public void call(List<Gazouillis> gazouillises) {
                            displayTweets(gazouillises);
                        }

                    });
        } else {
            String hashtag = bundle.getString(START_WITH_HASHTAG);
            String title = hashtag;
            gazouilleManager.getMessages(hashtag).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Action1<List<Gazouillis>>() {
                        @Override
                        public void call(List<Gazouillis> gazouillises) {
                            displayTweets(gazouillises);
                        }

                    });
        }
    }

    private void displayTweets(List<Gazouillis> gazouillis) {
        swipeRefreshLayout.setRefreshing(false);
        TweetAdapter adapter = new TweetAdapter(gazouillis);
        adapter.setHashTagClickListener(new HashTagSpan.OnHashTagClickListener() {
            @Override
            public void onClick(String hashTag) {
                Intent intent = new Intent(getApplicationContext(), ScrollingActivity.class);
                intent.putExtra(START_WITH_HASHTAG, hashTag);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void displayTweetsError(Throwable error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }


    public void displaySnackbar(String msg, View view) {
        if (view != null)
            Snackbar.make(view.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    //region menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            startActivity(new Intent(this.getApplicationContext(), EditProfilActivity.class));
            return true;
        } else if (itemId == R.id.action_login_in_out) {
            disconnect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disconnect() {
        userManager.disconnect();
        startActivity(new Intent(this.getApplicationContext(), LoginActivity.class));
        finish();
    }
    //endregion menu

}





