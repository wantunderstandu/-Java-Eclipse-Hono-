package com.example.hono_java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hono_java.modal.dto.DeviceEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper extends BaseMapper<DeviceEntity> {
}