/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.db.template;

import fun.asgc.neutrino.core.base.Orderly;
import fun.asgc.neutrino.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * sql语句+sql参数的封装，用于支持以下3种用法
 * 1、jdbcTemplate.query(User.class,"select * from user where id = ?",1);
 * 2、dbcTemplate.query(User.class,"select * from user where id = :id", new HashMap<String,Object>(){
 *        {
 * 				this.put("id","1");
 *         }
 *    });
 * 3、dbcTemplate.query(User.class,"select * from user where id = :id", new User().setId("1"));
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public class SqlAndParams {
	private String sql;
	private Object[] paramArray;
	private Map<String,Object> paramMap;
	private Object paramObject;

	public SqlAndParams(String sql, Object[] paramArray) {
		this.sql = sql;
		this.paramArray = paramArray;
	}

	public SqlAndParams(String sql) {
		this.sql = sql;
	}

	public SqlAndParams(String sql,Map<String,Object> paramMap) {
		this.sql = sql;
		this.paramMap = paramMap;
		this.initParams();
	}

	public SqlAndParams(String sql,Object paramObject) {
		this.sql = sql;
		this.paramObject = paramObject;

		this.initParamMap();
		this.initParams();
	}

	private void initParamMap(){
		if (null == paramObject) {
			return;
		}
		if (null == paramMap) {
			paramMap = new HashMap<>();
		}
		for(Field field : ReflectUtil.getDeclaredFields(paramObject.getClass())){
			paramMap.put(field.getName(), ReflectUtil.getFieldValue(field, paramObject));
		}
	}

	private void initParams() {
		if (null == paramMap) {
			paramMap = new HashMap<>();
		}
		List<Orderly> orderlyList = new ArrayList<>();
		for(String key : paramMap.keySet()){
			int index = sql.indexOf(":" + key);
			if(-1 != index){
				orderlyList.add(new Orderly(paramMap.get(key), index));
				sql = sql.replaceFirst(":" + key, "?");
			}
		}

		this.paramArray = orderlyList.stream().sorted().map(Orderly::getData).collect(Collectors.toList()).toArray();
	}

	public Object[] getParamArray(){
		return paramArray;
	}

	public String getSql(){
		return sql;
	}

	@Override
	public String toString() {
		return "SqlAndParams{" +
			"sql='" + sql + '\'' +
			", paramArray=" + Arrays.toString(paramArray) +
			", paramMap=" + paramMap +
			", paramObject=" + paramObject +
			'}';
	}
}
