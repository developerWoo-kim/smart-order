package gwkim.smartorder.order.response;

import lombok.Data;

@Data
public class CartCommonResponse<T> {
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";

    private String status;
    private T body;
    private String message;

    protected CartCommonResponse(String status, T body, String message) {
        this.status = status;
        this.body = body;
        this.message = message;
    }

    public static <T> CartCommonResponse<T> addSuccess(T data) {
        return new CartCommonResponse<>(SUCCESS_STATUS, data, "추가 되었습니다.");
    }

    public static <T> CartCommonResponse<T> storeChangeConfirm(T data) {
        return new CartCommonResponse<>(FAIL_STATUS, data, "장바구니에는 같은 게게의 메뉴만 담을 수 있습니다.");
    }
}
