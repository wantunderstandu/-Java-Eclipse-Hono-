package com.example.hono_java.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hono_java.modal.DeviceCredentials;
import org.apache.ibatis.annotations.Mapper;


//DeviceCredentials的mapper层，直接继承basemapper
@Mapper
public interface DeviceCredentialsMapper extends BaseMapper<DeviceCredentials> {
}
