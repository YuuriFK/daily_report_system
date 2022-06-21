package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = JpaConst.REP_TABLE_REACTION)
//@NamedQueries({
//    @NamedQuery(
//            name = JpaConst.Q_REACT_COUNT,
//            query = JpaConst.Q_REACT_COUNT_DEF),
//})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Reaction {
    @Id
    @Column(name = JpaConst.REP_COL_REACT_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reaction_id;

    @ManyToOne
    @JoinColumn(name = JpaConst.REP_COL_EMP, nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = JpaConst.REP_COL_ID, nullable = false)
    private Report report;

    @Column(name = JpaConst.REP_COL_REACTION, nullable = false)
    private String reaction_type;
}
