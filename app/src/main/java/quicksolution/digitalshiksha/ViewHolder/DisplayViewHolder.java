package quicksolution.digitalshiksha.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import quicksolution.digitalshiksha.Interface.ItemClickListener;
import quicksolution.digitalshiksha.R;


public class DisplayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtProductName;
    public ImageView imageView;
    public ItemClickListener listner;

    public DisplayViewHolder(@NonNull View itemView)
    {
        super(itemView);

        imageView=(ImageView) itemView.findViewById(R.id.product_image);
        txtProductName=(TextView) itemView.findViewById(R.id.product_name);



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
