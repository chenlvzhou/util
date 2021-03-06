package com.util.comutil.utils;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerResponse;
import com.util.comutil.enums.VoiceType;
import com.util.comutil.vo.Result;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class AliSpeechSynthesizerUtil {

    private static final Logger logger = LoggerFactory.getLogger(AliSpeechSynthesizerUtil.class);
    private static String appKey = "xxxx";

    private static NlsClient client;
    private static AccessToken accessToken = new AccessToken("xxxxx",
            "xxxxx");

    private static NlsClient getClient() throws IOException {
        if(client == null){
            accessToken.apply();
            client = new NlsClient(accessToken.getToken());
        }else{
            if(new Date().getTime()/1000 - accessToken.getExpireTime() > 0){
                accessToken.apply();
                client.setToken(accessToken.getToken());
            }
        }
        return client;
    }

    public static void closeClient(){
        if(client != null){
            client.shutdown();
        }
    }

    // listener
    public static class Listener extends SpeechSynthesizerListener {

        final public ByteArrayOutputStream out;
        final public Result ret;

        public Listener(ByteArrayOutputStream out, Result ret){
            this.out = out;
            this.ret = ret;
        }

        @Override
        public void onComplete(SpeechSynthesizerResponse response) {
            int code = response.getStatus();
            if(code == 20000000){
                return;
            }
            logger.error("??????????????????1???{} {}", response.getStatus(), response.getName());
            ret.code = 103;
            ret.msg = response.getName();
        }

        @Override
        public void onFail(SpeechSynthesizerResponse response) {
            logger.error("??????????????????2???{} {}", response.getStatus(), response.getName());
            ret.code = 103;
            ret.msg = response.getName();
        }

        @Override
        public void onMessage(ByteBuffer message) {
            byte[] byteArray = new byte[message.remaining()];
            message.get(byteArray, 0, byteArray.length);
            try {
                out.writeBytes(byteArray);
            } catch (Exception e) {
                logger.error("???????????????????????????{}", e.getMessage());
                ret.code = 100;
                ret.msg = "????????????????????????";
            }
        }
    }

    /**
     * ????????????
     * @param voice
     * @param content
     * @param ret
     */
    public static void process(ByteArrayOutputStream out, VoiceType voice, String content, Result ret){
        SpeechSynthesizer synthesizer = null;
        try {
            //????????????,????????????
            synthesizer = new SpeechSynthesizer(getClient(), new Listener(out, ret));
            synthesizer.setAppKey(appKey);
            //?????????????????????????????????
            synthesizer.setFormat(OutputFormatEnum.MP3);
            //??????????????????????????????
            synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            //?????????
            synthesizer.setVoice(voice.name());
            //??????????????????-500~500?????????????????????0
            synthesizer.setPitchRate(0);
            //??????????????????-500~500????????????0
            synthesizer.setSpeechRate(0);
            synthesizer.setVolume(100);
            //?????????????????????????????????
//            if(content.length() <1000){
//                synthesizer.setText(content);
//            }else{
//                //????????????????????????????????????
//                synthesizer.setLongText(content);
//            }

            //?????????????????????????????????
            synthesizer.setLongText(content);

            // ????????????????????????(????????????????????????????????????)????????????????????????????????????????????????????????????????????????
            synthesizer.addCustomedParam("enable_subtitle", false);
            //??????????????????????????????????????????json??????????????????,????????????????????????
            long start = System.currentTimeMillis();
            synthesizer.start();
            logger.info("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            start = System.currentTimeMillis();
            //????????????????????????
            synthesizer.waitForComplete();
            logger.info("tts stop latency " + (System.currentTimeMillis() - start) + " ms");
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
            ret.setCodeAndMsg(500, e.getMessage());
        } finally {
            //????????????
            if (null != synthesizer) {
                synthesizer.close();
            }
        }
    }

    final public static int CONNECT_TIMEOUT = 15000;
    final public static int READ_TIMEOUT = 20000;
    final public static int WRITE_TIMEOUT = 20000;
    private static class NoErrorResultErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

        }
    }
    private static RestTemplate _restTemplate = null;
    public static RestTemplate getRestTemplate(){
        if(_restTemplate == null){
            OkHttpClient okHttpClient=new OkHttpClient.Builder().hostnameVerifier(
                    (s, sslSession) ->true).build();
            OkHttp3ClientHttpRequestFactory factory=new OkHttp3ClientHttpRequestFactory(okHttpClient);
            factory.setConnectTimeout(CONNECT_TIMEOUT);
            factory.setReadTimeout(READ_TIMEOUT);
            factory.setWriteTimeout(WRITE_TIMEOUT);
            _restTemplate = new RestTemplate(factory);
            _restTemplate.setErrorHandler(new NoErrorResultErrorHandler());
        }
        return _restTemplate;
    }
}
