package services;

import actions.views.ReactionConverter;
import actions.views.ReactionView;

public class ReactionService extends ServiceBase {

    public void create(ReactionView rtv) {
            createInternal(rtv);
    }

    private void createInternal(ReactionView rtv) {
        em.getTransaction().begin();
        em.persist(ReactionConverter.toModel(rtv));
        em.getTransaction().commit();
    }

}
