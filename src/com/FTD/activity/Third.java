package com.FTD.activity;

import com.FTD.UI.R;
import android.app.ActivityManager;
import android.content.Context;
import android.widget.*;
import com.FTD.service.FTDservice;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.FTD.utils.HttpDownloader;
import java.util.ArrayList;


public class Third extends Activity {
    private long m_exitTime = 0;
    private LinearLayout main_layout, first_layout, second_layout,
            third_layout;
    private Switch mSwitch;
    private Context mcontext;
    private LinearLayout downloadLineralayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.third);

        setupViewComponet();
        downloadLineralayout = (LinearLayout)findViewById(R.id.downloadLineralayout);
        downloadLineralayout.setOnClickListener(downloadListener);

        mSwitch = (Switch) findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener(switchCheckedChangeListener);
        mSwitch.setChecked(serviceIsWorking());
    }
    private OnClickListener downloadListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("求toast");
            HttpDownloader httpDownloader = new HttpDownloader();
            int result = httpDownloader.downFile("http://192.168.1.100/signature/signature.db", "FTDsignature/", "signature.db");
            System.out.println(result);
            if(result == 0)
            Toast.makeText(Third.this,"数据库更新T成功",Toast.LENGTH_SHORT).show();
        }
    };


    public void onFeedbackItemClick(View v){
        //建立Intent 对象
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        //设置文本格式
        emailIntent.setType("plain/text");
        //设置对方邮件地址
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"97321776@qq.com"});
        //设置标题内容
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "用户反馈");
        //设置邮件文本内容
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        //启动一个新的ACTIVITY,"Sending mail..."是在启动这个ACTIVITY的等待时间时所显示的文字
        startActivity(Intent.createChooser(emailIntent, "发送用户反馈..."));
    }

    public void onAboutBtnClick(View v) {
        this.startActivity(new Intent(this, AboutPage.class));
    }

    private OnCheckedChangeListener switchCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                Intent intent = new Intent();
                intent.setClass(Third.this, FTDservice.class);
                startService(intent);
                System.out.println("打开----------------");
            } else {
                Intent intent = new Intent();
                intent.setClass(Third.this, FTDservice.class);
                stopService(intent);
                System.out.println("关闭----------------");
            }
        }
    };

    private OnClickListener startServiceListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(Third.this, FTDservice.class);
            startService(intent);
        }
    };

    private OnClickListener stopServiceListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(Third.this, FTDservice.class);
            stopService(intent);
        }
    };

    private void setupViewComponet() {
        Intent intent = getIntent();
        boolean clickble = intent.getBooleanExtra("clickble", true);

        main_layout = (LinearLayout) findViewById(R.id.main_layout_ly);
        main_layout.setOnClickListener(clickListener_main);

        first_layout = (LinearLayout) findViewById(R.id.first_layout_ly);
        first_layout.setOnClickListener(clickListener_first);

        second_layout = (LinearLayout) findViewById(R.id.second_layout_ly);
        second_layout.setOnClickListener(clickListener_second);

        third_layout = (LinearLayout) findViewById(R.id.third_layout_ly);
        third_layout.setSelected(clickble);

        mcontext = this.getBaseContext();
    }

    // @Override
    // protected void onStop() {
    // // TODO Auto-generated method stub
    // super.onStop();
    // this.finish();
    // }

    private OnClickListener clickListener_first = new OnClickListener() {
        @Override
        public void onClick(View v) {
            main_layout.setSelected(false);
            first_layout.setSelected(true);
            second_layout.setSelected(false);
            third_layout.setSelected(false);
            Intent intent = new Intent();
            intent.setClass(Third.this, First.class);
            intent.putExtra("clickble", true);
            Third.this.finish();
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
            intent.setClass(Third.this, Second.class);
            intent.putExtra("clickble", true);
            Third.this.finish();
            startActivity(intent);
            first_layout.setSelected(false);
        }
    };
    private OnClickListener clickListener_main = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            main_layout.setSelected(true);
            first_layout.setSelected(false);
            second_layout.setSelected(false);
            third_layout.setSelected(false);
            Intent intent = new Intent();
            intent.setClass(Third.this, MainActivity.class);
            intent.putExtra("clickble", true);
            Third.this.finish();
            startActivity(intent);
            second_layout.setSelected(false);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - m_exitTime) > 2000) { // System.currentTimeMillis()无论何时调用，肯定大于2000
                Toast.makeText(Third.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                m_exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //本方法判断自己些的一个Service-->com.FTD.service.FTDservice是否已经运行
    public boolean serviceIsWorking()
    {
        ActivityManager myManager=(ActivityManager)mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for(int i = 0 ; i<runningService.size();i++)
        {
            if(runningService.get(i).service.getClassName().toString().equals("com.FTD.service.FTDservice"))
            {
                return true;
            }
        }
        return false;
    }


}
