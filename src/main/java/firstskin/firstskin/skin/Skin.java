package firstskin.firstskin.skin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Skin {

    @Id
    @Column(name = "skin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skinId;

    @Enumerated(EnumType.STRING)
    private Kind kind;

    private String result;

    public Skin(Kind kind, String result) {
        this.kind = kind;
        this.result = result;
    }
}
