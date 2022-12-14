package com.reactnativeadvancedads;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Context;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import androidx.annotation.NonNull;

public class NativeAdsView extends FrameLayout {

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    private NativeAd nativeAd;
    private NativeAdView adView;
    private Context context;
    public NativeAdsView(@NonNull Context context){
        super(context);
        this.context = context;
        refreshAds();
    }

    public void refreshAds(){

        AdLoader.Builder builder = new AdLoader.Builder(context, ADMOB_AD_UNIT_ID);
        builder.forNativeAd(
            new NativeAd.OnNativeAdLoadedListener() {
            // OnLoadedListener implementation.
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {

                if (NativeAdsView.this.nativeAd != null) {
                NativeAdsView.this.nativeAd.destroy();
                }

                NativeAdsView.this.nativeAd = nativeAd;

                adView = (NativeAdView) inflate(context, R.layout.native_view, null);
                populateNativeAdView(nativeAd, adView);
                Log.d("Load Ads", "onNativeAdLoaded: load adddddddd");
                NativeAdsView.this.removeAllViews();
                NativeAdsView.this.addView(adView);
            }
            });

        VideoOptions videoOptions =
            new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions =
            new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
            builder
                .withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            String error =
                                String.format(
                                    "domain: %s, code: %d, message: %s",
                                    loadAdError.getDomain(),
                                    loadAdError.getCode(),
                                    loadAdError.getMessage());
                        }
                    })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

//     @Override
//    protected void onAttachedToWindow() {
//         Log.d("NativeAds", "onAttachedToWindow: ~~~~~~~~~");
//
//        super.onAttachedToWindow();
//    }
    @Override
    protected void onDetachedFromWindow() {
        Log.d("NativeAds", "onDetachedFromWindow: ~~~~~~~~~");
        if (this.nativeAd != null) {this.nativeAd.destroy();}
        removeAllViews();
        refreshAds();
        super.onDetachedFromWindow();

        return;
    }



    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        Log.d("Headline", "populateNativeAdView: ~~~~~~~~~~~~~~~~~" + nativeAd.getHeadline());
        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());

          Log.d("show demo", "populateNativeAdView: ------------------" + nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }
    }
}
