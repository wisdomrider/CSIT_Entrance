package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.DiscussionFragment;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceBot;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceForum;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceNews;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceResult;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.Home;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.Leaderboard;
import np.com.aawaz.csitentrance.fragments.other_fragments.SubjectsList;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Feedback;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.objects.Score;

public class MainActivity extends AppCompatActivity {

    public static boolean openedIntent = false;
    private static TextView titleMain;
    public TabLayout tabLayout;
    Intent intent;
    FragmentManager manager;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TextView name;
    CircleImageView imageView;
    String[] navigationText = new String[]{"leaderboard","discussion", "subject",
            "bot", "news", "forum", "result",
            "setting", "feedback", "share", "like", "rate"};
    int[] navigationId = new int[]{R.id.leaderBoard,R.id.discussion, R.id.subjectQuiz, R.id.entranceBot, R.id.entranceNews, R.id.entranceForum, R.id.entranceResult, R.id.settings, R.id.feedback, R.id.share, R.id.like, R.id.rate};

    public static void setTitle(String name) {
        titleMain.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        uploadInstanceId();

        intent = getIntent();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerMain);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        manager = getSupportFragmentManager();
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutMain);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarMain);
        titleMain = (TextView) findViewById(R.id.titleMain);


        setTitle("Home");
        manager.beginTransaction().replace(R.id.fragmentHolder, new Home()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                navigate(item);
                return true;
            }
        });

        manageHeader();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        };

        mDrawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        handlingIntent(getIntent());
        new EventSender().logEvent("main_activity");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.intent = intent;
        handlingIntent(intent);
    }

    private void handlingIntent(Intent intent) {
        if (intent.getStringExtra("fragment") != null) {
            String string = intent.getStringExtra("fragment");
            if (string.equals("model")) {
                startActivity(new Intent(this, ModelEntranceActivity.class));
                return;
            }
            for (int i = 0; i < navigationText.length; i++)
                if (navigationText[i].equals(string)) {
                    openedIntent = false;
                    navigate(mNavigationView.getMenu().findItem(navigationId[i]));
                }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadScore();
    }

    private void uploadScore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (SPHandler.getInstance().isScoreChanged()) {
            FirebaseFirestore.getInstance().collection("scores")
                    .document(user.getUid())
                    .set(Score.Companion.getScoreObject());

            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .update("score", SPHandler.getInstance().getTotalScore());

            SPHandler.getInstance().setScoreChanged(false);
        }
    }

    private void uploadInstanceId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !SPHandler.getInstance().isUserDataAdded()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", user.getDisplayName());
            map.put("phone_no", SPHandler.getInstance().getPhoneNo());
            map.put("instance_id", token);
            try {
                map.put("image_url", user.getPhotoUrl().toString());
                map.put("last_signed_in", getDateFromMilis(user.getMetadata().getLastSignInTimestamp()));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            map.put("email", user.getEmail());
            map.put("uid", user.getUid());
            map.put("account_created_at", getDateFromMilis(user.getMetadata().getCreationTimestamp()));
            map.put("score", SPHandler.getInstance().getTotalScore());

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .set(map, SetOptions.mergeFields("name", "phone_no", "instance_id", "image_url", "email", "uid", "score", "last_signed_in", "account_created_at"));

            SPHandler.getInstance().userDataAdded();
            FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
            if (SPHandler.getInstance().getForumSubscribed())
                FirebaseMessaging.getInstance().subscribeToTopic("forum");

            if (SPHandler.getInstance().getDiscussionSubscribed())
                FirebaseMessaging.getInstance().subscribeToTopic("discussion");

            if (SPHandler.getInstance().getNewsSubscribed())
                FirebaseMessaging.getInstance().subscribeToTopic("news");
        }
    }

    private String getDateFromMilis(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
    }

    private void manageHeader() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name = mNavigationView.getHeaderView(0).findViewById(R.id.userName);
        TextView email = mNavigationView.getHeaderView(0).findViewById(R.id.userEmail);
        imageView = mNavigationView.getHeaderView(0).findViewById(R.id.user_profile);
        if (user.getDisplayName() != null) {
            name.setText(user.getDisplayName());
        }
        email.setText(user.getEmail());
        Picasso.with(this)
                .load(user.getPhotoUrl())
                .error(ContextCompat.getDrawable(this, R.drawable.account_holder))
                .into(imageView);

        mNavigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid", user.getUid()));
            }
        });
    }

    private void navigate(MenuItem item) {
        int id = item.getItemId();
        mDrawerLayout.closeDrawer(mNavigationView);
        invalidateOptionsMenu();
        switch (id) {
            case R.id.aboutUs:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                new EventSender().logEvent("about_us");
                return;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                new EventSender().logEvent("settings");
                return;
            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");

                share.putExtra(Intent.EXTRA_SUBJECT, "CSIT Entrance");
                share.putExtra(Intent.EXTRA_TEXT, "Single app for all BSc CSIT Entrance preparing students.\nhttps://b5b88.app.goo.gl/jdF1");

                startActivity(Intent.createChooser(share, "Share CSIT Entrance"));
                new EventSender().logEvent("shared_app");
                return;
            case R.id.rate:
                new EventSender().logEvent("rated_app");
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=np.com.aawaz.csitentrance")));
                } catch (Exception e) {
                    Toast.makeText(this, "No play store app found.", Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.like:
                startActivity(newFacebookIntent());
                new EventSender().logEvent("liked_page");
                return;
            case R.id.feedback:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Send Feedback")
                        .input("Feedback text...", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                Feedback feedback = new Feedback(input.toString());
                                FirebaseFirestore.getInstance().collection("feedbacks").add(feedback);
                                new EventSender().logEvent("sent_feedback");
                                Toast.makeText(MainActivity.this, "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .positiveText("Send")
                        .build();
                dialog.getInputEditText().setLines(5);
                dialog.getInputEditText().setSingleLine(false);
                dialog.getInputEditText().setMaxLines(7);
                dialog.show();
                return;
            case R.id.logout:
                new MaterialDialog.Builder(this)
                        .title("Log out")
                        .content("Are you sure you want to log out?")
                        .positiveText("Log Out")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FirebaseAuth.getInstance().signOut();
                                SPHandler.getInstance().clearAll();
                                if (AccessToken.getCurrentAccessToken() != null)
                                    LoginManager.getInstance().logOut();
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("allDevices");
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("forum");
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("discussion");
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                finish();
                            }
                        })
                        .show();
                return;
        }
        setAppBarElevation(getResources().getDimension(R.dimen.app_bar_elevation));
        switch (id) {
            case R.id.main_home:
                manager.beginTransaction().replace(R.id.fragmentHolder, new Home()).commit();
                setTitle("Home");
                tabLayout.setVisibility(View.VISIBLE);
                item.setChecked(true);
                break;

            case R.id.leaderBoard:
                manager.beginTransaction().replace(R.id.fragmentHolder, new Leaderboard()).commit();
                setTitle("Leaderboard");
                tabLayout.setVisibility(View.GONE);
                setAppBarElevation(0);
                item.setChecked(true);
                new EventSender().logEvent("leaderboard");
                break;

            case R.id.entranceBot:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceBot()).commit();
                setTitle("Entrance Bot");
                tabLayout.setVisibility(View.GONE);
                new EventSender().logEvent("bot");
                item.setChecked(true);
                break;

            case R.id.discussion:
                manager.beginTransaction().replace(R.id.fragmentHolder, DiscussionFragment.Companion.newInstance(intent.getStringExtra("post_id"))).commit();
                setTitle("Question Discussion");
                tabLayout.setVisibility(View.GONE);
                new EventSender().logEvent("discussion");
                item.setChecked(true);
                break;

            case R.id.entranceNews:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceNews()).commit();
                tabLayout.setVisibility(View.GONE);
                setTitle("Entrance News");
                new EventSender().logEvent("news");
                item.setChecked(true);
                break;

            case R.id.entranceForum:
                manager.beginTransaction().replace(R.id.fragmentHolder, EntranceForum.newInstance(intent.getStringExtra("post_id"))).commit();
                tabLayout.setVisibility(View.GONE);
                setTitle("Entrance Forum");
                new EventSender().logEvent("forum");
                item.setChecked(true);
                break;

            case R.id.subjectQuiz:
                manager.beginTransaction().replace(R.id.fragmentHolder, new SubjectsList()).commit();
                setTitle("Subject Quiz");
                tabLayout.setVisibility(View.GONE);
                new EventSender().logEvent("subject_quiz");
                item.setChecked(true);
                break;

            case R.id.entranceResult:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceResult()).commit();
                setTitle("Entrance Result");
                setAppBarElevation(0);
                new EventSender().logEvent("result");
                tabLayout.setVisibility(View.GONE);
                item.setChecked(true);
                break;
        }
    }

    private Intent newFacebookIntent() {
        Uri uri = Uri.parse("https://m.facebook.com/CSITEntrance");
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void setAppBarElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(elevation);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getToolbarTitle().equals("CSIT Colleges"))
            menu.findItem(R.id.search).setVisible(true);
        else
            menu.findItem(R.id.search).setVisible(false);

        if (getToolbarTitle().equals("Play Quiz"))
            menu.findItem(R.id.exam).setVisible(false);
        else
            menu.findItem(R.id.exam).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.notifications:
                startActivity(new Intent(this, NotificationActivity.class));
                return true;
            case R.id.exam:
                startActivity(new Intent(this, ModelEntranceActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 202) {
            name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            Picasso.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.closeDrawer(mNavigationView);
        else if (Home.viewPager != null && Home.viewPager.getCurrentItem() > 0)
            Home.viewPager.setCurrentItem(0, true);
        else if (!getToolbarTitle().equals("Home"))
            navigate(mNavigationView.getMenu().findItem(R.id.main_home));
        else
            super.onBackPressed();
    }

    public String getToolbarTitle() {
        return titleMain.getText().toString();
    }
}