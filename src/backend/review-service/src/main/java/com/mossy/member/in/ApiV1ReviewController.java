package com.mossy.member.in;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review", description = "리뷰")
@RestController
@RequestMapping("/api/v1/review/reviews")
@RequiredArgsConstructor
public class ApiV1ReviewController {
}
