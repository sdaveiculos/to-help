package com.example.kevin.tohelp;

/**
 * Created by kevin on 16/03/2017.
 */
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kevin Amorim on 18/10/2016.
 * http://www.androidwarriors.com/2015/12/session-management-in-android-example.html
 */
public class SessionManager {

    /**
     * Função que irá setar uma nova preferência do tipo String, caso não exista. Se existir atualizará a mesma.
     * @param context
     * @param key
     * @param value
     */
    public void setStringPreferences(Context context, String key, String value) {

        SharedPreferences.Editor editor = context.getSharedPreferences("ToHelp", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();

    }

    /**
     * Função que irá setar uma nova preferência do tipo Boolean, caso não exista. Se existir atualizará a mesma.
     * @param context
     * @param key
     * @param value
     */
    public void setBooleanPreferences(Context context, String key, Boolean value) {

        SharedPreferences.Editor editor = context.getSharedPreferences("ToHelp", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    /**
     * Função que retornará uma preferência salva em String, caso a mesma exista. Se não existir será retornado uma string vazia.
     * @param context
     * @param key
     * @return
     */
    public String getStringPreferences(Context context, String key) {

        SharedPreferences prefs = context.getSharedPreferences("ToHelp", Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;

    }

    /**
     * Função que retornará uma preferência salva em Boolean, caso a mesma exista. Se não existir será retornado uma string vazia.
     * @param context
     * @param key
     * @return
     */
    public Boolean getBooleanPreferences(Context context, String key) {

        SharedPreferences prefs = context.getSharedPreferences("ToHelp", Context.MODE_PRIVATE);
        Boolean position = prefs.getBoolean(key, false);
        return position;

    }

}
