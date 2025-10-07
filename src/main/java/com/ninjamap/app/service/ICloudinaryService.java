package com.ninjamap.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
	public String uploadFile(MultipartFile file, String folder);

	public String deleteImage(String publicId);
}
