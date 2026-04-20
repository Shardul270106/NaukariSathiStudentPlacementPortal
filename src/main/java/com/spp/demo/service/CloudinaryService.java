package com.spp.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}")    String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret,
                "secure",     true
        ));
    }

    public String uploadPdf(MultipartFile file, Integer userId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",        "resumes",
                            "public_id",     "resume_" + userId + "_" + System.currentTimeMillis(),
                            "resource_type", "raw",
                            "format",        "pdf"       // ← forces .pdf extension on the URL
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF upload to Cloudinary failed: " + e.getMessage());
        }
    }

    public String uploadImage(MultipartFile file, String publicId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",        "company-logos",
                            "public_id",     publicId,
                            "resource_type", "image"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Image upload to Cloudinary failed: " + e.getMessage());
        }
    }
}