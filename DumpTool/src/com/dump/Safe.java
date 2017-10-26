package com.dump;

public class Safe {
	/**
	 * 目标数据库url是否安全（防止覆盖线上数据库）
	 * @return
	 */
	public static boolean targetUrlIsSafe() {
		String targetDbUrl = Config.TARGET_DBURL;
//		String targetDbUrl = "jdbc:mysql://10.3.10.25:13303/longzu_s1";
		String ipHead = targetDbUrl.substring(13, 16);
		if(!"127".equals(ipHead) && !"10.".equals(ipHead) && !"129".equals(ipHead)) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		targetUrlIsSafe();
	}
}
