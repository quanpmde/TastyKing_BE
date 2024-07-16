    package com.example.TastyKing.service;

    import lombok.RequiredArgsConstructor;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.time.temporal.TemporalAdjusters;
    import java.util.HashMap;
    import java.util.Map;

    @RequiredArgsConstructor
    @Service
    public class ReportService {
        @Autowired
        private UserService userService;
        @Autowired
        private OrderService orderService;
        private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
        public Map<String, Object> getDailyReport(LocalDate date) {
            long numberOfOrders = orderService.getNumberOfOrdersInDay(date);
            Double totalAmount = orderService.getTotalAmountInDay(date);
            long userCount = userService.countAllUsersWithUserRole();
            Map<String, Object> report = new HashMap<>();

            report.put("numberOfOrders", numberOfOrders);
            report.put("totalAmount", totalAmount);
            report.put("userCount", userCount);
            logger.info("Daily report generated for date {}: Number of Orders: {}, Total Amount: {}, User Count: {}", date, numberOfOrders, totalAmount, userCount);

            return report;
        }

        public Map<String, Object> getMonthlyReport(LocalDate date) {
            long numberOfOrders = orderService.getNumberOfOrdersInMonth(date);
            Double totalAmount = orderService.getTotalAmountInMonth(date);
            long userCount = userService.countAllUsersWithUserRole();
            Map<String, Object> report = new HashMap<>();

            report.put("numberOfOrders", numberOfOrders);
            report.put("totalAmount", totalAmount);
            report.put("userCount", userCount);
            logger.info("Monthly report generated for date {}: Number of Orders: {}, Total Amount: {}, User Count: {}", date, numberOfOrders, totalAmount, userCount);

            return report;
        }

        public Map<String, Object> getYearlyReport(LocalDate date) {
            long numberOfOrders = orderService.getNumberOfOrdersInYear(date);
            Double totalAmount = orderService.getTotalAmountInYear(date);
            long userCount = userService.countAllUsersWithUserRole();
            Map<String, Object> report = new HashMap<>();

            report.put("numberOfOrders", numberOfOrders);
            report.put("totalAmount", totalAmount);
            report.put("userCount", userCount);
            logger.info("Yearly report generated for date {}: Number of Orders: {}, Total Amount: {}, User Count: {}", date, numberOfOrders, totalAmount, userCount);

            return report;
        }

        public Map<String, Double> getMonthlyRevenue() {
            Map<String, Double> monthlyTotalAmount = orderService.getMonthlyTotalAmountForLast12Months();

            // Log the results
            logger.info("Monthly revenue report generated: {}", monthlyTotalAmount);

            return monthlyTotalAmount;
        }
    }