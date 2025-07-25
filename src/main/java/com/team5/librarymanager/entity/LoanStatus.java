package com.team5.librarymanager.entity;

public enum LoanStatus {
    BORROWED("Đang mượn"),
    RETURNED("Đã trả"),
    OVERDUE("Trễ hạn"),
    PENDING("Chờ duyệt");

    private final String displayValue;

    LoanStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
} 