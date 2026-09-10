package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Real-Time Traffic Violation and E-Challan Management System
 *
 * Features:
 * - Multiple vehicle registration
 * - Vehicle classification
 * - Violation detection
 * - Speed violation detection
 * - Signal violation detection
 * - Illegal parking detection
 * - Severity-based fine calculation
 * - Repeated violation penalties
 * - E-challan generation
 * - Paid/unpaid challan management
 * - Duplicate challan prevention
 * - Payment processing
 * - Outstanding fine calculation
 * - Violation history
 * - Custom exceptions
 * - Input validation
 */
public class TrafficViolationSystem {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Vehicle categories.
     */
    public enum VehicleType {
        TWO_WHEELER,
        FOUR_WHEELER,
        COMMERCIAL,
        HEAVY_VEHICLE
    }

    /**
     * Violation categories.
     */
    public enum ViolationType {
        OVER_SPEEDING,
        SIGNAL_VIOLATION,
        ILLEGAL_PARKING
    }

    /**
     * Violation severity.
     */
    public enum ViolationSeverity {
        LOW,
        MEDIUM,
        HIGH
    }

    /**
     * Challan payment status.
     */
    public enum PaymentStatus {
        UNPAID,
        PAID
    }

    // ============================================================
    // CUSTOM EXCEPTIONS
    // ============================================================

    /**
     * Invalid vehicle information.
     */
    public static class InvalidVehicleException
            extends RuntimeException {

        public InvalidVehicleException(String message) {
            super(message);
        }
    }

    /**
     * Invalid violation information.
     */
    public static class InvalidViolationException
            extends RuntimeException {

        public InvalidViolationException(String message) {
            super(message);
        }
    }

