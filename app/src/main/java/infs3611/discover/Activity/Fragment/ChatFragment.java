package infs3611.discover.Activity.Fragment;

import android.Manifest;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import infs3611.discover.R;

import static infs3611.discover.Activity.Fragment.UserProfileFragment.USER_NAME;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private ImageButton sendChatImageButton;
    private EditText inputChatEditText;
    private ListView conversationListView;
    private RecyclerView conversationRecyclerView;
    private String userName;
    private TextView roomName;
    ArrayList<String> listConversation = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    String temp_key;
    private FirebaseFirestore firebaseFirestore;
    private Map<String, Object> getAllConvoMap = new TreeMap<>();
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_room_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputChatEditText = view.findViewById(R.id.chatEntryEditText);
        sendChatImageButton = view.findViewById(R.id.sendChatButton);
        roomName = view.findViewById(R.id.socNameInMessageTextView);
        conversationListView = view.findViewById(R.id.conversationListView);
//        conversationRecyclerView = view.findViewById(R.id.conversationRecyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userName = USER_NAME;
        Bundle arguments = getArguments();
        roomName.setText(arguments.getString("selectedSocName"));

        firebaseFirestore.collection("Chatroom").document(roomName.getText().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.getData() != null) {
                    getAllConvoMap = documentSnapshot.getData();

                    //perform ListView
                    performListView(getAllConvoMap);
                }
            }
        });
        sendChatImageButton.setOnClickListener(this);


//        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listConversation);
//        conversationListView.setAdapter(arrayAdapter);


    }

    private void performListView(Map<String, Object> getAllConvoMap) {

    }

    @Override
    public void onClick(View view) {
        if (view == sendChatImageButton) {
            Map<String, String> sentChatMap = new HashMap<>();

            String name = userName;
            String id = firebaseUser.getUid();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String curDate = dateFormat.format(date);
            String message = inputChatEditText.getText().toString();

            sentChatMap.put("userId", id);
            sentChatMap.put("name", name);
            sentChatMap.put("date", curDate);
            sentChatMap.put("message", message);

            Map<String, Object> saveChatMap = new TreeMap<>();
            saveChatMap.put(curDate, sentChatMap);
            firebaseFirestore.collection("Chatroom").document(roomName.getText().toString()).set(saveChatMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("sendChatSuccessful", "Message Saved Successful");
                        //make sure view is updated

                    } else {
                        String error = task.getException().getMessage();
                        Log.e("sendChatError", error);
                    }
                }
            });
            inputChatEditText.setText("");

//            Map<String, Object> map = new HashMap<String, Object>();
//
//            Map<String, Object> map1 = new HashMap<String, Object>();
//            map1.put("msg", inputChatEditText.getText().toString());
//            map1.put("user", userName);
//
//            inputChatEditText.setText("");
        }

    }

    public void updateConversation(DataSnapshot dataSnapshot) {
        String msg, user, conversation;
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            msg = (String) ((DataSnapshot) i.next()).getValue();
            user = (String) ((DataSnapshot) i.next()).getValue();

            conversation = user + ": " + msg;
            arrayAdapter.insert(conversation, arrayAdapter.getCount());
            arrayAdapter.notifyDataSetChanged();
        }
    }

}
