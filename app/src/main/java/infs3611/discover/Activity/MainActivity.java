package infs3611.discover.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import infs3611.discover.R;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private FirebaseFirestore firebaseFirestore;
    private String userType;
    private FirebaseUser firebaseUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.mainConstraintLayout);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    Intent intent = new Intent (getBaseContext(), UserLoginActivity.class);
                    startActivity(intent);

//
//                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                userId = firebaseUser.getUid();
//                if(userId.equals("")) {
//                    Intent intent = new Intent (getBaseContext(), UserLoginActivity.class);
//                    startActivity(intent);
//                } else {
//                    firebaseFirestore = FirebaseFirestore.getInstance();
//                    firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.isSuccessful()){
//                                DocumentSnapshot documentSnapshot = task.getResult();
//                                userType = documentSnapshot.getString("userType");
//                            }else{
//                                Log.d("Fire_Log", "Error" +  task.getException().getMessage());
//                            }
//                        }
//                    });
//                    if(userType.equals("1")){
//                        Intent intent = new Intent (getBaseContext(), UserProfileActivity.class);
//                        startActivity(intent);
//                    } else if (userType.equals("2")){
//
//                    }
//                }

            }
        });

    }
}
