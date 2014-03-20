
package com.example.imagezoombar;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.RectF;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "NoBoringActionBarActivity";    
    private int mActionBarTitleColor;
    private int mActionBarHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private ListView mListView;
    private KenBurnsView mHeaderPicture;
    private ImageView mHeaderLogo;
    private View mHeader;
    private View mPlaceHolderView;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;    
    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();    
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;    
    private TypedValue mTypedValue = new TypedValue();   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();
        Log.e("antking_height", mMinHeaderTranslation+"");
        
        setContentView(R.layout.activity_main);
        
        mListView = (ListView)findViewById(R.id.listview);
        mHeader = findViewById(R.id.header);
        mHeaderPicture = (KenBurnsView)findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.picture0,R.drawable.picture1);
        mHeaderLogo = (ImageView)findViewById(R.id.header_logo);
        
        mActionBarTitleColor = getResources().getColor(R.color.actionbar_title_color);
        
        mSpannableString = new SpannableString(getString(R.string.noboringactionbar_title));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
        
        setupActionBar();
        setupListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void setupListView(){
        ArrayList<String> FAKES = new ArrayList<String>();
        for(int i=0;i<1000;i++){
            FAKES.add("entry"+i);
        }
        mPlaceHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView,false);
        mListView.addHeaderView(mPlaceHolderView);
        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,FAKES));
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                // TODO Auto-generated method stub
                int scrollY = getScrollY();
                Log.e("antking-scrollY", Math.max(-scrollY, mMinHeaderTranslation)+"");
                
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                
                float ratio = clamp(mHeader.getTranslationY()/mMinHeaderTranslation,0.0f,1.0f);
                interpolate(mHeaderLogo,getActionBarIconView(),mSmoothInterpolator.getInterpolation(ratio));
                
                setTitleAlpha(clamp(5.0F*ratio-4.0F,0.0F,1.0F));
            }
        });
            
        
    }
    @SuppressLint("NewApi")
    private void setTitleAlpha(float alpha){
        
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(mSpannableString);
        
    }
    
    public static float clamp(float value,float max,float min){
        return Math.max(Math.min(value, min), max);
    }
    
    private void interpolate(View view1,View view2,float interpolation){
        getOnScreenRect(mRect1,view1);
        getOnScreenRect(mRect2,view2);
        
        float scaleX = 1.0F + interpolation *(mRect2.width()/mRect1.width()-1.0F);
        float scaleY = 1.0F + interpolation *(mRect2.height()/mRect1.height()-1.0F);
        float translationX = 0.5F *(interpolation *(mRect2.left+mRect2.right-mRect1.left - mRect1.right));
        float translationY = 0.5F *(interpolation *(mRect2.top+mRect2.bottom-mRect1.top - mRect1.bottom));
        
        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }
    private RectF getOnScreenRect(RectF rect,View view){
        rect.set(view.getLeft(),view.getTop(),view.getRight(),view.getBottom());
        return rect;
    }
    public int getScrollY(){
        View c = mListView.getChildAt(0);
        if(c==null){
            return 0;
        }
        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();
        
        int headerHeight =0;
        if(firstVisiblePosition >=1){
            headerHeight = mPlaceHolderView.getHeight();
        }
        return -top + firstVisiblePosition * c.getHeight() +headerHeight;
    }
    @SuppressLint("NewApi")
    private void setupActionBar(){
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.ic_transparent);
    }
    private ImageView getActionBarIconView(){
        return (ImageView)findViewById(android.R.id.home);
    }
    public int getActionBarHeight(){
        if(mActionBarHeight !=0){
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        Log.e("antking-actionbar", mActionBarHeight+"");
        return mActionBarHeight;
    }

}
