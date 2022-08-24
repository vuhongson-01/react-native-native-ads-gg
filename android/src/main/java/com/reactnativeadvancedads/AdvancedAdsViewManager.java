package com.reactnativeadvancedads;

import androidx.annotation.NonNull;

import android.graphics.Color;
import android.widget.FrameLayout;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReactApplicationContext;

public class AdvancedAdsViewManager extends SimpleViewManager<FrameLayout> {
    public static final String REACT_CLASS = "AdvancedAdsView";
    private NativeAdsView adsFrame;

    public AdvancedAdsViewManager(ReactApplicationContext reactContext){
        adsFrame = new NativeAdsView(reactContext);
    }
    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public FrameLayout createViewInstance(ThemedReactContext reactContext) {
      return adsFrame;
    }

    @ReactProp(name = "color")
    public void setColor(FrameLayout view, String color) {
//         view.setBackgroundColor(Color.parseColor(color));
    }
}