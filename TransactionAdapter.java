package com.alterpat.expen;


import static com.alterpat.expen.MainActivity.setBalance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TViewHolder> {
    Context ctx;
    ArrayList<TransactionClass> transactionList;

    public TransactionAdapter(Context ctx, ArrayList<TransactionClass> transactionList) {
        this.ctx = ctx;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionAdapter.TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.transaction_row_layout,parent,false);
        return new TransactionAdapter.TViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TViewHolder holder, int position) {

        holder.tvMessage.setText(transactionList.get(holder.getAdapterPosition()).getMessage());

        if(transactionList.get(holder.getAdapterPosition()).isPositive())
        {
            holder.tvAmount.setTextColor(Color.parseColor("#00c853"));
            holder.tvAmount.setText("+$"+Integer.toString(transactionList.get(holder.getAdapterPosition()).getAmount()));
        }

        else {
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
            holder.tvAmount.setText("-$"+Integer.toString(transactionList.get(holder.getAdapterPosition()).getAmount()));
        }
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(ctx)
                        .setCancelable(false)
                        .setTitle("Click Cancel if you want to continue to delete this Transaction")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                transactionList.remove(holder.getAdapterPosition());
                                dialogInterface.dismiss();
                                notifyDataSetChanged();
                                MainActivity.checkIfEmpty(getItemCount());
                                setBalance(transactionList);
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TViewHolder extends RecyclerView.ViewHolder{
        TextView tvAmount,tvMessage;
        ImageView ivDelete;

        public TViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
