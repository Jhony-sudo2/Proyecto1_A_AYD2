package com.ayd2.congress.services.SystemConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.SysConfig.SysConfigResponse;
import com.ayd2.congress.dtos.SysConfig.SysConfigUpdate;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.ConfigMapper;
import com.ayd2.congress.models.SystemConfigEntity;
import com.ayd2.congress.repositories.SystemConfigRepository;

@Service
public class SystemConfigServiceImpl implements SystemConfigService{
    private final SystemConfigRepository repository;
    private final ConfigMapper mapper;
    @Autowired
    public SystemConfigServiceImpl(SystemConfigRepository repository,ConfigMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public SysConfigResponse update(SysConfigUpdate updateRequest) throws NotFoundException, InvalidPriceException {
        SystemConfigEntity configToUpdate = getConfiguration();
        Double minPrice = getConfiguration().getPrice();
        if (updateRequest.getPrice() < minPrice) {
            throw new InvalidPriceException("The price cannot be");
        }
        configToUpdate.setPercentage(updateRequest.getPercentage());
        configToUpdate.setPrice(updateRequest.getPrice());
        repository.save(configToUpdate);
        
        return mapper.toResponse(configToUpdate);
    }

    @Override
    public SystemConfigEntity getConfiguration() throws NotFoundException {
        return repository.findById(1L)
            .orElseThrow(()-> new NotFoundException("CONFIGURATION NOT FOUND"));
    }

    @Override
    public SysConfigResponse getConfigResponse() throws NotFoundException {
        SystemConfigEntity entity = getConfiguration();
        return mapper.toResponse(entity);
    }

    @Override
    public SysConfigResponse create() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }
    
}
