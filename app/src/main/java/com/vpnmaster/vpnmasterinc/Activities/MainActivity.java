package com.vpnmaster.vpnmasterinc.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.bumptech.glide.Glide;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.vpnmaster.vpnmasterinc.Config;
import com.vpnmaster.vpnmasterinc.Fragments.FragmentVip;
import com.vpnmaster.vpnmasterinc.R;
import com.vpnmaster.vpnmasterinc.Utils.ActiveServer;
import com.vpnmaster.vpnmasterinc.model.Countries;
import com.startapp.sdk.adsbase.StartAppSDK;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import top.oneconnectapi.app.OpenVpnApi;
import top.oneconnectapi.app.core.OpenVPNThread;


public class MainActivity extends ContentsActivity implements FragmentVip.RegionChooserInterface, PurchasesUpdatedListener, BillingClientStateListener {

    private static final String CHANNEL_ID = "vpn";
    public static String will_dev_33223327_facebook_reward_id;
    public static String will_dev_33223327_admob_reward;
    public static String will_dev_33223327_official_dont_change_value;
    public static Countries selectedCountry = null;
    private boolean isFirst = true;

    public static String type = "";
    public static String will_dev_33223327_admob_id = "";
    public static String will_dev_33223327_admob_banner_id = "";
    public static String admob_interstitial_id = "";
    public static String will_dev_33223327_admob_native_id = "";
    public static String will_dev_33223327_fb_banner_id = "";
    public static String will_dev_33223327_fb_native_id = "";
    public static String will_dev_33223327_fb_interstitial_id = "";
    public static boolean will_dev_33223327_all_ads_on_off = false;
    public static boolean will_dev_33223327_remove_premium = false;
    public static boolean will_dev_33223327_remove_all_video_ads_button = false;
    private InterstitialAd mInterstitialAdMob;

    private OpenVPNThread vpnThread = new OpenVPNThread();

    private BillingClient billingClient;
    private Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            Config.ALL_MONTH,
            Config.THREE_MONTH,
            Config.SIX_MONTH,
            Config.TWELVE_MONTH));

    private void connectToBillingService() {
        if (!billingClient.isReady()) {
            billingClient.startConnection(this);
        }
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            querySkuDetailsAsync(
                    BillingClient.SkuType.SUBS,
                    allSubs
            );
            queryPurchases();
        }
        updateSubscription();
    }

    @Override
    public void onBillingServiceDisconnected() {
        connectToBillingService();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

    }

    private void queryPurchases() {
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        List<Purchase> purchases = result.getPurchasesList();
        List<String> skus = new ArrayList<>();

        if (purchases != null) {
            int i = 0;
            for (Purchase purchase : purchases) {
                skus.add(purchase.getSkus().get(i));
                Log.v("CHECKBILLING", purchase.getSkus().get(i));
                i++;
            }

            if (skus.contains(Config.ALL_MONTH) ||
                    skus.contains(Config.THREE_MONTH) ||
                    skus.contains(Config.SIX_MONTH) ||
                    skus.contains(Config.TWELVE_MONTH)
            ) {
                Config.all_subscription = true;
            }
        }
    }

    private void querySkuDetailsAsync(@BillingClient.SkuType String skuType, List<String> skuList) {
        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build();

        billingClient.querySkuDetailsAsync(
                params, (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        for (SkuDetails details : skuDetailsList) {
                            skusWithSkuDetails.put(details.getSku(), details);
                        }
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (type.equals("ad")) {
            RequestConfiguration.Builder requestBuilder = new RequestConfiguration.Builder();
            MobileAds.setRequestConfiguration(requestBuilder.build());
        } else {
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);

            //Initialize facebook ads
            AudienceNetworkAds.initialize(this);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        StartAppSDK.setTestAdsEnabled(Config.startAppTestMode);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
            }
        });

        if (TextUtils.isEmpty(type)) {
            type = "";
        }

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(7)
                .threshold(4)
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        showMessage("Thank you for your feedback!", "");
                        ratingDialog.dismiss();
                    }
                })
                .negativeButtonText("Never")
                .negativeButtonTextColor(R.color.grey_500)
                .playstoreUrl("https://play.google.com/store/apps/details?id=" + this.getPackageName())
                .onRatingBarFormSumbit(feedback -> {}).build();

        ratingDialog.show();

        billingClient = BillingClient
                .newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToBillingService();

        Intent intent = getIntent();

        if(getIntent().getExtras() != null) {
            selectedCountry = getIntent().getExtras().getParcelable("c");
            updateUI("LOAD");

            if (!Utility.isOnline(getApplicationContext())) {
                showMessage("No Internet Connection", "error");
            } else {
                showInterstitialAndConnect();
            }
        } else {
            if(selectedCountry != null) {
                updateUI("CONNECTED");
                updateCurrentVipServerIcon(selectedCountry.getFlagUrl());
                flagName.setText(selectedCountry.getCountry());
            }
        }

        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
            will_dev_33223327_admob_banner_id = intent.getStringExtra("will_dev_33223327_admob_banner");
            admob_interstitial_id = intent.getStringExtra("admob_interstitial");
            will_dev_33223327_fb_banner_id = intent.getStringExtra("will_dev_33223327_fb_banner");
            will_dev_33223327_fb_interstitial_id = intent.getStringExtra("will_dev_33223327_fb_interstitial");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        inAppUpdate();
    }

    public void prepareVpn() {

        updateCurrentVipServerIcon(selectedCountry.getFlagUrl());
        flagName.setText(selectedCountry.getCountry());

        if (Utility.isOnline(getApplicationContext())) {

            if(selectedCountry != null) {
                Intent intent = VpnService.prepare(this);
                Log.v("CHECKSTATE", "start");

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else
                    startVpn(); //have already permission
            } else {
                showMessage("Please select a server first", "");
            }

        } else {
                showMessage("No Internet Connection", "error");
        }
    }

    protected void startVpn() {
        try {
                ActiveServer.saveServer(MainActivity.selectedCountry, MainActivity.this);
            OpenVpnApi.startVpn(this, selectedCountry.getOvpn(), selectedCountry.getCountry(), selectedCountry.getOvpnUserName(), selectedCountry.getOvpnUserPassword());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updateUI(intent.getStringExtra("state"));
                Log.v("CHECKSTATE", intent.getStringExtra("state"));

                if (isFirst) {
                    if (ActiveServer.getSavedServer(MainActivity.this).getCountry() != null) {
                        selectedCountry = ActiveServer.getSavedServer(MainActivity.this);
                        updateCurrentVipServerIcon(selectedCountry.getFlagUrl());
                        flagName.setText(selectedCountry.getCountry());
                    }

                    isFirst = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";

                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void disconnectFromVnp() {
        try {
            vpnThread.stop();
            updateUI("DISCONNECTED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegionSelected(Countries item) {

        //selectedCountry = item;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_mainwill_dev;
    }

    private void inAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, 11);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            showMessage("Start Download", "");
            if (resultCode != RESULT_OK) {
                Log.d("Update", "Update failed" + resultCode);
            }
        }

        if (resultCode == RESULT_OK) {
            startVpn();
        } else {
            showMessage("Permission Denied", "error");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VPN";
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateCurrentVipServerIcon(String serverIcon) {
        Glide.with(this)
                .load(serverIcon)
                .into(imgFlag);
    }

    public void checkSelectedCountry() {
        if (selectedCountry == null) {
            updateUI("DISCONNECT");
            showMessage("Please select a server first", "");
        } else {
            showInterstitialAndConnect();
            updateUI("LOAD");
        }
    }
}
