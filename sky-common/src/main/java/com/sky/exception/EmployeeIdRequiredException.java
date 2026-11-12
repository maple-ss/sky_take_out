package com.sky.exception;

/**
 * 员工id缺失异常
 */
public class EmployeeIdRequiredException extends BaseException {

    public EmployeeIdRequiredException() {
    }

    public EmployeeIdRequiredException(String msg) {
        super(msg);
    }

}
