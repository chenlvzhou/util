package com.util.comutil.service;

import com.util.comutil.enums.VoiceType;
import com.util.comutil.utils.AliSpeechSynthesizerUtil;
import com.util.comutil.utils.AliyunUtil;
import com.util.comutil.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author by chenlvzhou
 * @Classname AuthSpeechService
 * @Description TODO
 * @Date 2021/3/20 22:34
 */
@Service
public class AuthSpeechService {
    @Autowired
    private AliyunUtil aliyunUtil;

    public Boolean synthesizerAndOss(VoiceType voice, String content, String key){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = Result.create();
        AliSpeechSynthesizerUtil.process(out, voice, content, result);
        if(result.isSucceed()){
            // 音频存入有OSS中
            aliyunUtil.updateAudio(key, streamTran(out));
            return true;
        }
        return false;
    }

    private InputStream streamTran(ByteArrayOutputStream out) {
        return new ByteArrayInputStream(out.toByteArray());
    }

}
