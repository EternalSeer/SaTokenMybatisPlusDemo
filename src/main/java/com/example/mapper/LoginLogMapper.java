package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.domain.LoginLog;
import com.example.domain.LoginLogDailyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    List<LoginLogDailyStats> selectDailyStats(
            @Param("username") String username,
            @Param("success") Boolean success,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
