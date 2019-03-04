package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.common.constants.PriceConstants;
import com.guli.common.exception.GuliException;
import com.guli.edu.entity.Course;
import com.guli.edu.entity.CourseDescription;
import com.guli.edu.form.CourseInfoForm;
import com.guli.edu.mapper.CourseMapper;
import com.guli.edu.service.CourseDescriptionService;
import com.guli.edu.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-02-21
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

	@Autowired
	private CourseDescriptionService courseDescriptionService;

	/**
	 * 根据课程类别id查询课程的数量
	 * @param subjectId
	 * @return
	 */
	@Override
	public boolean getCountBySubjectId(String subjectId) {
		QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("subject_id", subjectId);
		Integer count = baseMapper.selectCount(queryWrapper);
		return null != count && count > 0;
	}

	@Transactional
	@Override
	public String saveCourseInfo(CourseInfoForm courseInfoForm) {

		//保存课程基本信息
		Course course = new Course();
		course.setStatus(Course.COURSE_DRAFT);
		BeanUtils.copyProperties(courseInfoForm, course);
		boolean resultCourseInfo = this.save(course);
		if(!resultCourseInfo){
			throw new GuliException(20001, "课程信息保存失败");
		}

		//保存课程详情信息
		CourseDescription courseDescription = new CourseDescription();
		courseDescription.setDescription(courseInfoForm.getDescription());
		courseDescription.setId(course.getId());
		boolean resultDescription = courseDescriptionService.save(courseDescription);
		if(!resultDescription){
			throw new GuliException(20001, "课程详情信息保存失败");
		}

		return course.getId();
	}

	@Override
	public CourseInfoForm getCourseInfoFormById(String id) {

		Course course = this.getById(id);
		if(course == null){
			throw new GuliException(20001, "数据不存在");
		}

		//课程基本信息
		CourseInfoForm courseInfoForm = new CourseInfoForm();
		BeanUtils.copyProperties(course, courseInfoForm);

		//课程详情
		CourseDescription courseDescription = courseDescriptionService.getById(id);
		if(course != null){
			courseInfoForm.setDescription(courseDescription.getDescription());
		}

		//设置显示精度：舍弃多余的位数
		courseInfoForm.setPrice(courseInfoForm.getPrice()
				.setScale(PriceConstants.DISPLAY_SCALE, BigDecimal.ROUND_FLOOR));

		return courseInfoForm;
	}

	@Override
	public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
		//保存课程基本信息
		Course course = new Course();
		BeanUtils.copyProperties(courseInfoForm, course);
		boolean resultCourseInfo = this.updateById(course);
		if(!resultCourseInfo){
			throw new GuliException(20001, "课程信息保存失败");
		}

		//保存课程详情信息
		CourseDescription courseDescription = new CourseDescription();
		courseDescription.setDescription(courseInfoForm.getDescription());
		courseDescription.setId(course.getId());
		boolean resultDescription = courseDescriptionService.updateById(courseDescription);
		if(!resultDescription){
			throw new GuliException(20001, "课程详情信息保存失败");
		}
	}
}
