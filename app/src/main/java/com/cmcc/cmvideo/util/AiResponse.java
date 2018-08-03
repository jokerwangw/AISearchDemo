package com.cmcc.cmvideo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AiResponse {
    private static AiResponse instance = null;
    private Random random = new Random();

    public enum RespType {
        //做下注释
        NORMAL,
        VIDEO_TYPE,
        VIDEO_NAME,
        USER_NAME,
        NETWORK,
        RESULTERROMSG
    }

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

    /**
     * 大家都在看什么电视剧
     */
    private List<Response> everyoneSeeList = new ArrayList<Response>(Arrays.asList(
            new Response("小咪为你找到了一些%s", RespType.VIDEO_TYPE),
            new Response("你觉得这些%s怎么样", RespType.VIDEO_TYPE),
            new Response("现在比较流行这些%s", RespType.VIDEO_TYPE),
            new Response("看看这些是不是你想要的", RespType.VIDEO_TYPE),
            new Response("小case，那，挑个喜欢的吧", RespType.VIDEO_TYPE),
            new Response("找到了，你想要的是不是这些？", RespType.VIDEO_TYPE),
            new Response("只找到这么多，表示已经尽力了", RespType.VIDEO_TYPE),
            new Response("emm，可能是这些吧", RespType.NORMAL)
    ));

    /**
     * 给我推荐个综艺、综艺节目，娱乐节目  动漫  动画片  纪实
     */
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

    /**
     * 有什么热门电视剧  流行电视剧    everyoneSeeList
     */
    private List<Response> newVideoList = new ArrayList<Response>(Arrays.asList(
            new Response("最近流行%s", RespType.VIDEO_NAME),
            new Response("最近%还不错", RespType.VIDEO_NAME),
            new Response("小咪为你找到了这些", RespType.NORMAL),
            new Response("这几部%s不错哟", RespType.VIDEO_TYPE),
            new Response("这几部%s还可以", RespType.VIDEO_TYPE)
    ));


    /**
     * 演员名字 + 片名
     */
    private List<Response> albumList = new ArrayList<Response>(Arrays.asList(
            new Response("好的，%s的视频", RespType.USER_NAME),
            new Response("%s的不错，我也很喜欢", RespType.USER_NAME),
            new Response("看看这些是不是你想要的", RespType.USER_NAME),
            new Response("小case，那，挑个喜欢的吧", RespType.VIDEO_TYPE),
            new Response("找到了，你想要的是不是这些？", RespType.NORMAL),
            new Response("只找到这么多，表示已经尽力了", RespType.NORMAL)
    ));

    private List<Response> sleepList = new ArrayList<Response>(Arrays.asList(
            new Response("念动咒语咪咕咪咕我会回来", RespType.NORMAL)
    ));

    private List<Response> changeList = new ArrayList<Response>(Arrays.asList(
            new Response("我的墨水被榨干了呢", RespType.NORMAL),
            new Response("不要急，让我再想想", RespType.NORMAL),
            new Response("噢哟，见底了呢", RespType.NORMAL),
            new Response("Come on,baby,再试一次", RespType.NORMAL)

    ));

    /**
     * 换一个 没有数据了 反馈语
     *
     * @return
     */
    public Response getChangeResponse() {
        Response target = null;
        try {
            target = (Response) changeList.get(random.nextInt(changeList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * rc等于4随机反馈一条反馈语
     *
     * @return
     */
    public Response getResultResponse() {
        Response target = null;
        try {
            target = (Response) errorRcMsgList.get(random.nextInt(errorRcMsgList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * 随机获取一条网络超时反馈语
     *
     * @return 反馈语
     */
    public Response getNetWorkStatus() {
        Response target = null;
        try {
            target = (Response) networkResponseList.get(random.nextInt(networkResponseList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * 随机获取一条大家都在看的反馈语
     *
     * @return 反馈语对象
     */
    public Response getEveryoneSee() {
        Response target = null;
        try {
            target = (Response) everyoneSeeList.get(random.nextInt(everyoneSeeList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * 随机获取一条猜你喜欢的反馈语
     *
     * @return 反馈语对象
     */
    public Response getGuessWhatYouLike() {
        Response target = null;
        try {
            target = (Response) guessWhatYouLikeList.get(random.nextInt(guessWhatYouLikeList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * 随机获取一条最新视频的反馈语
     *
     * @return 反馈语对象
     */
    public Response getNewVideo() {
        Response target = null;
        try {
            target = (Response) newVideoList.get(random.nextInt(newVideoList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * 随机获取一条人物专辑的反馈语
     *
     * @return 反馈语对象
     */
    public Response getAlbum() {
        Response target = null;
        try {
            target = (Response) albumList.get(random.nextInt(albumList.size())).clone();
        } catch (CloneNotSupportedException c) {
            c.printStackTrace();
        }
        return target;
    }

    /**
     * 获取休眠反馈语
     *
     * @return
     */
    public Response getSleep() {
        return sleepList.get(0);
    }

    public class Response implements Cloneable {
        public Response() {
        }

        public Response(String resp, RespType rt) {
            this.response = resp;
            this.respType = rt;
        }

        public String response;
        public RespType respType;

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
