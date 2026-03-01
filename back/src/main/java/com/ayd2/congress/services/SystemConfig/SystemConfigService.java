package com.ayd2.congress.services.SystemConfig;

import com.ayd2.congress.dtos.SysConfig.SysConfigResponse;
import com.ayd2.congress.dtos.SysConfig.SysConfigUpdate;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.SystemConfigEntity;

public interface SystemConfigService {
    SysConfigResponse update(SysConfigUpdate updateRequest) throws NotFoundException,InvalidPriceException;
    SystemConfigEntity getConfiguration() throws NotFoundException;
    SysConfigResponse getConfigResponse() throws NotFoundException;
    SysConfigResponse create();
}
