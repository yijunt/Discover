package infs3611.discover.Activity.Fragment;

import android.app.TabActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import infs3611.discover.Activity.UserActivity;
import infs3611.discover.R;

import static android.content.ContentValues.TAG;

public class MatchSocFragment extends Fragment implements View.OnClickListener {
    private ImageView socPictureImageView;
    private TextView socDescTextView;
    private ImageButton passSocImageButton, addSocImageButton, favSocImageButton;
    private FirebaseUser firebaseUser;
    private String userId, matchSocName;
    private String[] userLikesArray;
    private Object userLikes;
    private FirebaseFirestore firebaseFirestore;
    private HashMap<Integer, String> allSocHashtagMap = new HashMap<>();
    private String socID, socIntro;
    private boolean passFlag = true;
    private ScrollView socDesScrollView;
    private Button addHobbyButton;
    private LinearLayout buttonsLinearLayout;
    private int currentCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.match_soc_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socPictureImageView = view.findViewById(R.id.matchSocImageView);
        socDescTextView = view.findViewById(R.id.matchSocDescTextView);
        passSocImageButton = view.findViewById(R.id.passSocImageButton);
        addSocImageButton = view.findViewById(R.id.addSocImageButton);
        favSocImageButton = view.findViewById(R.id.favouriteSocImageButton);
        socDesScrollView = view.findViewById(R.id.socDescScrollView);
        buttonsLinearLayout = view.findViewById(R.id.buttonsLinearLayout);
        addHobbyButton = view.findViewById(R.id.addHobbiesButton);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.get("likes") != null) {
                    userLikes = documentSnapshot.get("likes");
                    String userLikesString = userLikes.toString();
                    userLikesString = userLikesString.replace("[", "");
                    userLikesString = userLikesString.replace("]", "");
                    userLikesArray = userLikesString.split(",");
                }
            }
        });
        firebaseFirestore.collection("Society").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int matchCounter = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.get("hashtag"));
                        Object socHashtagObject = document.get("hashtag");
                        String socHastagString = socHashtagObject.toString();
                        socHastagString = socHastagString.replace("[", "");
                        socHastagString = socHastagString.replace("]", "");
                        String[] socHastagArray = socHastagString.split(",");

                        boolean matchFlag = false;
                        if (socHastagArray.length > userLikesArray.length) {
                            //userLikesArray shorter in length
                            for (String i : userLikesArray) {
                                for (String j : socHastagArray) {
                                    if (i.trim().equalsIgnoreCase(j)) {
                                        //get soc name
                                        matchSocName = document.getId();
                                        matchFlag = true;
                                        matchCounter++;
                                    }
                                }
                            }
                        } else {
                            //socHastagArray shorter in length
                            for (String i : socHastagArray) {
                                for (String j : userLikesArray) {
                                    if (i.equalsIgnoreCase(j.trim())) {
                                        //get soc name
                                        matchSocName = document.getId();
                                        matchFlag = true;
                                        matchCounter++;
                                    }
                                }
                            }
                        }
                        if (matchFlag && !allSocHashtagMap.containsValue(matchSocName)) {
                            allSocHashtagMap.put(matchCounter, matchSocName);
                        }
                        putDataAsView();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        passSocImageButton.setOnClickListener(this);

    }

    private void putDataAsView() {
        currentCount++;
        if (!allSocHashtagMap.isEmpty()) {
            addHobbyButton.setVisibility(View.GONE);
            socDesScrollView.setVisibility(View.VISIBLE);
            buttonsLinearLayout.setVisibility(View.VISIBLE);

            //put the latest map data in view, and pop it
            firebaseFirestore.collection("Society").document(allSocHashtagMap.get(currentCount)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    socID = documentSnapshot.getId();
                    socIntro = documentSnapshot.get("intro").toString();
                    socDescTextView.setText(socIntro);

                    //set picture too...
                }
            });
        } else {

            addHobbyButton.setVisibility(View.VISIBLE);
            socDesScrollView.setVisibility(View.GONE);
            buttonsLinearLayout.setVisibility(View.GONE);
            addHobbyButton.setOnClickListener(this);

        }
    }

    @Override
    public void onClick(View v) {

        if (v == passSocImageButton) {
            allSocHashtagMap.remove(currentCount);
            putDataAsView();
        } else if (v == addHobbyButton) {

            //return to userProfile page
            TabLayout tabLayout = UserActivity.tabLayout;
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        }
    }
}
