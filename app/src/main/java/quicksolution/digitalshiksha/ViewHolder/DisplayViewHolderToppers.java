package quicksolution.digitalshiksha.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import quicksolution.digitalshiksha.Interface.ItemClickListener;
import quicksolution.digitalshiksha.R;

public class DisplayViewHolderToppers extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtToppersName,txtToppersDescription;
    public ImageView imageViewToppers;
    public ItemClickListener listner;

    public DisplayViewHolderToppers(@NonNull View itemView)
    {
        super(itemView);

        imageViewToppers=(ImageView) itemView.findViewById(R.id.topper_image);
        txtToppersName=(TextView) itemView.findViewById(R.id.topper_name);
        txtToppersDescription=(TextView) itemView.findViewById(R.id.about_topper);



    }
    public void setItemClickListner(ItemClickListener listner)
    {
        this.listner=listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view,getAdapterPosition(),false);

    }

}
