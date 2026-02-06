package com.mossy.shared.cash.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayMethod {
    CARD("신용/체크카드"),
    CASH("예치금");

    private final String description;
}
