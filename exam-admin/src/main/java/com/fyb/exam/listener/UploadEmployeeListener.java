package com.fyb.exam.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.entity.Employee;
import com.fyb.exam.service.IEmployeeService;
import com.fyb.exam.util.MD5Utils;
import com.fyb.exam.vo.EmployExcelVo;
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
public class UploadEmployeeListener extends AnalysisEventListener<EmployExcelVo> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(UploadEmployeeListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<EmployExcelVo> list = new ArrayList<EmployExcelVo>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private IEmployeeService employeeService;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param employeeService
     */
    public UploadEmployeeListener(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(EmployExcelVo data, AnalysisContext context) {
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
        ArrayList<Employee> employees = new ArrayList<>();
        for (EmployExcelVo employExcelVo : list) {
            //判断工号是否已存在
            QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
            employeeQueryWrapper.eq("employee_code",employExcelVo.getEmployeeCode());
            Employee one = employeeService.getOne(employeeQueryWrapper);
            if (one==null) {
                Employee employee = new Employee();
                employee.setEmployeeCode(employExcelVo.getEmployeeCode());
                employee.setPassword(MD5Utils.encode(employExcelVo.getPassword()));
                employee.setCreateTime(LocalDateTime.now());
                employee.setUpdateTime(LocalDateTime.now());
                employees.add(employee);
            }
        }
        employeeService.saveBatch(employees,BATCH_COUNT);
        LOGGER.info("存储数据库成功！");
    }
}
