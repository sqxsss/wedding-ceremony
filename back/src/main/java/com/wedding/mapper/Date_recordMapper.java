package com.wedding.mapper;

import com.wedding.model.po.Date_record;
import java.util.List;

public interface Date_recordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Date_record record);

    Date_record selectByPrimaryKey(Integer id);

    List<Date_record> selectAll();

    int updateByPrimaryKey(Date_record record);
}