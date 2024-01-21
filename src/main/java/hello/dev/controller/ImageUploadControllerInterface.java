package hello.dev.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public interface ImageUploadControllerInterface {

    void addImageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload);

    void editImageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload);

    void imageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload);

    String getFileName(String fileName, HttpServletRequest request) throws ServletException, IOException;

    String setFileName(String fileName);

    String setFilePath(String fileName);
}
