package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.OrderDetailRequest;
import com.example.TastyKing.dto.request.OrderRequest;
import com.example.TastyKing.dto.request.OrderUpdateRequest;
import com.example.TastyKing.dto.request.UpdateOrderStatusRequest;
import com.example.TastyKing.dto.response.OrderDetailResponse;
import com.example.TastyKing.dto.response.OrderResponse;
import com.example.TastyKing.entity.*;
import com.example.TastyKing.enums.OrderStatus;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.repository.*;
import com.example.TastyKing.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public OrderResponse getOrderByOrderID(Long orderID) {
        Order order = orderRepository.findById(orderID).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

        return OrderResponse.builder()
                .orderID(order.getOrderID())
                .user(order.getUser())

                .tables(order.getTable())
                .orderDate(order.getOrderDate())
                .note(order.getNote())
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
        List<Order> orders = orderRepository.findByUser_email(email);
        return orders.stream()
                .map(order -> OrderResponse.builder()
                        .orderID(order.getOrderID())
                        .user(order.getUser())
                        .tables(order.getTable())
                        .orderDate(order.getOrderDate())
                        .note(order.getNote())
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
        order.setOrderStatus(OrderStatus.Canceled.name());
        Order updatedOrder = orderRepository.save(order);

        // Trả về phản hồi sau khi hủy đơn hàng thành công
        return "The order was successfully canceled. Please contact the restaurant for refund assistance";
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> OrderResponse.builder()
                        .orderID(order.getOrderID())
                        .user(order.getUser())
                        .tables(order.getTable())
                        .orderDate(order.getOrderDate())
                        .note(order.getNote())
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
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmOrder(Long orderID) {
        try {
            // Fetch the existing order
            Order order = orderRepository.findById(orderID)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

            // Update the order status to "Confirmed"
            order.setOrderStatus(OrderStatus.Confirmed.name());

            Tables table = order.getTable();
            table.setTableStatus("Booked");
            tableRepository.save(table);
            // Save the updated order
            Order updatedOrder = orderRepository.save(order);

            // Send a confirmation email to the user
            String userEmail = updatedOrder.getUser().getEmail();
            emailUtil.sendOrderConfirmationEmail(userEmail, "" + updatedOrder.getOrderID());

            return "Order confirmed and confirmation email sent successfully.";
        } catch (Exception ex) {
            logger.error("Error occurred while confirming order: {}", ex.getMessage());
            throw new RuntimeException("Failed to confirm order", ex);
        }
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


}


