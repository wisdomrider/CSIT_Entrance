package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.AnswerDialog;
import np.com.aawaz.csitentrance.custom_views.CustomViewPager;
import np.com.aawaz.csitentrance.fragments.other_fragments.AnswersDrawer;
import np.com.aawaz.csitentrance.fragments.other_fragments.PopupDialogFragment;
import np.com.aawaz.csitentrance.fragments.other_fragments.QuestionFragment;
import np.com.aawaz.csitentrance.interfaces.OnDismissListener;
import np.com.aawaz.csitentrance.interfaces.QuizInterface;
import np.com.aawaz.csitentrance.objects.Question;
import np.com.aawaz.csitentrance.objects.SPHandler;


public class YearQuizActivity extends AppCompatActivity implements QuizInterface {

    ArrayList<Question> questions = new ArrayList<>();

    DrawerLayout drawerLayout;
    AnswersDrawer answersDrawer;
    CustomViewPager customViewPager;

    int qNo;
    int score;
    String code;
    private TextView scoreText;
    private SPHandler spHandler;


    private String mUrl;
    private String mTitle;

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //Toolbar as support action bar
        setTitle("");
        setSupportActionBar((Toolbar) findViewById(R.id.quizToolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        code = getIntent().getStringExtra("code");
        spHandler = SPHandler.getInstance();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutQuiz);
        customViewPager = (CustomViewPager) findViewById(R.id.viewPagerQuestion);
        answersDrawer = (AnswersDrawer) getSupportFragmentManager().findFragmentById(R.id.answerFragment);

        //Last Stage of the game
        fetchFromSp();

        //Load and Options to the ArrayList
        setDataToArrayList();

        initilizeViewPager();

        setHeader();

        appIndexing();
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/questions";
        mTitle = "BSc CSIT Entrance Qld Questions";
    }

    public Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    private void setHeader() {
        CircleImageView quizProf = (CircleImageView) findViewById(R.id.quizProfilePic);
        TextView name = (TextView) findViewById(R.id.quizName);
        scoreText = (TextView) findViewById(R.id.quizScore);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null)
            Picasso.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(quizProf);


        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null)
            name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        scoreText.setText("Your Score: " + spHandler.getScore(code));
    }

    private void initilizeViewPager() {
        customViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return QuestionFragment.newInstance(getIntent().getIntExtra("position", 1), position, questions.get(position))
                        .setListener(YearQuizActivity.this);
            }

            @Override
            public int getCount() {
                return 100;
            }
        });
        customViewPager.setCurrentItem(qNo);
    }

    private void fetchFromSp() {
        qNo = spHandler.getPlayed(code);
        score = spHandler.getScore(code);

        if (qNo >= 99) {
            startActivity(new Intent(this, ReportCardActivity.class)
                    .putExtra("title", getResources().getStringArray(R.array.years)[getIntent().getIntExtra("position", 1)])
                    .putExtra("code", code)
                    .putExtra("played", 100)
                    .putExtra("scored", score));
            finish();
            return;
        }


        answersDrawer.setInitialData(qNo, getIntent().getIntExtra("position", 1) + 1);
    }

    private void showDialogAd() {
        PopupDialogFragment popupDialogFragment = new PopupDialogFragment();
        popupDialogFragment.show(getSupportFragmentManager(), "popup");
    }

    @Override
    public void selected(CardView submit, boolean correct, String answer) {
        spHandler.increasePlayed(code);
        spHandler.increaseTimesPlayed();
//        if ((spHandler.getTimesPlayed() % 10) == 0) {
//            showDialogAd();
//        }
        spHandler.increasePlayed(spHandler.getSubjectCode(getIntent().getIntExtra("position", 1), qNo));
        if (customViewPager.getCurrentItem() == 99) {
            startActivity(new Intent(YearQuizActivity.this, ReportCardActivity.class)
                    .putExtra("title", getResources().getStringArray(R.array.years)[getIntent().getIntExtra("position", 1)])
                    .putExtra("code", code)
                    .putExtra("played", 100)
                    .putExtra("scored", score));
            finish();
            return;
        } else
            answersDrawer.increaseSize();
        if (correct) {
            spHandler.increaseScore(code);
            spHandler.increaseScore(spHandler.getSubjectCode(getIntent().getIntExtra("position", 1), qNo));
            scoreText.setText("Your Score: " + spHandler.getScore(code));
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorSelected));
            TextView text = (TextView) submit.findViewById(R.id.submit_button_text);
            text.setText("Correct");
            submit.setOnClickListener(null);
            YoYo.with(Techniques.Tada).duration(500).playOn(submit);
        } else {
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_1));
            TextView text = (TextView) submit.findViewById(R.id.submit_button_text);
            text.setText("In-Correct");
            submit.setOnClickListener(null);
            YoYo.with(Techniques.Shake).duration(500).playOn(submit);
        }
        if (spHandler.shouldShowAnswers() && !correct) {
            AnswerDialog answerDialog = AnswerDialog.newInstance(code, answer, customViewPager.getCurrentItem() + 1);
            answerDialog.show(getSupportFragmentManager(), "answer");
            answerDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss() {
                    changeQuestion(200);
                }
            });
        } else
            changeQuestion(500);
    }

    private void changeQuestion(long time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (customViewPager.getCurrentItem() != 99)
                    customViewPager.setCurrentItem(customViewPager.getCurrentItem() + 1);
            }
        }, time);
    }

    public void setDataToArrayList() {
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + (getIntent().getIntExtra("position", 1) + 1) + ".json", this));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Question q = new Question(jo_inside.getString("question"), jo_inside.getString("a"), jo_inside.getString("b"), jo_inside.getString("c"), jo_inside.getString("d"), jo_inside.getString("ans"));
                questions.add(q);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_answer) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return super.onCreateOptionsMenu(menu);
    }

}