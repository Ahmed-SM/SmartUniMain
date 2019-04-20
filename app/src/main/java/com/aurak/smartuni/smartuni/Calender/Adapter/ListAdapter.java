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

import com.aurak.smartuni.smartuni.Calender.Item;
import com.aurak.smartuni.smartuni.HomeActivity;
import com.aurak.smartuni.smartuni.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;





public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Item> listItems;
    private Context context;
    private Dialog dialog;
    private ListAdapter.ViewHolder holderInstance;
    private RecyclerView recyclerView;
    private Item NoItem;



    public ListAdapter(List<Item> listItems, Context context, RecyclerView recyclerView) {
        setHasStableIds(true);

        this.recyclerView = recyclerView;
        this.listItems = listItems;
        if(listItems.isEmpty()) {
            NoItem = new Item(
                    "No Events",
                    " ",
                    "No Event"
            );
            listItems.add(NoItem);
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
    public void restoreItem(Item item) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String todayAsString = formatter.format(today);
        String tomorrowAsString = formatter.format(tomorrow);
        //Toast.makeText(this, todayAsString + "  :  "+ tomorrowAsString, Toast.LENGTH_LONG).show();

        if (listItems.get(0).id.equals("No Event")) {
            listItems.remove(0);
        }

        if (item.getTime().equals(todayAsString)) {
            listItems.add(item);
        } else if (item.getTime().equals(tomorrowAsString)) {
            listItems.add(item);

        } else {
            listItems.add(item);
        }

        //listItems.add(position,item);
        notifyItemInserted(listItems.indexOf(item));
        notifyItemRangeChanged(listItems.indexOf(item), listItems.size());

    }

    @Override
    public void onBindViewHolder(@NonNull final ListAdapter.ViewHolder holder, int position) {
        holderInstance = holder;

            Item item = listItems.get(position);
            holder.textViewHead.setText(item.getTime());
            holder.textViewDesc.setText(item.getDesc());
            holder.recyclerView=recyclerView;

        holder.eventpopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText taskName = dialog.findViewById(R.id.taskName);
                final EditText monthYearPop = dialog.findViewById(R.id.monthyearpop);
                Button editButton = dialog.findViewById(R.id.Editbutton);
                if (listItems.get(0).id.equals("No Event")) return;
                taskName.setText(" ");
                monthYearPop.setText(" ");

                //Toast.makeText(context, "Test Click" + String.valueOf(viewHold.getAdapterPosition()) + listItems.get(0).getDesc(), Toast.LENGTH_SHORT).show();

                dialog.show();

                taskName.setText(listItems.get(holder.getAdapterPosition()).getDesc());
                monthYearPop.setText(listItems.get(holder.getAdapterPosition()).getTime());

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HomeActivity)context).attemptToUpdate(listItems.get(holder.getAdapterPosition()).id,
                                monthYearPop.getText().toString(),
                                taskName.getText().toString());
                        dialog.dismiss();
                    }
                });
            }

        });

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
