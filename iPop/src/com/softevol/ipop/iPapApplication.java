package com.softevol.ipop;

import android.app.Application;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "dGNCTWkyUXBKWkFzekVBRjVrODNOeWc6MQ")
public class iPapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
