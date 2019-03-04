package com.guli.edu.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author helen
 * @since 2019/3/1
 */
@Data
public class SubjectNestedVo {

	private String id;
	private String title;
	private List<SubjectVo> children = new ArrayList<>();
}
