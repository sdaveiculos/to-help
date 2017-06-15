package com.example.kevin.tohelp;

/**
 * Created by kevin on 16/03/2017.
 */
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainIndex extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Crio o session manager
    SessionManager sessionManager = new SessionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        //Campos a serem substituidos pelas variáveis salvas nas preferências
        TextView nome   = (TextView) headerView.findViewById(R.id.username);
        TextView email  = (TextView) headerView.findViewById(R.id.email);

        //Seto os campos
        nome.setText(sessionManager.getStringPreferences(MainIndex.this, "nomeUser"));
        email.setText(sessionManager.getStringPreferences(MainIndex.this, "emailUser"));

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();

        int id = item.getItemId();

        if (id == R.id.nav_criar_chamado) {
            fm.beginTransaction().replace(R.id.content_frame, new CriarChamado()).commit();
        }
        else if (id == R.id.nav_manage) {
            fm.beginTransaction().replace(R.id.content_frame, new CardFragment()).commit();
        }
        else if (id == R.id.nav_logout) {
            //Vou setar o usuário como deslogado
            sessionManager.setBooleanPreferences(MainIndex.this, "userLoggedOn", false);
            //Removo o ID do usuário
            sessionManager.setStringPreferences(MainIndex.this, "idUser", null);

            //Mando pra tela de Splash
            Intent intent = new Intent(this, Splash.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

