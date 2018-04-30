package infs3611.discover.Activity.Fragment;

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
import android.widget.ListView;
import android.widget.TextView;

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

public class EventListFragment extends Fragment {
    private ListView eventListView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userId;
    private String[] userJoinedSocArr;
    private Map<Integer, String> allSocEventMap;
    private ArrayAdapter eventListAdapter;
    private List<String> eventStringList = new ArrayList<>();
    private String[] eventArrayString;
    private String socName;
    private TextView noEventTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.soc_event_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventListView = view.findViewById(R.id.socEventListView);
        noEventTextView = view.findViewById(R.id.societyNoEventTextView);

    }

    @Override
    public void onStart() {
        super.onStart();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //get users' soc
        firebaseFirestore.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.get("joinedSociety") != null) {
                    noEventTextView.setVisibility(View.INVISIBLE);
                    eventListView.setVisibility(View.VISIBLE);
                    String userSocString = documentSnapshot.get("joinedSociety").toString();
                    userSocString = userSocString.replace("[", "");
                    userSocString = userSocString.replace("]", "");
                    userJoinedSocArr = userSocString.split(",");
                    for (String sName : userJoinedSocArr) {
                        socName = sName;
                        firebaseFirestore.collection("Society").document(socName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if (documentSnapshot.get("event") != null) {
                                    showUpcomingEvents(documentSnapshot.get("event"), socName);
                                }
                            }
                        });
                    }
                } else {
                    noEventTextView.setVisibility(View.VISIBLE);
                    eventListView.setVisibility(View.INVISIBLE);
                }
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
        eventListAdapter = new SocEventAdapter(getContext(), eventFinalList, socName);
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
}
