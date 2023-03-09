package com.vpnmaster.vpnmasterinc.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.vpnmaster.vpnmasterinc.Activities.MainActivity;
import com.vpnmaster.vpnmasterinc.R;
import com.vpnmaster.vpnmasterinc.AdapterWrappers.ServerListAdapterFree;
import com.vpnmaster.vpnmasterinc.Config;
import com.vpnmaster.vpnmasterinc.Utils.Constants;
import com.vpnmaster.vpnmasterinc.model.Countries;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentFree extends Fragment implements ServerListAdapterFree.RegionListAdapterInterface {
    private RecyclerView recyclerView;
    private ServerListAdapterFree adapter;
    private ArrayList<Countries> countryArrayList;
    private FragmentVip.RegionChooserInterface regionChooserInterface;
    int server;
    InterstitialAd mInterstitialAd;
    public com.facebook.ads.InterstitialAd facebookInterstitialAd;

    boolean isAds;
    private RelativeLayout animationHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_willdev, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countryArrayList = new ArrayList<>();
        animationHolder = view.findViewById(R.id.animation_layout);

        adapter = new ServerListAdapterFree(getActivity());
        recyclerView.setAdapter(adapter);

        if (MainActivity.will_dev_33223327_all_ads_on_off && getResources().getBoolean(R.bool.facebook_list_ads) && (!Config.ads_subscription && !Config.all_subscription&& !Config.vip_subscription)) {
            isAds = true;
        } else if (MainActivity.will_dev_33223327_all_ads_on_off && getResources().getBoolean(R.bool.admob_list_ads) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {

            isAds = true;
        } else {

            isAds = false;

        }
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
            JSONArray jsonArray = new JSONArray(Constants.FREE_SERVERS);
            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Countries(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));

                if((i % 2 == 0)&&(i > 0)){
                    if (!Config.vip_subscription && !Config.all_subscription) {
                        servers.add(null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        animationHolder.setVisibility(View.GONE);
        adapter.setData(servers);
    }

    @Override
    public void onCountrySelected(Countries item) {
        if(isAds) {
            if (MainActivity.type.equals("ad")) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                }
            } else if (MainActivity.type.equals("start")) {

                StartAppAd startAppAd = new StartAppAd(Objects.requireNonNull(getActivity()));
                startAppAd.showAd();

                startAppAd.loadAd (new AdEventListener() {
                    @Override
                    public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {

                    }

                    @Override
                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                        startAppAd.close();
                    }
                });
            } else {
                if (facebookInterstitialAd != null) {
                    if (facebookInterstitialAd.isAdLoaded()) {
                        facebookInterstitialAd.show();

                    } else {
                        com.facebook.ads.InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                Log.d("ADerror", adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                facebookInterstitialAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(FragmentFree.this.getContext(), MainActivity.will_dev_33223327_fb_interstitial_id);
                        facebookInterstitialAd.loadAd(facebookInterstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
                    }
                }
            }
        }

        regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof FragmentVip.RegionChooserInterface) {
            regionChooserInterface = (FragmentVip.RegionChooserInterface) ctx;
        }
    }
}
