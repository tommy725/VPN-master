package com.vpnmaster.vpnmasterinc.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.vpnmaster.vpnmasterinc.R;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage slider1 = new SliderPage();
        slider1.setTitle("secure your privacy with VPN Servers");
        slider1.setDescription("Our VPN App is Very Fast & Secure. And Easy to Use");
        slider1.setImageDrawable(R.drawable.screen_one);
        slider1.setBgColor(getResources().getColor(R.color.primary_dark));
        slider1.setTitleColor(getResources().getColor(R.color.white));
        slider1.setDescColor(getResources().getColor(R.color.white));

        SliderPage slider2 = new SliderPage();
        slider2.setTitle("Use Premium VPN Servers");
        slider2.setDescription("Become premium member and get more Secure Servers");
        slider2.setImageDrawable(R.drawable.screen_two);
        slider2.setBgColor(getResources().getColor(R.color.primary_dark));
        slider2.setTitleColor(getResources().getColor(R.color.white));
        slider2.setDescColor(getResources().getColor(R.color.white));

        addSlide(AppIntroFragment.newInstance(slider1));
        addSlide(AppIntroFragment.newInstance(slider2));


        setIndicatorColor(getResources().getColor(R.color.white), getResources().getColor(R.color.grayishblue)) ;
        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currenFragment) {
        startActivity(new Intent(getApplicationContext(), AcceptPrivacyPolicy.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currenFragment) {
        startActivity(new Intent(getApplicationContext(), AcceptPrivacyPolicy.class));
        finish();
    }
}

