package com.example.mangoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextInputLayout regName, regUserName, regEmail, regPhoneNo, regPassword;
    Button regBtn, regToLoginBtn;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ProgressDialog loadingBar;

    private Boolean validateName()
    {
        String val = regName.getEditText().getText().toString();
        if(val.isEmpty())
        {
            regName.setError("Field Cannot be Empty");
            return false;
        }
        else
        {
            regName.setError(null);
            return true;
        }
    }

    private Boolean validateUserName()
    {
        String val = regUserName.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if(val.isEmpty())
        {
            regUserName.setError("Field Cannot be Empty");
            return false;
        }
        else if(val.length() >= 15)
        {
            regUserName.setError("Username Too long!");
            return false;
        }
        else if(!val.matches(noWhiteSpace))
        {
            regUserName.setError("White Space is not allowed!");
            return false;
        }
        else
        {
            regUserName.setError(null);
            regUserName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail()
    {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(val.isEmpty())
        {
            regEmail.setError("Field Cannot be Empty");
            return false;
        }
        else if(!val.matches(emailPattern)){
            regEmail.setError("Invalid Email!");
            return false;
        }
        else
        {
            regEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePhone()
    {
        String val = regPhoneNo.getEditText().getText().toString();
        if(val.isEmpty())
        {
            regPhoneNo.setError("Field Cannot be Empty");
            return false;
        }
        else
        {
            regPhoneNo.setError(null);
            return true;
        }
    }

    private Boolean validatePassword()
    {
        String val = regPassword.getEditText().getText().toString();
        String passwordVal = "^"+
                "(?=.&[a-zA-z])"+
                "(?=.*[@#$%^+=])"+
                "(?=\\S+$)"+
                ".{4,}"+
                "$";

        if(val.isEmpty())
        {
            regPassword.setError("Field Cannot be Empty");
            return false;
        }
//        else if(!val.matches(passwordVal)){
//            regPassword.setError("Password is too Week");
//            return false;
//        }
        else
        {
            regPassword.setError(null);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        loadingBar = new ProgressDialog(this);

        regName = findViewById(R.id.name);
        regUserName = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regPhoneNo = findViewById(R.id.phone);
        regPassword = findViewById(R.id.password);
        regBtn = findViewById(R.id.register_btn);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateName() | !validateEmail() | !validatePassword() | !validateUserName() | !validatePhone())
                {
                    return;
                }

//                rootNode = FirebaseDatabase.getInstance();


//
                //Get All values
                String name = regName.getEditText().getText().toString();
                String username = regUserName.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String phone = regPhoneNo.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loadingBar.setTitle("Creating New Account");
                                loadingBar.setMessage("Ruko Jara, Sabar karo!!");
                                loadingBar.setCanceledOnTouchOutside(true);
                                loadingBar.show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        loadingBar.dismiss();
                                    }
                                }, 2500);

                                if(task.isSuccessful())
                                {
                                    UserHelperClass information = new UserHelperClass(
                                            name, username, email, phone, password
                                    );
                                    FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(SignUp.this, "Account Successfully Created!!", Toast.LENGTH_SHORT).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    loadingBar.dismiss();
                                                }
                                            }, 300);
                                            startActivity(new Intent(getApplicationContext(), Login.class));
                                            finish();
                                        }
                                    });

                                }
                                else
                                {
                                    String error = task.getException().toString();
                                    String[] arrOfStr = error.split(":");
                                    String error1 = (arrOfStr[1]);
                                    Toast.makeText(SignUp.this, "Error!"+error1, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


//

//
//                //Set All values
//                UserHelperClass helperClass = new UserHelperClass(name, username, email, phone, password );
////                UserHelperClass helperClass = new UserHelperClass(email, name, password, phone, username);
//
//                databaseReference.child(phone).setValue(helperClass);
//                Toast.makeText(SignUp.this, "Account Successfully Created!!", Toast.LENGTH_SHORT).show();


            }
        });

    }

    public void back(View view) {
        finish();
    }

}