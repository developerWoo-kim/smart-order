package gwkim.smartorder.order.response;

import lombok.Data;

@Data
public class CartCommonErrorResponse {
    private final String status = "error";
    private String errorCode;
    private String message;

    protected CartCommonErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }


}
