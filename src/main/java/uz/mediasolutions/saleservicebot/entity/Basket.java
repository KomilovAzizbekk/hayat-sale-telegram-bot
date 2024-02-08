package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.*;
import uz.mediasolutions.saleservicebot.entity.template.AbsLong;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;
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
@Entity(name = "basket")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE basket SET deleted=true WHERE id=?")
public class Basket extends AbsLong {

    @OneToOne(fetch = FetchType.LAZY)
    private TgUser tgUser;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ChosenProduct> chosenProducts;


}
