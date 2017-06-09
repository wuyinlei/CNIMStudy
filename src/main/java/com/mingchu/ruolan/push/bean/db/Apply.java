package com.mingchu.ruolan.push.bean.db;

import com.mingchu.ruolan.push.bean.db.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author qiujuer Email:qiujuer.live.cn
 */
@Entity
@Table(name = "TB_APPLY")
public class Apply {
    public static final int TYPE_ADD_USER = 1;
    public static final int TYPE_ADD_GROUP = 2;

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String attach;

    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    private String targetId;

    @ManyToOne
    @JoinColumn(name = "applicantId")
    private User applicant;
    @Column(updatable = false, insertable = false)
    private String applicantId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Apply() {
    }

    public Apply(User applicant, User target, String description) {
        this.applicant = applicant;
        this.targetId = target.getId();
        this.type = TYPE_ADD_USER;
        this.description = description;
    }


    public Apply(User applicant, Group target, String description) {
        this.applicant = applicant;
        this.targetId = target.getId();
        this.type = TYPE_ADD_GROUP;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
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

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
