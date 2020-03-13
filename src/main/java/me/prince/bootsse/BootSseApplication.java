package me.prince.bootsse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class BootSseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootSseApplication.class, args);
    }
}

@Controller
class WelcomeController {
    @GetMapping("source")
    public SseEmitter source() {
        SseEmitter emitter = new SseEmitter();
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();

        sseMvcExecutor.execute(() -> {
            try {
                while(true) {
                    emitter.send("hi: " + new Date());
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }

    @GetMapping("fluxSource")
    public Flux<String> fluxSource() {
        return Flux.interval(Duration.ofSeconds(1))
                .onBackpressureDrop()
                .map(aLong -> "Hello: " + new Date());
    }

}