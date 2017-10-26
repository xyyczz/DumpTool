1.修改config中dumpConfig.properties源数据库配置
2.修改config中dumpConfig.properties目标数据库配置，并创建一个db.target.schema值名字的数据库，不创建工具会自动创建
3.配置config中dumpConfig.properties开服时间配置
4.humanids=设置想要导出的玩家id(支持多玩家用,分隔)
5.个人表配置 （表明名，与角色id关联字段名字）注：个人表知道出相关数据
6.忽略表配置 注：忽略表不导出数据
7.个人表，忽略表除外其他表全部导出