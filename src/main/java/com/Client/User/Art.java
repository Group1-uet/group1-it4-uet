package com.Client.User;

import java.time.LocalDate;

public class Art extends Items{
    public Art(String id) {
        super(id);
    }

    public Art(String id, String name) {
        super(id, name);
    }

    public Art(String id, String name, String descrpition, double startingPrice, double currentPrice, LocalDate timeStart, LocalDate timeEnd) {
        super(id, name, descrpition, startingPrice, currentPrice, timeStart, timeEnd);
    }
}
