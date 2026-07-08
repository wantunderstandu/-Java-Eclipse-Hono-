package com.example.hono_java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.hono_java.modal.dto.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {
}
