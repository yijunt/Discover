package infs3611.discover.Activity.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import infs3611.discover.R;

public class SocEventAdapter extends ArrayAdapter<HashMap<String, String>> {
    ArrayList<HashMap<String, String>> eventFinalList;
    Context context;
    String socName;

    public SocEventAdapter(Context context, ArrayList<HashMap<String, String>> eventFinalList, String socName) {
        super(context, R.layout.soc_profile_event_list_item, eventFinalList);
        this.eventFinalList = eventFinalList;
        this.context = context;
        this.socName = socName;
    }

    @Override
    public int getCount() {
        return eventFinalList.size();
    }

    @Nullable
    @Override
    public HashMap<String, String> getItem(int position) {
        return eventFinalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HashMap<String, String> eventSplitMap = eventFinalList.get(position);
        ItemViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ItemViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            convertView = inflater.inflate(R.layout.soc_profile_event_list_item, null);
            viewHolder.eventDateTextView = convertView.findViewById(R.id.societyEventDateTextView);
            viewHolder.eventMonthTextView = convertView.findViewById(R.id.societyEventMonthTextView);
            viewHolder.eventDayTextView = convertView.findViewById(R.id.societyEventDayTextView);
            viewHolder.eventNameTextView = convertView.findViewById(R.id.societyEventNameTextView);
            viewHolder.eventSocTextView = convertView.findViewById(R.id.societyEventSocietyNameTextView);
            viewHolder.eventPlaceTextView = convertView.findViewById(R.id.societyEventLocationTextView);
            viewHolder.eventLinearLayout = convertView.findViewById(R.id.societyUpcomingEventLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }
        viewHolder.eventDateTextView.setText(eventSplitMap.get("eventDate"));
        viewHolder.eventMonthTextView.setText(eventSplitMap.get("eventMonth"));
        viewHolder.eventDayTextView.setText(eventSplitMap.get("eventDay"));
        viewHolder.eventNameTextView.setText(eventSplitMap.get("eventName"));
        viewHolder.eventSocTextView.setText(socName);
        viewHolder.eventPlaceTextView.setText(eventSplitMap.get("eventLocation"));

        return convertView;
    }

    public class ItemViewHolder {
        TextView eventMonthTextView;
        TextView eventDateTextView;
        TextView eventDayTextView;
        TextView eventNameTextView;
        TextView eventSocTextView;
        TextView eventPlaceTextView;
        LinearLayout eventLinearLayout;
    }

}
