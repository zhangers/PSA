package com.FTD.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.FTD.UI.Start;
import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ImageView;
import com.FTD.SQLite.PermissionRecord;
import com.FTD.SQLite.PermissionRecordDAO;
import com.FTD.draw.DisplayUtil;
import com.FTD.draw.PermissionModle;
import com.FTD.draw.myView;
import com.FTD.packageInfo.AppInfo;
import com.FTD.utils.SystemManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;import com.FTD.UI.R;

public class MainActivity extends Activity
        implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private long m_exitTime = 0;
    private LinearLayout main_layout, first_layout,
            second_layout, third_layout, loading_layout;
    private LinearLayout sumPicLayout = null;
    private LinearLayout sumPicLayout1 = null;
    private int bottom, left, right;
    private float scale;
    private int fiftyDip;
    private PackageManager pm = null;
    int[] pkgNums = null;
    private GestureDetector mGestureDetector;
    private List<PermissionModle> permissionModles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        setupViewComponet();

        loading_layout = (LinearLayout) findViewById(R.id.loading_LinearLayout);
        loading_layout.setVisibility(View.INVISIBLE);

        DisplayMetrics metric = new DisplayMetrics();
        metric = getResources().getDisplayMetrics();
        int ScreenWidth = metric.widthPixels;
        scale = metric.density;
        fiftyDip = DisplayUtil.dip2px(50f, scale);
        left = 0;
        right = ScreenWidth - DisplayUtil.dip2px(50f, scale) - 10;
        PermissionRecordDAO pr = new PermissionRecordDAO(this);
        pkgNums = pr.getAllPkgNum();
        permissionModles = toPermissionModle(pkgNums);
        bottom = DisplayUtil.dip2px(50f, scale) * pkgNums.length;
