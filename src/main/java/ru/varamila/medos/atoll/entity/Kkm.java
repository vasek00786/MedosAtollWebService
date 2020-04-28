package ru.varamila.medos.atoll.entity;

import java.math.BigDecimal;
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

    private BigDecimal totalPaymentSum; //итого
    public BigDecimal getTotalPaymentSum() {
        return totalPaymentSum;
    }
    public void setTotalPaymentSum(BigDecimal totalPaymentSum) {
        this.totalPaymentSum = totalPaymentSum;
    }

    private BigDecimal totalTaxSum;  //итого ндс
    public BigDecimal getTotalTaxSum() {
        return totalTaxSum;
    }
    public void setTotalTaxSum(BigDecimal totalTaxSum) {
        this.totalTaxSum = totalTaxSum;
    }

    private BigDecimal totalRefundSum;  //итого вернуть
    public BigDecimal getTotalRefundSum() {
        return totalRefundSum;
    }
    public void setTotalRefundSum(BigDecimal totalRefundSum) {
        this.totalRefundSum = totalRefundSum;
    }

    private String function; //имя ф-ии
    public String getFunction() {
        return function;
    }
    public void setFunction(String functionName) {this.function=functionName;}

    private boolean isTerminalPayment;  //нал безнал
    public boolean getIsTerminalPayment() { return isTerminalPayment; }
    public void setIsTerminalPayment(boolean isTerminalPayment) {this.isTerminalPayment=isTerminalPayment;}

    private String kassirInfo; //ФИО кассира
    public String getKassirInfo() {return kassirInfo;}
    public void setKassirInfo(String kassirInfo) {this.kassirInfo = kassirInfo;}

    /** ИНН кассира */
    public String getInn() {return inn;}
    public void setInn(String aINN) {inn = aINN;}
    private String inn;

    /** Электронный чек */
    public Boolean getIsElectronic() {return isElectronic;}
    public void setIsElectronic(Boolean aIsElectronic) {isElectronic = aIsElectronic;}
    private Boolean isElectronic ;

    /** Почта для электронного чека */
    public String  getEmail() {return email;}
    public void setEmail(String  aEmail) {email = aEmail;}
    private String  email ;

    /** Номер телефона для электронного чека */
    public String getCustomerPhone() {return customerPhone;}
    public void setCustomerPhone(String aCustomerPhone) {customerPhone = aCustomerPhone;}
    private String customerPhone ;

    public Kkm () {
        pos = new ArrayList<>();
    }


}