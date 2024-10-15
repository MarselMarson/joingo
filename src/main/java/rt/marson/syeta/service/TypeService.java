package rt.marson.syeta.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.Type;
import rt.marson.syeta.dto.TypeDto;
import rt.marson.syeta.mapper.TypeMapper;
import rt.marson.syeta.repository.TypeRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeService {
    private final TypeRepo typeRepo;
    private final TypeMapper typeMapper;

    public Type findTypeById(Short id) {
        return typeRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Тип ивента с id " + id + " не найден"));
    }

    public List<TypeDto> getAllTypes() {
        return typeMapper.toTypeDtos(findAllTypes());
    }

    public List<Type> findAllTypes() {
        return typeRepo.findAll();
    }
}
