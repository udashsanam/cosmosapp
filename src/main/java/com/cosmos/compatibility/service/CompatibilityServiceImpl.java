package com.cosmos.compatibility.service;

import com.cosmos.compatibility.dto.CompatibilityDto;
import com.cosmos.compatibility.entity.Compatibility;
import com.cosmos.compatibility.repo.CompatibilityRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CompatibilityServiceImpl {

    private final CompatibilityRepo compatibilityRepo;
    private final ModelMapper mapper;

    public CompatibilityDto addCompatibilityTestRequest(CompatibilityDto compatibilityDto) {
        Compatibility cmp = compatibilityRepo.save(mapper.map(compatibilityDto, Compatibility.class));
        return mapper.map(cmp, CompatibilityDto.class);
    }

    public List<CompatibilityDto> getALlCompatibilityTest(String deviceId) {
        List<Compatibility> cmp = compatibilityRepo.findAllByDeviceIdOrderByCreatedAtDesc(deviceId);
        return cmp.stream().map(cmpDto -> mapper.map(cmpDto, CompatibilityDto.class)).collect(Collectors.toList());
    }
}
