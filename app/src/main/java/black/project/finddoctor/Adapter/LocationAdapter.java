package black.project.finddoctor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import black.project.finddoctor.R;
import black.project.finddoctor.SearchActivity;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    static Context mContext;
    static List<String> mData;
    private static boolean online = false;

    public LocationAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View Layout;
        Layout = LayoutInflater.from(mContext).inflate(R.layout.card_location,parent,false);

        return new LocationViewHolder(Layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocationViewHolder holder, int position) {

        //adding animation
        holder.tv_loc.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition2));

        //decoration
        holder.tv_loc.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView tv_loc;
        LinearLayout container;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.LL_location_card);
            tv_loc = (TextView) itemView.findViewById(R.id.TV_locCard_location);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isOnline();
                    if(!online){
                        Toast.makeText(mContext, "You're Offline!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        //Toast.makeText(mContext,"You touched "+mData.get(pos),Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        intent.putExtra("type", "loc");
                        intent.putExtra("loc", mData.get(pos));
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            online=true;
            return true;
        } else {
            online=false;
            return false;
        }
    }
}
