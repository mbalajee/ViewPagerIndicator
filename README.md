# ViewPagerIndicator
Android View pager position indicator with different built in shapes.
 

Usage:

Include this project as a module.  

// XML 

<com.company.androidviewpagerindicaor.ViewPagerIndicator
android:id="@+id/viewPagerIndicator"
android:layout_width="wrap_content" 
android:layout_height="wrap_content"
android:layout_gravity="bottom|end" 
app:colorSelected="@color/colorAccent"
app:colorDeselected="@color/colorAccentLight"
app:sizeSelected="10dp"
app:sizeDeselected="10dp"
app:gap="8dp"
app:shape="circle"
android:layout_marginBottom="8dp"/>


// Activity

ViewPagerIndicator pagerIndicator = findViewById(R.id.viewPagerIndicator);
pagerIndicator.attachToViewPager(viewPager);  //viewPager replace with your ViewPager
