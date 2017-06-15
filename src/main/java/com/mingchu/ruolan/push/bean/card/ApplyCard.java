package com.mingchu.ruolan.push.bean.card;

import com.google.gson.annotations.Expose;
import com.mingchu.ruolan.push.bean.db.Apply;

import java.time.LocalDateTime;

/**
 * @author qiujuer Email:qiujuer.live.cn
 */
public class ApplyCard {
    @Expose
    private String id;
    @Expose
    private String attach;
    @Expose
    private String desc;
    @Expose
    private int type;
    @Expose
    private String targetId;
    @Expose
    private String applicantId;
    @Expose
    private LocalDateTime createAt;

    public ApplyCard(Apply apply) {
        this.id = apply.getId();
        this.attach = apply.getAttach();
        this.desc = apply.getDescription();
        this.type = apply.getType();
        this.targetId = apply.getTargetId();
        this.applicantId = apply.getApplicantId();
        this.createAt = apply.getCreatedAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
