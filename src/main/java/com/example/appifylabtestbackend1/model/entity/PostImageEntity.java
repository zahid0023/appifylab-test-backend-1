package com.example.appifylabtestbackend1.model.entity;

import com.example.appifylabtestbackend1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "post_images")
public class PostImageEntity extends AuditableEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    @NotNull
    @Column(name = "image_url", nullable = false, length = Integer.MAX_VALUE)
    private String imageUrl;

    @ColumnDefault("false")
    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "caption", length = Integer.MAX_VALUE)
    private String caption;

}