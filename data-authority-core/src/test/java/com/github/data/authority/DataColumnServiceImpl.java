package com.github.data.authority;

import com.google.common.collect.Lists;
import java.util.List;

public class DataColumnServiceImpl implements IDataColumnService{
    @Override
    public List<String> getDataColumnByUserId(String code) {
        return Lists.newArrayList("adminDtos.name","adminDto.name");
    }
}
