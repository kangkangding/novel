package com.kang.novel.ui.read;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;


import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spreada.utils.chinese.ZHConverter;
import com.kang.novel.R;
import com.kang.novel.application.SysManager;
import com.kang.novel.callback.ResultCallback;

import com.kang.novel.custom.MyTextView;
import com.kang.novel.entity.Setting;
import com.kang.novel.enums.Font;
import com.kang.novel.enums.Language;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.greendao.entity.Chapter;
import com.kang.novel.greendao.service.BookService;
import com.kang.novel.greendao.service.ChapterService;
import com.kang.novel.util.StringHelper;
import com.kang.novel.webapi.CommonApi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReadContentAdapter extends RecyclerView.Adapter<ReadContentAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Chapter> mDatas;
    private OnClickItemListener mOnClickItemListener;
    private View.OnTouchListener mOnTouchListener;

    private ChapterService mChapterService;
    private BookService mBookService;
    private Setting mSetting;
    private Book mBook;
    private Typeface mTypeFace;
    private TextView curTextView;
    private int mResourceId;
    private Context mContext;
    private RecyclerView rvContent;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ViewHolder viewHolder = (ViewHolder) msg.obj;
                    viewHolder.tvErrorTips.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    ReadContentAdapter(Context context, int resourceId, ArrayList<Chapter> datas, Book book) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        mResourceId = resourceId;
        mChapterService = new ChapterService();
        mBookService = new BookService();
        mSetting = SysManager.getSetting();
        mBook = book;
        mContext = context;
        initFont();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View arg0) {
            super(arg0);
        }

        MyTextView tvTitle;
        MyTextView tvContent;
        TextView tvErrorTips;
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public Chapter getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * ??????ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (rvContent == null) rvContent = (RecyclerView) viewGroup;
        View view = mInflater.inflate(mResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvTitle = view.findViewById(R.id.tv_title);
        viewHolder.tvContent = view.findViewById(R.id.tv_content);
        viewHolder.tvErrorTips = view.findViewById(R.id.tv_loading_error_tips);
        return viewHolder;
    }



    /**
     * ?????????
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        initView(i, viewHolder);


        if (mOnTouchListener != null){
            viewHolder.itemView.setOnTouchListener(mOnTouchListener);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onClick(viewHolder.itemView, i);
            }
        });

    }

    private void initView(final int postion, final ViewHolder viewHolder) {
        final Chapter chapter = getItem(postion);

        viewHolder.tvContent.setTypeface(mTypeFace);
        viewHolder.tvTitle.setTypeface(mTypeFace);
        viewHolder.tvErrorTips.setVisibility(View.GONE);
        viewHolder.tvTitle.setText("???" + getLanguageContext(chapter.getTitle()) + "???");
        if (mSetting.isDayStyle()) {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(mSetting.getReadWordColor()));
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(mSetting.getReadWordColor()));
        } else {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.sys_night_word));
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.sys_night_word));
        }

        viewHolder.tvTitle.setTextSize(mSetting.getReadWordSize() + 2);
        viewHolder.tvContent.setTextSize(mSetting.getReadWordSize());
        viewHolder.tvErrorTips.setOnClickListener(view -> getChapterContent(chapter, viewHolder));
        if (StringHelper.isEmpty(chapter.getContent())) {
            getChapterContent(chapter, viewHolder);
        } else {
            viewHolder.tvContent.setText(getLanguageContext(chapter.getContent()));
        }


        curTextView = viewHolder.tvContent;

        preLoading(postion);

        lastLoading(postion);

    }




    public void notifyDataSetChangedBySetting() {
        mSetting = SysManager.getSetting();
        initFont();
        super.notifyDataSetChanged();
    }

    public TextView getCurTextView() {
        return curTextView;
    }

    private String getLanguageContext(String content) {
        if (mSetting.getLanguage() == Language.traditional && mSetting.getFont() == Font.????????????) {
            return ZHConverter.convert(content, ZHConverter.TRADITIONAL);
        }
        return content;

    }

    /**
     * ??????????????????
     *
     * @param chapter
     * @param viewHolder
     */
    private void getChapterContent(final Chapter chapter, final ViewHolder viewHolder) {
        if (viewHolder != null) {
            viewHolder.tvErrorTips.setVisibility(View.GONE);
        }
        Chapter cacheChapter = mChapterService.findChapterByBookIdAndTitle(chapter.getBookId(), chapter.getTitle());

        if (cacheChapter != null && !StringHelper.isEmpty(cacheChapter.getContent())) {
            chapter.setContent(cacheChapter.getContent());
            chapter.setId(cacheChapter.getId());
            if (viewHolder != null) {
                if (mSetting.getLanguage() == Language.traditional) {
                    viewHolder.tvContent.setText(ZHConverter.convert(chapter.getTitle(), ZHConverter.TRADITIONAL));
                } else {
                    viewHolder.tvContent.setText(chapter.getContent());
                }

                viewHolder.tvErrorTips.setVisibility(View.GONE);
            }
        } else {
            CommonApi.getChapterContent(chapter.getUrl(), new ResultCallback() {
                @Override
                public void onFinish(final Object o, int code) {
                    chapter.setContent((String) o);
                    mChapterService.saveOrUpdateChapter(chapter);
                    if (viewHolder != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.tvContent.setText(getLanguageContext((String) o));
                                viewHolder.tvErrorTips.setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (viewHolder != null) {
                        mHandler.sendMessage(mHandler.obtainMessage(1, viewHolder));
                    }
                }

            });
        }

    }


    /**
     * ??????????????????
     */
    private void preLoading(int position) {
        if (position + 1 < getItemCount()) {
            Chapter chapter = getItem(position + 1);
            if (StringHelper.isEmpty(chapter.getContent())) {
                getChapterContent(chapter, null);
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param position
     */
    private void lastLoading(int position) {
        if (position > 0) {
            Chapter chapter = getItem(position - 1);
            if (StringHelper.isEmpty(chapter.getContent())) {
                getChapterContent(chapter, null);
            }
        }
    }

    public void saveHistory(int position) {
        if (!StringHelper.isEmpty(mBook.getId())) {
            mBook.setHisttoryChapterNum(position);
            mBookService.updateEntity(mBook);
        }
    }

    private void initFont() {
        if (mSetting.getFont() == Font.????????????) {
            mTypeFace = null;
        } else {
            mTypeFace = Typeface.createFromAsset(mContext.getAssets(), mSetting.getFont().path);
        }
    }

    private void hiddenSoftInput(EditText editText){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setmOnClickItemListener(OnClickItemListener mOnClickItemListener) {
        this.mOnClickItemListener = mOnClickItemListener;
    }

    void setmOnTouchListener(View.OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }

    public interface OnClickItemListener {
        void onClick(View view, int positon);
    }


}
