package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.UpdateVoucherRequest;
import com.example.TastyKing.dto.request.VoucherRequest;
import com.example.TastyKing.dto.response.VoucherResponse;
import com.example.TastyKing.entity.Combo;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.entity.Voucher;
import com.example.TastyKing.entity.VoucherExchange;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.mapper.VoucherMapper;
import com.example.TastyKing.repository.UserRepository;
import com.example.TastyKing.repository.VoucherExchangeRepository;
import com.example.TastyKing.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private static final String UPLOAD_DIR = "D:\\FPT U\\FU-SU24\\SWP391\\TastyKing-FE\\img\\";

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoucherExchangeRepository voucherExchangeRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse createVoucher(VoucherRequest request) throws IOException {
        String relativeImagePath = saveImage(request.getVoucherImage());
        Voucher voucher = voucherMapper.toVoucher(request);
        voucher.setVoucherExchangePoint(request.getVoucherExchangePoint());
        voucher.setNumberVoucherUsed(0);
        voucher.setVoucherImage(relativeImagePath);
        voucher.setExpried(0);
        Voucher newVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherResponse(newVoucher);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteVoucher(int voucherID){
        voucherRepository.deleteById(voucherID);
        return "Voucher deleted successfull";
    }

    public List<VoucherResponse> getAllVoucher(){
        List<Voucher> vouchers = voucherRepository.findAll();
        return vouchers.stream()
                .map(voucherMapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    public VoucherResponse getVoucherByVoucherID(int voucherID){
        Voucher voucher = voucherRepository.findById(voucherID).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXIST));
        return voucherMapper.toVoucherResponse(voucher);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse updateVoucher(int voucherID, UpdateVoucherRequest request) throws IOException {
        Voucher voucher = voucherRepository.findById(voucherID)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXIST));

        // Update fields based on the request
        voucher.setVoucherStartDate(request.getUpdateOpenDate());
        voucher.setVoucherDueDate(request.getUpdateEndDate());
        voucher.setVoucherTitle(request.getUpdateVoucherTitle());
        voucher.setVoucherDiscount(request.getUpdateDiscount());
        voucher.setVoucherQuantity(request.getUpdateQuantity());
        voucher.setVoucherExchangePoint(request.getUpdateExchangePoint());
        voucher.setVoucherDescribe(request.getUpdateDescription());

        // Handle image update only if a new image is provided
        if (request.getUpdateVoucherImage() != null && !request.getUpdateVoucherImage().isEmpty()) {
            String relativeImagePath = saveImage(request.getUpdateVoucherImage());
            voucher.setVoucherImage(relativeImagePath);
        }

        // Save the updated voucher entity
        Voucher voucherUpdate = voucherRepository.save(voucher);

        return voucherMapper.toVoucherResponse(voucherUpdate);
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


    @Scheduled(cron = "0 * * * * ?") // Run daily at midnight
    public void updateExpiredVouchers() {
        List<Voucher> vouchers = voucherRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Voucher voucher : vouchers) {
            if (voucher.getVoucherDueDate().isBefore(now) || voucher.getNumberVoucherUsed() >= voucher.getVoucherQuantity()) {
                if (voucher.getExpried() != 1) {
                    voucher.setExpried(1);
                    voucherRepository.save(voucher);
                }
            } else {
                if (voucher.getExpried() != 0) {
                    voucher.setExpried(0);
                    voucherRepository.save(voucher);
                }
            }
        }
    }



    public VoucherResponse applyVoucher(String voucherCode, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXIST));

        Optional<VoucherExchange> optionalVoucherExchange = voucherExchangeRepository
                .findTopByUserAndVoucherOrderByExchangeDateDesc(user, voucher);

        if (optionalVoucherExchange.isEmpty()) {
            throw new AppException(ErrorCode.USER_HAS_NOT_EXCHANGED_VOUCHER);
        }

        if (voucher.getNumberVoucherUsed() >= voucher.getVoucherQuantity()) {
            throw new AppException(ErrorCode.VOUCHER_NOT_ENOUGH);
        }

        // Increment the number of times this voucher has been used
        voucher.setNumberVoucherUsed(voucher.getNumberVoucherUsed() + 1);
        voucherRepository.save(voucher);

        // Remove the voucher exchange record with the nearest exchange date
        VoucherExchange voucherExchange = optionalVoucherExchange.get();
        voucherExchangeRepository.delete(voucherExchange);

        return voucherMapper.toVoucherResponse(voucher);
    }




}