    /**
     * Vehicle does not exist in the system.
     */
    public static class VehicleNotFoundException
            extends RuntimeException {

        public VehicleNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Duplicate vehicle registration.
     */
    public static class DuplicateVehicleException
            extends RuntimeException {

        public DuplicateVehicleException(String message) {
            super(message);
        }
    }

    /**
     * Duplicate violation/challan.
     */
    public static class DuplicateChallanException
            extends RuntimeException {

        public DuplicateChallanException(String message) {
            super(message);
        }
    }

    /**
     * Invalid payment operation.
     */
    public static class PaymentException
            extends RuntimeException {

        public PaymentException(String message) {
            super(message);
        }
    }

    // ============================================================
    // VEHICLE CLASS
    // ============================================================

    public static class Vehicle {

        private final String vehicleNumber;
        private final String ownerName;
        private final String ownerPhone;
        private final VehicleType vehicleType;

        public Vehicle(
                String vehicleNumber,
                String ownerName,
                String ownerPhone,
                VehicleType vehicleType) {

            this.vehicleNumber = vehicleNumber;
            this.ownerName = ownerName;
            this.ownerPhone = ownerPhone;
            this.vehicleType = vehicleType;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public String getOwnerPhone() {
            return ownerPhone;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }
    }

    // ============================================================
    // VIOLATION CLASS
    // ============================================================

    public static class Violation {

        private final String violationId;
        private final String vehicleNumber;
        private final ViolationType violationType;
        private final ViolationSeverity severity;
        private final double speed;
        private final double permittedSpeed;
        private final String location;
        private final String eventId;

        public Violation(
                String violationId,
                String vehicleNumber,
                ViolationType violationType,
                ViolationSeverity severity,
                double speed,
                double permittedSpeed,
                String location,
                String eventId) {

            this.violationId = violationId;
            this.vehicleNumber = vehicleNumber;
            this.violationType = violationType;
            this.severity = severity;
            this.speed = speed;
            this.permittedSpeed = permittedSpeed;
            this.location = location;
            this.eventId = eventId;
        }

        public String getViolationId() {
            return violationId;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public ViolationType getViolationType() {
            return violationType;
        }

        public ViolationSeverity getSeverity() {
            return severity;
        }

        public double getSpeed() {
            return speed;
        }

        public double getPermittedSpeed() {
            return permittedSpeed;
        }

        public String getLocation() {
            return location;
        }

        public String getEventId() {
            return eventId;
        }
    }

    // ============================================================
    // E-CHALLAN CLASS
    // ============================================================

    public static class EChallan {

        private final String challanId;
        private final Violation violation;
        private final Vehicle vehicle;
        private final double fineAmount;

        private PaymentStatus paymentStatus;

        public EChallan(
                String challanId,
                Violation violation,
                Vehicle vehicle,
                double fineAmount) {

            this.challanId = challanId;
            this.violation = violation;
            this.vehicle = vehicle;
            this.fineAmount = fineAmount;
            this.paymentStatus = PaymentStatus.UNPAID;
        }

        public String getChallanId() {
            return challanId;
        }

        public Violation getViolation() {
            return violation;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public double getFineAmount() {
            return fineAmount;
        }

        public PaymentStatus getPaymentStatus() {
            return paymentStatus;
        }

        private void markPaid() {
            paymentStatus = PaymentStatus.PAID;
        }
    }

    // ============================================================
    // SYSTEM DATA
    // ============================================================

    private final Map<String, Vehicle> vehicles =
            new HashMap<>();

    private final List<Violation> violations =
            new ArrayList<>();

    private final Map<String, EChallan> challans =
            new HashMap<>();

    /**
     * Used to prevent the same violation event
     * from generating multiple challans.
     */
    private final Set<String> processedViolationEvents =
            new HashSet<>();

    private int challanCounter = 1000;

    // ============================================================
    // VEHICLE REGISTRATION
    // ============================================================

    /**
     * Registers a vehicle.
     */
    public void registerVehicle(Vehicle vehicle) {

        validateVehicle(vehicle);

        String number =
                normalizeVehicleNumber(
                        vehicle.getVehicleNumber());

        if (vehicles.containsKey(number)) {

            throw new DuplicateVehicleException(
                    "Vehicle already registered: "
                            + number);
        }

        vehicles.put(number, vehicle);
    }

    /**
     * Validates vehicle information.
     */
    private void validateVehicle(
            Vehicle vehicle) {

        if (vehicle == null) {

            throw new InvalidVehicleException(
                    "Vehicle cannot be null.");
        }

        if (vehicle.getVehicleNumber() == null
                || vehicle.getVehicleNumber()
                .trim()
                .isEmpty()) {

            throw new InvalidVehicleException(
                    "Vehicle number is required.");
        }

        if (vehicle.getOwnerName() == null
                || vehicle.getOwnerName()
                .trim()
                .isEmpty()) {

            throw new InvalidVehicleException(
                    "Owner name is required.");
        }

        if (vehicle.getOwnerPhone() == null
                || vehicle.getOwnerPhone()
                .trim()
                .isEmpty()) {

            throw new InvalidVehicleException(
                    "Owner phone is required.");
        }

        if (vehicle.getVehicleType() == null) {

            throw new InvalidVehicleException(
                    "Vehicle type is required.");
        }
    }

    private String normalizeVehicleNumber(
            String vehicleNumber) {

        return vehicleNumber
                .trim()
                .toUpperCase();
    }

    /**
     * Returns a registered vehicle.
     */
    public Vehicle getVehicle(
            String vehicleNumber) {

        if (vehicleNumber == null
                || vehicleNumber.trim().isEmpty()) {

            throw new VehicleNotFoundException(
                    "Vehicle number is required.");
        }

        String normalized =
                normalizeVehicleNumber(
                        vehicleNumber);

        Vehicle vehicle =
                vehicles.get(normalized);

        if (vehicle == null) {

            throw new VehicleNotFoundException(
                    "Vehicle not found: "
                            + normalized);
        }

        return vehicle;
    }

    // ============================================================
    // VIOLATION VALIDATION
    // ============================================================

    private void validateViolation(
            Violation violation) {

        if (violation == null) {

            throw new InvalidViolationException(
                    "Violation cannot be null.");
        }

        if (violation.getViolationId() == null
                || violation.getViolationId()
                .trim()
                .isEmpty()) {

            throw new InvalidViolationException(
                    "Violation ID is required.");
        }

        if (violation.getVehicleNumber() == null
                || violation.getVehicleNumber()
                .trim()
                .isEmpty()) {

            throw new InvalidViolationException(
                    "Vehicle number is required.");
        }

        if (violation.getViolationType() == null) {

            throw new InvalidViolationException(
                    "Violation type is required.");
        }

        if (violation.getSeverity() == null) {

            throw new InvalidViolationException(
                    "Violation severity is required.");
        }

        if (violation.getLocation() == null
                || violation.getLocation()
                .trim()
                .isEmpty()) {

            throw new InvalidViolationException(
                    "Violation location is required.");
        }

        if (violation.getEventId() == null
                || violation.getEventId()
                .trim()
                .isEmpty()) {

            throw new InvalidViolationException(
                    "Violation event ID is required.");
        }

        if (Double.isNaN(violation.getSpeed())
                || Double.isInfinite(violation.getSpeed())
                || violation.getSpeed() < 0) {

            throw new InvalidViolationException(
                    "Speed cannot be negative.");
        }

        if (Double.isNaN(
                violation.getPermittedSpeed())
                || Double.isInfinite(
                violation.getPermittedSpeed())
                || violation.getPermittedSpeed() <= 0) {

            throw new InvalidViolationException(
                    "Permitted speed must be greater than zero.");
        }

        // Speed must only be supplied for speeding violations.
        if (violation.getViolationType()
                == ViolationType.OVER_SPEEDING
                && violation.getSpeed()
                <= violation.getPermittedSpeed()) {

            throw new InvalidViolationException(
                    "Speed must exceed permitted speed "
                            + "for an over-speeding violation.");
        }
    }

    // ============================================================
    // VIOLATION DETECTION
    // ============================================================

    /**
     * Detects an over-speeding violation.
     */
    public Violation detectOverSpeeding(
            String violationId,
            String vehicleNumber,
            double speed,
            double permittedSpeed,
            String location,
            String eventId) {

        if (speed <= permittedSpeed) {

            throw new InvalidViolationException(
                    "No over-speeding violation detected. "
                            + "Speed must exceed permitted speed.");
        }

        ViolationSeverity severity =
                determineSpeedSeverity(
                        speed,
                        permittedSpeed);

        return new Violation(
                violationId,
                vehicleNumber,
                ViolationType.OVER_SPEEDING,
                severity,
                speed,
                permittedSpeed,
                location,
                eventId);
    }

    /**
     * Detects signal violation.
     */
    public Violation detectSignalViolation(
            String violationId,
            String vehicleNumber,
            String location,
            String eventId) {

        return new Violation(
                violationId,
                vehicleNumber,
                ViolationType.SIGNAL_VIOLATION,
                ViolationSeverity.HIGH,
                0,
                50,
                location,
                eventId);
    }

    /**
     * Detects illegal parking.
     */
    public Violation detectIllegalParking(
            String violationId,
            String vehicleNumber,
            String location,
            String eventId) {

        return new Violation(
                violationId,
                vehicleNumber,
                ViolationType.ILLEGAL_PARKING,
                ViolationSeverity.MEDIUM,
                0,
                50,
                location,
                eventId);
    }

    /**
     * Determines over-speeding severity.
     *
     * Difference:
     *
     * 1-10 km/h     -> LOW
     * 11-30 km/h    -> MEDIUM
     * >30 km/h      -> HIGH
     */
    public ViolationSeverity determineSpeedSeverity(
            double speed,
            double permittedSpeed) {

        if (Double.isNaN(speed)
                || Double.isInfinite(speed)
                || speed < 0) {

            throw new InvalidViolationException(
                    "Speed cannot be negative.");
        }

        if (Double.isNaN(permittedSpeed)
                || Double.isInfinite(permittedSpeed)
                || permittedSpeed <= 0) {

            throw new InvalidViolationException(
                    "Permitted speed must be greater than zero.");
        }

        if (speed <= permittedSpeed) {

            throw new InvalidViolationException(
                    "Speed does not exceed permitted speed.");
        }

        double excess =
                speed - permittedSpeed;

        if (excess <= 10) {
            return ViolationSeverity.LOW;
        }

        if (excess <= 30) {
            return ViolationSeverity.MEDIUM;
        }

        return ViolationSeverity.HIGH;
    }

    // ============================================================
    // FINE CALCULATION
    // ============================================================

    /**
     * Base fines according to violation type.
     */
    private double getBaseFine(
            ViolationType violationType) {

        switch (violationType) {

            case OVER_SPEEDING:
                return 1000.0;

            case SIGNAL_VIOLATION:
                return 1500.0;

            case ILLEGAL_PARKING:
                return 500.0;

            default:
                throw new InvalidViolationException(
                        "Unknown violation type.");
        }
    }

    /**
     * Severity multiplier.
     */
    private double getSeverityMultiplier(
            ViolationSeverity severity) {

        switch (severity) {

            case LOW:
                return 1.0;

            case MEDIUM:
                return 1.5;

            case HIGH:
                return 2.0;

            default:
                throw new InvalidViolationException(
                        "Unknown severity.");
        }
    }

    /**
     * Calculates fine.
     *
     * Repeated violations receive a higher penalty.
     *
     * First violation:
     * base × severity
     *
     * Second violation:
     * 1.5 × normal fine
     *
     * Third and later:
     * 2 × normal fine
     */
    public double calculateFine(
            Violation violation) {

        validateViolation(violation);

        double baseFine =
                getBaseFine(
                        violation.getViolationType());

        double severityFine =
                baseFine
                        * getSeverityMultiplier(
                        violation.getSeverity());

        int previousViolations =
                countPreviousViolations(
                        violation.getVehicleNumber(),
                        violation.getViolationType());

        double multiplier;

        if (previousViolations == 0) {
            multiplier = 1.0;
        } else if (previousViolations == 1) {
            multiplier = 1.5;
        } else {
            multiplier = 2.0;
        }

        return severityFine * multiplier;
    }

    // ============================================================
    // VIOLATION REGISTRATION
    // ============================================================

    /**
     * Registers violation and creates e-challan.
     */
    public EChallan registerViolation(
            Violation violation) {

        validateViolation(violation);

        Vehicle vehicle =
                getVehicle(
                        violation.getVehicleNumber());

        String eventId =
                violation.getEventId()
                        .trim();

        if (processedViolationEvents
                .contains(eventId)) {

            throw new DuplicateChallanException(
                    "Challan already generated for "
                            + "violation event: "
                            + eventId);
        }

        double fine =
                calculateFine(violation);

        violations.add(violation);

        String challanId =
                generateChallanId();

        EChallan challan =
                new EChallan(
                        challanId,
                        violation,
                        vehicle,
                        fine);

        challans.put(
                challanId,
                challan);

        processedViolationEvents.add(
                eventId);

        return challan;
    }

    private String generateChallanId() {

        return "ECH"
                + (++challanCounter);
    }

    // ============================================================
    // REPEATED VIOLATION SUPPORT
    // ============================================================

    /**
     * Counts previous violations for the same vehicle
     * and same violation type.
     */
    public int countPreviousViolations(
            String vehicleNumber,
            ViolationType violationType) {

        if (vehicleNumber == null
                || vehicleNumber.trim().isEmpty()) {

            return 0;
        }

        if (violationType == null) {
            return 0;
        }

        String normalized =
                normalizeVehicleNumber(
                        vehicleNumber);

        int count = 0;

        for (Violation violation : violations) {

            if (normalizeVehicleNumber(
                    violation.getVehicleNumber())
                    .equals(normalized)
                    && violation.getViolationType()
                    == violationType) {

                count++;
            }
        }

        return count;
    }

    // ============================================================
    // PAYMENT
    // ============================================================

    /**
     * Pays an unpaid challan.
     */
    public void payChallan(
            String challanId) {

        if (challanId == null
                || challanId.trim().isEmpty()) {

            throw new PaymentException(
                    "Challan ID is required.");
        }

        EChallan challan =
                challans.get(
                        challanId.trim()
                                .toUpperCase());

        if (challan == null) {

            throw new PaymentException(
                    "Challan not found: "
                            + challanId);
        }

        if (challan.getPaymentStatus()
                == PaymentStatus.PAID) {

            throw new PaymentException(
                    "Challan is already paid: "
                            + challanId);
        }

        challan.markPaid();
    }

    /**
     * Returns a challan.
     */
    public EChallan getChallan(
            String challanId) {

        if (challanId == null
                || challanId.trim().isEmpty()) {

            throw new PaymentException(
                    "Challan ID is required.");
        }

        EChallan challan =
                challans.get(
                        challanId.trim()
                                .toUpperCase());

        if (challan == null) {

            throw new PaymentException(
                    "Challan not found: "
                            + challanId);
        }

        return challan;
    }

    // ============================================================
    // OUTSTANDING FINE
    // ============================================================

    /**
     * Calculates total unpaid fines for a vehicle.
     */
    public double calculateOutstandingFine(
            String vehicleNumber) {

        Vehicle vehicle =
                getVehicle(vehicleNumber);

        double total = 0.0;

        for (EChallan challan :
                challans.values()) {

            if (normalizeVehicleNumber(
                    challan.getVehicle()
                            .getVehicleNumber())
                    .equals(
                            normalizeVehicleNumber(
                                    vehicle
                                            .getVehicleNumber()))
                    && challan.getPaymentStatus()
                    == PaymentStatus.UNPAID) {

                total += challan.getFineAmount();
            }
        }

        return total;
    }

    // ============================================================
    // HISTORY
    // ============================================================

    /**
     * Returns complete violation history
     * for a vehicle.
     */
    public List<Violation> getViolationHistory(
            String vehicleNumber) {

        getVehicle(vehicleNumber);

        String normalized =
                normalizeVehicleNumber(
                        vehicleNumber);

        List<Violation> result =
                new ArrayList<>();

        for (Violation violation :
                violations) {

            if (normalizeVehicleNumber(
                    violation.getVehicleNumber())
                    .equals(normalized)) {

                result.add(violation);
            }
        }

        return Collections.unmodifiableList(
                result);
    }

    /**
     * Classifies vehicle based on violation history.
     */
    public String classifyVehicle(
            String vehicleNumber) {

        List<Violation> history =
                getViolationHistory(
                        vehicleNumber);

        if (history.isEmpty()) {
            return "LOW_RISK";
        }

        int highSeverity = 0;
        int mediumSeverity = 0;

        for (Violation violation :
                history) {

            if (violation.getSeverity()
                    == ViolationSeverity.HIGH) {

                highSeverity++;

            } else if (violation.getSeverity()
                    == ViolationSeverity.MEDIUM) {

                mediumSeverity++;
            }
        }

        if (highSeverity >= 2
                || history.size() >= 4) {

            return "HIGH_RISK";
        }

        if (highSeverity >= 1
                || mediumSeverity >= 2
                || history.size() >= 2) {

            return "MEDIUM_RISK";
        }

        return "LOW_RISK";
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public int getVehicleCount() {
        return vehicles.size();
    }

    public int getViolationCount() {
        return violations.size();
    }

    public int getChallanCount() {
        return challans.size();
    }

    public List<Violation> getAllViolations() {
        return Collections.unmodifiableList(
                violations);
    }

    public List<EChallan> getAllChallans() {

        List<EChallan> result =
                new ArrayList<>(
                        challans.values());

        result.sort(
                Comparator.comparing(
                        EChallan::getChallanId));

        return Collections.unmodifiableList(
                result);
    }

    // ============================================================
    // MAIN METHOD
    // ============================================================

    public static void main(String[] args) {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        // Register vehicle
        Vehicle vehicle =
                new Vehicle(
                        "TN01AB1234",
                        "Arun Kumar",
                        "9876543210",
                        VehicleType.FOUR_WHEELER);

        system.registerVehicle(vehicle);

        // Detect speeding
        Violation violation =
                system.detectOverSpeeding(
                        "V001",
                        "TN01AB1234",
                        95,
                        60,
                        "Katpadi Road",
                        "EVENT001");

        // Generate challan
        EChallan challan =
                system.registerViolation(
                        violation);

        System.out.println(
                "E-Challan Generated");

        System.out.println(
                "Challan ID: "
                        + challan.getChallanId());

        System.out.println(
                "Vehicle: "
                        + challan.getVehicle()
                        .getVehicleNumber());

        System.out.println(
                "Fine: ₹"
                        + challan.getFineAmount());

        System.out.println(
                "Status: "
                        + challan.getPaymentStatus());

        // Pay challan
        system.payChallan(
                challan.getChallanId());

        System.out.println(
                "After Payment: "
                        + challan.getPaymentStatus());

        System.out.println(
                "Outstanding Fine: ₹"
                        + system
                        .calculateOutstandingFine(
                                "TN01AB1234"));

        System.out.println(
                "Vehicle Classification: "
                        + system
                        .classifyVehicle(
                                "TN01AB1234"));
    }
}