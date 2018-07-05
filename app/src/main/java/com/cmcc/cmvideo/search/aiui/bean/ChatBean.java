package com.cmcc.cmvideo.search.aiui.bean;

/**
 * @Author lhluo
 * @description 闲聊实体
 * @date 2018/5/30
 */
public class ChatBean {

    /**
     * answer : {"answerType":"openQA","emotion":"default","question":{"question":"我好无聊啊","question_ws":"我/NP// 好/VI// 无聊/AA// 啊/UE//"},"text":"有我陪你，你就不会感到无聊啦。","topicID":"32184159073720340","type":"T"}
     * man_intv :
     * no_nlu_result : 0
     * operation : ANSWER
     * rc : 0
     * service : openQA
     * status : 0
     * text : 我好无聊啊
     * uuid : atn00e0fa95@dx00070e69ae8fa11001
     * sid : atn00e0fa95@dx00070e69ae8fa11001
     */

    private AnswerBean answer;
    private String man_intv;
    private int no_nlu_result;
    private String operation;
    private int rc;
    private String service;
    private int status;
    private String text;
    private String uuid;
    private String sid;

    public AnswerBean getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerBean answer) {
        this.answer = answer;
    }

    public String getMan_intv() {
        return man_intv;
    }

    public void setMan_intv(String man_intv) {
        this.man_intv = man_intv;
    }

    public int getNo_nlu_result() {
        return no_nlu_result;
    }

    public void setNo_nlu_result(int no_nlu_result) {
        this.no_nlu_result = no_nlu_result;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public static class AnswerBean {
        /**
         * answerType : openQA
         * emotion : default
         * question : {"question":"我好无聊啊","question_ws":"我/NP// 好/VI// 无聊/AA// 啊/UE//"}
         * text : 有我陪你，你就不会感到无聊啦。
         * topicID : 32184159073720340
         * type : T
         */

        private String answerType;
        private String emotion;
        private QuestionBean question;
        private String text;
        private String topicID;
        private String type;

        public String getAnswerType() {
            return answerType;
        }

        public void setAnswerType(String answerType) {
            this.answerType = answerType;
        }

        public String getEmotion() {
            return emotion;
        }

        public void setEmotion(String emotion) {
            this.emotion = emotion;
        }

        public QuestionBean getQuestion() {
            return question;
        }

        public void setQuestion(QuestionBean question) {
            this.question = question;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTopicID() {
            return topicID;
        }

        public void setTopicID(String topicID) {
            this.topicID = topicID;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static class QuestionBean {
            /**
             * question : 我好无聊啊
             * question_ws : 我/NP// 好/VI// 无聊/AA// 啊/UE//
             */

            private String question;
            private String question_ws;

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public String getQuestion_ws() {
                return question_ws;
            }

            public void setQuestion_ws(String question_ws) {
                this.question_ws = question_ws;
            }
        }
    }
}
