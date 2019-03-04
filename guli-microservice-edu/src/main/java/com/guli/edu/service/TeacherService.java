package com.guli.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.entity.Teacher;
import com.guli.edu.query.TeacherQuery;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-02-21
 */
public interface TeacherService extends IService<Teacher> {

	void pageQuery(Page<Teacher> pageParam, TeacherQuery teacherQuery);
}
