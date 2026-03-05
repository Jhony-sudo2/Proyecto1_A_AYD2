package com.ayd2.congress.services.Congress;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.dtos.Congress.UpdateCongress;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.CongressMapper;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Congress.LocationEntity;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.repositories.CongressRepository;
import com.ayd2.congress.services.Location.LocationService;
import com.ayd2.congress.services.Organization.OrganizationService;
import com.ayd2.congress.services.SystemConfig.SystemConfigService;
import com.ayd2.congress.services.aws.S3Service;

@Service
public class CongressServiceImpl implements CongressService{
    private final CongressRepository congressRepository;
    private final CongressMapper congressMapper;
    private final OrganizationService organizationService;
    private final LocationService locationService;
    private final SystemConfigService systemConfigService;
    private final S3Service s3Service;

    @Autowired
    public CongressServiceImpl(CongressRepository congressRepository, CongressMapper congressMapper,
            OrganizationService organizationService, LocationService locationService,SystemConfigService systemConfigService, S3Service s3Service) {
        this.congressRepository = congressRepository;
        this.congressMapper = congressMapper;
        this.organizationService = organizationService;
        this.locationService = locationService;
        this.systemConfigService = systemConfigService;
        this.s3Service = s3Service;
    }

    @Override
    public CongressResponse create(NewCongressRequest request) throws NotFoundException, InvalidDateRangeException, InvalidPriceException, IOException, DuplicatedEntityException {
        OrganizationEntity organization = organizationService.getById(request.getOrganizationId());
        LocationEntity location = locationService.getLocationById(request.getLocationId());

        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate  = request.getEndDate();
        LocalDateTime endCallDate = request.getEndCallDate();
        Double minPirceCongress = systemConfigService.getConfiguration().getPrice();
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("START DATE MUST BE BEFORE END DATE");
        }
        if(!endCallDate.isBefore(startDate)){
            throw new InvalidDateRangeException("DATE INVALID TO CALL TO APPLICATION");
        }
        if (request.getPrice() < minPirceCongress) {
            throw new InvalidPriceException("The minimun price must be: " + minPirceCongress);
        }
        boolean isLocationOccupied = congressRepository.isLocationOccupied(location.getId(), startDate, endDate);
        if (isLocationOccupied) {
            throw new DuplicatedEntityException("El lugar seleccionado ya está ocupado en las fechas indicadas.");
        }

        String imagen = s3Service.uploadBase64(request.getImageUrl(), "congress_"+request.getName());
        CongressEntity newCongress = congressMapper.toEntity(request);
        newCongress.setLocation(location);
        newCongress.setOrganization(organization);
        newCongress.setImageUrl(imagen);
        congressRepository.save(newCongress);
        return congressMapper.toResponse(newCongress);
    }

    
    @Override
    public CongressResponse update(UpdateCongress updateCongress,Long id) throws NotFoundException, InvalidDateRangeException {
        LocationEntity location = locationService.getLocationById(updateCongress.getLocationId());

        CongressEntity congressToUpdate = getById(id);
        LocalDateTime startCongressDate = congressToUpdate.getStartDate();
        
        if (updateCongress.getEndCallDate().isAfter(startCongressDate)) {
            throw new InvalidDateRangeException("DATE INVALID TO CALL TO APPLICATION");
        }  
        
        congressToUpdate.setName(updateCongress.getName());
        congressToUpdate.setDescription(updateCongress.getDescription());
        congressToUpdate.setLocation(location);
        congressRepository.save(congressToUpdate);
        return congressMapper.toResponse(congressToUpdate);
    }

    @Override
    public CongressEntity getById(Long id) throws NotFoundException {
        return congressRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Congress not found"));
    }

    @Override
    public List<CongressResponse> getAllByOrganizationId(Long organizationId) throws NotFoundException {
        organizationService.getById(organizationId);
        List<CongressEntity> list = congressRepository.findAllByOrganizationId(organizationId);
        return congressMapper.toCongressResponseList(list);
    }

    @Override
    public CongressResponse getByIdResponse(Long id) throws NotFoundException {
        CongressEntity entity = getById(id);
        return congressMapper.toResponse(entity);
    }

    @Override
    public List<CongressResponse> getAllCongress() {
        List<CongressEntity> response = congressRepository.findAll();
        return congressMapper.toCongressResponseList(response);
    }
    
}
