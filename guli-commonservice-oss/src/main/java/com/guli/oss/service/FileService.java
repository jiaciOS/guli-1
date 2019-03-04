package com.guli.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author helen
 * @since 2019/2/27
 */
public interface FileService {

	/**
	 * 文件上传至阿里云
	 * @param file
	 * @return
	 */
	String upload(MultipartFile file);
}
