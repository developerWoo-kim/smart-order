package gwkim.smartorder.order.response;

import lombok.Data;
@Data
public class OrderCommonResponse<T> {
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";

    private String status;
    private T body;
    private String message;

    private OrderCommonResponse(String status, T body, String message) {
        this.status = status;
        this.body = body;
        this.message = message;
    }

    public static <T> OrderCommonResponse<T> orderSuccess(T data) {
        return new OrderCommonResponse<>(SUCCESS_STATUS, data, "주문이 완료되었습니다.");
    }

}
