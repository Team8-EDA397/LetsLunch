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
public class SplashscreenTest {

    @Rule
    public ActivityTestRule<Splashscreen> mActivityTestRule = new ActivityTestRule<Splashscreen>(Splashscreen.class);
    private Splashscreen mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_splash(){
        View view_splash = mActivity.findViewById(R.id.splash);
        assertNotNull(view_splash);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}