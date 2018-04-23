package infs3611.discover.Activity.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import infs3611.discover.R;

public class DiscoverSocFragment extends Fragment implements View.OnClickListener {

    private Button discoverButton;
    private ListView favouriteSocListView;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.discover_soc_page_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        discoverButton = view.findViewById(R.id.discoverButton);
        favouriteSocListView = view.findViewById(R.id.userFavouriteSocListView);
    }

    @Override
    public void onStart() {
        super.onStart();

        context = getActivity().getApplicationContext();

        discoverButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == discoverButton) {

            MatchSocFragment matchSocFragment = new MatchSocFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.simpleFrameLayout, matchSocFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }
}
