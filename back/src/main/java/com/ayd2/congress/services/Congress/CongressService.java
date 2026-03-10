package com.ayd2.congress.services.Congress;

import java.io.IOException;
import java.util.List;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCommitteeRequest;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.dtos.Congress.UpdateCongress;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Congress.CongressEntity;

public interface CongressService {
    CongressResponse create(NewCongressRequest request) throws NotFoundException,InvalidDateRangeException,InvalidPriceException,IOException,DuplicatedEntityException;
    CongressResponse update(UpdateCongress updateCongress,Long id) throws NotFoundException,InvalidDateRangeException;
    CongressEntity getById(Long id) throws NotFoundException;
    CongressResponse getByIdResponse(Long id) throws NotFoundException;
    List<CongressResponse> getAllByOrganizationId(Long organizationId) throws NotFoundException;
    List<CongressResponse> getAllCongress();
    void createScientificCommittee(Long congressId,NewCommitteeRequest request) throws NotFoundException,DuplicatedEntityException;
    List<UserResponse> getCommitteByCongressId(Long id) throws NotFoundException;
    void removeCommitteeMember(Long congressId,Long userId) throws NotFoundException;
}
