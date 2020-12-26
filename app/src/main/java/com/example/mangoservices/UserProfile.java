package com.example.mangoservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity {


    TextInputLayout fullname, phone, username_text;
    TextView name_label, username_label, email, password;
    Button update_btn;
    String user_fullname, user_username, user_password, user_email, user_phone;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullname = findViewById(R.id.fullname_profil);
        email = findViewById(R.id.email_profile);
        phone = findViewById(R.id.phone_profil);
        password = findViewById(R.id.password_profile);
        name_label = findViewById(R.id.name);
        username_label = findViewById(R.id.username);
        username_text = findViewById(R.id.username_profile);

        reference = FirebaseDatabase.getInstance().getReference("users/");



        showAllData();

    }

    private void showAllData() {
        Intent intent = getIntent();
        user_fullname = intent.getStringExtra("name");
        user_username= intent.getStringExtra("username");
        user_password = intent.getStringExtra("password");
        user_email = intent.getStringExtra("email");
        user_phone = intent.getStringExtra("phone");

        getSupportActionBar().setTitle(user_username);

        fullname.getEditText().setText(user_fullname);
        email.setText(user_email);
        phone.getEditText().setText(user_phone);
        password.setText(user_password);
        username_text.getEditText().setText(user_username);

        name_label.setText(user_fullname);
        username_label.setText(user_username);

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    public void update(View view) {

        if(isNameChanged() || isPhoneChanged() || isUsernameChanged())
        {
            Toast.makeText(this, "Data has been Updated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "No Data Changed!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUsernameChanged() {
        if (!user_username.equals(username_text.getEditText().getText().toString())) {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(username_text.getEditText().getText().toString());
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isPhoneChanged() {
        if (!user_phone.equals(phone.getEditText().getText().toString())) {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").setValue(phone.getEditText().getText().toString());
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isNameChanged() {
            if (!user_fullname.equals(fullname.getEditText().getText().toString())) {
                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(fullname.getEditText().getText().toString());
                return true;
            }
            else
            {
                return false;
            }
        }
}