package rt.marson.syeta.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.Language;
import rt.marson.syeta.dto.LanguageDto;
import rt.marson.syeta.mapper.LanguageMapper;
import rt.marson.syeta.repository.LanguageRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepo languageRepo;
    private final LanguageMapper languageMapper;

    public List<LanguageDto> getAllLanguages() {
        return languageMapper.toLanguageDtos(findAllLanguages());
    }

    public List<Language> findAllLanguages() {
        return languageRepo.findAll();
    }

    public Language findById(Long id) {
        return languageRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Язык с id " + id + " не найден"));
    }
}
