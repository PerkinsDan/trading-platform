package com.setap.tradingplatformapi.database;

import java.util.ArrayList;
import orderProcessor.Order;

public class User {

  String userId;
  int balance = 0;
  ArrayList<Order> usersOrders;

  public User(String userId, int balance) { // Id comes from Google Firebase uid
    this.userId = userId;
    this.balance = balance;
  }

  public void setUsersOrders(ArrayList<Order> usersOrders) {
    this.usersOrders = usersOrders;
  }
}
