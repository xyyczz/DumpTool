package com.dump;
import java.sql.SQLException;

public class Main {
	
	public static void main(String[] args) throws SQLException {
		System.out.println("=====start dump=====");
		if(!Safe.targetUrlIsSafe()) {
			System.out.println("请确认目标数据库是否安全 url= " + Config.TARGET_DBURL);
			return;
		}
		
		long start = System.currentTimeMillis();
		//创建数据库
		FieldStructTable.createSchema();
		//拷贝表结构
		FieldStructTable.copyTablesStruct();
		//拷贝数据
		FieldDataTable.copyTablesDatas();
		
		long end = System.currentTimeMillis();
		
		System.out.println("=====end dump cost=====" + (end - start)/1000/60 + "min");
	}
}
