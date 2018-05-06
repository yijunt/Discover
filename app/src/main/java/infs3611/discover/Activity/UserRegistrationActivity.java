package infs3611.discover.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import infs3611.discover.R;

/**
 * Created by June on 8/04/2018.
 */

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText fullNameEditText;
    private EditText zIDEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText verifyPasswordEditText;

    private CheckBox memberCheckBox;
    private CheckBox execCheckBox;
    private int userType = 0;
    private Spinner studyFieldSpinner;
    private Spinner facultySpinner;
    private Spinner programSpinner;
    private Spinner societySpinner, positionSpinner;

    private Button signUpButton;
    private Button execSignUpButton, execCancelButton;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private String getSocString;
    private DatabaseReference databaseReference;

    private String email, password;

    private FirebaseFirestore firebaseFirestore;
    private Map<String, Object> studyFieldMap;
    private HashMap<String, ArrayList<String>> allSocPositiondMap = new HashMap<>();
    private HashMap<String, Object> selectedSocPositionMap = new HashMap<>();
    //    public List<String> allSocName = new ArrayList<>();
    public String chosenStudyField, chosenFaculty, chosenProgram, chosenSoc, chosenSocPosition;
    public Object value;
    ArrayAdapter studyArrayAdapter, facultyArrayAdapter, programArrayAdapter, socArrayAdapter, socPositionArrayAdapter;
    private String userId;

    private PopupWindow societyPositionDialog;

    private LinearLayout registrationLinearLayout;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);


        FirebaseApp.initializeApp(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        zIDEditText = findViewById(R.id.studentIDEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        verifyPasswordEditText = findViewById(R.id.verifyPasswordEditText);

        memberCheckBox = findViewById(R.id.memberCheckBox);
        execCheckBox = findViewById(R.id.execCheckBox);

        studyFieldSpinner = findViewById(R.id.studyFieldSpinner);
        facultySpinner = findViewById(R.id.facultySpinner);
        programSpinner = findViewById(R.id.programSpinner);

        registrationLinearLayout = findViewById(R.id.registrationLinearLayout);

        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(this);

        insertStudyFieldSpinner();

        studyFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString() == "undergraduate") {
                    firebaseFirestore.collection("studyField").document("undergraduate").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            insertFacultySpinner(documentSnapshot.getData());
                            chosenStudyField = "undergraduate";
                        }
                    });

                } else if (adapterView.getItemAtPosition(i).toString() == "postgraduate") {
                    firebaseFirestore.collection("studyField").document("postgraduate").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            insertFacultySpinner(documentSnapshot.getData());
                            chosenStudyField = "postgraduate";
                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> facultyAdapterView, View view, int i, long l) {
                value = studyFieldMap.get(facultyAdapterView.getItemAtPosition(i));

                chosenFaculty = facultyAdapterView.getItemAtPosition(i).toString();

                firebaseFirestore.collection("studyField").document(chosenStudyField).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        insertProgramSpinner();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> programAdapterView, View view, int i, long l) {
                chosenFaculty = programAdapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void checkValidation() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        String verifyPassword = verifyPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            //name is null
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        //check validations


        if (memberCheckBox.isChecked()) {
            userType = 1;
        }
        if (execCheckBox.isChecked()) {
            userType = 2;
        }
        if (userType == 2) { //soc exec
            showSocExecDialog();
        } else if (userType == 1) {
            registerUser();
        }
    }

    private void registerUser() {
        //show progress bar if validations ok
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user is successfully registered and logged in
                            //profile activity starts here
                            saveUserInformation();
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(UserRegistrationActivity.this, "Error when register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertStudyFieldSpinner() {
        String[] studyFieldArr = {"undergraduate", "postgraduate"};
        studyArrayAdapter = new ArrayAdapter(UserRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, studyFieldArr);
        studyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studyFieldSpinner.setAdapter(studyArrayAdapter);
    }

    private void insertFacultySpinner(Map<String, Object> map) {
        studyFieldMap = map;
        ArrayList<String> facultyArr = new ArrayList<>();
        facultyArr.addAll(studyFieldMap.keySet());
        facultyArrayAdapter = new ArrayAdapter(UserRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, facultyArr);
        facultyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpinner.setAdapter(facultyArrayAdapter);
    }

    private void insertProgramSpinner() {
        firebaseFirestore.collection("studyField").document(chosenStudyField).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String programString = value.toString();
                programString = programString.replace("[", "");
                programString = programString.replace("]", "");
                String[] programArr = programString.split(",");

                programArrayAdapter = new ArrayAdapter(UserRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, programArr);
                programArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                programSpinner.setAdapter(programArrayAdapter);
            }
        });
    }

    private void saveUserInformation() {
        String name = fullNameEditText.getText().toString();
        String zID = zIDEditText.getText().toString();
        userId = firebaseAuth.getCurrentUser().getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("zID", zID);
        userMap.put("studyField", chosenFaculty);
        userMap.put("userType", userType);

        if (userType == 2) {
            userMap.put("execSoc", chosenSoc);
            userMap.put("execSocPosition", chosenSocPosition);
            firebaseFirestore.collection("Society").document(chosenSoc).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    selectedSocPositionMap = (HashMap<String, Object>) documentSnapshot.get("admin");

                    selectedSocPositionMap.put(chosenSocPosition, userId);
                    firebaseFirestore.collection("Society").document(chosenSoc).update("admin",selectedSocPositionMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            });
        }

        firebaseFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserRegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                    Toast.makeText(UserRegistrationActivity.this, "User Info Saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    String error = task.getException().getMessage();
                    //TODO: Change Toast to log
                    Toast.makeText(UserRegistrationActivity.this, "Firestore Error): " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSocExecDialog() {

        LayoutInflater inflater = (LayoutInflater) UserRegistrationActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_society_position, null);

        societyPositionDialog = new PopupWindow(view, TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, true);
        societySpinner = view.findViewById(R.id.societySpinner);
        positionSpinner = view.findViewById(R.id.positionSpinner);
        execSignUpButton = view.findViewById(R.id.execSignUpButton);
        execCancelButton = view.findViewById(R.id.execCancelButton);

        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            societyPositionDialog.setElevation(7.0f);
        }

        execSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chosenSocPosition != null) {
                    registerUser();
                    societyPositionDialog.dismiss();
                }
            }
        });

        execCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                societyPositionDialog.dismiss();
            }
        });
        firebaseFirestore.collection("Society").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final DocumentSnapshot document : task.getResult()) {
                        ArrayList<String> socPositionList = new ArrayList<>();
                        getSocString = document.getId();

                        HashMap<String, String> socPositionMap = (HashMap<String, String>) document.get("admin");
                        if (socPositionMap != null) {
                            Iterator it = socPositionMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                if (pair.getValue().toString().isEmpty()) {
                                    socPositionList.add(pair.getKey().toString());
                                }
                            }
                        }
                        if (!socPositionList.isEmpty()) {
                            allSocPositiondMap.put(getSocString, socPositionList);
                        }
                    }
                    setTheSpinner();
                }
            }
        });
    }

    private void setTheSpinner() {
        ArrayList<String> socArr = new ArrayList<>();
        socArr.addAll(allSocPositiondMap.keySet());
        socArrayAdapter = new ArrayAdapter(UserRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, socArr);
        socArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        societySpinner.setAdapter(socArrayAdapter);

        societySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> socAdapterView, View view, int i, long l) {

                chosenSoc = socAdapterView.getItemAtPosition(i).toString();
                ArrayList<String> socPositionArrayString = allSocPositiondMap.get(chosenSoc);

                socPositionArrayAdapter = new ArrayAdapter(UserRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, socPositionArrayString);
                socPositionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                positionSpinner.setAdapter(socPositionArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> socPositionAdapterView, View view, int i, long l) {
                chosenSocPosition = socPositionAdapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        societyPositionDialog.showAtLocation(registrationLinearLayout, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View view) {
        if (view == signUpButton) {
            checkValidation();
        }
    }

}
