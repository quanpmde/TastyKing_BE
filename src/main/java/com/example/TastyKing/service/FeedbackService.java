package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.FeedbackRequest;
import com.example.TastyKing.dto.response.FeedbackResponse;
import com.example.TastyKing.entity.Feedback;
import com.example.TastyKing.entity.Order;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.repository.FeedbackRepository;
import com.example.TastyKing.repository.OrderRepository;
import com.example.TastyKing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest){
        User user = userRepository.findByEmail(feedbackRequest.getUser().getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        Order order = orderRepository.findById(feedbackRequest.getOrder().getOrderID())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

        // Kiểm tra nếu feedback đã tồn tại cho đơn hàng này
        Optional<Feedback> existingFeedback = feedbackRepository.findByOrder(order);

        if (existingFeedback.isPresent()) {
            // Xóa feedback cũ
            feedbackRepository.delete(existingFeedback.get());
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .order(order)
                .content(feedbackRequest.getContent())
                .emotion(feedbackRequest.getEmotion())
                .feedbackDate(LocalDateTime.now())
                .build();

        Feedback saveFeedBack = feedbackRepository.save(feedback);

        return FeedbackResponse.builder()
                .feedbackId(saveFeedBack.getFeedbackId())
                .user(saveFeedBack.getUser())
                .orderID(saveFeedBack.getOrder().getOrderID())
                .content(saveFeedBack.getContent())
                .emotion(saveFeedBack.getEmotion())
                .feedbackDate(saveFeedBack.getFeedbackDate())
                .build();
    }


    public FeedbackResponse getFeedbackByOrderID(Long orderID){
        Feedback feedback = feedbackRepository.findByOrderOrderID(orderID)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .user(feedback.getUser())
                .orderID(feedback.getOrder().getOrderID())
                .content(feedback.getContent())
                .emotion(feedback.getEmotion())
                .feedbackDate(feedback.getFeedbackDate())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<FeedbackResponse> getAllFeedback(){
        List<Feedback> feedbackList = feedbackRepository.findAll();

        return feedbackList.stream()
                .map(feedback -> FeedbackResponse.builder()
                        .feedbackId(feedback.getFeedbackId())
                        .user(feedback.getUser())
                        .orderID(feedback.getOrder().getOrderID())
                        .content(feedback.getContent())
                        .feedbackDate(feedback.getFeedbackDate())
                        .emotion(feedback.getEmotion())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public FeedbackResponse getFeedbackByFeedbackID(Integer feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .user(feedback.getUser())
                .orderID(feedback.getOrder().getOrderID())
                .content(feedback.getContent())
                .emotion(feedback.getEmotion())
                .feedbackDate(feedback.getFeedbackDate())
                .build();
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String deleteFeedback(Integer feedbackId){
        feedbackRepository.deleteById(feedbackId);
        return "Delete feedback successfull";
    }
}
