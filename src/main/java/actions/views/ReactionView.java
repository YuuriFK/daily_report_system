package actions.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ReactionView {
    private Integer reaction_id;
    private EmployeeView employee;
    private ReportView id;
    private String reaction_type;
}
