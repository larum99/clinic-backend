package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.DentalServiceRequest;
import com.dental.clinic.dto.response.DentalServiceResponse;
import com.dental.clinic.exception.BusinessException;
import com.dental.clinic.exception.DuplicateResourceException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.DentalServiceMapper;
import com.dental.clinic.repository.DentalServiceRepository;
import com.dental.clinic.service.DentalService;
import com.dental.clinic.utils.ApplicationConstants;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DentalServiceImpl implements DentalService {

    private final DentalServiceRepository dentalServiceRepository;
    private final DentalServiceMapper dentalServiceMapper;

    public DentalServiceImpl(
            DentalServiceRepository dentalServiceRepository,
            DentalServiceMapper dentalServiceMapper) {

        this.dentalServiceRepository = dentalServiceRepository;
        this.dentalServiceMapper = dentalServiceMapper;
    }

    @Override
    @Transactional
    public DentalServiceResponse createDentalService(DentalServiceRequest request) {

        validateServiceCreation(request);

        com.dental.clinic.entity.DentalService dentalService =
                dentalServiceMapper.toEntity(request);

        com.dental.clinic.entity.DentalService savedDentalService =
                dentalServiceRepository.save(dentalService);

        return dentalServiceMapper.toResponse(savedDentalService);
    }

    @Override
    public DentalServiceResponse findDentalServiceById(Long id) {

        com.dental.clinic.entity.DentalService dentalService = findServiceEntityById(id);

        return dentalServiceMapper.toResponse(dentalService);
    }

    @Override
    public Page<DentalServiceResponse> findAllDentalServices(Pageable pageable) {
        return dentalServiceRepository.findAll(pageable)
                .map(dentalServiceMapper::toResponse);
    }

    @Override
    @Transactional
    public DentalServiceResponse updateDentalService(
            Long id,
            DentalServiceRequest request) {

        com.dental.clinic.entity.DentalService dentalService = findServiceEntityById(id);

        validateServiceUpdate(dentalService, request);

        dentalService.setName(request.name());
        dentalService.setDescription(request.description());
        dentalService.setDurationMinutes(request.durationMinutes());
        dentalService.setPrice(request.price());
        dentalService.setStatus(request.status());

        com.dental.clinic.entity.DentalService updatedDentalService =
                dentalServiceRepository.save(dentalService);

        return dentalServiceMapper.toResponse(updatedDentalService);
    }

    @Override
    @Transactional
    public void deleteDentalService(Long id) {

        com.dental.clinic.entity.DentalService dentalService = findServiceEntityById(id);

        dentalServiceRepository.delete(dentalService);
    }

    private com.dental.clinic.entity.DentalService findServiceEntityById(Long id) {

        return dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SERVICE_NOT_FOUND.formatted(id)
                ));
    }

    private void validateServiceCreation(DentalServiceRequest request) {

        validateServiceNameDoesNotExist(request.name());
        validateDuration(request.durationMinutes());
        validatePrice(request.price());
        validateStatus(request.status());
    }

    private void validateServiceUpdate(
            com.dental.clinic.entity.DentalService dentalService,
            DentalServiceRequest request) {

        validateServiceNameForUpdate(
                dentalService,
                request.name());

        validateDuration(request.durationMinutes());
        validatePrice(request.price());
        validateStatus(request.status());
    }

    private void validateServiceNameDoesNotExist(String name) {

        if (dentalServiceRepository.existsByName(name)) {

            throw new DuplicateResourceException(
                    MessageConstants.SERVICE_NAME_DUPLICATE.formatted(name)
            );
        }
    }

    private void validateServiceNameForUpdate(
            com.dental.clinic.entity.DentalService dentalService,
            String name) {

        if (!dentalService.getName().equals(name)) {
            validateServiceNameDoesNotExist(name);
        }
    }

    private void validateDuration(Short durationMinutes) {

        if (durationMinutes <= 0) {

            throw new BusinessException(
                    MessageConstants.INVALID_SERVICE_DURATION
            );
        }
    }

    private void validatePrice(BigDecimal price) {

        if (price != null && price.signum() < 0) {

            throw new BusinessException(
                    MessageConstants.INVALID_SERVICE_PRICE
            );
        }
    }

    private void validateStatus(String status) {

        if (!ApplicationConstants.STATUS_ACTIVE.equals(status)
                && !ApplicationConstants.STATUS_INACTIVE.equals(status)) {

            throw new BusinessException(
                    MessageConstants.INVALID_SERVICE_STATUS
            );
        }
    }
}