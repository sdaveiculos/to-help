package com.example.kevin.tohelp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;
    private Integer tipo;
    private Activity activity = (Activity) this;

    ProgressDialog mProgressDialog;
    String TAG = "Chamado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        latitude  = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        tipo      = getIntent().getIntExtra("tipo", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng posicao = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(posicao).title("Você está aqui. Clique para realizar o chamado!")).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicao));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom, 3000, null);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick (Marker var1) {
                new JSONTask().execute("http://192.168.0.196/tohelp/website/www/action.php?ah=chamado/criarChamado");
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                new JSONTask().execute("http://192.168.0.196/tohelp/website/www/action.php?ah=chamado/criarChamado");
                return true;
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Mapa.this);
            mProgressDialog.setMessage("Enviando...");
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
                String idUser = sessionManager.getStringPreferences(activity, "idUser");

                //Criando builder de Uri com os parâmetros de cadastro
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idPessoa", idUser)
                        .appendQueryParameter("latitude", String.valueOf(latitude))
                        .appendQueryParameter("longitude", String.valueOf(longitude))
                        .appendQueryParameter("tipo", tipo.toString());
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Chamado");
                    builder.setMessage(jsonResponse.getString("message"));
                    builder.setPositiveButton("OK", null);
                    AlertDialog alerta = builder.create();
                    alerta.show();

                    Log.v("chamado", jsonResponse.getString("message"));
                }
                else {
                    //Login com sucesso. Exibo um toast avisando o sucesso
                    Toast.makeText(activity, "Chamado realizado com sucesso. Obrigado por ajudar o Corpo de Bombeiros.", Toast.LENGTH_LONG).show();
                    Log.v("chamado", "Chamado realizado com sucesso. Obrigado por ajudar o Corpo de Bombeiros.");

                    //Agora devo mandar o usuário para home do app
                    Intent intent = new Intent(activity, MainIndex.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
