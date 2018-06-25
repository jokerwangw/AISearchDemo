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

    /**
     * rc == 4 反馈语
     */
    private List<Response> errorRcMsgList = new ArrayList<Response>(Arrays.asList(
            new Response("这个问题有点难，我还需要学习", RespType.RESULTERROMSG),
            new Response("对不起，我开小差了", RespType.RESULTERROMSG),
            new Response("我还在学习中，等我学会再回答你", RespType.RESULTERROMSG),
            new Response("我好像没听明白，再说一次呗", RespType.RESULTERROMSG)
    ));


    /**
     * 网络不好 类似后台查询异常反馈语
     */
    private List<Response> networkResponseList = new ArrayList<Response>(Arrays.asList(
            new Response("对不起，我开小差了", RespType.NETWORK),
            new Response("没有找到你想要的，我再努力试试", RespType.NETWORK),
            new Response("我好像没听明白，再说一次呗", RespType.NETWORK),
            new Response("我已经尽力了，但是没有找到结果", RespType.NETWORK)
    ));

    private List<Response> everyoneSeeList = new ArrayList<Response>(Arrays.asList(
            new Response("小咪为你找到了一些%s", RespType.VIDEO_TYPE),
            new Response("你觉得这些%s怎么样", RespType.VIDEO_TYPE),
            new Response("现在比较流行这些%s", RespType.VIDEO_TYPE),
            new Response("emm，可能是这些吧", RespType.NORMAL)
    ));
    private List<Response> guessWhatYouLikeList = new ArrayList<Response>(Arrays.asList(
            new Response("这部%s怎么样", RespType.VIDEO_TYPE),
            new Response("我想你会喜欢这部%s", RespType.VIDEO_TYPE),
            new Response("我觉得这个不错", RespType.NORMAL),
            new Response("我翻了翻抽屉，只找到这个", RespType.NORMAL),
            new Response("咱俩这么熟了，为你推荐个好看的", RespType.NORMAL),
            new Response("%s怎么样？", RespType.VIDEO_NAME),
            new Response("emm，我个人比较喜欢这个", RespType.NORMAL),
            new Response("最近流行%s", RespType.VIDEO_NAME),
            new Response("最近%s还不错哟", RespType.VIDEO_NAME)
    ));
    private List<Response> newVideoList = new ArrayList<Response>(Arrays.asList(
            new Response("最近流行%s", RespType.VIDEO_NAME),
            new Response("最近%还不错", RespType.VIDEO_NAME),
            new Response("小咪为你找到了这些", RespType.NORMAL),
            new Response("这几部%s不错哟", RespType.VIDEO_TYPE),
            new Response("这几部%s还可以", RespType.VIDEO_TYPE)
    ));

    private List<Response> albumList = new ArrayList<Response>(Arrays.asList(
            new Response("好的，%s的视频", RespType.USER_NAME),
            new Response("%s的不错，我也很喜欢，看看这些是视频是不是你要的", RespType.USER_NAME),
            new Response("小case，那，挑个喜欢的吧", RespType.VIDEO_TYPE),
            new Response("找到了，你想要的是不是这些？", RespType.NORMAL),
            new Response("只找到这么多，表示已经尽力了", RespType.NORMAL)
    ));


    /**
     * rc等于4随机反馈一条反馈语
     *
     * @return
     */
    public Response getResultResponse() {
        return errorRcMsgList.get(random.nextInt(errorRcMsgList.size()));
    }


    /**
     * 随机获取一条网络超时反馈语
     *
     * @return 反馈语
     */
    public Response getNetWorkStatus() {
        return networkResponseList.get(random.nextInt(networkResponseList.size()));
    }

    /**
     * 随机获取一条大家都在看的反馈语
     *
     * @return 反馈语对象
     */
    public Response getEveryoneSee() {
        return everyoneSeeList.get(random.nextInt(everyoneSeeList.size()));
    }

    /**
     * 随机获取一条猜你喜欢的反馈语
     *
     * @return 反馈语对象
     */
    public Response getGuessWhatYouLike() {
        return guessWhatYouLikeList.get(random.nextInt(guessWhatYouLikeList.size()));
    }

    /**
     * 随机获取一条最新视频的反馈语
     *
     * @return 反馈语对象
     */
    public Response getNewVideo() {
        return newVideoList.get(random.nextInt(newVideoList.size()));
    }

    /**
     * 随机获取一条人物专辑的反馈语
     *
     * @return 反馈语对象
     */
    public Response getAlbum() {
        return albumList.get(random.nextInt(albumList.size()));
    }


    public class Response {
        public Response() {
        }

        public Response(String resp, RespType rt) {
            this.response = resp;
            this.respType = rt;
        }

        public String response;
        public RespType respType;
    }

    public enum RespType {
        NORMAL,
        VIDEO_TYPE,
        VIDEO_NAME,
        USER_NAME,
        NETWORK,
        RESULTERROMSG
    }
}
