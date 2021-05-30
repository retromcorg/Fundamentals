package com.johnymuffin.beta.fundamentals.banks;

import com.johnymuffin.beta.fundamentals.simplejson.JSONArray;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;

import java.util.UUID;

public class FundamentalsBank {
    private final String bankName;
    private final UUID bankOwner;
    private UUID[] accessList;
    private double balance;


    public FundamentalsBank(String bankName, UUID bankOwner, UUID[] accessList, double balance) {
        this.bankName = bankName;
        this.bankOwner = bankOwner;
        this.accessList = accessList;
        this.balance = balance;
    }

    public FundamentalsBank(JSONObject jsonInfo) {
        this.bankName = String.valueOf(jsonInfo.get("bankName"));
        this.bankOwner = UUID.fromString(String.valueOf("ownerUUID"));
        if (jsonInfo.containsKey("accessList")) {
            JSONArray arrayAccessList = (JSONArray) jsonInfo.get("accessList");
            this.accessList = new UUID[arrayAccessList.size()];
            int i = 0;
            for (Object rawUUID : arrayAccessList) {
                this.accessList[i] = UUID.fromString(String.valueOf(rawUUID));
                i = i + 1;
            }
        } else {
            this.accessList = new UUID[0];
        }
        this.balance = Double.valueOf(String.valueOf(jsonInfo.get("balance")));
    }


    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bankName", bankName);
        jsonObject.put("ownerUUID", bankOwner);
        JSONArray accessList = new JSONArray();
        for (UUID uuid : this.accessList) {
            accessList.add(uuid.toString());
        }
        jsonObject.put("accessList", accessList);
        jsonObject.put("balance", balance);
        return jsonObject;
    }

    public String getBankName() {
        return bankName;
    }

    public UUID getBankOwner() {
        return bankOwner;
    }

    public UUID[] getAccessList() {
        return accessList;
    }

    public void setAccessList(UUID[] accessList) {
        this.accessList = accessList;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
