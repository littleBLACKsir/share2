package com.example.myapplication3.Utils;

public class StringUtils {
    public static boolean IsEmpty(String str)
    {
        if(str==null||str.length()<=0)
        {
            return true;
        }
        else {
            return false;
        }
    }
}
