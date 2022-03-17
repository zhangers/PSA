package com.FTD.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SigRecordDao {
	private SQLiteDatabase db;
	private DB_Sign_OpenHelper helper;
	
	public SigRecordDao(Context context){
		helper = new DB_Sign_OpenHelper(context);
	}
	public void add(SigRecord sigRecord){
		System.out.println("add begin!");
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("appLabel", sigRecord.getAppLabel());
		values.put("pkgname", sigRecord.getPkgName());
		values.put("signature", sigRecord.getSignature());
		db.insert("signatrueRecord", "appLabel", values);
		System.out.println("add is done!");
	}
	
	public SigRecord find(String appLabel){
		db = helper.getWritableDatabase();
		Cursor cursor = db.query("signatrueRecord",new String[]{"appLabel","pkgname","signature"}, "appLabel=?", new String[]{String.valueOf(appLabel)}, null, null, null);
		if (cursor.moveToNext()) {
			return new SigRecord(cursor.getString(0), cursor.getString(1), cursor.getLong(2));
		}
		return null;
	}
	
	public SigRecord findByPackageName(String packageName){
		db = helper.getWritableDatabase();
		Cursor cursor = db.query("signatrueRecord",new String[]{"appLabel","pkgname","signature"}, "pkgname=?", new String[]{String.valueOf(packageName)}, null, null, null);
		if (cursor.moveToNext()) {
			return new SigRecord(cursor.getString(0), cursor.getString(1), cursor.getLong(2));
		}
		return null;
	}


}
