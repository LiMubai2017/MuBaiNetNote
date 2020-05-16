package com.example.to_do_list.view;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.to_do_list.R;
import com.example.to_do_list.model.Tag;
import com.example.to_do_list.presenter.MainPresenter;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

/**
 * Created by abc on 2017/11/16.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{
    private List<Tag> mTagList;
    ViewHolder holder;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ExpandableTextView tagContent;
        View tagView;
        TextView attachment;

        public ViewHolder(View itemView) {
            super(itemView);
            tagView=itemView;
            tagContent = (ExpandableTextView) tagView.findViewById(R.id.tag_content);
            attachment = (TextView) itemView.findViewById(R.id.tag_attachment);
        }
    }

    public TagAdapter(List<Tag> mTagList) {
        this.mTagList = mTagList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d("onCreatViewHolder","执行了");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item,parent,false);
        holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("onBindViewHolder","执行了");
        Log.d("List长度： ",mTagList.size()+"");
        String[] priority = {"低","较低","一般","较高","高"};
        Tag tag = mTagList.get(position);
        holder.tagContent.setText(tag.getContent());
        if(MainPresenter.rankMode == 1) holder.attachment.setText("Begin: "+tag.getYear_begin()+"-"+(tag.getMonth_begin()+1)+"-"+tag.getDay_begin());
        if(MainPresenter.rankMode == 2) holder.attachment.setText("End: "+tag.getYear_end()+"-"+(tag.getMonth_end()+1)+"-"+tag.getDay_end());
        if(MainPresenter.rankMode == 3) holder.attachment.setText("优先级: "+priority[tag.getPriority()]);

        holder.tagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),EditView.class);
                intent.putExtra("tagPosition",position);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTagList.size();
    }
}
