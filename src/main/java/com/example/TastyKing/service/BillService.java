package com.example.TastyKing.service;

import com.example.TastyKing.dto.response.OrderDetailResponse;
import com.example.TastyKing.dto.response.OrderResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BillService {
    @Autowired
    private OrderService orderService;

    public void generatePdfBill(Long orderID, HttpServletResponse response) throws IOException {
        OrderResponse order = orderService.getOrderByOrderID(orderID);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=bill_" + orderID + ".pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            // Header
            Paragraph header = new Paragraph("Tasty King\nBill", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            // Customer information
            Paragraph customerInfo = new Paragraph(
                    "Customer Name: " + order.getCustomerName() + "\n" +
                            "Phone: " + order.getCustomerPhone() +"\n"
                    +   "Booking Date: " + order.getBookingDate(),
                    normalFont
            );
            customerInfo.setSpacingBefore(20);
            document.add(customerInfo);

            // Bill details table
            PdfPTable table = new PdfPTable(new float[]{1, 4, 2, 2, 2});
            table.setWidthPercentage(100);
            table.setSpacingBefore(20);

            // Table header
            table.addCell(new PdfPCell(new Phrase("TT", normalFont)));
            table.addCell(new PdfPCell(new Phrase("Product", normalFont)));
            table.addCell(new PdfPCell(new Phrase("Quantity", normalFont)));
            table.addCell(new PdfPCell(new Phrase("Unit Price", normalFont)));
            table.addCell(new PdfPCell(new Phrase("Total", normalFont)));

            // Table rows
            int index = 1;
            for (OrderDetailResponse detail : order.getOrderDetails()) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(index++), normalFont)));
                table.addCell(new PdfPCell(new Phrase(detail.getFoodName(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detail.getQuantity()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detail.getFoodPrice()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detail.getQuantity() * detail.getFoodPrice()), normalFont)));
            }

            document.add(table);

            // Total amount
            Paragraph totalAmount = new Paragraph("Total Amount: " + order.getTotalAmount(), normalFont);
            totalAmount.setSpacingBefore(20);
            document.add(totalAmount);

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF", e);
        } finally {
            document.close();
        }
    }

}
