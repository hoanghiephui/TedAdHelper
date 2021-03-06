package gun0912.tedadhelper.backpress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import gun0912.tedadhelper.R;
import gun0912.tedadhelper.TedAdHelper;
import gun0912.tedadhelper.nativead.OnNativeAdListener;
import gun0912.tedadhelper.nativead.TedNativeAd;
import gun0912.tedadhelper.util.AppUtil;
import gun0912.tedadhelper.util.SharedPreferenceUtil;


public class TedBackPressDialog extends AppCompatActivity {

    private static final String EXTRA_APP_NAME = "app_name";
    private static final String EXTRA_FACEBOOK_KEY = "facebook_key";
    private static final String EXTRA_ADMOB_KEY = "admob_key";
    private static final String EXTRA_AD_PRIORITY_LIST = "ad_priority_list";
    private static final String EXTRA_SHOW_REVIEW_BUTTON = "show_review_button";
    private static final String EXTRA_ADMOB_NATIVE_TYPE = "admob_native_type";

    private static OnBackPressListener onBackPressListener;
    private static TedAdHelper.ImageProvider imageProvider;
    ViewGroup adViewContainer;
    TextView tvFinish;
    TextView tvReview;
    View dividerBtn;
    String appName;
    String facebookKey;
    String admobKey;
    boolean showReviewButton;
    @TedAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType;
    TedNativeAd adViewNativeAd;
    ArrayList<Integer> adPriorityList;

    public static void startFacebookDialog(Activity activity, String appName, String facebookKey, OnBackPressListener onBackPressListener) {
        startDialog(activity, appName, facebookKey, null, TedAdHelper.AD_FACEBOOK,TedAdHelper.ADMOB_NATIVE_AD_TYPE.NATIVE_EXPRESS, onBackPressListener);
    }

    public static void startDialog(Activity activity, String appName, String facebookKey, String admobKey, int adPriority, @TedAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType, OnBackPressListener onBackPressListener) {
        startDialog(activity, appName, facebookKey, admobKey, adPriority, admobNativeAdType,true, onBackPressListener);
    }

