package com.guli.common.handler;

/**
 * @author helen
 * @since 2019/2/21
 */

import com.fasterxml.jackson.core.JsonParseException;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExceptionUtil;
import com.guli.common.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public R error(Exception e){
		log.error(ExceptionUtil.getMessage(e));
		return R.error();
	}

	@ExceptionHandler(BadSqlGrammarException.class)
	@ResponseBody
	public R error(BadSqlGrammarException e) {
		log.error(ExceptionUtil.getMessage(e));
		return R.setResult(ResultCodeEnum.BAD_SQL_GRAMMAR);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public R error(JsonParseException e){
		log.error(ExceptionUtil.getMessage(e));
		return R.setResult(ResultCodeEnum.JSON_PARSE_ERROR);
	}

	@ExceptionHandler(GuliException.class)
	@ResponseBody
	public R error(GuliException e){
		log.error(ExceptionUtil.getMessage(e));
		return R.error().message(e.getMessage()).code(e.getCode());
	}
}
