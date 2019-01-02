package com.pinyougou.manager.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entry.ResultSet;
import util.FastDFSClient;


@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")	
    private String FILE_SERVER_URL;

	@RequestMapping("/upLoad")
public ResultSet upLoad(MultipartFile file)  {
		
	//1.获取文件的扩展名
	String filename = file.getOriginalFilename();
	System.out.println(filename);
	String extName = filename.substring(filename.lastIndexOf(".") + 1);
	
	
	//2、创建一个 FastDFS 的客户端
	FastDFSClient fastDFSClient;
	try {
		fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
		String path = fastDFSClient.uploadFile(file.getBytes(),extName);
		String url=FILE_SERVER_URL+path;
		return new ResultSet(true,url);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new ResultSet(false,"上传失败");
	}
	//3、执行上传处理
	
}
}
