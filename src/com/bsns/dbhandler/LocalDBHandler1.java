package com.bsns.dbhandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.bsns.beans.LocationRequestBean;
import com.bsns.beans.MylocationTransactionBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class LocalDBHandler1 extends SQLiteOpenHelper
{	
	private SQLiteDatabase sampleDB;
	public LocalDBHandler1(Context context)
	{
		// For temporary we have used below path to have access to DB, but once app is fully developed, change path to "BSNS.db" 
		super(context, "/mnt/sdcard/BSNSTemp/BSNS.db", null, 1);
		sampleDB=context.openOrCreateDatabase("/mnt/sdcard/BSNSTemp/BSNS.db",context.MODE_PRIVATE, null);
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS LocationRequest"
				+ "( LrId INTEGER PRIMARY KEY, "
				+ "  UserId TEXT NOT NULL, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longtitude TEXT NOT NULL, "
				+ "  Radius TEXT NOT NULL, "
				+ "  RepeatTime TEXT NOT NULL, "
				+ "  AmPiId TEXT NOT NULL, "
				+ "  PlPiId TEXT NOT NULL);");
		/*
				
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS MylocationTransaction"
				+ "( LocId INTEGER PRIMARY KEY, "
				+ "  UserId TEXT NOT NULL, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longtitude TEXT NOT NULL, "
				+ "  InsertedAt DATETIME DEFAULT CURRENT_TIMESTAMP);");*/
	}
	
	public void insertLocationRequest(ArrayList<LocationRequestBean> locationRequestBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "LocationRequest (UserId,Latitude,Longtitude,Radius,RepeatTime,AmPiId,PlPiId)" +" VALUES (?,?,?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        Log.e("insert", "Insert request with " + locationRequestBean.size());
        for (int i = 0; i<locationRequestBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, locationRequestBean.get(i).getUserId());
                  statement.bindString(2, locationRequestBean.get(i).getLatitude());
                  statement.bindString(3, locationRequestBean.get(i).getLongtitude());
                  statement.bindString(4, locationRequestBean.get(i).getRadius());
                  statement.bindString(5, locationRequestBean.get(i).getRepeatTime());
                  statement.bindString(6, locationRequestBean.get(i).getAmPiId());
                  statement.bindString(7, locationRequestBean.get(i).getPlPiId());
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
	
	public void insertMylocationTransaction(ArrayList<MylocationTransactionBean> mylocationTransactionBean){
		//sampleDB.execSQL("delete from subject_info");
		
		String sql = "INSERT INTO "+ "MylocationTransaction (UserId,Latitude,Longtitude,InsertedAt)" +" VALUES (?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        for (int i = 0; i<mylocationTransactionBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, mylocationTransactionBean.get(i).getUserId());
                  statement.bindString(2, mylocationTransactionBean.get(i).getLatitude());
                  statement.bindString(3, mylocationTransactionBean.get(i).getLongtitude());
                  statement.bindString(4,getDateTime());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public Cursor retrieveLocationRequest()
	{
		Cursor cursor=sampleDB.query(true, "LocationRequest",null,null,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveLocationRequestByPlPiId(int PlPiId)
	{
		String where_cond="PlPiId='" + PlPiId + "'";
		Cursor cursor=sampleDB.query(true, "LocationRequest",null,where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveLocationRequestByAmPiId(int AmPiId)
	{
		String where_cond="AmPiId='" + AmPiId + "'";
		Cursor cursor=sampleDB.query(true, "LocationRequest",null,where_cond,null, null,null,null, null);
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
		Cursor cursor=sampleDB.query(true, "LocationRequest",new String[]{"AmPiId"},null,null, null,null,null, null);
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
		Cursor cursor=sampleDB.query(true, "LocationRequest",new String[]{"PlPiId"},null,null, null,null,null, null);
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
	
	public void deleteLocationRequest(int PlPiId)
	{
		sampleDB.execSQL("delete from LocationRequest where PlPiId='" + PlPiId + "'");
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
	
	/*
	public Cursor retrieveSem(String stream)
	{
		String where_cond="stream='" + stream + "'";
		Cursor cursor=sampleDB.query(true, "student_info",new String[]{"sem"},where_cond,null, null,null,null, null);
		return cursor;
	}
	public Cursor retrieveDiv(String stream, String sem)
	{
		String where_cond="stream='" + stream + "' and sem='" + sem +"'";
		Cursor cursor=sampleDB.query(true, "student_info",new String[]{"division"},where_cond,null, null,null,null, null);
		return cursor;
	}
	public Cursor retrieveStudIds(String stream, String sem)
	{
		String where_cond="stream='" + stream + "' and sem='" + sem +"'";
		Cursor cursor=sampleDB.query(true, "student_info",new String[]{"stud_id"},where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveSubjIds(String stream, String sem, String lect_type, String sub_type)
	{		
		String where_cond="";
		if(lect_type.equals("Practical"))
		{
			lect_type="6";
			where_cond="course_name='" + stream + "' and sem='" + sem +"'" + " and credit='" + lect_type + "' and type='" + sub_type + "'";
		}
		else
		{
			lect_type="4";
			where_cond="course_name='" + stream + "' and sem='" + sem +"'" + " and type='" + sub_type + "'";
		}
					
		Cursor cursor=sampleDB.query(true, "subject_info",new String[]{"sub_id"},where_cond,null, null,null,null, null);
		return cursor;
	}
	public Cursor retrieveStudDivwise(String stream, String sem, String div){
		String where_cond="stream='" + stream + "' and sem='" + sem + "' and division='" + div +"'";
		Cursor cursor=sampleDB.query(false, "student_info",new String[]{"stud_id,name"},where_cond,null, null,null,"stud_id", null);
		return cursor;
	}
	public Cursor retrieveStudBatchwise(String stream, String sem, String batch){
		
		String bq="";
		StringTokenizer st=new StringTokenizer(batch,",");
		int cnt=0;
		while(st.hasMoreTokens()){
			if(cnt==0)
				bq=bq + " batch=" + "'" + st.nextElement() + "'";
			else
				bq=bq + " or batch=" + "'" + st.nextElement() + "'";
			
			cnt++;
		}
		bq=" and (" + bq + ")";
		String where_cond="stream='" + stream + "' and sem='" + sem + "'" + bq;
		Cursor cursor=sampleDB.query(false, "student_info",new String[]{"stud_id,name"},where_cond,null, null,null,"stud_id", null);
		return cursor;
	}
	public Cursor retrieveBatch(String stream, String sem)
	{
		String where_cond="stream='" + stream + "' and sem='" + sem + "'";
		Cursor cursor=sampleDB.query(true, "student_info",new String[]{"batch"},where_cond,null, null,null,"batch", null);
		return cursor;
	}
	*/
}
