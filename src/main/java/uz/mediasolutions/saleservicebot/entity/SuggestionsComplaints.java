package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.mediasolutions.saleservicebot.entity.template.AbsLong;

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
@Entity(name = "suggestions_complaints")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE suggestions_complaints SET deleted=true WHERE id=?")
public class SuggestionsComplaints extends AbsLong {

    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private TgUser tgUser;
}
