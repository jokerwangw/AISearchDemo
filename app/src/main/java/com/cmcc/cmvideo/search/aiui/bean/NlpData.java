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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getNormValue() {
            return normValue;
        }

        public void setNormValue(String normValue) {
            this.normValue = normValue;
        }
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
