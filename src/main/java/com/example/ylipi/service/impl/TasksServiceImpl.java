package com.example.ylipi.service.impl;

import com.example.ylipi.entity.Tasks;
import com.example.ylipi.mapper.TasksMapper;
import com.example.ylipi.service.ITasksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@Service
public class TasksServiceImpl extends ServiceImpl<TasksMapper, Tasks> implements ITasksService {

}
