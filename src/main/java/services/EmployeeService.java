package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import constants.JpaConst;
import models.Employee;
import utils.EncryptUtil;
import validators.EmployeeValidator;
import views.EmployeeConverter;
import views.EmployeeView;

public class EmployeeService {
    //指定されたページ数の一覧画面に表示するデータを取得
    public List<EmployeeView> getPerPage(int page) {
        List<Employee> employees = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Employee.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return EmployeeConverter.toViewList(employees);
    }


    //従業員テーブルのデータの件数を取得
    public long countAll() {
        long empCount = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT, Long.class)
                .getSingleResult();

        return empCount;
    }


    //社員番号、パスワードを条件にデータを取得
    public EmployeeView fineOne(String code, String plainPass, String pepper) {
        Employee e = null;
        try {
            //パスワードのハッシュ化
            String pass = EncryptUtil.getPasswordEncrypt(plainPass, pepper);

            //社員番号とハッシュ化済パスワードを条件に未削除の従業員を1件取得する
            e = em.createNamedQuery(JpaConst.Q_EMP_GET_BY_CODE_AND_PASS, Employee.class)
                    .setParameter(JpaConst.JPQL_PARM_CODE, code)
                    .setParameter(JpaConst.JPQL_PARM_PASSWORD, pass)
                    .getSingleResult();

        } catch (NoResultException ex) {
        }
        return EmployeeConverter.toView(e);
    }

    //  idを条件にデータを1件取得
    public EmployeeView findOne(int id) {
        Employee e = findOneInternal(id);
        return EmployeeConverter.toView(e);
    }

    //社員番号を条件に該当するデータの件数を取得, 重複した社員番号登録できないようチェックするときに使用
    public long countByCode(String code) {
        long employees_count = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT_RESISTERED_BY_CODE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_CODE, code)
                .getSingleResult();
        return employees_count;
    }

    //画面入力された従業員の情報からデータを1件作成、バリデーション後、テーブルに登録
    public List<String> create(EmployeeView ev, String pepper) {
        String pass = EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper);
        ev.setPassword(pass);

        LocalDateTime now = LocalDateTime.now();
        ev.setCreatedAt(now);
        ev.setUpdatedAt(now);

        List<String> errors = EmployeeValidator.validate(this, ev, true, true);

        if (errors.size() == 0) {
            create(ev);
        }
        return errors;
    }

    //画面入力された従業員の情報からデータを1件作成、バリデーション後、テーブルの該当データを更新
    public List<String> update(EmployeeView ev, String pepper) {
        EmployeeView savedEmp = findOne(ev.getId());

        boolean validateCode = false;
        if (!savedEmp.getCode().equals(ev.getCode())) {
            validateCode = true;
            savedEmp.setCode(ev.getCode());
        }

        boolean validatePass = false;
        if (ev.getPassword() != null && !ev.getPassword().equals("")) {
            validatePass = true;
            savedEmp.setPassword(
                    EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper));
        }

        savedEmp.setName(ev.getName());
        savedEmp.setAdminFlag(ev.getAdminFlag());

        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);

        List<String> errors = EmployeeValidator.validate(this, savedEmp, validateCode, validatePass);

        if (errors.size() == 0) {
            update(savedEmp);
        }
        return errors;
    }

    //idを条件に従業員データを論理削除（delete_flgを1に更新）
    public void destroy(Integer id) {
        EmployeeView savedEmp = findOne(id);

        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);

        savedEmp.setDeleteFlag(JpaConst.EMP_DEL_TRUE);

        update(savedEmp);
    }


    //社員番号とパスワードを条件に検索し、データが取得できるかどうかで認証結果を返却
    public Boolean validateLogin(String code, String plainPass, String pepper) {
        boolean isValidEmployee = false;
        if (code != null && !code.equals("") && plainPass != null && !plainPass.equals("")) {
            EmployeeView ev = findOne(code, plainPass, pepper);
            if (ev != null && ev.getId() != null) {
                isValidEmployee = true;
            }
        }
        return isValidEmployee;
    }

    //  idを条件にデータを1件取得
    private Employee findOneInternal(int id) {
        Employee e = em.find(Employee.class, id);

        return e;
    }

    //従業員データを1件登録
    private void create(EmployeeView ev) {
        em.getTransaction().begin();
        em.persist(EmployeeConverter.toModel(ev));
        em.getTransaction().commit();
    }

    //従業員データを更新
    private void update(EmployeeView ev) {
        em.getTransaction().begin();
        Employee e = findOneInternal(ev.getId());
        EmployeeConverter.copyViewToModel(e, ev);
        em.getTransaction().commit();
    }
}
