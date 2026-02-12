package com.mossy.boundedContext.global.jwt;

public  record JwtUserClaim(Long userId, String role, Long sellerId) {}
