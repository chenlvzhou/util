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
            logger.error("生成语音失败1：{} {}", response.getStatus(), response.getName());
            ret.code = 103;
            ret.msg = response.getName();
        }

        @Override
        public void onFail(SpeechSynthesizerResponse response) {
            logger.error("生成语音失败2：{} {}", response.getStatus(), response.getName());
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
                logger.error("保存语音文件失败：{}", e.getMessage());
                ret.code = 100;
                ret.msg = "保存语音文件失败";
            }
        }
    }

    /**
     * 语音生成
     * @param voice
     * @param content
     * @param ret
     */
    public static void process(ByteArrayOutputStream out, VoiceType voice, String content, Result ret){
        SpeechSynthesizer synthesizer = null;
        try {
            //创建实例,建立连接
            synthesizer = new SpeechSynthesizer(getClient(), new Listener(out, ret));
            synthesizer.setAppKey(appKey);
            //设置返回音频的编码格式
            synthesizer.setFormat(OutputFormatEnum.MP3);
            //设置返回音频的采样率
            synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            //发音人
            synthesizer.setVoice(voice.name());
            //语调，范围是-500~500，可选，默认是0
            synthesizer.setPitchRate(0);
            //语速，范围是-500~500，默认是0
            synthesizer.setSpeechRate(0);
            synthesizer.setVolume(100);
            //设置用于语音合成的文本
//            if(content.length() <1000){
//                synthesizer.setText(content);
//            }else{
//                //设置用于语音合成的长文本
//                synthesizer.setLongText(content);
//            }

            //统一用语音合成的长文本
            synthesizer.setLongText(content);

            // 是否开启字幕功能(返回对应文本的相应时间戳)，默认不开启，需要注意并非所有发音人都支持该参数
            synthesizer.addCustomedParam("enable_subtitle", false);
            //此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
            long start = System.currentTimeMillis();
            synthesizer.start();
            logger.info("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            start = System.currentTimeMillis();
            //等待语音合成结束
            synthesizer.waitForComplete();
            logger.info("tts stop latency " + (System.currentTimeMillis() - start) + " ms");
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
            ret.setCodeAndMsg(500, e.getMessage());
        } finally {
            //关闭连接
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
