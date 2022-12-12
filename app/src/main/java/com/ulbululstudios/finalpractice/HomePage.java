package com.ulbululstudios.finalpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    // firebase
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); // initializing database
    private DatabaseReference databaseReference = firebaseDatabase.getReference("user"); // getting instance database reference
    FirebaseUser fbuser; // current user


    // views variables
    private EditText tvEmail, tvPassword, tvConfirmPassword, tvContactNumber, tvRollNumber;
    private RadioGroup radioGroup;
    private RadioButton rbSex;
    private Button btnUpdate;
    private FloatingActionButton fabDeleteUser;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // get current user
        fbuser = firebaseAuth.getCurrentUser();

        // initializing variables
        tvEmail = findViewById(R.id.tv_home_email);
        tvPassword = findViewById(R.id.tv_home_password);
        tvConfirmPassword = findViewById(R.id.tv_home_confirm_password);
        tvContactNumber = findViewById(R.id.tv_home_contact);
        tvRollNumber = findViewById(R.id.tv_home_roll_no);
        radioGroup = findViewById(R.id.radio_group_home);
        btnUpdate = findViewById(R.id.btn_home_update);
        fabDeleteUser = findViewById(R.id.fab_home_delete_user);
        ///-------------------------------------------


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    key = postSnapshot.getKey();
                    User value = postSnapshot.getValue(User.class);

                    tvEmail.setText(value.getEmail());
                    tvPassword.setText(value.getPassword());
                    tvContactNumber.setText(value.getContactNo());
                    tvRollNumber.setText(value.getRollNo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
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

                Map<String, Object> map = new HashMap<>();
                map.put("email", email);
                map.put("password", password);
                map.put("contactNo", contactNo);
                map.put("gender", rbSex.getText().toString());
                map.put("rollNo", rollNo);

                // checking if fields have some text
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contactNo.isEmpty() || rollNo.isEmpty() || selectedSex == -1) {
                    Toast.makeText(HomePage.this, "Cannot add empty fields!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) { // comparing password and confirm password
                    Toast.makeText(HomePage.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else { // if fields are not empty and passwords match
                    // updating user with provided credentials
                    fbuser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                databaseReference.child(key).updateChildren(map);
                                Toast.makeText(HomePage.this, "User updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomePage.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }); // button update end

        fabDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbuser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference.child(key).removeValue();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(HomePage.this, MainActivity.class));
                    }
                });
            }
        });
    }
}