package rt.marson.syeta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.CountryDto;
import rt.marson.syeta.service.user.CountryService;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Tag(name = "Страна")
@SecurityRequirements
public class CountryController {
    private final CountryService countryService;

    @GetMapping()
    @Operation(summary = "Список всех стран")
    List<CountryDto> getCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{countryId}")
    @Operation(summary = "Получить страну по id")
    CountryDto getCountryById(@PathVariable Long countryId) {
        return countryService.getCountryById(countryId);
    }
}
