package com.sd.spartan.easyhealth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.sd.spartan.easyhealth.activity.DonateActivity;
import com.sd.spartan.easyhealth.activity.FavoriteActivity;
import com.sd.spartan.easyhealth.activity.MessageActivity;
import com.sd.spartan.easyhealth.activity.NotifyActivity;
import com.sd.spartan.easyhealth.activity.SettingsActivity;
import com.sd.spartan.easyhealth.activity.MainViewActivity;
import com.sd.spartan.easyhealth.activity.TutorialsListActivity;
import com.sd.spartan.easyhealth.activity.UserProfileActivity;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.dialog.PopupAlert;
import com.sd.spartan.easyhealth.dialog.PopupLogOut;
import com.sd.spartan.easyhealth.dialog.PopupPreCondition;
import com.sd.spartan.easyhealth.dialog.PopupUpdate;
import com.sd.spartan.easyhealth.fragment.DiagHomeFragment;
import com.sd.spartan.easyhealth.fragment.DivisionFragment;
import com.sd.spartan.easyhealth.fragment.HomeFragment;
import com.sd.spartan.easyhealth.model.NotifyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer ;
    private TextView drawerStatusTV;
    private TextView mAdvSearchAB;
    private TextView mLanAB;
    private TextView mCountAB ;
    private ImageButton mNotifyImgBtnAB, mSearchImgBtnAB ;
    private Toolbar mToolbar ;
    private String userPass;
    public static String mDiagIdAdmin, mRegDiagIdAdmin, mRegUserIdAdmin, mSubDistrictIdAdmin,
            mUserPhnNumAdmin, mUserNameAdmin, mAdminDiagNameBan, mAdminDiagNameEng;
    private String mDiagUsername, mDiagPassword;

    private int presentFragNum = 0, count = 0 ;
    public static boolean mDiagAdmin = false ;

    private DatabaseHelperTest databaseHelperTest ;
    private final ArrayList<NotifyModel> notifyList = new ArrayList<>() ;

    private MaterialAlertDialogBuilder materialAlertDialogBuilder ;

    private Menu nav_Menu ;
    private DatabaseHelperTest mDatabaseHelperTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize() ;
        getSharedPreferenceData() ;

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        drawerStatusTV = headerView.findViewById(R.id.drawer_email);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.open,R.string.close);
        drawerToggle.syncState();

        nav_Menu = navigationView.getMenu();
        CheckNavbarDiagUser() ;


        ChangeByLanguage(nav_Menu, mDiagAdmin) ;

        databaseHelperTest = new DatabaseHelperTest(this) ;
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.MaterialAlertDialog_rounded) ;

        ToolbarInstance() ;
    }

    private void Initialize() {
        mToolbar = findViewById(R.id.toolbar);
        TextView mTitleAB = findViewById(R.id.text_toolbar_title);
        mAdvSearchAB = findViewById(R.id.text_advance_search);
        mLanAB = findViewById(R.id.text_language);
        mCountAB = findViewById(R.id.text_notify_count);
        mNotifyImgBtnAB = findViewById(R.id.img_btn_notify);
        mSearchImgBtnAB = findViewById(R.id.img_btn_search);

        setSupportActionBar(mToolbar);
        mTitleAB.setText(APP_NAME);

        mDatabaseHelperTest = new DatabaseHelperTest(this) ;
        AccessController.AnimBlink(this, mSearchImgBtnAB);
    }

    private void ToolbarInstance() {
        mNotifyImgBtnAB.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotifyActivity.class)));

        mSearchImgBtnAB.setOnClickListener(v -> {
            mSearchImgBtnAB.clearAnimation();
            SubDistrictLoad();
        });
        mAdvSearchAB.setOnClickListener(v -> SubDistrictLoad());
        mLanAB.setOnClickListener(v -> {
            AppAccess.languageEng = !AppAccess.languageEng ;
            getSharedPreferences(USER, MODE_PRIVATE).edit().putBoolean(LAN_ENG,
                    AppAccess.languageEng).apply();

            ChangeByLanguage(nav_Menu, mDiagAdmin) ;
            CheckNavbarDiagUser();
        });
    }

    private void SubDistrictLoad() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSharedPreferences(USER, MODE_PRIVATE).edit().putString(SELECT_NAV, SUBDISTRICT_SML ).apply();
        DivisionFragment subFragment = new DivisionFragment();
        transaction.replace(R.id.nav_host_fragment, subFragment,MENU_SUBDISTRICT);
        transaction.addToBackStack(null);
        transaction.commit();

        presentFragNum = 2 ;
        AppAccess.createUser = false ;
    }

    private void GetNotificationList() {
        Cursor y = mDatabaseHelperTest.checkTable(NOTIFY_COUNT, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                String unreadNum = y.getString(0);
                if(unreadNum.equalsIgnoreCase("0")){
                    mCountAB.setVisibility(View.GONE);
                    mNotifyImgBtnAB.clearAnimation();
                }else{
                    mCountAB.setVisibility(View.VISIBLE);
                    mCountAB.setText(unreadNum);
                    AccessController.AnimBlink(MainActivity.this, mNotifyImgBtnAB);
                }

                if (y.isLast())
                    break;
                y.moveToNext();
            }
        }
    }

    private void CheckNavbarDiagUser() {
        if(mUserPhnNumAdmin.equals("")){
            nav_Menu.findItem(R.id.menu_favorite).setVisible(false);
            nav_Menu.findItem(R.id.menu_user_settings).setVisible(false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new DiagHomeFragment()).addToBackStack(null).commit() ;
            mDiagAdmin = true ;
            AppAccess.clientID = DIAG_CLIENT_ID+ mRegDiagIdAdmin;
        }else{
            nav_Menu.findItem(R.id.menu_favorite).setVisible(true);
            nav_Menu.findItem(R.id.menu_user_settings).setVisible(true);
            drawerStatusTV.setText(String.format("%s%s", BD_PHN_CODE, mUserPhnNumAdmin));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment() ).addToBackStack(null).commit() ;
            mDiagAdmin = false ;

            AppAccess.clientID = USER_CLIENT_ID+ mRegUserIdAdmin;
        }

        AccessController.PushVersion(MainActivity.this, AppAccess.clientID, NUM_two );

    }

    private void getSharedPreferenceData() {
        mRegUserIdAdmin = getSharedPreferences(USER, MODE_PRIVATE).getString(REG_USER_ID, "") ;
        mUserNameAdmin = getSharedPreferences(USER, MODE_PRIVATE).getString(USER_NAME, "") ;
        userPass = getSharedPreferences(USER, MODE_PRIVATE).getString(USER_PASS, "") ;
        mUserPhnNumAdmin = getSharedPreferences(USER, MODE_PRIVATE).getString(USER_PHN_NUM, "") ;
        mSubDistrictIdAdmin = getSharedPreferences(USER, MODE_PRIVATE).getString(SUBDISTRICT_ID, "") ;

        mDiagIdAdmin = getSharedPreferences(USER, MODE_PRIVATE).getString(DIAG_ID, "") ;
        mRegDiagIdAdmin = getSharedPreferences(USER, MODE_PRIVATE).getString(REG_DIAG_ID, "") ;
        mDiagUsername = getSharedPreferences(USER, MODE_PRIVATE).getString(DIAG_USERNAME, "") ;
        mDiagPassword = getSharedPreferences(USER, MODE_PRIVATE).getString(DIAG_PASS, "") ;
        mAdminDiagNameBan = getSharedPreferences(USER, MODE_PRIVATE).getString(DIAG_NAME_BAN, "") ;
        mAdminDiagNameEng = getSharedPreferences(USER, MODE_PRIVATE).getString(DIAG_NAME_ENG, "") ;
        AppAccess.languageEng = getSharedPreferences(USER, MODE_PRIVATE).getBoolean(LAN_ENG, false) ;
    }


    private void ChangeByLanguage(Menu nav_Menu, boolean diagAdmin) {
        if(AppAccess.languageEng){
            mLanAB.setText(BANGLA);
            if(diagAdmin){
                drawerStatusTV.setText(mAdminDiagNameEng);
            }

            nav_Menu.findItem(R.id.menu_home).setTitle(R.string.menu_home_eng);
            nav_Menu.findItem(R.id.menu_user_profile).setTitle(R.string.menu_profile_eng);
            nav_Menu.findItem(R.id.menu_district).setTitle(R.string.menu_district_eng);
            nav_Menu.findItem(R.id.menu_subdistrict).setTitle(R.string.menu_subdistrict_eng);
            nav_Menu.findItem(R.id.menu_favorite).setTitle(R.string.menu_favourite_eng);
            nav_Menu.findItem(R.id.menu_user_settings).setTitle(R.string.menu_settings_eng);
            nav_Menu.findItem(R.id.menu_tutorials).setTitle(R.string.menu_tutorials_eng);

        }else{
            mLanAB.setText(ENGLISH);
            if(diagAdmin){
                drawerStatusTV.setText(mAdminDiagNameBan);
            }
            nav_Menu.findItem(R.id.menu_home).setTitle(R.string.menu_home_ban);
            nav_Menu.findItem(R.id.menu_user_profile).setTitle(R.string.menu_profile_ban);
            nav_Menu.findItem(R.id.menu_district).setTitle(R.string.menu_district_ban);
            nav_Menu.findItem(R.id.menu_subdistrict).setTitle(R.string.menu_subdistrict_ban);
            nav_Menu.findItem(R.id.menu_favorite).setTitle(R.string.menu_favourite_ban);
            nav_Menu.findItem(R.id.menu_user_settings).setTitle(R.string.menu_settings_ban);
            nav_Menu.findItem(R.id.menu_tutorials).setTitle(R.string.menu_tutorials_ban);
        }
        nav_Menu.findItem(R.id.menu_about).setTitle(R.string.menu_about_eng);
        nav_Menu.findItem(R.id.menu_privacy).setTitle(R.string.menu_privacy_eng);
        nav_Menu.findItem(R.id.menu_rate_us).setTitle(R.string.menu_rating_eng);
        nav_Menu.findItem(R.id.menu_share).setTitle(R.string.menu_share_eng);
        nav_Menu.findItem(R.id.menu_logout).setTitle(R.string.menu_logout_eng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_notification){
            startActivity(new Intent(this, NotifyActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        closeDrawer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()){
            case R.id.menu_home:
                ShowHomeFragment() ;
                setTitle(APP_NAME);
                presentFragNum = 0;
                break;
            case R.id.menu_user_profile:
                Intent intent = new Intent(this, UserProfileActivity.class) ;
                startActivity(intent);
                break;
            case R.id.menu_district:
                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(SELECT_NAV, DISTRICT ).apply();
                DivisionFragment newFragment = new DivisionFragment();
                transaction.replace(R.id.nav_host_fragment, newFragment,MENU_DISTRICT);
                transaction.addToBackStack(null);
                transaction.commit();
                presentFragNum = 1 ;
                break;
            case R.id.menu_subdistrict:
                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(SELECT_NAV, SUBDISTRICT_SML ).apply();
                DivisionFragment subFragment = new DivisionFragment();
                transaction.replace(R.id.nav_host_fragment, subFragment,MENU_SUBDISTRICT);
                transaction.addToBackStack(null);
                transaction.commit();
                presentFragNum = 2 ;
                AppAccess.createUser = false ;
                break;
            case R.id.menu_favorite:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.menu_user_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_tutorials:
                startActivity(new Intent(this, TutorialsListActivity.class));
                break;
            case R.id.menu_message:
                startActivity(new Intent(this, MessageActivity.class));
                break;
            case R.id.menu_donate:
                startActivity(new Intent(this, DonateActivity.class));
                break;
            case R.id.menu_about:
                CreatePreConditionDialog( getResources().getString(R.string.menu_about_eng), about_easy_needs) ;
                break;
            case R.id.menu_privacy:
                CreatePreConditionDialog( getResources().getString(R.string.menu_privacy_eng), privacy_and_policy) ;
                break;
            case R.id.menu_rate_us:
                PlayStoreRateLink() ;
                break;
            case R.id.menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, SHARE_PLAY_STORE_LINK + BuildConfig.APPLICATION_ID);
                sendIntent.setType(TEXT_PLAIN);
                startActivity(sendIntent);
                break;

            case R.id.menu_logout:
                PopupLogoutDialog() ;

                break;

            default:
                throw new IllegalStateException(REFRESH_THE_APPS);
        }


        return false;
    }



    private void ShowHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mDiagAdmin){
            DiagHomeFragment diagHomeFragment = new DiagHomeFragment();
            transaction.replace(R.id.nav_host_fragment, diagHomeFragment,DIAG_HOME);
        }else{
            HomeFragment homeFragment = new HomeFragment();
            transaction.replace(R.id.nav_host_fragment, homeFragment,USER_HOME);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void gotoSplashMainScreen() {
        Intent i = new Intent(MainActivity.this, MainViewActivity.class) ;
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }

    private void loadNotificationList() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, mDiagIdAdmin);
        dataMap.put(REG_USER_ID, mRegUserIdAdmin);

        getNotificationList();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.NOTIFICATION_DIAG,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){

                            JSONArray JA = object.getJSONArray(DATA);
                            for (int i=0; i<JA.length(); i++){
                                JSONObject JO = JA.getJSONObject(i);

                                boolean notiInsert = true;
                                if(Integer.parseInt(JO.getString(NOTIFY_MSG_SHOW)) >= NUM_FIVE){
                                    if(JO.getString(NOTIFY_MSG_SHOW).equalsIgnoreCase(String.valueOf(NUM_FIVE))){
                                        String versionAgoId = getSharedPreferences(NOTIFY, MODE_PRIVATE).getString(NOTIFY_VERSION_ID , "") ;
                                        if(!versionAgoId.equalsIgnoreCase(JO.getString(NOTIFY_ID))){
                                            getSharedPreferences(NOTIFY, MODE_PRIVATE).edit().putString(NOTIFY_VERSION_ID , JO.getString(NOTIFY_ID)).apply();
                                            VersionTimeCheck(JO.getString(NOTIFY_TITLE), JO.getString(NOTIFY_MSG)) ;
                                        }
                                        notiInsert = false ;
                                    } else if(JO.getString(NOTIFY_MSG_SHOW).equalsIgnoreCase(String.valueOf(NUM_SIX))){
                                        String rateAgoId = getSharedPreferences(NOTIFY, MODE_PRIVATE).getString(NOTIFY_RATE_ID, "") ;
                                        if(!rateAgoId.equalsIgnoreCase(JO.getString(NOTIFY_ID))){
                                            getSharedPreferences(NOTIFY, MODE_PRIVATE).edit().putString(NOTIFY_RATE_ID, JO.getString(NOTIFY_ID)).apply();
                                            PopupRateDialog();
                                        }
                                        notiInsert = false ;
                                    }else if(JO.getString(NOTIFY_MSG_SHOW).equalsIgnoreCase(String.valueOf(NUM_SEVEN))){
                                        String announceAgoId = getSharedPreferences(NOTIFY, MODE_PRIVATE).getString(NOTIFY_ANNOUNCE_ID, "") ;
                                        if(!announceAgoId.equalsIgnoreCase(JO.getString(NOTIFY_ID))){
                                            getSharedPreferences(NOTIFY, MODE_PRIVATE).edit().putString(NOTIFY_ANNOUNCE_ID, JO.getString(NOTIFY_ID)).apply();
                                            CreateAnnouncementDialog( JO.getString(NOTIFY_TITLE), JO.getString(NOTIFY_MSG) ) ;
                                        }
                                        notiInsert = false ;
                                    } else if(JO.getString(NOTIFY_MSG_SHOW).equalsIgnoreCase(String.valueOf(NUM_EIGHT))){
                                        CreateAlertDialog( JO.getString(NOTIFY_TITLE), JO.getString(NOTIFY_MSG) ) ;
                                        notiInsert = false ;
                                    }
                                } else{
                                    for(int j=0; j<notifyList.size(); j++){
                                        if(notifyList.get(j).getNotify_id().equalsIgnoreCase(JO.getString(NOTIFY_ID))){
                                            notiInsert = false ;
                                        }
                                    }
                                }

                                if(notiInsert){
                                    AccessController.showNotify(MainActivity.this, i, JO.getString(NOTIFY_TITLE), JO.getString(NOTIFY_MSG));
                                    databaseHelperTest.InsertNotificationTbl(JO.getString(NOTIFY_ID), JO.getString(NOTIFY_TITLE), JO.getString(NOTIFY_MSG), AppAccess.clientID) ;
                                    GetNotificationList();
                                }

                            }
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(MainActivity.this), dataMap) ;
    }

    private void VersionTimeCheck(String notify_title, String notify_msg) {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        if(!notify_title.equalsIgnoreCase(versionName) && !notify_msg.equalsIgnoreCase(versionCode+"")){
            boolean timeCheck ;
            if(AppAccess.updateMinute==0){
                timeCheck= true;
                updateRateMinute(24*60);
            } else{
                timeCheck= timeChecker(AppAccess.currentUpdateTime, AppAccess.updateMinute);
            }
            if(timeCheck){
                PopupUpdateDialog();
            }
        }

    }
    private void TimeRateCheck() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(TIME_PATTERN);
        String startTime = AppAccess.currentTime ;
        String CurrentTime = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
        try {
            long diff = Objects.requireNonNull(simpleDateFormat1.parse(CurrentTime)).getTime() - Objects.requireNonNull(simpleDateFormat1.parse(startTime)).getTime() ;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long elapsedMinutes = (diff / minutesInMilli);

            if(elapsedMinutes >= AppAccess.rateMinute){
                PopupRateDialog();
            }
        } catch (ParseException ignored) {
        }

    }

    private void getNotificationList() {
        Cursor y = databaseHelperTest.checkTable(NOTIFY_ALL, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                notifyList.add(new NotifyModel(
                        y.getString(NUM_ONE),
                        y.getString(NUM_two),
                        y.getString(NUM_THREE),
                        y.getString(NUM_FOUR),
                        y.getString(NUM_FIVE)
                ));
                if (y.isLast())
                    break;
                y.moveToNext();
            }
        }
    }



    private void PopupLogoutDialog() {
        PopupLogOut.LogOut(MainActivity.this, view -> { //yes
            PopupLogOut.backPressDialog.cancel();

            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(USER_PHN_NUM, "" ).apply();
            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(USER_PASS, "" ).apply();
            getSharedPreferences(USER, MODE_PRIVATE).edit().clear().apply();
            if(mDiagAdmin){
                AccessController.InsertFavouriteSelect(MainActivity.this, databaseHelperTest);
            }else{
                AccessController.UpdateUserProfile(MainActivity.this, mRegUserIdAdmin, mUserPhnNumAdmin, mSubDistrictIdAdmin) ;
            }
            databaseHelperTest.DeleteFavouriteTbl() ;

            gotoSplashMainScreen() ;
        }, view -> { //rate
            PlayStoreRateLink();
            PopupLogOut.backPressDialog.cancel();
        }, view -> {//no
            PopupLogOut.backPressDialog.cancel();
        });

    }
    private void PopupRateDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_rate,  null);
        TextView textRateNow = view.findViewById(R.id.text_rate_now) ;
        TextView textRemind = view.findViewById(R.id.text_remind_later) ;
        TextView textThanks = view.findViewById(R.id.text_thanks) ;

        materialAlertDialogBuilder.setView(view);
        Dialog rateDialog = materialAlertDialogBuilder.create();
        rateDialog.show();
        rateDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        textRateNow.setOnClickListener(v -> {
            rateDialog.cancel();
            rateDialog.dismiss();
            PlayStoreRateLink() ;
        });
        textRemind.setOnClickListener(v -> {
            rateDialog.cancel();
            rateDialog.dismiss();
            notificationRateMinute(2*60) ;
        });
        textThanks.setOnClickListener(v -> {
            Log.e("Tag", "thanks") ;
            rateDialog.cancel();
            rateDialog.dismiss();
            notificationRateMinute(24*60) ;
        });
    }
    private void PopupUpdateDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_update,  null);
        TextView textUpdateNow = view.findViewById(R.id.text_update_now) ;
        TextView textRemindLater = view.findViewById(R.id.text_remind_later) ;

        Dialog updateDialog ;
        materialAlertDialogBuilder.setView(view);
        updateDialog = materialAlertDialogBuilder.create();
        updateDialog.show();
        updateDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);

        textUpdateNow.setOnClickListener(v -> {
            updateDialog.cancel();
            updateRateMinute(60);
            PlayStoreRateLink();
        });
        textRemindLater.setOnClickListener(v -> {
            updateRateMinute(5*24*60);
            updateDialog.cancel();
        });




    }
    private void CreatePreConditionDialog(String title, String details) {
        PopupPreCondition.setTitle(title);
        PopupPreCondition.setDetail(details);
        PopupPreCondition.PreConditionDialog(MainActivity.this);
    }
    private void CreateAnnouncementDialog(String title, String msg) {
        PopupUpdate.setTitle(title);
        PopupUpdate.setDetail(msg);
        PopupUpdate.UpdateDialog(MainActivity.this, null, null);
        PopupUpdate.textUpdateNow.setVisibility(View.GONE);
        PopupUpdate.textRemindLater.setVisibility(View.GONE);
    }
    private void CreateAlertDialog(String title, String msg) {
        PopupAlert.setTitle(title);
        PopupAlert.setDetail(msg);
        PopupAlert.AlertDialog(MainActivity.this);
    }


    private boolean timeChecker(String previousTime, int minute) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(TIME_PATTERN);
        String CurrentTime = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
        try {
            long diff = Objects.requireNonNull(simpleDateFormat1.parse(CurrentTime)).getTime() - Objects.requireNonNull(simpleDateFormat1.parse(previousTime)).getTime() ;

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long elapsedMinutes = (diff / minutesInMilli);

            if(elapsedMinutes >= minute){
                return true ;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false ;
    }
    private void notificationRateMinute(int minute) {
        AppAccess.currentTime = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
        AppAccess.rateMinute = minute ;
    }
    private void updateRateMinute(int minute) {
        AppAccess.currentUpdateTime = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
        AppAccess.updateMinute = minute;
    }
    private void PlayStoreRateLink() {
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RATE_MARKET+getPackageName())));
        }
        catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RATE_PLAY_STORE+getPackageName())));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSharedPreferenceData() ;
        AppAccess.preCondiUserShow = getSharedPreferences(SHARED_CONDITION, MODE_PRIVATE).getBoolean(PRE_CONDITION_USER, true) ;
        AppAccess.preCondiDiagShow = getSharedPreferences(SHARED_CONDITION, MODE_PRIVATE).getBoolean(PRE_CONDITION_DIAG, true) ;

        if(mUserPhnNumAdmin.equalsIgnoreCase("") && userPass.equalsIgnoreCase("")
                && mDiagUsername.equals("") && mDiagPassword.equals("") ){
            gotoSplashMainScreen();
        }else{
            loadNotificationList() ;
        }

        if(AppAccess.rateMinute == 0 ){
            notificationRateMinute(15) ;

        }
        TimeRateCheck() ;
//        AppAccess.languageEng = getSharedPreferences(USER, MODE_PRIVATE).getBoolean(LAN_ENG, false) ;
        ChangeByLanguage(nav_Menu, mDiagAdmin) ;
        count = NUM_ZERO;
        GetNotificationList() ;

    }
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer() ;
        }else if(presentFragNum>NUM_ZERO){
            ShowHomeFragment() ;
            presentFragNum=NUM_ZERO ;
            count=NUM_ZERO;
            setTitle(APP_NAME);
        } else if (fragment != null) {
            if(count==NUM_ONE){
                databaseHelperTest.close();
                moveTaskToBack(true) ;
                notificationRateMinute(NUM_ZERO) ;
            }else{
                ToastNotify.ShowToast(this, TOAST_AGAIN_EXIT);
                count++;
            }
        }


    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        mDatabaseHelperTest.close();
    }
}