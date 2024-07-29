package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.RefundRequest;
import com.example.TastyKing.dto.request.UpdateRefundRequest;
import com.example.TastyKing.dto.response.RefundResponse;
import com.example.TastyKing.entity.Order;
import com.example.TastyKing.entity.Refund;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.mapper.RefundMapper;
import com.example.TastyKing.repository.OrderRepository;
import com.example.TastyKing.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {
    private static final String UPLOAD_DIR = "D:\\FPT U\\FU-SU24\\SWP391\\TastyKing-FE\\img\\";
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private OrderRepository orderRepository;

    public RefundResponse createNewRefund(RefundRequest request) throws IOException{
        // Tìm order theo orderID từ request
        Order order;
        if (request.getOrderID() != null) {
            order = orderRepository.findById(request.getOrderID()).orElseThrow(() ->
                    new AppException(ErrorCode.ORDER_NOT_EXIST));
        } else {
            throw new AppException(ErrorCode.ORDERID_MUST_BE_PROVIDED);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = order.getBookingDate();

        // Kiểm tra xem thời gian hiện tại có trước 24 tiếng so với thời gian nhận bàn không
        if (ChronoUnit.HOURS.between(now, bookingDateTime) < 24) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
        }


        String relativeImagePath = saveImage(request.getRefundImage());

        // Tạo một payment mới và đặt trạng thái
        Refund refund = refundMapper.toRefund(request);
        refund.setOrder(order);
        refund.setRefundAmount((int) Math.round(order.getTotalAmount()));
        refund.setRefundDate(new Date());
        refund.setRefundImage(relativeImagePath);
        refund.setRefundStatus("NOT_ACCEPT_YET");
        refund.setRefundDescription("Refund for order: " + order.getOrderID() + " | CustomerName: " + order.getCustomerName() + " | Total: " + order.getTotalAmount());
        // Thiết lập lại mối quan hệ ngược (bi-directional relationship
        order.setRefund(refund);

        //Lưu Refund
        Refund newRefund = refundRepository.save(refund);

        // Tạo response
        RefundResponse refundResponse = refundMapper.toRefundResponse(newRefund);
        refundResponse.setOrderID(order.getOrderID());

        // Chuyển đổi Refund thành RefundResponse và trả về
        return refundResponse;
    }

    public List<RefundResponse> getAllRefund() {
        // Lấy tất cả các refund từ cơ sở dữ liệu
        List<Refund> refunds = refundRepository.findAllByOrderByRefundDateDesc();
        // Chuyển đổi danh sách Refund thành danh sách RefundResponse
        return refunds.stream()
                .map(refund -> {
                    RefundResponse refundResponse = refundMapper.toRefundResponse(refund);
                    refundResponse.setOrderID(refund.getOrder().getOrderID());
                    return refundResponse;
                })
                .collect(Collectors.toList());
    }

    public RefundResponse getRefundByRefundID(int refundID) {
        // Tìm refund theo refundID
        Refund refund = refundRepository.findById(refundID)
                .orElseThrow(() -> new AppException(ErrorCode.REFUND_NOT_EXIST));
        RefundResponse refundResponse = refundMapper.toRefundResponse(refund);
        refundResponse.setOrderID(refund.getOrder().getOrderID());
        // Chuyển đổi Refund thành RefundResponse và trả về
        return refundResponse;
    }

    public RefundResponse getRefundByOrderID(Long orderID) {
        // Tìm refund theo orderID
        Refund refund = refundRepository.findByOrderOrderID(orderID)
                .orElseThrow(() -> new AppException(ErrorCode.REFUND_NOT_EXIST));
        // Chuyển đổi Refund thành RefundResponse
        RefundResponse refundResponse = refundMapper.toRefundResponse(refund);
        // Set thêm orderID và customerName vào RefundResponse
        refundResponse.setOrderID(refund.getOrder().getOrderID());
        // Trả về RefundResponse
        return refundResponse;
    }

    public RefundResponse updateRefund(Long orderID, UpdateRefundRequest request) throws IOException {
        // Tìm refund theo orderID
        Refund refund = refundRepository.findByOrderOrderID(orderID)
                .orElseThrow(() -> new AppException(ErrorCode.REFUND_NOT_EXIST));

        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

        // Cập nhật các thuộc tính của Refund từ RefundRequest
        refundMapper.updateRefundFromRequest(request, refund);

        // Cập nhật ảnh nếu có
        if (request.getRefundImage() != null && !request.getRefundImage().isEmpty()) {
            String relativeImagePath = saveImage(request.getRefundImage());
            refund.setRefundImage(relativeImagePath);
        }

        refund.setRefundAmount((int) Math.round(order.getTotalAmount()));
        refund.setRefundDate(new Date());

        // Lưu refund đã cập nhật vào cơ sở dữ liệu
        Refund updatedRefund = refundRepository.save(refund);

        // Tạo response
        RefundResponse refundResponse = refundMapper.toRefundResponse(updatedRefund);
        refundResponse.setOrderID(refund.getOrder().getOrderID());

        return refundResponse;
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String imagePath = UPLOAD_DIR + image.getOriginalFilename();
            Files.write(Paths.get(imagePath), image.getBytes());
            return "img/" + image.getOriginalFilename();
        }
        return null;
    }
}