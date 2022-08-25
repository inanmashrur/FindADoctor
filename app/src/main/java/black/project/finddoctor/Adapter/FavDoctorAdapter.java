package black.project.finddoctor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import black.project.finddoctor.DoctorProfileActivity;
import black.project.finddoctor.R;
import black.project.finddoctor.model.Doctor;

public class FavDoctorAdapter extends RecyclerView.Adapter<FavDoctorAdapter.FavDoctorViewHolder> {

     static Context mContext;
     static List<Doctor> mData;
    private static boolean online = false;

    public FavDoctorAdapter(Context mContext, List<Doctor> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public FavDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View Layout;
        Layout = LayoutInflater.from(mContext).inflate(R.layout.card_doctor,parent,false);

        return new FavDoctorViewHolder(Layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavDoctorViewHolder holder, int position) {

        //adding animation
        holder.tv_name.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.tv_speciality.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.tv_fee.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition2));
        holder.picURL.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));

        //decoration
        holder.tv_name.setText(mData.get(position).getDocName());
        holder.tv_speciality.setText(mData.get(position).getSpeciality());
        holder.tv_fee.setText(mData.get(position).getFee());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class FavDoctorViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_speciality, tv_fee;
        ImageView picURL;
        LinearLayout container;

        public FavDoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.LL_doctor_card);
            picURL = (ImageView) itemView.findViewById(R.id.IV_doc_pic);
            tv_name = (TextView) itemView.findViewById(R.id.TV_doctor_name);
            tv_speciality = (TextView) itemView.findViewById(R.id.TV_doctor_speciality);
            tv_fee = (TextView) itemView.findViewById(R.id.TV_Fee);

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
                        Intent intent = new Intent(mContext, DoctorProfileActivity.class);
                        intent.putExtra("doctorId", mData.get(pos).getId());
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