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
public class joinGroupTest {

    @Rule
    public ActivityTestRule<joinGroup> mActivityTestRule = new ActivityTestRule<joinGroup>(joinGroup.class);
    private joinGroup mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_textView() {
        View view_textView = mActivity.findViewById(R.id.textViewHeaderJoinGroup);
        assertNotString(view_textView);
    }

    public void assertNotString(View view_textView) {
        //Not String
    }

    @Test
    public void input_editTextGroupCode(){
        View view_editTextGroupCode = mActivity.findViewById(R.id.editTextGroupCode);
        assertNotNull(view_editTextGroupCode);
    }

    @Test
    public void input_buttonJoiningGroup(){
        View view_buttonJoiningGroup = mActivity.findViewById(R.id.buttonJoiningGroup);
        assertNotNull(view_buttonJoiningGroup);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}