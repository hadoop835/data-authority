package com.github.data.authority.util;

import com.github.data.authority.parser.DruidParseStatementsFactory;
import com.github.data.authority.parser.ParserColumnInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

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

    static String sql6 = "select * from test x where x.sex = 'woman'";
    static String sql7 = "select * from test x where x.sex = 'woman' union select * from test x where x.sex = '111'";
    static String sql8 = "select * from test x where exists(select id from test1 a where a.id = x.id) and x.id=1";
    static List<String> list = Lists.newArrayList("1", "2");

    public static void main(String[] args) {
        Map<String, List<ParserColumnInfo>> list = Maps.newHashMap();

        List<ParserColumnInfo> parserColumnInfos = Lists.newArrayList();
        ParserColumnInfo parserColumnInfo = new ParserColumnInfo();
        parserColumnInfo.setTable("test");
        parserColumnInfo.setColumn("age =0 and sex =1 and name like ? or a=1");
        parserColumnInfo.setOperator("0");
        parserColumnInfos.add(parserColumnInfo);

        list.put("test", parserColumnInfos);
        String result = DruidParseStatementsFactory.parseStatements(sql4, list);
        System.out.println(result);
    }
}
