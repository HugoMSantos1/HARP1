package harp.medpick.com.inventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivityDash extends AppCompatActivity implements View.OnClickListener {

    Button Add_New, Repair_request, Retire_request, Support;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dash);


        Add_New = findViewById(R.id.Add_New);
        Repair_request = findViewById(R.id.Repair_request);
        Retire_request = findViewById(R.id.Retire_request);
        Support = findViewById(R.id.Support);

        Add_New.setOnClickListener(this);
        Repair_request.setOnClickListener(this);
        Retire_request.setOnClickListener(this);
        Support.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Add_New:
                Toast.makeText(this, "Moving to Add New (Inventory) page", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivityDash.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.Repair_request:
                Toast.makeText(this, "Moving to Repair Request (Warrenty) page", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(MainActivityDash.this,Retire_Product_Page.class);
                startActivity(intent1);
                break;
            case R.id.Retire_request:
                Toast.makeText(this, "Moving to Barcode Page", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MainActivityDash.this, BarcodeReader.class);
                startActivity(intent2);
                break;
            case R.id.Support:
                Toast.makeText(this, "Moving to assistance page", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(MainActivityDash.this, SupportActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
