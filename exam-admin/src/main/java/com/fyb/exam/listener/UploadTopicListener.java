package com.fyb.exam.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.service.ITopicService;
import com.fyb.exam.vo.TopicExcelVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param topicService
     */
    public UploadTopicListener(ITopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
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
        for (TopicExcelVo topicExcelVo : list) {
            Topic topic = new Topic();
            topic.setTopicDesc(topicExcelVo.getTopicDesc());
            topic.setAnswer1(topicExcelVo.getAnswer1());
            topic.setAnswer2(topicExcelVo.getAnswer2());
            topic.setAnswer3(topicExcelVo.getAnswer3());
            topic.setAnswer4(topicExcelVo.getAnswer4());
            topic.setCorrectAnswer(topicExcelVo.getCorrectAnswer());
            topic.setCreateTime(LocalDateTime.now());
            topic.setUpdateTime(LocalDateTime.now());
            topics.add(topic);
        }
        topicService.saveBatch(topics,BATCH_COUNT);
        LOGGER.info("存储数据库成功！");
    }
}
