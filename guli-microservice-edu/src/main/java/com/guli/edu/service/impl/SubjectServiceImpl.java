package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExcelImportUtil;
import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.CourseService;
import com.guli.edu.service.SubjectService;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.vo.SubjectVo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-02-21
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

	@Autowired
	private CourseService courseService;

	@Transactional
	@Override
	public List<String> batchImport(MultipartFile file) {

		List<String> msg = new ArrayList<>();
		try {

			ExcelImportUtil excelHSSFUtil = new ExcelImportUtil(file.getInputStream());
			Sheet sheet = excelHSSFUtil.getSheet();

			int rowCount = sheet.getPhysicalNumberOfRows();
			if (rowCount <= 1) {
				msg.add("请填写数据");
				return msg;
			}
			for (int rowNum = 1; rowNum < rowCount; rowNum++) {

				Row rowData = sheet.getRow(rowNum);
				if (rowData != null) {// 行不为空

					//一级分类名称
					String levelOneValue = "";
					Cell levelOneCell = rowData.getCell(0);
					if(levelOneCell != null){
						levelOneValue = excelHSSFUtil.getCellValue(levelOneCell);
						if (StringUtils.isEmpty(levelOneValue)) {
							msg.add("第" + rowNum + "行一级分类为空");
							continue;
						}
					}

					Subject subject = this.getByTitle(levelOneValue);
					Subject subjectLevelOne = null;
					String parentId = null;
					if(subject == null){//创建一级分类
						subjectLevelOne = new Subject();
						subjectLevelOne.setTitle(levelOneValue);
						subjectLevelOne.setSort(0);
						baseMapper.insert(subjectLevelOne);//添加
						parentId = subjectLevelOne.getId();
					}else{
						parentId = subject.getId();
					}

					//二级分类名称
					String levelTwoValue = "";
					Cell levelTwoCell = rowData.getCell(1);
					if(levelTwoCell != null){
						levelTwoValue = excelHSSFUtil.getCellValue(levelTwoCell);
						if (StringUtils.isEmpty(levelTwoValue)) {
							msg.add("第" + rowNum + "行二级分类为空");
							continue;
						}
					}

					Subject subjectSub = this.getSubByTitle(levelTwoValue, parentId);
					Subject subjectLevelTwo = null;
					if(subjectSub == null){//创建二级分类
						subjectLevelTwo = new Subject();
						subjectLevelTwo.setTitle(levelTwoValue);
						subjectLevelTwo.setParentId(parentId);
						subjectLevelTwo.setSort(0);
						baseMapper.insert(subjectLevelTwo);//添加
					}
				}
			}

		}catch (Exception e){
			//EXCEL_DATA_ERROR(false, 21005, "Excel数据导入错误");
			throw new GuliException(ResultCodeEnum.EXCEL_DATA_IMPORT_ERROR);
		}

		return msg;
	}

	@Override
	public List<SubjectNestedVo> nestedList() {

		//最终要的到的数据列表
		ArrayList<SubjectNestedVo> subjectNestedVoArrayList = new ArrayList<>();

		//获取一级分类数据记录
		QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id", 0);
		queryWrapper.orderByAsc("sort", "id");
		List<Subject> subjects = baseMapper.selectList(queryWrapper);

		//获取二级分类数据记录
		QueryWrapper<Subject> queryWrapper2 = new QueryWrapper<>();
		queryWrapper2.ne("parent_id", 0);
		queryWrapper2.orderByAsc("sort", "id");
		List<Subject> subSubjects = baseMapper.selectList(queryWrapper2);

		//填充一级分类vo数据
		int count = subjects.size();
		for (int i = 0; i < count; i++) {
			Subject subject = subjects.get(i);

			//创建一级类别vo对象
			SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
			BeanUtils.copyProperties(subject, subjectNestedVo);
			subjectNestedVoArrayList.add(subjectNestedVo);

			//填充二级分类vo数据
			ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
			int count2 = subSubjects.size();
			for (int j = 0; j < count2; j++) {

				Subject subSubject = subSubjects.get(j);
				if(subject.getId().equals(subSubject.getParentId())){

					//创建二级类别vo对象
					SubjectVo subjectVo = new SubjectVo();
					BeanUtils.copyProperties(subSubject, subjectVo);
					subjectVoArrayList.add(subjectVo);
				}
			}
			subjectNestedVo.setChildren(subjectVoArrayList);
		}

		return subjectNestedVoArrayList;
	}

	@Override
	public boolean saveLevelOne(Subject subject) {

		Subject subjectLevelOne = this.getByTitle(subject.getTitle());
		if(subjectLevelOne == null){
			return this.save(subject);
		}else{
			throw new GuliException(20001, "类别已存在");
		}
	}

	@Override
	public boolean saveLevelTwo(Subject subject) {
		Subject subjectLevelTwo = this.getSubByTitle(subject.getTitle(), subject.getParentId());
		if(subjectLevelTwo == null){
			return this.save(subject);
		}else{
			throw new GuliException(20001, "类别已存在");
		}
	}

	public boolean removeSubjectById(String id) {

		//根据id查询是否存在二级分类，如果有则提示用户尚有子节点
		if(this.getSubCountById(id)){
			throw new GuliException(20001,"该分类下存在二级分类，请先删除子分类");
		}

		//根据id查询是否存在课程信息，如果有则提示用户尚有课程
		if(this.courseService.getCountBySubjectId(id)){
			throw new GuliException(20001,"该分类下存在课程，请先删除课程");
		}

		Integer result = baseMapper.deleteById(id);
		return null != result && result > 0;
	}

	/**
	 * 根据分类名称查询这个一级分类是否存在
	 * @param title
	 * @return
	 */
	private Subject getByTitle(String title) {

		QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("title", title);
		queryWrapper.eq("parent_id", "0");
		return baseMapper.selectOne(queryWrapper);
	}

	/**
	 * 根据分类名称和父id查询这个二级分类是否存在
	 * @param title
	 * @return
	 */
	private Subject getSubByTitle(String title, String parentId) {

		QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("title", title);
		queryWrapper.eq("parent_id", parentId);
		return baseMapper.selectOne(queryWrapper);
	}

	/**
	 * 根据父id查询二级分类的数量
	 * @return
	 */
	private boolean getSubCountById(String parentId) {

		QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id", parentId);
		Integer count = baseMapper.selectCount(queryWrapper);
		return null != count && count > 0;
	}
}
