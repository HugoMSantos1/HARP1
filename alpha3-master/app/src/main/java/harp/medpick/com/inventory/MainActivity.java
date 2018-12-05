package harp.medpick.com.inventory;

//import android.provider.productsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String NAME_KEY = "Name";
    private static final String BARCODE_KEY = "Barcode";
    private static final String PRICE_KEY = "Price";
    private static final String BUY_KEY = "Purchase Date";
    private static final String SUPPLIER_KEY = "Supplier";
    private static final String WARRANTY_KEY = "Warranty End Date";
    FirebaseFirestore db;
    TextView textDisplay;
    TextView message;

    EditText name, barcode, price, buydate, supplier, warrantydate;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        textDisplay = findViewById(R.id.textDisplay);
        message = findViewById(R.id.displayMessage);
        save    = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewProduct();
            }
        });

}

    private void addRealtimeUpdate() {
        DocumentReference productListener = db.collection("Harp").document();
        productListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null ){
                    Log.d("ERROR", e.getMessage());
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()){
                    message.setText(documentSnapshot.getData().toString());
                }
            }
        });
    }
    private void DeleteData() {

        db.collection("Harp").document()
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(MainActivity.this, "Data deleted !",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void UpdateData() {

        DocumentReference product = db.collection("Harp").document();
        product.update(NAME_KEY, "ProductName");
        product.update(BARCODE_KEY, "0123456790");
        product.update(PRICE_KEY, "2000");
        product.update(BUY_KEY, "12-11-2017");
        product.update(SUPPLIER_KEY, "MEDTRONIC");
        product.update(WARRANTY_KEY, "12-21-2018")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Updated Successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void ReadSingleproduct() {

        DocumentReference user = db.collection("Harp").document();
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    StringBuilder fields = new StringBuilder("");
                    //name, barcode, price, buydate, supplier, warrantydate
                    fields.append("Name: ").append(doc.get("Name"));
                    fields.append("\nBarcode: ").append(doc.get("Barcode"));
                    fields.append("\nPrice: ").append(doc.get("Price"));
                    fields.append("\nPurchase Date: ").append(doc.get("Purchase Date"));
                    fields.append("\nSupplier: ").append(doc.get("Supplier"));
                    fields.append("\nWarranty End Date: ").append(doc.get("Warranty End Date"));
                    message.setText(fields.toString());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    //name, barcode, price, buydate, supplier, warrantydate
    private void addNewProduct(){
        save    = findViewById(R.id.save);
        name    = findViewById(R.id.name);
        barcode   = findViewById(R.id.barcode);
        price   = findViewById(R.id.price);
        buydate    = findViewById(R.id.buydate);
        supplier    = findViewById(R.id.supplier);
        warrantydate    = findViewById(R.id.warrantydate);
        String mName = name.getText().toString();
        String mBarcode = barcode.getText().toString();
        String mPrice = price.getText().toString();
        String mBuydate = buydate.getText().toString();
        String mSupplier = supplier.getText().toString();
        String mWarrantydate = warrantydate.getText().toString();
        Map<String, Object>newProduct = new HashMap<>();
        newProduct.put(NAME_KEY, mName);
        newProduct.put(BARCODE_KEY, mBarcode);
        newProduct.put(PRICE_KEY, mPrice);
        newProduct.put(BUY_KEY, mBuydate);
        newProduct.put(SUPPLIER_KEY, mSupplier);
        newProduct.put(WARRANTY_KEY, mWarrantydate);
        db.collection("Harp").document().set(newProduct)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Product Registered",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "ERROR" +e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }
}
