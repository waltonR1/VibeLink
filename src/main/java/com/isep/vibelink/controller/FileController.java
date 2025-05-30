package com.isep.vibelink.controller;

import com.isep.vibelink.util.ResponseInfo;
import com.isep.vibelink.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器，用于处理前端文件上传请求。
 */
@Slf4j
@Controller
public class FileController {

    private final FileService fileService;

    /**
     * 构造函数注入文件服务。
     */
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    /**
     * 处理文件上传请求。
     *
     * @param file 上传的文件 （表单字段名为 file）
     * @return 包含上传结果的响应信息，成功时返回文件路径
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseInfo<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileService.storeFile(file);
            return ResponseInfo.success("Upload successful", path);
        } catch (Exception e) {
            log.error("Upload failed", e);
        }
        return ResponseInfo.fail("Upload failed");
    }
}
