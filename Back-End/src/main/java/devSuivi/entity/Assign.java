package devSuivi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Assign")
@IdClass( AssignId.class )
public class Assign {

    @Id
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name="id_user")
    private User user;

    @Id
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name="id_project")
    private Project project;
}
