package com.alterpat.expen;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView tvSign;
    public static TextView tvEmpty, tvBalance;
    EditText etAmount, etMessage;
    ImageView ivSend;
    boolean positive = true;
    RecyclerView rvTransactions;
    TransactionAdapter adapter;
    ArrayList<TransactionClass> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadData();
        setCustomActionBar();
        checkIfEmpty(transactionList.size());
        rvTransactions.setHasFixedSize(true);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this,transactionList);
        rvTransactions.setAdapter(adapter);

        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSign();
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etAmount.getText().toString().trim().isEmpty())
                {
                    etAmount.setError("Please Enter an Amount:");
                    return;
                }
                if(etMessage.getText().toString().isEmpty())
                {
                    etMessage.setError("Please Enter a description:");
                    return;
                }
                try {
                    int amt = Integer.parseInt(etAmount.getText().toString().trim());

                    sendTransaction(amt,etMessage.getText().toString().trim(),positive);
                    checkIfEmpty(transactionList.size());
                    setBalance(transactionList);
                    etAmount.setText("");
                    etMessage.setText("");
                }
                catch (Exception e){
                    etAmount.setError("Amount must be an integer greater than zero");
                }
            }
        });
    }
    private void setCustomActionBar() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View v = LayoutInflater.from(this).inflate(R.layout.custom_action_bar,null);
        tvBalance = v.findViewById(R.id.tvBalance);

        setBalance(transactionList);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setElevation(0);
    }

    public static void setBalance(ArrayList<TransactionClass> transactionList){
        int bal = calculateBalance(transactionList);
        if(bal<0)
        {
            tvBalance.setText("-$ "+calculateBalance(transactionList)*-1);
        }
        else {
            tvBalance.setText("+$ "+calculateBalance(transactionList));
        }
    }

    private void loadData() {
        SharedPreferences pref = getSharedPreferences("com.cs.ec",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("transactions",null);
        Type type = new TypeToken<ArrayList<TransactionClass>>(){}.getType();
        if(json!=null)
        {
            transactionList=gson.fromJson(json,type);
        }
    }

    private void sendTransaction(int amt,String msg, boolean positive) {
        transactionList.add(new TransactionClass(amt,msg,positive));
        adapter.notifyDataSetChanged();
        rvTransactions.smoothScrollToPosition(transactionList.size()-1);
    }

    private void changeSign() {
        if(positive)
        {
            tvSign.setText("-$");
            tvSign.setTextColor(Color.parseColor("#F44336"));
            positive = false;
        }
        else {
            tvSign.setText("+$");
            tvSign.setTextColor(Color.parseColor("#00c853"));
            positive = true;
        }
    }

    public static void checkIfEmpty(int size) {
        if (size == 0)
        {
            MainActivity.tvEmpty.setVisibility(View.VISIBLE);
        }
        else {
            MainActivity.tvEmpty.setVisibility(View.GONE);
        }
    }

    public static int calculateBalance(ArrayList<TransactionClass> transactionList)
    {
        int bal = 0;
        for(TransactionClass transaction : transactionList)
        {
            if(transaction.isPositive())
            {
                bal+=transaction.getAmount();
            }
            else {
                bal-=transaction.getAmount();
            }
        }
        return bal;
    }

    private void initViews() {
        transactionList = new ArrayList<TransactionClass>();
        tvSign = findViewById(R.id.tvSign);
        rvTransactions = findViewById(R.id.rvTransactions);
        etAmount = findViewById(R.id.etAmount);
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("com.cs.ec",MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(transactionList);
        editor.putString("transactions",json);
        editor.apply();
    }
}
