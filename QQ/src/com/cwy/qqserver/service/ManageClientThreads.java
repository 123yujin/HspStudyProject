package com.cwy.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

//用于管理和客户端通信的线程
public class ManageClientThreads {
    private static HashMap<String,ServerConnectClientThread> hm = new HashMap<>();
    public static  HashMap<String,ServerConnectClientThread> getHm(){
        return hm;
    }
    public static void addClientThread(String userId,ServerConnectClientThread serverConnectClientThread){
        hm.put(userId,serverConnectClientThread);
    }
    public static ServerConnectClientThread getSeverConnectClientThread(String usrId){
        return hm.get(usrId);
    }
    public static void removeSeverConnectClientThread(String userId){
        hm.remove(userId);
    }
    public static boolean checkOnlineUser(String userId){
           if(!hm.containsKey(userId)){
               return false;
           }
           return true;
    }
    //返回用户列表
    public static String getOnlineUser(){
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while(iterator.hasNext()){
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }
}
