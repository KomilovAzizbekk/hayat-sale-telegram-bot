package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.mediasolutions.saleservicebot.entity.template.AbsLong;
import uz.mediasolutions.saleservicebot.entity.template.AbsUUID;
import uz.mediasolutions.saleservicebot.enums.StatusName;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Entity(name = "orders")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE orders SET deleted=true WHERE id=?")
public class Order extends AbsUUID {

    @Column(name = "number", columnDefinition = "serial")
    private Long number = 1L;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ChosenProduct> chosenProducts;

    @ManyToOne(fetch = FetchType.LAZY)
    private TgUser tgUser;

    @Column(name = "lan")
    private Double lan;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    @Column(name = "ordered_time")
    private LocalDateTime orderedTime;

}
