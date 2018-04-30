package infs3611.discover.Activity.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import infs3611.discover.R;

public class MessageGroupAdapter extends ArrayAdapter<String>{
    ArrayList<String> socArrayList;
    Context context;

    public MessageGroupAdapter(@NonNull Context context, ArrayList<String> socArrayList) {
        super(context, R.layout.message_group_list_item, socArrayList);
        this.socArrayList = socArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return socArrayList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return socArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String socString = socArrayList.get(position);
        ItemViewHolder itemViewHolder;

        if(convertView == null) {
            itemViewHolder = new ItemViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.message_group_list_item, null);
            itemViewHolder.socNameTextView = convertView.findViewById(R.id.socNameTextView);
            itemViewHolder.socPicImageView = convertView.findViewById(R.id.societyIconImageView);
            itemViewHolder.latestMessageTimeTextView = convertView.findViewById(R.id.messageLatestTimeTextView);

            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        itemViewHolder.socNameTextView.setText(socString);

        return convertView;
    }

    public class ItemViewHolder{
        TextView socNameTextView;
        ImageView socPicImageView;
        TextView latestMessageTimeTextView;
    }
}
