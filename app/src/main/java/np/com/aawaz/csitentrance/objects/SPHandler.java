package np.com.aawaz.csitentrance.objects;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.Utils.Preferences;
import np.com.aawaz.csitentrance.misc.MyApplication;

public class SPHandler {
    public static String CHEMISTRY = "chem";
    public static String PHYSICS = "phy";
    public static String MATH = "math";
    public static String ENGLISH = "eng";
    public static String YEAR2069 = "year2069";
    public static String YEAR2070 = "year2070";
    public static String YEAR2071 = "year2071";
    public static String YEAR2072 = "year2072";
    public static String YEAR2073 = "year2073";
    public static String YEAR2074 = "year2074";
    public static String MODEL1 = "model1";
    public static String MODEL2 = "model2";
    public static String MODEL3 = "model3";
    public static String MODEL4 = "model4";
    public static String MODEL5 = "model5";
    public static String MODEL6 = "model6";
    public static String MODEL7 = "model7";
    public static String MODEL8 = "model8";
    public static String MODEL9 = "model9";
    public static String MODEL10 = "model10";
    public static String SAGARMATHA = "model11-sagarmatha";
    public static String MODEL12 = "model12";
    public static String MODEL13 = "model13";
    public static String MODEL14 = "model14";
    public static String MODEL15 = "model15-cab";
    public static String MODEL16 = "model16";

    private static SPHandler spHandler;
    private final Preferences scoreEditor;
    private final Preferences infoEditor;
    Preferences scoreSp;
    Preferences infoSp;
    private String PLAYED = "_played";
    private String SCORE = "_score";

