package gun0912.tedadhelper.front;

/**
 * Created by TedPark on 2017. 3. 8..
 */

public interface OnFrontAdListener {
    void onDismissed(int adType);

    void onError(String errorMessage);

    void onLoaded(int adType);

    void onFaceLoaded(com.facebook.ads.InterstitialAd facebookFrontAD);

    void onAdmobLoaded(com.google.android.gms.ads.InterstitialAd admobFrontAD);

    void onAdClicked(int adType);

    void onFacebookAdCreated(com.facebook.ads.InterstitialAd facebookFrontAD);
}
