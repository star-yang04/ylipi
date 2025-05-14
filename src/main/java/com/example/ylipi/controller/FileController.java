package com.example.ylipi.controller;

import cn.hutool.core.io.FileUtil;
import com.example.ylipi.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 功能:处理文件上传下载相关的接口
 * 作者：yangxin
 * 日期：2025/5/9 15:17
 */
@RestController
@RequestMapping("/files")
public class FileController {

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 找到文件上传的位置
        String filePath="/opt/uploads/avatars/";
        if(!FileUtil.exist(filePath)){
            FileUtil.mkdir(filePath);
        }
        byte[] bytes=file.getBytes();
        String fileName =System.currentTimeMillis()+"_"+ file.getOriginalFilename();
        //String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        // 写入文件
        FileUtil.writeBytes(bytes,filePath+fileName);
        String url="http://115.120.224.202:9993/avatars/"+fileName;
        return Result.success(url);

    }


    /**
     * 文件下载接口
     * @param fileName
     * @param response
     * @throws IOException
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        //通过response把文件送出去

        // 找到文件的位置
        String filePath="/opt/uploads/avatars/";//获取当前项目的根路径
        //拿出文件
        String realPath=filePath+fileName; // D:\javaweb\project\spring_test\files\exp.png
        boolean exist = FileUtil.exist(realPath);
        // 读取文件字节流
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream os = response.getOutputStream();
        // 输出流对象把文件写出到客户端
        os.write(bytes);
        os.flush();
        os.close();
    }
}
