package com.techupstudio.countus.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techupstudio.countus.Adapters.PersonsAdapter;
import com.techupstudio.countus.Helpers.CountusPreference;
import com.techupstudio.countus.Models.Person;
import com.techupstudio.countus.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_PERSON_REQUEST_CODE = 100;
    public static final int SETTINGS_REQUEST_CODE = 200;
    private RecyclerView personsRV;
    private PersonsAdapter personsAdapter;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = findViewById(R.id.add_person);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, NewPersonActivity.class), NEW_PERSON_REQUEST_CODE);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        personsRV = findViewById(R.id.personsRV);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showAddedPeople();
            }
        });

        showAddedPeople();
    }

    private void showAddedPeople() {
        personsAdapter = new PersonsAdapter(this, new ArrayList<Person>(), new PersonsAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, Person item, int position) {
                Intent intent = new Intent(MainActivity.this, PersonDetailsActivity.class);
                intent.putExtra("name", item.getLastName()+" "+item.getMiddleName()+" "+item.getFirstName());
                intent.putExtra("age", String.valueOf(item.getAge()));
                intent.putExtra("gender", item.getGender());
                intent.putExtra("notes", item.getProperties().get("notes").toString());
                intent.putExtra("phone", item.getPhone());
                intent.putExtra("address", item.getAddress());
                startActivity(intent);
            }
        });
        personsRV.setAdapter(personsAdapter);
        personsRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.getInstance().collection("Registered Persons")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("People")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    List<Person> people = task.getResult().toObjects(Person.class);
                    personsAdapter.removeAllPerson();
                    personsAdapter.addAllPerson(people);
                    personsAdapter.notifyDataSetChanged();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mActionMenuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(true);
        searchView = (SearchView) mActionMenuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchButtonOnSearchListener();
        return true;
    }


    private void searchButtonOnSearchListener(){

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                personsAdapter.resetSearch();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                personsAdapter.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                personsAdapter.search(newText);
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.statistics:
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(MainActivity.this, SortPreferenceActivity.class), SETTINGS_REQUEST_CODE);
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                ((CountusPreference) getApplicationContext()).rememberUser(false);
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
