package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.mediasolutions.saleservicebot.entity.template.AbsUUID;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Entity(name = "products")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE products SET deleted=true WHERE id=?")
public class Product extends AbsUUID {

    @Column(name = "nameUz")
    private String nameUz;

    @Column(name = "nameRu")
    private String nameRu;

    @Column(name = "number")
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column(name = "for_unique", columnDefinition = "serial")
    private Integer forUnique;

}
