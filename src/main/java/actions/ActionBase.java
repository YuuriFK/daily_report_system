package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

//Employee, Report Actionクラスの親クラス

public abstract class ActionBase {
    //  Webアプリケーションのコンテキスト情報
    protected ServletContext context;
    //リクエスト情報のオブジェクト, FrontController doGetdoPostの引数
    protected HttpServletRequest request;
    //レスポンス情報のオブジェクト, FrontController doGetdoPostの引数
    protected HttpServletResponse response;

    //初期化処理:サーブレットコンテキスト、リクエスト、レスポンスをクラスフィールドに設定
    public void init(
            ServletContext servletContext,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        this.context = servletContext;
        this.request = servletRequest;
        this.response = servletResponse;
    }

    //フロントコントローラから呼び出されるメソッド, 内容実装はサブクラスで
    public abstract void process() throws ServletException, IOException;

    //パラメータのcommandの値に該当するメソッドを実行。不正の時はエラー画面をよびだす。
    protected void invoke()
            throws ServletException, IOException {

        Method commandMethod;
        try {

            String command = request.getParameter(ForwardConst.CMD.getValue());

            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this, new Object[0]);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NullPointerException e) {

            e.printStackTrace();
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }

    }

    //引数で指定されたjspの呼び出しを行う
    protected void forward(ForwardConst target) throws ServletException, IOException {
        //jspファイルの相対パスを作成
        String forward = String.format("/WEB-INF/views/%s.jsp", target.getValue());
        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);
        //jspファイルの呼び出し
        dispatcher.forward(request, response);
    }

    //引数の値を元にURLを構築、リダイレクト
    protected void redirect(ForwardConst action, ForwardConst command)
            throws ServletException, IOException {
        //URLを構築
        String redirectUrl = request.getContextPath() + "/?action=" + action.getValue();
        if (command != null) {
            redirectUrl = redirectUrl + "&command=" + command.getValue();
        }
        response.sendRedirect(redirectUrl);
    }

    //リクエストからパラメータtokenの値を取得し、セッションIDと比較
    //tokenが不正の時はエラー画面表示
    protected boolean checkToken() throws ServletException, IOException {
        //パラメータからtokenの値を取得
        String _token = getRequestParam(AttributeConst.TOKEN);
        if (_token == null || !(_token.equals(getTokenId()))) {
            //tokenが設定されていない、またはセッションIDと一致しない場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;
        } else {
            return true;
        }
    }



    //セッションIDを取得する
    protected String getTokenId() {
        return request.getSession().getId();
    }



    //リクエストから表示を要求されているページ数を取得し、返却
    protected int getPage() {
        int page;
        page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));
        if (page == Integer.MIN_VALUE) {
            page = 1;
        }
        return page;
    }



     //文字列を数値に変換する
    protected int toNumber(String strNumber) {
        int number = 0;
        try {
            number = Integer.parseInt(strNumber);
        } catch (Exception e) {
            number = Integer.MIN_VALUE;
        }
        return number;
    }

    //文字列をLocalDate型に変換する
    protected LocalDate toLocalDate(String strDate) {
        if (strDate == null || strDate.equals("")) {
            return LocalDate.now();
        }
        return LocalDate.parse(strDate);
    }



    //リクエストパラメータから引数で指定したパラメータ名の値を返却する
    protected String getRequestParam(AttributeConst key) {
        return request.getParameter(key.getValue());
    }

    //リクエストスコープにパラメータを設定する
    protected <V> void putRequestScope(AttributeConst key, V value) {
        request.setAttribute(key.getValue(), value);
    }

    //セッションスコープから指定されたパラメータの値を取得し、返却する
    @SuppressWarnings("unchecked")
    protected <R> R getSessionScope(AttributeConst key) {
        return (R) request.getSession().getAttribute(key.getValue());
    }

    //セッションスコープにパラメータを設定する
    protected <V> void putSessionScope(AttributeConst key, V value) {
        request.getSession().setAttribute(key.getValue(), value);
    }

    //セッションスコープから指定された名前のパラメータを除去する
    protected void removeSessionScope(AttributeConst key) {
        request.getSession().removeAttribute(key.getValue());
    }

    //アプリケーションスコープから指定されたパラメータの値を取得し、返却する
    @SuppressWarnings("unchecked")
    protected <R> R getContextScope(PropertyConst key) {
        return (R) context.getAttribute(key.getValue());
    }

}