package com.example.kevin.tohelp;

/**
 * Created by kevin on 16/03/2017.
 */
import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.content.Intent;
import android.provider.Settings;

public class CriarChamado extends Fragment {

    Button btnIncendio, btnEnchente, btnTempestade;
    LocationService locationService;
    Location location;

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
        btnIncendio = (Button) rootView.findViewById(R.id.btIncendio);
        btnEnchente = (Button) rootView.findViewById(R.id.btEnchente);
        btnTempestade = (Button) rootView.findViewById(R.id.btTempestade);

        btnIncendio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO só no terceiro click no botão que estou conseguindo recuperar as coordenadas, devo verificar isso
                //Agora devo verificar se o GPS tá ativo
                if (locationService.canGetLocation()) {

                    Log.v("LocationService", "-------------------------------------------------------------------");
                    Log.v("LocationService", "GPS ativo: "+locationService.isGPSEnabled);
                    Log.v("LocationService", "Network ativo: "+locationService.isNetworkEnabled);
                    Log.v("LocationService", "Latitude: "+locationService.latitude);
                    Log.v("LocationService", "Longitude: "+locationService.longitude);
                    Log.v("LocationService", "Location: "+locationService.location);

                    //Pego a latitude e longitude
                    double latitude = locationService.getLatitude();
                    double longitude = locationService.getLongitude();

                    //TODO agora devo realizar o chamado para o servidor
                    Log.v("locationService", latitude+" sua latitude");
                    Log.v("locationService", longitude+" sua longitude");

                }
                else {

                    //Se o gps não estiver ativo, vou verificar se a permissão foi garantida
                    //Caso getLocation() retorne null, significa que não tenho permissão ainda
                    if (location == null) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                    Log.v("boo", "cancelou arrombado");
                }
                return;
            }
        }

    }

}

