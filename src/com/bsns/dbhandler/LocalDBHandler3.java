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

import com.bsns.beans.CountryBean;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.GroupTransactionBean;
import com.bsns.beans.LocationRequestBean;
import com.bsns.beans.MylocationTransactionBean;
import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;

public class LocalDBHandler3 extends SQLiteOpenHelper {
	
	private SQLiteDatabase sampleDB;
	public LocalDBHandler3(Context context)
	{
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
	}
	
	public Cursor retrieveUserListTransactionDetails(String userid1,String userid2)
	{
		String where_cond="userid1='"+userid1+"'and userid2='"+userid2+"'";
		Cursor cursor=sampleDB.query(true, "UserListTransaction",new String[]{"IsSharingLocation"},where_cond,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrievePLFriendName()
	{
		Cursor cursor=sampleDB.rawQuery("select distinct Fname,Lname from UserMaster where UserId in (select PlanLocationMaster.UserId from PlanLocationMaster)", null);
		return cursor;
	}
	
	public Cursor loadGroupNames(String userid1)
	{
		String where_cond="UserId='"+userid1+"'";
		Cursor cursor=sampleDB.query(true, "GroupMaster",new String[]{"GroupName"},where_cond,null, null,null,null, null);
		return cursor;

		
	}
	
	public Cursor loadGroupId(String groupname,String uid)
	{
		String where_cond="UserId='"+uid+"' and GroupName='"+groupname+"'";
		Cursor cursor=sampleDB.query(true, "GroupMaster",new String[]{"GroupId"},where_cond,null, null,null,null, null);
		return cursor;
	}
	public void updateUserListTransactionDetails(String userid1,String userid2,String IsSharingLocation)
	{
		sampleDB.execSQL("update UserListTransaction set IsSharingLocation='"+IsSharingLocation+"' where UserId1='" + userid1 + "'and UserId2='"+userid2+"'");	
	}
	public void updateGroupTransaction(String groupid,String userid)
	{
		sampleDB.execSQL("update GroupTransaction set GroupId='"+groupid+"' where UserId='" + userid +"'");	
	}
	
	public void removeUserFromGroupTransaction(String userid)
	{
		sampleDB.execSQL("delete from GroupTransaction where UserId='" + userid +"'");
	}
	
	public void removeUserFromUserMaster(String userid)
	{
		sampleDB.execSQL("delete from UserMaster where UserId='" + userid +"'");
	}
	
	public void removeUserFromUserListTransaction(String userid1,String userid2,String userid11,String userid22)
	{
		sampleDB.execSQL("delete from UserListTransaction where UserId1='" + userid1 +"'and UserId2='"+userid2+"'or UserId1='"+userid11+"'and UserId2='"+userid22+"'");
	}
	
	public Cursor retrieveRelation()
	{
		Cursor cursor=sampleDB.query(true, "RelationMaster",null,null,null, null,null,null, null);
		return cursor;
	}
	
	public Cursor retrieveRelationId(String relname)
	{
		String where_cond="RelName='"+relname+"'";
		Cursor cursor=sampleDB.query(true, "RelationMaster",new String[]{"RelId"},where_cond,null, null,null,null, null);
		return cursor;
	}
	public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
	}
	
	public void setAcceptRequest(String UserId1,String UserId2)
	{
		sampleDB.execSQL("update UserListTransaction set IsAccept='True' where UserId1='" + UserId1 +"'and UserId2='" + UserId2 + "'");
	}
	
	public Cursor retrivegroupid() {
		
		String where_cond="GroupName='Friends'";
		Cursor gpid=sampleDB.query(true, "GroupMaster",new String[]{"GroupId"},where_cond,null, null,null,null, null);
		return gpid;
		
	}
	
	
	public void updateGroupName(String groupname,String groupid,String UserId)
	{
		sampleDB.execSQL("update GroupMaster set GroupName='" + groupname + "' where GroupId='" + groupid + "' and UserId='" + UserId + "'");
		
	}
	
	public void removeSelectedGroup(String groupid,String UserId)
	{
		sampleDB.execSQL("delete from GroupMaster where GroupId='" + groupid +"' and UserId='" +UserId + "'" );
	}
	
	public Cursor retrievegroupid(String UserId)
	{
		String where_cond="UserId="+UserId;
		Cursor grpname=sampleDB.query(true, "GroupTransaction",new String[]{"GroupId"},where_cond,null, null,null,null, null);
		return grpname; 
	}
	public Cursor retrievegroupname(String GroupId)
	{
		String where_cond="GroupId="+GroupId;
		Cursor grpname=sampleDB.query(true, "GroupMaster",new String[]{"GroupName"},where_cond,null, null,null,null, null);
		return grpname; 
	}
	
	public Cursor retrieveCountryName(String CountryId)
	{
		String where_cond="CountryId="+CountryId;
		Cursor countryname=sampleDB.query(true, "Country",new String[]{"CountryName"},where_cond,null, null,null,null, null);
		return countryname; 
	}

	public void deletealldata()
	{
		sampleDB.execSQL("delete from UserMaster");
		sampleDB.execSQL("delete from UserListTransaction");
		sampleDB.execSQL("delete from GroupMaster");
		sampleDB.execSQL("delete from GroupTransaction");
		sampleDB.execSQL("delete from Country");
		sampleDB.execSQL("delete from RelationMaster");
		
		
		
	}
	
	public Cursor retrievePlanLocationDetails(String UserId)
	{
		String where_cond="UserId='"+UserId +"'";
		Cursor pldetail=sampleDB.query(true, "PlanLocationMaster",null,where_cond,null, null,null,null, null);
		return pldetail;
	}

	public Cursor retrievePlanLocationId(String UserId)
	{
		String where_cond="UserId='"+UserId +"'";
		Cursor plid=sampleDB.query(true, "PlanLocationMaster",new String[]{"PlanLocId"},where_cond,null, null,null,null, null);
		return plid;
	}
	
	public Cursor retrieveAmpIdPlPId(String PlanLocId)
	{
		String where_cond="PlanLocId='"+PlanLocId +"'";
		Cursor locid=sampleDB.query(true, "MyPlanLocationMaster",new String[]{"AmPiId","PlPiId"},where_cond,null, null,null,null, null);
		return locid;
	}
	
	public Cursor retrieveLocationTransactionByName(String Name)
	{
		String where_cond="Fname='"+Name +"'";
		Cursor locid=sampleDB.query(true, "UserMaster",new String[]{"UserId"},where_cond,null, null,null,null, null);
		Cursor loct=null;
		if(locid.getCount()>0)
		{
			locid.moveToFirst();
			String where_cond2="UserId='"+ locid.getString(0) +"'";
			loct=sampleDB.query(true, "LocationTransaction",new String[]{"Latitude,Longitude,UpdatedAt"},where_cond2,null, null,null,null, null);					
		}
		return loct;
	}
	
	public void removefromplanlocationmaster(String PlanLocId)
	{
		sampleDB.execSQL("delete from PlanLocationMaster where PlanLocId='"+PlanLocId+"'");
	}
	
	public void removeMyPlanLocation(String PlanLocId)
	{
		sampleDB.execSQL("delete from MyPlanLocationMaster where PlanLocId='"+PlanLocId+"'");
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
