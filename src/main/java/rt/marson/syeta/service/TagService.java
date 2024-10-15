package rt.marson.syeta.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.Tag;
import rt.marson.syeta.dto.TagDto;
import rt.marson.syeta.mapper.TagMapper;
import rt.marson.syeta.repository.TagRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepo tagRepo;
    private final TagMapper tagMapper;

    public List<TagDto> getAllByTypeId(Short typeId) {
        if (typeId != null) {
            return tagMapper.toTagDtos(findAllByTypeId(typeId));
        } else {
            return tagMapper.toTagDtos(findAll());
        }
    }

    public List<Tag> findAllByTypeId(Short typeId) {
        return tagRepo.findAllByTypesId(typeId);
    }

    public List<Tag> findAll() {
        return tagRepo.findAll();
    }

    public Tag findById(Integer id) {
        return tagRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тэг с id " + id + " не найден"));
    }
}
