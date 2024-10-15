package rt.marson.syeta.controller.upload;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rt.marson.syeta.dto.file.FileUrlDto;
import rt.marson.syeta.service.FileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class DropBoxController {
    private final FileService fileService;

    @PostMapping("/upload")
    public List<String> uploadFilesAndGetUrls(@RequestParam("file") MultipartFile[] files) throws IOException, DbxException {
        return fileService.uploadFiles(files);
    }

    @PostMapping("/uploadSingleFile")
    public FileUrlDto uploadFileAndGetUrl(@RequestParam("file") MultipartFile file) throws IOException, DbxException {
        return fileService.uploadFile(file);
    }
}
