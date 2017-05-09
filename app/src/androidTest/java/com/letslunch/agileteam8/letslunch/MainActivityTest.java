package com.letslunch.agileteam8.letslunch;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by rsalem on 4/30/2017.
 */
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_login(){
        View view_login = mActivity.findViewById(R.id.login);
        assertNotNull(mActivity.findViewById(R.id.login));
    }

    @Test
    public void input_user(){
        View view_user = mActivity.findViewById(R.id.user);
        assertNotNull(mActivity.findViewById(R.id.user));
    }

    @Test
    public void input_pass(){
        View view_pass = mActivity.findViewById(R.id.pass);
        assertNotNull(mActivity.findViewById(R.id.pass));
    }

    @Test
    public void input_wholeLogo(){
        View view_wholeLogo = mActivity.findViewById(R.id.wholeLogo);
        assertNotNull(mActivity.findViewById(R.id.wholeLogo));
    }


    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}