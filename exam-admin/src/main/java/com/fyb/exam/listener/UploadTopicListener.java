package com.fyb.exam.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.Const;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.service.ITopicService;
import com.fyb.exam.vo.TopicExcelVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 模板的读取类
 *
 * @author Jiaju Zhuang
 */
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class UploadTopicListener extends AnalysisEventListener<TopicExcelVo> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(UploadTopicListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<TopicExcelVo> list = new ArrayList<TopicExcelVo>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private ITopicService topicService;

    private ArrayList<TopicExcelVo> topicExcelVos;

    private String creatorId;

    private Integer areaId;

    private int successCount=0;

    private int failCount=0;

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param topicService
     * @param topicExcelVos
     */
    public UploadTopicListener(ITopicService topicService, ArrayList<TopicExcelVo> topicExcelVos,String creatorId,Integer areaId) {
        this.topicService = topicService;
        this.topicExcelVos=topicExcelVos;
        this.creatorId=creatorId;
        this.areaId=areaId;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(TopicExcelVo data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", data.toString());
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        ArrayList<Topic> topics = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (TopicExcelVo topicExcelVo : list) {
            //题干不能为空，题型不能为空，正确选项不能为空
            if (StringUtils.isEmpty(topicExcelVo.getTopicDesc())||StringUtils.isEmpty(topicExcelVo.getCorrectAnswer())
                    ||topicExcelVo.getType()==null) {
                topicExcelVos.add(topicExcelVo);
                failCount++;
                continue;
            }
            //题干与数据库已有重复的
            QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
            topicQueryWrapper.eq("topic_desc",topicExcelVo.getTopicDesc());
            Topic one = topicService.getOne(topicQueryWrapper);
            if (one!=null) {
                topicExcelVos.add(topicExcelVo);
                failCount++;
                continue;
            }
            //即将批量存储的这5条数据中存在题干重复的
            if(!hashSet.add(topicExcelVo.getTopicDesc())){
                topicExcelVos.add(topicExcelVo);
                failCount++;
                continue;
            }
            //单选题
            if (topicExcelVo.getType() == Const.SINGLE_TYPE) {
                String correctAnswer = topicExcelVo.getCorrectAnswer();
                if (correctAnswer.length() > 1) {
                    topicExcelVos.add(topicExcelVo);
                    failCount++;
                    continue;
                }
            }
            //判断题
            if (topicExcelVo.getType() == Const.JUDGE_TYPE) {
                int count=0;
                for (int i = 1; i < 7; i++) {
                    try {
                        Method method = topicExcelVo.getClass().getMethod("getAnswer" + i);
                        Object invoke = method.invoke(topicExcelVo);
                        if (invoke!=null) {
                            count++;
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if(count!=2){
                    topicExcelVos.add(topicExcelVo);
                    failCount++;
                    continue;
                }
                if(!topicExcelVo.getCorrectAnswer().equals("A")&&!topicExcelVo.getCorrectAnswer().equals("B")){
                    topicExcelVos.add(topicExcelVo);
                    failCount++;
                    continue;
                }
            }
            //多选题
            if(topicExcelVo.getType()==Const.MULTIPLE_TYPE){
                String correctAnswer = topicExcelVo.getCorrectAnswer();
                if (!(correctAnswer.length()>1)) {
                    topicExcelVos.add(topicExcelVo);
                    failCount++;
                    continue;
                }
            }
            //题型不在这三种范围的
            if(topicExcelVo.getType()!=1&&topicExcelVo.getType()!=2&&topicExcelVo.getType()!=3){
                topicExcelVos.add(topicExcelVo);
                failCount++;
                continue;
            }
            Topic topic = new Topic();
            topic.setTopicDesc(topicExcelVo.getTopicDesc());
            topic.setType(topicExcelVo.getType());
            topic.setAnswer1(topicExcelVo.getAnswer1());
            topic.setAnswer2(topicExcelVo.getAnswer2());
            topic.setAnswer3(topicExcelVo.getAnswer3());
            topic.setAnswer4(topicExcelVo.getAnswer4());
            topic.setAnswer5(topicExcelVo.getAnswer5());
            topic.setAnswer6(topicExcelVo.getAnswer6());
            topic.setCorrectAnswer(topicExcelVo.getCorrectAnswer());
            topic.setCreateTime(LocalDateTime.now());
            topic.setUpdateTime(LocalDateTime.now());
            topic.setCreatorId(creatorId);
            topic.setAreaId(areaId);
            topic.setLastOperatorId(creatorId);
            topics.add(topic);
            successCount++;
        }
        topicService.saveBatch(topics, BATCH_COUNT);
        LOGGER.info("存储数据库成功！");
    }
}
