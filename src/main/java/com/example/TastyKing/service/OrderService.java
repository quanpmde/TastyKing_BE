    package com.example.TastyKing.service;
    
    import com.example.TastyKing.dto.request.*;
    import com.example.TastyKing.dto.response.OrderDetailResponse;
    import com.example.TastyKing.dto.response.OrderResponse;
    import com.example.TastyKing.entity.*;
    import com.example.TastyKing.enums.OrderStatus;
    import com.example.TastyKing.exception.AppException;
    import com.example.TastyKing.exception.ErrorCode;
    import com.example.TastyKing.repository.*;
    import com.example.TastyKing.util.EmailUtil;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.stereotype.Service;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.temporal.ChronoUnit;
    import java.time.temporal.TemporalAdjusters;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;
    
    @Service
    @RequiredArgsConstructor
    public class OrderService {
        private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private VoucherRepository voucherRepository;
        @Autowired
        private TableRepository tableRepository;
        @Autowired
        private FoodRepository foodRepository;
        @Autowired
        private EmailUtil emailUtil;
    
    
        @Transactional(rollbackFor = Exception.class)
        public OrderResponse createOrder(OrderRequest request) {
            try {
                User user = userRepository.findByEmail(request.getUser().getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

                Tables tables = tableRepository.findById(request.getTables().getTableID())
                        .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));

                if (tables.getTableStatus().equalsIgnoreCase("Booked") || tables.getTableStatus().equalsIgnoreCase("Serving")) {
                    throw new AppException(ErrorCode.TABLE_NOT_EXIST);
                }

                Order order;
                if (request.getOrderID() != null) {
                    // Update existing order
                    order = orderRepository.findById(request.getOrderID())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
                    // Update order details if necessary (e.g., update total amount)
                    order.setTotalAmount(order.getTotalAmount() + request.getTotalAmount());
                    order.setOrderDate(LocalDateTime.now());
                    order.setNote(request.getNote());
                    order.setNumOfCustomer(request.getNumOfCustomer());
                    order.setCustomerName(request.getCustomerName());
                    order.setBookingDate(request.getBookingDate());
                    order.setCustomerPhone(request.getCustomerPhone());
                } else {
                    // Create new order
                    order = Order.builder()
                            .user(user)
                            .table(tables)
                            .orderDate(LocalDateTime.now())
                            .note(request.getNote())
                            .totalAmount(request.getTotalAmount())
                            .numOfCustomer(request.getNumOfCustomer())
                            .customerName(request.getCustomerName())
                            .bookingDate(request.getBookingDate())
                            .customerPhone(request.getCustomerPhone())
                            .orderStatus(OrderStatus.Processing.name())
                            .build();
                }

                List<OrderDetail> orderDetails = request.getOrderDetails().stream()
                        .map(detail -> {
                            OrderDetailId detailId = new OrderDetailId(order.getOrderID(), detail.getFoodID());
                            Food food = foodRepository.findById(detail.getFoodID())
                                    .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));
                            return OrderDetail.builder()
                                    .id(detailId)
                                    .order(order)
                                    .food(food)
                                    .quantity(detail.getQuantity())
                                    .build();
                        })
                        .collect(Collectors.toList());

                if (order.getOrderDetails() != null) {
                    order.getOrderDetails().addAll(orderDetails);
                } else {
                    order.setOrderDetails(orderDetails);
                }

                // Set deposit based on order details and table name
                if (orderDetails.isEmpty()) {
                    order.setDeposit(100000.0);
                } else if ("Hall1".equalsIgnoreCase(tables.getTableName()) || "Hall2".equalsIgnoreCase(tables.getTableName())) {
                    order.setDeposit(2000000.0);
                } else {
                    order.setDeposit(0.0);
                }

                Order savedOrder = orderRepository.save(order);

                return OrderResponse.builder()
                        .orderID(savedOrder.getOrderID())
                        .user(savedOrder.getUser())
                        .tables(savedOrder.getTable())
                        .orderDate(savedOrder.getOrderDate())
                        .note(savedOrder.getNote())
                        .totalAmount(savedOrder.getTotalAmount())
                        .numOfCustomer(savedOrder.getNumOfCustomer())
                        .customerName(savedOrder.getCustomerName())
                        .bookingDate(savedOrder.getBookingDate())
                        .customerPhone(savedOrder.getCustomerPhone())
                        .orderStatus(savedOrder.getOrderStatus())
                        .deposit(savedOrder.getDeposit())
                        .orderDetails(savedOrder.getOrderDetails().stream()
                                .map(detail -> OrderDetailResponse.builder()
                                        .foodID(detail.getFood().getFoodID())
                                        .foodName(detail.getFood().getFoodName())
                                        .foodPrice(detail.getFood().getFoodPrice())
                                        .foodImage(detail.getFood().getFoodImage())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            } catch (Exception ex) {
                logger.error("Error occurred while creating order: {}", ex.getMessage());
                throw new RuntimeException("Failed to create order", ex);
            }
        }
    
    
        @Transactional(rollbackFor = Exception.class)
        @PreAuthorize("hasRole('ADMIN')")
        public OrderResponse updateOrderStatus(Long orderID, UpdateOrderStatusRequest request) {
            try {
                // Fetch the existing order
                Order order = orderRepository.findById(orderID)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
    
                // Update the order status
                order.setOrderStatus(request.getOrderStatus());
    
                // Save the updated order
                Order updatedOrder = orderRepository.save(order);
    
                // Prepare OrderResponse
                return OrderResponse.builder()
                        .orderID(updatedOrder.getOrderID())
                        .user(updatedOrder.getUser())
                        .tables(updatedOrder.getTable())
                        .orderDate(updatedOrder.getOrderDate())
                        .note(updatedOrder.getNote())
                        .totalAmount(updatedOrder.getTotalAmount())
                        .numOfCustomer(updatedOrder.getNumOfCustomer())
                        .customerName(updatedOrder.getCustomerName())
                        .bookingDate(updatedOrder.getBookingDate())
                        .customerPhone(updatedOrder.getCustomerPhone())
                        .orderStatus(updatedOrder.getOrderStatus())
                        .orderDetails(updatedOrder.getOrderDetails().stream()
                                .map(detail -> OrderDetailResponse.builder()
                                        .foodID(detail.getFood().getFoodID())
                                        .foodName(detail.getFood().getFoodName())
                                        .foodPrice(detail.getFood().getFoodPrice())
                                        .foodImage(detail.getFood().getFoodImage())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
    
            } catch (Exception ex) {
                logger.error("Error occurred while updating order status: {}", ex.getMessage());
                throw new RuntimeException("update status failed");
            }
        }
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        public OrderResponse getOrderByOrderID(Long orderID) {
            Order order = orderRepository.findById(orderID).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
    
            return OrderResponse.builder()
                    .orderID(order.getOrderID())
                    .user(order.getUser())
    
                    .tables(order.getTable())
                    .orderDate(order.getOrderDate())
                    .note(order.getNote())
                    .deposit(order.getDeposit())
                    .totalAmount(order.getTotalAmount())
                    .numOfCustomer(order.getNumOfCustomer())
                    .customerName(order.getCustomerName())
                    .bookingDate(order.getBookingDate())
                    .customerPhone(order.getCustomerPhone())
                    .orderStatus(order.getOrderStatus())
                    .orderDetails(order.getOrderDetails().stream()
                            .map(detail -> OrderDetailResponse.builder()
                                    .foodID(detail.getFood().getFoodID())
                                    .foodName(detail.getFood().getFoodName())
                                    .foodPrice(detail.getFood().getFoodPrice())
                                    .foodImage(detail.getFood().getFoodImage())
                                    .quantity(detail.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }


        public List<OrderResponse> getOrderByUser(String email) {
            List<Order> orders = orderRepository.findByUser_emailOrderByOrderIDDesc(email);
            return orders.stream()
                    .map(this::convertToOrderResponse)
                    .collect(Collectors.toList());
        }

        private OrderResponse convertToOrderResponse(Order order) {
            return OrderResponse.builder()
                    .orderID(order.getOrderID())
                    .user(order.getUser())
                    .tables(order.getTable())
                    .orderDate(order.getOrderDate())
                    .note(order.getNote())
                    .deposit(order.getDeposit())
                    .totalAmount(order.getTotalAmount())
                    .numOfCustomer(order.getNumOfCustomer())
                    .customerName(order.getCustomerName())
                    .bookingDate(order.getBookingDate())
                    .customerPhone(order.getCustomerPhone())
                    .orderStatus(order.getOrderStatus())
                    .orderDetails(order.getOrderDetails().stream()
                            .map(detail -> OrderDetailResponse.builder()
                                    .foodID(detail.getFood().getFoodID())
                                    .foodName(detail.getFood().getFoodName())
                                    .foodPrice(detail.getFood().getFoodPrice())
                                    .foodImage(detail.getFood().getFoodImage())
                                    .quantity(detail.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }

        public String cancelOrder(Long orderID) {
            Order order = orderRepository.findById(orderID)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
    
            // Lấy thời gian hiện tại và thời gian nhận bàn
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime bookingDateTime = order.getBookingDate();
    
            // Kiểm tra xem thời gian hiện tại có trước 24 tiếng so với thời gian nhận bàn không
            if (ChronoUnit.HOURS.between(now, bookingDateTime) < 24) {
                throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
            }
    
            // Cập nhật trạng thái đơn hàng nếu hủy hợp lệ
            Tables tables = tableRepository.findById(order.getTable().getTableID()).orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));
    
            order.setOrderStatus(OrderStatus.Canceled.name());
            tables.setTableStatus("Available");
            Order updatedOrder = orderRepository.save(order);
            Tables tablesUpdate = tableRepository.save(tables);
            // Trả về phản hồi sau khi hủy đơn hàng thành công
            return "The order was successfully canceled. Please contact the restaurant for refund assistance";
        }



        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        public List<OrderResponse> getAllOrder() {
            List<Order> orders = orderRepository.findAllOrderByOrderIDDesc();
            return orders.stream()
                    .map(order -> OrderResponse.builder()
                            .orderID(order.getOrderID())
                            .user(order.getUser())
                            .tables(order.getTable())
                            .orderDate(order.getOrderDate())
                            .note(order.getNote())
                            .deposit(order.getDeposit())
                            .totalAmount(order.getTotalAmount())
                            .numOfCustomer(order.getNumOfCustomer())
                            .customerName(order.getCustomerName())
                            .bookingDate(order.getBookingDate())
                            .customerPhone(order.getCustomerPhone())
                            .orderStatus(order.getOrderStatus())
                            .orderDetails(order.getOrderDetails().stream()
                                    .map(detail -> OrderDetailResponse.builder()
                                            .foodID(detail.getFood().getFoodID())
                                            .foodName(detail.getFood().getFoodName())
                                            .foodPrice(detail.getFood().getFoodPrice())
                                            .foodImage(detail.getFood().getFoodImage())
                                            .quantity(detail.getQuantity())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());
        }

        @Transactional(rollbackFor = Exception.class)
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        public String cancelOrderByAdmin(Long orderID) {
            try {
                Order order = orderRepository.findById(orderID)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

                // Fetch the table associated with the order
                Tables table = tableRepository.findById(order.getTable().getTableID())
                        .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));

                // Check if the table is booked and if the booking is within the allowed cancellation period
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime bookingDateTime = order.getBookingDate();

                if (!table.getTableStatus().equalsIgnoreCase("Booked")) {
                    throw new RuntimeException("Table is not booked, cannot cancel order.");
                }

                // Update the order and table statuses
                order.setOrderStatus(OrderStatus.Canceled.name());
                table.setTableStatus("Available");

                Order updatedOrder = orderRepository.save(order);
                Tables updatedTable = tableRepository.save(table);

                // Send cancellation email to the user
                String userEmail = updatedOrder.getUser().getEmail();
                emailUtil.sendOrderCancelEmail(userEmail, "" + updatedOrder.getOrderID());

                return "The order was successfully canceled. Email has been sent to the customer.";
            } catch (Exception ex) {
                logger.error("Error occurred while canceling order: {}", ex.getMessage());
                throw new RuntimeException("Failed to cancel order", ex);
            }
        }

        @Transactional(rollbackFor = Exception.class)
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public String confirmOrder(Long orderID) {
            try {
                // Fetch the existing order
                Order order = orderRepository.findById(orderID)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

                // Update the order status to "Confirmed"


                // Update payment status to "PAID" if payment exists
                Payment payment = order.getPayment();
                if (payment != null) {
                    payment.setPaymentStatus("PAID");
                }

                Tables table = order.getTable();
                if (table.getTableStatus().equalsIgnoreCase("Available")) {

                    table.setTableStatus("Booked");
                    tableRepository.save(table);
                    order.setOrderStatus(OrderStatus.Confirmed.name());
                    // Save the updated order
                    Order updatedOrder = orderRepository.save(order);

                    // Send a confirmation email to the user
                    String userEmail = updatedOrder.getUser().getEmail();
                    emailUtil.sendOrderConfirmationEmail(userEmail, "" + updatedOrder.getOrderID());
                    return "Order confirm successfull. Email has sent to customer";
                }
                else {
                    return "Can't confirm order. This table has not exist";
                }
            } catch (Exception ex) {
                logger.error("Error occurred while confirming order: {}", ex.getMessage());
                throw new RuntimeException("Failed to confirm order", ex);
            }
        }

        @Transactional(rollbackFor = Exception.class)
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        public String receiveTable(Long orderID) {
    
            // Fetch the existing order
            Order order = orderRepository.findById(orderID)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
    
            // Check if booking date is greater than current time
            if (order.getBookingDate().isAfter(LocalDateTime.now())) {
                throw new AppException(ErrorCode.INVALID_BOOKING_TIME); // Custom exception for invalid time
            }
    
            // Update the order status to "Confirmed"
            order.setOrderStatus(OrderStatus.InProgress.name());
    
            Tables table = order.getTable();
            table.setTableStatus("Serving");
            tableRepository.save(table);
    
            // Save the updated order
            Order updatedOrder = orderRepository.save(order);
    
            return "The customer has successfully received the table.";
        }
        @Transactional(rollbackFor = Exception.class)
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        public String doneDeal(Long orderID) {

            // Fetch the existing order
            Order order = orderRepository.findById(orderID)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

            // Check if booking date is greater than current time


            // Update the order status to "Confirmed"
            order.setOrderStatus(OrderStatus.Done.name());

            Tables table = order.getTable();
            table.setTableStatus("Available");
            tableRepository.save(table);

            // Save the updated order
            Order updatedOrder = orderRepository.save(order);

            return "The order has been completed";
        }
    
    
        @Transactional(rollbackFor = Exception.class)
    
        public OrderResponse updateOrder(OrderUpdateRequest request) {
            try {
                User user = userRepository.findByEmail(request.getUser().getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
    
                Tables tables = tableRepository.findById(request.getTables().getTableID())
                        .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));
    
                Order order;
                if (request.getOrderID() != null) {
                    // Update existing order
                    order = orderRepository.findById(request.getOrderID())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
                    // Update order details
                    order.setTotalAmount(order.getTotalAmount() + request.getTotalAmount());
                    order.setOrderDate(LocalDateTime.now());
    
                    order.setNote(request.getNote());
                    order.setNumOfCustomer(request.getNumOfCustomer());
                    order.setCustomerName(request.getCustomerName());
                    order.setBookingDate(request.getBookingDate());
                    order.setCustomerPhone(request.getCustomerPhone());
    
                    // Manage existing order details
                    Map<Long, OrderDetail> existingOrderDetails = order.getOrderDetails()
                            .stream()
                            .collect(Collectors.toMap(detail -> detail.getFood().getFoodID(), detail -> detail));
    
                    for (OrderDetailRequest detailRequest : request.getOrderDetails()) {
                        Food food = foodRepository.findById(detailRequest.getFoodID())
                                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));
    
                        if (existingOrderDetails.containsKey(detailRequest.getFoodID())) {
                            OrderDetail existingDetail = existingOrderDetails.get(detailRequest.getFoodID());
                            existingDetail.setQuantity(existingDetail.getQuantity() + detailRequest.getQuantity());
                        } else {
                            OrderDetailId detailId = new OrderDetailId(order.getOrderID(), detailRequest.getFoodID());
                            OrderDetail newDetail = OrderDetail.builder()
                                    .id(detailId)
                                    .order(order)
                                    .food(food)
                                    .quantity(detailRequest.getQuantity())
                                    .build();
                            order.getOrderDetails().add(newDetail);
                        }
                    }
                } else {
                    // Create new order
                    order = Order.builder()
                            .user(user)
                            .table(tables)
                            .orderDate(LocalDateTime.now())
                            .note(request.getNote())
                            .totalAmount(request.getTotalAmount())
                            .numOfCustomer(request.getNumOfCustomer())
                            .customerName(request.getCustomerName())
                            .bookingDate(request.getBookingDate())
                            .customerPhone(request.getCustomerPhone())
                            .orderStatus(OrderStatus.Processing.name())
                            .build();
    
                    List<OrderDetail> orderDetails = request.getOrderDetails().stream()
                            .map(detail -> {
                                OrderDetailId detailId = new OrderDetailId(order.getOrderID(), detail.getFoodID());
                                Food food = foodRepository.findById(detail.getFoodID())
                                        .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));
                                return OrderDetail.builder()
                                        .id(detailId)
                                        .order(order)
                                        .food(food)
                                        .quantity(detail.getQuantity())
                                        .build();
                            })
                            .collect(Collectors.toList());
    
                    order.setOrderDetails(orderDetails);
                }
    
                Order savedOrder = orderRepository.save(order);
    
    
                return OrderResponse.builder()
                        .orderID(savedOrder.getOrderID())
                        .user(savedOrder.getUser())
                        .tables(savedOrder.getTable())
                        .orderDate(savedOrder.getOrderDate())
                        .note(savedOrder.getNote())
                        .totalAmount(savedOrder.getTotalAmount())
                        .numOfCustomer(savedOrder.getNumOfCustomer())
                        .customerName(savedOrder.getCustomerName())
                        .bookingDate(savedOrder.getBookingDate())
                        .customerPhone(savedOrder.getCustomerPhone())
                        .orderStatus(savedOrder.getOrderStatus())
                        .orderDetails(savedOrder.getOrderDetails().stream()
                                .map(detail -> OrderDetailResponse.builder()
                                        .foodID(detail.getFood().getFoodID())
                                        .foodName(detail.getFood().getFoodName())
                                        .foodPrice(detail.getFood().getFoodPrice())
                                        .foodImage(detail.getFood().getFoodImage())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            } catch (Exception ex) {
                logger.error("Error occurred while creating order: {}", ex.getMessage());
                throw new RuntimeException("Failed to create order", ex);
            }
        }
    
        @Transactional(rollbackFor = Exception.class)
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        public OrderResponse createOrderByAdmin(OrderRequest request) {
            try {
                User user = userRepository.findByEmail(request.getUser().getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
    
                Tables tables = tableRepository.findById(request.getTables().getTableID())
                        .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));
    
    
                Order order;
                if (request.getOrderID() != null) {
                    // Update existing order
                    order = orderRepository.findById(request.getOrderID())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
                    // Update order details if necessary (e.g., update total amount)
                    order.setTotalAmount(order.getTotalAmount() + request.getTotalAmount());
                    order.setOrderDate(LocalDateTime.now());
                    order.setNote(request.getNote());
                    order.setNumOfCustomer(request.getNumOfCustomer());
                    order.setCustomerName(request.getCustomerName());
                    order.setBookingDate(request.getBookingDate());

                    order.setCustomerPhone(request.getCustomerPhone());
                } else {
                    // Create new order
                    order = Order.builder()
                            .user(user)
                            .table(tables)
                            .orderDate(LocalDateTime.now())
                            .note(request.getNote())
                            .totalAmount(request.getTotalAmount())
                            .numOfCustomer(request.getNumOfCustomer())
                            .customerName(request.getCustomerName())
                            .bookingDate(request.getBookingDate())
                            .customerPhone(request.getCustomerPhone())
                            .deposit(0.0)
                            .orderStatus(OrderStatus.InProgressNotPaying.name())
                            .build();
                }
    
                List<OrderDetail> orderDetails = request.getOrderDetails().stream()
                        .map(detail -> {
                            OrderDetailId detailId = new OrderDetailId(order.getOrderID(), detail.getFoodID());
                            Food food = foodRepository.findById(detail.getFoodID())
                                    .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));
                            return OrderDetail.builder()
                                    .id(detailId)
                                    .order(order)
                                    .food(food)
                                    .quantity(detail.getQuantity())
                                    .build();
                        })
                        .collect(Collectors.toList());
    
                if (order.getOrderDetails() != null) {
                    order.getOrderDetails().addAll(orderDetails);
                } else {
                    order.setOrderDetails(orderDetails);
                }
                Tables table = order.getTable();
                table.setTableStatus("Serving");
                tableRepository.save(table);
                Order savedOrder = orderRepository.save(order);
              
                return OrderResponse.builder()
                        .orderID(savedOrder.getOrderID())
                        .user(savedOrder.getUser())
                        .tables(savedOrder.getTable())
                        .orderDate(savedOrder.getOrderDate())
                        .note(savedOrder.getNote())
                        .totalAmount(savedOrder.getTotalAmount())
                        .numOfCustomer(savedOrder.getNumOfCustomer())
                        .customerName(savedOrder.getCustomerName())
                        .bookingDate(savedOrder.getBookingDate())
                        .deposit(savedOrder.getDeposit())
                        .customerPhone(savedOrder.getCustomerPhone())
                        .orderStatus(savedOrder.getOrderStatus())
                        .orderDetails(savedOrder.getOrderDetails().stream()
                                .map(detail -> OrderDetailResponse.builder()
                                        .foodID(detail.getFood().getFoodID())
                                        .foodName(detail.getFood().getFoodName())
                                        .foodPrice(detail.getFood().getFoodPrice())
                                        .foodImage(detail.getFood().getFoodImage())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            } catch (Exception ex) {
                logger.error("Error occurred while creating order: {}", ex.getMessage());
                throw new RuntimeException("Failed to create order", ex);
            }
        }

        public long getNumberOfOrdersInDay(LocalDate date) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            return orderRepository.countOrdersByStatusAndDateRange(startOfDay, endOfDay);
        }

        public long getNumberOfOrdersInMonth(LocalDate date) {
            LocalDateTime startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            LocalDateTime endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
            return orderRepository.countOrdersByStatusAndDateRange(startOfMonth, endOfMonth);
        }

        public long getNumberOfOrdersInYear(LocalDate date) {
            LocalDateTime startOfYear = date.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
            LocalDateTime endOfYear = date.with(TemporalAdjusters.lastDayOfYear()).atTime(23, 59, 59);
            return orderRepository.countOrdersByStatusAndDateRange(startOfYear, endOfYear);
        }

        public Double getTotalAmountInDay(LocalDate date) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            Double totalAmount = orderRepository.sumTotalAmountByStatusAndDateRange(startOfDay, endOfDay);
            return totalAmount != null ? totalAmount : 0.0;
        }

        public Double getTotalAmountInMonth(LocalDate date) {
            LocalDateTime startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            LocalDateTime endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
            Double totalAmount = orderRepository.sumTotalAmountByStatusAndDateRange(startOfMonth, endOfMonth);
            return totalAmount != null ? totalAmount : 0.0;
        }

        public Double getTotalAmountInYear(LocalDate date) {
            LocalDateTime startOfYear = date.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
            LocalDateTime endOfYear = date.with(TemporalAdjusters.lastDayOfYear()).atTime(23, 59, 59);
            Double totalAmount = orderRepository.sumTotalAmountByStatusAndDateRange(startOfYear, endOfYear);
            return totalAmount != null ? totalAmount : 0.0;
        }
        public Map<String, Double> getMonthlyTotalAmountForLast12Months() {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minus(12, ChronoUnit.MONTHS);

            List<Object[]> results = orderRepository.findMonthlyTotalAmountByStatusAndDateRange(start, end);
            Map<String, Double> monthlyTotalAmount = new HashMap<>();

            for (Object[] result : results) {
                int year = (int) result[0];
                int month = (int) result[1];
                Double total = (Double) result[2];
                String key = year + "-" + (month < 10 ? "0" + month : month); // format YYYY-MM
                monthlyTotalAmount.put(key, total);
            }

            // Log the results
            logger.info("Monthly Total Amount for the last 12 months: {}", monthlyTotalAmount);

            return monthlyTotalAmount;
        }

        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Transactional(rollbackFor = Exception.class)
        public OrderResponse updateOrderByAdmin(OrderUpdateRequest request) {
            try {
                User user = userRepository.findByEmail(request.getUser().getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

                Tables newTable = tableRepository.findById(request.getTables().getTableID())
                        .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));

                Order order;
                if (request.getOrderID() != null) {
                    // Update existing order
                    order = orderRepository.findById(request.getOrderID())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

                    // Update table status if table is changed
                    Tables oldTable = order.getTable();
                    if (!oldTable.getTableID().equals(newTable.getTableID())) {
                        oldTable.setTableStatus("Available");
                        tableRepository.save(oldTable);
                        newTable.setTableStatus("Serving");
                        tableRepository.save(newTable);
                    }

                    // Update order details
                    order.setTotalAmount(order.getTotalAmount() + request.getTotalAmount());
                    order.setOrderDate(LocalDateTime.now());
                    order.setTable(newTable);
                    order.setNote(request.getNote());
                    order.setNumOfCustomer(request.getNumOfCustomer());
                    order.setCustomerName(request.getCustomerName());
                    order.setBookingDate(request.getBookingDate());
                    order.setCustomerPhone(request.getCustomerPhone());

                    // Manage existing order details
                    Map<Long, OrderDetail> existingOrderDetails = order.getOrderDetails()
                            .stream()
                            .collect(Collectors.toMap(detail -> detail.getFood().getFoodID(), detail -> detail));

                    for (OrderDetailRequest detailRequest : request.getOrderDetails()) {
                        Food food = foodRepository.findById(detailRequest.getFoodID())
                                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));

                        if (existingOrderDetails.containsKey(detailRequest.getFoodID())) {
                            OrderDetail existingDetail = existingOrderDetails.get(detailRequest.getFoodID());
                            existingDetail.setQuantity(existingDetail.getQuantity() + detailRequest.getQuantity());
                        } else {
                            OrderDetailId detailId = new OrderDetailId(order.getOrderID(), detailRequest.getFoodID());
                            OrderDetail newDetail = OrderDetail.builder()
                                    .id(detailId)
                                    .order(order)
                                    .food(food)
                                    .quantity(detailRequest.getQuantity())
                                    .build();
                            order.getOrderDetails().add(newDetail);
                        }
                    }
                } else {
                    // Create new order
                    order = Order.builder()
                            .user(user)
                            .table(newTable)
                            .orderDate(LocalDateTime.now())
                            .note(request.getNote())
                            .totalAmount(request.getTotalAmount())
                            .numOfCustomer(request.getNumOfCustomer())
                            .customerName(request.getCustomerName())
                            .bookingDate(request.getBookingDate())
                            .customerPhone(request.getCustomerPhone())
                            .orderStatus(OrderStatus.InProgressNotPaying.name())
                            .build();

                    List<OrderDetail> orderDetails = request.getOrderDetails().stream()
                            .map(detail -> {
                                OrderDetailId detailId = new OrderDetailId(order.getOrderID(), detail.getFoodID());
                                Food food = foodRepository.findById(detail.getFoodID())
                                        .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));
                                return OrderDetail.builder()
                                        .id(detailId)
                                        .order(order)
                                        .food(food)
                                        .quantity(detail.getQuantity())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    order.setOrderDetails(orderDetails);

                    // Set the new table status to "Serving"
                    newTable.setTableStatus("Serving");
                    tableRepository.save(newTable);
                }

                Order savedOrder = orderRepository.save(order);

                return OrderResponse.builder()
                        .orderID(savedOrder.getOrderID())
                        .user(savedOrder.getUser())
                        .tables(savedOrder.getTable())
                        .orderDate(savedOrder.getOrderDate())
                        .note(savedOrder.getNote())
                        .totalAmount(savedOrder.getTotalAmount())
                        .numOfCustomer(savedOrder.getNumOfCustomer())
                        .customerName(savedOrder.getCustomerName())
                        .bookingDate(savedOrder.getBookingDate())
                        .customerPhone(savedOrder.getCustomerPhone())
                        .orderStatus(savedOrder.getOrderStatus())
                        .orderDetails(savedOrder.getOrderDetails().stream()
                                .map(detail -> OrderDetailResponse.builder()
                                        .foodID(detail.getFood().getFoodID())
                                        .foodName(detail.getFood().getFoodName())
                                        .foodPrice(detail.getFood().getFoodPrice())
                                        .foodImage(detail.getFood().getFoodImage())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            } catch (Exception ex) {
                logger.error("Error occurred while creating order: {}", ex.getMessage());
                throw new RuntimeException("Failed to create order", ex);
            }
        }

        public Double getTotalAmountByOrderID(Long orderID) {
            return orderRepository.findTotalAmountByOrderID(orderID);
        }
        public OrderResponse updateOnlyOrderInfo(UpdateOrderInformation request, Long orderID) {
            try {
                // Find the order by ID
                Order order = orderRepository.findById(orderID)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
                User user = userRepository.findByEmail(request.getUser().getEmail()).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
Tables tables = tableRepository.findById(request.getTables().getTableID()).orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXIST));
                // Update order information
                order.setUser(user);
                order.setOrderDate(order.getOrderDate());
                order.setNote(request.getNote());
                order.setTable(tables);
                order.setNumOfCustomer(request.getNumOfCustomer());
                order.setCustomerName(request.getCustomerName());
                order.setBookingDate(request.getBookingDate());
                order.setCustomerPhone(request.getCustomerPhone());

                // Save the updated order
                Order savedOrder = orderRepository.save(order);

                // Create and return response
                return OrderResponse.builder()
                        .orderID(savedOrder.getOrderID())
                        .user(savedOrder.getUser())
                        .tables(savedOrder.getTable())
                        .orderDate(savedOrder.getOrderDate())
                        .note(savedOrder.getNote())
                        .totalAmount(savedOrder.getTotalAmount())
                        .numOfCustomer(savedOrder.getNumOfCustomer())
                        .customerName(savedOrder.getCustomerName())
                        .bookingDate(savedOrder.getBookingDate())
                        .customerPhone(savedOrder.getCustomerPhone())
                        .orderStatus(savedOrder.getOrderStatus())
                        .orderDetails(savedOrder.getOrderDetails().stream()
                                .map(detail -> OrderDetailResponse.builder()
                                        .foodID(detail.getFood().getFoodID())
                                        .foodName(detail.getFood().getFoodName())
                                        .foodPrice(detail.getFood().getFoodPrice())
                                        .foodImage(detail.getFood().getFoodImage())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            } catch (Exception ex) {
                logger.error("Error occurred while updating order info: {}", ex.getMessage());
                throw new RuntimeException("Failed to update order info", ex);
            }
        }


    }
    
    
