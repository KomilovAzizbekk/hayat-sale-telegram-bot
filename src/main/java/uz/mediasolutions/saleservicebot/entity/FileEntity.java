package uz.mediasolutions.saleservicebot.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.mediasolutions.saleservicebot.entity.template.AbsLong;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Entity(name = "files")
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE files SET deleted=true WHERE id=?")
public class FileEntity extends AbsLong {

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "type")
    private String type;

    @Lob
    private byte[] data;

}
