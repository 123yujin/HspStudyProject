package com.cwy.QQClient.view;

import com.cwy.QQClient.service.UserClientService;
import com.cwy.QQClient.utiliy.Utility;

import java.io.IOException;

public class QQView {
    private boolean loop = true;
    private String  key = "";

    private UserClientService userClientService = new UserClientService();
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQView().mainMenu();
        System.out.println("客户端退出系统.....");
    }
    //显示主菜单
    public void mainMenu() throws IOException, ClassNotFoundException {
        while (loop){
            System.out.println("==========欢迎登入网络通信系统=========");
            System.out.println("\t\t 1 登入系统");
            System.out.println("\t\t 9 退出系统");
            System.out.println("请输入你的选择");
            key = Utility.readString(1);

            switch (key){
                case "1":
                    System.out.print("请输入用户号:");
                    String userId = Utility.readString(50);
                    System.out.print("请输入 密码：");
                    String pwd = Utility.readString(50);
                    if(userClientService.checkUser(userId,pwd)){
                        System.out.println("==========欢迎" + userId + "==========");
                        //进入到二级菜单
                        while(loop){
                            System.out.println("\n===========网络通信系统二级菜单(用户" + userId + ")===========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.println("请输入你的选择：");
                            key = Utility.readString(1);
                            switch(key){
                                case "1":
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    userClientService.sendMessageToAll();
                                    break;
                                case "3":
                                    userClientService.privateMessage();
                                    break;
                                case "4":
                                   userClientService.sendFileToOne();
                                    break;
                                case "9":
                                    //调用方法，给服务器发送一个Message对象
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    }else{
                        System.out.println("======登入失败======");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
