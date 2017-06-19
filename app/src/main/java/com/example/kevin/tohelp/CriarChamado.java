package com.example.kevin.tohelp;

/**
 * Created by kevin on 16/03/2017.
 */
import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.content.Intent;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CriarChamado extends Fragment {

    Button btnIncendio;
    Button btnEnchente;
    Button btnAcidente;
    Button btnDesmoronamento;
    Button btnDesmatamento;

    LocationService locationService;
    Location location;

    String TAG = "Chamado";
    Integer tipoChamado;

    double latitude;
    double longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Crio um objeto LocationService
        locationService = new LocationService(getContext(), getActivity());
        //Chamo o getLocation()
        location = locationService.getLocation();
        //Agora eu verifico se eu posso criar uma localização e se eu tenho permissão
        if (location == null && !locationService.canGetLocation()) {
            //Seu não não puder criar a localização e não tiver permissão, já peço assim que entrar no fragment
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        View rootView = inflater.inflate(R.layout.activity_criar_chamado, container, false);

        //Botões para criar chamado
        btnIncendio         = (Button) rootView.findViewById(R.id.btIncendio);
        btnEnchente         = (Button) rootView.findViewById(R.id.btEnchente);
        btnAcidente         = (Button) rootView.findViewById(R.id.btAcidente);
        btnDesmatamento     = (Button) rootView.findViewById(R.id.btDesmatamento);
        btnDesmoronamento   = (Button) rootView.findViewById(R.id.btDesmoronamento);

        btnIncendio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Agora devo verificar se o GPS tá ativo
                if (locationService.canGetLocation()) {

                    Log.v("LocationService", "-------------------------------------------------------------------");
                    Log.v("LocationService", "GPS ativo: "+locationService.isGPSEnabled);
                    Log.v("LocationService", "Network ativo: "+locationService.isNetworkEnabled);
                    Log.v("LocationService", "Latitude: "+locationService.latitude);
                    Log.v("LocationService", "Longitude: "+locationService.longitude);
                    Log.v("LocationService", "Location: "+locationService.location);

                    //Pego a latitude e longitude
                    latitude = locationService.getLatitude();
                    longitude = locationService.getLongitude();
                    tipoChamado = 1;

                    //Agora devo realizar o chamado para o servidor
                    Log.v("locationService", latitude+" sua latitude");
                    Log.v("locationService", longitude+" sua longitude");

                    Intent intent = new Intent(getActivity(), Mapa.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("tipo", tipoChamado);
                    startActivity(intent);

                }
                else {

                    //Se o gps não estiver ativo, vou verificar se a permissão foi garantida
                    //Caso getLocation() retorne null, significa que não tenho permissão ainda
                    if (location == null) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                    //Se eu tiver permissão, mas não tiver um provider, devo requisitar que seja habilitado o GPS
                    if (!locationService.isGPSEnabled && !locationService.isNetworkEnabled) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }

                }

            }
        });

        btnEnchente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Agora devo verificar se o GPS tá ativo
                if (locationService.canGetLocation()) {

                    Log.v("LocationService", "-------------------------------------------------------------------");
                    Log.v("LocationService", "GPS ativo: "+locationService.isGPSEnabled);
                    Log.v("LocationService", "Network ativo: "+locationService.isNetworkEnabled);
                    Log.v("LocationService", "Latitude: "+locationService.latitude);
                    Log.v("LocationService", "Longitude: "+locationService.longitude);
                    Log.v("LocationService", "Location: "+locationService.location);

                    //Pego a latitude e longitude
                    latitude = locationService.getLatitude();
                    longitude = locationService.getLongitude();
                    tipoChamado = 2;

                    //Agora devo realizar o chamado para o servidor
                    Log.v("locationService", latitude+" sua latitude");
                    Log.v("locationService", longitude+" sua longitude");

                    Intent intent = new Intent(getActivity(), Mapa.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("tipo", tipoChamado);
                    startActivity(intent);
                }
                else {

                    //Se o gps não estiver ativo, vou verificar se a permissão foi garantida
                    //Caso getLocation() retorne null, significa que não tenho permissão ainda
                    if (location == null) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                    //Se eu tiver permissão, mas não tiver um provider, devo requisitar que seja habilitado o GPS
                    if (!locationService.isGPSEnabled && !locationService.isNetworkEnabled) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }

                }

            }
        });

        btnAcidente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Agora devo verificar se o GPS tá ativo
                if (locationService.canGetLocation()) {

                    Log.v("LocationService", "-------------------------------------------------------------------");
                    Log.v("LocationService", "GPS ativo: "+locationService.isGPSEnabled);
                    Log.v("LocationService", "Network ativo: "+locationService.isNetworkEnabled);
                    Log.v("LocationService", "Latitude: "+locationService.latitude);
                    Log.v("LocationService", "Longitude: "+locationService.longitude);
                    Log.v("LocationService", "Location: "+locationService.location);

                    //Pego a latitude e longitude
                    latitude = locationService.getLatitude();
                    longitude = locationService.getLongitude();
                    tipoChamado = 3;

                    //Agora devo realizar o chamado para o servidor
                    Log.v("locationService", latitude+" sua latitude");
                    Log.v("locationService", longitude+" sua longitude");

                    Intent intent = new Intent(getActivity(), Mapa.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("tipo", tipoChamado);
                    startActivity(intent);

                }
                else {

                    //Se o gps não estiver ativo, vou verificar se a permissão foi garantida
                    //Caso getLocation() retorne null, significa que não tenho permissão ainda
                    if (location == null) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                    //Se eu tiver permissão, mas não tiver um provider, devo requisitar que seja habilitado o GPS
                    if (!locationService.isGPSEnabled && !locationService.isNetworkEnabled) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }

                }

            }
        });

        btnDesmatamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Agora devo verificar se o GPS tá ativo
                if (locationService.canGetLocation()) {

                    Log.v("LocationService", "-------------------------------------------------------------------");
                    Log.v("LocationService", "GPS ativo: "+locationService.isGPSEnabled);
                    Log.v("LocationService", "Network ativo: "+locationService.isNetworkEnabled);
                    Log.v("LocationService", "Latitude: "+locationService.latitude);
                    Log.v("LocationService", "Longitude: "+locationService.longitude);
                    Log.v("LocationService", "Location: "+locationService.location);

                    //Pego a latitude e longitude
                    latitude = locationService.getLatitude();
                    longitude = locationService.getLongitude();
                    tipoChamado = 4;

                    //Agora devo realizar o chamado para o servidor
                    Log.v("locationService", latitude+" sua latitude");
                    Log.v("locationService", longitude+" sua longitude");

                    Intent intent = new Intent(getActivity(), Mapa.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("tipo", tipoChamado);
                    startActivity(intent);

                }
                else {

                    //Se o gps não estiver ativo, vou verificar se a permissão foi garantida
                    //Caso getLocation() retorne null, significa que não tenho permissão ainda
                    if (location == null) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                    //Se eu tiver permissão, mas não tiver um provider, devo requisitar que seja habilitado o GPS
                    if (!locationService.isGPSEnabled && !locationService.isNetworkEnabled) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }

                }

            }
        });

        btnDesmoronamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Agora devo verificar se o GPS tá ativo
                if (locationService.canGetLocation()) {

                    Log.v("LocationService", "-------------------------------------------------------------------");
                    Log.v("LocationService", "GPS ativo: "+locationService.isGPSEnabled);
                    Log.v("LocationService", "Network ativo: "+locationService.isNetworkEnabled);
                    Log.v("LocationService", "Latitude: "+locationService.latitude);
                    Log.v("LocationService", "Longitude: "+locationService.longitude);
                    Log.v("LocationService", "Location: "+locationService.location);

                    //Pego a latitude e longitude
                    latitude = locationService.getLatitude();
                    longitude = locationService.getLongitude();
                    tipoChamado = 5;

                    //Agora devo realizar o chamado para o servidor
                    Log.v("locationService", latitude+" sua latitude");
                    Log.v("locationService", longitude+" sua longitude");

                    Intent intent = new Intent(getActivity(), Mapa.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("tipo", tipoChamado);
                    startActivity(intent);

                }
                else {

                    //Se o gps não estiver ativo, vou verificar se a permissão foi garantida
                    //Caso getLocation() retorne null, significa que não tenho permissão ainda
                    if (location == null) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                    //Se eu tiver permissão, mas não tiver um provider, devo requisitar que seja habilitado o GPS
                    if (!locationService.isGPSEnabled && !locationService.isNetworkEnabled) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }

                }

            }
        });

        return rootView;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                //Tendo vindo algum resultado e o valor do resultado for PERMISSION_GRANTED
                //Significa que recebi a permissão e conseguirei criar o chamado no próximo click
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Se chegou aqui significa que deu bom e o usuário deu permissão
                    //Agora chamo o getLocation pra já capturar a localização atual do usuário
                    location = locationService.getLocation();

                } else {
                    //TODO Permissão negada. O que devo fazer?
                    //Talvez exibir um toast ou um alert que não é possível criar um chamado sem a localização
                    Log.v("locationService", "Cancelou a permissão");
                    Toast.makeText(getContext(), "A permissão para utilizar GPS foi negada. Não será possível recuperar sua posição atual.", Toast.LENGTH_LONG);
                }
                return;
            }
        }

    }

}

