package rt.marson.syeta.controller.google.places;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.google.places.AutocompletePredictionDto;
import rt.marson.syeta.dto.google.places.PlaceDetailsDto;
import rt.marson.syeta.dto.auth.AuthUserId;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/places")
public class GooglePlacesController {
    private final GeoApiContext geoApiContext;
    private final SessionTokenStore tokenStore;

    public GooglePlacesController(@Value("${google.places.api.key}") String apiKey, SessionTokenStore tokenStore) {
        this.geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        this.tokenStore = tokenStore;
    }

    @GetMapping("/autocomplete")
    public List<AutocompletePredictionDto> autocomplete(@RequestParam String input,
                                                        @RequestParam(required = false) String location,
                                                        @RequestParam String language,
                                                        @AuthUserId Long userId) throws IOException, InterruptedException, ApiException {
        SessionToken userSessionToken = tokenStore.getSessionToken(userId);
        if (input == null || input.isBlank()) {
            return new ArrayList<>();
        }

        if (location == null || location.isBlank()) {
            if (userSessionToken != null && !userSessionToken.isExpired()) {
                userSessionToken.setInput(input);
                userSessionToken.setLanguage(language);
            } else {
                // Если токен истек или его нет, генерируем новый
                userSessionToken = new SessionToken(UUID.randomUUID(), LocalDateTime.now(), input, null, language);
                tokenStore.saveSessionToken(userId, userSessionToken);
            }

            // Отправляем запрос на автозаполнение с текущим токеном
            return Arrays.stream(
                            PlacesApi
                                    .placeAutocomplete(geoApiContext, input, new PlaceAutocompleteRequest.SessionToken(userSessionToken.getSessionToken()))
                                    .language(language)
                                    .await())
                    .map(prediction -> new AutocompletePredictionDto(
                            prediction.placeId,
                            prediction.structuredFormatting.mainText,
                            prediction.structuredFormatting.secondaryText,
                            null)
                    )
                    .toList();
        } else {
            LatLng latLng = convertStringToLatLng(location);

            if (userSessionToken != null && !userSessionToken.isExpired()) {
                // Обновляем input и location в существующем токене
                userSessionToken.setInput(input);
                userSessionToken.setLocation(latLng);
                userSessionToken.setLanguage(language);
            } else {
                // Если токен истек или его нет, генерируем новый
                userSessionToken = new SessionToken(UUID.randomUUID(), LocalDateTime.now(), input, latLng, language);
                tokenStore.saveSessionToken(userId, userSessionToken);
            }

            // Отправляем запрос на автозаполнение с текущим токеном
            return Arrays.stream(
                        PlacesApi
                            .placeAutocomplete(geoApiContext, input, new PlaceAutocompleteRequest.SessionToken(userSessionToken.getSessionToken()))
                            .radius(30_000)
                            .location(userSessionToken.getLocation())
                            .origin(userSessionToken.getLocation())
                            .language(userSessionToken.getLanguage())
                            .await()
                    )
                    .map(prediction -> new AutocompletePredictionDto(
                                            prediction.placeId,
                                            prediction.structuredFormatting.mainText,
                                            prediction.structuredFormatting.secondaryText,
                                            prediction.distanceMeters
                            )
                    )
                    .toList();
        }
    }

    private LatLng convertStringToLatLng(String location) {
        // Разделяем строку по запятой
        String[] parts = location.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат координат");
        }

        try {
            // Преобразуем строки в числа (широта и долгота)
            double lat = Double.parseDouble(parts[0].trim());
            double lng = Double.parseDouble(parts[1].trim());
            return new LatLng(lat, lng);  // Создаем объект LatLng
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат чисел для координат");
        }
    }

        @GetMapping("/details")
    public PlaceDetailsDto placeDetails(@RequestParam(name = "place_id") String placeId,
                                        @RequestParam String language,
                                        @AuthUserId Long userId) throws IOException, InterruptedException, ApiException {
        SessionToken userSessionToken = tokenStore.getSessionToken(userId);
        UUID sessionToken;

        if (userSessionToken != null) {
            sessionToken = userSessionToken.getSessionToken();
        } else {
            sessionToken = UUID.randomUUID();
        }

        var placeDetails = PlacesApi.placeDetails(geoApiContext, placeId)
                .sessionToken(new PlaceAutocompleteRequest.SessionToken(sessionToken))
                .language(language)
                .fields(
                        PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                        PlaceDetailsRequest.FieldMask.GEOMETRY,
                        PlaceDetailsRequest.FieldMask.NAME,
                        PlaceDetailsRequest.FieldMask.VICINITY)
                .await();

        return new PlaceDetailsDto(
                placeDetails.formattedAddress,
                placeDetails.name,
                placeDetails.geometry.location.lat + ", " + placeDetails.geometry.location.lng,
                placeDetails.vicinity
        );
    }
}
