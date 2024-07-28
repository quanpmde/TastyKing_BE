package com.example.TastyKing.mapper;

import com.example.TastyKing.dto.request.RefundRequest;
import com.example.TastyKing.dto.request.UpdateRefundRequest;
import com.example.TastyKing.dto.response.RefundResponse;
import com.example.TastyKing.entity.Refund;
import org.hibernate.sql.Update;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RefundMapper {
    @Mapping(source = "refundImage", target = "refundImage", qualifiedByName = "mapImage")
    Refund toRefund(RefundRequest request);

    RefundResponse toRefundResponse(Refund refund);

    @Mapping(source = "refundImage", target = "refundImage", qualifiedByName = "mapImage", ignore = true) // Ignore image mapping here, handle it separately
    void updateRefundFromRequest(UpdateRefundRequest request, @MappingTarget Refund refund);

    @Named("mapImage")
    default String mapImage(MultipartFile refundImage) {
        if (refundImage != null && !refundImage.isEmpty()) {
            return refundImage.getOriginalFilename();
        }
        return null;
    }
}