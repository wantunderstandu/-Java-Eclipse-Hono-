package com.example.hono_java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.hono_java.modal.dto.DeviceTelemetry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceTelemetryMapper extends BaseMapper<DeviceTelemetry> {
}
