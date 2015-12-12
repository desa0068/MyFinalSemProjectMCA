package com.bsns.dbhandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.bsns.beans.ClientInfo;
import com.bsns.beans.CountryBean;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.GroupTransactionBean;
import com.bsns.beans.LocationRequestBean;
import com.bsns.beans.LocationTransactionBean;
import com.bsns.beans.MylocationTransactionBean;
import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;

public class LocalDBHandler2 extends SQLiteOpenHelper
{	
	private SQLiteDatabase sampleDB;
	public LocalDBHandler2(Context context)
	{
		// For temporary we have used below path to have access to DB, but once app is fully developed, change path to "BSNS.db" 
		super(context, "/mnt/sdcard/BSNSTemp/BSNS.db", null, 1);
		sampleDB=context.openOrCreateDatabase("/mnt/sdcard/BSNSTemp/BSNS.db",context.MODE_PRIVATE, null);
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS UserMaster"
				+ "( UserId TEXT, "
				+ "  Fname TEXT NOT NULL, "
				+ "  Mname TEXT NOT NULL, "
				+ "  Lname TEXT NOT NULL, "
				+ "  EmailId TEXT NOT NULL, "
				+ "  ContactNo TEXT NOT NULL, "
				+ "  Password TEXT NOT NULL, "
				+ "  Gender TEXT NOT NULL, "
				+ "  RegDate TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL, "
				+ "  DPUrl TEXT NOT NULL, "
				+ "  CountryId TEXT NOT NULL);");
		
				
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS UserListTransaction"
				+ "( UserListId TEXT, "
				+ "  UserId1 TEXT NOT NULL, "
				+ "  UserId2 TEXT NOT NULL, "
				+ "  SenderRelId TEXT NOT NULL, "
				+ "  ReceiverRelId TEXT NOT NULL, "
				+ "  IsSharingLocation TEXT NOT NULL, "
				+ "  IsAccept TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL, "
				+ "  CreationDate DATETIME DEFAULT CURRENT_TIMESTAMP);");
		
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS GroupMaster"
				+ "( GroupId TEXT, "
				+ "  GroupName TEXT NOT NULL, "
				+ "  UserId TEXT NOT NULL, "
				+ "  DPUrl TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL, "
				+ "  CreationDate DATETIME DEFAULT CURRENT_TIMESTAMP);");
		
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS GroupTransaction"
				+ "( GTId TEXT, "
				+ "  GroupId TEXT NOT NULL, "
				+ "  UserId TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL, "
				+ "  UpdationDate DATETIME DEFAULT CURRENT_TIMESTAMP);");
		
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS Country"
				+ "( CountryId TEXT, "
				+ "  CountryName TEXT NOT NULL, "
				+ "  CountryCode TEXT NOT NULL, "
				+ "  CountryDescription TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL);");
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS RelationMaster"
				+ "( RelId TEXT, "
				+ "  RelName TEXT NOT NULL, "
				+ "  IsDelete TEXT NOT NULL);");
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS ClientInfo"
				+ "( PSubId TEXT, "
				+ "  PSubName TEXT NOT NULL, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longitude TEXT NOT NULL);");
		
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS LocationTransaction"
				+ "( UserId TEXT, "
				+ "  Latitude TEXT NOT NULL, "
				+ "  Longitude TEXT NOT NULL, "
				+ "  DeviceName TEXT NOT NULL, "
				+ "  UpdatedAt TEXT NOT NULL);");
	}
	
	public Cursor retrieveAllCountry()
	{
		String where_cond="IsDelete='False'";
		Cursor cursor=sampleDB.query(true, "Country",null,where_cond,null, null,null,null, null);
		return cursor;
		
	}
	
	public Cursor retrieveFriendRequests(String UserId)
	{
		Cursor cursor=sampleDB.rawQuery("select * from UserMaster where UserId in(select UserId1 from UserListTransaction where UserId2=? and IsAccept='False' and IsDelete='False')",new String[]{UserId});
		return cursor;
	}
	
	public void insertUserMaster(ArrayList<UserMasterBean> userMasterBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "UserMaster" +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<userMasterBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, userMasterBean.get(i).getUserId());
                  statement.bindString(2, userMasterBean.get(i).getFname());
                  statement.bindString(3, userMasterBean.get(i).getMname());
                  statement.bindString(4, userMasterBean.get(i).getLname());
                  statement.bindString(5, userMasterBean.get(i).getEmailId());
                  statement.bindString(6, userMasterBean.get(i).getContactNo());
                  statement.bindString(7, userMasterBean.get(i).getPassword());
                  statement.bindString(8, userMasterBean.get(i).getGender());
                  statement.bindString(9, userMasterBean.get(i).getRegDate());
                  statement.bindString(10, userMasterBean.get(i).getIsDelete());
                  statement.bindString(11, userMasterBean.get(i).getDPUrl());
                  statement.bindString(12, userMasterBean.get(i).getCountryId());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertUserListTransaction(ArrayList<UserListTransactionBean> userListTransactionBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "UserListTransaction" +" VALUES (?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<userListTransactionBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, userListTransactionBean.get(i).getUserListId());
                  statement.bindString(2, userListTransactionBean.get(i).getUserId1());
                  statement.bindString(3, userListTransactionBean.get(i).getUserId2());
                  statement.bindString(4, userListTransactionBean.get(i).getSenderRelId());
                  statement.bindString(5, userListTransactionBean.get(i).getReceiverRelId());
                  statement.bindString(6, userListTransactionBean.get(i).getIsSharingLocation());
                  statement.bindString(7, userListTransactionBean.get(i).getIsAccept());
                  statement.bindString(8, userListTransactionBean.get(i).getIsDelete());
                  statement.bindString(9, userListTransactionBean.get(i).getCreationDate());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertGroupMaster(ArrayList<GroupMasterBean> groupMasterBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "GroupMaster" +" VALUES (?,?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<groupMasterBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, groupMasterBean.get(i).getGroupId());
                  statement.bindString(2, groupMasterBean.get(i).getGroupName());
                  statement.bindString(3, groupMasterBean.get(i).getUserId());
                  statement.bindString(4, groupMasterBean.get(i).getDPUrl());
                  statement.bindString(5, groupMasterBean.get(i).getIsDelete());
                  statement.bindString(6, groupMasterBean.get(i).getCreationDate());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertGroupTransaction(ArrayList<GroupTransactionBean> groupTransactionBean)
	{
		//sampleDB.execSQL("delete from student_info");
		
		String sql = "INSERT INTO "+ "GroupTransaction" +" VALUES (?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<groupTransactionBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, groupTransactionBean.get(i).getGtId());
                  statement.bindString(2, groupTransactionBean.get(i).getGroupId());
                  statement.bindString(3, groupTransactionBean.get(i).getUserId());
                  statement.bindString(4, groupTransactionBean.get(i).getIsDelete());
                  statement.bindString(5, groupTransactionBean.get(i).getUpdationDate());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertCountry(ArrayList<CountryBean> countryBean)
	{
		//sampleDB.execSQL("delete from Country");
		
		String sql = "INSERT INTO "+ "Country" +" VALUES (?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<countryBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, countryBean.get(i).getCountryId());
                  statement.bindString(2, countryBean.get(i).getCountryName());
                  statement.bindString(3, countryBean.get(i).getCountryCode());
                  statement.bindString(4, countryBean.get(i).getCountryDescription());
                  statement.bindString(5, countryBean.get(i).getIsDelete());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertRelationMaster(ArrayList<RelationMasterBean> relationMasterBean)
	{
		//sampleDB.execSQL("delete from RelationMaster");
		
		String sql = "INSERT INTO "+ "RelationMaster" +" VALUES (?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<relationMasterBean.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, relationMasterBean.get(i).getRelId());
                  statement.bindString(2, relationMasterBean.get(i).getRelName());
                  statement.bindString(3, relationMasterBean.get(i).getIsDelete());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	public void insertClientInfo(ArrayList<ClientInfo> clientInfo)
	{
		//sampleDB.execSQL("delete from ClientInfo");
		
		String sql = "INSERT INTO "+ "ClientInfo" +" VALUES (?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<clientInfo.size(); i++) {
                  statement.clearBindings();
                  statement.bindString(1, clientInfo.get(i).getPSubId());
                  statement.bindString(2, clientInfo.get(i).getPSubName());
                  statement.bindString(3, clientInfo.get(i).getLatitude());
                  statement.bindString(4, clientInfo.get(i).getLongitude());
                  statement.execute();
         }
         sampleDB.setTransactionSuccessful();	
         sampleDB.endTransaction();
	}
	
	
	public void insertLocationTransaction(ArrayList<LocationTransactionBean> locationTransactionBean)
	{
		
		
		String sql = "INSERT INTO "+ "LocationTransaction" +" VALUES (?,?,?,?,?);";
        SQLiteStatement statement = sampleDB.compileStatement(sql);
        sampleDB.beginTransaction();
        
        for (int i = 0; i<locationTransactionBean.size(); i++) {
        		  sampleDB.execSQL("delete from LocationTransaction where UserId='" + locationTransactionBean.get(i).getUserId() + "'");
                  statement.clearBindings();
                  statement.bindString(1, locationTransactionBean.get(i).getUserId());
                  statement.bindString(2, locationTransactionBean.get(i).getLatitude());
                  statement.bindString(3, locationTransactionBean.get(i).getLongitude());
                  statement.bindString(4, locationTransactionBean.get(i).getDeviceName());
                  statement.bindString(5, locationTransactionBean.get(i).getUpdatedAt());
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
	
	public Cursor retrieveGroupMaster()
	{
		Cursor cursor=sampleDB.query(true, "GroupMaster",null,null,null, null,null,null, null);
		return cursor;
	}

	public Cursor retrieveUserMasterById(String UserId)
	{
		String where_cond="UserId='" + UserId + "'";
		Cursor cursor=sampleDB.query(true, "UserMaster",null,where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveFriends(String UserId,String GroupName)
	{
		Cursor cursor=sampleDB.rawQuery("select * from UserMaster where UserId in (select GroupTransaction.UserId from GroupTransaction where GroupTransaction.GroupId in (select GroupMaster.GroupId from GroupMaster where GroupMaster.UserId=? and GroupMaster.GroupName=?))", new String[]{UserId,GroupName});
		return cursor;
	}

	public Cursor retrieveRelationMaster()
	{
		Cursor cursor=sampleDB.query(true, "RelationMaster",null,null,null, null,null,null, null);
		return cursor;
	}
	
	public String retrieveReceiverRelationId(String UserId1,String UserId2)
	{
		Cursor cursor=sampleDB.rawQuery("select RelationMaster.RelName from RelationMaster where RelId in (select UserListTransaction.ReceiverRelId from UserListTransaction where UserId1=? and UserId2=?)", new String[]{UserId1,UserId2});
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			cursor.moveToFirst();
		}
		return cursor.getString(0);
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
	
	public Cursor retrieveLocationTransactionByUserId(String UserId)
	{
		String where_cond="UserId='" + UserId + "'";
		Cursor cursor=sampleDB.query(true, "LocationTransaction",null,where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveClientInfo()
	{
		Cursor cursor=sampleDB.query(true, "ClientInfo",null,null,null, null,null,null, null);
		return cursor;
	}
	
	public void deleteAllRefreshData()
	{
		sampleDB.execSQL("delete from RelationMaster");
		sampleDB.execSQL("delete from ClientInfo");
		sampleDB.execSQL("delete from Country");
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