    String[][] subjects = {new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//69
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//70
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//71
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//72
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//73
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//74
            new String[]{PHYSICS, ENGLISH, MATH, CHEMISTRY},//model 1
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//model 2
            new String[]{PHYSICS, CHEMISTRY, ENGLISH, MATH},//model 3
            new String[]{ENGLISH, PHYSICS, CHEMISTRY, MATH},//model 4
            new String[]{ENGLISH, MATH, CHEMISTRY, PHYSICS},//model 5
            new String[]{MATH, PHYSICS, CHEMISTRY, ENGLISH},//model 6
            new String[]{ENGLISH, MATH, PHYSICS, CHEMISTRY},//model 7 Samriddhi
            new String[]{ENGLISH, CHEMISTRY, PHYSICS, MATH},//model 8 (Actually 7)
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//model 9 (Actually 8)
            new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},//model 10(actually 9)
            new String[]{ENGLISH, PHYSICS, MATH, CHEMISTRY},//model 11 Sagarmatha
            new String[]{ENGLISH, MATH, CHEMISTRY, PHYSICS},//model 12, actually 10
            new String[]{ENGLISH, CHEMISTRY, PHYSICS, MATH},
            new String[]{ENGLISH, MATH, PHYSICS, CHEMISTRY},
            new String[]{ENGLISH, MATH, PHYSICS, CHEMISTRY},
            new String[]{ENGLISH, PHYSICS, MATH, CHEMISTRY}};

    public SPHandler() {
        scoreSp = scoreEditor = new Preferences("play_data", Context.MODE_PRIVATE);
        infoSp = infoEditor = new Preferences("info", Context.MODE_PRIVATE);

    }

    public static SPHandler getInstance() {
        if (spHandler == null)
            return spHandler = new SPHandler();
        else
            return spHandler;
    }

    public static boolean containsDevUID(String uid) {
        ArrayList<String> admins = new ArrayList<>();
        admins.add("IJdET0udoOayA11j6BIoT6D7O0S2");
        admins.add("R3LmmEGnMePjYtswECxiw4E3aFc2");
        admins.add("URhcpsBTKuQ8juVPgGNeWEJn0yA3");
        admins.add("gc9e24JKjDVsbm1Fk5uFOEE5AgI3");
        return admins.contains(uid);
    }

    public String getCurrentUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public int getScore(String name) {
        return scoreSp.getInt(name + SCORE, 0);
    }

    public int getPlayed(String name) {
        return scoreSp.getInt(name + PLAYED, 0);
    }

    public void setScore(String name, int value) {
        scoreEditor.putInt(name + SCORE, value).apply();
    }

    public void setPlayed(String name, int value) {
        scoreEditor.putInt(name + PLAYED, value).apply();
    }

    public void increasePlayed(String name) {
        new EventSender().logEvent("questions_played");
        scoreEditor.putInt(name + PLAYED, getPlayed(name) + 1).apply();
    }

    public void increaseScore(String name) {
        scoreEditor.putInt(name + SCORE, getScore(name) + 1).apply();
        setScoreChanged(true);
    }

    public boolean isScoreChanged() {
        return scoreSp.getBoolean("score_changed", true);
    }

    public void setScoreChanged(boolean changed) {
        scoreEditor.putBoolean("score_changed", changed).apply();
    }

    public int getAccuracy(String name) {
        return (int) (((float) getScore(name) / (float) getPlayed(name)) * 100);
    }

    public int getTotalScore() {
        return getScore(YEAR2069) + getScore(YEAR2070) + getScore(YEAR2071) + getScore(YEAR2072) + getScore(YEAR2073) + +getScore(YEAR2074) +
                getScore(MODEL1) + getScore(MODEL2) + getScore(MODEL3) + getScore(MODEL4) + getScore(MODEL5) + getScore(MODEL6) + getScore(MODEL7) +
                getScore(MODEL8) + getScore(MODEL9) + getScore(MODEL10) + getScore(SAGARMATHA) + getScore(MODEL12) + getScore(MODEL13) + getScore(MODEL14) + getScore(MODEL15)+ getScore(MODEL16);
    }

    public int getTotalPlayed() {
        return getPlayed(YEAR2069) + getPlayed(YEAR2070) + getPlayed(YEAR2071) + getPlayed(YEAR2072) + getPlayed(YEAR2073) + getPlayed(YEAR2074) +
                getPlayed(MODEL1) + getPlayed(MODEL2) + getPlayed(MODEL3) + getPlayed(MODEL4) + getPlayed(MODEL5) + getPlayed(MODEL6) + getPlayed(MODEL7) +
                getPlayed(MODEL8) + getPlayed(MODEL9) + getPlayed(MODEL10) + getPlayed(SAGARMATHA) + getPlayed(MODEL12) + getPlayed(MODEL13) + getPlayed(MODEL14) + getPlayed(MODEL15)+ getPlayed(MODEL16);
    }

    public boolean isResultPublished() {
        return infoSp.getBoolean("result_publish", false);
    }

    public void setResultPublished() {
        infoEditor.putBoolean("result_publish", true).apply();
    }

    public String getSubjectCode(int index, int questionNo) {
        return subjects[index][questionNo / 25];
    }

    public int getIndexOfQuestion(int index, String subject) {
        for (int i = 0; i < 4; i++)
            if (subjects[index][i].equals(subject))
                return i * 25;
        return -1;
    }

    public int getSubjectColor(String subCode) {
        switch (subCode) {
            case "math":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.math);
            case "phy":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.physics);
            case "chem":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.chemistry);
            case "eng":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.english);
            default:
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorPrimary);

        }
    }

    public String getSubjectName(String subCode) {
        switch (subCode) {
            case "math":
                return "Mathematics";
            case "phy":
                return "Physics";
            case "chem":
                return "Chemistry";
            case "eng":
                return "English";
            default:
                return "";
        }
    }

    public long getLastPostedTime() {
        return scoreSp.getLong("lastPosted", 0);
    }

    public void setLastPostedTime(long time) {
        scoreEditor.putLong("lastPosted", time).apply();
    }

    public String getForumText() {
        return scoreSp.getString("forumText", "");
    }

    public void setForumText(String forumText) {
        scoreEditor.putString("forumText", forumText).apply();
    }

    public boolean getForumSubscribed() {
        return scoreSp.getBoolean("forumSub", true);
    }

    public void setForumSubscribed(boolean newsSubscribed) {
        scoreEditor.putBoolean("forumSub", newsSubscribed).apply();
    }

    public boolean getDiscussionSubscribed() {
        return scoreSp.getBoolean("discussionSub", true);
    }

    public void setDiscussionSubscribed(boolean discussionSubscribed) {
        scoreEditor.putBoolean("discussionSub", discussionSubscribed).apply();
    }

    public boolean getNewsSubscribed() {
        return scoreSp.getBoolean("newsSub", true);
    }

    public void setNewsSubscribed(boolean newsSubscribed) {
        scoreEditor.putBoolean("newsSub", newsSubscribed).apply();
    }

    public void clearAll() {
        scoreEditor.clear().apply();
        infoEditor.clear().apply();
    }

    public String getLeaderBoardLastResponse() {
        return infoSp.getString("leaderboard", null);
    }

    public void setLeaderBoardLastResponse(String leaderBoardLastResponse) {
        infoEditor.putString("leaderboard", leaderBoardLastResponse).apply();
    }

    public String getPhoneNo() {
        return infoSp.getString("phone_no", "");
    }

    public void setPhoneNo(String no) {
        infoEditor.putString("phone_no", no).apply();
    }

    public void increaseTimesPlayed() {
        infoEditor.putInt("times_played", getTimesPlayed() + 1).commit();
    }

    public int getTimesPlayed() {
        return infoSp.getInt("times_played", 2);
    }

    public boolean isUserDataAdded() {
        return infoSp.getBoolean("is_user_data_updated", false);
    }

    public void userDataAdded() {
        infoEditor.putBoolean("is_user_data_updated", true).apply();
    }

    public int getLastAdPosition() {
        return infoSp.getInt("last_ad", 0);
    }

    public void setLastAd(int lastAd) {
        infoEditor.putInt("last_ad", lastAd).apply();
    }

    public void showAnswer(boolean isChecked) {
        infoEditor.putBoolean("answerShow", isChecked).apply();
    }

    public boolean shouldShowAnswers() {
        return infoSp.getBoolean("answerShow", true);
    }

    public JSONObject getRegistrationDetail() {
        try {
            String data = infoSp.getString("mock_data", null);
            if (data != null)
                return new JSONObject(infoSp.getString("mock_data", null));
        } catch (JSONException e) {
        }
        return null;
    }

    public void setRegistrationDetail(String registrationDetail) {
        infoEditor.putString("mock_data", registrationDetail).apply();
    }

    public int getUnreadPostCount() {
        return infoSp.getInt("post_count", 0);
    }

    public void addNewPostMessage(String message) {
        ArrayList<String> messages = getUnreadPostMessages();
        messages.add(message);
        infoEditor.putInt("post_count", getUnreadPostCount() + 1).apply();
        infoEditor.putString("post_message", new Gson().toJson(messages, ArrayList.class)).apply();
    }

    public ArrayList<String> getUnreadPostMessages() {
        String content = infoSp.getString("post_message", "");
        if (content.length() != 0)
            return new Gson().fromJson(content, ArrayList.class);
        else
            return new ArrayList<>();
    }

    public void clearUnreadCount() {
        infoEditor.putInt("post_count", 0).apply();
        infoEditor.putString("post_message", "").apply();
    }

    public void addForumData(String response) {
        infoEditor.putString("forum_data", response).apply();
    }

    public String getForumData() {
        return infoSp.getString("forum_data", null);
    }

    public boolean isOddSplash() {
        boolean value = infoSp.getBoolean("splash_counter", false);
        infoEditor.putBoolean("splash_counter", !value).apply();
        return value;
    }

    public void addNewDiscussionMessage(String message) {
        ArrayList<String> messages = getUnreadDiscussionMessages();
        messages.add(message);
        infoEditor.putInt("discussion_count", getUnreadDiscussionCount() + 1).apply();
        infoEditor.putString("discussion_message", new Gson().toJson(messages, ArrayList.class)).apply();
    }

    public ArrayList<String> getUnreadDiscussionMessages() {
        String content = infoSp.getString("discussion_message", "");
        if (content.length() != 0)
            return new Gson().fromJson(content, ArrayList.class);
        else
            return new ArrayList<>();
    }

    public int getUnreadDiscussionCount() {
        return infoSp.getInt("discussion_count", 0);
    }

    public void clearUnreadDiscussionCount() {
        infoEditor.putInt("discussion_count", 0).apply();
        infoEditor.putString("discussion_message", "").apply();
    }

}