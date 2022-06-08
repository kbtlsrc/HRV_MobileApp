package com.example.hrvmobileapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hrvmobileapp.databinding.HomePageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomePage extends AppCompatActivity {
    private HomePageBinding binding_home;


    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;

    PointsGraphSeries<DataPoint> series;
    Dialog dialog;
    GraphView graphView;
    DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding_home = HomePageBinding.inflate(getLayoutInflater());
        View view = binding_home.getRoot();
        setContentView(view);
        //   value_string = new ArrayList<>();
        //   value_double= new ArrayList<>();


        firebaseDatabase = FirebaseDatabase.getInstance();

        //graph
        graphView = binding_home.graphId;
        series = new PointsGraphSeries<>();


        //popup
        dialog = new Dialog(this);



        for (int i = 0; i < 12999; i++) {
            reference = firebaseDatabase.getReference("hrvData")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(String.valueOf(i));

            showHrvData();
        }


        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
        graphView.getGridLabelRenderer().setNumVerticalLabels(5);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setYAxisBoundsManual(true); // Prevents auto-rescaling the Y-axis
        graphView.getViewport().setXAxisBoundsManual(true);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMinX(0);
        viewport.setScrollable(true);
        viewport.setScalable(true);
        series.setShape(PointsGraphSeries.Shape.RECTANGLE);
        graphView.getViewport().setScalableY(true);
        series.setColor(Color.rgb(117,53,173));
        graphView.getGridLabelRenderer().setTextSize(12f);



        binding_home.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                startActivity(intent);
            }
        });
    }//oncreate



    private void showHrvData(){
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HrvData value = snapshot.getValue(HrvData.class);
                try {
                    if (value != null) {
                        Date dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS")
                                .parse("01.01.2000 " + value.time);

                        double y = value.hrv;
                        series.appendData(new DataPoint(dt,y), true, 13000);

                    }
                } catch (ParseException e) {
                        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                            @Override
                            public String formatLabel(double value, boolean isValueX) {
                                if(isValueX){
                                    return sdf.format(dt);
                                }
                                return super.formatLabel(value, isValueX);
                            }
                        });


                        double thres = 1.3;
                        if(thres > y) {

                            dialog.setContentView(R.layout.pop_window);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        }
                    }//if
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }


                 //  long x = new Date().getTime();
                 //  long x = Long.parseLong(value.time);
                // series.appendData(new DataPoint(x,y), true, 13000 );

            }//ondatachange


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }//showuserdata


}