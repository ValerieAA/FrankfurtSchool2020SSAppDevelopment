package de.fs.fintech.geogame;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {

    public static final int GRID_SPAN_COUNT = 2;

    public static final String STATE_IS_GRID_LAYOUT = "state_is_grid_layout";

    private RecyclerView recyclerOne;
    private Button listLayoutButton;
    private Button gridLayoutButton;

    private boolean isGridLayout = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        recyclerOne = findViewById(R.id.recycler_one);
        listLayoutButton = findViewById(R.id.btn_recycler_list);
        gridLayoutButton = findViewById(R.id.btn_recycler_grid);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ItemAdapter adapter = new ItemAdapter();
        adapter.setItems(Arrays.asList(
                new Person("Douglass Banker", 24, createDate(1996, 2, 13)),
                new Person("Deshawn Cusimano", 32, createDate(1989, 7, 25)),
                new Person("Kristle Barnette", 41, createDate(1980, 12, 13)),
                new Person("Dominga Friar", 20, createDate(1999, 8, 13)),
                new Person("Brian Olive", 30, createDate(1991, 4, 13)),
                new Person("Jorge Vidrine", 64, createDate(1957, 8, 13)),
                new Person("Magda Burg", 14, createDate(2007, 7, 13)),
                new Person("Kenya Bulluck", 18, createDate(2003, 11, 13)),
                new Person("Keri Rathjen", 29, createDate(1992, 10, 13)),
                new Person("Israel Hartt", 35, createDate(1985, 3, 13)),
                new Person("Jule Provenzano", 43, createDate(1978, 8, 13)),
                new Person("Irene Duwe", 30, createDate(1990, 1, 13)),
                new Person("Jacquelyn Hanshaw", 12, createDate(2008, 2, 13)),
                new Person("Elease Brauer", 75, createDate(1946, 9, 13)),
                new Person("Dorinda Ohern", 42, createDate(1979, 10, 13)),
                new Person("Odette Jensen", 32, createDate(1989, 12, 13)),
                new Person("Brunilda Deibler", 23, createDate(1998, 5, 13)),
                new Person("Maria Housley", 21, createDate(2000, 6, 13)),
                new Person("September Bibler", 27, createDate(1994, 6, 13)),
                new Person("Wendie Haase", 55, createDate(1966, 7, 13))
        ));
        recyclerOne.setAdapter(adapter);

        if(null == savedInstanceState){
        showListLayout(recyclerOne);
        } else {
            isGridLayout = savedInstanceState.getBoolean(STATE_IS_GRID_LAYOUT);
            if(isGridLayout){
                showGridLayout(recyclerOne);
            } else {
                showListLayout(recyclerOne);
            }
        }

        if (null != listLayoutButton) {
            listLayoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showListLayout(recyclerOne);
                }
            });
        }

        if (null != gridLayoutButton) {
            gridLayoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGridLayout(recyclerOne);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_GRID_LAYOUT, isGridLayout);
    }

    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

    private void showListLayout(RecyclerView recyclerView) {
        isGridLayout = false;
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void showGridLayout(RecyclerView recyclerView) {
        isGridLayout = true;
        GridLayoutManager layoutManager = new GridLayoutManager(
                this
                , GRID_SPAN_COUNT);
        recyclerView.setLayoutManager(layoutManager);
    }

    private static class Person {
        private final String name;
        private final int age;
        private final Date birthday;

        public Person(String name, int age, Date birthday) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Date getBirthday() {
            return birthday;
        }
    }

    private static class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private List<Person> items = new LinkedList<>();

        public void setItems(List<Person> newItems) {
            items.clear();
            if (null != newItems) {
                items.addAll(newItems);
            }

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ItemViewHolder(inflater.inflate(R.layout.list_item_person, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Person item = items.get(position);
            holder.bind(item);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView ageTextView;
        private final TextView birthdayTextView;
        private final java.text.DateFormat dateFormat;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.txt_name);
            this.ageTextView = itemView.findViewById(R.id.txt_age);
            this.birthdayTextView = itemView.findViewById(R.id.txt_birthday);
            this.dateFormat = DateFormat.getMediumDateFormat(itemView.getContext());
        }

        public void bind(Person value) {
            nameTextView.setText(value.getName());

            String ageString = ageTextView.getResources().getString(R.string.fmt_ages_1d, value.getAge());
            ageTextView.setText(ageString);

            Date birthday = value.getBirthday();
            if (null == birthday) {
                birthdayTextView.setText("");
            } else {
                String birthdayString = dateFormat.format(birthday);
                birthdayTextView.setText(birthdayString);
            }
        }
    }
}
