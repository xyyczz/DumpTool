package com.dump;
import java.io.FileInputStream;
import java.sql.Types;
import java.util.Properties;

import org.apache.logging.log4j.message.ParameterizedMessage;

public class Utils {
	
	/**
	 * 基于参数创建字符串
	 * #0开始
	 * @param str
	 * @param params
	 * @return
	 */
	public static String createStr(String str, Object...params) {
		return ParameterizedMessage.format(str, params);
	}
	
	/**
	 * 读取配置文件
	 * @return
	 */
	public static Properties readProperties(String name) {
//		String filePath = "E:/DumpTool/config/dumpConfig.properties";
		String filePath = System.getProperty("user.dir");
		filePath = filePath.replaceAll("\\\\", "/");
		filePath =  filePath + "/../config/dumpConfig.properties";
		try(FileInputStream in = new FileInputStream(filePath)) {
			Properties p = new Properties();
			p.load(in);
			
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据数据库列类型获得列类型名
	 * @param type
	 * @return
	 */
	public static String getSqlFieldTypeNameByType(int type) {
		switch (type) {
		case Types.INTEGER:
			return "INTEGER".toLowerCase();
		case Types.BIT:
			return "BIT".toLowerCase();
		case Types.BOOLEAN:
			return "BOOLEAN".toLowerCase();
		case Types.TINYINT:
			return "TINYINT".toLowerCase();
		case Types.SMALLINT:
			return "SMALLINT".toLowerCase();
		case Types.CHAR: 
			return "CHAR".toLowerCase();
		case Types.BIGINT:
			return "BIGINT".toLowerCase();
		case Types.DECIMAL:
			return "DECIMAL".toLowerCase();
		case Types.DOUBLE:
			return "DOUBLE".toLowerCase();
		case Types.REAL:
			return "REAL".toLowerCase();
		case Types.FLOAT:
			return "FLOAT".toLowerCase();
		case Types.VARBINARY:
			return "VARBINARY".toLowerCase();
		case Types.LONGVARBINARY:
			return "LONGVARBINARY".toLowerCase();
		case Types.BLOB:
			return "BLOB".toLowerCase();
		case Types.LONGNVARCHAR:
			return "LONGNVARCHAR".toLowerCase();
		case Types.LONGVARCHAR:
			return "LONGVARCHAR".toLowerCase();
		case Types.VARCHAR:
			return "VARCHAR".toLowerCase();
		default:
			return "VARCHAR".toLowerCase();
		}
	}
}
