package rt.marson.syeta.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {
    @Value("${dropbox.app.key}")
    private String appKey;

    @Value("${dropbox.app.secret}")
    private String appSecret;

    @Value("${dropbox.access.refresh-token}")
    private String refreshToken;

    @Value("${dropbox.access.token}")
    private String accessToken;

    @Bean
    public DbxClientV2 dropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/syeta").build();
        return new DbxClientV2(config, new DbxCredential(accessToken, -1L, refreshToken, appKey, appSecret));
    }
}
