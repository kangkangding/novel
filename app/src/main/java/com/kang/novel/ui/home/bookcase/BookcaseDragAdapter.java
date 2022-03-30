package com.kang.novel.ui.home.bookcase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.kang.novel.R;
import com.kang.novel.common.APPCONST;
import com.kang.novel.creator.DialogCreator;
import com.kang.novel.custom.DragAdapter;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.greendao.service.BookService;
import com.kang.novel.ui.read.ReadActivity;
import com.kang.novel.util.StringHelper;

import java.util.ArrayList;

public class BookcaseDragAdapter extends DragAdapter {
    private int mResourceId;
    private ArrayList<Book> list;
    private Context mContext;
    private boolean mEditState;
    private BookService mBookService;


    public BookcaseDragAdapter(Context context, int textViewResourceId, ArrayList<Book> objects, boolean editState) {
        mContext = context;
        mResourceId = textViewResourceId;
        list = objects;
        mEditState = editState;
        mBookService = new BookService();
    }

    @Override
    public void onDataModelMove(int from, int to) {
        Book b = list.remove(from);
        list.add(to, b);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSortCode(i);
        }
        mBookService.updateBooks(list);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Book getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getSortCode();
    }

    public void remove(Book item) {
        list.remove(item);
        notifyDataSetChanged();
        mBookService.deleteBook(item);
    }

    public void add(Book item) {
        list.add(item);
        notifyDataSetChanged();
        mBookService.addBook(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, null);
            viewHolder.ivBookImg =  convertView.findViewById(R.id.iv_book_img);
            viewHolder.tvBookName = convertView.findViewById(R.id.tv_book_name);
            viewHolder.tvNoReadNum =  convertView.findViewById(R.id.tv_no_read_num);
            viewHolder.ivDelete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(position, viewHolder);
        return convertView;
    }

    private void initView(int position, ViewHolder viewHolder) {
        final Book book = getItem(position);
        if (StringHelper.isEmpty(book.getImgUrl())) {
            book.setImgUrl("");
        }
        Glide.with(mContext)
                .load(book.getImgUrl())
                .error(R.mipmap.no_image)
                .placeholder(R.mipmap.no_image)
                .into(viewHolder.ivBookImg);

        viewHolder.tvBookName.setText(book.getName());
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreator.createCommonDialog(mContext, "删除书籍", "确定删除《" + book.getName() + "》及其所有缓存吗？",
                        true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove(book);
                                dialogInterface.dismiss();

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });


        if (mEditState) {
            viewHolder.tvNoReadNum.setVisibility(View.GONE);
            viewHolder.ivDelete.setVisibility(View.VISIBLE);
            viewHolder.ivBookImg.setOnClickListener(null);
        } else {
            viewHolder.ivDelete.setVisibility(View.GONE);
            if (book.getNoReadNum() != 0) {
                viewHolder.tvNoReadNum.setVisibility(View.VISIBLE);
                if (book.getNoReadNum() > 99) {
                    viewHolder.tvNoReadNum.setText("...");
                } else {
                    viewHolder.tvNoReadNum.setText(String.valueOf(book.getNoReadNum()));
                }
            } else {
                viewHolder.tvNoReadNum.setVisibility(View.GONE);
            }
            viewHolder.ivBookImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( mContext, ReadActivity.class);
                    intent.putExtra(APPCONST.BOOK, book);
                    book.setNoReadNum(0);
                    mBookService.updateEntity(book);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    public void setmEditState(boolean mEditState) {
        this.mEditState = mEditState;
        notifyDataSetChanged();
    }

    public boolean ismEditState() {
        return mEditState;
    }

    class ViewHolder {
        ImageView ivBookImg;
        TextView tvBookName;
        TextView tvNoReadNum;
        ImageView ivDelete;
    }

}
