package hello.dev.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/")
public class ImageUploadController {

    // 컨트롤러클래스의 로그를 출력
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    // 게시글 신규 등록에 이미지 업로드
    @PostMapping("/imageUpload")
    public void addImageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload) {
        log.info("<=====ImageUploadController.addImageUpload=====>");

        imageUpload(request, response, upload);
    }

    // 게시글 수정에 이미지 업로드
    @PostMapping("/board/{titleCode}/{seq}/imageUpload")
    public void editImageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload) {
        log.info("<=====ImageUploadController.editImageUpload=====>");

        imageUpload(request, response, upload);
    }

    //MultipartFile 타입은 ckedit에서 upload란 이름으로 저장하게 된다
    public void imageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload) {
        log.info("<=====ImageUploadController.imageUpload=====>");

        OutputStream out = null;
        PrintWriter printWriter = null;

        // 한글깨짐을 방지하기위해 문자셋 설정
        response.setCharacterEncoding("utf-8");

        // 마찬가지로 파라미터로 전달되는 response 객체의 한글 설정
        response.setContentType("text/html; charset=utf-8");

        try {
            // 이미지 이름 세팅
            String uploadFileName = setFileName(upload.getOriginalFilename());

            // 이미지 경로 세팅
            String imgUploadPath = setFilePath(uploadFileName);

            // 파일을 바이트 배열로 변환
            byte[] bytes = upload.getBytes();

            // 서버로 업로드
            // write메소드의 매개값으로 파일의 총 바이트를 매개값으로 준다.
            // 지정된 바이트를 출력 스트립에 쓴다 (출력하기 위해서)
            out = new FileOutputStream(new File(imgUploadPath));
            out.write(bytes);

            // 클라이언트에 결과 표시
            String callback = request.getParameter("CKEditorFuncNum");
            // 서버=>클라이언트로 텍스트 전송(자바스크립트 실행)
            printWriter = response.getWriter();

            String fileUrl = "/images/" + uploadFileName;

            // view에 띄어줌
            printWriter.println("<script>window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + fileUrl
                    + "','이미지가 업로드되었습니다.')" + "</script>");
            printWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    // request에서 이미지 이름 가져오기
    public String getFileName(String fileName, HttpServletRequest request) throws ServletException, IOException {
        log.info("<=====ImageUploadController.getFileName=====>");

        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            Collection<String> headerNames = part.getHeaderNames();

            if (part.getSubmittedFileName() != null && fileName == null) {
                return part.getSubmittedFileName();
            }
        }

        return null;
    }

    // 이미지 이름 세팅
    public String setFileName(String fileName) {
        log.info("<=====ImageUploadController.setFileName=====>");

        // 이름 중복 방지
        UUID uuid = UUID.randomUUID();

        // 업로드한 파일명, 확장자명, 저장될 파일명
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = uuid + "." + ext;

        return uploadFileName;
    }

    // 이미지 경로 세팅
    public String setFilePath(String fileName) {
        log.info("<=====ImageUploadController.setFilePath=====>");

        return System.getProperty("user.dir") + File.separator + "images" + File.separator + fileName;
    }
}
