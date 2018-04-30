package infs3611.discover.Activity.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;

import infs3611.discover.Activity.Adapter.MessageGroupAdapter;
import infs3611.discover.R;

public class MessageFragment extends Fragment implements View.OnClickListener {
    private ListView messageGroupListView;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseFirestore firebaseFirestore;
    private String[] socArrayString;
    private ArrayList<String> socStringList = new ArrayList<>();
    private ArrayAdapter socListAdapter;
    private Fragment chatFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageGroupListView = view.findViewById(R.id.messageGroupListView);
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

                showMessageGroup(documentSnapshot.get("joinedSociety"));
            }
        });
    }

    private void showMessageGroup(Object socObject) {
        if (socObject != null) {
            String socString = socObject.toString();
            socString = socString.replace("[", "");
            socString = socString.replace("]", "");
            socArrayString = socString.split(",");

            socStringList = new ArrayList<String>(Arrays.asList(socArrayString));

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, socStringList);
            socListAdapter = new MessageGroupAdapter(getContext(), socStringList);
            messageGroupListView.setAdapter(socListAdapter);
//            socListView.setAdapter(adapter);
            messageGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemValue = (String) messageGroupListView.getItemAtPosition(position);

                    chatFragment = new ChatFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString("selectedSocName", itemValue);
                    chatFragment.setArguments(arguments);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.simpleFrameLayout, chatFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {

    }
}
