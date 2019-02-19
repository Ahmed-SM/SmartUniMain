package com.aurak.smartuni.smartuni.Calender.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.Calender.ListItem;
import com.aurak.smartuni.smartuni.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private Dialog dialog;

    public ListAdapter(List<ListItem> listItems, Context context) {
        setHasStableIds(true);

        this.listItems = listItems;
        this.context = context;

    }


    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        final ListAdapter.ViewHolder viewHold = new ListAdapter.ViewHolder(v);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.event_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        viewHold.eventpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView taskName = dialog.findViewById(R.id.taskName);
                final TextView monthYearPop = dialog.findViewById(R.id.monthyearpop);
                taskName.setText("No event");
                monthYearPop.setText(" ");
                if (listItems.size()>0) {
                    taskName.setText(listItems.get(viewHold.getAdapterPosition()+1).getDesc());
                    monthYearPop.setText(listItems.get(viewHold.getAdapterPosition()+1).getTime().toString());
                }
                Toast.makeText(context, "Test Click" + String.valueOf(viewHold.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                dialog.show();


            }
        });


        return new ViewHolder(v);

    }

    public void deletedEvent(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
    }
    private void restoreItem(ListItem item,int position) {
        listItems.add(position,item);
        notifyItemInserted(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.textViewHead.setText(formatter.format(listItem.getTime()));
        holder.textViewDesc.setText(listItem.getDesc());

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout eventpopup;
        public TextView textViewHead;
        public TextView textViewDesc;
        //public LinearLayout viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            eventpopup = itemView.findViewById(R.id.list_item);
            textViewHead = itemView.findViewById(R.id.textViewHead);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            //viewForeground = itemView.findViewById(R.id.view_foreground);

        }
    }

    public void updateData() {

        notifyDataSetChanged();
    }
    public List<ListItem> getListItems() {
        return listItems;
    }

    public void setListItems(List<ListItem> listItems) {
        this.listItems = listItems;
    }

    public void clear() {
        if (listItems.size() > 0) {
            final int size = listItems.size();
            listItems.clear();
            notifyItemRangeRemoved(0, size);
        }

    }
}
