package rt.marson.syeta.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.LanguageDto;
import rt.marson.syeta.dto.TagDto;
import rt.marson.syeta.dto.TypeDto;
import rt.marson.syeta.service.user.LanguageService;
import rt.marson.syeta.service.user.PhoneCodeService;
import rt.marson.syeta.service.TagService;
import rt.marson.syeta.service.TypeService;

import java.util.List;

@RestController
@RequestMapping("/const")
@RequiredArgsConstructor
@Tag(name = "Справочники")
@SecurityRequirements
public class ConstController {
    private final TagService tagService;
    private final TypeService typeService;
    private final LanguageService langService;
    private final PhoneCodeService phoneCodeService;

    @GetMapping("/tags")
    public List<TagDto> getTags(@RequestParam(name = "typeId", required = false) Short typeId) {
        return tagService.getAllByTypeId(typeId);
    }

    @GetMapping("/types")
    public List<TypeDto> getTypes() {
        return typeService.getAllTypes();
    }

    @GetMapping("/languages")
    public List<LanguageDto> getLanguages() {
        return langService.getAllLanguages();
    }
/*
    @GetMapping("/phoneCodes")
    public List<PhoneCodeDto> getPhoneCodes() {
        return phoneCodeService.getAllPhoneCodes();
    }*/
}
