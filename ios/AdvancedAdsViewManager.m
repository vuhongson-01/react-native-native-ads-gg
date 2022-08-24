#import <MapKit/MapKit.h>
#import <React/RCTViewManager.h>
#import <GoogleMobileAds/GoogleMobileAds.h>
static NSString *const TestAdUnit = @"ca-app-pub-3940256099942544/3986624511";
@interface AdvancedAdsViewManager : RCTViewManager<GADNativeAdLoaderDelegate,
                                                 GADVideoControllerDelegate,
                                                 GADNativeAdDelegate>

   /// You must keep a strong reference to the GADAdLoader during the ad loading
   /// process.
   @property(nonatomic, strong) GADAdLoader *adLoader;

   /// The native ad view that is being presented.
   @property(nonatomic, strong) GADNativeAdView *nativeAdView;

   /// The height constraint applied to the ad view, where necessary.
   @property(nonatomic, strong) NSLayoutConstraint *heightConstraint;
@end

@implementation AdvancedAdsViewManager

RCT_EXPORT_MODULE(AdvancedAdsView)

- (UIView *)view
{
  //   UIView *catView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
   self.nativeAdView  = [[NSBundle mainBundle] loadNibNamed:@"AdsView" owner:self options:nil].firstObject;
//   catView.backgroundColor = [UIColor blueColor];
//  return catView;
   GADVideoOptions *videoOptions = [[GADVideoOptions alloc] init];
   videoOptions.startMuted = true;

   self.adLoader = [[GADAdLoader alloc] initWithAdUnitID:TestAdUnit
                                      rootViewController:[UIApplication sharedApplication].delegate.window.rootViewController
                                                 adTypes:@[ GADAdLoaderAdTypeNative ]
                                                 options:@[ videoOptions ]];
   self.adLoader.delegate = self;
   [self.adLoader loadRequest:[GADRequest request]];
     return self.nativeAdView;
}

- (UIImage *)imageForStars:(NSDecimalNumber *)numberOfStars {
  double starRating = numberOfStars.doubleValue;
  if (starRating >= 5) {
    return [UIImage imageNamed:@"stars_5"];
  } else if (starRating >= 4.5) {
    return [UIImage imageNamed:@"stars_4_5"];
  } else if (starRating >= 4) {
    return [UIImage imageNamed:@"stars_4"];
  } else if (starRating >= 3.5) {
    return [UIImage imageNamed:@"stars_3_5"];
  } else {
    return nil;
  }
}

 RCT_CUSTOM_VIEW_PROPERTY(color, NSString, UIView)
 {
  //  [view setBackgroundColor:[self hexStringToColor:json]];
 }

//  - hexStringToColor:(NSString *)stringToConvert
//  {
//    NSString *noHashString = [stringToConvert stringByReplacingOccurrencesOfString:@"#" withString:@""];
//    NSScanner *stringScanner = [NSScanner scannerWithString:noHashString];

//    unsigned hex;
//    if (![stringScanner scanHexInt:&hex]) return nil;
//    int r = (hex >> 16) & 0xFF;
//    int g = (hex >> 8) & 0xFF;
//    int b = (hex) & 0xFF;

