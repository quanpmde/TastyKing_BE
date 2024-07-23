package com.example.TastyKing.service;

import com.example.TastyKing.configuration.VNPayConfig;
import com.example.TastyKing.dto.request.PaymentRequest;
import com.example.TastyKing.dto.response.PaymentResponse;
import com.example.TastyKing.entity.Order;
import com.example.TastyKing.entity.Payment;
import com.example.TastyKing.entity.RewardPoint;
import com.example.TastyKing.entity.Tables;
import com.example.TastyKing.enums.OrderStatus;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.mapper.PaymentMapper;
import com.example.TastyKing.repository.OrderRepository;
import com.example.TastyKing.repository.PaymentRepository;
import com.example.TastyKing.repository.RewardPointRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RewardPointRepository rewardPointRepository;


    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createNewPayment(PaymentRequest request) throws IOException{
        // Tìm order theo orderID từ request
        Order order;
        if (request.getOrderID() != null) {
            order = orderRepository.findById(request.getOrderID()).orElseThrow(() ->
                    new AppException(ErrorCode.ORDER_NOT_EXIST));
        } else {
            throw new AppException(ErrorCode.ORDERID_MUST_BE_PROVIDED);
        }

        // Tính tổng số tiền của tất cả các order cần thanh toán
        int totalAmount = (int) Math.round(order.getTotalAmount());

        // Tạo một payment mới và đặt trạng thái
        Payment payment = paymentMapper.toPayment(request);
        payment.setPaymentStatus("NOT_PAY_YET");
        payment.setOrder(order);
        payment.setPaymentAmount(totalAmount); // Đặt paymentAmount
        payment.setPaymentDate(new Date());
        payment.setPaymentDescription(order.getCustomerName() + " paying for order: " + order.getOrderID() + "Total: " + order.getTotalAmount());
        // Thiết lập lại mối quan hệ ngược (bi-directional relationship)
        order.setPayment(payment);

        // Nếu phương thức thanh toán là VNPAY, gọi createVNPayOrder
        String paymentUrl = null;
        if ("VNPAY".equals(payment.getPaymentMethod())) {
            String urlReturn = "http://localhost:8080/TastyKing"; // URL trả về của bạn
            paymentUrl = createVNPayOrder(totalAmount, "Payment for orderID: " + order.getOrderID(), urlReturn);
            orderService.confirmOrder(order.getOrderID());
        }

        // Lưu payment
        Payment newPayment = paymentRepository.save(payment);

        // Tạo response và thiết lập paymentUrl nếu có
        PaymentResponse paymentResponse = paymentMapper.toPaymentResponseWithUrl(newPayment, paymentUrl);
        paymentResponse.setOrderID(order.getOrderID());

        // Cập nhật trạng thái của tất cả các order và bill nếu paymentStatus là "paid"
        if ("PAID".equals(payment.getPaymentStatus())) {
            updatePaymentStatus(newPayment.getPaymentID(), payment.getPaymentStatus());
        }

        return paymentResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createNewPaymentByAdmin(PaymentRequest request) throws IOException {
        // Tìm order theo orderID từ request
        Order order;
        if (request.getOrderID() != null) {
            order = orderRepository.findById(request.getOrderID()).orElseThrow(() ->
                    new AppException(ErrorCode.ORDER_NOT_EXIST));
        } else {
            throw new AppException(ErrorCode.ORDERID_MUST_BE_PROVIDED);
        }

        // Tính tổng số tiền của tất cả các order cần thanh toán
        int totalAmount = (int) Math.round(order.getTotalAmount());

        // Tạo một payment mới và đặt trạng thái
        Payment payment = paymentMapper.toPayment(request);
        payment.setPaymentStatus("PAID"); // Set payment status to PAID
        payment.setOrder(order);
        payment.setPaymentAmount(totalAmount); // Đặt paymentAmount
        payment.setPaymentDate(new Date());
        payment.setPaymentDescription(order.getCustomerName() + " paying for order: " + order.getOrderID() + " Total: " + order.getTotalAmount() + " VND");
        payment.setPaymentMethod(request.getPaymentMethod());

        // Thiết lập lại mối quan hệ ngược (bi-directional relationship)
        order.setPayment(payment);

        // Lưu payment
        Payment newPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái của tất cả các order và bill nếu paymentStatus là "paid"
        if ("PAID".equalsIgnoreCase(payment.getPaymentStatus())) {
            updatePaymentStatus(newPayment.getPaymentID(), payment.getPaymentStatus());
            // Update order status to Done
            order.setOrderStatus(OrderStatus.Done.name());
            Tables tables = order.getTable();
            tables.setTableStatus("Available");
            orderRepository.save(order);
        }

        // Tạo response
        PaymentResponse paymentResponse = paymentMapper.toPaymentResponseWithUrl(newPayment, null);
        paymentResponse.setOrderID(order.getOrderID());

        return paymentResponse;
    }
    public String createVNPayOrder(int total, String orderInfor, String urlReturn){
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total*100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        urlReturn += VNPayConfig.vnp_Returnurl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    public int orderReturn(HttpServletRequest request){
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                // Thanh toán thành công, cập nhật trạng thái payment
                String txnRef = request.getParameter("vnp_TxnRef");
                int paymentId = Integer.parseInt(txnRef); // Sử dụng txnRef như ID payment

                updatePaymentStatus(paymentId, "paid");
                return 1; // Thành công
            } else {
                return 0; // Thất bại
            }
        } else {
            return -1; // Lỗi xác thực chữ ký
        }
    }

    public void updatePaymentStatus(int paymentID, String newStatus) {
        Payment payment = paymentRepository.findById(paymentID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        if ("PAID".equalsIgnoreCase(newStatus) || "NOT_PAY_YET".equalsIgnoreCase(newStatus)) {
            // Cập nhật trạng thái của payment
            payment.setPaymentStatus(newStatus);
            paymentRepository.save(payment);

        } else {
            throw new AppException(ErrorCode.ONLY_PAY_OR_NOT_PAY_YET_IS_ALLOWED);
        }
    }

    public PaymentResponse updatePayment(int paymentID, PaymentRequest paymentRequest) {
        // Tìm payment theo paymentID
        Payment payment = paymentRepository.findById(paymentID).orElseThrow(() ->
                new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        // Cập nhật thông tin payment từ paymentRequest
        if (paymentRequest.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        }
        if (paymentRequest.getPaymentAmount() != null) {
            payment.setPaymentAmount(paymentRequest.getPaymentAmount());
        }
        if (paymentRequest.getPaymentStatus() != null) {
            payment.setPaymentStatus(paymentRequest.getPaymentStatus());
        }
        payment.setPaymentDescription(payment.getOrder().getCustomerName() + " paying for order " + payment.getOrder().getOrderID() + " Total: " + payment.getPaymentAmount());
        payment.setPaymentDate(new Date());

        payment.setPaymentStatus("PAID");
        payment.getOrder().setOrderStatus(OrderStatus.Done.name());
        Tables tables = payment.getOrder().getTable();
        tables.setTableStatus("Available");

        // Cập nhật reward points
        updateRewardPoints(payment);

        // Lưu thông tin payment cập nhật
        Payment updatedPayment = paymentRepository.save(payment);

        // Nếu paymentType là VNPAY, truy cập đến phương thức confirmOrder của orderService
        if ("VNPAY".equals(paymentRequest.getPaymentMethod()) && "PAID".equals(paymentRequest.getPaymentStatus())) {
            orderService.confirmOrder(payment.getOrder().getOrderID());
        }

        // Trả về thông tin payment cập nhật
        return paymentMapper.toPaymentResponse(updatedPayment);
    }

    private void updateRewardPoints(Payment payment) {
        double paymentAmount = payment.getPaymentAmount();
        int pointsToAdd = 0;

        if (paymentAmount > 1000000) {
            pointsToAdd = 100;
        } else if (paymentAmount > 800000) {
            pointsToAdd = 80;
        } else if (paymentAmount > 500000) {
            pointsToAdd = 50;
        } else if (paymentAmount > 200000) {
            pointsToAdd = 20;
        }

        if (pointsToAdd > 0) {
            RewardPoint rewardPoint = rewardPointRepository.findByUser(payment.getOrder().getUser())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NO_EXIST));

            rewardPoint.setBalance(rewardPoint.getBalance() + pointsToAdd);
            rewardPointRepository.save(rewardPoint);
        }
    }


    public PaymentResponse getPaymentByOrderID(Long orderID) {
        Payment payment = paymentRepository.findByOrderOrderID(orderID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        return PaymentResponse.builder()
                .paymentID(payment.getPaymentID())
                .paymentAmount(payment.getPaymentAmount())
                .paymentMethod(payment.getPaymentMethod())
                .orderID(payment.getOrder().getOrderID())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDescription(payment.getPaymentDescription())
                .paymentDate(payment.getPaymentDate())
                .build();

    }

    public List<PaymentResponse> getAllPayment(){
        List<Payment> payments = paymentRepository.findAllByOrderByPaymentDateDesc();
        return payments.stream()
                .map(payment -> PaymentResponse
                        .builder()
                        .paymentID(payment.getPaymentID())
                        .orderID(payment.getOrder().getOrderID())
                        .paymentMethod(payment.getPaymentMethod())
                        .paymentAmount(payment.getPaymentAmount())
                        .paymentDate(payment.getPaymentDate())
                        .paymentStatus(payment.getPaymentStatus())
                        .paymentDescription(payment.getPaymentDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentByID(int paymentID){
        Payment payment = paymentRepository.findById(paymentID).orElseThrow(() ->
                new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        return PaymentResponse.builder()
                .paymentID(payment.getPaymentID())
                .orderID(payment.getOrder().getOrderID())
                .paymentMethod(payment.getPaymentMethod())
                .paymentAmount(payment.getPaymentAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDescription(payment.getPaymentDescription())
                .build();
    }

    public List<PaymentResponse> getAllPaymentsByStatus(String paymentStatus) {
        List<Payment> payments = paymentRepository.findByPaymentStatusOrderByPaymentDateDesc(paymentStatus);
        if (payments.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }
        return payments.stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }



}
