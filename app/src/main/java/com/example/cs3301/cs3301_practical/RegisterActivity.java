package com.example.cs3301.cs3301_practical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Allows Clients to register to use app.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {
    Button bRegister;
    EditText etName, etAge, etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bRegister:
                // Get Client info
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                //String strAge = etAge.getText().toString();
                String password = etPassword.getText().toString();
                int age = Integer.parseInt(etAge.getText().toString());

                Client registerClientDetails = new Client(name, username, password, age);
                registerClient(registerClientDetails);

                break;
        }
    }

    private boolean isValidRegistration(String name, String strAge, String username, String password) {
        return false;
    }

    private void registerClient(Client client){
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storeClientDataInBackground(client, new GetClientCallBack() {
            @Override
            public void done(Client returnedClient) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

}
