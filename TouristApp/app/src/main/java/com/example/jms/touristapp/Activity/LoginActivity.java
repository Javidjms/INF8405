package com.example.jms.touristapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jms.touristapp.R;
import com.example.jms.touristapp.Database.SqlLiteDbHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class allow an user to authenticate only it the user is an admin
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailtext; // Email input
    private EditText passwordtext; // Pwd input

    private final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //  Get the view text
        emailtext = (EditText) findViewById(R.id.email_text);
        passwordtext = (EditText) findViewById(R.id.password_text);
        //Get buttons
        Button signupbutton = (Button) findViewById(R.id.signup_button);

        // Initalise skip button
        TextView skip = (TextView) findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,POIListActivity.class);
                intent.putExtra("category","ALL");
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //Set eventlistener on buttons
        signupbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // validation input field
                String stremail = emailtext.getText().toString();
                String strpassword = passwordtext.getText().toString();
                if (strpassword.equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.login_empty_pwd, Toast.LENGTH_SHORT).show();
                } else if (stremail.equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.login_empty_email, Toast.LENGTH_SHORT).show();
                }  else {
                    if (!stremail.matches(EMAIL_PATTERN)) {
                        Toast.makeText(LoginActivity.this, R.string.login_unvalid_email, Toast.LENGTH_SHORT).show();
                    }
                    else { // If everything is filled - Check with admin DB
                        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(LoginActivity.this) ;
                        dbHelper.openDataBase();
                        try {
                            if(!dbHelper.isGoodPassword(stremail,hashMD5(strpassword))){  // Checl if password is correct
                                Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                            }
                            else{// If password is correct
                                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this,AdminMenuActivity.class);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });



    }

    /**
     * MD5 Hash algorithm for password
     * @param password password to be hashed
     * @return the MD5 hash of the pwd
     * @throws NoSuchAlgorithmException
     */
    public String hashMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : hash) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.slide_in_left,R.anim.slide_out_right);
    }



}
