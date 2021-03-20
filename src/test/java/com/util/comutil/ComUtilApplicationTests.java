package com.util.comutil;

import com.util.comutil.enums.VoiceType;
import com.util.comutil.service.AuthSpeechService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class ComUtilApplicationTests {

    @Autowired
    private AuthSpeechService authSpeechService;

    @Test
    void contextLoads() {
    }

    @Test
    void executorSpeech(){
        List<String> list = new ArrayList<>();
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");
        list.add("11111111");

        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        list.parallelStream().forEach(context -> {
            executorService.execute(()->{
                String key = UUID.randomUUID().toString();
                authSpeechService.synthesizerAndOss(VoiceType.Aixiang, context, key);
                countDownLatch.countDown();
            });
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("语言合成失败");
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }

}
