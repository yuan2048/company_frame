package com.szxx.recruit.exception;

import com.szxx.recruit.exception.code.ResponseCodeInterface;

/**
 * @ClassName: BusinessException
 * TODO:类文件简单描述
 * @Author: yws
 * @CreateDate: 2020/9/4 16:30
 * @UpdateUser: yws
 * @UpdateDate: 2020/9/4 16:30
 * @Version: 0.0.1
 */
public class BusinessException extends RuntimeException{
    /**
     * 异常编号
     */
    private final int messageCode;

    /**
     * 对messageCode 异常信息进行补充说明
     */
    private final String detailMessage;

    public BusinessException(int messageCode,String message) {
        super(message);
        this.messageCode = messageCode;
        this.detailMessage = message;
    }
    /**
     * 构造函数
     * @param code 异常码
     */
    public BusinessException(ResponseCodeInterface code) {
        this(code.getCode(), code.getMsg());
    }

    public int getMessageCode() {
        return messageCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
