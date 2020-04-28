package ru.varamila.medos.atoll;

import javax.ws.rs.Produces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.atol.drivers10.fptr.Fptr;
import ru.atol.drivers10.fptr.IFptr;
import ru.varamila.medos.atoll.entity.Kkm;
import ru.varamila.medos.atoll.entity.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/makePayment")
public class KkmController {
    private static final Logger LOG = LogManager.getLogger(KkmController.class);

    private static final String STATUS = "status";

       private static IFptr printer;
       private static boolean isInit=false;


    @GetMapping
    public String test() {
        return "Hello, test!";
    }

    @PostMapping("/printReceipt")
    @Produces("application/json;charset=UTF-8")
    /*Вот тут и происходит основная работа по печати чека*/
    public String makeResponse(@RequestBody String jsonData) {
       LOG.info("let s get it started: {} ", jsonData);
        Kkm kkm = getKkmFromJson(jsonData);
        String function = kkm.getFunction();
       LOG.info("function = {}", function);
        switch (function) {
            case "printXReport":
               return printReport("X",kkm);
            case "printZReport":
                return printReport("Z",kkm);
            case "printTestReport":
               return printReport("TEST",kkm);
            case "makePayment":
                return makePayment(kkm);
            case "makeRefund":
                return makeRefund(kkm);

            case "continuePrint":
                break;
            case "checkStatus":
               return checkStatus();
            default:
               LOG.error("I don't know wheat to do :-(");
               return "I don't know wheat to do :-(";
        }
        return jsonData;

    }
    /**Инициализация принтера*/
    private static void initPrinter() {
        if (!isInit) {
            String lib = "C:\\1\\fptr10.dll";
            System.load(lib);
            LOG.info("loadeded kkm lib: {}", lib);
            printer = new Fptr();
            LOG.info("INIT_PRINTER, ver = {}", printer.version());
            isInit=true;
        }
    }

    private void openPrinter() {
        if (!isInit) {
            initPrinter();
            LOG.info("init printer");
        }
        if (!printer.isOpened()) {
            printer.open();
            LOG.info("open printer");
        }
    }

