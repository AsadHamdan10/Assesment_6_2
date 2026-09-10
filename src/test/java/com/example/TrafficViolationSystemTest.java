package com.example;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for
 * Real-Time Traffic Violation and
 * E-Challan Management System.
 */
public class TrafficViolationSystemTest {

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private TrafficViolationSystem.Vehicle createVehicle(
            String number) {

        return new TrafficViolationSystem.Vehicle(
                number,
                "Arun Kumar",
                "9876543210",
                TrafficViolationSystem.VehicleType.FOUR_WHEELER);
    }

    private TrafficViolationSystem.Vehicle createTwoWheeler(
            String number) {

        return new TrafficViolationSystem.Vehicle(
                number,
                "Rahul",
                "9876543211",
                TrafficViolationSystem.VehicleType.TWO_WHEELER);
    }

    private TrafficViolationSystem.Violation speeding(
            String violationId,
            String vehicleNumber,
            double speed,
            double permittedSpeed,
            String eventId) {

        return new TrafficViolationSystem.Violation(
                violationId,
                vehicleNumber,
                TrafficViolationSystem.ViolationType.OVER_SPEEDING,
                TrafficViolationSystem.ViolationSeverity.HIGH,
                speed,
                permittedSpeed,
                "Chennai Main Road",
                eventId);
    }

    private TrafficViolationSystem.Violation signal(
            String violationId,
            String vehicleNumber,
            String eventId) {

        return new TrafficViolationSystem.Violation(
                violationId,
                vehicleNumber,
                TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                TrafficViolationSystem.ViolationSeverity.HIGH,
                0,
                50,
                "Main Junction",
                eventId);
    }

    private TrafficViolationSystem.Violation parking(
            String violationId,
            String vehicleNumber,
            String eventId) {

        return new TrafficViolationSystem.Violation(
                violationId,
                vehicleNumber,
                TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                TrafficViolationSystem.ViolationSeverity.MEDIUM,
                0,
                50,
                "Market Road",
                eventId);
    }

    // ============================================================
    // VEHICLE REGISTRATION - POSITIVE
    // ============================================================

