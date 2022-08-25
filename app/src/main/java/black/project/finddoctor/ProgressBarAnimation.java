package black.project.finddoctor;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

class ProgressBarAnimation extends Animation {
    Context context;
    ProgressBar progressBar;
    TextView textView;
    Float from, to;
    boolean loggedIn;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, Float from, Float to, boolean loggedIn) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
        this.loggedIn = loggedIn;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        float value = from + (to-from) * interpolatedTime ;
        progressBar.setProgress((int)value);
        textView.setText("Loading "+(int)value+"%");

        if( value==to ){
            if( loggedIn ) {
                context.startActivity(new Intent(context, HomeActivity.class));
            }
            else{
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        }
    }
}
