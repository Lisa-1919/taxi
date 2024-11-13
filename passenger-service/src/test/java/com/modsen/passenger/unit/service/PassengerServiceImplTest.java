package com.modsen.passenger.unit.service;

import com.modsen.passenger.dto.PagedResponsePassengerList;
import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.entity.Passenger;
import com.modsen.passenger.mapper.PassengerMapper;
import com.modsen.passenger.repo.PassengerRepository;
import com.modsen.passenger.service.PassengerServiceImpl;
import com.modsen.passenger.util.ExceptionMessages;
import com.modsen.passenger.util.PassengerTestEntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private PassengerMapper passengerMapper;
    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger testPassenger;
    private RequestPassenger testRequestPassenger;
    private ResponsePassenger testResponsePassenger;
    private Long passengerId;

    @BeforeEach
    void setUp() {
        passengerId = PassengerTestEntityUtils.DEFAULT_PASSENGER_ID;
        testPassenger = PassengerTestEntityUtils.createTestPassenger();
        testRequestPassenger = PassengerTestEntityUtils.createTestRequestPassenger();
        testResponsePassenger = PassengerTestEntityUtils.createTestResponsePassenger();
    }

    @Nested
    class AddPassengerTests {

        @Test
        void addPassengerOk() {
            when(passengerRepository.existsByEmail(testRequestPassenger.email())).thenReturn(false);
            when(passengerRepository.existsByPhoneNumber(testRequestPassenger.phoneNumber())).thenReturn(false);
            when(passengerMapper.requestPassengerToPassenger(testRequestPassenger)).thenReturn(testPassenger);
            when(passengerRepository.save(testPassenger)).thenReturn(testPassenger);
            when(passengerMapper.passengerToResponsePassenger(testPassenger)).thenReturn(testResponsePassenger);

            ResponsePassenger result = passengerService.addPassenger(testRequestPassenger);

            assertEquals(testResponsePassenger, result);
            verify(passengerRepository).save(testPassenger);
            verify(passengerMapper).passengerToResponsePassenger(testPassenger);
        }

        @Test
        void addPassengerDuplicateEmail() {
            when(passengerRepository.existsByEmail(testRequestPassenger.email())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> passengerService.addPassenger(testRequestPassenger));

            verify(passengerRepository).existsByEmail(testRequestPassenger.email());
            verifyNoMoreInteractions(passengerRepository, passengerMapper);
        }

        @Test
        void addPassengerDuplicatePhoneNumber() {
            when(passengerRepository.existsByEmail(testRequestPassenger.email())).thenReturn(false);
            when(passengerRepository.existsByPhoneNumber(testRequestPassenger.phoneNumber())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> passengerService.addPassenger(testRequestPassenger));

            verify(passengerRepository).existsByEmail(testRequestPassenger.email());
            verify(passengerRepository).existsByPhoneNumber(testRequestPassenger.phoneNumber());
            verifyNoMoreInteractions(passengerRepository, passengerMapper);
        }

    }

    @Nested
    class EditPassengerTests {

        @Test
        void editPassengerOk() {
            RequestPassenger requestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();
            Passenger updatedPassenger = PassengerTestEntityUtils.createUpdatedPassenger();
            ResponsePassenger responsePassenger = PassengerTestEntityUtils.createUpdatedResponsePassenger();

            when(passengerRepository.findPassengerByIdNonDeleted(passengerId)).thenReturn(Optional.of(testPassenger));
            when(passengerRepository.existsByEmail(requestPassenger.email())).thenReturn(false);
            when(passengerRepository.existsByPhoneNumber(requestPassenger.phoneNumber())).thenReturn(false);
            when(passengerRepository.save(testPassenger)).thenReturn(updatedPassenger);
            when(passengerMapper.passengerToResponsePassenger(updatedPassenger)).thenReturn(responsePassenger);

            ResponsePassenger result = passengerService.editPassenger(passengerId, requestPassenger);

            assertEquals(responsePassenger, result);

            verify(passengerMapper).updatePassengerFromRequestPassenger(eq(requestPassenger), eq(testPassenger));
            verify(passengerRepository).save(testPassenger);
            verify(passengerMapper).passengerToResponsePassenger(updatedPassenger);
        }

        @Test
        void editPassengerEntityNotFound() {
            mockPassengerNotFound(passengerId);

            assertThrows(EntityNotFoundException.class, () -> passengerService.editPassenger(passengerId, testRequestPassenger));

            verify(passengerRepository).findPassengerByIdNonDeleted(passengerId);
            verifyNoMoreInteractions(passengerRepository, passengerMapper);
        }

        @Test
        void editPassengerDuplicateEmail() {
            RequestPassenger updateRequestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();

            when(passengerRepository.findPassengerByIdNonDeleted(passengerId)).thenReturn(Optional.of(testPassenger));
            when(passengerRepository.existsByEmail(updateRequestPassenger.email())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> passengerService.editPassenger(passengerId, updateRequestPassenger));

            verify(passengerRepository).findPassengerByIdNonDeleted(passengerId);
            verify(passengerRepository).existsByEmail(updateRequestPassenger.email());
            verifyNoMoreInteractions(passengerRepository, passengerMapper);
        }

        @Test
        void editPassengerDuplicatePhoneNumber() {
            RequestPassenger updateRequestPassenger = PassengerTestEntityUtils.createUpdateRequestPassenger();

            when(passengerRepository.findPassengerByIdNonDeleted(passengerId)).thenReturn(Optional.of(testPassenger));
            when(passengerRepository.existsByEmail(updateRequestPassenger.email())).thenReturn(false);
            when(passengerRepository.existsByPhoneNumber(updateRequestPassenger.phoneNumber())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> passengerService.editPassenger(passengerId, updateRequestPassenger));

            verify(passengerRepository).findPassengerByIdNonDeleted(passengerId);
            verify(passengerRepository).existsByEmail(updateRequestPassenger.email());
            verify(passengerRepository).existsByPhoneNumber(updateRequestPassenger.phoneNumber());
            verifyNoMoreInteractions(passengerRepository, passengerMapper);
        }
    }

    @Nested
    class DeletePassengerTests {

        @Test
        void deletePassengerOk(){
            when(passengerRepository.findPassengerByIdNonDeleted(passengerId)).thenReturn(Optional.of(testPassenger));

            passengerService.deletePassenger(passengerId);

            verify(passengerRepository).findPassengerByIdNonDeleted(passengerId);
            verify(passengerRepository).delete(testPassenger);
        }

        @Test
        void deletePassengerNotFound(){
            mockPassengerNotFound(passengerId);

            assertThrows(EntityNotFoundException.class, () -> passengerService.deletePassenger(passengerId));

            verify(passengerRepository).findPassengerByIdNonDeleted(passengerId);
            verifyNoMoreInteractions(passengerRepository);
        }

    }

    @Nested
    class GetPassengerTests {

        @Test
        void getPassengerByIdOk() {
            when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(testPassenger));
            when(passengerMapper.passengerToResponsePassenger(testPassenger)).thenReturn(testResponsePassenger);

            ResponsePassenger responsePassenger = passengerService.getPassengerById(passengerId);

            assertEquals(testResponsePassenger, responsePassenger);
        }

        @Test
        void getPassengerByIdEntityNotFound() {
            mockPassengerNotFound(passengerId);

            assertThrows(EntityNotFoundException.class, () -> passengerService.getPassengerById(passengerId));

            verify(passengerRepository).findById(passengerId);
            verifyNoMoreInteractions(passengerMapper);
        }

        @Test
        void getPassengerByIdNonDeletedOk() {
            when(passengerRepository.findPassengerByIdNonDeleted(passengerId)).thenReturn(Optional.of(testPassenger));
            when(passengerMapper.passengerToResponsePassenger(testPassenger)).thenReturn(testResponsePassenger);

            ResponsePassenger responsePassenger = passengerService.getPassengerByIdNonDeleted(passengerId);

            assertEquals(testResponsePassenger, responsePassenger);
        }

        @Test
        void getPassengerByIdNonDeletedEntityNotFound() {
            mockPassengerNotFound(passengerId);

            assertThrows(EntityNotFoundException.class, () -> passengerService.getPassengerByIdNonDeleted(passengerId));

            verify(passengerRepository).findPassengerByIdNonDeleted(passengerId);
            verifyNoMoreInteractions(passengerMapper);
        }

        @Test
        void getAllPassengers() {
            Pageable pageable = PassengerTestEntityUtils.createDefaultPageRequest();
            Page<Passenger> passengerPage = PassengerTestEntityUtils.createDefaultPassengerPage(List.of(testPassenger));

            when(passengerRepository.findAll(pageable)).thenReturn(passengerPage);
            when(passengerMapper.passengerToResponsePassenger(testPassenger)).thenReturn(testResponsePassenger);

            PagedResponsePassengerList result = passengerService.getAllPassengers(pageable);

            assertPagedResponse(result);

            verify(passengerRepository).findAll(pageable);
            verify(passengerMapper).passengerToResponsePassenger(testPassenger);
        }

        @Test
        void getAllNonDeletedPassengers() {
            Pageable pageable = PassengerTestEntityUtils.createDefaultPageRequest();
            Page<Passenger> passengerPage = PassengerTestEntityUtils.createDefaultPassengerPage(List.of(testPassenger));

            when(passengerRepository.findAllNonDeleted(pageable)).thenReturn(passengerPage);
            when(passengerMapper.passengerToResponsePassenger(testPassenger)).thenReturn(testResponsePassenger);

            PagedResponsePassengerList result = passengerService.getAllNonDeletedPassengers(pageable);

            assertPagedResponse(result);

            verify(passengerRepository).findAllNonDeleted(pageable);
            verify(passengerMapper).passengerToResponsePassenger(testPassenger);
        }

    }

    @Nested
    class PassengerExistsTests {
        @Test
        void doesPassengerExistOk() {
            boolean expected = true;

            when(passengerRepository.existsByIdAndIsDeletedFalse(passengerId)).thenReturn(expected);

            boolean actual = passengerService.doesPassengerExist(passengerId);

            assertEquals(expected, actual);
            verify(passengerRepository).existsByIdAndIsDeletedFalse(passengerId);
        }

        @Test
        void doesPassengerExistEntityNotFound() {
            when(passengerRepository.existsByIdAndIsDeletedFalse(passengerId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> passengerService.doesPassengerExist(passengerId));

            verify(passengerRepository).existsByIdAndIsDeletedFalse(passengerId);
        }
    }

    private void mockPassengerNotFound(Long id) {
        when(passengerRepository.findPassengerByIdNonDeleted(id))
                .thenThrow(new EntityNotFoundException(ExceptionMessages.PASSENGER_NOT_FOUND.format(id)));
    }

    private void assertPagedResponse(PagedResponsePassengerList actual) {
        assertEquals(testResponsePassenger, actual.passengers().get(0));
        assertEquals(PassengerTestEntityUtils.DEFAULT_TOTAL_ELEMENTS, actual.totalElements());
        assertEquals(PassengerTestEntityUtils.DEFAULT_TOTAL_PAGES, actual.totalPages());
        assertEquals(PassengerTestEntityUtils.DEFAULT_PAGE_NUMBER, actual.pageNumber());
        assertEquals(PassengerTestEntityUtils.DEFAULT_PAGE_SIZE, actual.pageSize());
        assertTrue(actual.last());
    }

}