    @Test
    public void testVehicleRegistration() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        assertEquals(
                1,
                system.getVehicleCount());
    }

    @Test
    public void testMultipleVehicleRegistration() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        system.registerVehicle(
                createTwoWheeler("TN02XY5678"));

        system.registerVehicle(
                createVehicle("KA01AA1111"));

        assertEquals(
                3,
                system.getVehicleCount());
    }

    @Test
    public void testVehicleNumberIsCaseInsensitive() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("tn01ab1234"));

        assertNotNull(
                system.getVehicle("TN01AB1234"));
    }

    // ============================================================
    // VIOLATION DETECTION
    // ============================================================

    @Test
    public void testOverSpeedingDetection() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                system.detectOverSpeeding(
                        "V001",
                        "TN01AB1234",
                        90,
                        60,
                        "Chennai",
                        "EVENT001");

        assertEquals(
                TrafficViolationSystem.ViolationType
                        .OVER_SPEEDING,
                violation.getViolationType());

        assertEquals(
                TrafficViolationSystem.ViolationSeverity
                        .HIGH,
                violation.getSeverity());
    }

    @Test
    public void testSignalViolationDetection() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                system.detectSignalViolation(
                        "V002",
                        "TN01AB1234",
                        "Main Junction",
                        "EVENT002");

        assertEquals(
                TrafficViolationSystem.ViolationType
                        .SIGNAL_VIOLATION,
                violation.getViolationType());
    }

    @Test
    public void testIllegalParkingDetection() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                system.detectIllegalParking(
                        "V003",
                        "TN01AB1234",
                        "Market Road",
                        "EVENT003");

        assertEquals(
                TrafficViolationSystem.ViolationType
                        .ILLEGAL_PARKING,
                violation.getViolationType());
    }

    // ============================================================
    // SPEED SEVERITY
    // ============================================================

    @Test
    public void testLowSpeedViolation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.LOW,
                system.determineSpeedSeverity(
                        65,
                        60));
    }

    @Test
    public void testMediumSpeedViolation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.MEDIUM,
                system.determineSpeedSeverity(
                        80,
                        60));
    }

    @Test
    public void testHighSpeedViolation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.HIGH,
                system.determineSpeedSeverity(
                        100,
                        60));
    }

    // ============================================================
    // FINE CALCULATION
    // ============================================================

    @Test
    public void testOverSpeedingFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "V001",
                        "TN01AB1234",
                        TrafficViolationSystem.ViolationType
                                .OVER_SPEEDING,
                        TrafficViolationSystem.ViolationSeverity
                                .HIGH,
                        100,
                        60,
                        "Chennai",
                        "EVENT001");

        double fine =
                system.calculateFine(violation);

        assertEquals(
                2000.0,
                fine,
                0.01);
    }

    @Test
    public void testSignalViolationFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                signal(
                        "V001",
                        "TN01AB1234",
                        "EVENT001");

        double fine =
                system.calculateFine(violation);

        assertEquals(
                3000.0,
                fine,
                0.01);
    }

    @Test
    public void testIllegalParkingFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                parking(
                        "V001",
                        "TN01AB1234",
                        "EVENT001");

        double fine =
                system.calculateFine(violation);

        assertEquals(
                750.0,
                fine,
                0.01);
    }

    // ============================================================
    // E-CHALLAN
    // ============================================================

    @Test
    public void testEChallanGeneration() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                speeding(
                        "V001",
                        "TN01AB1234",
                        100,
                        60,
                        "EVENT001");

        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        violation);

        assertNotNull(challan);

        assertNotNull(
                challan.getChallanId());

        assertEquals(
                TrafficViolationSystem.PaymentStatus.UNPAID,
                challan.getPaymentStatus());

        assertEquals(
                1,
                system.getChallanCount());
    }

    @Test
    public void testChallanContainsVehicleInformation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        assertEquals(
                "TN01AB1234",
                challan.getVehicle()
                        .getVehicleNumber());

        assertEquals(
                "Arun Kumar",
                challan.getVehicle()
                        .getOwnerName());
    }

    // ============================================================
    // REPEATED VIOLATION
    // ============================================================

    @Test
    public void testRepeatedViolationGetsHigherPenalty() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan first =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT001"));

        TrafficViolationSystem.EChallan second =
                system.registerViolation(
                        speeding(
                                "V002",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT002"));

        assertTrue(
                second.getFineAmount()
                        > first.getFineAmount());
    }

    @Test
    public void testThirdRepeatedViolationGetsEvenHigherPenalty() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan first =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT001"));

        TrafficViolationSystem.EChallan second =
                system.registerViolation(
                        speeding(
                                "V002",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT002"));

        TrafficViolationSystem.EChallan third =
                system.registerViolation(
                        speeding(
                                "V003",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT003"));

        assertTrue(
                second.getFineAmount()
                        > first.getFineAmount());

        assertTrue(
                third.getFineAmount()
                        > second.getFineAmount());
    }

    // ============================================================
    // DUPLICATE CHALLAN
    // ============================================================

    @Test
    public void testDuplicateViolationIsRejected() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.Violation violation =
                speeding(
                        "V001",
                        "TN01AB1234",
                        100,
                        60,
                        "EVENT001");

        system.registerViolation(violation);

        try {

            system.registerViolation(violation);

            fail("Duplicate violation should be rejected.");

        } catch (
                TrafficViolationSystem.DuplicateChallanException e) {

            assertTrue(
                    e.getMessage()
                            .contains("EVENT001"));
        }
    }

    @Test
    public void testDifferentEventsAreAllowed() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan first =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT001"));

        TrafficViolationSystem.EChallan second =
                system.registerViolation(
                        speeding(
                                "V002",
                                "TN01AB1234",
                                100,
                                60,
                                "EVENT002"));

        assertNotEquals(
                first.getChallanId(),
                second.getChallanId());

        assertEquals(
                2,
                system.getChallanCount());
    }

    // ============================================================
    // PAYMENT
    // ============================================================

    @Test
    public void testChallanPayment() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        system.payChallan(
                challan.getChallanId());

        assertEquals(
                TrafficViolationSystem.PaymentStatus.PAID,
                challan.getPaymentStatus());
    }

    @Test
    public void testOutstandingFineBeforePayment() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        assertEquals(
                challan.getFineAmount(),
                system.calculateOutstandingFine(
                        "TN01AB1234"),
                0.01);
    }

    @Test
    public void testOutstandingFineAfterPaymentIsZero() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        system.payChallan(
                challan.getChallanId());

        assertEquals(
                0.0,
                system.calculateOutstandingFine(
                        "TN01AB1234"),
                0.01);
    }

    @Test
    public void testMultipleOutstandingFines() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan first =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        TrafficViolationSystem.EChallan second =
                system.registerViolation(
                        parking(
                                "V002",
                                "TN01AB1234",
                                "EVENT002"));

        double expected =
                first.getFineAmount()
                        + second.getFineAmount();

        assertEquals(
                expected,
                system.calculateOutstandingFine(
                        "TN01AB1234"),
                0.01);
    }

    @Test
    public void testPaidChallanIsExcludedFromOutstandingFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan first =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        TrafficViolationSystem.EChallan second =
                system.registerViolation(
                        parking(
                                "V002",
                                "TN01AB1234",
                                "EVENT002"));

        system.payChallan(
                first.getChallanId());

        assertEquals(
                second.getFineAmount(),
                system.calculateOutstandingFine(
                        "TN01AB1234"),
                0.01);
    }

    @Test
    public void testPayingAlreadyPaidChallanFails() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        speeding(
                                "V001",
                                "TN01AB1234",
                                90,
                                60,
                                "EVENT001"));

        system.payChallan(
                challan.getChallanId());

        try {

            system.payChallan(
                    challan.getChallanId());

            fail("Already paid challan should fail.");

        } catch (
                TrafficViolationSystem.PaymentException e) {

            assertTrue(
                    e.getMessage()
                            .contains("already paid"));
        }
    }

    // ============================================================
    // HISTORY
    // ============================================================

    @Test
    public void testViolationHistory() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        system.registerViolation(
                speeding(
                        "V001",
                        "TN01AB1234",
                        90,
                        60,
                        "EVENT001"));

        system.registerViolation(
                parking(
                        "V002",
                        "TN01AB1234",
                        "EVENT002"));

        assertEquals(
                2,
                system.getViolationHistory(
                        "TN01AB1234")
                        .size());
    }

    @Test
    public void testVehicleClassificationWithNoHistory() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        assertEquals(
                "LOW_RISK",
                system.classifyVehicle(
                        "TN01AB1234"));
    }

    @Test
    public void testVehicleClassificationWithRepeatedViolations() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        system.registerViolation(
                speeding(
                        "V001",
                        "TN01AB1234",
                        100,
                        60,
                        "EVENT001"));

        system.registerViolation(
                speeding(
                        "V002",
                        "TN01AB1234",
                        100,
                        60,
                        "EVENT002"));

        assertEquals(
                "HIGH_RISK",
                system.classifyVehicle(
                        "TN01AB1234"));
    }

    // ============================================================
    // INVALID VEHICLE TESTS
    // ============================================================

    @Test
    public void testNullVehicle() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.registerVehicle(null);

            fail("Null vehicle should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertEquals(
                    "Vehicle cannot be null.",
                    e.getMessage());
        }
    }

    @Test
    public void testEmptyVehicleNumber() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.registerVehicle(
                    new TrafficViolationSystem.Vehicle(
                            "",
                            "Arun",
                            "9876543210",
                            TrafficViolationSystem.VehicleType
                                    .FOUR_WHEELER));

            fail("Empty vehicle number should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertEquals(
                    "Vehicle number is required.",
                    e.getMessage());
        }
    }

    @Test
    public void testNullOwnerName() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.registerVehicle(
                    new TrafficViolationSystem.Vehicle(
                            "TN01AB1234",
                            null,
                            "9876543210",
                            TrafficViolationSystem.VehicleType
                                    .FOUR_WHEELER));

            fail("Null owner should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertEquals(
                    "Owner name is required.",
                    e.getMessage());
        }
    }

    @Test
    public void testEmptyOwnerPhone() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.registerVehicle(
                    new TrafficViolationSystem.Vehicle(
                            "TN01AB1234",
                            "Arun",
                            "",
                            TrafficViolationSystem.VehicleType
                                    .FOUR_WHEELER));

            fail("Empty phone should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertEquals(
                    "Owner phone is required.",
                    e.getMessage());
        }
    }

    @Test
    public void testNullVehicleType() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.registerVehicle(
                    new TrafficViolationSystem.Vehicle(
                            "TN01AB1234",
                            "Arun",
                            "9876543210",
                            null));

            fail("Null vehicle type should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertEquals(
                    "Vehicle type is required.",
                    e.getMessage());
        }
    }

    @Test
    public void testDuplicateVehicleRegistration() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        try {

            system.registerVehicle(
                    createVehicle("TN01AB1234"));

            fail("Duplicate vehicle should fail.");

        } catch (
                TrafficViolationSystem.DuplicateVehicleException e) {

            assertTrue(
                    e.getMessage()
                            .contains("TN01AB1234"));
        }
    }

    // ============================================================
    // INVALID VIOLATION TESTS
    // ============================================================

    @Test
    public void testNullViolation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.registerViolation(null);

            fail("Null violation should fail.");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertEquals(
                    "Violation cannot be null.",
                    e.getMessage());
        }
    }

    @Test
    public void testNegativeSpeed() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        system.registerVehicle(
                createVehicle("TN01AB1234"));

        try {

            TrafficViolationSystem.Violation violation =
                    new TrafficViolationSystem.Violation(
                            "V001",
                            "TN01AB1234",
                            TrafficViolationSystem.ViolationType
                                    .OVER_SPEEDING,
                            TrafficViolationSystem.ViolationSeverity.HIGH,
                            -10,
                            60,
                            "Chennai",
                            "EVENT001");

            system.registerViolation(violation);

            fail("Negative speed should fail.");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertEquals(
                    "Speed cannot be negative.",
                    e.getMessage());
        }
    }

    @Test
    public void testZeroPermittedSpeed() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.determineSpeedSeverity(
                    50,
                    0);

            fail("Zero permitted speed should fail.");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertEquals(
                    "Permitted speed must be greater than zero.",
                    e.getMessage());
        }
    }

    @Test
    public void testSpeedNotExceedingLimit() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.determineSpeedSeverity(
                    60,
                    60);

            fail("No violation should be generated.");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertEquals(
                    "Speed does not exceed permitted speed.",
                    e.getMessage());
        }
    }

    @Test
    public void testUnregisteredVehicleViolation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Violation violation =
                speeding(
                        "V001",
                        "TN99XX9999",
                        90,
                        60,
                        "EVENT001");

        try {

            system.registerViolation(violation);

            fail("Unregistered vehicle should fail.");

        } catch (
                TrafficViolationSystem.VehicleNotFoundException e) {

            assertTrue(
                    e.getMessage()
                            .contains("TN99XX9999"));
        }
    }

    // ============================================================
    // INVALID PAYMENT TESTS
    // ============================================================

    @Test
    public void testUnknownChallanPayment() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.payChallan("UNKNOWN");

            fail("Unknown challan should fail.");

        } catch (
                TrafficViolationSystem.PaymentException e) {

            assertTrue(
                    e.getMessage()
                            .contains("not found"));
        }
    }

    @Test
    public void testEmptyChallanIdPayment() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        try {

            system.payChallan("");

            fail("Empty challan ID should fail.");

        } catch (
                TrafficViolationSystem.PaymentException e) {

            assertEquals(
                    "Challan ID is required.",
                    e.getMessage());
        }
    }

    // ============================================================
    // BOUNDARY TESTS
    // ============================================================

    @Test
    public void testSpeedExactlyTenAboveLimitIsLow() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.LOW,
                system.determineSpeedSeverity(
                        70,
                        60));
    }

    @Test
    public void testSpeedElevenAboveLimitIsMedium() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.MEDIUM,
                system.determineSpeedSeverity(
                        71,
                        60));
    }

    @Test
    public void testSpeedExactlyThirtyAboveLimitIsMedium() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.MEDIUM,
                system.determineSpeedSeverity(
                        90,
                        60));
    }

    @Test
    public void testSpeedThirtyOneAboveLimitIsHigh() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        assertEquals(
                TrafficViolationSystem.ViolationSeverity.HIGH,
                system.determineSpeedSeverity(
                        91,
                        60));
    }

    // ============================================================
    // MULTIPLE FAILURE SCENARIO
    // ============================================================

    @Test
    public void testMultipleFailureScenarios() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        // Failure 1:
        // Invalid vehicle
        try {

            system.registerVehicle(null);

            fail("Invalid vehicle should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertTrue(true);
        }

        // Failure 2:
        // Empty vehicle number
        try {

            system.registerVehicle(
                    new TrafficViolationSystem.Vehicle(
                            "",
                            "Arun",
                            "9876543210",
                            TrafficViolationSystem.VehicleType
                                    .FOUR_WHEELER));

            fail("Empty vehicle number should fail.");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertTrue(true);
        }

        // Register valid vehicle for later tests
        system.registerVehicle(
                createVehicle("TN01AB1234"));

        // Failure 3:
        // Negative speed
        try {

            system.determineSpeedSeverity(
                    -20,
                    60);

            fail("Negative speed should fail.");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertTrue(true);
        }

        // Failure 4:
        // Speed does not exceed limit
        try {

            system.determineSpeedSeverity(
                    50,
                    60);

            fail("Non-violation should fail.");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertTrue(true);
        }

        // Failure 5:
        // Unknown vehicle
        try {

            system.getVehicle(
                    "UNKNOWN");

            fail("Unknown vehicle should fail.");

        } catch (
                TrafficViolationSystem.VehicleNotFoundException e) {

            assertTrue(true);
        }
    }

    // ============================================================
    // COMPLETE END-TO-END TEST
    // ============================================================

    @Test
    public void testCompleteViolationLifecycle() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        // Register vehicle
        system.registerVehicle(
                createVehicle("TN01AB1234"));

        // Detect violation
        TrafficViolationSystem.Violation violation =
                system.detectOverSpeeding(
                        "V001",
                        "TN01AB1234",
                        100,
                        60,
                        "Chennai",
                        "EVENT001");

        // Generate challan
        TrafficViolationSystem.EChallan challan =
                system.registerViolation(
                        violation);

        assertEquals(
                TrafficViolationSystem.PaymentStatus.UNPAID,
                challan.getPaymentStatus());

        // Outstanding fine exists
        assertTrue(
                system.calculateOutstandingFine(
                        "TN01AB1234") > 0);

        // Pay challan
        system.payChallan(
                challan.getChallanId());

        // Outstanding fine should disappear
        assertEquals(
                0.0,
                system.calculateOutstandingFine(
                        "TN01AB1234"),
                0.01);

        // History should contain violation
        assertEquals(
                1,
                system.getViolationHistory(
                        "TN01AB1234")
                        .size());
    }
}