package com.vpnmaster.vpnmasterinc.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.vpnmaster.vpnmasterinc.Activities.MainActivity;
import com.vpnmaster.vpnmasterinc.R;
import com.vpnmaster.vpnmasterinc.Activities.UnlockAllActivity;
import com.vpnmaster.vpnmasterinc.AdapterWrappers.ServerListAdapterVip;
import com.vpnmaster.vpnmasterinc.Config;
import com.facebook.ads.*;
import com.vpnmaster.vpnmasterinc.Utils.Constants;
import com.vpnmaster.vpnmasterinc.model.Countries;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentVip extends Fragment implements ServerListAdapterVip.RegionListAdapterInterface {

    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private RegionChooserInterface regionChooserInterface;
    private static RewardedAd rewardedAd;
    private RelativeLayout animationHolder;
    private static final String TAG = "Facebok Ads";
    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;
    private RewardedAd mRewardedAd;
    private RewardedVideoAd rewardedVideoAd;
    private static SharedPreferences sharedPreferences;
    static Countries countryy;
    public static Context context;
    public static boolean viewSet = false;
    static View view;
    public static boolean fbAdIsLoading = true;
    public static boolean googleAdIsLoading = true;
    public static boolean googleAdResune = false;
    public static boolean fbAdResume = false;
    public static ProgressDialog progressdialog;
    private static PopupWindow pw;
    private static View popupView;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdSettings.addTestDevice("c4894289-bd58-4ec5-b608-192469edce5a");
        AdSettings.addTestDevice("4cbd7f01-b2fb-4d12-ac35-f399d9f30351");
        AdSettings.addTestDevice("ad883e4f-8d84-4631-afdb-12104e62f4b8");
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one_dev_willdev, container, false);

        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("! Just a moment finding best video for you !");
        progressdialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        sharedPreferences = getContext().getSharedPreferences("userRewarded",Context.MODE_PRIVATE);

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);
        mPurchaseLayout.setVisibility(View.GONE);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
                initAdMob();
            }
        });

        LayoutInflater pInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = pInflater.inflate(R.layout.layout_bottom_sheetwilldev, (ViewGroup) view, false);
        pw = new PopupWindow(popupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, true);
        pw.setAnimationStyle(R.style.Animation);
        initOnClick();

        adapter = new ServerListAdapterVip(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadServers();
    }

    private void loadServers() {

        ArrayList<Countries> servers = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(Constants.PREMIUM_SERVERS);

            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Countries(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setData(servers);
        animationHolder.setVisibility(View.GONE);
    }

    @Override
    public void onCountrySelected(Countries item) {

             regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Countries item);
    }

    public static void unblockServer()
    {

        TextView title = popupView.findViewById(R.id.title);
        LinearLayout lyButtons = popupView.findViewById(R.id.lyButtons);
        View view = popupView.findViewById(R.id.view);

        if (!MainActivity.will_dev_33223327_remove_all_video_ads_button) {
            lyButtons.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

        FrameLayout mainLayout = popupView.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> {
            pw.dismiss();
        });

        popupView.findViewById(R.id.but_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UnlockAllActivity.class));
                pw.dismiss();
            }
        });

        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public static void onItemClick(Countries country)
    {
        countryy = country;
        if (Config.vip_subscription || Config.all_subscription || !MainActivity.will_dev_33223327_remove_premium) {
            Intent intent=new Intent(context, MainActivity.class);
            intent.putExtra("c", country);
            intent.putExtra("type",MainActivity.type);
            intent.putExtra("will_dev_33223327_admob_banner",MainActivity.will_dev_33223327_admob_banner_id);
            intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
            intent.putExtra("will_dev_33223327_fb_banner",MainActivity.will_dev_33223327_fb_banner_id);
            intent.putExtra("will_dev_33223327_fb_interstitial",MainActivity.will_dev_33223327_fb_interstitial_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
        else
        {
            unblockServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        googleAdIsLoading = true;
        fbAdIsLoading = true;
        googleAdResune = false;
        fbAdResume = false;
    }

    public void initAdMob() {
        //ADMOB
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getActivity(), MainActivity.will_dev_33223327_admob_reward,
            adRequest, new RewardedAdLoadCallback(){
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                    mRewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {

                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {

                            Log.d(TAG, "Ad was dismissed.");
                            mRewardedAd = null;
                        }
                    });
                }
            });
    }

    private void initOnClick() {
        popupView.findViewById(R.id.watch_ads).setOnClickListener(v -> {

            progressdialog.show();

            if(mRewardedAd != null) {
                mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("c", countryy);
                        intent.putExtra("type", MainActivity.type);
                        intent.putExtra("will_dev_33223327_admob_banner", MainActivity.will_dev_33223327_admob_banner_id);
                        intent.putExtra("admob_interstitial", MainActivity.admob_interstitial_id);
                        intent.putExtra("will_dev_33223327_fb_banner", MainActivity.will_dev_33223327_fb_banner_id);
                        intent.putExtra("will_dev_33223327_fb_interstitial", MainActivity.will_dev_33223327_fb_interstitial_id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("adWatched", true);
                        editor.apply();
                        mRewardedAd = null;
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                });
            } else {
                Toast.makeText(context.getApplicationContext(), "Ad Not Available or Buy Subscription", Toast.LENGTH_SHORT).show();
            }

            progressdialog.dismiss();
            pw.dismiss();
        });

        popupView.findViewById(R.id.watch_face_ads).setOnClickListener(v -> {

            progressdialog.show();

            rewardedVideoAd = new RewardedVideoAd(getActivity(), MainActivity.will_dev_33223327_facebook_reward_id);
            RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    fbAdIsLoading = false;
                    progressdialog.dismiss();
                    Toast.makeText(context.getApplicationContext(), "Ad Not Available or Buy Subscription", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {

                    Log.d(TAG, "FB Rewarded video ad is loaded and ready to be displayed!");
                    progressdialog.dismiss();
                    Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
                    fbAdIsLoading = false;
                    rewardedVideoAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {

                    Log.d(TAG, "FB Rewarded video ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                    Log.d(TAG, "FB Rewarded video ad impression logged!");
                }

                @Override
                public void onRewardedVideoCompleted() {

                    Log.d(TAG, "Rewarded video completed!");
                    Intent intent=new Intent(context, MainActivity.class);
                    intent.putExtra("c",countryy);
                    intent.putExtra("type",MainActivity.type);
                    intent.putExtra("will_dev_33223327_admob_banner",MainActivity.will_dev_33223327_admob_banner_id);
                    intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
                    intent.putExtra("will_dev_33223327_fb_banner",MainActivity.will_dev_33223327_fb_banner_id);
                    intent.putExtra("will_dev_33223327_fb_interstitial",MainActivity.will_dev_33223327_fb_interstitial_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                }

                @Override
                public void onRewardedVideoClosed() {

                    Log.d(TAG, "FB Rewarded video ad closed!");
                }
            };
            rewardedVideoAd.loadAd(
                    rewardedVideoAd.buildLoadAdConfig()
                            .withAdListener(rewardedVideoAdListener)
                            .build());
            pw.dismiss();

        });

        popupView.findViewById(R.id.watch_start_ads).setOnClickListener(v -> {

            StartAppSDK.setTestAdsEnabled(Config.startAppTestMode);

            progressdialog.show();

            StartAppAd startAppAd = new StartAppAd(context.getApplicationContext());

            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "Rewarded video completed!");

                    Intent intent=new Intent(context, MainActivity.class);
                    intent.putExtra("c",countryy);
                    intent.putExtra("type",MainActivity.type);
                    intent.putExtra("will_dev_33223327_admob_banner",MainActivity.will_dev_33223327_admob_banner_id);
                    intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
                    intent.putExtra("will_dev_33223327_fb_banner",MainActivity.will_dev_33223327_fb_banner_id);
                    intent.putExtra("will_dev_33223327_fb_interstitial",MainActivity.will_dev_33223327_fb_interstitial_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                    progressdialog.dismiss();
                }
            });

            CountDownTimer connectionTimedOut = new CountDownTimer(10000, 1000) {
                public void onFinish() {
                    Toast.makeText(context.getApplicationContext(), "Please try again later or Buy Subscription", Toast.LENGTH_SHORT).show();
                    progressdialog.dismiss();
                }
                public void onTick(long millisUntilFinished) {
                }
            }.start();

            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    startAppAd.showAd();
                    progressdialog.dismiss();
                    connectionTimedOut.cancel();

                    new CountDownTimer(30000, 1000) {
                        public void onFinish() {

                            Intent intent=new Intent(context, MainActivity.class);
                            intent.putExtra("c",countryy);
                            intent.putExtra("type",MainActivity.type);
                            intent.putExtra("will_dev_33223327_admob_banner",MainActivity.will_dev_33223327_admob_banner_id);
                            intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
                            intent.putExtra("will_dev_33223327_fb_banner",MainActivity.will_dev_33223327_fb_banner_id);
                            intent.putExtra("will_dev_33223327_fb_interstitial",MainActivity.will_dev_33223327_fb_interstitial_id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }

                        public void onTick(long millisUntilFinished) {
                        }
                    }.start();
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    progressdialog.dismiss();
                    connectionTimedOut.cancel();
                    Toast.makeText(context.getApplicationContext(), "Ad Not Available or Buy Subscription", Toast.LENGTH_SHORT).show();
                    Log.d("StartApp Failed", ad.getErrorMessage());
                }
            });

            pw.dismiss();
        });
    }
}