package com.dump;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class FieldStructTable {
	//表名
	private String tableName;
	//<表名, 表信息>
	private static final Map<String, FieldStructTable> CACHE = new TreeMap<>();
	//有序字段
	private final List<String> fieldNameList = new ArrayList<>();
	//各字段信息
	private final Map<String, Field> fields = new LinkedHashMap<>();
	
	/**
	 * 拷贝表结构
	 */
	public static void copyTablesStruct() {
		readTablesStruct();
		createTables();
	}
	
	/**
	 * 读表结构
	 */
	private static void readTablesStruct() {
		try {
			DBConnection dbConn = DBConnection.createSourceConn();
			ResultSet rs = dbConn.createStatement().executeQuery("select TABLE_NAME from information_schema.tables where table_schema = '" + Config.SOURCE_DBSCHEMA + "'");
			
			List<String> tableNames = new ArrayList<>();
			while(rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				tableNames.add(tableName);
			}
			
			for(String n : tableNames) {
				initTable(dbConn, n);
			}
			
			//关闭数据库连接
			dbConn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化FieldSet
	 * @param tableName
	 * @throws SQLException
	 */
	private static void initTable(DBConnection dbConn, String tableName) throws SQLException {
		//缓存FieldSet
		String sql = Utils.createStr("SELECT * FROM `{}` WHERE 0 = 1", tableName);
		ResultSet rs = dbConn.createStatement().executeQuery(sql);
		
		//缓存表信息
		FieldStructTable fs = new FieldStructTable(rs.getMetaData());
		fs.tableName = tableName;
		put(tableName, fs);
	}
	
	/**
	 * 建表
	 * @param statement
	 * @throws SQLException 
	 */
	private static void createTables() {
		DBConnection conn = DBConnection.createTargetConn();
		try {
			Statement statement = conn.createStatement();
			for(Entry<String, FieldStructTable> entry : FieldStructTable.getCache().entrySet()) {
				createTable(statement, entry.getValue());
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 创建目标数据库
	 * @param statement
	 */
	public static void createSchema() {
		DBConnection conn = DBConnection.createTargetSystemConn();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select COUNT(*) from information_schema.schemata where schema_name = '" + Config.TARGET_DBSCHEMA + "'");
			rs.next();
			if(rs.getInt(1) <= 0) {
				System.out.println("创建数据库： " + Config.TARGET_DBSCHEMA);
				statement.executeUpdate("CREATE DATABASE `" + Config.TARGET_DBSCHEMA + "` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci");
			}else {
				System.out.println("数据库存在，不重新创建： " + Config.TARGET_DBSCHEMA);
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 建表
	 * @param st
	 * @param table
	 * @throws Exception
	 */
	private static void createTable(Statement st, FieldStructTable table) throws Exception {
		String tabelName = table.getTableName();
		//表存在先删除
		StringBuilder sqlDel = new StringBuilder("DROP TABLE IF EXISTS ");
		sqlDel.append("`");
		sqlDel.append(tabelName);
		sqlDel.append("`");
		st.executeUpdate(sqlDel.toString());
		// 拼成SQL语句
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE `").append(tabelName).append("`");	// 建表
		sql.append("(");
		
		for(String fieldName : table.getFieldNameList()) {
			Field field = table.getField(fieldName);
			//字段名
			sql.append("`").append(fieldName).append("` ");
			//类型
			sql.append(field.getColumnTypeName());
			if("double".equalsIgnoreCase(field.getColumnTypeName())) {
				
			}else {
				sql.append("(");
				sql.append(field.getColumnLen());
				sql.append(")");
			}
			sql.append(",");
		}
	
		sql.append("PRIMARY KEY (`id`)");							// 设置主键
		sql.append(")");
		System.out.println("\n建表: " + sql);
		
		// 执行建表操作
		st.executeUpdate(sql.toString());
	}
	
	/**
	 * 获取一个FieldSet对象 全局函数
	 * @param name
	 * @return
	 */
	public static FieldStructTable get(String name) {
		return CACHE.get(name);
	}
	
	/**
	 * 新增一个FieldS对象 全局函数
	 * @param name
	 * @param fieldSet
	 * @return
	 */
	public static void put(String name, FieldStructTable fieldSet) {
		CACHE.put(name, fieldSet);
	}
	
	/**
	 * 获取一个FieldSet对象 全局函数
	 * @param name
	 * @return
	 */
	public static Map<String, FieldStructTable> getCache() {
		return CACHE;
	}
	
	/**
	 * 获取所有数据表名
	 * @return
	 */
	public static Set<String> getTableNames() {
		return CACHE.keySet();
	}
	
	/**
	 * 默认构造函数
	 */
	public FieldStructTable() {}
	
	/**
	 * 构造函数
	 * @param metaData
	 */
	public FieldStructTable(ResultSetMetaData metaData) {
		try {
			init(metaData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 * @param meta
	 * @throws SQLException 
	 */
	private void init(ResultSetMetaData meta) throws SQLException {
		for(int i = 1; i <= meta.getColumnCount(); i++) {
			Field f = new Field();
			f.name = meta.getColumnName(i);			//设置名称
			f.columnType = meta.getColumnType(i);	//设置数据库字段类型
			f.columnTypeName = Utils.getSqlFieldTypeNameByType(f.columnType);
			f.columnLen = meta.getColumnDisplaySize(i);	//设置数据库字段长度
			//记录
			this.fieldNameList.add(f.name);
			this.fields.put(f.name, f);
		}
	}

	/**
	 * 字段数量
	 * @return
	 */
	public int size() {
		return fields.size();
	}
	
	/**
	 * 获取具体字段信息
	 * @param name
	 * @return
	 */
	public Field getField(String name) {
		return fields.get(name);
	}
	
	/**
	 * 获取所有字段信息
	 * @param name
	 * @return
	 */
	public List<Field> getFields() {
		return new ArrayList<>(fields.values());
	}
	
	/**
	 * 返回Entry
	 * @return
	 */
	public Set<String> getFieldNames() {
		return fields.keySet();
	}
	
	/**
	 * 返回Entry
	 * @return
	 */
	public Set<Entry<String, Field>> entrySet() {
		return fields.entrySet();
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getFieldNameList() {
		return fieldNameList;
	}

	public static void main(String[] args) {
//		readTablesStruct();
		createSchema();
	}
}
