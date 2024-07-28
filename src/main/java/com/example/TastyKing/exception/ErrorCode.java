package com.example.TastyKing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    EMAIL_EXISTED(1001, "Email has existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must at least 5 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must at least 8 characters", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1004, "Email has not existed", HttpStatus.NOT_FOUND),
    LOGIN_FAILED(1005, "Login failed. Please check your email or password", HttpStatus.FORBIDDEN),
    PHONE_INVALID(1006, "Number phone format invalid.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "Access Denied", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1008,"Unauthenticated", HttpStatus.UNAUTHORIZED),

    CATEGORY_NOT_EXIST(1009,"Category not exist" ,HttpStatus.BAD_REQUEST ),
    CATEGORY_EXISTED(1010,"Category has existed" ,HttpStatus.BAD_REQUEST ),
    FOOD_NOT_EXIST(1011,"Food not exist",HttpStatus.BAD_REQUEST),
    PRICE_INVALID(1012, "Price must be greater than 0", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCHER(1013,"Please enter correct old password", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1013,"Do not match. Please enter correct password", HttpStatus.BAD_REQUEST),
    OTP_INVALID(1014,"OTP invalid. Try again" ,HttpStatus.BAD_REQUEST ),
    COMBO_NOT_EXIST(1015, "Combo not exist", HttpStatus.BAD_REQUEST),
    OPEN_DATE_INVALID(1016, "Open date must be in the future", HttpStatus.BAD_REQUEST),
    END_DATE_INVALID(1017, "End date invalid", HttpStatus.BAD_REQUEST),
    POSITION_EXISTED(1018,"Position has existed" , HttpStatus.BAD_REQUEST ),
    POSITION_NOT_EXIST(1019, "Position has not exist",HttpStatus.BAD_REQUEST ),
    INVALID_NUM_OF_CHAIR(1020, "Number of chair must be at least 2 chairs", HttpStatus.BAD_REQUEST),
    TABLE_NOT_EXIST(1021,"Table has not exist" ,HttpStatus.BAD_REQUEST ),
    VOUCHER_NOT_EXIST(1022,"Voucher has not exist" ,HttpStatus.BAD_REQUEST ),
    VOUCHER_HAS_EXISTED(1023,"Voucher has existed" ,HttpStatus.BAD_REQUEST ),
    ORDER_NOT_EXIST(1024, "Order has not exist", HttpStatus.BAD_REQUEST ),
    CANNOT_CANCEL_ORDER(1025,"Cannot cancel order within 24 hours of booking date" ,HttpStatus.BAD_REQUEST),
    POINT_EMPTY(1026,"User does not have any reward points." ,HttpStatus.BAD_REQUEST ),
    POINT_NOT_ENOUGH(1027,"User does not have enough reward points." ,HttpStatus.BAD_REQUEST ),
    VOUCHER_NOT_ENOUGH(1028,"Voucher is out of stock." ,HttpStatus.BAD_REQUEST ),
    EXCHANGE_NOT_EXIST(1029, "Voucher exchange does not exist",HttpStatus.BAD_REQUEST ),
    REWARD_NO_EXIST(1030,"Reward point does not exist" ,HttpStatus.BAD_REQUEST),
    USER_HAS_NOT_EXCHANGED_VOUCHER(1031,"User has not exchanged this voucher.", HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_TIME(1032, "Can not receive table. Please wait", HttpStatus.BAD_REQUEST),
    RATING_INVALID(1033, "Rating must be greater than 1", HttpStatus.BAD_REQUEST),
    USER_NO_EXIST(1034, "User does not exist",HttpStatus.BAD_REQUEST ),
    OUT_OF_TABLE(1035, "Number of table is out of stock", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_EXISTED(1036,"Payment has not exist",HttpStatus.BAD_REQUEST),
    ORDERID_MUST_BE_PROVIDED(1037,"Order ID must be provided",HttpStatus.BAD_REQUEST),
    ONLY_PAY_OR_NOT_PAY_YET_IS_ALLOWED(1038,"Invalid status. Only 'paid' or 'not pay yet' is allowed.",HttpStatus.BAD_REQUEST),
    TABLE_HAS_BOOKED(1039, "Can't book this table. It has booked. Order will be cancel", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_EXIST(1040, "Can not find review", HttpStatus.BAD_REQUEST),
    ORDER_NOT_DONE(1041, "User can't leave a review. Please complete a order to continue.", HttpStatus.BAD_REQUEST),
    FEEDBACK_NOT_FOUND(1042, "Feedback doesn't found", HttpStatus.BAD_REQUEST),
    TABLE_ALREADY_BOOKED(1043, "Order fail. This table has been booked on this time. Please choose another table or choose another time", HttpStatus.BAD_REQUEST),
    REFUND_NOT_EXIST(1044, "Refun has not exist",HttpStatus.BAD_REQUEST);
    private int code;
    private String message;
    private HttpStatusCode statusCode;
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
