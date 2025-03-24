package org.database;

import orderProcessor.Order;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    UUID userId;
    int balance = 0;
    ArrayList<Order> usersOrders;

    public User(int balance) {
        userId = UUID.randomUUID();
        this.balance = balance;
    }

    public void setUsersOrders(ArrayList<Order> usersOrders) {
        this.usersOrders = usersOrders;
    }

}
