package com.php25.timetasks.cron;

/**
 * @author penghuiping
 * @date 2020/6/28 17:30
 */
class SymbolCronExpr extends Symbol {

    /**
     * false:week,true:day
     */
    protected boolean weekOrDay = false;

    /**
     * true:工作日,false:非工作日
     */
    protected boolean workday = false;
}
