package com.example.kleptomaniac.vitccuniversaldatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kleptomaniac on 6/7/17.
 */

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.MyViewHolder> {
    private List<SuggestionsClass> suggestionList,allItems;
    public Context context;
    public View view;
    public Context filterContext;


    public SuggestionsAdapter(Context context,List<SuggestionsClass> suggestionList)
    {
        this.allItems = suggestionList;
        this.suggestionList = new ArrayList<>();
        this.suggestionList.addAll(allItems);
        this.filterContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       this.context = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item,parent,false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final SuggestionsClass contentRequest = suggestionList.get(position);

        holder.itemName.setText(contentRequest.getItemName());

        holder.itemName.setClickable(true);
        holder.itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),contentRequest.getKeyCode(),Toast.LENGTH_SHORT).show();
                if(contentRequest.getKeyCode() == null)
                {

                    return;
                }
                Intent intent = new Intent(v.getContext(),ViewResponses.class);
                intent.putExtra("CONTENTKEY",contentRequest.getKeyCode());
                v.getContext().startActivity(intent);
            }


        });



    }

    @Override
    public int getItemCount() {
             return suggestionList.size();

    }

    public void filter(final String newText) {
        Log.e("VITCC","Adapter filter with text"+newText);
        Log.e("VITCC","All items Length"+allItems.size());
        Log.e("VITCC","suggestion items Length"+suggestionList.size());

        new Thread(new Runnable() {
            @Override
            public void run() {

                if(TextUtils.isEmpty(newText))
                {
                    suggestionList.clear();
                    Log.e("VITCC","Text is empty in filter");
                   SuggestionsClass prompt = new SuggestionsClass("Enter ? to view the entire list",null);
                    suggestionList.add(prompt);
                }
                else
                {

                    if(newText.equals("?"))
                    {
                        suggestionList.clear();
                     suggestionList.addAll(allItems);
                    }
                    else {
                        int suggestionCount = 0;
                        suggestionList.clear();
                        for (SuggestionsClass suggestion : allItems) {
                            if (suggestion.getItemName().toLowerCase().startsWith(newText.toLowerCase())) {
                                suggestionList.add(suggestion);
                                suggestionCount++;
                            }
                        }
                        if(suggestionCount == 0)
                        {
                            SuggestionsClass prompt = new SuggestionsClass("No results to your query",null);
                            suggestionList.add(prompt);
                        }
                    }
                }
                ((Activity)filterContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("VITCC","Notifying dataset Change");
                        notifyDataSetChanged();
                    }
                });
            }


        }).start();
    }

    private void notifyDataChanged() {


    }

    public void updateList(SuggestionsClass suggestion) {
       this.allItems.add(suggestion);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;

        public MyViewHolder(View itemView) {
            super(itemView);
           itemName = (TextView) itemView.findViewById(R.id.suggestion_item);



        }
    }


}
