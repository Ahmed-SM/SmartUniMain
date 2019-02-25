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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.Calender.Item;
import com.aurak.smartuni.smartuni.HomeActivity;
import com.aurak.smartuni.smartuni.R;

import java.util.List;




public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Item> listItems;
    private Context context;
    private Dialog dialog;
    private ListAdapter.ViewHolder holderInstance;
    private RecyclerView recyclerView;


    public ListAdapter(List<Item> listItems, Context context, RecyclerView recyclerView) {
        setHasStableIds(true);

        this.recyclerView = recyclerView;
        this.listItems = listItems;
        if(listItems.isEmpty()) {
            Item listItem = new Item(
                    "No Events",
                    " ",
                    "No Event"
            );
            listItems.add(listItem);
        }
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
                final EditText taskName = dialog.findViewById(R.id.taskName);
                final EditText monthYearPop = dialog.findViewById(R.id.monthyearpop);
                Button editButton = dialog.findViewById(R.id.Editbutton);
                taskName.setText("No event");
                monthYearPop.setText(" ");
                if (taskName.getText().equals("No event")) {
                    taskName.setText(listItems.get(viewHold.getAdapterPosition()+1).getDesc());
                    monthYearPop.setText(listItems.get(viewHold.getAdapterPosition()+1).getTime());
                }
                Toast.makeText(context, "Test Click" + String.valueOf(viewHold.getAdapterPosition()+1), Toast.LENGTH_SHORT).show();
                dialog.show();

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HomeActivity)context).attemptToUpdate(listItems.get(viewHold.getAdapterPosition()+1).id,
                                monthYearPop.getText().toString(),
                                taskName.getText().toString());
                    }
                });
            }

        });


        return new ViewHolder(v);

    }

    public void deletedEvent( int position) {
        if (position == 0 && !(listItems.size()>1)){
            if (!listItems.get(position).id.equals("No Event")) {
                Item item = new Item("No Events", " ", "No Event");
                listItems.set(position,item);
                return;
            }
        }else {
            listItems.remove(position);
            notifyItemRemoved(position);
        }
        notifyItemRangeChanged(position, listItems.size());

    }
    private void restoreItem(Item item, int position) {
        listItems.add(position,item);
        notifyItemInserted(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holderInstance = holder;

            Item item = listItems.get(position);
            holder.textViewHead.setText(item.getTime());
            holder.textViewDesc.setText(item.getDesc());
            holder.recyclerView=recyclerView;

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
        public RecyclerView recyclerView;
        public TextView textViewHead;
        public TextView textViewDesc;
        //public LinearLayout viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            eventpopup = itemView.findViewById(R.id.list_item);
            textViewHead = itemView.findViewById(R.id.textViewHead);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            RecyclerView recyclerView = getRecyclerView();
            //viewForeground = itemView.findViewById(R.id.view_foreground);

        }
    }

    public void updateData() {

        notifyDataSetChanged();
    }
    public List<Item> getListItems() {
        return listItems;
    }

    public void setListItems(List<Item> items) {
        this.listItems = items;
    }

    public void clear() {
        if (!this.listItems.isEmpty()) {
            final int size = listItems.size();
            listItems.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;

    }
}
