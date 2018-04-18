package infs3611.discover.Activity.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import infs3611.discover.R;

public class SocietyProfileFragment extends Fragment implements View.OnClickListener {
    private ImageButton websiteImageButton, emailImageButton, facebookImageButton;
    private TextView societyDescTextView, societyMembersNumberTextView, noEventTextView;
    private Button societyJoinButton, societyViewAdminButton;
    private TextView eventMonth, eventDate, eventDay;
    private TextView eventName, eventSoc, eventPlace, eventLink;
    private LinearLayout eventLinearLayout;
    private ImageButton societyProfilePic;
    private TextView socNameTextView, socShortNameTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.society_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        websiteImageButton = view.findViewById(R.id.societyWebsiteImageButton);
        emailImageButton = view.findViewById(R.id.societyEmailImageButton);
        facebookImageButton = view.findViewById(R.id.societyFacebookImageButton);
        societyDescTextView = view.findViewById(R.id.societyDescriptionTextView);
        societyMembersNumberTextView = view.findViewById(R.id.societyMemberNoTextView);
        societyJoinButton = view.findViewById(R.id.societyJoinButton);
        societyViewAdminButton = view.findViewById(R.id.societyViewAdminButton);
        noEventTextView = view.findViewById(R.id.societyNoEventTextView);
        eventMonth = view.findViewById(R.id.societyEventMonthTextView);
        eventDate = view.findViewById(R.id.societyEventDateTextView);
        eventDay = view.findViewById(R.id.societyEventDayTextView);
        eventName = view.findViewById(R.id.societyEventNameTextView);
        eventSoc = view.findViewById(R.id.societyEventSocietyNameTextView);
        eventPlace = view.findViewById(R.id.societyEventLocationTextView);
        eventLinearLayout = view.findViewById(R.id.societyUpcomingEventLayout);
        societyProfilePic = view.findViewById(R.id.societyImageView);
        socNameTextView = view.findViewById(R.id.societyNameTextView);
        socShortNameTextView = view.findViewById(R.id.shortSocietyNameTextView);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }
}
