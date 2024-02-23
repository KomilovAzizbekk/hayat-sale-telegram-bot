package uz.mediasolutions.saleservicebot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "language_source")
@Getter
@Setter
@ToString
@Builder
public class LanguageSourcePs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private LanguagePs languagePs;

    @Column(name = "language")
    private String language;

    @Column(columnDefinition = "text", name = "translation")
    private String translation;

    public LanguageSourcePs(LanguagePs languagePs, String language, String translation) {
        this.languagePs = languagePs;
        this.language = language;
        this.translation = translation;
    }
}
