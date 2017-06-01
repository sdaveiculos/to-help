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
    Button btnTempestade;

    LocationService locationService;
    Location location;

    String TAG = "Chamado";
    Integer tipoChamado;
    Context context = getContext();

    double latitude;
    double longitude;

    ProgressDialog mProgressDialog;

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

                    new JSONTask().execute("http://192.168.0.106/tohelp/website/www/action.php?ah=chamado/criarChamado");

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
                    //Toast.makeText(context, "Sem a permissão para recuperar a sua localização por GPS o APP não consegue realizar o chamado", Toast.LENGTH_LONG);
                }
                return;
            }
        }

    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Enviado...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //Crio a url realizando seus tratamentos
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "URL criada.");

            try {
                //Pegando o id do usuário logado
                SessionManager sessionManager = new SessionManager();
                String idUser = sessionManager.getStringPreferences(getActivity(), "idUser");

                //Criando builder de Uri com os parâmetros de cadastro
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idPessoa", idUser)
                        .appendQueryParameter("latitude", String.valueOf(longitude))
                        .appendQueryParameter("longitude", String.valueOf(longitude))
                        .appendQueryParameter("tipo", tipoChamado.toString());
                String query = builder.build().getEncodedQuery();

                Log.v(TAG, "Builder da Uri criada. Agora vou abrir conexão.");

                //Abrindo a conexão
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                Log.v(TAG, "Conexão estabelecida.");

                // For POST only - START
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Android");
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();

                os.write(query.getBytes());
                os.flush();
                os.close();
                // For POST only - END

                Log.v(TAG, "POST realizado.");

                //Variável de controle da resposta
                int responseCode = con.getResponseCode();

                Log.v(TAG, "Peguei o response do servidor.");

                //Sucesso na requisição
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.v("Deu bom", "Vamos lá");

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //Retorno a string de dados do servidor para ser tratado no onPostExecute
                    return response.toString();

                } else {
                    //TODO caso dê um erro, devemos tratar o mesmo informando ao usuário o erro retornado
                    //O que causa o HttpURLConnection não estar ok?
                    Log.v("Response", "POST falhou!");
                }

                //Envia para o servidor
                con.connect();

                //TODO devo tratar as exceções? caso não, o que o usuário verá quando ocorrer uma dessa?
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();

            try {
                //Crio o objeto json a partir da resposta do servidor
                JSONObject jsonResponse = new JSONObject(result);

                //Caso o login tenha falhado, exibo um alert dialog
                //FIXME talvez apagar os campos quando o usuário falhar o login ou pelo menos o campo de senha
                if (!jsonResponse.getBoolean("status")) {
                    //Criando alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Chamado");
                    builder.setMessage(jsonResponse.getString("message"));
                    builder.setPositiveButton("OK", null);
                    AlertDialog alerta = builder.create();
                    alerta.show();

                    Log.v("chamado", jsonResponse.getString("message"));
                }
                else {
                    //Login com sucesso. Exibo um toast avisando o sucesso
                    Toast.makeText(getActivity(), "Chamado realizado com sucesso. Obrigado por ajudar o Corpo de Bombeiros.", Toast.LENGTH_LONG).show();
                    Log.v("chamado", "Chamado realizado com sucesso. Obrigado por ajudar o Corpo de Bombeiros.");

                    //Agora devo mandar o usuário para home do app
                    Intent intent = new Intent(getActivity(), MainIndex.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}

