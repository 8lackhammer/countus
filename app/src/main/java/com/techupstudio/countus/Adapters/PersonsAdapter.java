package com.techupstudio.countus.Adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techupstudio.countus.Helpers.Helper;
import com.techupstudio.countus.Models.Person;
import com.techupstudio.countus.R;

import com.google.firebase.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> {
    private OnItemClickListener listener;

    //TODO : fix select item on first selection / selection started
    //TODO : remove search icon on tab Profile
    //TODO : add date - month and year - groups in objects list

    private View rootView;
    private Context context;
    private boolean pinEnabled;
    private boolean undoEnabled;
    private boolean selectionEnabled;
    private List<Person> DATA;
    private List<Person> MAIN_DATA;
    private List<Person> SELECTED_ITEMS;

    public interface OnItemClickListener{
        void onClick(View view, Person item, int position);
    }

    public PersonsAdapter(Context context, List<Person> persons) {
        this.context = context;
        this.MAIN_DATA = persons;
        this.DATA = new ArrayList<>(MAIN_DATA);
        this.SELECTED_ITEMS = new ArrayList<>();
    }

    public PersonsAdapter(Context context, List<Person> persons, OnItemClickListener listener) {
        this.context = context;
        this.MAIN_DATA = persons;
        this.DATA = new ArrayList<>(MAIN_DATA);
        this.SELECTED_ITEMS = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_person, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Person person = DATA.get(position);
        String fullName = person.getLastName()+" "+person.getFirstName();
        holder.userName.setText(fullName);
        String age = ""+person.getAge()+" years";
        holder.userAge.setText(age);
        holder.userGender.setText(person.getGender());
        Helper.log(person.getProperties().get("timestamp").toString());
        holder.timeStamp.setText(getDate((Timestamp) person.getProperties().get("timestamp")));
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(v, person, position);
                }
            }
        });
    }

    private String getDate(Timestamp date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return fmt.format(date.getSeconds());
    }

    @Override
    public int getItemCount() {
        return this.DATA.size();
    }

    public List<Person> getDataSet(){
        return DATA;
    }

    public void addPerson(Person person) {
        DATA.add(person);
        MAIN_DATA.add(person);
        notifyItemInserted(DATA.size()-1);
    }

    public void removePerson(Person person) {
        int i = DATA.indexOf(person);
        DATA.remove(i);
        i = MAIN_DATA.indexOf(person);
        MAIN_DATA.remove(i);
        notifyItemRemoved(i);
    }

    public void removeAllPerson(){
        DATA = new ArrayList<>();
        MAIN_DATA = new ArrayList<>();
    }

    public void addAllPerson(List<Person> personList){
        DATA.addAll(personList);
        MAIN_DATA.addAll(personList);
    }

    public void resetSearch() {
        DATA = new ArrayList<>(MAIN_DATA);
        notifyDataSetChanged();
    }

    public void search(String key) {

        key = key.toLowerCase().trim();

        List<Person> persons = new ArrayList<>();

        for (Person person: DATA){
            try{
                int i = Integer.valueOf(key.trim());
                if (i == person.getAge()){
                    persons.add(person);
                }
                else{
                    if (person.getFirstName().toLowerCase().contains(key) || person.getLastName().toLowerCase().contains(key)
                            || person.getMiddleName().toLowerCase().contains(key)  || person.getPhone().toLowerCase().contains(key)
                            || person.getProperties().get("notes").toString().toLowerCase().contains(key)
                            || person.getGender().toLowerCase().contains(key)){
                        persons.add(person);
                    }
                }
            }
            catch (Exception e){
                if (person.getFirstName().toLowerCase().contains(key) || person.getLastName().toLowerCase().contains(key)
                        || person.getMiddleName().toLowerCase().contains(key)  || person.getPhone().toLowerCase().contains(key)
                        || person.getProperties().get("notes").toString().toLowerCase().contains(key)
                        || person.getGender().toLowerCase().contains(key)){
                    persons.add(person);
                }
            }
        }

        DATA = persons;
        notifyDataSetChanged();

        if (key.isEmpty())
            resetSearch();

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView circleImageView;
        public TextView userName, timeStamp, userAge, userGender;
        public CheckBox markedState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.person_image);
            userName = itemView.findViewById(R.id.person_name);
            timeStamp = itemView.findViewById(R.id.timestamp);
            userAge = itemView.findViewById(R.id.person_age);
            userGender = itemView.findViewById(R.id.gender);
        }
    }

    public Context getContext() { return context; }


}

