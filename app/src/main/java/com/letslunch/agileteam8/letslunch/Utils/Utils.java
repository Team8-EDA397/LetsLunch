package com.letslunch.agileteam8.letslunch.Utils;

import android.text.TextUtils;
/**
 * Created by Carl-Henrik Hult on 2017-05-08.
 */

public class Utils
{


    private Boolean isAllInfoAvailable(String groupName, String location, String time) {
        if(TextUtils.isEmpty(groupName) || groupName == null) {
            return false;
        } else if(TextUtils.isEmpty(location)) {
            return false;
        } else if(TextUtils.isEmpty(time)) {
            return false;
        }

        return true;
    }
}
