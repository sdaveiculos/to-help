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
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    //Elementos do formulário
    private TextView btnCadastro;
    private EditText fieldLogin;
    private EditText fieldSenha;

    //Variáveis com dados a serem enviados para o servidor
    private String TAG      = "Login";
    private String login    = null;
    private String senha    = null;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instanciando os campos do formulário de login
        btnCadastro = (TextView) findViewById(R.id.btnCadastro);
        fieldLogin  = (EditText) findViewById(R.id.loginUsuario);
        fieldSenha  = (EditText) findViewById(R.id.loginSenha);
    }

    /**
     * Método para mudar para a tela de cadastro
     * @param view
     */
    public void telaCadastro (View view) {
        Intent intent = new Intent(this, TelaCadastro.class);
        startActivity(intent);
    }

    /**
     * Método para realizar o login de um usuário, enviando seu login e senha para o servidor
     * Será utilizado uma AsyncTask para fazer o envio dos dados para o servidor
     *
     * @param view
     */
    public void fazerLogin (View view) {

        //Pegando os valores inseridos pelo usuário
        login = fieldLogin.getText().toString();
        senha = fieldSenha.getText().toString();

        //Enviando os dados de login para o servidor
        new JSONTask().execute("http://10.145.251.236/tohelp/website/www/action.php?ah=pessoa/fazerLogin");

    }

    /**
     * Método para enviar as informações de login para o servidor
     * TODO devo rever este ao conseguir criar a classe que gerenciará os envios ao servidor
     */
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Login.this);
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
                        .appendQueryParameter("login", login)
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("Login");
                    builder.setMessage(jsonResponse.getString("message"));
                    builder.setPositiveButton("OK", null);
                    AlertDialog alerta = builder.create();
                    alerta.show();
                }
                else {
                    //Login com sucesso. Exibo um toast avisando o sucesso
                    Toast.makeText(Login.this, "Login realizado com sucesso", Toast.LENGTH_LONG).show();

                    //Criando controlador de sessão
                    SessionManager sessionManager = new SessionManager();
                    //Salvo que existe um usuário logado
                    sessionManager.setBooleanPreferences(Login.this, "userLoggedOn", true);
                    //Salvo o id do usuário logado
                    sessionManager.setStringPreferences(Login.this, "idUser", jsonResponse.getJSONObject("data").getString("idUser"));

                    //Salvar o nome do usuário e o e-mail dele para exibir no drawer
                    sessionManager.setStringPreferences(Login.this, "nomeUser", jsonResponse.getJSONObject("data").getString("nomeUser"));
                    sessionManager.setStringPreferences(Login.this, "emailUser", jsonResponse.getJSONObject("data").getString("emailUser"));

                    //Agora devo mandar o usuário para home do app
                    Intent intent = new Intent(getBaseContext(), MainIndex.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}

