package ru.varamila.medos.atoll.entity;

public class Position {
    private String code;    //код услуги
    private String name;    //наименование услуги
    private int count;            //кол-во услуги в чеке
    private double price;   //цена за одну услугу
    private double sum;   //цена за все услуги с именем name
    private double taxName;   //ндс в процентах (10 20 30)
    private String taxSum;   //сумма ндс

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return this.code; }
    public void setCode(String code) { this.code = code; }

    public int getCount() { return this.count; }
    public void setCount(int count) { this.count = count; }

    public double getPrice() { return this.price; }
    public void setPrice(double price) { this.price = price; }

    public double getSum() { return this.sum; }
    public void setSum(double sum) { this.sum = sum; }

    public double getTaxName() { /*return "Ставка НДС " + this.taxName + "%";*/ return  this.taxName; }
    public void setTaxName(double taxName) { this.taxName=taxName; }

    public String getTaxSum() { return this.taxSum; }
    public void setTaxSum(String taxSum) { this.taxSum = taxSum; }

    public Position(String name,String code, int count,double price,double sum, String taxName,String taxSum) {
        this.name=name; this.code=code; this.count=count; this.price=price; this.sum=sum;  this.taxSum = taxSum;
    }
    public Position () {}
}