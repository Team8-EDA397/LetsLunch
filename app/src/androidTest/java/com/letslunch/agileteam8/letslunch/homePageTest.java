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

//Before you run this test make sure you login, otherwise the test will fail.
public class homePageTest {

    @Rule
    public ActivityTestRule<homePage> mActivityTestRule = new ActivityTestRule<homePage>(homePage.class);
    private homePage mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void input_buttonJoinGroup(){
        View view_buttonJoinGroup = mActivity.findViewById(R.id.buttonJoinGroup);
        assertNotNull(mActivity.findViewById(R.id.buttonJoinGroup));
    }

    @Test
    public void input_logoutButton(){
        View view_logoutButton = mActivity.findViewById(R.id.logoutButton);
        assertNotNull(mActivity.findViewById(R.id.logoutButton));
    }

    @Test
    public void input_buttonCreateGroup(){
        View view_buttonCreateGroup = mActivity.findViewById(R.id.buttonCreateGroup);
        assertNotNull(mActivity.findViewById(R.id.buttonCreateGroup));
    }

    @Test
    public void input_existingGroups(){
        View view_existingGroups = mActivity.findViewById(R.id.existingGroups);
        assertNotNull(mActivity.findViewById(R.id.existingGroups));
    }

    @Test
    public void input_listViewGroups(){
        View view_listViewGroups = mActivity.findViewById(R.id.listViewGroups);
        assertNotNull(mActivity.findViewById(R.id.listViewGroups));
    }


    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}