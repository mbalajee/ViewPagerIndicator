package learn.in.androidviewpagerindicaor;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by balajim on 5/28/17.
 */

public class ViewPagerIndicator extends LinearLayout {

    private final int SHAPE_CIRCLE = 0;
    private final int SHAPE_SQUARE = 1;

    private Context mContext;

    // Attributes from xml
    private int mColorSelected, mColorDeselected;

    // Indicator shape - default shape is circle
    private int mShape = SHAPE_CIRCLE;

    // LayoutParams for selected and deselected indicators
    private LayoutParams mParamsSelected, mParamsDeselected;

    // ViewPager to which Indicators are attached to
    private ViewPager mViewPager;

    // Listens to data changes from view pager adapter
    private DataSetObserver mObserverIndicatorCount;

    // Avoid adding a indicator when adapter is added to view pager initially
    private boolean mInitialChange = true;

    // Always the first indicator is selected initially
    private int mCurrentlySelectedIndicator = 0;

    // Used on Observer to find if item is added or removed
    private int mCurrentAdapterSize = 0;

    public ViewPagerIndicator(Context context) {
        super(context);
        this.mContext = context;
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PagerIndicator,
                0, 0);

        try {
            mColorSelected = a.getColor(R.styleable.PagerIndicator_colorSelected, Color.BLACK);
            mColorDeselected = a.getColor(R.styleable.PagerIndicator_colorDeselected, Color.GRAY);
            mShape = a.getInteger(R.styleable.PagerIndicator_shape, SHAPE_CIRCLE); // Defaults to Circle
            int sizeSelected = a.getDimensionPixelSize(R.styleable.PagerIndicator_sizeSelected, 16);
            int sizeDeselected = a.getDimensionPixelSize(R.styleable.PagerIndicator_sizeDeselected, 12);
            int gap = a.getDimensionPixelSize(R.styleable.PagerIndicator_gap, 8);

            mParamsSelected = new LinearLayout.LayoutParams(sizeSelected, sizeSelected);
            mParamsDeselected = new LinearLayout.LayoutParams(sizeDeselected, sizeDeselected);
            mParamsSelected.setMarginEnd(gap);
            mParamsDeselected.setMarginEnd(gap);

        } finally {
            a.recycle();
        }

        // Indicator views are always added horizontally
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void attachToViewPager(final ViewPager viewPager) {
        this.mViewPager = viewPager;

        // Change indicator when view pager changes its current item
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Add observer for adapter data changes
        mObserverIndicatorCount = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                // This method gets triggered for initial population of data in adapter
                // Don't add indicator during initial adapter
                if (!mInitialChange && mViewPager.getAdapter().getCount() > 0) {
                    if (mViewPager.getAdapter().getCount() > mCurrentAdapterSize) {
                        // Data added
                        addIndicator();
                    } else if (mViewPager.getAdapter().getCount() < mCurrentAdapterSize){
                        // Data removed
                        removeIndicator();
                    }
                } // Wait till the adapter is initially loaded
                else if (mViewPager.getAdapter().getCount() > 0) {
                    addIndicators(viewPager.getAdapter().getCount());
                    mInitialChange = false;
                }

                mCurrentAdapterSize = viewPager.getAdapter().getCount();
            }
        };

        // Register the observer. !!!Must be unregistered
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            mViewPager.getAdapter().registerDataSetObserver(mObserverIndicatorCount);
        }
    }

    private void addIndicators(int indicatorCount) {
        for (int i = 0; i < indicatorCount; i++) {
            createIndicator(i);
        }
    }

    // Adds a new Indicator at the end and selects it
    private void addIndicator() {
        createIndicator(mCurrentAdapterSize);
        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
    }

    // Removes a currently indicator from the list and selects a indicator at the given position
    private void removeIndicator() {

        int indicatorToRemove;

        if (mCurrentlySelectedIndicator == mCurrentAdapterSize - 1) {
            indicatorToRemove = mCurrentlySelectedIndicator - 1;
        } else {
            indicatorToRemove = mCurrentlySelectedIndicator + 1;
        }

        // Remove from view
        View view = getChildAt(indicatorToRemove);
        if (view != null) {
            removeView(view);
        }
    }

    // Creates a new Indicator with deselected configuration
    private void createIndicator(int tag) {

        LayoutParams params;
        int color;
        if (tag == mCurrentlySelectedIndicator) {
            params = mParamsSelected;
            color = mColorSelected;
        } else {
            params = mParamsDeselected;
            color = mColorDeselected;
        }

        // Create indicator view
        View indicator = new View(mContext);
        indicator.setTag(tag);

        // Set params
        indicator.setLayoutParams(params);

        // Set indicator color
        setColor(indicator, color);

        // Add it to the view
        addView(indicator);
    }

    private void setColor(View indicator, int color) {
        if (mShape == SHAPE_CIRCLE) {
            Drawable drawable = getResources().getDrawable(R.drawable.drawable_default_shape_circle);
            indicator.setBackground(drawable);
            ((GradientDrawable) drawable).setColor(color);
        } else {
            indicator.setBackgroundColor(color);
        }
    }

    private void selectIndicator(int position) {

        View selectIndicator = getChildAt(position);
        View deselectIndicator = getChildAt(mCurrentlySelectedIndicator);

        if (selectIndicator != null) {
            selectIndicator.setLayoutParams(mParamsSelected);
            setColor(selectIndicator, mColorSelected);
        }

        if (deselectIndicator != null) {
            deselectIndicator.setLayoutParams(mParamsDeselected);
            setColor(deselectIndicator, mColorDeselected);
        }

        mCurrentlySelectedIndicator = position;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mViewPager != null && mViewPager.getAdapter() != null) {
            mViewPager.getAdapter().unregisterDataSetObserver(mObserverIndicatorCount);
        }
    }
}
