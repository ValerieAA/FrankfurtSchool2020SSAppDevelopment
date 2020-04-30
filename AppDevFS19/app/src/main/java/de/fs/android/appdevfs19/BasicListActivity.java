package de.fs.android.appdevfs19;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.fs.android.appdevfs19.dummy.MyItemRecyclerContent;

public class BasicListActivity extends AppCompatActivity {
    private static final Logger log= LoggerFactory.getLogger(BasicListActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // see https://developer.android.com/guide/topics/ui/layout/recyclerview
        RecyclerView myRecyclerView = findViewById(R.id.my_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);

        ArrayList<MyItemRecyclerContent.DummyItem> items=new ArrayList<>();
        items.add(new MyItemRecyclerContent.DummyItem("1","c1","d1"));
        items.add(new MyItemRecyclerContent.DummyItem("2","c2","d2"));
        items.add(new MyItemRecyclerContent.DummyItem("3","c3","d3"));
        items.add(new MyItemRecyclerContent.DummyItem("4","c4","d4"));
        log.info("Yo we got "+items.size()+" items");

        MyItemRecyclerFragment.OnListFragmentInteractionListener listener= new MyItemRecyclerFragment.OnListFragmentInteractionListener() {
            public void onListFragmentInteraction(MyItemRecyclerContent.DummyItem item) {
                Toast.makeText(BasicListActivity.this,"Item clicked:"+item.details, Toast.LENGTH_LONG).show();
            }
        };
        myRecyclerView.setAdapter(new MyItemRecyclerViewAdapter(items,listener));


    }

}
