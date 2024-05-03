package firstskin.firstskin.skin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Skin {

    @Id
    @Column(name = "skin_id")
    @GeneratedValue
    private Long skinId;

    private Kind kind;

    private String result;

    public Skin(Kind kind, String result) {
        this.kind = kind;
        this.result = result;
    }
}
