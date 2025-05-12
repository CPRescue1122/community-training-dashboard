package com.rescue.communitytraining.converters;

import com.rescue.communitytraining.entity.District;
import com.rescue.communitytraining.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDistrictConverter implements Converter<String, District> {
    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public District convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return districtRepository.findById(Long.parseLong(source)).orElse(null);
    }
}

