package kz.springboot.springbootdemo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name="roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Roles implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="role", length =255)
    private String role;
    @Column(name="description", length =255)
    private String description;

    @Override
    public String getAuthority() {
        return this.role;
    }

}
