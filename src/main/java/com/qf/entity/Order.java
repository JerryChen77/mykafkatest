package com.qf.entity;

/**
 * @author Cjl
 * @date 2021/8/23 18:38
 */
public class Order {
    private Long orderId;
    private int count;

    public Order() {
    }

    public Order(Long orderId, int count) {
        this.orderId = orderId;
        this.count = count;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
