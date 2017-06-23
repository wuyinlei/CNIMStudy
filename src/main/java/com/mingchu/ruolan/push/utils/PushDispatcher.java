package com.mingchu.ruolan.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.PushModel;
import com.mingchu.ruolan.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 推送分发
 */
public class PushDispatcher {

    private static final String appId = "TFU1Iv5VRc82D5PUeY3RH9";
    private static final String appKey = "goXZnTq5X172YhJwcXWby3";
    private static final String masterSecret = "mVEtZ6GsN29l4bGYAOPLJ8";
    private static final String url = "http://sdk.open.api.igexin.com/apiex.htm";

    private IGtPush pusher;

    //消息接收者集合
    private List<BatchBean> batches = new ArrayList<>();

    private PushDispatcher() {

    }

    //启动推送
    public static PushDispatcher start() {
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        dispatcher.pusher = new IGtPush(url, appKey, masterSecret);
        return dispatcher;
    }

    /**
     * 构建目标 + 内容
     *
     * @param clientId 客户端id
     * @param text     推送消息
     * @return BatchBean
     */
    private static BatchBean buildMessage(String clientId, String text) {
        // 定义"点击链接打开通知模板"，并设置标题、内容、链接
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        //传递的内容
        template.setTransmissionContent(text);
        template.setTransmissionType(0);

        // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表
        // 、是否支持离线发送、以及离线消息有效期(单位毫秒)
        SingleMessage message = new SingleMessage();
        message.setData(template);  //设置透传消息到单消息模板中
        message.setOffline(true);  //是否支持离线消息
        message.setOfflineExpireTime(24 * 3600 * 1000);  //离线消息时长

        Target target = new Target();  //接收者
        target.setAppId(appId);
        target.setClientId(clientId);  //客户端id

        return new BatchBean(message, target);
    }

    /**
     * 添加推送
     *
     * @param receiver 接收者
     * @param model    发送的model
     * @return true  false
     */
    public boolean add(User receiver, PushModel model) {
        if (receiver == null || model == null || Strings.isNullOrEmpty(receiver.getPushId()))
            return false;

        String pushStr = model.getPushString();
        if (Strings.isNullOrEmpty(pushStr))
            return false;

        BatchBean batch = buildMessage(receiver.getPushId(), pushStr);
        batches.add(batch);
        return true;
    }

    public boolean submit() {
        if (batches.size() == 0)
            return false;
        //是否有数据
        boolean haveData = false;
        //构建批量发送者
        IBatch batch = pusher.getBatch();
        //循环
        for (BatchBean batchBean : batches) {
            try {
                //添加操作
                batch.add(batchBean.message, batchBean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!haveData)   //全部失败  则直接不发送
            return false;

        IPushResult result = null;  //推送result
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //失败情况下尝试重复发送一次
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
