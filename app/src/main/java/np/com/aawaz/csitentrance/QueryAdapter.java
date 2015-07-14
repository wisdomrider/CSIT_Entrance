package np.com.aawaz.csitentrance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> messages;
    ArrayList<Integer> flag;



    QueryAdapter(Context context,ArrayList<String> messages,ArrayList<Integer> flag){
        this.context=context;
        this.flag=flag;
        this.messages=messages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.query_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(flag.get(position)==0){
            holder.rightText.setText(messages.get(position));
            holder.leftImg.setVisibility(View.GONE);
        } else {
            holder.leftText.setText(messages.get(position));
            holder.leftImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void added(String text) {
        messages.add(text);
        flag.add(0);
        notifyItemInserted(messages.size());

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView leftImg;
        ImageView rightImg;
        TextView leftText;
        TextView rightText;
        public ViewHolder(View itemView) {
            super(itemView);
            leftImg= (ImageView) itemView.findViewById(R.id.leftImg);
            rightImg= (ImageView) itemView.findViewById(R.id.rightImg);
            rightText= (TextView) itemView.findViewById(R.id.rightText);
            leftText= (TextView) itemView.findViewById(R.id.leftText);
        }
    }


}