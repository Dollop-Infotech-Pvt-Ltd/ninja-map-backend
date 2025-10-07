package com.ninjamap.app.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ninjamap.app.exception.FileValidationException;
import com.ninjamap.app.service.ICloudinaryService;

@Service
public class CloudinaryServiceImpl implements ICloudinaryService {

	@Autowired
	private Cloudinary cloudinary;

	private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

	@Override
	public String uploadFile(MultipartFile file, String folderName) {
		validateFile(file);

		try {
			String originalFilename = file.getOriginalFilename();
			String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
			String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

			String resourceType = isRawFile(extension) ? "raw" : "image";
			String publicId = resourceType.equals("raw") ? "/" + baseName + "." + extension : "/" + baseName;

			@SuppressWarnings("unchecked")
			Map<String, Object> options = ObjectUtils.asMap("folder", folderName, "resource_type", resourceType,
					"public_id", publicId);

			// Progressive + optimized images
			if ("image".equals(resourceType)) {
				options.put("progressive", true); // Progressive image
				options.put("quality", "auto"); // Automatic quality
				options.put("fetch_format", "auto"); // Auto WebP/AVIF format
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

			// Ensure secure URL includes transformations
			String url = (String) uploadResult.get("secure_url");
			if ("image".equals(resourceType)) {
				url = url.replace("/upload/", "/upload/f_auto,q_auto,fl_progressive/");
			}

			System.out.println("File uploaded successfully: " + url);
			return url;

		} catch (IOException e) {
			throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
		}
	}

	@Override
	public String deleteImage(String publicId) {
		if (publicId == null || publicId.isBlank()) {
			throw new IllegalArgumentException("Public ID cannot be null or empty.");
		}

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
			String deletionResult = (String) result.get("result");
			return deletionResult;

		} catch (IOException e) {
			throw new RuntimeException("Failed to delete file: " + publicId, e);
		}
	}

	private boolean isRawFile(String extension) {
		return extension.equals("pdf") || extension.equals("docx") || extension.equals("txt")
				|| extension.equals("csv");
	}

	private void validateFile(MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (filename == null || !filename.contains(".")) {
			throw new FileValidationException("Invalid file name: " + filename);
		}

		if (file.getSize() > MAX_FILE_SIZE_BYTES) {
			long maxSizeInMB = MAX_FILE_SIZE_BYTES / (1024 * 1024);
			throw new FileValidationException("File size exceeds maximum allowed limit of " + maxSizeInMB + " MB.");
		}
	}
}
