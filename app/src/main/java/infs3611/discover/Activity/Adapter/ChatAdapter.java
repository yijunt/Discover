package infs3611.discover.Activity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import infs3611.discover.R;

public class ChatAdapter extends ArrayAdapter<HashMap<String, Object>> {
    public ChatAdapter(@NonNull Context context, ArrayList<HashMap<String, Object>> chatMap) {
        super(context, R.layout.message_text_item, chatMap);
    }
}
