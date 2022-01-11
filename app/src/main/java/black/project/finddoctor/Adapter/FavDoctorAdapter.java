package black.project.finddoctor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import black.project.finddoctor.DoctorProfileActivity;
import black.project.finddoctor.R;
import black.project.finddoctor.model.Doctor;

public class FavDoctorAdapter extends RecyclerView.Adapter<FavDoctorAdapter.FavDoctorViewHolder> {

     static Context mContext;
     static List<Doctor> mData;

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
        //holder.tv_title1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        //holder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition2));

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
        //ImageView picURL;
        LinearLayout container;

        public FavDoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.LL_doctor_card);
            //picURL = (TextView) itemView.findViewById(R.id.TV_date1);
            tv_name = (TextView) itemView.findViewById(R.id.TV_doctor_name);
            tv_speciality = (TextView) itemView.findViewById(R.id.TV_doctor_speciality);
            tv_fee = (TextView) itemView.findViewById(R.id.TV_Fee);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        //Toast.makeText(mContext,"You touched "+mData.get(pos).getDocName(),Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(mContext, DoctorProfileActivity.class);
                        intent.putExtra("doctorId", mData.get(pos).getId());
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}