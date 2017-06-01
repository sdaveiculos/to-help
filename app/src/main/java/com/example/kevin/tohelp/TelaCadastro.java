package com.example.kevin.tohelp;

/**
 * Created by kevin on 16/03/2017.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Tela para cadastro de um usuário.
 * Esta irá enviar as informações do usuário para o servidor
 *
 * @author Kevin Amorim
 * @since 05/10/2016
 */
public class TelaCadastro extends AppCompatActivity {

    //Campos do formulário
    private EditText    fieldNome;
    private EditText    fieldEmail;
    private EditText    fieldSenha;
    //private EditText    fieldSexo;
    private Button      btnLogin;

    //Variável de resposta do servidor
    private JSONObject loginResponse;
    private String TAG = "TelaCadastro";

    //Variáveis dos dados a serem enviados para o servidor
    String nome     = null;
    String email    = null;
    String sexo     = null;
    String senha    = null;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        //Instanciando os campos do formulário
        fieldNome   = (EditText) findViewById(R.id.cadastroNome);
        fieldEmail  = (EditText) findViewById(R.id.cadastroEmail);
        fieldSenha  = (EditText) findViewById(R.id.cadastroSenha);
        btnLogin    = (Button) findViewById(R.id.btn_signup);

        //Ouvindo click do botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                enviarDados(view);
            }
        });
    }

    //Método que irá enviar os dados inseridos pelo usuário para o servidor
    public void enviarDados (View v) {

        nome    = fieldNome.getText().toString();
        email   = fieldEmail.getText().toString();
        //sexo    = fieldSexo.getText().toString();
        senha   = fieldSenha.getText().toString();

        //Toast.makeText(TelaCadastro.this, "Dados cadastrados com sucesso!", Toast.LENGTH_LONG).show();

        //Esta task deverá ser rodada quando o botão de cadastro for pressionado
        new JSONTask().execute("http://192.168.137.222/tohelp/website/www/action.php?ah=pessoa/pessoa_add");

    }

    /**
     * Método para enviar as informações de cadastro de um usuário para o servidor
     * TODO devo rever este ao conseguir criar a classe que gerenciará os envios ao servidor
     */
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TelaCadastro.this);
            mProgressDialog.setMessage("Carregando...");
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
                //Criando builder de Uri com os parâmetros de cadastro
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("nome", nome)
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("senha", senha);
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

                //Caso o cadastro tenha falhado, exibo um alert dialog
                if (!jsonResponse.getBoolean("status")) {
                    //Criando alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(TelaCadastro.this);
                    builder.setTitle("Cadastro");
                    builder.setMessage(jsonResponse.getString("message"));
                    builder.setPositiveButton("OK", null);
                    AlertDialog alerta = builder.create();
                    alerta.show();
                }
                else {
                    //Cadastro com sucesso. Exibo um toast e mando para próxima activity
                    Toast.makeText(TelaCadastro.this, "Cadastro realizado com sucesso", Toast.LENGTH_LONG).show();

                    //Criando controlador de sessão
                    SessionManager sessionManager = new SessionManager();
                    //Salvo que existe um usuário logado
                    sessionManager.setBooleanPreferences(TelaCadastro.this, "userLoggedOn", true);
                    //Salvo o id do usuário logado
                    sessionManager.setStringPreferences(TelaCadastro.this, "idUser", jsonResponse.getJSONObject("data").getString("id"));

                    //Salvar o nome do usuário e o e-mail dele para exibir no drawer
                    sessionManager.setStringPreferences(TelaCadastro.this, "nomeUser", jsonResponse.getJSONObject("data").getString("nome"));
                    sessionManager.setStringPreferences(TelaCadastro.this, "emailUser", jsonResponse.getJSONObject("data").getString("email"));

                    Intent intent = new Intent(getBaseContext(), MainIndex.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
