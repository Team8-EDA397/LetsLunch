package com.letslunch.agileteam8.letslunch;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by miriam on 5/7/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Toast.class, Context.class, TextUtils.class, Intent.class})
public class CreateGroupTest {

    private createGroup myCreateGroup;
    private Toast mToast;

    public static final String TEST_LONG_ID = "123456789";
    public static final String TEST_NULL = null;
    public static final String TEST_EMPTY = "";
    public static final String TEST_ILLEGAL_CHARS = "?[/.]  ^ ";
    public static final String TEST_GOOD_NAME = "123";
    public static final String TEST_EXISTING_CODE = "test-fail";

    @Before
    public void setup(){
        //setup class to test
        myCreateGroup = new createGroup();

        //setup mocks
        mToast = PowerMockito.mock(Toast.class);


        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence a = (CharSequence) invocation.getArguments()[0];
                return !(a != null && a.length() > 0);
            }
        });

        PowerMockito.mockStatic(Toast.class);
        //noinspection WrongConstant
        when(Toast.makeText(Matchers.<SignupActivity>anyObject()
                , Mockito.anyString(), Mockito.anyShort())).thenReturn(mToast);

    }

    @Test
    public void testGroupCodeValidation() throws Exception {
        //get method to test
        String methodName = "generateId";
        Method method = myCreateGroup.getClass().getDeclaredMethod(methodName,String.class);
        method.setAccessible(true);

        assertThat("Test long id gets truncated",((String) method.invoke(myCreateGroup,TEST_LONG_ID)).length(), is(14));
        assertThat("Null code does not fail ",((String) method.invoke(myCreateGroup,TEST_NULL)).length(),is(5));
        assertThat("Emplty string results in just the code added",Pattern.compile("^-([0-9]{4})$").matcher((String) method.invoke(myCreateGroup,TEST_EMPTY)).find(),is(true));
        assertThat("Illegal char (.) is removed",((String) method.invoke(myCreateGroup,TEST_ILLEGAL_CHARS)).contains("."),is(false));
        assertThat("Remove Illegal Characters test(? AND ^ are legal)", ((String) method.invoke(myCreateGroup,TEST_ILLEGAL_CHARS)).length(), is(7));
        assertThat("Good group name is retained",((String) method.invoke(myCreateGroup,TEST_GOOD_NAME)),startsWith(TEST_GOOD_NAME+"-"));
        assertThat("Good group name is id test",((String) method.invoke(myCreateGroup,TEST_GOOD_NAME)).length(),is(TEST_GOOD_NAME.length()+5));
    }

}
