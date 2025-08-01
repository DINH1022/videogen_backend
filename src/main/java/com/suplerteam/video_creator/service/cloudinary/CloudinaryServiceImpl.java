package com.suplerteam.video_creator.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService{
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadAudio(
            InputStreamResource resource, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".mp3");
        try (InputStream inputStream = resource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                    "resource_type", "video"
            ));
            return uploadResult.get("secure_url").toString();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        finally {
            tempFile.delete();
        }
    }

    @Override
    public String uploadImage(
            InputStreamResource resource, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".png");
        try (InputStream inputStream = resource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                    "resource_type", "image"
            ));
            return uploadResult.get("secure_url").toString();
        }
        finally {
            tempFile.delete();
        }
    }

    @Override
    public String uploadVideoFromUrl(String url) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(url, ObjectUtils.asMap(
                "resource_type", "video"
        ));
        return uploadResult.get("secure_url").toString();
    }

    @Override
    public String uploadVideo(InputStreamResource resource, String filename) throws IOException {
        File tempFile = File.createTempFile(filename, ".mp4");
        try (InputStream inputStream = resource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                    "resource_type", "video"
            ));
            return uploadResult.get("secure_url").toString();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        finally {
            tempFile.delete();
        }
    }
}
