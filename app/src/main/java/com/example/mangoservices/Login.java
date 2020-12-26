package com.example.mangoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Button callSignUp;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username, password;
    Button LoginBtn;
    private ProgressBar progressbar;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;



    private Boolean validateUserName()
    {
        String val = username.getEditText().getText().toString();
        if(val.isEmpty())
        {
            username.setError("Field Cannot be Empty");
            return false;
        }
        else
        {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword()
    {
        String val = password.getEditText().getText().toString();

        if(val.isEmpty())
        {
            password.setError("Field Cannot be Empty");
            return false;
        }
        else
        {
            password.setError(null);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        callSignUp = findViewById(R.id.callSignup);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.text_login);
        sloganText = findViewById(R.id.slogan);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        LoginBtn = findViewById(R.id.login_button);
        progressbar = findViewById(R.id.progressBar);

        //Call Signup with animation
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Login.this, SignUp.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(callSignUp, "interchange_tran");
                pairs[3] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[4] = new Pair<View, String>(username, "username_tran");
                pairs[5] = new Pair<View, String>(password, "password_tran");
                pairs[6] = new Pair<View, String>(LoginBtn, "GO_tran");

                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs).toBundle());
//                finish();
            }
        });
    }

    public void loginUser(View view)
    {
        if(!validatePassword() | !validateUserName())
        {
            return;
        }
        else
        {
            isUser();
        }
    }



    private void isUser()
    {
        String username_text = username.getEditText().getText().toString().trim();
        String password_text = password.getEditText().getText().toString().trim();

        Log.d("username", username_text);
        Log.d("password", password_text);

        progressbar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(username_text, password_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(Login.this, "Fetching data, Please Wait...", Toast.LENGTH_SHORT).show();

                if(task.isSuccessful())
                {
                    Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_LONG).show();

                    DatabaseReference databaseReference  = firebaseDatabase.getReference("users/"+mAuth.getUid());
                    Log.d("databaseReference", databaseReference.toString());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);

//                            Log.d("userHelperClass", userHelperClass.toString());
                            String nameFromDB = userHelperClass.getName();
                            Log.d("nameFromDB", nameFromDB);

                            String usernameFromDB = userHelperClass.getUsername();
                            String emailFromDB = userHelperClass.getEmail();
                            String phoneFromDB = userHelperClass.getPhone();
                            String passwordFromDB = userHelperClass.getPassword();


//                            Log.d("usernameFromDB", usernameFromDB);
//                            Log.d("emailFromDB", emailFromDB);
//                            Log.d("phoneFromDB", phoneFromDB);
//                            Log.d("passwordFromDB", passwordFromDB);

                            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                            intent.putExtra("name", nameFromDB);
                            intent.putExtra("email", emailFromDB);
                            intent.putExtra("phone", phoneFromDB);
                            intent.putExtra("password", passwordFromDB);
                            intent.putExtra("username", usernameFromDB);

                            progressbar.setVisibility(View.INVISIBLE);

                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
//                    Intent intent = new Intent(getApplicationContext(), UserProfile.class);
//                    startActivity(intent);
                }
                else
                {
                    progressbar.setVisibility(View.INVISIBLE);
                    String error = task.getException().toString();
                    String[] arrOfStr = error.split(":");
                    String error1 = (arrOfStr[1]);
                    Toast.makeText(Login.this, "Error"+error1, Toast.LENGTH_SHORT).show();

                }

            }
        });

//        final String userEnteredUsername = username.getEditText().getText().toString();
//        String userEnteredPassword = password.getEditText().getText().toString();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
//
//        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
//
//        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists())
//                {
//                    username.setError(null);
//                    username.setErrorEnabled(false);
//                    String passwordFromDB = dataSnapshot.child(username.getEditText().getText().toString()).child("password").getValue(String.class);
//
//                    if(passwordFromDB.equals(userEnteredPassword))
//                    {
//
//                        Log.d("tag","Value is: " + passwordFromDB);
//                        username.setError(null);
//                        username.setErrorEnabled(false);
//
//                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
//                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);
//                        String phoneFromDB = dataSnapshot.child(userEnteredUsername).child("phone").getValue(String.class);
//                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
//
//                        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
//                        intent.putExtra("name", nameFromDB);
//                        intent.putExtra("email", emailFromDB);
//                        intent.putExtra("phone", phoneFromDB);
//                        intent.putExtra("password", passwordFromDB);
//                        intent.putExtra("username", usernameFromDB);
//
//                        startActivity(intent);
//                    }
//                    else
//                    {
//                        password.setError("Password is Incorrect");
//                        password.requestFocus();
//                    }
//                }
//                else
//                {
//                    username.setError("No Such User Exist");
//                    username.requestFocus();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



    }
}