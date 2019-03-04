package com.guli.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.entity.Subject;
import com.guli.edu.form.CourseInfoForm;
import com.guli.edu.vo.SubjectNestedVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-02-21
 */
public interface SubjectService extends IService<Subject> {

	List<String> batchImport(MultipartFile file);

	List<SubjectNestedVo> nestedList();

	boolean removeSubjectById(String id);

	boolean saveLevelOne(Subject subject);

	boolean saveLevelTwo(Subject subject);

}
