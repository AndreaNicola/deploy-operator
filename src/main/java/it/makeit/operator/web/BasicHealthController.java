
package it.makeit.operator.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class BasicHealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

}
