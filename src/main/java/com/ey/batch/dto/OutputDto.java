package com.ey.batch.dto;

public class OutputDto {
    private String fullName;

    public OutputDto() {};

    public OutputDto(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "OutputDto{" +
                "fullName='" + fullName + '\'' +
                '}';
    }
}
