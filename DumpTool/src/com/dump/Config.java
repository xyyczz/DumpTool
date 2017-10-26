package com.dump;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Config {
	public static final String SOURCE_DBURL;
	public static final String SOURCE_DBUSER;
	public static final String SOURCE_DBPWD;
	public static final String SOURCE_DBSCHEMA;
	
	public static final String TARGET_DBURL;
	public static final String TARGET_DBUSER;
	public static final String TARGET_DBPWD;
	public static final String TARGET_DBSCHEMA;
	//忽略开服时间
	public static final boolean IGNORE_SERVER_START;
	
	//配置文件名称
	private static final String CONFIG_NAME = "dumpConfig.properties";
	
	//个人表<表明， 与角色id关联的字段名>
	public static final Map<String, String> perTableMap = new HashMap<>();
	//忽略表
	public static final Set<String> ignoreTables = new HashSet<>();
	//玩家id
	public static final String HUMAN_ID_IN_STR;
	//
	public static final boolean IS_SELF_START_UP;
	
	
	static {
		//获取配置
		Properties prop = Utils.readProperties(CONFIG_NAME);
		SOURCE_DBURL = prop.getProperty("db.source.url");
		SOURCE_DBUSER = prop.getProperty("db.source.user");
		SOURCE_DBPWD = prop.getProperty("db.source.pwd");
		SOURCE_DBSCHEMA = prop.getProperty("db.source.schema");
		
		TARGET_DBURL = prop.getProperty("db.target.url");
		TARGET_DBUSER = prop.getProperty("db.target.user");
		TARGET_DBPWD = prop.getProperty("db.target.pwd");
		TARGET_DBSCHEMA = prop.getProperty("db.target.schema");
		
		IGNORE_SERVER_START = Boolean.valueOf(prop.getProperty("is.ignore.serverstarttime"));
		
		//开服时间配置
		Set<String> startServerTimeSet = new HashSet<>();
		for(int i=1; i<10; i++){
			String table = prop.getProperty("serverstarttime.part" + i);
			if(table != null){
				startServerTimeSet.add(table);
			}
		}
		
		//玩家ids, sql in子句例：IN (100010000000000001, 100010000000000002)
		String str = prop.getProperty("humanids");
		String[] idStrs = str.split(",");
		StringBuilder sb = new StringBuilder(" IN (");
		for(int i = 0; i < idStrs.length - 1; i++) {
			sb.append(idStrs[i]);
			sb.append(", ");
		}
		sb.append(idStrs[idStrs.length - 1]);
		sb.append(")");
		HUMAN_ID_IN_STR = sb.toString();
		
		//个人表配置
		for(int i=1; i<200; i++){
			String table = prop.getProperty("persional.table" + i);
			if(table != null){
				String[] tableStr = table.split(",");
				perTableMap.put(tableStr[0], tableStr[1]);
			}
		}
		//忽略表配置
		for(int i=1; i<200; i++){
			String table = prop.getProperty("ignore.table" + i);
			if(table != null){
				ignoreTables.add(table);
			}
		}
		//忽略开服时间
		if(IGNORE_SERVER_START) {
			ignoreTables.addAll(startServerTimeSet);
		}
		//是否是自启服
		IS_SELF_START_UP = Boolean.valueOf(prop.getProperty("is.self.start.up"));
	}
	
	public static void main(String[] args) {
		System.out.println("aa");
	}
}
