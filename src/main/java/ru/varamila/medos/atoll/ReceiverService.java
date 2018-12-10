package ru.varamila.medos.atoll;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ru.atol.drivers10.fptr.Fptr;
import ru.varamila.medos.atoll.entity.Kkm;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ru.atol.drivers10.fptr.IFptr;
import ru.varamila.medos.atoll.entity.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Path("/print")
public class ReceiverService {
    private static IFptr printer;
    private static boolean isInit=false;

    @GET
    public String test1() {
        return "TEst OK1";
    }

    @POST
    @Path("/test")
    public String test(String jsonData) {
        System.out.println(jsonData);
        return jsonData;
    }

    @GET
    @Path("/closePrinter")
    public String closePrinter() {
        if (printer!=null  && printer.isOpened()) {
            printer.close();
            return "printer closed";
        }
        return "printer already closed";
    }


   @POST
   @Path("/printReceipt")
   @Produces(MediaType.APPLICATION_JSON)
    public String makeResponse(@Context HttpServletRequest aRequest
            ,  String jsonData) {
       System.out.println(jsonData);
        System.out.println("let s get it started");
        Kkm kkm = getKkmFromJson(jsonData);
        String function = kkm.getFunction();
        System.out.println("function = "+function);
        switch (function) {
            case "printXReport":
                printReport("X",kkm);
                break;
            case "printZReport":
                printReport("Z",kkm);
                break;
            case "printTestReport":
                printReport("TEST",kkm);
                break;
            case "makePayment":
                return makePayment(kkm);
            case "makeRefund":
                return makeRefund(kkm);

            case "continuePrint":
                break;
                default:
                    System.out.println("I don't know wheat to do :-(");
        }
        return jsonData;

    }
/**Инициализация принтера*/
    private void initPrinter() {
        if (!isInit) {
             printer = new Fptr();
            System.out.println("INIT_PRINTER, ver = "+printer.version());
            isInit=true;
        }
}

    private void openPrinter() {
        if (!isInit) {
            initPrinter();
        }
        if (!printer.isOpened()) {
            printer.open();
        }
    }

private String toString(BigDecimal aSum) {
        return aSum.setScale(2, RoundingMode.HALF_UP).toString();
}


    /** Приход (продажа)*/
    private String makePayment(Kkm aKkm) {
        return makePaymentOrRefund(aKkm,IFptr.LIBFPTR_RT_SELL);
    }

    /**Возврат суммы*/
    private String makeRefund(Kkm aKkm) {
        return makePaymentOrRefund(aKkm, IFptr.LIBFPTR_RT_SELL_RETURN);
    }

    private String makePaymentOrRefund(Kkm aKkm, int aCheckType) {
        JsonObject ret = new JsonObject();
        try {
            System.out.println(aCheckType+" making payment/refund, print a check!");
            openPrinter();

            operatorLogin(aKkm);
            printer.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, aCheckType);
            if (aKkm.getIsElectronic()!=null && aKkm.getIsElectronic()) {
                System.out.println("is electronic = true");
                printer.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_ELECTRONICALLY, true);
                printer.setParam(1008, aKkm.getCustomerPhone()!=null&&!aKkm.getCustomerPhone().equals("")?aKkm.getCustomerPhone():aKkm.getEmail());
            }
            printer.openReceipt();
            for (Position pos:aKkm.getPos()){
                System.out.println("add pos = "+pos.getName());
                printer.setParam(IFptr.LIBFPTR_PARAM_COMMODITY_NAME, pos.getCode()+" "+pos.getName());
                printer.setParam(IFptr.LIBFPTR_PARAM_PRICE, toString(pos.getPrice()));
                printer.setParam(IFptr.LIBFPTR_PARAM_QUANTITY, pos.getCount());
                printer.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, IFptr.LIBFPTR_TAX_NO);
                printer.setParam(IFptr.LIBFPTR_PARAM_USE_ONLY_TAX_TYPE, true);
                printer.registration();
            }
            printer.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, aKkm.getIsTerminalPayment()?IFptr.LIBFPTR_PT_ELECTRONICALLY:IFptr.LIBFPTR_PT_CASH);
            printer.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_SUM, toString(aKkm.getTotalPaymentSum()));
            printer.payment();
            int a = printer.closeReceipt();
            System.out.println("closed check "+a);
            boolean isGood =checkDocumentClosed();
            if (!isGood) {
                printer.cancelReceipt();
                ret.addProperty("status","error");
                ret.addProperty("errorCode",printer.errorDescription());
            } else {
                ret.addProperty("status","ok");
                ret.addProperty("receiptId",a);
            }
        } catch (Exception e) {
            ret.addProperty("status","systemError");
            ret.addProperty("errorName",e.getLocalizedMessage());
            ret.addProperty("errorFullName",e.getMessage());
        }
        printer.cut();
       closePrinter();

        return ret.toString();
    }

/**печать отчетов (X, Z отчеты)*/
    private boolean printReport(String aReport, Kkm aKkm) {
        openPrinter();
        if ("X".equals(aReport)) {
            System.out.println("print X report");
            operatorLogin(aKkm);
            printer.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_X);
            printer.report();
            checkDocumentClosed();

        } else if ("Z".equals(aReport)) {
            System.out.println("print Z report");
            printer.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_CLOSE_SHIFT);
            printer.report();
            printer.checkDocumentClosed();
        } else if ("TEST".equals(aReport)) {
            System.out.println("print TEST report");
            printer.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_OFD_TEST);
            printer.report();
            printer.checkDocumentClosed();
        }
        printer.cut();
        closePrinter();
        return false;
    }

    private Kkm getKkmFromJson(String aJson) {
        try {
            Gson g = new GsonBuilder().create();
            return g.fromJson(aJson,Kkm.class);
        } catch (Exception e) {
            return null;
        }

    }

    private boolean checkDocumentClosed() {
        if (printer.checkDocumentClosed()<0) {
            System.out.println("err = "+printer.errorDescription());
            return false;
        }
        return true;
    }
    private void operatorLogin(Kkm aKkm) {
        printer.setParam(1021,aKkm.getFIO());
        printer.setParam(1203,aKkm.getINN()!=null?aKkm.getINN():"");
        printer.operatorLogin(); //Регистрация кассира
    }


}
