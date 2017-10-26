package com.dump;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FieldDataTable {
	//表名
	private String tableName;
	//有序数据
	private final List<List<Object>> dataList = new ArrayList<>();
	
	/**
	 * 拷数据
	 * @throws SQLException 
	 */
	public static void copyTablesDatas() throws SQLException {
		DBConnection dbConnSource = DBConnection.createSourceConn();
		DBConnection dbConnTarget = DBConnection.createTargetConn();
		try {
			Statement stSource = dbConnSource.createStatement();
			Statement stTarget = dbConnTarget.createStatement();
			for(String name : FieldStructTable.getTableNames()) {
				//忽略表
				if(Config.ignoreTables.contains(name)) {
					continue;
				}
				long start = System.currentTimeMillis();
				System.out.println("copy table..." + name);
				
				FieldDataTable table = readTable(name, stSource);
				writeTable(stTarget, table);
				
				long end = System.currentTimeMillis();
				System.out.println("copy table " + name + " complete cost time=" + (end - start)/1000 + "s");
			}
			//更新为自启服serverId
			if(Config.IS_SELF_START_UP) {
				updateServerId(stTarget);
			}
		} catch (SQLException e) {
			dbConnSource.close();
			dbConnTarget.close();
			e.printStackTrace();
		}
		dbConnSource.close();
		dbConnTarget.close();
	}
	
	/**
	 * 读表数据
	 * @param tableName
	 * @param st
	 * @return
	 * @throws SQLException
	 */
	private static FieldDataTable readTable(String tableName, Statement st) throws SQLException {
//		long start = System.currentTimeMillis();
//		System.out.println("read table..." + tableName);
		StringBuilder sb = new StringBuilder("SELECT * from ");
		sb.append(tableName);
		if(Config.perTableMap.containsKey(tableName)) {
			sb.append(" where ");
			sb.append(Config.perTableMap.get(tableName) + Config.HUMAN_ID_IN_STR);
		}
		ResultSet resultSet = st.executeQuery(sb.toString());
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		FieldDataTable fieldDataTable = new FieldDataTable();
		while(resultSet.next()) {
			List<Object> list = new ArrayList<>();
			for(int i = 1; i <= columnCount; i++) {
				list.add(resultSet.getObject(i));
			}
//			System.out.println(list);
			fieldDataTable.dataList.add(list);
		}
		fieldDataTable.tableName = tableName;
//		long end = System.currentTimeMillis();
//		System.out.println("read table " + tableName + "complete cost time=" + (end - start)/1000 + "s");
		return fieldDataTable;
	}
	
	
	/**
	 * 写表
	 * @param statement
	 * @param table
	 * @throws SQLException
	 */
	private static void writeTable(Statement statement, FieldDataTable table) throws SQLException {
//		long start = System.currentTimeMillis();
//		System.out.println("writeTable... " + table.getTableName());
		
		String tabelName = table.getTableName();
		for(List<Object> list : table.getDataList()) {
			//INSERT INTO `core_id_allot` VALUES ('0', 'HUMAN', '785500');
			StringBuilder sqlInsert = new StringBuilder("INSERT INTO `");
			sqlInsert.append(tabelName);
			sqlInsert.append("` VALUES (");
			for(int i = 0; i < list.size(); i++) {
				sqlInsert.append("'");
				sqlInsert.append(list.get(i));
				sqlInsert.append("'");
				
				if(i + 1 < list.size()) {
					sqlInsert.append(", ");
				}
			}
			sqlInsert.append(")");
//			System.out.println(sqlInsert.toString());
			statement.execute(sqlInsert.toString());
		}
		
//		long end = System.currentTimeMillis();
//		System.out.println("writeTable " + table.getTableName() + "complete cost time=" + (end - start)/1000 + "s");
	}
	
	
	/**
	 * 更新serverId
	 * @param stTarget
	 * @throws SQLException
	 */
	private static void updateServerId(Statement stTarget) throws SQLException {
		System.out.println("更新为自启服id");
		stTarget.executeUpdate("UPDATE demo_human SET serverId = 17");
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<List<Object>> getDataList() {
		return dataList;
	}
	
	public static void main(String[] args) {
		DBConnection conn = DBConnection.createSourceConn();
		try {
			readTable("demo_human", conn.createStatement());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