    private double round(BigDecimal aSum) {
        return aSum.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /** Приход (продажа)*/
    private String makePayment(Kkm aKkm) {
        return makePaymentOrRefund(aKkm,IFptr.LIBFPTR_RT_SELL);
    }

    /**Возврат суммы*/
    private String makeRefund(Kkm aKkm) {
        return makePaymentOrRefund(aKkm, IFptr.LIBFPTR_RT_SELL_RETURN);
    }

    /**true - good, false - bad*/
    private String checkStatus() {
        openPrinter();
        LOG.info("Начинаем проверку статуса принтера");
    int errorCode = printer.errorCode();
    if (errorCode>0) {
        switch (errorCode) {
            case 80: //Чек не закрыт
                printer.cancelReceipt();
                LOG.info("Receipt is cancel!");
                break;
            case 67: //Открыт чек возврата
                printer.cancelReceipt();
                LOG.info("Receipt is cancel2!");
                break;
            default:
                LOG.warn("unknown errorCode: {}", errorCode);
        }
        LOG.warn("Printer error: {}", errorCode);
        return printer.errorCode()>0 ? printer.errorCode()+"" : null;
    } else {
        LOG.info("NO ERROR printer");
    }
    return null;
}

    private String makePaymentOrRefund(Kkm aKkm, int aCheckType) {
        JsonObject ret = new JsonObject();
        try {
            boolean isRefund = aCheckType==IFptr.LIBFPTR_RT_SELL_RETURN;
            openPrinter();
            operatorLogin(aKkm);
            printer.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, aCheckType);
            if (aKkm.getIsElectronic()!=null && aKkm.getIsElectronic()) {
               LOG.info("is electronic = true");
                printer.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_ELECTRONICALLY, true);
                printer.setParam(1008, aKkm.getCustomerPhone()!=null&&!aKkm.getCustomerPhone().equals("")?aKkm.getCustomerPhone():aKkm.getEmail());
            }
            printer.openReceipt();
           checkStatus();

            if (aKkm.getPos()!=null) {
                for (Position pos:aKkm.getPos()){
                    LOG.info("add pos = {}" ,pos.getName()+pos.getPrice());
                    printer.setParam(IFptr.LIBFPTR_PARAM_COMMODITY_NAME, pos.getCode()+" "+pos.getName());
                    printer.setParam(IFptr.LIBFPTR_PARAM_PRICE, round(pos.getPrice()));
                    printer.setParam(IFptr.LIBFPTR_PARAM_QUANTITY, pos.getCount());
                    printer.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, IFptr.LIBFPTR_TAX_NO);
                    printer.setParam(IFptr.LIBFPTR_PARAM_USE_ONLY_TAX_TYPE, true);
                    printer.registration();
                }
            }
            printer.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, aKkm.getIsTerminalPayment() ? IFptr.LIBFPTR_PT_ELECTRONICALLY : IFptr.LIBFPTR_PT_CASH);
            printer.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_SUM, round(isRefund ? aKkm.getTotalRefundSum() : aKkm.getTotalPaymentSum()));
            printer.payment();
            int a = printer.closeReceipt();
            if (printer.errorCode()>0) {
                LOG.warn("wrong status '{}' before print. maybe, need close smena? Shas close", printer.errorCode());
            }
           LOG.info("closed {}",  a==0 ? "GOOD" : "BAD: "+a);
            boolean isGood =checkDocumentClosed();
            if (!isGood) {
                printer.cancelReceipt();
                ret.addProperty(STATUS,"error");
                ret.addProperty("errorCode",printer.errorDescription());
                if (printer.errorCode()>0) {
                    LOG.warn("Wrons status 1: '{}' перед печатью чека, возможно, надо закрыть пред. смену. Закрываем", printer.errorCode());
                }
            } else {
                ret.addProperty(STATUS,"ok");
                ret.addProperty("receiptId",a);
                if (printer.errorCode()>0) {
                    LOG.warn("Wrons status 2: '{}' перед печатью чека, возможно, надо закрыть пред. смену. Закрываем", printer.errorCode());
                }
            }
        } catch (Exception e) {
            ret.addProperty(STATUS,"systemError");
            ret.addProperty("errorName",e.getLocalizedMessage());
            ret.addProperty("errorFullName",e.getMessage());
        }
        printer.cut();
        return ret.toString();
    }

    /**печать отчетов (X, Z отчеты)*/
    private String printReport(String aReport, Kkm aKkm) {
        openPrinter();
        checkStatus();
        if ("X".equals(aReport)) {
           LOG.info("print X report");
            operatorLogin(aKkm);
            checkStatus();
            printer.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_X);
            checkStatus();
            printer.report();
            checkStatus();
            checkDocumentClosed();

        } else if ("Z".equals(aReport)) {
           LOG.info("print Z report");
            printer.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_CLOSE_SHIFT);
            printer.report();
            printer.checkDocumentClosed();
        } else if ("TEST".equals(aReport)) {
           LOG.info("print TEST report");
            printer.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_OFD_TEST);
            printer.report();
            printer.checkDocumentClosed();
        }
        checkStatus();
        printer.cut();
      //  closePrinter();
        return checkStatus()!=null ? printer.errorCode()+"" : aReport+" успешно напечатан";
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
           LOG.error("err = {}" ,printer.errorDescription());
            return false;
        }
        return true;
    }
    private void operatorLogin(Kkm aKkm) {
        printer.setParam(1021,aKkm.getKassirInfo());
        printer.setParam(1203,aKkm.getInn()!=null ? aKkm.getInn() : "");
        printer.operatorLogin(); //Регистрация кассира
    }
}
