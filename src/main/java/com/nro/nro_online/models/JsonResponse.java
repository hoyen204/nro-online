package com.nro.nro_online.models;

import java.util.List;

import lombok.Getter;

@Getter
public class JsonResponse {
    private boolean success;
    private String message;
    private List<TransactionHistory> data;

    // Constructors, getters, and setters

    // You can generate these using your IDE or manually write them.
}
