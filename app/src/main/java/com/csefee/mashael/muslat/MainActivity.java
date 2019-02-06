package com.csefee.mashael.muslat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import database.PublicCairoDatabaseHelper;
import database.PublicTransportDataBaseHelper;
import database.SuperJetDatabaseHelper;
import database.TrainDatabaseHelper;
import fragments.DashboardFragment;
import fragments.HomeFragment;
import fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 112;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 111;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 110;
    HomeFragment homeFragment;
    FragmentTransaction ft;
    Spinner spinner;
    int select;
    CursorAdapter suggestionAdapter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    ImageView hideButton = findViewById(R.id.hidebutton);
                    hideButton.setImageResource(R.drawable.up);

                    LinearLayout searchContainer = findViewById(R.id.search_container);
                    searchContainer.setVisibility(View.VISIBLE);

                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.p_search, homeFragment); // f1_container is your FrameLayout container
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                    return true;

                case R.id.navigation_dashboard:
                    hideButton = findViewById(R.id.hidebutton);
                    hideButton.setImageResource(R.drawable.down);

                    searchContainer = findViewById(R.id.search_container);
                    searchContainer.setVisibility(View.GONE);

                    DashboardFragment dashFragment = new DashboardFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.p_search, dashFragment); // f1_container is your FrameLayout container
                    ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft1.commit();

                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /*spinner = findViewById(R.id.selection_spinner);
        spinner.setSelection(0);

        TextView note_lineText = findViewById(R.id.line_note);
        note_lineText.setVisibility(View.GONE);
*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Here, thisActivity is the current activity
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Need to some permission to be granted", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(this, "Need to some permission to be granted", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                Toast.makeText(this, "Need to some permission to be granted", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }  // Permission has already been granted


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        final ImageView hideButton = findViewById(R.id.hidebutton);
        final LinearLayout searchContainer = findViewById(R.id.search_container);

        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchContainer.getVisibility() == View.VISIBLE) {
                    searchContainer.setVisibility(View.GONE);
                    hideButton.setImageResource(R.drawable.down);

                } else {
                    searchContainer.setVisibility(View.VISIBLE);
                    hideButton.setImageResource(R.drawable.up);

                }

            }
        });

        spinner = findViewById(R.id.selection_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int x = spinner.getSelectedItemPosition();
                switch (x) {
                    case 0:
                        homeFragment = new HomeFragment();
                        homeFragment.setType(0);
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.p_search, homeFragment); // f1_container is your FrameLayout container
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.commit();
                        searchView(0);
                        break;


                    case (1):
                        homeFragment = new HomeFragment();
                        homeFragment.setType(1);
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.p_search, homeFragment); // f1_container is your FrameLayout container
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.commit();
                        searchView(1);
                        break;


                    case (2):
                        homeFragment = new HomeFragment();
                        homeFragment.setType(2);
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.p_search, homeFragment); // f1_container is your FrameLayout container
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.commit();
                        searchView(2);
                        break;

                    case (3):
                        homeFragment = new HomeFragment();
                        homeFragment.setType(3);
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.p_search, homeFragment); // f1_container is your FrameLayout container
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.commit();
                        searchView(3);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onStart() {
        super.onStart();

        spinner = findViewById(R.id.selection_spinner);
        final SearchView searchFrom = (SearchView) findViewById(R.id.FromSearchView);
        final SearchView searchto = (SearchView) findViewById(R.id.ToSearchView);
        Button searchbtn = findViewById(R.id.search);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchFrom.getQuery().toString().equals(searchto.getQuery().toString())) {
                    Toast.makeText(MainActivity.this, "Enter different places !!!",
                            Toast.LENGTH_LONG).show();

                } else if (searchFrom.getQuery().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Check from location !!!",
                            Toast.LENGTH_LONG).show();

                } else if (searchto.getQuery().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Check to location !!!",
                            Toast.LENGTH_LONG).show();

                } else {
                    SearchFragment searchFragment = new SearchFragment();
                    switch (spinner.getSelectedItemPosition()) {
                        case 0:
                            searchFragment.data(0, searchFrom.getQuery().toString(), searchto.getQuery().toString(), findViewById(android.R.id.content));
                            break;
                        case 1:
                            searchFragment.data(1, searchFrom.getQuery().toString(), (String) searchto.getQuery().toString(), findViewById(android.R.id.content));
                            break;
                        case 2:
                            searchFragment.data(2, searchFrom.getQuery().toString(), (String) searchto.getQuery().toString(), findViewById(android.R.id.content));
                            break;
                        case 3:
                            searchFragment.data(3, searchFrom.getQuery().toString(), (String) searchto.getQuery().toString(), findViewById(android.R.id.content));
                            break;
                    }
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.p_search, searchFragment); // f1_container is your FrameLayout container
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();


                }
            }
        });

    }

    void searchView(final int select) {

        this.select = select;

        switch (select) {
            case 0:
                suggestionAdapter = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        null,
                        new String[]{"city_name"},
                        new int[]{android.R.id.text1},
                        0);

                break;
            case 1:
                suggestionAdapter = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        null,
                        new String[]{"StationName"},
                        new int[]{android.R.id.text1},
                        0);
                break;
            case 2:
                suggestionAdapter = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        null,
                        new String[]{"name"},
                        new int[]{android.R.id.text1},
                        0);
                break;
            case 3:
                suggestionAdapter = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        null,
                        new String[]{"StationName"},
                        new int[]{android.R.id.text1},
                        0);
                break;


        }


        final SearchView searchFrom = (SearchView) findViewById(R.id.FromSearchView);
        searchFrom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor c = null;
                switch (select) {
                    case 0:
                        SQLiteOpenHelper superjetDatabaseHelper = new SuperJetDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = superjetDatabaseHelper.getReadableDatabase();
                        c = db.rawQuery("SELECT city_name,_id FROM city WHERE city_name LIKE ?", new String[]{"%" + newText + "%"});
                        break;

                    case 1:
                        SQLiteOpenHelper trainDatabaseHelper = new TrainDatabaseHelper(getApplicationContext());
                        db = trainDatabaseHelper.getReadableDatabase();
                        try {
                            c = db.rawQuery("SELECT StationName,_id FROM Station WHERE StationName LIKE ?", new String[]{"%" + newText + "%"});
                        } catch (Exception ignored) {

                        }
                        break;

                    case 2:
                        SQLiteOpenHelper publicCairoDatabaseHelper = new PublicCairoDatabaseHelper(getApplicationContext());
                        db = publicCairoDatabaseHelper.getReadableDatabase();
                        c = db.rawQuery("SELECT name,_id FROM area WHERE name LIKE ?", new String[]{"%" + newText + "%"});
                        break;
                    case 3:
                        SQLiteOpenHelper publicTransportDatabaseHelper = new PublicTransportDataBaseHelper(getApplicationContext());
                        db = publicTransportDatabaseHelper.getReadableDatabase();
                        c = db.rawQuery("SELECT StationName,_id FROM Station WHERE StationName LIKE ?", new String[]{"%" + newText + "%"});
                        break;

                }

                if (c.getCount() > 0) {
                    c.moveToFirst();
                    suggestionAdapter.changeCursor(c);
                    suggestionAdapter.notifyDataSetChanged();
                    return true;
                } else {
                    return false;
                }
            }
        });

        searchFrom.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchFrom.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(0);//2 is the index of col containing suggestion name.
                searchFrom.setQuery(suggestion, true);//setting suggestion
                return true;
            }
        });
        searchFrom.setSuggestionsAdapter(suggestionAdapter);

        final SearchView searchTo = (SearchView) findViewById(R.id.ToSearchView);
        searchTo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor c = null;
                switch (select) {
                    case 0:
                        SQLiteOpenHelper superjetDatabaseHelper = new SuperJetDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = superjetDatabaseHelper.getReadableDatabase();
                        c = db.rawQuery("SELECT city_name,_id FROM city WHERE city_name LIKE ?", new String[]{"%" + newText + "%"});
                        break;

                    case 1:
                        SQLiteOpenHelper trainDatabaseHelper = new TrainDatabaseHelper(getApplicationContext());
                        db = trainDatabaseHelper.getReadableDatabase();
                        try {
                            c = db.rawQuery("SELECT StationName,_id FROM Station WHERE StationName LIKE ?", new String[]{"%" + newText + "%"});
                        } catch (Exception ignored) {

                        }
                        break;

                    case 2:
                        SQLiteOpenHelper publicCairoDatabaseHelper = new PublicCairoDatabaseHelper(getApplicationContext());
                        db = publicCairoDatabaseHelper.getReadableDatabase();
                        c = db.rawQuery("SELECT name,_id FROM area WHERE name LIKE ?", new String[]{"%" + newText + "%"});
                        break;
                    case 3:
                        SQLiteOpenHelper publicTransportDatabaseHelper = new PublicTransportDataBaseHelper(getApplicationContext());
                        db = publicTransportDatabaseHelper.getReadableDatabase();
                        c = db.rawQuery("SELECT StationName,_id FROM Station WHERE StationName LIKE ?", new String[]{"%" + newText + "%"});
                        break;

                }

                if (c.getCount() > 0) {
                    c.moveToFirst();
                    suggestionAdapter.changeCursor(c);
                    suggestionAdapter.notifyDataSetChanged();
                    return true;
                } else {
                    return false;
                }
            }

        });
        searchTo.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchTo.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(0);//2 is the index of col containing suggestion name.
                searchTo.setQuery(suggestion, true);//setting suggestion
                return true;
            }
        });
        searchTo.setSuggestionsAdapter(suggestionAdapter);


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}


