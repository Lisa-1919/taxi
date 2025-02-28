package com.modsen.rating.unit.service

import com.modsen.rating.client.DriverServiceClient
import com.modsen.rating.client.PassengerServiceClient
import com.modsen.rating.client.RideServiceClient
import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.entity.Rate
import com.modsen.rating.mapper.RateMapper
import com.modsen.rating.repo.RateRepository
import com.modsen.rating.service.RateServiceImpl
import com.modsen.rating.util.RateTestEntityUtils
import com.modsen.rating.util.UserType
import jakarta.persistence.EntityNotFoundException
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RateServiceImplTest {

    @InjectMocks
    private lateinit var rateService: RateServiceImpl

    @Mock
    private lateinit var rateRepository: RateRepository

    @Mock
    private lateinit var rateMapper: RateMapper

    @Mock
    private lateinit var rideServiceClient: RideServiceClient

    @Mock
    private lateinit var passengerServiceClient: PassengerServiceClient

    @Mock
    private lateinit var driverServiceClient: DriverServiceClient

    @Nested
    inner class AddRateTests {

        private lateinit var requestRate: RequestRate
        private lateinit var rate: Rate
        private lateinit var responseRate: ResponseRate

        private fun initializeTestData(userType: UserType) {
            requestRate = RateTestEntityUtils.createTestRequestRate(userType = userType)
            rate = RateTestEntityUtils.createTestRate(userType = userType)
            responseRate = RateTestEntityUtils.createTestResponseRate(userType = userType)
        }
        @ParameterizedTest(name = "{0} added a rate")
        @CsvSource(
            "PASSENGER",
            "DRIVER"
        )
        fun addRateOk(userType: UserType) {
            initializeTestData(userType)

            checkRideExistenceMock(requestRate.rideId, requestRate.userId, userType)
            checkUserExistenceMock(requestRate.userId, userType)
            given(rateMapper.requestRateToRate(requestRate)).willReturn(rate)
            given(rateRepository.save(rate)).willReturn(rate)
            given(rateMapper.rateToResponseRate(rate)).willReturn(responseRate)

            val result = rateService.addRate(requestRate)

            assertEquals(responseRate, result)
            verifyRideExistence(requestRate.rideId, requestRate.userId, requestRate.userType)
            verifyUserExistence(requestRate.userId, requestRate.userType)
            verify(rateMapper).requestRateToRate(requestRate)
            verify(rateRepository).save(rate)
            verify(rateMapper).rateToResponseRate(rate)
            verifyAddRateNoMoreInteractionsMock()
        }

        @ParameterizedTest(name = "{0} not found")
        @CsvSource("PASSENGER", "DRIVER")
        fun addRateUserNotFound(userType: UserType) {
            initializeTestData(userType)

            checkRideExistenceMock(requestRate.rideId, requestRate.userId, userType)
            checkUserExistenceMockWithException(userType, requestRate.userId)

            assertThrows<EntityNotFoundException> {rateService.addRate(requestRate)}

            verifyRideExistence(requestRate.rideId, requestRate.userId, userType)
            verifyUserExistence(requestRate.userId, requestRate.userType)

            verifyAddRateNoMoreInteractionsMock()
        }

        private fun verifyAddRateNoMoreInteractionsMock(){
            verifyNoMoreInteractions(
                rateRepository,
                rateMapper,
                rideServiceClient,
                passengerServiceClient,
                driverServiceClient
            )
        }
    }

    @Nested
    inner class GetRateById {
        @Test
        fun getRateByIdOk() {
            val rateId = RateTestEntityUtils.DEFAULT_RATE_ID
            val testResponseRate = RateTestEntityUtils.createTestResponseRate()
            val testRate = RateTestEntityUtils.createTestRate()

            given(rateRepository.findById(rateId)).willReturn(Optional.of(testRate))
            given(rateMapper.rateToResponseRate(testRate)).willReturn(testResponseRate)

            val result = rateService.getRateById(rateId)

            assertEquals(testResponseRate, result)
            verify(rateRepository).findById(rateId)
            verify(rateMapper).rateToResponseRate(testRate)
            verifyNoMoreInteractions(rateRepository, rateMapper)
        }

        @Test
        fun getRateNotFound() {
            val rateId = RateTestEntityUtils.DEFAULT_RATE_ID
            given(rateRepository.findById(rateId)).willThrow(EntityNotFoundException::class.java)

            assertThrows<EntityNotFoundException> {rateService.getRateById(rateId)}

            verify(rateRepository).findById(rateId)
            verifyNoMoreInteractions(rateMapper)
        }
    }

        @Test
        fun getAllRates() {
            val testResponseRate = RateTestEntityUtils.createTestResponseRate()
            val testRate = RateTestEntityUtils.createTestRate()
            val pageable: Pageable = RateTestEntityUtils.createPageRequest()
            val ratePage: Page<Rate> = RateTestEntityUtils.createDefaultRatePage(listOf(testRate))

            given(rateRepository.findAll(pageable)).willReturn(ratePage)
            given(rateMapper.rateToResponseRate(testRate)).willReturn(testResponseRate)

            val result = rateService.getAllRates(pageable)

            assertEquals(RateTestEntityUtils.DEFAULT_TOTAL_ELEMENTS, result.totalElements)
            assertEquals(testResponseRate, result.list[0])
            verify(rateRepository).findAll(pageable)
            verify(rateMapper).rateToResponseRate(testRate)
            verifyNoMoreInteractions(rateRepository, rateMapper)
        }

    @Nested
    inner class GetAllRatesByUser {

        private lateinit var testRequestRate: RequestRate
        private lateinit var testRate: Rate
        private lateinit var testResponseRate: ResponseRate
        private lateinit var pageable: Pageable
        private lateinit var ratePage: Page<Rate>

        private fun initializeTestData(userType: UserType) {
            testRequestRate = RateTestEntityUtils.createTestRequestRate(userType = userType)
            testRate = RateTestEntityUtils.createTestRate(userType = userType)
            testResponseRate = RateTestEntityUtils.createTestResponseRate(userType = userType)
            pageable = RateTestEntityUtils.createPageRequest()
            ratePage = RateTestEntityUtils.createDefaultRatePage(listOf(testRate))
        }

        @ParameterizedTest(name = "get all rates by {0} id")
        @CsvSource("PASSENGER", "DRIVER")
        fun getAllRatesByUserId(userType: UserType) {
            val userId = RateTestEntityUtils.DEFAULT_USER_ID

            initializeTestData(userType)

            getAllRatesByUserTypeMock(userId, userType)

            given(rateMapper.rateToResponseRate(testRate)).willReturn(testResponseRate)

            val result = when (userType) {
                UserType.DRIVER -> rateService.getAllRatesByDriverId(userId, pageable)
                UserType.PASSENGER -> rateService.getAllRatesByPassengerId(userId, pageable)
            }

            assertEquals(RateTestEntityUtils.DEFAULT_TOTAL_ELEMENTS, result.totalElements)
            assertEquals(testResponseRate, result.list[0])
            verifyRepositoryInteractionsByUserType(userId, userType)
            verify(rateMapper).rateToResponseRate(testRate)
            verifyNoMoreInteractions(rateRepository, rateMapper)
        }
        private fun getAllRatesByUserTypeMock(userId: UUID, userType: UserType){
            when (userType) {
                UserType.DRIVER -> given(rateRepository.getAllRatesByDriverId(userId, pageable)).willReturn(ratePage)
                UserType.PASSENGER -> given(rateRepository.getAllRatesByPassengerId(userId, pageable)).willReturn(ratePage)
            }
        }

        private fun verifyRepositoryInteractionsByUserType(userId: UUID, userType: UserType){
            when (userType) {
                UserType.DRIVER -> verify(rateRepository).getAllRatesByDriverId(userId, pageable)
                UserType.PASSENGER -> verify(rateRepository).getAllRatesByPassengerId(userId, pageable)
            }
        }

    }

    private fun checkRideExistenceMock(rideId: Long, userId: UUID, userType: UserType) {
        when (userType) {
            UserType.DRIVER -> given(
                rideServiceClient.doesRideExistForDriver(rideId, userId)
            ).willReturn(ResponseEntity.ok(true))

            UserType.PASSENGER -> given(
                rideServiceClient.doesRideExistForPassenger(rideId, userId)
            ).willReturn(ResponseEntity.ok(true))
        }
    }

    private fun checkUserExistenceMock(userId: UUID, userType: UserType) {
        when (userType) {
            UserType.DRIVER -> given(driverServiceClient.doesDriverExist(userId)).willReturn(ResponseEntity.ok(true))
            UserType.PASSENGER -> given(passengerServiceClient.doesPassengerExist(userId)).willReturn(
                ResponseEntity.ok(true)
            )
        }
    }

    private fun checkUserExistenceMockWithException(userType: UserType, userId: UUID) {
        when (userType) {
            UserType.DRIVER -> given(driverServiceClient.doesDriverExist(userId)).willThrow(EntityNotFoundException::class.java)
            UserType.PASSENGER -> given(passengerServiceClient.doesPassengerExist(userId)).willThrow(
                EntityNotFoundException::class.java
            )
        }
    }

    private fun verifyRideExistence(rideId: Long, userId: UUID, userType: UserType) {
        when (userType) {
            UserType.DRIVER -> verify(rideServiceClient).doesRideExistForDriver(rideId, userId)
            UserType.PASSENGER -> verify(rideServiceClient).doesRideExistForPassenger(rideId, userId)
        }
    }

    private fun verifyUserExistence(userId: UUID, userType: UserType) {
        when (userType) {
            UserType.DRIVER -> verify(driverServiceClient).doesDriverExist(userId)
            UserType.PASSENGER -> verify(passengerServiceClient).doesPassengerExist(userId)
        }
    }

}