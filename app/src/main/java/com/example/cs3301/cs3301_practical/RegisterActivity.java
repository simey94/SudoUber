package com.example.cs3301.cs3301_practical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
        etName.setInputType(InputType.TYPE_CLASS_TEXT);
        etAge = (EditText) findViewById(R.id.etAge);
        etAge.setInputType(InputType.TYPE_CLASS_TEXT);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etUsername.setInputType(InputType.TYPE_CLASS_TEXT);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
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
                String age = etAge.getText().toString();

                if (isValidRegistrationValues(name, age, username, password)) {
                    Client registerClientDetails = new Client(name, username, password, Integer.parseInt(age));
                    registerClient(registerClientDetails);
                }
                break;
        }
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


    /* Form validation methods  */

    private boolean isValidRegistrationValues(String name, String strAge,
                                              String username, String password) {
        if (isValidName(name)) {
            if (isValidUsername(username)) {
                if (isValidPassword(password)) {
                    if (isValidAge(strAge)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isValidName(String name) {
        if (name.length() == 0) {
            etName.setError("Please specify a name");
            return false;
        } else if (name.length() > 25) {
            etName.setError("Name can only be up to 25 Characters");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidAge(String strAge) {
        int age;
        try {
            age = Integer.parseInt(strAge);
        } catch (NumberFormatException e) {
            etAge.setError("Age must be a number");
            return false;
        }
        if (age >= 16 && age <= 100) {
            return true;
        } else {
            etAge.setError("Age should be a number between 16 and 100");
            return false;
        }
    }

    private boolean isValidUsername(String username) {
        if (username.length() == 0) {
            etUsername.setError("Please specify a username");
            return false;
        } else if (username.length() > 25) {
            etUsername.setError("Username can only be up to 25 characters");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() == 0) {
            etPassword.setError("Please specify a password");
            return false;
        } else if (password.length() > 25) {
            etPassword.setError("Password can only be up to 25 characters");
            return false;
        } else {
            return true;
        }
    }
}
