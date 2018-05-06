package infs3611.discover.Activity.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import java.util.regex.Pattern;

import infs3611.discover.Activity.Adapter.SocEventAdapter;
import infs3611.discover.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ExecSocietyProfileFragment extends Fragment implements OnClickListener {
    private ImageButton websiteImageButton, emailImageButton, facebookImageButton;
    private ArrayList<HashMap<String, String>> socEventObject = new ArrayList<HashMap<String, String>>();
    private TextView societyDescTextView, societyMembersNumberTextView, noEventTextView;
    private Button societyViewAdminButton, socEditDescButton;
    private ImageView societyProfilePic;
    private TextView socNameTextView, socShortNameTextView;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private Map<String, Object> socMap;
    private Object socEvent, name, shortName;
    private String userId;
    private ListView eventListView;
    private PopupWindow bioDialog, eventDialog;
    private ArrayAdapter eventListAdapter;
    private EditText bioEditText;
    private ConstraintLayout execSocContraintLayout;
    private Button bioSaveButton, bioCancelButton;
    private Object bioTextField;
    private List<String> eventStringList = new ArrayList<>();
    private String[] eventArrayString;
    private FirebaseUser firebaseUser;
    private String websiteLink, emailLink, facebookLink;
    private String selectedSoc;
    private String bio;
    private Button addEventButton, saveEventButton, cancelEventButton;
    private EditText eventNameEditText, eventDateEditText, eventTimeEditText, eventLocationEditText, eventLinkEditText;
    private Map<String, Object> userMap = new HashMap<>();
    private HashMap<String, String> eventHashMap = new HashMap<>();
    private List<String> joinedSocStringList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exec_soc_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        websiteImageButton = view.findViewById(R.id.societyWebsiteImageButton);
        emailImageButton = view.findViewById(R.id.societyEmailImageButton);
        facebookImageButton = view.findViewById(R.id.societyFacebookImageButton);
        societyDescTextView = view.findViewById(R.id.societyDescriptionTextView);
        societyMembersNumberTextView = view.findViewById(R.id.societyMemberNoTextView);
        societyViewAdminButton = view.findViewById(R.id.societyViewAdminButton);
        noEventTextView = view.findViewById(R.id.societyNoEventTextView);
        eventListView = view.findViewById(R.id.eventListView);
        societyProfilePic = view.findViewById(R.id.societyImageView);
        socNameTextView = view.findViewById(R.id.societyNameTextView);
        socShortNameTextView = view.findViewById(R.id.shortSocietyNameTextView);
        socEditDescButton = view.findViewById(R.id.editSocDescButton);
        execSocContraintLayout = view.findViewById(R.id.execSocContraintLayout);
        addEventButton = view.findViewById(R.id.addEvent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        selectedSoc = arguments.getString("selectedSocName");
        context = getActivity().getApplicationContext();

        societyViewAdminButton.setOnClickListener(this);
        websiteImageButton.setOnClickListener(this);
        emailImageButton.setOnClickListener(this);
        facebookImageButton.setOnClickListener(this);
        socEditDescButton.setOnClickListener(this);
        addEventButton.setOnClickListener(this);

        socMap = new HashMap<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Society").document(selectedSoc).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                socEvent = documentSnapshot.get("event");
                if (socEvent != null) {
                    eventListView.setVisibility(ListView.VISIBLE);
                    noEventTextView.setVisibility(TextView.GONE);
                    showUpcomingEvents(socEvent, documentSnapshot.get("name").toString());
                } else {
                    eventListView.setVisibility(LinearLayout.GONE);
                    noEventTextView.setVisibility(TextView.VISIBLE);
                }

                name = documentSnapshot.get("name");
                shortName = documentSnapshot.get("shortName");
                if (name != null && shortName != null) {
                    socNameTextView.setText(name.toString());
                    socShortNameTextView.setText(shortName.toString());
                }
                if (documentSnapshot.get("link") != null) {
                    String linkString = documentSnapshot.get("link").toString();
                    int emailInt = linkString.indexOf("email=");
                    int facebookInt = linkString.indexOf("facebook=");
                    int websiteInt = linkString.indexOf("website=");

                    websiteLink = linkString.substring(websiteInt, facebookInt - 1);
                    websiteInt = websiteLink.indexOf("=");
                    websiteLink = websiteLink.substring(websiteInt + 1, websiteLink.length() - 1);

                    facebookLink = linkString.substring(facebookInt, emailInt - 1);
                    facebookInt = facebookLink.indexOf("=");
                    facebookLink = facebookLink.substring(facebookInt + 1, facebookLink.length() - 1);

                    emailLink = linkString.substring(emailInt, linkString.length() - 1);
                    emailInt = emailLink.indexOf("=");
                    emailLink = emailLink.substring(emailInt + 1);

                }
                if (documentSnapshot.get("profilePic") != null) {
                    //TODO: set profile pic
                }

                showSocDesc(documentSnapshot.get("description"));
                showMemberNumbersJoined(documentSnapshot.get("numberOfMembers"));
            }
        });

        ifUserJoinedSoc();
    }

    private void showUpcomingEvents(Object eventObject, String socName) {

        String likesString = eventObject.toString();
        likesString = likesString.replace("[", "");
        likesString = likesString.replace("]", "");
        eventArrayString = likesString.split(Pattern.quote("},"));

        ArrayList<HashMap<String, String>> eventFinalList = new ArrayList<HashMap<String, String>>();
        eventStringList = new ArrayList<String>(Arrays.asList(eventArrayString));
        eventListAdapter = new SocEventAdapter(context, eventFinalList, socName);
        eventListView.setAdapter(eventListAdapter);
        for (String object : eventStringList) {
            String[] eventStringArray;
            String eventInfo = object;
            eventInfo = eventInfo.replace("{", "");
            eventInfo = eventInfo.replace("}", "");
            eventStringArray = eventInfo.split(",");

            HashMap<String, String> eventSplitMap = new HashMap<>();
            for (String moreObject : eventStringArray) {
                int cutAt = moreObject.indexOf("=");
                String key = moreObject.substring(0, cutAt);
                key = key.trim();
                String data = moreObject.substring(cutAt + 1);
                eventSplitMap.put(key, data);
            }
            eventFinalList.add(eventSplitMap);
        }
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object checkEvent = eventListView.getAdapter().getItem(position);

                String checkedEventLink = checkEvent.toString();
                int x = checkedEventLink.indexOf("=http");
                checkedEventLink = checkedEventLink.substring(x + 1);
                checkedEventLink = checkedEventLink.substring(0, checkedEventLink.length() - 1);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(checkedEventLink));
                startActivity(intent);
            }
        });

    }

    private void showMemberNumbersJoined(Object numberOfMembersObject) {
        if (numberOfMembersObject != null) {
            societyMembersNumberTextView.setText(numberOfMembersObject.toString());
        }
    }

    private void showSocDesc(Object descriptionObject) {
        if (descriptionObject != null) {
            String desc = descriptionObject.toString();
            desc = desc.replace("_a", "\n");
            societyDescTextView.setText(desc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        if (v == societyViewAdminButton) {
            //set new fragment
        } else if (v == websiteImageButton) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(websiteLink));
            startActivity(intent);

        } else if (v == emailImageButton) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", emailLink, null));
            startActivity(intent);

        } else if (v == facebookImageButton) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(facebookLink));
            startActivity(intent);
        } else if (v == socEditDescButton) {
            editSocBioFunction();
        } else if (v == addEventButton) {
            addSocEventFunction();
        }
    }

    private void addSocEventFunction() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_soc_add_event, null);

        eventDialog = new PopupWindow(view, TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, true);
        saveEventButton = view.findViewById(R.id.editEventSaveButton);
        cancelEventButton = view.findViewById(R.id.editEventCancelButton);
        eventNameEditText = view.findViewById(R.id.editEventName);
        eventDateEditText = view.findViewById(R.id.editEventDate);
        eventTimeEditText = view.findViewById(R.id.editEventTime);
        eventLocationEditText = view.findViewById(R.id.editEventLocation);
        eventLinkEditText = view.findViewById(R.id.editEventLink);

        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            eventDialog.setElevation(7.0f);
        }
        eventDialog.showAtLocation(execSocContraintLayout, Gravity.CENTER, 0, 0);

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHashMap.put("eventName", eventNameEditText.getText().toString());
                eventHashMap.put("eventDate", eventDateEditText.getText().toString());
                eventHashMap.put("eventLocation", eventLocationEditText.getText().toString());
                eventHashMap.put("eventLink", eventLinkEditText.getText().toString());

                firebaseFirestore.collection("Society").document(selectedSoc).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        socEventObject = (ArrayList<HashMap<String, String>>) documentSnapshot.get("event");
                    }
                });
                if (socEventObject != null) {
                    socEventObject.add(socEventObject.size(), eventHashMap);

                    firebaseFirestore.collection("Society").document(selectedSoc).update("event", socEventObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Log
                                eventDialog.dismiss();
                            } else {
                                String error = task.getException().getMessage();
                                //TODO: Change Toast to log
                                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        cancelEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bioDialog.dismiss();
            }
        });

    }

    private void ifUserJoinedSoc() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseFirestore.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.get("joinedSociety") != null) {
                    String joinedSoc = documentSnapshot.get("joinedSociety").toString();
                    joinedSoc = joinedSoc.replace("[", "");
                    joinedSoc = joinedSoc.replace("]", "");
                    joinedSoc = joinedSoc.replace(", ", ",");
                    String[] joinedSocArray = joinedSoc.split(",");

                    joinedSocStringList = Arrays.asList(joinedSocArray);

                }
                if (!joinedSocStringList.contains(selectedSoc)) {
                    joinedSocStringList.add(selectedSoc);

                    firebaseFirestore.collection("Users").document(userId).update("joinedSociety", joinedSocStringList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            } else {
                                String error = task.getException().getMessage();
                                //TODO: Change Toast to log
                                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void editSocBioFunction() {

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

        firebaseFirestore.collection("Society").document(selectedSoc).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                bioTextField = documentSnapshot.get("description");

                if (bioTextField != null) {

                    String desc = bioTextField.toString();
                    desc = desc.replace("_a", "\n");
                    bioEditText.setText(desc);
                }
            }
        });
        bioDialog.showAtLocation(execSocContraintLayout, Gravity.CENTER, 0, 0);

        bioSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bio = bioEditText.getText().toString();
                userMap.put("description", bio);

                firebaseFirestore.collection("Society").document(selectedSoc).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
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
