package smartAmigos.com.nammakarnataka;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import smartAmigos.com.nammakarnataka.helper.CircleProgressBarDrawable;
import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;
import smartAmigos.com.nammakarnataka.helper.place_general_adapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceCategoryFragment extends Fragment {

    private List<place_general_adapter> placesAdapter = new ArrayList<>();

    public PlaceCategoryFragment() {
        // Required empty public constructor
    }

    View view;
    String category;
    Context context;
    ListView place_list;
    SQLiteDatabaseHelper helper;
    Cursor placesListCursor;
    TextView category_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_place_category, container, false);

        initializeViews();

        return view;
    }


    public void initializeViews() {
        Bundle bundle = this.getArguments();
        category = bundle.getString("category", "");
        place_list = view.findViewById(R.id.places_list);

        context = getActivity().getApplicationContext();
        category_name = view.findViewById(R.id.category_name);
        category_name.setText(category+"S");

        helper = new SQLiteDatabaseHelper(context);
        placesListCursor = helper.getAllPlacesByCategory(category);
        while (placesListCursor.moveToNext()){

            placesAdapter.add( new place_general_adapter(
                    placesListCursor.getInt(0),
                    placesListCursor.getString(1),
                    placesListCursor.getString(2),
                    placesListCursor.getString(3),
                    placesListCursor.getString(4),
                    placesListCursor.getString(5),
                    placesListCursor.getDouble(6),
                    placesListCursor.getDouble(7),
                    placesListCursor.getString(8),
                    placesListCursor.getInt(9),
                    placesListCursor.getDouble(10)
                    ));
        }

        displayList();
    }




    private void displayList() {
        ArrayAdapter<place_general_adapter> adapter = new myPlaceAdapterClass();
        place_list.setAdapter(adapter);
        place_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    helper = new SQLiteDatabaseHelper(context);
                    place_general_adapter current = placesAdapter.get(position);


                    PlaceDetails placeDetails = new PlaceDetails();
                    Bundle fragment_agruments = new Bundle();
                    fragment_agruments.putInt("id", current.getId());

                    placeDetails.setArguments(fragment_agruments);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_activity_content, placeDetails).commit();

                }catch (Exception e){

                }

            }
        });
    }




    public class myPlaceAdapterClass extends ArrayAdapter<place_general_adapter> {

        myPlaceAdapterClass() {
            super(context, R.layout.place_list_item, placesAdapter);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.place_list_item, parent, false);
            }
            place_general_adapter current = placesAdapter.get(position);


            TextView t_name = itemView.findViewById(R.id.place_list_placename);
            t_name.setText(current.getTitle());


            TextView t_district = itemView.findViewById(R.id.place_list_districtname);
            t_district.setText(current.getDistrict());

            String head_image = getResources().getString(R.string.s3_base_url)+"/"+category+"/"+current.getId()+"/head.jpg";
            Uri uri = Uri.parse(head_image);
            SimpleDraweeView draweeView = itemView.findViewById(R.id.place_list_image);
            draweeView.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(1));
            draweeView.setImageURI(uri);

            return itemView;
        }

    }


}
