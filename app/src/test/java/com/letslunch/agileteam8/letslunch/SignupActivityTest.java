package com.letslunch.agileteam8.letslunch;

/**
 * Created by miriam on 4/25/2017.
 */

 import android.content.Context;
 import android.content.Intent;
 import android.text.TextUtils;
 import android.widget.Toast;

 import com.letslunch.agileteam8.letslunch.Activities.SignupActivity;

 import org.mockito.stubbing.Answer;
 import org.powermock.api.mockito.PowerMockito;
 import org.powermock.core.classloader.annotations.PrepareForTest;
 import org.mockito.invocation.InvocationOnMock;
 import org.powermock.modules.junit4.PowerMockRunner;
 import org.mockito.Matchers;
 import org.mockito.Mockito;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import static org.junit.Assert.*;

 import java.lang.reflect.Method;

 import static org.mockito.Matchers.any;
 import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Toast.class, Context.class, TextUtils.class, Intent.class})
public class SignupActivityTest {

    private SignupActivity mySignup;
    private Toast mToast;

    public static final String TEST_NAME = "SomeName";
    public static final String TEST_EMAIL = "some@email.com";
    public static final String TEST_PASSWORD = "123";


    @Before
    public void setUp(){
        //setup class to test
        mySignup = new SignupActivity();

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
    public void isAllInfoProvidedTest() throws Exception{
        //get method to test
        String methodName = "isAllInfoProvided";
        Method method =mySignup.getClass().getDeclaredMethod(methodName,TEST_NAME.getClass(),TEST_EMAIL.getClass(),TEST_PASSWORD.getClass());
        method.setAccessible(true);

        //tests
        assertTrue((boolean) method.invoke(mySignup,TEST_NAME,TEST_EMAIL,TEST_PASSWORD));
        assertFalse((boolean) method.invoke(mySignup,null,TEST_EMAIL,TEST_PASSWORD));
        assertFalse((boolean) method.invoke(mySignup,TEST_NAME,null,TEST_PASSWORD));
        assertFalse((boolean) method.invoke(mySignup,TEST_NAME,TEST_EMAIL,null));


    }
}
