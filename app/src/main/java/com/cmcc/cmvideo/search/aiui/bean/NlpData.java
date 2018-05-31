package com.cmcc.cmvideo.search.aiui.bean;

import java.util.List;

public class NlpData {
    public int rc;
    public TppData data;
    public String service;
    public String text;
    public String uuid;
    public String sid;
    public AnswerBean answer;
    public List<SemanticBean> semantic;


    public static class SemanticBean {
        public String intent;
        public List<SlotsBean> slots;

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public List<SlotsBean> getSlots() {
            return slots;
        }

        public void setSlots(List<SlotsBean> slots) {
            this.slots = slots;
        }
    }

    public static class SlotsBean {
        public String name;
        public String value;
        public String normValue;

    }


    /**
     * answer : {"answerType":"openQA","emotion":"default","question":{"question":"我好无聊啊","question_ws":"我/NP// 好/VI// 无聊/AA// 啊/UE//"},"text":"有我陪你，你就不会感到无聊啦。","topicID":"32184159073720340","type":"T"}
     */

    public AnswerBean getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerBean answer) {
        this.answer = answer;
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

        public String answerType;
        public String emotion;
        public QuestionBean question;
        public String text;
        public String topicID;
        public String type;


        public static class QuestionBean {
            /**
             * question : 我好无聊啊
             * question_ws : 我/NP// 好/VI// 无聊/AA// 啊/UE//
             */

            public String question;
            public String question_ws;

        }
    }

}
