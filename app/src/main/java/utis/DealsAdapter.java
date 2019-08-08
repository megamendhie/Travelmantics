package utis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelmantics.AdminActivity;
import com.example.travelmantics.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealHolder> {
    private ArrayList<Deal> dealsList;
    private ChildEventListener childEventListener;
    private DatabaseReference databaseReference;
    private Activity activity;


    public DealsAdapter(Activity activity){
        this.activity = activity;
        dealsList = new ArrayList<Deal>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Deal deal = dataSnapshot.getValue(Deal.class);
                deal.setId(dataSnapshot.getKey());
                dealsList.add(deal);
                DealsAdapter.this.notifyItemInserted(dealsList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference = Firebaseutil.databaseReference;
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deal_item, viewGroup, false);
        return new DealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealHolder dealHolder, int position) {
        dealHolder.onBind(dealsList.get(position));
    }

    @Override
    public int getItemCount() {
        return dealsList.size();
    }

    public class DealHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private String TAG = "DealHolder";
        private TextView txtTitle, txtDescription, txtPrice;
        private ImageView imgDeal;
        private Deal deal;

        public DealHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgDeal = itemView.findViewById(R.id.imgDeal);
            itemView.setOnClickListener(this);

        }

        public void onBind(Deal model){
            deal=model;
            txtTitle.setText(model.getTitle());
            txtDescription.setText(model.getDescription());
            txtPrice.setText(String.format(Locale.getDefault(), "N%d", model.getPrice()));
            if(model.getImgUrl()!=null && !model.getImgUrl().isEmpty())
                Picasso.get().load(model.getImgUrl()).into(imgDeal);
        }

        public void displayFull(){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView;
            dialogView = inflater.inflate(R.layout.user_view, null);
            builder.setView(dialogView);
            final AlertDialog dialog= builder.create();
            dialog.show();
            TextView txtTitle = dialog.findViewById(R.id.txtTitle);
            TextView txtPrice = dialog.findViewById(R.id.txtPrice);
            TextView txtDescription = dialog.findViewById(R.id.txtDescription);
            ImageView imgDeal = dialogView.findViewById(R.id.imgDeal);

            txtTitle.setText(deal.getTitle());
            txtPrice.setText(String.format(Locale.getDefault(), "N%d", deal.getPrice()));
            txtDescription.setText(deal.getDescription());
            if(deal.getImgUrl()!=null && !deal.getImgUrl().isEmpty())
                Picasso.get().load(deal.getImgUrl()).into(imgDeal);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.i(TAG, "onClick: " + position);
            Deal selectedDeal = dealsList.get(position);

            Intent intent = new Intent(v.getContext(), AdminActivity.class);
            intent.putExtra("Deal", selectedDeal);
            if(Firebaseutil.isAdmin)
                v.getContext().startActivity(intent);
            else
                displayFull();
        }
    }
}
