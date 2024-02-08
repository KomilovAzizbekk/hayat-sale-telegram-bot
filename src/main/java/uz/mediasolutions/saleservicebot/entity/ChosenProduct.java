package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.*;
import uz.mediasolutions.saleservicebot.entity.template.AbsLong;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Entity(name = "chosen_product")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE chosen_product SET deleted=true WHERE id=?")
public class ChosenProduct extends AbsLong {

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(name = "count")
    private Integer count;

    @Column(name = "turn")
    private boolean turn = false;
}
