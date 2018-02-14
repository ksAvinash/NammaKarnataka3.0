package smartAmigos.com.nammakarnataka;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;
import smartAmigos.com.nammakarnataka.helper.districts_adapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class DistrictListFragment extends Fragment {


    public DistrictListFragment() {
        // Required empty public constructor
    }

    View view;
    private List<districts_adapter> dist_adapterList = new ArrayList<>();
    ListView list;
    Context context;
    SQLiteDatabaseHelper helper;
    Cursor districtCursor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_district_list, container, false);

        initializeViews();


        populateDistrictList();

        return view;
    }


    private void initializeViews(){
        context = getActivity().getApplicationContext();
        list = view.findViewById(R.id.districtsList);
        dist_adapterList.clear();

    }


    private void populateDistrictList(){
        helper = new SQLiteDatabaseHelper(context);
        districtCursor = helper.getAllDistricts();

        while(districtCursor.moveToNext()){
            dist_adapterList.add(new districts_adapter( districtCursor.getString(0)));
        }

        displayList();
    }


    private void displayList() {
        ArrayAdapter<districts_adapter> adapter = new districtAdapterClass();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                districtCursor.moveToPosition(position);
                String dist = districtCursor.getString(0);

                PlacesListFragment placesListFragment = new PlacesListFragment();
                Bundle fragment_agruments = new Bundle();

                fragment_agruments.putString("district", dist);
                placesListFragment.setArguments(fragment_agruments);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();

            }
        });
    }


    public class districtAdapterClass extends ArrayAdapter<districts_adapter> {

        districtAdapterClass() {
            super(context, R.layout.district_item, dist_adapterList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.district_item, parent, false);

            }
            districts_adapter current = dist_adapterList.get(position);

            TextView t_name = itemView.findViewById(R.id.item_distTitle);
            t_name.setText(current.getDistrict());

            return itemView;
        }

    }



}
