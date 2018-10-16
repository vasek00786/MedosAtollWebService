package ru.varamila.medos.atoll.entity;

import java.util.ArrayList;
import java.util.List;


public class Kkm {

    private List<Position> pos;
    public List<Position> getPos() {
        return pos;
    }
    public void setPos(List<Position> pos) {
        this.pos = pos;
    }


    private double totalPaymentSum; //итого
    public double getTotalPaymentSum() {
        return totalPaymentSum;
    }
    public void setTotalPaymentSum(double totalPaymentSum) {
        this.totalPaymentSum = totalPaymentSum;
    }


    private double totalTaxSum;  //итого ндс
    public double getTotalTaxSum() {
        return totalTaxSum;
    }
    public void setTotalTaxSum(double totalTaxSum) {
        this.totalTaxSum = totalTaxSum;
    }

    private double totalRefundSum;  //итого вернуть
    public double getTotalRefundSum() {
        return totalRefundSum;
    }
    public void setTotalRefundSum(double totalRefundSum) {
        this.totalRefundSum = totalRefundSum;
    }

    private String function; //имя ф-ии
    public String getFunction() {
        return function;
    }
    public void setFunction(String functionName) {}

    private boolean isTerminalPayment;  //нал безнал
    public boolean getIsTerminalPayment() { return isTerminalPayment; }
    public void setIsTerminalPayment(boolean isTerminalPayment) {this.isTerminalPayment=isTerminalPayment;}

    private String FIO; //ФИО кассира
    public String getFIO() {return FIO;}
    public void setFIO(String FIO) {this.FIO = FIO;}

    /** ИНН кассира */
    public String getINN() {return theINN;}
    public void setINN(String aINN) {theINN = aINN;}
    /** ИНН кассира */
    private String theINN ;

    /** Электронный чек */
    public Boolean getIsElectronic() {return isElectronic;}
    public void setIsElectronic(Boolean aIsElectronic) {isElectronic = aIsElectronic;}
    /** Электронный чек */
    private Boolean isElectronic ;

    /** Почта для электронного чека */
    public String  getEmail() {return email;}
    public void setEmail(String  aEmail) {email = aEmail;}
    /** Почта для электронного чека */
    private String  email ;

    /** Номер телефона для электронного чека */
    public String getCustomerPhone() {return customerPhone;}
    public void setCustomerPhone(String aCustomerPhone) {customerPhone = aCustomerPhone;}
    /** Номер телефона для электронного чека */
    private String customerPhone ;

    public Kkm () {
        pos = new ArrayList<Position>();
    }


}