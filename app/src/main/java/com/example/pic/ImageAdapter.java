package com.example.pic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> implements Filterable {

    private Context mContext;
    private List<Post> mPosts;
    private List<Post> mPostsFull;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
        mPostsFull = new ArrayList<>(posts);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Post postCurrent = mPosts.get(position);
        holder.textViewUsername.setText(postCurrent.getmUser());
        holder.textViewQuote.setText(postCurrent.getmQuote());
        //holder.textViewQuote.setText(postCurrent.getmImageUrl());
        //holder.imageView.setImageURI((postCurrent.getmImageUrl()));
        Picasso.get()
                .load(postCurrent.getmImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewUsername;
        public TextView textViewQuote;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewQuote = itemView.findViewById(R.id.card_quote);
            textViewUsername = itemView.findViewById(R.id.card_username);
            imageView = itemView.findViewById(R.id.card_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int position = getBindingAdapterPosition();//getAbsoluteAdapterPosition() ?????????
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);//???
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public Filter getFilter() {
        return postFilter;
    }

    private Filter postFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Post> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(mPostsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Post item:mPostsFull){
                    if(item.getmQuote().toLowerCase().contains(filterPattern) || item.getmUser().toLowerCase().contains(filterPattern) ){
                        filteredList.add(item);
                    }
                }
            }
           FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPosts.clear();
            mPosts.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
