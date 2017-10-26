package com.dump;
import org.apache.commons.lang3.builder.ToStringBuilder;


public class Field {

	public String name;				//字段名称 实体与数据库相同

	public int columnType;			//数据库字段类型
	
	public String columnTypeName;		//数据库字段类型名字

	public int columnLen;			//数据库字段长度

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public int getColumnLen() {
		return columnLen;
	}

	public void setColumnLen(int columnLen) {
		this.columnLen = columnLen;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("columnType", columnType)
				.append("columnLen", columnLen)
				.append("columnTypeName", columnTypeName)
				.toString();
	}
}
