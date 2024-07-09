package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true,nullable = false)
    //@NotBlank(message = "Email is mandatory")
    private String mail;

    @Column(nullable = false)
    //@NotBlank(message = "Password is mandatory")
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    //@NotBlank(message = "First name is mandatory")
    private String name;

    @Column(nullable = false)
    //@NotBlank(message = "Last name is mandatory")
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    //@Column()
    //private String permissions; //perm1;perm2;perm3

    public User(Long id, String name, String lastName, String email, String password){
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.mail = email;
        this.password = password;
    }

}
