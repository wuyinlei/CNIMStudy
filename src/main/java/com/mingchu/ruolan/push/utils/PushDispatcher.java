package com.mingchu.ruolan.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class PushDispatcher {
    private static final String appId = "FkrmoAROraAsbMNSQgmnU6";
    private static final String appKey = "weLATrH3JU7PF2I7oghH27";
    private static final String masterSecret = "qdz4iEXE2Y5HS6DI2gN4c5";
    private static final String url = "http://sdk.open.api.igexin.com/apiex.htm";

    private IGtPush pusher;
    private List<BatchBean> batches = new ArrayList<>();

    private PushDispatcher() {

    }

    public static PushDispatcher start() {
        PushDispatcher dispatcher = new PushDispatcher();
        dispatcher.pusher = new IGtPush(url, appKey, masterSecret);
        return dispatcher;
    }

//    public boolean add(User receiver, PushModel model) {
//        if (Strings.isNullOrEmpty(receiver.getPushId()))
//            return false;
//
//        String pushString = model.getPushString();
//        if (Strings.isNullOrEmpty(pushString))
//            return false;
//
//        BatchBean batch = buildMessage(receiver.getPushId(), pushString);
//        batches.add(batch);
//        return true;
//    }

    private static BatchBean buildMessage(String clientId, String text) {
        // 定义"点击链接打开通知模板"，并设置标题、内容、链接
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(0);

        // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表 // 、是否支持离线发送、以及离线消息有效期(单位毫秒)
        SingleMessage message = new SingleMessage();
        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 3600 * 1000);

        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);

        return new BatchBean(message, target);
    }

    public boolean submit() {
        if (batches.size() == 0)
            return false;

        boolean haveData = false;
        IBatch batch = pusher.getBatch();
        for (BatchBean batchBean : batches) {
            try {
                batch.add(batchBean.message, batchBean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!haveData)
            return false;

        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                result = batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (result != null) {
            Logger.getLogger("PushDispatcher").log(Level.INFO, result.getResponse().toString());
            return true;
        } else {
            Logger.getLogger("PushDispatcher").log(Level.WARNING, "服务器响应异常");
            return false;
        }
    }

    private static class BatchBean {
        SingleMessage message;
        Target target;

        BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }
}
