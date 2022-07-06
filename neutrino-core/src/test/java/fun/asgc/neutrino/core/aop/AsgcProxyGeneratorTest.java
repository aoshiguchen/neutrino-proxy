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

import fun.asgc.neutrino.core.aop.proxy.AsgcProxyGenerator;
import fun.asgc.neutrino.core.base.Handler;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/6
 */
public class AsgcProxyGeneratorTest {

	@Test
	public void test1() {
		String result = AsgcProxyGenerator.getInstance().generator("A", Panda.class);
		System.out.println(result);
	}

	@Test
	public void test2() {
		String result = AsgcProxyGenerator.getInstance().generator("A", Giraffe.class);
		System.out.println(result);
	}

	@Test
	public void test3() throws InvocationTargetException, IllegalAccessException {
		Method[] methods = A.class.getDeclaredMethods();
		methods[0].invoke(new A(), "aa", "bb");
		System.out.println(methods[0].isSynthetic());
	}

	public static class A implements Handler<String,String>{
		public void handle(String s, String s2) {
			System.out.println("hello");
		}

		public void a() {

		}


	}

}