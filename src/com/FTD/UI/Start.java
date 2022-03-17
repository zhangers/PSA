package com.FTD.UI;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.FTD.activity.MainActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import com.FTD.packageInfo.AppInfo;
import com.FTD.utils.SystemManager;
import com.FTD.utils.fileFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Start extends Activity {
    public static List<AppInfo> static_listAppInfo;
    private PackageManager pm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        new Thread() {

            @Override
            public void run() {
                try {
                    getRoot();
                    fileFunction.copyDB();
                    static_listAppInfo = queryFilterAppInfo();
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gotoMainPage();
            }

        }.start();
    }

    private void gotoMainPage() {
        Intent intent = (new Intent(this, MainActivity.class));
        startActivity(intent);
        finish();
    }

    public void getRoot() {
        String apkRoot = "chmod 777 " + getPackageCodePath();
        SystemManager.RootCommand(apkRoot);
    }

    public List<AppInfo> queryFilterAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        appInfos.clear();
        for (ApplicationInfo app : listAppcations) {
            // 非系统程序
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                appInfos.add(getAppInfo(app));
            }
            // 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                appInfos.add(getAppInfo(app));
            }

        }
        return appInfos;
    }
    // 构造一个AppInfo对象 ，并赋值
    private AppInfo getAppInfo(ApplicationInfo app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        appInfo.setUid(app.uid);
        return appInfo;
    }
}