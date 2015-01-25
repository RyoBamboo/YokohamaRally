package com.dr.yokohamarally.core;

public class ValidateDto {
    private boolean ret;
    private int message;

    public ValidateDto(boolean ret, int message) {
        this.ret = ret;
        this.message = message;
    }

    public int getMessage() {
        return message;
    }

    public boolean isValid() {
        return ret;
    }
}
