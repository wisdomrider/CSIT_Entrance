package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.NewsDetailActivity;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.FirebasePaths;
import np.com.aawaz.csitentrance.objects.News;


public class EntranceNews extends Fragment implements ClickListener, ValueEventListener {

    RecyclerView recy;
    SwipeRefreshLayout newsSwipe;
    NewsAdapter newsAdapter;
    ProgressBar progress;
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    private LinearLayout errorLayout;
    private String mUrl;
    private String mTitle;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setting the data
        addOneTimeListener();
        appIndexing();
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/news";
        mTitle = "Latest news about the BSc CSIT entrance exam.";
    }

    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseUserActions.getInstance().end(getAction());
        reference.removeEventListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy = view.findViewById(R.id.newsFeedRecy);
        newsSwipe = view.findViewById(R.id.newsSwipeRefresh);
        errorLayout = view.findViewById(R.id.errorNews);
        progress = view.findViewById(R.id.progressbarNews);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneTimeListener();
            }
        });
        newsSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsSwipe.setRefreshing(false);
            }
        });
    }


    private void addOneTimeListener() {
        reference = FirebaseDatabase.getInstance().getReference().child(FirebasePaths.NEWS);
        errorLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        newsSwipe.setVisibility(View.GONE);
        newsAdapter = new NewsAdapter(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        reference.addValueEventListener(this);
        recy.setLayoutManager(linearLayoutManager);
        recy.setAdapter(newsAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        reference.removeEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        newsSwipe.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        newsAdapter.clearData();
        for (DataSnapshot eachNews : dataSnapshot.getChildren()) {
            newsAdapter.addToTop(eachNews.getValue(News.class));
        }
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        progress.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void itemClicked(View view, int position) {
        News eachNews = newsAdapter.getNewsAt(position);
        Bundle bundle = new Bundle();
        bundle.putString("title", eachNews.title);
        bundle.putString("author", eachNews.author);
        bundle.putString("detail", eachNews.message);
        bundle.putLong("time", eachNews.time_stamp);
        startActivity(new Intent(getContext(), NewsDetailActivity.class).putExtra("data", bundle));
    }

    @Override
    public void itemLongClicked(View view, int position) {

    }

    @Override
    public void upVoteClicked(View view, int position) {

    }
}