package infs3611.discover.Activity.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

public class SocietyProfileFragment extends Fragment implements View.OnClickListener {
    private ImageButton websiteImageButton, emailImageButton, facebookImageButton;
    private TextView societyDescTextView, societyMembersNumberTextView, noEventTextView;
    private Button societyJoinButton, societyViewAdminButton;
    private ImageView societyProfilePic;
    private TextView socNameTextView, socShortNameTextView;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private Map<String, Object> socMap;
    private Object socEvent, name, shortName;
    private ListView eventListView;
    private ArrayAdapter eventListAdapter;
    private List<String> eventStringList = new ArrayList<>();
    private String[] eventArrayString;
    private String websiteLink, emailLink, facebookLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.society_profile_fragment, container, false);
    }

    public SocietyProfileFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        websiteImageButton = view.findViewById(R.id.societyWebsiteImageButton);
        emailImageButton = view.findViewById(R.id.societyEmailImageButton);
        facebookImageButton = view.findViewById(R.id.societyFacebookImageButton);
        societyDescTextView = view.findViewById(R.id.societyDescriptionTextView);
        societyMembersNumberTextView = view.findViewById(R.id.societyMemberNoTextView);
        societyJoinButton = view.findViewById(R.id.societyJoinButton);
        societyViewAdminButton = view.findViewById(R.id.societyViewAdminButton);
        noEventTextView = view.findViewById(R.id.societyNoEventTextView);
        eventListView = view.findViewById(R.id.eventListView);
        societyProfilePic = view.findViewById(R.id.societyImageView);
        socNameTextView = view.findViewById(R.id.societyNameTextView);
        socShortNameTextView = view.findViewById(R.id.shortSocietyNameTextView);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        String selectedSoc = arguments.getString("selectedSocName");
        context = getActivity().getApplicationContext();

        societyViewAdminButton.setOnClickListener(this);
        websiteImageButton.setOnClickListener(this);
        emailImageButton.setOnClickListener(this);
        facebookImageButton.setOnClickListener(this);

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
        }
    }
}
