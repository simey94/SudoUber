package com.example.cs3301.cs3301_practical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Class to allow clients to login.
 */

public class LoginActivity extends Activity implements View.OnClickListener {

    Button bLogin;
    EditText etUsername, etPassword;
    TextView tvRegisterLink;
    ClientLocalStore clientLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);

        // when login button is clicked onClick is notified
        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);

        clientLocalStore = new ClientLocalStore(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                Client client = new Client(username,password);
                authenticate(client);

                clientLocalStore.storeClientData(client);
                clientLocalStore.setClientLoggedIn(true);
                break;

            // when register link is clicked move to Register Activity
            case R.id.tvRegisterLink:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }

    }

    private void authenticate(Client client){
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchClientDataInBackground(client, new GetClientCallBack() {

            @Override
            public void done(Client returnedClient) {
                if(returnedClient == null){
                    // no user returned show error
                    showErrorMessage();
                } else {
                    logClientIn(returnedClient);
                }
            }
        });

    }

    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Incorrect client details");
        dialogBuilder.setPositiveButton("OK",null);
        dialogBuilder.show();
    }

    private void logClientIn(Client returnedClient){
       clientLocalStore.storeClientData(returnedClient);
        clientLocalStore.setClientLoggedIn(true);
        startActivity(new Intent(this, MainActivity.class));
    }
}