package com.kang.novel.ui.home.bbs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kang.novel.R;
import com.kang.novel.common.APPCONST;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.ui.bookinfo.BookInfoActivity;
import com.kang.novel.ui.read.ReadActivity;

import java.util.List;

/**
 * Created by Micky on 2018/12/3.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Book> mList;

    public ItemAdapter(Context context, List<Book> list) {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Book book = mList.get(position);
        Log.d("ItemAdapter", "getImgUrl = " + book.getImgUrl());
        holder.simpleDraweeView.setImageURI(book.getImgUrl());
        holder.textView.setText(book.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到阅读页面
//                Intent intent = new Intent(context, ReadActivity.class);
//                intent.putExtra(APPCONST.BOOK,book);
//                context.startActivity(intent);

                //跳转到详情页面
                Intent intent = new Intent(context, BookInfoActivity.class);
                intent.putExtra(APPCONST.BOOK, book);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView simpleDraweeView;
        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.item_cover);
            textView = itemView.findViewById(R.id.item_name);
        }
    }

    public void setData(List<Book> mList) {
        this.mList = mList;
    }
}
