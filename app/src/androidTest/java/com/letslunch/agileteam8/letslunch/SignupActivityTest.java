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
public class SignupActivityTest {

    @Rule
    public ActivityTestRule<SignupActivity> mActivityTestRule = new ActivityTestRule<SignupActivity>(SignupActivity.class);
    private SignupActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
    mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_name(){
        View view_name = mActivity.findViewById(R.id.input_name);
        assertNotNull(view_name);
    }


    @Test
    public void input_email(){
        View view_email = mActivity.findViewById(R.id.input_email);
        assertNotNull(view_email);
    }


    @Test
    public void input_password(){
        View view_password = mActivity.findViewById(R.id.input_password);
        assertNotNull(view_password);
    }

    @After
    public void tearDown() throws Exception {
    mActivity = null;
    }

}