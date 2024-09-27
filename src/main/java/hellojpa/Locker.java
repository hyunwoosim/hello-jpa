package hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Locker {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToOne(mappedBy = "locker") // 읽기 전용이 된다
    private Member member;
}
