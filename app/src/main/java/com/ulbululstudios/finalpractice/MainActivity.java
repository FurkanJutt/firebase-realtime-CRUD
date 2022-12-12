package com.ulbululstudios.finalpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // firebase
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); // initializing database
    private DatabaseReference databaseReference = firebaseDatabase.getReference("user"); // creating new database reference


    // views variables
    private EditText tvEmail, tvPassword, tvConfirmPassword, tvContactNumber, tvRollNumber;
    private RadioGroup radioGroup;
    private RadioButton rbSex;
    private Button btnSubmit;
    private FloatingActionButton fabNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing variables
        tvEmail = findViewById(R.id.tv_email);
        tvPassword = findViewById(R.id.tv_password);
        tvConfirmPassword = findViewById(R.id.tv_confirm_password);
        tvContactNumber = findViewById(R.id.tv_contact);
        tvRollNumber = findViewById(R.id.tv_roll_no);
        radioGroup = findViewById(R.id.radio_group);
        btnSubmit = findViewById(R.id.btn_submit);
        fabNextPage = findViewById(R.id.fab_next_page);
        ///-------------------------------------------

        // submit button - register user
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting text from text views
                String email = tvEmail.getText().toString().toLowerCase().trim();
                String password = tvPassword.getText().toString();
                String confirmPassword = tvConfirmPassword.getText().toString();
                String contactNo = tvContactNumber.getText().toString().trim();
                String rollNo = tvRollNumber.getText().toString().toLowerCase().trim();
                int selectedSex = radioGroup.getCheckedRadioButtonId();
                rbSex = findViewById(selectedSex);

                User user = new User(email, password, contactNo, rollNo, rbSex.getText().toString());


                // checking if fields have some text
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contactNo.isEmpty() || rollNo.isEmpty() || selectedSex == -1) {
                    Toast.makeText(MainActivity.this, "Cannot add empty fields!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) { // comparing password and confirm password
                    Toast.makeText(MainActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else { // if fields are not empty and passwords match
                    // creating new user with provided credentials
                   firebaseAuth.createUserWithEmailAndPassword(email, confirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               databaseReference.push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       Toast.makeText(MainActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(MainActivity.this, "Failure: " + e.toString(), Toast.LENGTH_SHORT).show();
                                   }
                               });
                           } else {
                               Toast.makeText(MainActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                           }
                       }
                   }); // end create user


                }
            }
        }); // end submit button

        // next page floating action button
        fabNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting text from text views
                String email = tvEmail.getText().toString().toLowerCase().trim();
                String password = tvPassword.getText().toString();

                // checking if fields have some text
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Cannot add empty fields!", Toast.LENGTH_SHORT).show();
                } else { // if fields are not empty
                    // sign_in user with provided credentials
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (!user.isEmailVerified()) { // if email is not verified send verification email
                                    user.sendEmailVerification();
                                    Toast.makeText(MainActivity.this, "Please verify your email first!", Toast.LENGTH_SHORT).show();
                                } else { // if email is verified go to home page
                                    startActivity(new Intent(MainActivity.this, HomePage.class));
                                }
                            } else { // error to login
                                Toast.makeText(MainActivity.this, "Failed to Login user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }); // end next page button
    } // end onCreate()
}