package infs3611.discover.Activity.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import infs3611.discover.Activity.Adapter.ChatAdapter;
import infs3611.discover.R;

import static infs3611.discover.Activity.Fragment.UserProfileFragment.USER_NAME;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private ImageButton sendChatImageButton;
    private EditText inputChatEditText;
    private ListView conversationListView;
    private LinearLayout chatLinearLayout;
    private String userName;
    private TextView roomName;
    private ArrayAdapter arrayAdapter;
    String temp_key;
    private FirebaseFirestore firebaseFirestore;
    private TreeMap<String, Object> getAllConvoMap = new TreeMap<>();
    private FirebaseUser firebaseUser;
    private TreeMap<String, String> convoMap;
    private String id;
    private ArrayList<TreeMap<String, String>> messageFinalList = new ArrayList<TreeMap<String, String>>();
    private Map<String, Object> saveChatMap;

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
        chatLinearLayout = view.findViewById(R.id.chatLinearLayout);
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        id = firebaseUser.getUid();
        userName = USER_NAME;
        Bundle arguments = getArguments();
        roomName.setText(arguments.getString("selectedSocName"));

        firebaseFirestore.collection("Chatroom").document(roomName.getText().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.getData() != null) {
                    getAllConvoMap = new TreeMap(documentSnapshot.getData());

                    //perform ListView
                    performListView(getAllConvoMap);
                }
            }
        });
        sendChatImageButton.setOnClickListener(this);
    }

    private void scrollMyListViewToBottom() {
        conversationListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                conversationListView.setSelection(arrayAdapter.getCount() - 1);
            }
        });
    }
    private void performListView(TreeMap<String, Object> getAllConvoMap) {

        arrayAdapter = new ChatAdapter(getContext(), messageFinalList);
        arrayAdapter.clear();
        conversationListView.setAdapter(arrayAdapter);

        for (Map.Entry<String, Object> entry : getAllConvoMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String valueString = value.toString();
            convoMap = new TreeMap<>();
            if (valueString != null) {

                String dateData, nameData, messageData, userIdData;
                int dateInt = valueString.indexOf("date=");
                int nameInt = valueString.indexOf("name=");
                int messageInt = valueString.indexOf("message=");
                int userIDInt = valueString.indexOf("userId=");

                dateData = valueString.substring(dateInt, nameInt - 1);
                dateInt = dateData.indexOf("=");
                dateData = dateData.substring(dateInt + 1, dateData.length() - 1);

                nameData = valueString.substring(nameInt, messageInt - 1);
                nameInt = nameData.indexOf("=");
                nameData = nameData.substring(nameInt + 1, nameData.length() - 1);

                messageData = valueString.substring(messageInt, userIDInt - 1);
                messageInt = messageData.indexOf("=");
                messageData = messageData.substring(messageInt + 1, messageData.length() - 1);

                userIdData = valueString.substring(userIDInt, valueString.length() - 1);
                userIDInt = userIdData.indexOf("=");
                userIdData = userIdData.substring(userIDInt + 1);


                convoMap.put("date", dateData);
                convoMap.put("name", nameData);
                convoMap.put("message", messageData);
                if (userIdData.equals(id)) { //if the message is logged in user sent
                    convoMap.put("userIdFlag", "yes");
                } else {
                    convoMap.put("userIdFlag", "not");
                }
            }

            messageFinalList.add(convoMap);
        }

        scrollMyListViewToBottom();
    }

    @Override
    public void onClick(View view) {
        if (view == sendChatImageButton) {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(chatLinearLayout.getWindowToken(), 0);

            if (!inputChatEditText.getText().toString().trim().equals("")) {
                Map<String, String> sentChatMap = new HashMap<>();
                String name = userName;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String curDate = dateFormat.format(date);
                String message = inputChatEditText.getText().toString();

                sentChatMap.put("userId", id);
                sentChatMap.put("name", name);
                sentChatMap.put("date", curDate);
                sentChatMap.put("message", message);

                saveChatMap = new TreeMap<>();
                saveChatMap.put(curDate, sentChatMap);
                firebaseFirestore.collection("Chatroom").document(roomName.getText().toString()).update(saveChatMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("sendChatSuccessful", "Message Saved Successful");

                            inputChatEditText.setText("");
                        } else {
                            String error = task.getException().getMessage();
                            Log.e("sendChatError", error);
                            firebaseFirestore.collection("Chatroom").document(roomName.getText().toString()).set(saveChatMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Log.d("sendChatSuccessful", "Message Saved Successful");
                                        inputChatEditText.setText("");
                                    } else {
                                        String error = task.getException().getMessage();
                                        Log.e("sendChatError", error);
                                    }
                                }
                            });
                        }
                    }
                });
            }

        }
    }
}
