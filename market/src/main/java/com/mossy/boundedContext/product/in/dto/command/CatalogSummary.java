//package com.mossy.boundedContext.product.in.dto.command;
//
//public record CatalogSummary(
//        Double minPrice,
//        Long sellerCount
//) {
//
//    public CatalogSummary(Object minPrice, Long sellerCount) {
//        this(
//                (minPrice instanceof Number n) ? n.doubleValue() : 0.0,
//                (sellerCount == null) ? 0L : sellerCount
//        );
//    }
//}