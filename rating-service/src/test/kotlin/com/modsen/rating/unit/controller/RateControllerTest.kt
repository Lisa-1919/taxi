package com.modsen.rating.unit.controller

import com.modsen.rating.controller.RateController
import com.modsen.rating.dto.PagedResponseRateList
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.service.RateService
import com.modsen.rating.util.ExceptionMessages
import com.modsen.rating.util.RateTestEntityUtils
import com.modsen.rating.util.UserType
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post


@WebMvcTest(RateController::class)
@ActiveProfiles("test")
class RateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var rateService: RateService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Nested
    inner class GetRateById {

        private var rateId: Long = 0

        @BeforeEach
        fun setUp(){
            rateId = RateTestEntityUtils.DEFAULT_RATE_ID
        }

        @Test
        fun getRateById() {
            val responseRate = RateTestEntityUtils.createTestResponseRate(id = rateId)

            given(rateService.getRateById(rateId)).willReturn(responseRate)

            mockMvc.get("/api/v1/rates/$rateId")
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(rateId) }
                }

            verify(rateService).getRateById(rateId)
        }

        @Test
        fun getRateByIdNotFound() {
            val errorMessage = ExceptionMessages.rateNotFound(rateId)

            given(rateService.getRateById(rateId)).willThrow(EntityNotFoundException(errorMessage))

            mockMvc.get("/api/v1/rates/$rateId")
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.message") { value(errorMessage) }
                }

            verify(rateService).getRateById(rateId)
        }
    }

    @Test
    fun getAllRates() {
        val responseRate: ResponseRate = RateTestEntityUtils.createTestResponseRate()
        val pageable: Pageable = RateTestEntityUtils.createPageRequest()
        val pagedResponse = RateTestEntityUtils.createPagedResponseRateList(listOf(responseRate))

        given(rateService.getAllRates(pageable)).willReturn(pagedResponse)

        mockMvc.get("/api/v1/rates") {
            param("page", pageable.pageNumber.toString())
            param("size", pageable.pageSize.toString())
        }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.totalElements") { value(pagedResponse.totalElements) }
            }

        verify(rateService).getAllRates(pageable)
    }

    @Nested
    inner class GetRatesFromPassengerTests {
        private lateinit var responseRate: ResponseRate
        private lateinit var pageable: Pageable
        private lateinit var pagedResponse: PagedResponseRateList

        @BeforeEach
        fun setUp() {
            responseRate = RateTestEntityUtils.createTestResponseRate(userType = UserType.PASSENGER)
            pageable = RateTestEntityUtils.createPageRequest()
            pagedResponse = RateTestEntityUtils.createPagedResponseRateList(listOf(responseRate))
        }

        @Test
        fun getAllRatesFromPassengers() {
            given(rateService.getAllRatesFromPassengers(pageable)).willReturn(pagedResponse)

            mockMvc.get("/api/v1/rates/from-passengers") {
                param("page", pageable.pageNumber.toString())
                param("size", pageable.pageSize.toString())
            }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.totalElements") { value(pagedResponse.totalElements) }
                }

            verify(rateService).getAllRatesFromPassengers(pageable)
        }

        @Test
        fun getAllRatesByPassengerId() {
            val passengerId = 1L

            given(rateService.getAllRatesByPassengerId(passengerId, pageable)).willReturn(pagedResponse)

            mockMvc.get("/api/v1/rates/from-passengers/$passengerId") {
                param("page", pageable.pageNumber.toString())
                param("size", pageable.pageSize.toString())
            }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.totalElements") { value(pagedResponse.totalElements) }
                }

            verify(rateService).getAllRatesByPassengerId(passengerId, pageable)
        }
    }

    @Nested
    inner class GetRatesFromDriverTests {
        private lateinit var responseRate: ResponseRate
        private lateinit var pageable: Pageable
        private lateinit var pagedResponse: PagedResponseRateList

        @BeforeEach
        fun setUp() {
            responseRate = RateTestEntityUtils.createTestResponseRate(userType = UserType.DRIVER)
            pageable = RateTestEntityUtils.createPageRequest()
            pagedResponse = RateTestEntityUtils.createPagedResponseRateList(listOf(responseRate))
        }

        @Test
        fun getAllRatesFromDrivers() {
            given(rateService.getAllRatesFromDrivers(pageable)).willReturn(pagedResponse)

            mockMvc.get("/api/v1/rates/from-drivers") {
                param("page", pageable.pageNumber.toString())
                param("size", pageable.pageSize.toString())
            }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.totalElements") { value(pagedResponse.totalElements) }
                }

            verify(rateService).getAllRatesFromDrivers(pageable)
        }

        @Test
        fun getAllRatesByDriverId() {
            val driverId = 1L

            given(rateService.getAllRatesByDriverId(driverId, pageable)).willReturn(pagedResponse)

            mockMvc.get("/api/v1/rates/from-drivers/$driverId") {
                param("page", pageable.pageNumber.toString())
                param("size", pageable.pageSize.toString())
            }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.totalElements") { value(pagedResponse.totalElements) }
                }

            verify(rateService).getAllRatesByDriverId(driverId, pageable)
        }
    }

    @Nested
    inner class AddRateTests {
        @Test
        fun addRate() {
            val requestRate = RateTestEntityUtils.createTestRequestRate()
            val responseRate = RateTestEntityUtils.createTestResponseRate()

            given(rateService.addRate(requestRate)).willReturn(responseRate)

            mockMvc.post("/api/v1/rates") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(requestRate)
            }
                .andExpect {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(responseRate.id) }
                }

            verify(rateService).addRate(requestRate)
        }
    }
}
