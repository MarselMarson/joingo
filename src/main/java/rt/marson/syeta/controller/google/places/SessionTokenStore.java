package rt.marson.syeta.controller.google.places;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionTokenStore {
    private final ConcurrentHashMap<Long, SessionToken> sessionTokens = new ConcurrentHashMap<>();

    public SessionToken getSessionToken(Long userId) {
        return sessionTokens.get(userId);
    }

    public void saveSessionToken(Long userId, SessionToken sessionToken) {
        sessionTokens.put(userId, sessionToken);
    }

    public void removeSessionToken(Long userId) {
        sessionTokens.remove(userId);
    }
}
