package infs3611.discover.Activity.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infs3611.discover.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser firebaseUser;
    private String userId;
    private Object userType, name, studyField, bioTextField, likesArray;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout linearLayout;
    private ImageView profilePic;
    private String[] likesArrayString;
    private TextView userNameTextView, studyFieldTextView, execPositionTextView, bioTextFieldTextView;
    private Button editBioButton, editLikesButton;
    private PopupWindow bioDialog, likesDialog, searchLikesDialog;
    private EditText bioEditText;
    private Button bioSaveButton, bioCancelButton, likesSaveButton, likesCancelButton, likesAddButton, likesSearchAddButton, likesSearchCancelButton;
    private String bio;
    private Map<String, Object> userMap;
    private GridView likesGridView, likesEditGridView;
    private Context context;
    private Spinner likesHeaderSpinner, likesBodySpinner;
    private LinearLayout userProfileLayout;
    private Map<String, Object> likesMap;
    private String chosenLikesHeader, chosenBodyHeader;
    private List<String> likesEditStringList = new ArrayList<>();
    private List<String> likesStringList = new ArrayList<>();
    private ArrayAdapter<String> editGridViewArrayAdapter, gridViewArrayAdapter;
    private Context likesContext;
    public UserProfileFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayout = view.findViewById(R.id.execSocLayout);
        profilePic = view.findViewById(R.id.profileImageView);
        userNameTextView = view.findViewById(R.id.profileNameTextView);
        studyFieldTextView = view.findViewById(R.id.profileStudyTextView);
        execPositionTextView = view.findViewById(R.id.profileMemberType);
        bioTextFieldTextView = view.findViewById(R.id.bioDetailTextView);
        editBioButton = view.findViewById(R.id.bioEditButton);
        likesGridView = view.findViewById(R.id.likesGridView);
        editLikesButton = view.findViewById(R.id.likesEditButton);
        userProfileLayout = view.findViewById(R.id.userProfileLayout);
    }

    @Override
    public void onStart() {
        super.onStart();
        context = getActivity().getApplicationContext();

        userMap = new HashMap<>();

        editBioButton.setOnClickListener(this);
        editLikesButton.setOnClickListener(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                userType = documentSnapshot.get("userType");

                if (userType.toString().equals("1")) {
                    linearLayout.setVisibility(LinearLayout.GONE);
                } else if (userType.toString().equals("2")) {

                }

                name = documentSnapshot.get("name");
                studyField = documentSnapshot.get("studyField");

                if (name != null && studyField != null) {
                    userNameTextView.setText(name.toString());
                    studyFieldTextView.setText(studyField.toString());
                }

                showUserBio(documentSnapshot.get("bio"));
                showUserLikes(documentSnapshot.get("likes"));

            }
        });

    }

    private void showUserBio(Object bioObject) {
        if (bioObject != null) {
            bioTextFieldTextView.setText(bioObject.toString());
        }
    }

    private void showUserLikes(Object likesObject) {
        if (likesObject != null) {
            String likesString = likesObject.toString();
            likesString = likesString.replace("[", "");
            likesString = likesString.replace("]", "");
            likesArrayString = likesString.split(",");

            likesStringList = new ArrayList<String>(Arrays.asList(likesArrayString));

            gridViewArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, likesStringList);

            likesGridView.setAdapter(gridViewArrayAdapter);

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v == editBioButton) {
            editBioFunction();
        } else if (v == editLikesButton) {
            editLikesFunction();
        }
    }

    private void editLikesFunction() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_likes_edit, null);
        likesContext = view.getContext();

        likesDialog = new PopupWindow(view, TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, true);

        likesSaveButton = view.findViewById(R.id.editLikesSaveButton);
        likesCancelButton = view.findViewById(R.id.editLikesCancelButton);
        likesAddButton = view.findViewById(R.id.addLikesButton);
        likesEditGridView = view.findViewById(R.id.likesEditGridView);

        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            likesDialog.setElevation(7.0f);
        }

        firebaseFirestore.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                likesArray = documentSnapshot.get("likes");
                if (likesArray != null) {
                    String likesString = likesArray.toString();
                    likesString = likesString.replace("[", "");
                    likesString = likesString.replace("]", "");
                    String[] likesArrayStringUnsaved = likesString.split(",");

                    likesEditStringList = new ArrayList<String>(Arrays.asList(likesArrayStringUnsaved));
                    editGridViewArrayAdapter = new ArrayAdapter<String>(likesContext, android.R.layout.simple_list_item_1, likesEditStringList);
                    likesEditGridView.setAdapter(editGridViewArrayAdapter);

                }
            }
        });

        likesAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater1 = (LayoutInflater) likesContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view1 = inflater1.inflate(R.layout.popup_likes_add, null);
                final Context searchLikesContext = view1.getContext();

                searchLikesDialog = new PopupWindow(view1, TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, true);

                likesSearchAddButton = view1.findViewById(R.id.editLikesAddButton);
                likesSearchCancelButton = view1.findViewById(R.id.editLikesCancelButton);
                likesBodySpinner = view1.findViewById(R.id.likesBodySpinner);
                likesHeaderSpinner = view1.findViewById(R.id.likesHeaderSpinner);

                searchLikesDialog.showAtLocation(userProfileLayout, Gravity.CENTER, 0, 0);
                firebaseFirestore.collection("Likes").document("Likes").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        likesMap = documentSnapshot.getData();

                        ArrayList<String> likesHeaderArr = new ArrayList<>();
                        likesHeaderArr.addAll(likesMap.keySet());
                        ArrayAdapter likesHeaderArrayAdaper = new ArrayAdapter(view1.getContext(), android.R.layout.simple_spinner_dropdown_item, likesHeaderArr);
                        likesHeaderArrayAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        likesHeaderSpinner.setAdapter(likesHeaderArrayAdaper);


                    }
                });
                likesHeaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        final Object likesValue = likesMap.get(parent.getItemAtPosition(position));

                        chosenLikesHeader = parent.getItemAtPosition(position).toString();

                        String likesString = likesValue.toString();
                        likesString = likesString.replace("[", "");
                        likesString = likesString.replace("]", "");
                        String[] likesArr = likesString.split(",");

                        ArrayAdapter likesBodyArrayAdapter = new ArrayAdapter(view1.getContext(), android.R.layout.simple_spinner_dropdown_item, likesArr);
                        likesBodyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        likesBodySpinner.setAdapter(likesBodyArrayAdapter);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                likesBodySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        chosenBodyHeader = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                likesSearchAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likesEditStringList.add(chosenBodyHeader);
                        editGridViewArrayAdapter.notifyDataSetChanged();
                        searchLikesDialog.dismiss();
                    }
                });

                likesSearchCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchLikesDialog.dismiss();
                    }
                });
            }
        });

        likesSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                likesStringList = likesEditStringList;
                firebaseFirestore.collection("Users").document(userId)
                        .update("likes",likesStringList).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                gridViewArrayAdapter.notifyDataSetChanged();
                likesDialog.dismiss();
            }
        });

        likesCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likesDialog.dismiss();
            }
        });


        likesDialog.showAtLocation(userProfileLayout, Gravity.CENTER, 0, 0);
    }

    private void editBioFunction() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_bio_edit, null);

        bioDialog = new PopupWindow(view, TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, true);
        bioEditText = view.findViewById(R.id.editBioEditText);
        bioSaveButton = view.findViewById(R.id.editBioSaveButton);
        bioCancelButton = view.findViewById(R.id.editBioCancelButton);

        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            bioDialog.setElevation(7.0f);
        }

        firebaseFirestore.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                bioTextField = documentSnapshot.get("bio");

                if (bioTextField != null) {
                    bioEditText.setText(bioTextField.toString());
                }
            }
        });
        bioDialog.showAtLocation(userProfileLayout, Gravity.CENTER, 0, 0);

        bioSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bio = bioEditText.getText().toString();
                userMap.put("bio", bio);

                firebaseFirestore.collection("Users").document(userId).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log
                        } else {
                            String error = task.getException().getMessage();
                            //TODO: Change Toast to log
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bioDialog.dismiss();
            }
        });
        bioCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bioDialog.dismiss();
            }
        });

    }

}
