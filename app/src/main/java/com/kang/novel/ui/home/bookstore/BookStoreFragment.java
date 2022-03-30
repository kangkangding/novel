package com.kang.novel.ui.home.bookstore;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import com.kang.novel.databinding.FragmentBookStoreBinding;

/**
 * 书架
 */
public class BookStoreFragment extends Fragment {

    private BookStorePresenter mBookStorePresenter;
    private FragmentBookStoreBinding binding;

    public BookStoreFragment() {
        mBookStorePresenter = new BookStorePresenter(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentBookStoreBinding.inflate(inflater,container,false);
        mBookStorePresenter.enable();
        mBookStorePresenter.init();

        return binding.getRoot();

    }

    public RecyclerView getRvTypeList() {
        return binding.rvTypeList;
    }

    public RecyclerView getRvBookList() {
        return binding.rvBookList;
    }

    public SmartRefreshLayout getSrlBookList() {
        return binding.srlBookList;
    }

    public FragmentBookStoreBinding getBinding() {
        return binding;
    }
}
