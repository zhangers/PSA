package com.FTD.SQLite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PermissionRecordDAO {
    private DB_Se_OpenHelper helper;
    private SQLiteDatabase db;

    public PermissionRecordDAO(Context context) {
        helper = new DB_Se_OpenHelper(context);
    }

    public void add(PermissionRecord permissionrecord) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", permissionrecord.getTitle());
        values.put("content", permissionrecord.getContent());
        values.put("pkg", permissionrecord.getPkg());
        values.put("timestamp", permissionrecord.getTimestamp());
        values.put("id", permissionrecord.getId());
        values.put("action", permissionrecord.getType());
        values.put("type", permissionrecord.getType());
        db.insert("permissionrecord", "id", values);
    }

    public PermissionRecord find(int id) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"title", "content", "pkg", "timestamp", "id", "action", "type"}, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            return new PermissionRecord(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getLong(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
        }
        return null;
    }

    /*
     * int getNumOf_Pkg(int pkg);
     * 参数：pkg号（也就是uid）
     * 返回值，这类pkg的信息个数
    */
    public int getNumOf_Pkg(int pkg) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"title", "content", "pkg", "timestamp", "id", "action", "type"}, "pkg=?", new String[]{String.valueOf(pkg)}, null, null, null);
        return cursor.getCount();
    }

    public int getNumOf_action(int action) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"title", "content", "pkg", "timestamp", "id", "action", "type"}, "action=?", new String[]{String.valueOf(action)}, null, null, null);
        return cursor.getCount();
    }

    public int getTyep_NumOf_pkg(int type,int pkg) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"title", "content", "pkg", "timestamp", "id", "action", "type"}, "pkg=? and type=?", new String[]{String.valueOf(pkg),String.valueOf(type)}, null, null, null);
        return cursor.getCount();
    }
    public int getTotalNumOf_type(int type){
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"title", "content", "pkg", "timestamp", "id", "action", "type"}, "type=?", new String[]{String.valueOf(type)}, null, null, null);
        return cursor.getCount();
    }

    //所有记录的个数
    public int getNumOfRecord() {
        return getNumOf_action(0) + getNumOf_action(1) + getNumOf_action(2);
    }

    public List<PermissionRecord> getPermissionRecordOf_pkg(int pkg) {
        List<PermissionRecord> permissionRecords = new ArrayList<PermissionRecord>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"title", "content", "pkg", "timestamp", "id", "action", "type"}, "pkg=?", new String[]{String.valueOf(pkg)}, null, null, null);
        while (cursor.moveToNext()) {
            permissionRecords.add(new PermissionRecord(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getLong(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6)));
        }
        return permissionRecords;
    }

    public int[] getAllPkgNum() {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query("permissionrecord", new String[]{"pkg"}, null, null, "pkg", null, null);
        int count = cursor.getCount();
        int[] pkgList = new int[count];
        int i = 0;
        while (cursor.moveToNext()) {
            //System.out.println("cursor--->"+cursor.getInt(0));
            pkgList[i] = cursor.getInt(0);
            //System.out.println("pkg--->"+i+"--->"+pkgList[i]);
            i = i + 1;
        }
        return pkgList;
    }
}
