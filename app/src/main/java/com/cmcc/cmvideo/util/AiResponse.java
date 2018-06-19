package com.cmcc.cmvideo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AiResponse {
    private static AiResponse instance = null;
    private Random random = new Random();
    private AiResponse() {

    }
    public static synchronized AiResponse getInstance() {
        if (instance == null) {
            synchronized (AiResponse.class) {
                if (instance == null) {
                    instance = new AiResponse();
                }
            }
        }
        return instance;
    }
    private List<Response> everyoneSeeList = new ArrayList<Response>(Arrays.asList(
            new Response("小咪为你找到了一些%s",RespType.VIDEO_TYPE),
            new Response("你觉得这些%s怎么样",RespType.VIDEO_TYPE),
            new Response("现在比较流行这些%s",RespType.VIDEO_TYPE),
            new Response("emm，可能是这些吧",RespType.NORMAL)
    ));
    private List<Response> guessWhatYouLikeList = new ArrayList<Response>(Arrays.asList(
            new Response("这部%s怎么样",RespType.VIDEO_TYPE),
            new Response("我想你会喜欢这部%s",RespType.VIDEO_TYPE),
            new Response("我觉得这个不错，要不要试试？",RespType.NORMAL),
            new Response("我翻了翻抽屉，只找到这个",RespType.NORMAL),
            new Response("咱俩这么熟了，为你推荐个好看的",RespType.NORMAL),
            new Response("%s怎么样？",RespType.VIDEO_NAME),
            new Response("emm，我个人比较喜欢这个",RespType.NORMAL),
            new Response("最近流行%s，要看吗？",RespType.VIDEO_NAME),
            new Response("最近%s不错，要看吗？",RespType.VIDEO_NAME)
    ));
    private List<Response> newVideoList = new ArrayList<Response>(Arrays.asList(
            new Response("最近流行%s，要看吗？",RespType.VIDEO_NAME),
            new Response("最近%s不错，要看吗？",RespType.VIDEO_NAME),
            new Response("小咪为你找到了这些",RespType.NORMAL),
            new Response("这几部%s不错哟",RespType.VIDEO_TYPE),
            new Response("这几部%s还可以",RespType.VIDEO_TYPE)
    ));

    private List<Response> albumList = new ArrayList<Response>(Arrays.asList(
            new Response("好的，%s的视频",RespType.USER_NAME),
            new Response("%s的不错，我也很喜欢，看看这些是视频是不是你要的",RespType.USER_NAME),
            new Response("小case，那，挑个喜欢的吧",RespType.VIDEO_TYPE),
            new Response("找到了，看看？",RespType.NORMAL),
            new Response("只找到这么多，表示已经尽力了",RespType.NORMAL)
    ));
    /**
     * 随机获取一条大家都在看的反馈语
     * @return 反馈语对象
     */
    public Response getEveryoneSee(){
        return everyoneSeeList.get(random.nextInt(everyoneSeeList.size()));
    }
    /**
     * 随机获取一条猜你喜欢的反馈语
     * @return 反馈语对象
     */
    public Response getGuessWhatYouLike(){
        return guessWhatYouLikeList.get(random.nextInt(guessWhatYouLikeList.size()));
    }
    /**
     * 随机获取一条最新视频的反馈语
     * @return 反馈语对象
     */
    public Response getNewVideo(){
        return newVideoList.get(random.nextInt(newVideoList.size()));
    }
    /**
     * 随机获取一条人物专辑的反馈语
     * @return 反馈语对象
     */
    public Response getAlbum(){
        return albumList.get(random.nextInt(albumList.size()));
    }


    public class Response{
        public Response(){}
        public Response(String resp,RespType rt){
            this.response = resp;
            this.respType =rt;
        }
        public String response;
        public RespType respType;
    }

    public enum RespType{
        NORMAL,
        VIDEO_TYPE,
        VIDEO_NAME,
        USER_NAME
    }
}
