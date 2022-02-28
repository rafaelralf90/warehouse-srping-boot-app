package it.raffaele.esposito.app.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIError {

    private int code;
    private String engMessage;
}