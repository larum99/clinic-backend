package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.AppointmentRequest;
import com.clinic.clinic.dto.response.AppointmentResponse;
import com.clinic.clinic.entity.Appointment;
import com.clinic.clinic.entity.AppointmentHistory;
import com.clinic.clinic.entity.DentalService;
import com.clinic.clinic.entity.Patient;
import com.clinic.clinic.entity.Specialist;
import com.clinic.clinic.entity.SpecialistSchedule;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.enums.AppointmentStatus;
import com.clinic.clinic.exception.BusinessException;
import com.clinic.clinic.exception.ResourceNotFoundException;
import com.clinic.clinic.mapper.AppointmentMapper;
import com.clinic.clinic.repository.AppointmentHistoryRepository;
import com.clinic.clinic.repository.AppointmentRepository;
import com.clinic.clinic.repository.DentalServiceRepository;
import com.clinic.clinic.repository.PatientRepository;
import com.clinic.clinic.repository.SpecialistRepository;
import com.clinic.clinic.repository.SpecialistScheduleRepository;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.service.AppointmentService;
import com.clinic.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final AppointmentHistoryRepository appointmentHistoryRepository;
    private final PatientRepository patientRepository;
    private final SpecialistRepository specialistRepository;
    private final DentalServiceRepository dentalServiceRepository;
    private final SpecialistScheduleRepository specialistScheduleRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentHistoryRepository appointmentHistoryRepository,
            PatientRepository patientRepository,
            SpecialistRepository specialistRepository,
            DentalServiceRepository dentalServiceRepository,
            SpecialistScheduleRepository specialistScheduleRepository,
            UserRepository userRepository,
            AppointmentMapper appointmentMapper
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentHistoryRepository = appointmentHistoryRepository;
        this.patientRepository = patientRepository;
        this.specialistRepository = specialistRepository;
        this.dentalServiceRepository = dentalServiceRepository;
        this.specialistScheduleRepository = specialistScheduleRepository;
        this.userRepository = userRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, Long createdByUserId) {
        Patient patient = findPatientEntityById(request.patientId());
        Specialist specialist = findSpecialistEntityById(request.specialistId());
        DentalService service = request.serviceId() != null
                ? findServiceEntityById(request.serviceId())
                : null;
        User createdBy = findUserById(createdByUserId);

        LocalDateTime startDatetime = request.startDatetime();

        if (startDatetime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(MessageConstants.APPOINTMENT_PAST_DATETIME);
        }

        LocalDateTime endDatetime = calculateEndDatetime(startDatetime);

        validateScheduleAvailability(specialist, startDatetime);
        validateNoSpecialistOverlap(specialist.getId(), startDatetime, endDatetime, null);
        validateNoPatientOverlap(patient.getId(), startDatetime, endDatetime, null);

        Appointment appointment = appointmentMapper.toEntity(request, patient, specialist, service, createdBy);
        appointment.setEndDatetime(endDatetime);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        logStatusChange(savedAppointment, null, AppointmentStatus.CONFIRMADA, null, createdBy);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse findAppointmentById(Long id) {
        return appointmentMapper.toResponse(findAppointmentEntityById(id));
    }

    @Override
    public Page<AppointmentResponse> findAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(appointmentMapper::toResponse);
    }

    @Override
    public Page<AppointmentResponse> findMyAppointments(Long userId, Pageable pageable) {
        Patient patient = findPatientByUserId(userId);
        return appointmentRepository.findByPatientId(patient.getId(), pageable)
                .map(appointmentMapper::toResponse);
    }

    @Override
    @Transactional
    public AppointmentResponse createMyAppointment(AppointmentRequest request, Long userId) {
        Patient patient = findPatientByUserId(userId);

        if (request.patientId() != null && !request.patientId().equals(patient.getId())) {
            throw new BusinessException(MessageConstants.APPOINTMENT_PATIENT_MISMATCH);
        }

        return createAppointment(
                new AppointmentRequest(
                        patient.getId(),
                        request.specialistId(),
                        request.serviceId(),
                        request.startDatetime(),
                        request.reason(),
                        request.notes(),
                        request.status()
                ),
                userId
        );
    }

    @Override
    @Transactional
    public AppointmentResponse cancelMyAppointment(Long id, Long userId) {
        Patient patient = findPatientByUserId(userId);
        Appointment appointment = findAppointmentEntityById(id);

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new ResourceNotFoundException(MessageConstants.APPOINTMENT_NOT_FOUND.formatted(id));
        }

        if (appointment.getStatus() != AppointmentStatus.CANCELADA) {
            User changedBy = findUserById(userId);
            logStatusChange(appointment, appointment.getStatus(), AppointmentStatus.CANCELADA, null, changedBy);
            appointment.setStatus(AppointmentStatus.CANCELADA);
            appointmentRepository.save(appointment);
        }

        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request, Long changedByUserId) {
        Appointment appointment = findAppointmentEntityById(id);

        Patient patient = findPatientEntityById(request.patientId());
        Specialist specialist = findSpecialistEntityById(request.specialistId());
        DentalService service = request.serviceId() != null
                ? findServiceEntityById(request.serviceId())
                : null;

        LocalDateTime startDatetime = request.startDatetime();
        LocalDateTime endDatetime = calculateEndDatetime(startDatetime);

        validateScheduleAvailability(specialist, startDatetime);
        validateNoSpecialistOverlap(specialist.getId(), startDatetime, endDatetime, id);
        validateNoPatientOverlap(patient.getId(), startDatetime, endDatetime, id);

        appointment.setPatient(patient);
        appointment.setSpecialist(specialist);
        appointment.setService(service);
        appointment.setStartDatetime(startDatetime);
        appointment.setEndDatetime(endDatetime);
        appointment.setReason(request.reason());
        appointment.setNotes(request.notes());

        User changedBy = findUserById(changedByUserId);

        AppointmentStatus nextStatus = parseStatus(request.status());
        if (nextStatus != null && nextStatus != appointment.getStatus()) {
            logStatusChange(appointment, appointment.getStatus(), nextStatus, null, changedBy);
            appointment.setStatus(nextStatus);
        }

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.delete(findAppointmentEntityById(id));
    }

    private Appointment findAppointmentEntityById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.APPOINTMENT_NOT_FOUND.formatted(id)
                ));
    }

    private Patient findPatientEntityById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PATIENT_NOT_FOUND.formatted(id)
                ));
    }

    private Patient findPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PATIENT_NOT_FOUND_FOR_USER.formatted(userId)
                ));
    }

    private Specialist findSpecialistEntityById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SPECIALIST_NOT_FOUND.formatted(id)
                ));
    }

    private DentalService findServiceEntityById(Long id) {
        return dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SERVICE_NOT_FOUND.formatted(id)
                ));
    }

    private User findUserById(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND.formatted(id)
                ));
    }

    private LocalDateTime calculateEndDatetime(LocalDateTime startDatetime) {
        return startDatetime.plusMinutes(DEFAULT_APPOINTMENT_DURATION_MINUTES);
    }

    private void validateScheduleAvailability(Specialist specialist, LocalDateTime startDatetime) {
        List<SpecialistSchedule> schedules = specialistScheduleRepository
                .findBySpecialistIdAndDayOfWeek(
                        specialist.getId(),
                        (short) startDatetime.getDayOfWeek().getValue()
                ).stream()
                .filter(schedule -> Boolean.TRUE.equals(schedule.getActive()))
                .toList();

        if (schedules.isEmpty()) {
            return;
        }

        LocalTime startTime = startDatetime.toLocalTime();
        boolean covered = schedules.stream().anyMatch(schedule ->
                !startTime.isBefore(schedule.getStartTime())
                        && !startTime
                        .plusMinutes(DEFAULT_APPOINTMENT_DURATION_MINUTES)
                        .isAfter(schedule.getEndTime())
        );

        if (!covered) {
            throw new BusinessException(MessageConstants.APPOINTMENT_SCHEDULE_UNAVAILABLE);
        }
    }

    private void validateNoSpecialistOverlap(Long specialistId, LocalDateTime start, LocalDateTime end, Long excludedAppointmentId) {
        if (appointmentRepository.existsOverlappingAppointmentForSpecialist(
                specialistId, start, end, AppointmentStatus.CANCELADA, excludedAppointmentId)) {
            throw new BusinessException(MessageConstants.APPOINTMENT_SPECIALIST_CONFLICT);
        }
    }

    private void validateNoPatientOverlap(Long patientId, LocalDateTime start, LocalDateTime end, Long excludedAppointmentId) {
        if (appointmentRepository.existsOverlappingAppointmentForPatient(
                patientId, start, end, AppointmentStatus.CANCELADA, excludedAppointmentId)) {
            throw new BusinessException(MessageConstants.APPOINTMENT_PATIENT_CONFLICT);
        }
    }

    private void logStatusChange(
            Appointment appointment,
            AppointmentStatus previousStatus,
            AppointmentStatus newStatus,
            String comment,
            User changedBy
    ) {
        AppointmentHistory history = new AppointmentHistory();
        history.setAppointment(appointment);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setComment(comment);
        history.setChangedBy(changedBy);
        appointmentHistoryRepository.save(history);
    }

    private AppointmentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String normalized = status.trim().toUpperCase().replace(' ', '_');
        try {
            return AppointmentStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(MessageConstants.APPOINTMENT_INVALID_STATUS.formatted(status));
        }
    }
}
