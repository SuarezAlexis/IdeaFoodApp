package com.jainjo.ideafood.log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.AdminIdeaFragment;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Idea;
import com.jainjo.ideafood.model.TipoIdea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogDayViewHolder extends RecyclerView.ViewHolder {
    private TextView dayOfWeek;
    private TextView dayOfMonth;
    private TextView month;
    private ViewPager2 viewPager;
    private Button logDayAddBtn;
    private IdeaViewModel ideaViewModel;

    public LogDayViewHolder(@NonNull final View itemView) {
        super(itemView);
        logDayAddBtn = (Button) itemView.findViewById(R.id.logDayAddBtn);
        dayOfWeek = (TextView) itemView.findViewById(R.id.dayOfWeek);
        dayOfMonth = (TextView) itemView.findViewById(R.id.dayOfMonth);
        month = (TextView) itemView.findViewById(R.id.month);
        viewPager = (ViewPager2) itemView.findViewById(R.id.logDayViewPager);
    }

    public void bindData(final LogViewModel viewModel, final MainActivity activity) {
        logDayAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                for(TipoIdea ti : viewModel.getViewPagerAdapter().getTiposIdea()) {
                    if(ti.getId() == viewPager.getAdapter().getItemId(viewPager.getCurrentItem())) {
                        idea.setTipo(ti);
                        break;
                    }
                }
                activity.updateFragment( AdminIdeaFragment.newInstance(viewModel.getDate(), viewModel.getViewPagerAdapter().getTiposIdea(),idea) );
            }
        });
        Calendar c = Calendar.getInstance();
        c.setTime(viewModel.getDate());
        dayOfWeek.setText( c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase() );
        dayOfMonth.setText( new SimpleDateFormat("dd").format(c.getTime()));
        month.setText( c.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()).toUpperCase() );
        Calendar today = Calendar.getInstance();
        if( today.get(Calendar.YEAR) == c.get(Calendar.YEAR) && today.get(Calendar.MONTH) == c.get(Calendar.MONTH)  && today.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH)) {
                this.itemView.setBackgroundColor(itemView.getResources().getColor(R.color.green_200));
        }
        viewPager.setAdapter( viewModel.getViewPagerAdapter() );
    }


}
