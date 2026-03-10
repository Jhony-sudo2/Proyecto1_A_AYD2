package com.ayd2.congress.services.certificate;

import java.util.List;

import com.ayd2.congress.dtos.certificate.CertificateResponse;
import com.ayd2.congress.exceptions.NotFoundException;

public interface CertificateService {
    List<CertificateResponse> getCertificatesByUserId(Long id,Long congressId) throws NotFoundException;

}
