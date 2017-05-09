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
public class createGroupTest {
    @Rule
    public ActivityTestRule<createGroup> mActivityTestRule = new ActivityTestRule<createGroup>(createGroup.class);
    private createGroup mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_textView() {
        View view_textView = mActivity.findViewById(R.id.textViewHeaderCreateGroup);
        assertNotString(view_textView);
    }

    public void assertNotString(View view_textView) {
        //Not String
    }

    @Test
    public void input_groupName(){
        View view_groupName = mActivity.findViewById(R.id.editTextGroupName);
        assertNotNull(view_groupName);
    }


    @Test
    public void input_setLocation(){
        View view_setLocation = mActivity.findViewById(R.id.editTextSetLocation);
        assertNotNull(view_setLocation);
    }


    @Test
    public void input_setTime(){
        View view_setTime = mActivity.findViewById(R.id.editTextSetTime);
        assertNotNull(view_setTime);
    }

    @Test
    public void input_groupCreation(){
        View view_groupCreation = mActivity.findViewById(R.id.buttonGroupCreation);
        assertNotNull(view_groupCreation);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}