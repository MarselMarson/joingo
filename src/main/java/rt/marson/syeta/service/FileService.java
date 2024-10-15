package rt.marson.syeta.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.RequestedLinkAccessLevel;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import jakarta.persistence.EntityNotFoundException;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rt.marson.syeta.entity.File;
import rt.marson.syeta.dto.file.FileUrlDto;
import rt.marson.syeta.repository.FileRepo;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${aws.s3.bucket}")
    private String bucketName;

    //private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private final FileRepo fileRepo;
    private final DbxClientV2 dbxClient;
    private final S3Client s3Client;

    public List<String> uploadFiles(MultipartFile[] files) throws IOException, DbxException {
        List<String> fileDto = new ArrayList<>();

        for (MultipartFile file : files) {
            fileDto.add(uploadFile(file).getUrl());
        }

        return fileDto;
    }

    public FileUrlDto uploadFile(MultipartFile file) throws IOException, DbxException {
        String fileNameExt = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = generateUniqueFileName();
        String fileUrl = uploadFileAndGetUrl(file, fileName);
        String sharedLink = shareUrlAndGetSharedLink(fileUrl);
        saveFile(fileName + "." + fileNameExt, sharedLink);

        return new FileUrlDto(sharedLink);
    }

    private String shareUrlAndGetSharedLink(String fileUrl) throws DbxException {
        SharedLinkSettings settings = SharedLinkSettings.newBuilder()
                .withAccess(RequestedLinkAccessLevel.VIEWER)
                .withRequestedVisibility(RequestedVisibility.PUBLIC)
                .build();

        SharedLinkMetadata sharedLinkMetadata = dbxClient.sharing().createSharedLinkWithSettings(fileUrl, settings);
        String sharedLink = sharedLinkMetadata.getUrl();
        return sharedLink.replace("dl=0", "raw=1");
    }

    private String uploadFileAndGetUrl(MultipartFile file, String fileName) throws IOException, DbxException {
        @Cleanup InputStream in = file.getInputStream();
        String fileNameExt = FilenameUtils.getExtension(file.getOriginalFilename());

        FileMetadata metadata = dbxClient.files().uploadBuilder("/" + fileName + "." + fileNameExt)
                .withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(in);
        return metadata.getPathLower();
    }

    private void saveFile(String fileName, String url) {
        fileRepo.save(
                File.builder()
                        .name(fileName)
                        .url(url)
                        .build()
        );
    }

    private static String generateUniqueFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String getNameFromUrl(String url) {
        // Ищем индекс ".com/" и берем всё, что идет после него
        int index = url.indexOf(".com/");
        if (index != -1) {
            return url.substring(index + 5); // 5 - это длина ".com/"
        }
        return null; // Если ".com/" не найден

        /*String regex = "/([^/?]*)\\?";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        String name = "";

        if (matcher.find()) {
            name = matcher.group(1);
        }
        return name;*/
    }

    public File getByFileName(String name) {
        return fileRepo.findByName(name).orElseThrow(() -> new EntityNotFoundException("файл с именем " + name + " не найден"));
    }

    public FileUrlDto uploadSingleFileAws(MultipartFile file) throws IOException {
        return new FileUrlDto(uploadFileAws(file));
    }

    public String uploadFileAws(MultipartFile file) throws IOException {
        String fileName = generateUniqueFileName();
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .contentType("image/jpeg")
                        .key(fileName)
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );
        String url = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
        saveFile(fileName, url);
        return url;
    }

    public List<String> uploadFilesAws(MultipartFile[] files) throws InterruptedException, ExecutionException {
        List<Callable<String>> uploadTasks = getCallables(files);

        //List<Future<String>> futures = executorService.invokeAll(uploadTasks);
        List<Future<String>> futures = threadPoolTaskExecutor.getThreadPoolExecutor().invokeAll(uploadTasks);
        List<String> uploadedUrls = new ArrayList<>();

        for (Future<String> future : futures) {
            uploadedUrls.add(future.get());
        }

        return uploadedUrls;
    }

    @NotNull
    private List<Callable<String>> getCallables(MultipartFile[] files) {
        List<Callable<String>> uploadTasks = new ArrayList<>();

        for (MultipartFile file : files) {
            uploadTasks.add(() -> uploadFileAws(file));
        }
        return uploadTasks;
    }
}
