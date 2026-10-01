package com.example.smartpantrymanager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Holder> {
    public static class Row {
        final String title,subtitle;
        final Runnable action;
        public Row(String title,String subtitle,Runnable action) {
            this.title=title;
            this.subtitle=subtitle;
            this.action=action;
        }
    }
    private final List<Row> rows;
    public CardAdapter(List<Row> rows) {
        this.rows=rows;
    }
    public Holder onCreateViewHolder(ViewGroup parent,int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false));
    }
    public void onBindViewHolder(Holder holder,int position) {
        Row row=rows.get(position);
        holder.title.setText(row.title);
        holder.subtitle.setText(row.subtitle);
        holder.itemView.setOnClickListener(v -> row.action.run());
    }
    public int getItemCount() {
        return rows.size();
    }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView title,subtitle;
        Holder(View view) {
            super(view);
            title=view.findViewById(R.id.title);
            subtitle=view.findViewById(R.id.subtitle);
        }
    }
}
