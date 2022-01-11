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

import black.project.finddoctor.R;
import black.project.finddoctor.SearchActivity;

public class SpecialityAdapter extends RecyclerView.Adapter<SpecialityAdapter.SpecialityViewHolder> {

    static Context mContext;
    static List<String> mData;

    public SpecialityAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public SpecialityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View Layout;
        Layout = LayoutInflater.from(mContext).inflate(R.layout.card_speciality,parent,false);

        return new SpecialityViewHolder(Layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final SpecialityViewHolder holder, int position) {

        //adding animation
        //holder.tv_title1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
        //holder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition2));

        //decoration
        holder.tv_spec.setText(mData.get(position));
        //holder.tv_loc.setText("test");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class SpecialityViewHolder extends RecyclerView.ViewHolder {

        TextView tv_spec;
        LinearLayout container;

        public SpecialityViewHolder(@NonNull View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.LL_spec_card);
            tv_spec = (TextView) itemView.findViewById(R.id.TV_specCard_spec);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        //Toast.makeText(mContext,"You touched "+mData.get(pos),Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        intent.putExtra("type", "spec");
                        intent.putExtra("spec", mData.get(pos));
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
