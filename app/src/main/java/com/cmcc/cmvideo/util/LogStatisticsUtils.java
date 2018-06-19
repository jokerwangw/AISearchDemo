package com.cmcc.cmvideo.util;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public class LogStatisticsUtils {
    public static void addSendLogMessage(final String logLevel, final String logMsg, final boolean bBlock) {
//        if (!VariableConstant.getInstance().getLevel().contains(logLevel)||VariableConstant.getInstance().getClientLogSwitch().equals("close")) {
//            return;
//        }
//        //上传log日志不用记录
//        if (!TextUtils.isEmpty(logMsg) && logMsg.contains("common/v1/app-logs") || (logMsg.equals(lastSendingLogMsg))) {
//            return;
//        }
//        lastSendingLogMsg = logMsg;
//        final LogData logData = new LogData();
//        mExecutorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (bBlock) {
//                    logData.setTimeStamp(String.valueOf(System.currentTimeMillis()));
//                    logData.setLogLevel(logLevel);
//                    logData.setSessionId(MGRuntimeInfoHelper.getCurrentUserSessionId());
//                    logData.setMessage(logMsg);
//                    sendLog(logData);
//                } else {
//                    LogDataDao logDataDao = DBManager.getInstance(ApplicationContext.application).getSession().getLogDataDao();
//                    logData.setTimeStamp(String.valueOf(System.currentTimeMillis()));
//                    logData.setLogLevel(logLevel);
//                    logData.setSessionId(MGRuntimeInfoHelper.getCurrentUserSessionId());
//                    logData.setMessage(logMsg);
//                    logDataDao.insert(logData);
//                    /** 每条数据保存到数据库 */
//                    sendMoreLogDatas(logDataDao);
//                }
//
//            }
//        });
    }
}
