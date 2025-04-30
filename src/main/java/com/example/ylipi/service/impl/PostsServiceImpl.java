package com.example.ylipi.service.impl;

import com.example.ylipi.entity.Posts;
import com.example.ylipi.mapper.PostsMapper;
import com.example.ylipi.service.IPostsService;
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
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements IPostsService {

}
