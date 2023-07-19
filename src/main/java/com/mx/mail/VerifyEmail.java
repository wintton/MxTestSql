package com.mx.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class VerifyEmail extends Authenticator {
    //账号
    private String userName;
    //密码
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //构造方法
    public VerifyEmail() {
        super();
    }

    public VerifyEmail(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {

        return new PasswordAuthentication(userName, password);

    }
}