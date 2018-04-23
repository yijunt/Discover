package infs3611.discover.Activity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.Iterator;
import java.util.Map;

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
    private Map<Integer, String> allSocHashtagMap;

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
                                    if (i.equalsIgnoreCase(j)) {
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
                                    if (i.equalsIgnoreCase(j)) {
                                        //get soc name
                                        matchSocName = document.getId();
                                        matchFlag = true;
                                        matchCounter++;
                                    }
                                }

                            }

                        }
                        if (matchFlag) {
                            allSocHashtagMap.put(matchCounter, matchSocName);
                        }
                    }
                    if(!allSocHashtagMap.isEmpty()) {
                        //run all map and get soc picture and description in loop
                        Iterator<Map.Entry<Integer, String>> iterator = allSocHashtagMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, String> entry = iterator.next();
                            Log.d("Society Map ->","Key : " + entry.getKey() + " Value :" + entry.getValue());
                            //im lost...
                        }

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });


        getUserLikes();


    }

    @Override
    public void onClick(View v) {

    }


    private void getUserLikes() {


    }
}
