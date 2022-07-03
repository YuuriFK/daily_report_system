package actions.views;


import java.util.ArrayList;
import java.util.List;

import models.Reaction;

public class ReactionConverter {
    public static Reaction toModel(ReactionView rtv) {
        return new Reaction(
                rtv.getReaction_id(),
                EmployeeConverter.toModel(rtv.getEmployee()),
                ReportConverter.toModel(rtv.getId()),
                rtv.getReaction_type());
    }

    public static ReactionView toView(Reaction rt) {
        if (rt == null) {
            return null;
        }
        return new ReactionView(
                rt.getReaction_id(),
                EmployeeConverter.toView(rt.getEmployee()),
                ReportConverter.toView(rt.getReport()),
                rt.getReaction_type());
    }

    public static List<ReactionView> toViewList(List<Reaction> list) {
        List<ReactionView> evs = new ArrayList<>();
        for (Reaction rt : list) {
            evs.add(toView(rt));
        }
        return evs;
    }

    public static void copyViewToModel(Reaction rt, ReactionView rtv) {
        rt.setReaction_id(rtv.getReaction_id());
        rt.setEmployee(EmployeeConverter.toModel(rtv.getEmployee()));
        rt.setReport(ReportConverter.toModel(rtv.getId()));
        rt.getReaction_type();
    }

}