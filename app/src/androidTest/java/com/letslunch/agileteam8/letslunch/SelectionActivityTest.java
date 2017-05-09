package com.letslunch.agileteam8.letslunch;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by rsalem on 4/29/2017.
 */
public class SelectionActivityTest {

    @Rule
    public ActivityTestRule<SelectionActivity> mActivityTestRule = new ActivityTestRule<SelectionActivity>(SelectionActivity.class);
    private SelectionActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_imageViewLunchbox(){
        View view_imageViewLunchbox = mActivity.findViewById(R.id.lunchbox);
        assertNotNull(view_imageViewLunchbox);
    }

    @Test
    public void input_imageViewBuyLunch(){
        View view_imageViewBuyLunch = mActivity.findViewById(R.id.buylunch);
        assertNotNull(view_imageViewBuyLunch);
    }

    @Test
    public void input_buttonbl(){
        View view_buttonbl = mActivity.findViewById(R.id.buttonbl);
        assertNotNull(view_buttonbl);
    }


    @Test
    public void input_buttonlb(){
        View view_buttonlb = mActivity.findViewById(R.id.buttonlb);
        assertNotNull(view_buttonlb);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}