package uz.mediasolutions.saleservicebot.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "language_ps")
@Getter
@Setter
@ToString
@Builder
public class LanguagePs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "primary_lang")
    private String primaryLang;

    @OneToMany(mappedBy = "languagePs" ,fetch = FetchType.EAGER)
    private List<LanguageSourcePs> languageSourcePs;

}