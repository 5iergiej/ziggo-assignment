package com.example.restservice.api.v1.order;

import java.util.List;

public record OrderList(List<OrderListItem> orders) {
}
