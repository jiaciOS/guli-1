package com.guli.edu.service;

import com.guli.edu.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.form.CourseInfoForm;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-02-21
 */
public interface CourseService extends IService<Course> {

	boolean getCountBySubjectId(String subjectId);

	/**
	 * 保存课程和课程详情信息
	 * @param courseInfoForm
	 * @return 新生成的课程id
	 */
	String saveCourseInfo(CourseInfoForm courseInfoForm);

	CourseInfoForm getCourseInfoFormById(String id);

	void updateCourseInfoById(CourseInfoForm courseInfoForm);
}
