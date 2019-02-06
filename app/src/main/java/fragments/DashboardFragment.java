package fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.csefee.mashael.muslat.CartviewAdapter;
import com.csefee.mashael.muslat.DetailActivity;
import com.csefee.mashael.muslat.R;
import com.csefee.mashael.muslat.SuggestionActivity;
import com.csefee.mashael.muslat.clientid;

import database.FavDatabaseHelper;
import database.PublicCairoDatabaseHelper;
import database.PublicTransportDataBaseHelper;
import database.SuperJetDatabaseHelper;
import database.TrainDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    LinearLayoutManager layoutManager;
    private String[] startpoint, endpoint, starttime;
    private int[] jet_id, train_id, public_cairo_id, public_transport_id, endtime, distance, price;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        RecyclerView superjetRecyclerView = getView().findViewById(R.id.jet_fav);
        RecyclerView trainRecyclerView = getView().findViewById(R.id.trains_fav);
        RecyclerView publiccairoRecyclerView = getView().findViewById(R.id.publiccairo_fav);
        RecyclerView publictransportRecyclerView = getView().findViewById(R.id.publictransport_fav);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = superjetRecyclerView.getLayoutParams();
        params.width = width;
        superjetRecyclerView.setLayoutParams(params);

        params = trainRecyclerView.getLayoutParams();
        params.width = width;
        trainRecyclerView.setLayoutParams(params);

        params = publiccairoRecyclerView.getLayoutParams();
        params.width = width;
        publiccairoRecyclerView.setLayoutParams(params);

        params = publictransportRecyclerView.getLayoutParams();
        params.width = width;
        publictransportRecyclerView.setLayoutParams(params);

        SQLiteOpenHelper favSqLiteOpenHelper = new FavDatabaseHelper(getContext());
        SQLiteDatabase db = favSqLiteOpenHelper.getReadableDatabase();

        SQLiteOpenHelper superjetSqLiteOpenHelper = new SuperJetDatabaseHelper(getContext());
        SQLiteDatabase jetDatabase = superjetSqLiteOpenHelper.getReadableDatabase();

        SQLiteOpenHelper trainSqLiteOpenHelper = new TrainDatabaseHelper(getContext());
        SQLiteDatabase trainDatabase = trainSqLiteOpenHelper.getReadableDatabase();

        SQLiteOpenHelper publiccairoSqLiteOpenHelper = new PublicCairoDatabaseHelper(getContext());
        SQLiteDatabase publiccairoDatabase = publiccairoSqLiteOpenHelper.getReadableDatabase();

        SQLiteOpenHelper publictranspoerSqLiteOpenHelper = new PublicTransportDataBaseHelper(getContext());
        SQLiteDatabase publictransportDatabase = publictranspoerSqLiteOpenHelper.getReadableDatabase();

        //superjet fav handling
        Cursor cursor = db.rawQuery("SELECT tableid FROM favourite Where type=? ", new String[]{"jet"});
        startpoint = new String[cursor.getCount()];
        starttime = new String[cursor.getCount()];
        endpoint = new String[cursor.getCount()];
        endtime = new int[cursor.getCount()];
        distance = new int[cursor.getCount()];
        price = new int[cursor.getCount()];
        jet_id = new int[cursor.getCount()];

        int i = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Cursor cursor1 = jetDatabase.rawQuery("SELECT * FROM scedule WHERE  _id=?", new String[]{String.valueOf(cursor.getInt(0))});
                if (cursor1.moveToFirst()) {
                    Cursor c = jetDatabase.rawQuery("SELECT city_name FROM city WHERE _id=?", new String[]{Integer.toString(cursor1.getInt(0))});
                    c.moveToFirst();
                    startpoint[i] = c.getString(0);
                    c.close();
                    c = jetDatabase.rawQuery("SELECT city_name FROM city WHERE _id=?", new String[]{Integer.toString(cursor1.getInt(1))});
                    c.moveToFirst();
                    endpoint[i] = c.getString(0);
                    c.close();
                    starttime[i] = cursor1.getString(2);
                    distance[i] = cursor1.getInt(3);
                    price[i] = cursor1.getInt(4);
                    jet_id[i] = cursor1.getInt(5);
                }
                cursor1.close();
                cursor.moveToNext();
                i++;
            }
        }
        cursor.close();

        CartviewAdapter jetAdapter = new CartviewAdapter(startpoint, starttime, endpoint, endtime, distance, price);
        superjetRecyclerView.setAdapter(jetAdapter);

        layoutManager = new LinearLayoutManager(getActivity());
        superjetRecyclerView.setLayoutManager(layoutManager);


        //train fav
        cursor = db.rawQuery("SELECT tableid FROM favourite Where type=? ", new String[]{"train"});
        startpoint = new String[cursor.getCount()];
        starttime = new String[cursor.getCount()];
        endpoint = new String[cursor.getCount()];
        endtime = new int[cursor.getCount()];
        distance = new int[cursor.getCount()];
        price = new int[cursor.getCount()];
        train_id = new int[cursor.getCount()];

        i = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Cursor cursor1 = trainDatabase.rawQuery("SELECT * FROM trainline WHERE  _id=?", new String[]{String.valueOf(cursor.getInt(0))});
                if (cursor1.moveToFirst()) {
                    Cursor c = trainDatabase.rawQuery("SELECT StationName FROM Station WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(2))});
                    c.moveToFirst();
                    startpoint[i] = c.getString(0);
                    c.close();
                    c = trainDatabase.rawQuery("SELECT StationName FROM Station WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(3))});
                    c.moveToFirst();
                    endpoint[i] = c.getString(0);
                    c.close();

                    starttime[i] = "0";
                    distance[i] = 0;
                    price[i] = 0;
                    train_id[i] = cursor1.getInt(0);
                }
                cursor1.close();
                cursor.moveToNext();
                i++;
            }
        }

        cursor.close();

        CartviewAdapter trainadapter = new CartviewAdapter(startpoint, starttime, endpoint, endtime, distance, price);
        trainRecyclerView.setAdapter(trainadapter);

        layoutManager = new LinearLayoutManager(getActivity());
        trainRecyclerView.setLayoutManager(layoutManager);

        //public bus cairo fav
        cursor = db.rawQuery("SELECT tableid FROM favourite Where type=? ", new String[]{"public_cairo"});
        startpoint = new String[cursor.getCount()];
        starttime = new String[cursor.getCount()];
        endpoint = new String[cursor.getCount()];
        endtime = new int[cursor.getCount()];
        distance = new int[cursor.getCount()];
        price = new int[cursor.getCount()];
        public_cairo_id = new int[cursor.getCount()];

        i = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Cursor cursor1 = publiccairoDatabase.rawQuery("SELECT * FROM line WHERE  id=?", new String[]{String.valueOf(cursor.getInt(0))});
                if (cursor1.moveToFirst()) {
                    Cursor c = publiccairoDatabase.rawQuery("SELECT Name FROM area WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(2))});
                    c.moveToFirst();
                    startpoint[i] = c.getString(0);
                    c.close();
                    c = publiccairoDatabase.rawQuery("SELECT Name FROM area WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(3))});
                    c.moveToFirst();
                    endpoint[i] = c.getString(0);
                    c.close();

                    starttime[i] = "0";
                    distance[i] = 0;
                    price[i] = 0;
                    public_cairo_id[i] = cursor1.getInt(0);
                }
                cursor1.close();
                cursor.moveToNext();
                i++;
            }
        }

        cursor.close();

        CartviewAdapter publicCairoadapter = new CartviewAdapter(startpoint, starttime, endpoint, endtime, distance, price);
        publiccairoRecyclerView.setAdapter(publicCairoadapter);

        layoutManager = new LinearLayoutManager(getActivity());
        publiccairoRecyclerView.setLayoutManager(layoutManager);

        //public bus cairo fav
        cursor = db.rawQuery("SELECT tableid FROM favourite Where type=? ", new String[]{"public_transport"});
        startpoint = new String[cursor.getCount()];
        starttime = new String[cursor.getCount()];
        endpoint = new String[cursor.getCount()];
        endtime = new int[cursor.getCount()];
        distance = new int[cursor.getCount()];
        price = new int[cursor.getCount()];
        public_transport_id = new int[cursor.getCount()];

        i = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Cursor cursor1 = publictransportDatabase.rawQuery("SELECT * FROM line WHERE  _id=?", new String[]{String.valueOf(cursor.getInt(0))});
                if (cursor1.moveToFirst()) {
                    Cursor c = publictransportDatabase.rawQuery("SELECT StationName FROM Station WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(1))});
                    c.moveToFirst();
                    startpoint[i] = c.getString(0);
                    c.close();
                    c = publictransportDatabase.rawQuery("SELECT StationName FROM Station WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(2))});
                    c.moveToFirst();
                    endpoint[i] = c.getString(0);
                    c.close();

                    starttime[i] = "0";
                    distance[i] = 0;
                    price[i] = 0;
                    public_transport_id[i] = cursor1.getInt(0);
                }
                cursor1.close();
                cursor.moveToNext();
                i++;
            }
        }

        cursor.close();

        CartviewAdapter publicTransportadapter = new CartviewAdapter(startpoint, starttime, endpoint, endtime, distance, price);
        publictransportRecyclerView.setAdapter(publicTransportadapter);

        layoutManager = new LinearLayoutManager(getActivity());
        publictransportRecyclerView.setLayoutManager(layoutManager);

        jetAdapter.setListener(new CartviewAdapter.Listener() {
            @Override
            public void onClick(int postion) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", (int) jet_id[postion]);
                Log.w("id", String.valueOf(jet_id[postion]));
                intent.putExtra("type", "jet");

                getActivity().startActivity(intent);

            }
        });

        trainadapter.setListener(new CartviewAdapter.Listener() {
            @Override
            public void onClick(int postion) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", (int) train_id[postion]);
                Log.w("id", String.valueOf(train_id[postion]));
                intent.putExtra("type", "train");

                getActivity().startActivity(intent);

            }
        });

        publicCairoadapter.setListener(new CartviewAdapter.Listener() {
            @Override
            public void onClick(int postion) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", (int) public_cairo_id[postion]);
                Log.w("id", String.valueOf(public_cairo_id[postion]));
                intent.putExtra("type", "public_cairo");

                getActivity().startActivity(intent);

            }
        });

        publicTransportadapter.setListener(new CartviewAdapter.Listener() {
            @Override
            public void onClick(int postion) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", (int) public_transport_id[postion]);
                Log.w("id", String.valueOf(public_transport_id[postion]));
                intent.putExtra("type", "public_transport");

                getActivity().startActivity(intent);

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        LinearLayout favlayout = RootView.findViewById(R.id.Favourites);
        final LinearLayout favcontrol = RootView.findViewById(R.id.FAV_details);
        final ImageView favSelect = RootView.findViewById(R.id.fav_select);


        favlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (favcontrol.getVisibility()) {
                    case View.VISIBLE:
                        favcontrol.setVisibility(View.GONE);
                        favSelect.setImageResource(R.drawable.down);
                        break;
                    case View.GONE:
                        favcontrol.setVisibility(View.VISIBLE);
                        favSelect.setImageResource(R.drawable.up);
                        break;
                }
            }
        });

        Button suggestionBtn = (Button) RootView.findViewById(R.id.add_suggestion);
        suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SuggestionActivity.class);
                getActivity().startActivity(intent);
            }
        });
        Button changeContactBtn = (Button) RootView.findViewById(R.id.contact_change);
        changeContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), clientid.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                getActivity().startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return RootView;
    }

}
