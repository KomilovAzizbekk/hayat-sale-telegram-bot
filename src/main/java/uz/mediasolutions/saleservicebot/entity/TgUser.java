package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.mediasolutions.saleservicebot.entity.template.AbsLong;
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
@Entity(name = "tg_users")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE tg_users SET deleted=true WHERE id=?")
public class TgUser extends AbsLong {

    @Column(nullable = false, name = "chat_id")
    private String chatId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_accepted")
    private boolean isAccepted;

    @Column(name = "is_rejected")
    private boolean isRejected;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Market market;

}
