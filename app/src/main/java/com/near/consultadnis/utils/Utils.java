package com.near.consultadnis.utils;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.near.consultadnis.R;

public abstract class Utils {

    public abstract static class var{
        public static String DATABASE_NAME = "PERSONAL";
        public static String WS = "https://rest.softdatamen.com/v1/d17ce74a532cf475f063bc88ebae3923/reniec/basico?dni=";
    }

    public abstract static class function{

        public static void showToastError( final String mensaje ) {
            showToast( mensaje, ContextCompat.getColor(com.blankj.utilcode.util.Utils.getApp(), R.color.red_700), Color.WHITE );
        }
        public static void showToastSuccess( final String mensaje ) {
            showToast( mensaje, ContextCompat.getColor(com.blankj.utilcode.util.Utils.getApp(), R.color.green_700), Color.WHITE );
        }
        public static void showToastInfo( final String mensaje ) {
            showToast( mensaje, ContextCompat.getColor(com.blankj.utilcode.util.Utils.getApp(), R.color.blue_700), Color.WHITE );
        }
        public static void showToast(final String mensaje, final int backgroundcolor, final int foregroundcolor){
            ToastUtils t = ToastUtils.getDefaultMaker();
            t.setBgColor( backgroundcolor );
            //t.setTextColor( ContextCompat.getColor( activity, R.color.green_700 ) );
            t.show(  new SpanUtils()
                    .append( mensaje ).setFontSize(14, true).setBold()
                    //.setBackgroundColor( ContextCompat.getColor( activity, R.color.colorTextWhite ) )
                    .setForegroundColor( foregroundcolor )
                    .create() );
        }

    }
}
