package com.mingchu.ruolan.push.bean.card;

import com.google.gson.annotations.Expose;
import com.mingchu.ruolan.push.bean.db.GroupMember;

import java.time.LocalDateTime;

public class GroupMemberCard {
    @Expose
    private String id;
    @Expose
    private String alias;
    @Expose
    private boolean isAdmin;
    @Expose
    private boolean isOwner;
    @Expose
    private String userId;
    @Expose
    private String groupId;
    @Expose
    private LocalDateTime modifyAt;

    public GroupMemberCard(GroupMember member) {
        this.id = member.getId();
        this.alias = member.getAlias();
        this.isAdmin = member.getPermissionType() == GroupMember.PERMISSION_TYPE_ADMIN;
        this.isOwner = member.getPermissionType() == GroupMember.PERMISSION_TYPE_ADMIN_SU;
        this.userId = member.getUserId();
        this.groupId = member.getGroupId();
        this.modifyAt = member.getUpdateAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }
}
