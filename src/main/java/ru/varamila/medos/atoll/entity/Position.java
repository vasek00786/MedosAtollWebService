package ru.varamila.medos.atoll.entity;

import java.math.BigDecimal;

public class Position {
    private String code;    //код услуги
    private String name;    //наименование услуги
    private int count;            //кол-во услуги в чеке
    private BigDecimal price;   //цена за одну услугу
    private BigDecimal sum;   //цена за все услуги с именем name
    private BigDecimal taxName;   //ндс в процентах (10 20 30)
    private String taxSum;   //сумма ндс

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return this.code; }
    public void setCode(String code) { this.code = code; }

    public int getCount() { return this.count; }
    public void setCount(int count) { this.count = count; }

    public BigDecimal getPrice() { return this.price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getSum() { return this.sum; }
    public void setSum(BigDecimal sum) { this.sum = sum; }

    public BigDecimal getTaxName() { /*return "Ставка НДС " + this.taxName + "%";*/ return  this.taxName; }
    public void setTaxName(BigDecimal taxName) { this.taxName=taxName; }

    public String getTaxSum() { return this.taxSum; }
    public void setTaxSum(String taxSum) { this.taxSum = taxSum; }

    public Position(String name,String code, int count,BigDecimal price,BigDecimal sum, String taxSum) {
        this.name=name; this.code=code; this.count=count; this.price=price; this.sum=sum;  this.taxSum = taxSum;
    }
    public Position () {}
}