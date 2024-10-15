package rt.marson.syeta.controller.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rt.marson.syeta.dto.file.FileUrlDto;
import rt.marson.syeta.service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class AwsController {
    private final FileService fileService;

    @PostMapping("/uploadAws")
    public List<String> uploadFilesAndGetUrls(@RequestParam("file") MultipartFile[] files) throws ExecutionException, InterruptedException {
        return fileService.uploadFilesAws(files);
    }

    @PostMapping("/uploadAwsSingleFile")
    public FileUrlDto uploadFileAndGetUrl(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.uploadSingleFileAws(file);
    }
}
