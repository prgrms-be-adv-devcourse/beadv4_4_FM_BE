package backend.mossy.boundedContext.auth.in;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthPingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
