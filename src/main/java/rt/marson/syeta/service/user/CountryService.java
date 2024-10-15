package rt.marson.syeta.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.Country;
import rt.marson.syeta.dto.CountryDto;
import rt.marson.syeta.mapper.CountryMapper;
import rt.marson.syeta.repository.CountryRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepo countryRepo;
    private final CountryMapper countryMapper;

    public List<CountryDto> getAllCountries() {
        return countryMapper.toUserDots(countryRepo.findAll());
    }

    public Country findCountryById(Long id) {
        return countryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Страна с id " + id + " не найдена"));
    }

    public CountryDto getCountryById(Long countryId) {
        return countryMapper.toDto(findCountryById(countryId));
    }

    public Country findCountryByName(String countryName) {
        return countryRepo.findByName(countryName)
                .orElseThrow(() -> new EntityNotFoundException("Страна с именем " + countryName + " не найдена"));
    }
}
