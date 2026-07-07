package com.example.hono_java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hono_java.modal.Device;
import org.apache.ibatis.annotations.Mapper;


//Device的mapper层，直接继承basemapper
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