    public static void startDialog(Activity activity, String appName, String facebookKey, String admobKey, int adPriority, @TedAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType, boolean showReviewButton, OnBackPressListener onBackPressListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == TedAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = TedAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = TedAdHelper.AD_FACEBOOK;
        }
        startDialog(activity, appName, facebookKey, admobKey, tempAdPriorityList,admobNativeAdType, showReviewButton, onBackPressListener);

    }

    public static void startDialog(Activity activity, String appName, String facebookKey, String admobKey, Integer[] adPriorityList, @TedAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType, boolean showReviewButton, OnBackPressListener onBackPressListener) {
        startDialog(activity, appName, facebookKey, admobKey, adPriorityList,admobNativeAdType, showReviewButton, onBackPressListener, null);
    }

    public static void startDialog(Activity activity, String appName, String facebookKey, String admobKey, Integer[] adPriorityList, @TedAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType, boolean showReviewButton, OnBackPressListener onBackPressListener, TedAdHelper.ImageProvider imageProvider) {
        Intent intent = new Intent(activity, TedBackPressDialog.class);
        intent.putExtra(EXTRA_APP_NAME, appName);
        intent.putExtra(EXTRA_FACEBOOK_KEY, facebookKey);
        intent.putExtra(EXTRA_ADMOB_KEY, admobKey);
        intent.putExtra(EXTRA_SHOW_REVIEW_BUTTON, showReviewButton);
        intent.putExtra(EXTRA_AD_PRIORITY_LIST, new ArrayList<>(Arrays.asList(adPriorityList)));
        intent.putExtra(EXTRA_ADMOB_NATIVE_TYPE,admobNativeAdType);

        if (onBackPressListener == null) {
            throw new RuntimeException("OnBackPressListener can not null");
        }
        TedBackPressDialog.onBackPressListener = onBackPressListener;
        TedBackPressDialog.imageProvider = imageProvider;

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void startAdmobDialog(Activity activity, String appName, String admobKey, OnBackPressListener onBackPressListener) {
        startDialog(activity, appName, null, admobKey, TedAdHelper.AD_ADMOB, TedAdHelper.ADMOB_NATIVE_AD_TYPE.NATIVE_EXPRESS, onBackPressListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setupFromSavedInstanceState(savedInstanceState);

        setContentView(R.layout.dialog_backpress);


        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width = (int) (display.getWidth() * 0.9);


        setFinishOnTouchOutside(false);

        initView();
        showReviewButton();
        checkReview();
        adViewNativeAd = new TedNativeAd(adViewContainer, this, appName, facebookKey, admobKey, imageProvider,admobNativeAdType);

        adViewNativeAd.loadAD(adPriorityList, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {

                if (onBackPressListener != null) {
                    onBackPressListener.onError(errorMessage);
                }
            }

            @Override
            public void onLoaded(int adType) {
                if (onBackPressListener != null) {
                    onBackPressListener.onLoaded(adType);
                }
            }

            @Override
            public void onAdClicked(int adType) {
                if (onBackPressListener != null) {
                    onBackPressListener.onAdClicked(adType);
                }
            }
        });


    }

    private void showReviewButton() {

        if (!showReviewButton) {
            dividerBtn.setVisibility(View.GONE);
            tvReview.setVisibility(View.GONE);
        }

    }

    private void checkReview() {

        boolean isReview = SharedPreferenceUtil.getSharedPreference(this, SharedPreferenceUtil.REVIEW, false);
        if (isReview) {
            dividerBtn.setVisibility(View.GONE);
            tvReview.setVisibility(View.GONE);
        }


    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {


        // 액티버티를 처음 생성중인지, 재생성 중인지 확인한다.
        if (savedInstanceState != null) {
            // 저장된 상태를 복구한다.
            appName = savedInstanceState.getString(EXTRA_APP_NAME);
            facebookKey = savedInstanceState.getString(EXTRA_FACEBOOK_KEY);
            admobKey = savedInstanceState.getString(EXTRA_ADMOB_KEY);
            adPriorityList = savedInstanceState.getIntegerArrayList(EXTRA_AD_PRIORITY_LIST);
            showReviewButton = savedInstanceState.getBoolean(EXTRA_SHOW_REVIEW_BUTTON);
            admobNativeAdType = savedInstanceState.getInt(EXTRA_ADMOB_NATIVE_TYPE);
        } else {

            // 새 객체를 위해 멤버 변수를 초기화 한다.
            appName = getIntent().getStringExtra(EXTRA_APP_NAME);
            facebookKey = getIntent().getStringExtra(EXTRA_FACEBOOK_KEY);
            admobKey = getIntent().getStringExtra(EXTRA_ADMOB_KEY);
            adPriorityList = getIntent().getIntegerArrayListExtra(EXTRA_AD_PRIORITY_LIST);
            showReviewButton = getIntent().getBooleanExtra(EXTRA_SHOW_REVIEW_BUTTON, false);
            admobNativeAdType = getIntent().getIntExtra(EXTRA_ADMOB_NATIVE_TYPE, TedAdHelper.ADMOB_NATIVE_AD_TYPE.NATIVE_EXPRESS);
        }


    }

    private void initView() {

        adViewContainer = (ViewGroup) findViewById(R.id.adview_container);
        dividerBtn = findViewById(R.id.divider_btn);

        tvReview = (TextView) findViewById(R.id.tv_review);
        tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReviewClick();
            }
        });
        tvFinish = (TextView) findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                onFinishClick();
            }
        });

    }

    private void onReviewClick() {

        AppUtil.openPlayStore(this, getPackageName());
        SharedPreferenceUtil.putSharedPreference(this, SharedPreferenceUtil.REVIEW, true);

        if (onBackPressListener != null) {
            onBackPressListener.onReviewClick();
        }

    }

    private void onFinishClick() {
        finish();
        overridePendingTransition(0, 0);

        if (onBackPressListener != null) {
            onBackPressListener.onFinish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // 현재 상태를 저장한다.
        savedInstanceState.putString(EXTRA_APP_NAME, appName);
        savedInstanceState.putString(EXTRA_FACEBOOK_KEY, facebookKey);
        savedInstanceState.putString(EXTRA_ADMOB_KEY, admobKey);
        savedInstanceState.putIntegerArrayList(EXTRA_AD_PRIORITY_LIST, adPriorityList);
        savedInstanceState.putInt(EXTRA_ADMOB_NATIVE_TYPE,admobNativeAdType);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        if (adViewNativeAd != null) {
            adViewNativeAd.onDestroy();
        }
        onBackPressListener = null;
        imageProvider = null;

        super.onDestroy();
    }
}
