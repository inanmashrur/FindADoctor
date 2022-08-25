package black.project.finddoctor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

import black.project.finddoctor.DoctorProfileActivity;
import black.project.finddoctor.R;
import black.project.finddoctor.model.Doctor;

public class AppointDoctorAdapter extends RecyclerView.Adapter<AppointDoctorAdapter.DoctorViewHolder> {

    static Context mContext;
    static List< Map<String,Object> > mData;
    private static boolean online = false;

    public AppointDoctorAdapter(Context mContext, List< Map<String,Object> > mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View Layout;
        Layout = LayoutInflater.from(mContext).inflate(R.layout.card_apoint,parent,false);

        return new DoctorViewHolder(Layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final DoctorViewHolder holder, int position) {

        //adding animation
        holder.tv_name.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.tv_speciality.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.tv_date.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.tv_serial.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition2));

        //decoration
        Log.d("","Inside OnBind");
        holder.tv_name.setText(mData.get(position).get("docName").toString());
        holder.tv_speciality.setText(mData.get(position).get("speciality").toString());
        holder.tv_date.setText(mData.get(position).get("date").toString());
        holder.tv_serial.setText(mData.get(position).get("serial").toString());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_speciality, tv_date, tv_serial;
        LinearLayout container;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.LL_appoint_card);
            tv_name = (TextView) itemView.findViewById(R.id.TV_appC_doctor_name);
            tv_speciality = (TextView) itemView.findViewById(R.id.TV_appC_doctor_speciality);
            tv_date = (TextView) itemView.findViewById(R.id.TV_appC_date);
            tv_serial = (TextView) itemView.findViewById(R.id.TV_appC_serial);

            Log.d("State","Inside Holder");

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
                        Log.d("Touched: ",mData.get(pos).get("docId").toString());

                        //Toast.makeText(mContext,"You touched "+mData.get(pos).getDocName(),Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(mContext, DoctorProfileActivity.class);
                        intent.putExtra("doctorId", mData.get(pos).get("docId").toString());
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
