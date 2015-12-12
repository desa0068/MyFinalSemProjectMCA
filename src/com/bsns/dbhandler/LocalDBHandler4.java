package com.bsns.dbhandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.bsns.beans.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class LocalDBHandler4 extends SQLiteOpenHelper
{	
	private SQLiteDatabase sampleDB;
	public LocalDBHandler4(Context context)
	{
		// For temporary we have used below path to have access to DB, but once app is fully developed, change path to "BSNS.db" 
		super(context, "/mnt/sdcard/BSNSTemp/BSNS.db", null, 1);
		sampleDB=context.openOrCreateDatabase("/mnt/sdcard/BSNSTemp/BSNS.db",context.MODE_PRIVATE, null);
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS PlanLocationMaster"
				+ "( PlanLocId INTEGER PRIMARY KEY, "
				+ "  UserId TEXT NOT NULL, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longitude TEXT NOT NULL, "
				+ "  Radius TEXT NOT NULL, "
				+ "  RepeatTime TEXT NOT NULL, "
				+ "  CreationDate TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL);");
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS MyPlanLocationMaster"
				+ "( PlanLocId INTEGER PRIMARY KEY, "
				+ "  UserId TEXT NOT NULL, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longitude TEXT NOT NULL, "
				+ "  Radius TEXT NOT NULL, "
				+ "  RepeatTime TEXT NOT NULL, "
				+ "  AmPiId TEXT NOT NULL, "
				+ "  PlPiId TEXT NOT NULL, "
				+ "  CreationDate TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL);");
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS MylocationTransaction"
				+ "( LocId INTEGER PRIMARY KEY, "
				+ "  UserId TEXT NOT NULL, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longtitude TEXT NOT NULL, "
				+ "  DeviceName TEXT NOT NULL, "
				+ "  InsertedAt DATETIME DEFAULT CURRENT_TIMESTAMP);");
		
	}
	
	public void insertPlanLocationMaster(ArrayList<PlanLocationMasterBean> planLocationMasterBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "PlanLocationMaster (PlanLocId,UserId,Latitude,Longitude,Radius,RepeatTime,CreationDate,IsDelete)" +" VALUES (?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        for (int i = 0; i<planLocationMasterBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, planLocationMasterBean.get(i).getPlanLocId());
                  statement.bindString(2, planLocationMasterBean.get(i).getUserId());
                  statement.bindString(3, planLocationMasterBean.get(i).getLatitude());
                  statement.bindString(4, planLocationMasterBean.get(i).getLongitude());
                  statement.bindString(5, planLocationMasterBean.get(i).getRadius());
                  statement.bindString(6, planLocationMasterBean.get(i).getRepeatTime());
                  statement.bindString(7, planLocationMasterBean.get(i).getCreationDate());
                  statement.bindString(8, planLocationMasterBean.get(i).getIsDelete());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertMyPlanLocationMaster(ArrayList<MyPlanLocationMasterBean> myPlanLocationMasterBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "MyPlanLocationMaster (PlanLocId,UserId,Latitude,Longitude,Radius,RepeatTime,AmPiId,PlPiId,CreationDate,IsDelete)" +" VALUES (?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        for (int i = 0; i<myPlanLocationMasterBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, myPlanLocationMasterBean.get(i).getPlanLocId());
                  statement.bindString(2, myPlanLocationMasterBean.get(i).getUserId());
                  statement.bindString(3, myPlanLocationMasterBean.get(i).getLatitude());
                  statement.bindString(4, myPlanLocationMasterBean.get(i).getLongitude());
                  statement.bindString(5, myPlanLocationMasterBean.get(i).getRadius());
                  statement.bindString(6, myPlanLocationMasterBean.get(i).getRepeatTime());
                  statement.bindString(7, myPlanLocationMasterBean.get(i).getAmPiId());
                  statement.bindString(8, myPlanLocationMasterBean.get(i).getPlPiId());
                  statement.bindString(9, myPlanLocationMasterBean.get(i).getCreationDate());
                  statement.bindString(10, myPlanLocationMasterBean.get(i).getIsDelete());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertMylocationTransaction(ArrayList<MylocationTransactionBean> mylocationTransactionBean){
		//sampleDB.execSQL("delete from subject_info");
		
		String sql = "INSERT INTO "+ "MylocationTransaction (UserId,Latitude,Longtitude,DeviceName,InsertedAt)" +" VALUES (?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        for (int i = 0; i<mylocationTransactionBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, mylocationTransactionBean.get(i).getUserId());
                  statement.bindString(2, mylocationTransactionBean.get(i).getLatitude());
                  statement.bindString(3, mylocationTransactionBean.get(i).getLongtitude());
                  statement.bindString(4, mylocationTransactionBean.get(i).getDeviceName());
                  statement.bindString(5,getDateTime());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
	}
	
	public Cursor retrieveLocationRequest()
	{
		Cursor cursor=sampleDB.query(true, "LocationRequest",null,null,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveMyPlanLocationMasterByPlPiId(int PlPiId)
	{
		String where_cond="PlPiId='" + PlPiId + "'";
		Cursor cursor=sampleDB.query(true, "MyPlanLocationMaster",null,where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveMyPlanLocationByAmPiId(int AmPiId)
	{
		String where_cond="AmPiId='" + AmPiId + "'";
		Cursor cursor=sampleDB.query(true, "MyPlanLocationMaster",null,where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveMyLocationTransaction()
	{
		Cursor cursor=sampleDB.query(true, "MyLocationTransaction",null,null,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveMyLocationTransaction(int AmPiId)
	{
		String where_cond="AmPiId='" + AmPiId + "'";
		Cursor cursor=sampleDB.query(true, "MyLocationTransaction",new String[]{"PlPiId"},where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public String generateUniqueAMPiId()
	{
		int ampiid=1,cnt=0,cnt2=0;
		Cursor cursor=sampleDB.query(true, "MyPlanLocationMaster",new String[]{"AmPiId"},null,null, null,null,null, null);
		String temp[]=new String[cursor.getCount()];
		Random rand = new Random();
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			
			while(cursor.moveToNext())
			{
				temp[cnt]=cursor.getString(0);
				cnt++;
			}
			
			while(true)
			{
				cnt2=0;
				ampiid=rand.nextInt(10000);
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i].equals("1" + ampiid))
					{
						cnt2=1;
					}
				}
				if(cnt2==0)
					break;
			}
		}
		return "1" +ampiid;
	}
	
	public String generateUniquePlPiId()
	{
		int plpiid=1,cnt=0,cnt2=0;
		Cursor cursor=sampleDB.query(true, "MyPlanLocationMaster",new String[]{"PlPiId"},null,null, null,null,null, null);
		String temp[]=new String[cursor.getCount()];
		Random rand = new Random();
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			
			while(cursor.moveToNext())
			{
				temp[cnt]=cursor.getString(0);
				cnt++;
			}
			
			while(true)
			{
				cnt2=0;
				plpiid=rand.nextInt(10000);
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i].equals("2" + plpiid))
					{
						cnt2=1;
					}
				}
				if(cnt2==0)
					break;
			}
		}
		return "2" +plpiid;
	}
	
	public void deleteMyPlanLocationMaster(int PlPiId)
	{
		sampleDB.execSQL("delete from MyPlanLocationMaster where PlPiId='" + PlPiId + "'");
	}

	public void deleteMylocationTransaction(int LocId)
	{
		sampleDB.execSQL("delete from MylocationTransaction where LocId=" + LocId);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}