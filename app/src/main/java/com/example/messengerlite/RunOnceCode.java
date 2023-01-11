package com.example.messengerlite;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class RunOnceCode extends Application
{
    public RunOnceCode()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Map config = new HashMap();
        config.put("cloud_name", "dk8hbcln1");
        MediaManager.init(this, config);
    }
}