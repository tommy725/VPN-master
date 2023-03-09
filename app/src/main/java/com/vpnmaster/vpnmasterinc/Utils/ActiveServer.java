package com.vpnmaster.vpnmasterinc.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.vpnmaster.vpnmasterinc.model.Countries;

public class ActiveServer {
    public static void saveServer(Countries countries, Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences("activeServer", 0);
        SharedPreferences.Editor editor;
        editor = sp.edit();
        editor.putString("countryName", countries.getCountry());
        editor.putString("vpnUserName", countries.getOvpnUserName());
        editor.putString("vpnPassword", countries.getOvpnUserPassword());
        editor.putString("config", countries.getOvpn());
        editor.putString("flagUrl", countries.getFlagUrl());
        editor.commit();
    }

    public static Countries getSavedServer(Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences("activeServer", 0);
        Countries countries = new Countries(
                sp.getString("countryName", ""),
                sp.getString("flagUrl", ""),
                sp.getString("config", ""),
                sp.getString("vpnUserName", ""),
                sp.getString("vpnPassword", "")
        );

        return countries;
    }

    public static void deleteSaveServer(String key, Context context){
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences("activeServer", 0);
        SharedPreferences.Editor editor;
        editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