//        getRoot();
//        fileFunction.copyDB();
//        listAppInfo = queryFilterAppInfo();
        showdraw();


    }

    private void showdraw() {

        // 绘制图标
        for (int i = pkgNums.length - 1; i >= 0; i--) {
            ImageView appIcon = new ImageView(this);
            Drawable appImage = getIconByPkg(pkgNums[i], Start.static_listAppInfo);
            appIcon.setImageDrawable(appImage);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    fiftyDip, fiftyDip);
            sumPicLayout = (LinearLayout) findViewById(R.id.sumpic_layout);
            sumPicLayout.addView(appIcon, lp);
        }

        myView mView = new myView(this, permissionModles, bottom, left, right,
                scale);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        sumPicLayout1 = (LinearLayout) findViewById(R.id.sumpic_layout1);
        sumPicLayout1.addView(mView, lp1);
        mGestureDetector = new GestureDetector(this);
        mView.setOnTouchListener(this);
        mView.setFocusable(true);
        mView.setClickable(true);
        mView.setLongClickable(true);
        mGestureDetector.setIsLongpressEnabled(true);

    }

    public List<PermissionModle> toPermissionModle(int[] pkgNums) {
        List<PermissionModle> permissionModles = new ArrayList<PermissionModle>();
        for (int i = 0; i < pkgNums.length; i++) {
            PermissionModle permissionModle = new PermissionModle();
            PermissionRecord[] permissionRecords = getRecordArray_pkg(pkgNums[i]);
            for (int j = 0; j < permissionRecords.length; j++) {
                switch (permissionRecords[j].getType()) {
                    case 1:
                        permissionModle.setSendMsg(true);
                        permissionModle.setSendMsgTimestamp(permissionRecords[j].getTimestamp());
                        break;
                    case 8:
                        permissionModle.setReadCtc(true);
                        permissionModle.setReadCtcTimestamp(permissionRecords[j].getTimestamp());
                        break;
                    case 4:
                    	permissionModle.setReadMsg(true);
                    	permissionModle.setReadMsgTimestamp(permissionRecords[j].getTimestamp());
                    case 16:
                        permissionModle.setReadPhone(true);
                        permissionModle.setReadPhoneTimestamp(permissionRecords[j].getTimestamp());
                        break;
                    case 32:
                        permissionModle.setTrack(true);
                        permissionModle.setTrackTimestamp(permissionRecords[j].getTimestamp());
                        break;
                    case 64:
                        permissionModle.setIMEI(true);
                        permissionModle.setIMEITimestamp(permissionRecords[j].getTimestamp());
                        break;
                    case 512:
                        permissionModle.setRoot(true);
                        permissionModle.setRootTimestamp(permissionRecords[j].getTimestamp());
                        break;
                    case 1024:
                        permissionModle.setMonitorCall(true);
                        permissionModle.setMonitorCallTimestamp(permissionRecords[j].getTimestamp());
                        break;
                    default:
                        break;
                }
            }
            permissionModles.add(permissionModle);

        }
        return permissionModles;
    }

    public PermissionRecord[] getRecordArray_pkg(int pkg) {
        PermissionRecordDAO manageDAO = new PermissionRecordDAO(this);
        List<PermissionRecord> permissionRecords = manageDAO
                .getPermissionRecordOf_pkg(pkg);
        if (permissionRecords.size() == 0) {
            return null;
        } else {
            PermissionRecord pr[] = new PermissionRecord[1];
            PermissionRecord records[] = permissionRecords.toArray(pr);
            return records;
        }
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

    private Drawable getIconByPkg(int pkg, List<AppInfo> listAppInfo) {
        AppInfo appInfo = new AppInfo();
        for (int i = 0; i < listAppInfo.size(); i++) {
            appInfo = listAppInfo.get(i);
            if (appInfo.getUid() == pkg) {
                break;
            }
        }
        return appInfo.getAppIcon();
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

    private void setupViewComponet() {
        Intent intent = getIntent();
        boolean clickble = intent.getBooleanExtra("clickble", true);

        main_layout = (LinearLayout) findViewById(R.id.main_layout_ly);
        main_layout.setSelected(clickble);

        first_layout = (LinearLayout) findViewById(R.id.first_layout_ly);
        first_layout.setOnClickListener(clickListener_first);

        second_layout = (LinearLayout) findViewById(R.id.second_layout_ly);
        second_layout.setOnClickListener(clickListener_second);

        third_layout = (LinearLayout) findViewById(R.id.third_layout_ly);
        third_layout.setOnClickListener(clickListener_third);

    }

    public void getRoot() {
        String apkRoot = "chmod 777 " + getPackageCodePath();
        SystemManager.RootCommand(apkRoot);
    }

//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//		this.finish();
//	}

    private OnClickListener clickListener_first = new OnClickListener() {
        @Override
        public void onClick(View v) {
            main_layout.setSelected(true);
            first_layout.setSelected(false);
            second_layout.setSelected(false);
            third_layout.setSelected(false);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, First.class);
            intent.putExtra("clickble", true);
            MainActivity.this.finish();
            startActivity(intent);
            main_layout.setSelected(false);
        }
    };
    private OnClickListener clickListener_second = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            main_layout.setSelected(false);
            first_layout.setSelected(false);
            second_layout.setSelected(true);
            third_layout.setSelected(false);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Second.class);
            intent.putExtra("clickble", true);
            MainActivity.this.finish();
            startActivity(intent);
            first_layout.setSelected(false);
        }
    };
    private OnClickListener clickListener_third = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            main_layout.setSelected(false);
            first_layout.setSelected(false);
            second_layout.setSelected(false);
            third_layout.setSelected(true);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Third.class);
            intent.putExtra("clickble", true);
            MainActivity.this.finish();
            startActivity(intent);
            third_layout.setSelected(false);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - m_exitTime) > 2000) { // System.currentTimeMillis()无论何时调用，肯定大于2000
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                m_exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
        // Log.i("MyGesture", "onLongPress");
//        int getX = (int) e.getX();
//        int getY = (int) e.getY();
//        new AlertDialog.Builder(this).setTitle("权限")
//                .setMessage(getPermissionInfoByAxis(getX, getY))
//                .setPositiveButton("关闭", null).show();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
//        int getX = (int) e.getX();
//        int getY = (int) e.getY();
//        new AlertDialog.Builder(this).setTitle("权限")
//                .setMessage(getPermissionInfoByAxis(getX, getY))
//                .setPositiveButton("关闭", null).show();

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        int getX = (int) e.getX();
        int getY = (int) e.getY();
        new AlertDialog.Builder(this).setTitle("权限")
                .setMessage(getPermissionInfoByAxis(getX, getY))
                .setPositiveButton("关闭", null).show();
        return false;
    }

    public String getPermissionInfoByAxis(int x, int y) {
        PermissionModle permissionModle = new PermissionModle();
        StringBuilder sbInfo = new StringBuilder();
        String infoTime = null;
        String infoName = null;
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < pkgNums.length; i++) {
            if (y < fiftyDip * (pkgNums.length - i)
                    && y > fiftyDip * (pkgNums.length - i) - fiftyDip) {
                permissionModle = permissionModles.get(i);
                break;
            }
        }
        System.out.println(permissionModle.toString());
        int counter = permissionModle.getNumOfPermissions();
        if (counter == 0) {
            return "该应用没有获取任何权限";
        }
        int between = (right - left) / counter;
        int n = 0;
        for (int i = 0; i < counter; i++) {
            if (x > between * i && x < between * i + between) {
                n = i + 1;
                break;
            }
        }
        list = permissionModle.getPermissionNameByCounter(n, permissionModle);
        infoName = list.get(0).get("PermissionName");
        infoTime = list.get(1).get("time");
        sbInfo.append("此应用在");
        sbInfo.append(infoTime);
        sbInfo.append("访问");
        sbInfo.append(infoName);
        if (sbInfo.toString() == null) {
            return "该应用没有获取任何权限";
        }
        return sbInfo.toString();
    }
}
