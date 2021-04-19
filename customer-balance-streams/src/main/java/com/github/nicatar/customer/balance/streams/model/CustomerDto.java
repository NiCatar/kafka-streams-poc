package com.github.nicatar.customer.balance.streams.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"Name", "amount", "time"})
public class CustomerDto {

    @JsonProperty("Name")
    private String name;
    private long amount;
    private Date time;

    public CustomerDto() {
    }

//    public CustomerDto(Customer customer) {
//        this.name = (String) customer.getName();
//        this.amount = (long) customer.getAmount();
//        this.time = (Date) customer.getTime();
//    }

    public CustomerDto(String name, long amount, Date time) {
        this.name = name;
        this.amount = amount;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
