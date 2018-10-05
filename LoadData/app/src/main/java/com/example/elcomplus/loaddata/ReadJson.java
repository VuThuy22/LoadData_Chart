package com.example.elcomplus.loaddata;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class ReadJson {
    private Context context;

    public ReadJson(Context context) {
        this.context = context;
    }

    public static String  readJsonFromFile(Context context, String name) {
        String json=null;
        try{
            InputStream is=context.getAssets().open(name);
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            json=new String(buffer,"UTF-8");

        }catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
