package com.github.data.authority.util;

import com.google.common.collect.Lists;
import java.util.List;

public class Test {
    static String sql = "select * from test left join test1 on 1 = 1 where name = 'maple';";
    static String sql5 = "select * from test a left join test1 b on a.id = b.id where a.name = 'maple';";
    static String sql2 = "select * from (select * from test a left join test1 b on 1 = 1 where name = 'maple') x where x" +
        ".sex = 'man';";
   static String sql3 = "select * from (select * from test a left join test1 b on 1 = 1 and a.is_delete is false where " +
        "name = 'maple') x where x.sex = 'man' and x.is_delete is false;";
    static String sql4 = "select * from (select * from test a left join test1 b on 1 = 1 and a.is_delete is false where " +
        "name = 'maple') x where x.sex = 'man' and x.is_delete is false " +
        "union all select * from (select * from test a left join test1 b on 1 = 1 and a.is_delete is false " +
        "where name = 'maple') x where x.sex = 'woman' and x.is_delete is false;";
    static List<String> list = Lists.newArrayList("1","2");
    public static void main(String[] args) {
          String sql = DruidParseStatementsUtils.parseStatements(sql3);
          System.out.println(sql);
    }
}
