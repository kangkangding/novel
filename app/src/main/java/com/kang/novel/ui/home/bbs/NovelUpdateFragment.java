package com.kang.novel.ui.home.bbs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kang.novel.R;
import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.URLCONST;
import com.kang.novel.entity.bookstore.BookType;
import com.kang.novel.enums.BookSource;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.util.TextHelper;
import com.kang.novel.webapi.BookStoreApi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: kangkang.ding
 * @date: 2022/3/4
 * @describe: 最热
 */
public class NovelUpdateFragment extends Fragment {

    private int mScreenWidth;
    private static final float MIN_SCALE = .95f;
    private static final float MAX_SCALE = 1.15f;
    private int mMinWidth;
    private int mMaxWidth;
    private ImageView mBgTop;
    private RecyclerView mBg;
    private ContentLoadingProgressBar cpb_loading;
    private RecyclerView mRecyclerView;
    private List<ItemBean> mList;
    private ItemAdapter mAdapter;
    private BgAdapter mBgAdapter;
    private Context context;
    private List<BookType> mBookTypes;
    private List<Book> bookList;
    private BookType curType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel_update, container, false);
        Context context = getContext();
        mBgTop = view.findViewById(R.id.bg_top);
        mBg = view.findViewById(R.id.bg);
        cpb_loading = view.findViewById(R.id.cpb_loading);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        cpb_loading.setVisibility(View.VISIBLE);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mMinWidth = (int) (mScreenWidth * 0.28f);
        mMaxWidth = mScreenWidth - 2 * mMinWidth;

        mList = new ArrayList<>();
        mAdapter = new ItemAdapter(context,bookList);
        mBgAdapter = new BgAdapter(context,bookList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置不可滑动
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context)
        {
            @Override
            public boolean canScrollHorizontally() {
                // 屏蔽滑动事件
                return false;
            }
        };
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBg.setLayoutManager(layoutManager2);
        mBg.setAdapter(mBgAdapter);
        PagerSnapHelper pagerSnapHelper2 = new PagerSnapHelper();
        pagerSnapHelper2.attachToRecyclerView(mBg);



        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GalleryItemDecoration(context));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper(){
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                mBg.scrollToPosition(targetPos);
                //   mBg.scrollTo(velocityX,velocityY);
                Log.d("PageSnap","位置："+targetPos);
                return targetPos;
            }
        };
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 移动bg
                Log.d("MICKY","移动的距离："+dx);
                final int childCount = recyclerView.getChildCount();
                Log.e("tag", childCount + "");
                for (int i = 0; i < childCount; i++) {
                    LinearLayout child = (LinearLayout) recyclerView.getChildAt(i);
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
                    // lp.rightMargin = 5;
                    // lp.height = 200;


                    int left = child.getLeft();
                    int right = mScreenWidth - child.getRight();
                    final float percent = left < 0 || right < 0 ? 0 : Math.min(left, right) * 1f / Math.max(left, right);
                    Log.d("Wumingtag", "percent = " + percent+";位置："+i);
                    float scaleFactor = MIN_SCALE + Math.abs(percent) * (MAX_SCALE - MIN_SCALE);
                    int width = (int) (mMinWidth + Math.abs(percent) * (mMaxWidth - mMinWidth));
                    // lp.width = width;
                    Log.d("Wumingtag", "scaleFactor = " + scaleFactor+";位置："+i);
                    child.setLayoutParams(lp);
                    child.setScaleY(scaleFactor);
                    child.setScaleX(scaleFactor);
                    // child.setBackground(getD);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        // 设置遮罩层
        mBgTop.setImageBitmap(PictureBlur.fastblur(BitmapFactory.decodeResource(getResources(), R.mipmap.bookshelf_card_mode_bg),30));
        // 设置遮罩层透明度
        mBgTop.setAlpha(0.7f);
        mBgTop.setVisibility(View.GONE);
        initData();

        return view;
    }

    public void initData() {
        BookStoreApi.getBookRank(URLCONST.method_tl_rank, BookSource.tianlai,new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                mBookTypes = (ArrayList<BookType>)o;
                curType = mBookTypes.get(0);
                bookList = mBookTypes.get(0).getBooks();
                mHandler.sendMessage(mHandler.obtainMessage(1));
            }

            @Override
            public void onError(Exception e) {
                TextHelper.showText(e.getMessage());

            }
        });
    }

    /**
     * 获取小说详情
     * @param position
     * @param book
     */
    private void getBookInfo(final int position, final Book book){

        //获取小说详情
        BookStoreApi.getBookInfo(book, new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                bookList.set(position,(Book) o);
                mHandler.sendEmptyMessage(2);

            }

            @Override
            public void onError(Exception e) {
                //防止网站进行反爬虫处理，进行重复获取
                getBookInfo(position,book);
                mHandler.sendEmptyMessage(2);
            }
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    cpb_loading.setVisibility(View.GONE);
                    if (bookList != null) {
                        for (int i = 0; i < bookList.size(); i++) {
                            getBookInfo(i, bookList.get(i));
                        }
                        refreshData();
                    }
                    break;
                case 2:
                    refreshData();
                    break;
            }
        }
    };

    private void refreshData() {
        mAdapter.setData(bookList);
        mAdapter.notifyDataSetChanged();

        mBgAdapter.setData(bookList);
        mBgAdapter.notifyDataSetChanged();
    }
}
