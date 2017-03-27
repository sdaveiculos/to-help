package com.example.kevin.tohelp;

/**
 * Created by kevin on 16/03/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class Splash extends Activity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(this, 3000);
    }

    public void run(){
        //Instancio o controlador de sessão
        SessionManager sessionManager = new SessionManager();

        //TODO descomentar esse código caso queria se deslogar do app
        //FIXME criar action para se deslogar
        //sessionManager.setBooleanPreferences(Splash.this, "userLoggedOn", false);

        //Verifico se existe um usuário logado
        Boolean userLogado = sessionManager.getBooleanPreferences(Splash.this, "userLoggedOn");

        //Se houver um usuário logado, devo manda-lo para o inicio do app
        if (userLogado) {
            Intent intent = new Intent(this, MainIndex.class);
            startActivity(intent);
        }
        //Caso esteja deslogado, mando para tela de login
        else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

        finish();

    }

}
