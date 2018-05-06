package infs3611.discover.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import infs3611.discover.R;

public class ChatAdapter extends ArrayAdapter<TreeMap<String, String>> {
    ArrayList<TreeMap<String, String>> chatDetails;
    Context context;

    public ChatAdapter(@NonNull Context context, ArrayList<TreeMap<String, String>> chatMap) {
        super(context, R.layout.message_text_item, chatMap);
        this.chatDetails = chatMap;
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatDetails.size();
    }

    @Nullable
    @Override
    public TreeMap<String, String> getItem(int position) {
        return chatDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TreeMap<String, String> chatSplitMap = chatDetails.get(position);
        ItemViewHolder itemViewHolder;

        if (convertView == null) {
            itemViewHolder = new ItemViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.message_text_item, null);
            itemViewHolder.userNameTextView = convertView.findViewById(R.id.messageNameTextView);
            itemViewHolder.messageContentTextView = convertView.findViewById(R.id.messageContentTextView);
            itemViewHolder.timeTextView = convertView.findViewById(R.id.messageTimeTextView);
            itemViewHolder.messageBoxRelativeLayout = convertView.findViewById(R.id.message_root);

            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }

        if (chatSplitMap.get("userIdFlag").equals("not")) {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                itemViewHolder.messageBoxRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.message_grey_box));
            } else {
                itemViewHolder.messageBoxRelativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.message_grey_box));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            itemViewHolder.timeTextView.setLayoutParams(params);
            itemViewHolder.messageBoxRelativeLayout.setLayoutParams(params);
            itemViewHolder.messageContentTextView.setTextColor(R.color.colorDarkGrey);
        } else {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                itemViewHolder.messageBoxRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.message_blue_box));
            } else {
                itemViewHolder.messageBoxRelativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.message_blue_box));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            itemViewHolder.timeTextView.setLayoutParams(params);
            itemViewHolder.messageBoxRelativeLayout.setLayoutParams(params);
            itemViewHolder.messageContentTextView.setTextColor(R.color.colorWhite);
        }

        itemViewHolder.userNameTextView.setText(chatSplitMap.get("name"));
        itemViewHolder.messageContentTextView.setText(chatSplitMap.get("message"));
        itemViewHolder.timeTextView.setText(chatSplitMap.get("date"));

        return convertView;
    }

    public class ItemViewHolder {
        TextView userNameTextView, messageContentTextView, timeTextView;
        RelativeLayout messageBoxRelativeLayout;
    }
}
