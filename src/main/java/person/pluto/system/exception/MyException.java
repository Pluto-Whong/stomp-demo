package person.pluto.system.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String retCode;
    private String retMsg;
    private Object data;

    public MyException(String code, String msg, Object data) {
        super(msg);
        this.retCode = code;
        this.retMsg = msg;
        this.data = data;
    }

}
