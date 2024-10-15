package rt.marson.syeta.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.lang.Thread.sleep;

@Service
public class DelayService {
    private static boolean isAlive = false;
    private static final Thread thread = new Thread(() -> {
        isAlive = true;
        while (isAlive) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            HttpEntity<?> entity = new HttpEntity<>(headers);
            restTemplate.exchange("https://syeta.onrender.com/countries/1",  HttpMethod.GET, entity, String.class);
            try {
                sleep(25_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });

    public boolean startDelay() {
        thread.start();
        return true;
    }

    public boolean stopDelay() {
        isAlive = false;
        System.out.println("im dead");
        return true;
    }
}
