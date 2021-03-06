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
package fun.asgc.neutrino.core.aop;

import fun.asgc.neutrino.core.aop.interceptor.InnerGlobalInterceptor;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public class Panda {

	public Panda() {
		System.out.println("熊猫出生");
	}

	public void eat() {
		System.out.println("熊猫吃竹子");
	}

	@Intercept(exclude = InnerGlobalInterceptor.class)
	public void play(String project) {
		System.out.println("熊猫在玩" + project);
	}

	public Integer division(int x, int y) {
		return x / y;
	}

	public void say(String msg) {
		System.out.println("熊猫说：" + msg);
	}

	public String request(String a, String b) {
		return String.format("参数 a:%s b:%s 处理结果", a, b);
	}

	public void up() throws Exception {
		throw new Exception("跳太高，摔死了!");
	}
}
