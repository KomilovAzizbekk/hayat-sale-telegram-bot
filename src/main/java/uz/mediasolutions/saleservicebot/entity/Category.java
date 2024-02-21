package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.mediasolutions.saleservicebot.entity.template.AbsUUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Entity(name = "category")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE category SET deleted=true WHERE id=?")
public class Category extends AbsUUID {

    @Column(name = "nameUz")
    private String nameUz;

    @Column(name = "nameRu")
    private String nameRu;

    @Column(name = "number")
    private Integer number;

}