package com.example.KG25.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderlistId implements Serializable {
    private String id;
    private String pdname;
}