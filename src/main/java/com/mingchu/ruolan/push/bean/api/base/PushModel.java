package com.mingchu.ruolan.push.bean.api.base;

import com.google.gson.annotations.Expose;
import com.mingchu.ruolan.push.utils.TextUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class PushModel {
    //退出登录
    public static final int ENTITY_TYPE_LOGOUT = -1;
    //普通消息送达
    public static final int ENTITY_TYPE_MESSAGE = 200;
    //新朋友添加
    public static final int ENTITY_TYPE_ADD_FRIEND = 1001;
    //新群添加
    public static final int ENTITY_TYPE_ADD_GROUP = 1002;
    //新的群成员添加
    public static final int ENTITY_TYPE_ADD_GROUP_MEMBERS = 1003;
    //群成员信息更改
    public static final int ENTITY_TYPE_MODIFY_GROUP_MEMBERS = 2004;
    //群成员退出
    public static final int ENTITY_TYPE_EXIT_GROUP_MEMBERS = 3001;


    private List<Entity> entities = new ArrayList<>();

    public PushModel add(Entity entity) {
        entities.add(entity);
        return this;
    }

    public PushModel add(int type, String content) {
        return add(new Entity(type, content));
    }

    public String getPushString() {
        if (entities.size() == 0)
            return null;
        return TextUtil.toJson(entities);
    }

    public static class Entity {
        // 消息类型
        @Expose
        public int type;
        // 消息实体
        @Expose
        public String content;
        // 消息生成时间
        @Expose
        public LocalDateTime createAt = LocalDateTime.now();
        public Entity(int type, String content) {
            this.type = type;
            this.content = content;
        }
    }
}