//    return [UIColor colorWithRed:r / 255.0f green:g / 255.0f blue:b / 255.0f alpha:1.0f];
//  }

 #pragma mark GADAdLoaderDelegate implementation
 //----------------------------------------------------------------------------------------------------
 - (void)adLoader:(GADAdLoader *)adLoader didFailToReceiveAdWithError:(NSError *)error {
   NSLog(@"%@ failed with error: %@", adLoader, error);
 //  self.refreshButton.enabled = YES;
 }

 #pragma mark GADNativeAdLoaderDelegate implementation
 //----------------------------------------------------------------------------------------------------
 - (void)adLoader:(GADAdLoader *)adLoader didReceiveNativeAd:(GADNativeAd *)nativeAd {
 //  self.refreshButton.enabled = YES;

   GADNativeAdView *nativeAdView = self.nativeAdView;

   // Deactivate the height constraint that was set when the previous video ad loaded.
   self.heightConstraint.active = NO;

   // Set ourselves as the ad delegate to be notified of native ad events.
   nativeAd.delegate = self;

   // Populate the native ad view with the native ad assets.
   // The headline and mediaContent are guaranteed to be present in every native ad.
   ((UILabel *)nativeAdView.headlineView).text = nativeAd.headline;
   nativeAdView.mediaView.mediaContent = nativeAd.mediaContent;

   // This app uses a fixed width for the GADMediaView and changes its height
   // to match the aspect ratio of the media content it displays.
   if (nativeAd.mediaContent.aspectRatio > 0) {
     self.heightConstraint =
         [NSLayoutConstraint constraintWithItem:nativeAdView.mediaView
                                      attribute:NSLayoutAttributeHeight
                                      relatedBy:NSLayoutRelationEqual
                                         toItem:nativeAdView.mediaView
                                      attribute:NSLayoutAttributeWidth
                                     multiplier:(1 / nativeAd.mediaContent.aspectRatio)
                                       constant:0];
     self.heightConstraint.active = YES;
   }

   if (nativeAd.mediaContent.hasVideoContent) {
     // By acting as the delegate to the GADVideoController, this ViewController
     // receives messages about events in the video lifecycle.
     nativeAd.mediaContent.videoController.delegate = self;
   }

   // These assets are not guaranteed to be present. Check that they are before
   // showing or hiding them.
   ((UILabel *)nativeAdView.bodyView).text = nativeAd.body;
   nativeAdView.bodyView.hidden = nativeAd.body ? NO : YES;

   [((UIButton *)nativeAdView.callToActionView) setTitle:nativeAd.callToAction
                                                forState:UIControlStateNormal];
   nativeAdView.callToActionView.hidden = nativeAd.callToAction ? NO : YES;

   ((UIImageView *)nativeAdView.iconView).image = nativeAd.icon.image;
   nativeAdView.iconView.hidden = nativeAd.icon ? NO : YES;

   ((UIImageView *)nativeAdView.starRatingView).image = [self imageForStars:nativeAd.starRating];
   nativeAdView.starRatingView.hidden = nativeAd.starRating ? NO : YES;

   ((UILabel *)nativeAdView.storeView).text = nativeAd.store;
   nativeAdView.storeView.hidden = nativeAd.store ? NO : YES;

   ((UILabel *)nativeAdView.priceView).text = nativeAd.price;
   nativeAdView.priceView.hidden = nativeAd.price ? NO : YES;

   ((UILabel *)nativeAdView.advertiserView).text = nativeAd.advertiser;
   nativeAdView.advertiserView.hidden = nativeAd.advertiser ? NO : YES;

   // In order for the SDK to process touch events properly, user interaction
   // should be disabled.
   nativeAdView.callToActionView.userInteractionEnabled = NO;

   // Associate the native ad view with the native ad object. This is
   // required to make the ad clickable.
   // Note: this should always be done after populating the ad views.
   nativeAdView.nativeAd = nativeAd;
 }

 #pragma mark GADVideoControllerDelegate implementation

 - (void)videoControllerDidEndVideoPlayback:(GADVideoController *)videoController {
 //  self.videoStatusLabel.text = @"Video playback has ended.";
   return;
 }

 #pragma mark GADNativeAdDelegate

 - (void)nativeAdDidRecordClick:(GADNativeAd *)nativeAd {
   NSLog(@"%s", __PRETTY_FUNCTION__);
 }

 - (void)nativeAdDidRecordImpression:(GADNativeAd *)nativeAd {
   NSLog(@"%s", __PRETTY_FUNCTION__);
 }

 - (void)nativeAdWillPresentScreen:(GADNativeAd *)nativeAd {
   NSLog(@"%s", __PRETTY_FUNCTION__);
 }

 - (void)nativeAdWillDismissScreen:(GADNativeAd *)nativeAd {
   NSLog(@"%s", __PRETTY_FUNCTION__);
 }

 - (void)nativeAdDidDismissScreen:(GADNativeAd *)nativeAd {
   NSLog(@"%s", __PRETTY_FUNCTION__);
 }

 - (void)nativeAdWillLeaveApplication:(GADNativeAd *)nativeAd {
   NSLog(@"%s", __PRETTY_FUNCTION__);
 }

@end
