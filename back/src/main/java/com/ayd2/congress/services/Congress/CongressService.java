package com.ayd2.congress.services.Congress;

import java.util.List;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.dtos.Congress.UpdateCongress;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Congress.CongressEntity;

public interface CongressService {
    CongressResponse create(NewCongressRequest request) throws NotFoundException,InvalidDateRangeException,InvalidPriceException;
    CongressResponse update(UpdateCongress updateCongress,Long id) throws NotFoundException;
    CongressEntity getById(Long id) throws NotFoundException;
    CongressResponse getByIdResponse(Long id) throws NotFoundException;
    List<CongressResponse> getAllByOrganizationId(Long organizationId) throws NotFoundException;
}
